package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.RideStatus;
import pack.constant.State;
import pack.entity.UberRide;
import pack.entity.User;
import pack.handler.RideWebhookHandler;
import pack.model.ReceiptResponse;
import pack.model.StatusChangedResponse;
import pack.service.api.UberApiService;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UberRideDaoService;
import pack.service.dao.UserService;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Service
public class WebhookService {
    @Autowired
    private UserService userService;

    @Autowired
    private UberRideDaoService uberRideDaoService;

    @Autowired
    private FakeTripLogicService fakeTripLogicService;

    @Autowired
    private RideWebhookHandler rideWebhookHandler;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private OrderDaoService orderDaoService;

    @Resource(name = "nextRideStatusMap")
    private Map<RideStatus, RideStatus> nextRideStatusMap;

    /* When receive "requests.status_changed" on webhook. Using uuid from the request,
    ** the method finds UberRide and checks whether new "webhooked" status is appropriate
    ** for the current one */
    public void handleStatusChangeWebhook(StatusChangedResponse response) {
        User user = userService.getByUuid(response.getMeta().getUser_id());     // Get user by uuid from response
        String requestId = response.getMeta().getResource_id();                 // Get requestId from response
        Optional<UberRide> uberRideOptional = uberRideDaoService.getByRequestId(requestId);     // Get UberRide by request_Id
        uberRideOptional.ifPresent(uberRide -> {                                // If UberRide is found
            // Get new status from response and get corresponding RideStatus enum
            RideStatus updatedRideStatus = RideStatus.findByName(response.getMeta().getStatus());
            // Check if the updated status is appropriate to the current one
            // (I checked this because sometimes I had received many random requests from Uber)
            if (ifRideStatusAppropriate(uberRide, updatedRideStatus)) {
                // Handle new status by appropriate method
                handleStatusChange(user, uberRide, updatedRideStatus);
                fakeTripLogicService.updateRideStatus(user, updatedRideStatus);             // Call fake logic status changing
            }
        });
    }

    //* When receive "requests.receipt_ready"" on webhook.
    public void handleReceiptWebhook(StatusChangedResponse response) {
        User user = userService.getByUuid(response.getMeta().getUser_id());     // Get user by uuid from response
        String requestId = response.getMeta().getResource_id();                 // Get requestId
        Optional<UberRide> uberRide = uberRideDaoService.getByRequestId(requestId);                // Get uberRide by request
        // Check if UberRide is present and if the status of new event is "ready"
        if (uberRide.isPresent() && "ready".equals(response.getMeta().getStatus())) {
            Optional<ReceiptResponse> receiptResponseOptional = uberApiService.getReceiptResponse(user, response);
            receiptResponseOptional.ifPresent(receiptResponse ->
                    rideWebhookHandler.handleReceipt(user, receiptResponse));
            orderDaoService.removeByUser(user);
            uberRideDaoService.removeByUser(user);
            userService.save(user, State.LOGGED);
        }
    }

    // Handles status changing and delegates to appropriate methods
    private void handleStatusChange(User user, UberRide uberRide, RideStatus rideStatus) {
        uberRide.setStatus(rideStatus.getName());          // Set name from UberStatus enum
        uberRideDaoService.save(uberRide);                                        // Save UberRide
        userService.save(user, rideStatus.getUserState());  // Save user with new state
        switch (rideStatus) {
            case PROCESSING:
                rideWebhookHandler.handleProcessing(user);
                break;
            case ACCEPTED:
                rideWebhookHandler.handleAccepted(user);
                break;
            case ARRIVING:
                rideWebhookHandler.handleArriving(user);
                break;
            case IN_PROGRESS:
                rideWebhookHandler.handleInProgress(user);
                break;
            case COMPLETED:
                rideWebhookHandler.handleCompleted(user);
        }
    }

    // TODO check for "driver_canceled"
    // Check if the new status received on webhook is appropriate to be the next
    private boolean ifRideStatusAppropriate(UberRide uberRide, RideStatus newStatus) {
        RideStatus currentStatus = RideStatus.findByName(uberRide.getStatus());
        if (nextRideStatusMap.get(currentStatus).equals(newStatus)) {
            return true;
        } else return false;
    }
}