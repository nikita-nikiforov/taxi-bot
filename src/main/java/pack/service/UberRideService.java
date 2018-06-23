package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pack.constant.RideStatus;
import pack.constant.State;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.handler.RideStatusWebhookHandler;
import pack.model.ProductItem;
import pack.model.ReceiptResponse;
import pack.model.StatusChangedResponse;
import pack.model.UberRideResponse;
import pack.service.api.UberApiService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static pack.constant.RideStatus.*;
import static pack.model.UberRideResponse.Driver;
import static pack.model.UberRideResponse.Vehicle;


@Service
public class UberRideService {

    @Autowired
    private UberRideRepository uberRideRepository;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RideStatusWebhookHandler rideStatusWebhookHandler;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private Sender sender;

    @Resource(name = "nextRideStatusMap")
    private Map<RideStatus, RideStatus> nextRideStatusMap;


    public Optional<UberRide> getByUserChatId(long chatId) {
        return uberRideRepository.findByOrderUserChatId(chatId);
    }

    public Optional<UberRide> getByRequestId(String requestId) {
        return uberRideRepository.findByRequest(requestId);
    }

    public void save(UberRide uberRide) {
        uberRideRepository.save(uberRide);
    }

    public Optional<UberRide> getByOrder(Order order) {
        return uberRideRepository.findByOrder(order);
    }

    // To determine if there's a taxi
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {
        return uberApiService.getProductsNearBy(user, coord);
    }

    // Confirm the ride. Return true, if request successfully start new UberRide
    public boolean confirmRide(User user) {
        Optional<UberRideResponse> responseOptional = uberApiService.getUberNewRideResponse(user);
        if (responseOptional.isPresent()) {
            UberRideResponse uberRideResponse = responseOptional.get();
            UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
            uberRide.setRequest(uberRideResponse.getRequest_id());
            uberRideRepository.save(uberRide);
            return true;
        } else {
            return false;
        }

    }

    // When receive "requests.status_changed" on webhook. Using uuid from the request,
    // the method finds UberRide in DB and checks
    public void proceedStatusChangeWebhook(StatusChangedResponse response) {
        User user = userService.getByUuid(response.getMeta().getUser_id());     // Get user by uuid from response
        String requestId = response.getMeta().getResource_id();                 // Get requestId from response
        Optional<UberRide> uberRideOptional = getByRequestId(requestId);        // Get UberRide by request_Id
        uberRideOptional.ifPresent(uberRide -> {
            // Get new status from response and find corresponding RideStatus enum
            RideStatus updatedRideStatus = RideStatus.findByName(response.getMeta().getStatus());
            // Check if the updated status is appropriate to the current one
            // (I checked this because sometimes I had received many random requests from Uber)
            if (ifRideStatusAppropriate(uberRide, updatedRideStatus)) {
                // Handle new status by appropriate method
                handleStatusChange(user, uberRide, updatedRideStatus);
                fakeTripLogic(user, updatedRideStatus);             // Call fake logic status changing
            }
        });
    }

    // Implements fake logic of Uber ride in Sandbox
    private void fakeTripLogic(User user, RideStatus currentStatus) {
        try {
            // Sleep for some random time
            TimeUnit.SECONDS.sleep(new SplittableRandom().nextInt(15, 20));
            // If not COMPLETED, make putRequest to update to the next status.
            // (Because when COMPLETED is recieved, Uber removes the trip.
            // So, COMPLETED indicates that there's nothing to update)
            if (currentStatus != COMPLETED) {
                RideStatus newStatus;
                // update the ride status to the next one
                switch (currentStatus) {
                    case PROCESSING:
                        newStatus = ACCEPTED;
                        break;
                    case ACCEPTED:
                        newStatus = ARRIVING;
                        break;
                    case ARRIVING:
                        newStatus = IN_PROGRESS;
                        break;
                    case IN_PROGRESS:
                        newStatus = COMPLETED;
                        break;
                    default:
                        newStatus = currentStatus;
                }
                uberApiService.putSandboxRide(user, newStatus.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchElementException | HttpClientErrorException e) {
            System.out.println("Current ride is deleted.");
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

    public Driver getDriverResponse(User user) {
        UberRideResponse currentTrip = uberApiService.getCurrentTrip(user);
        return currentTrip.getDriver();
    }

    public Vehicle getVehicleResponse(User user) {
        UberRideResponse currentTrip = uberApiService.getCurrentTrip(user);
        return currentTrip.getVehicle();
    }

    // Handles status changing and delegates to appropriate methods
    private void handleStatusChange(User user, UberRide uberRide, RideStatus rideStatus) {
        uberRide.setStatus(rideStatus.getName());          // Set name from UberStatus enum
        save(uberRide);                                        // Save UberRide
        userService.save(user, rideStatus.getUserState());  // Save user with new state

        switch (rideStatus) {
            case PROCESSING:
                rideStatusWebhookHandler.handleProcessing(user, uberRide);
                break;
            case ACCEPTED:
                rideStatusWebhookHandler.handleAccepted(user, uberRide);
                break;
            case ARRIVING:
                rideStatusWebhookHandler.handleArriving(user, uberRide);
                break;
            case IN_PROGRESS:
                rideStatusWebhookHandler.handleInProgress(user, uberRide);
                break;
            case COMPLETED:
                rideStatusWebhookHandler.handleCompleted(user, uberRide);
                break;
        }
    }

    public void removeByUser(User user) {
        uberRideRepository.removeByOrderUser(user);
    }

    public void proceedReceiptWebhook(StatusChangedResponse response) {
        User user = userService.getByUuid(response.getMeta().getUser_id());     // Get user by uuid from response
        String requestId = response.getMeta().getResource_id();                 // Get requestId
        Optional<UberRide> uberRide = getByRequestId(requestId);                // Get uberRide by request
        // Check if UberRide is present and if the status of new event is "ready"
        if (uberRide.isPresent() && "ready".equals(response.getMeta().getStatus())) {
            Optional<ReceiptResponse> receiptResponseOptional = uberApiService.getReceiptResponse(user, response);
            receiptResponseOptional.ifPresent(receiptResponse -> {
                rideStatusWebhookHandler.handleReceipt(user, receiptResponse);
            });

            orderService.removeByUser(user);
            uberRideService.removeByUser(user);
            userService.save(user, State.LOGGED);
        }
        // TODO
    }

    public void cancelUberRide(User user) {
        uberApiService.deleteRideRequest(user);
    }
}
