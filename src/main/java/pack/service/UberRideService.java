package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.RideStatus;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.handler.RideStatusWebhookHandler;
import pack.model.ProductItem;
import pack.model.StatusChangedResponse;
import pack.model.UberRideResponse;
import pack.service.api.UberApiService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

import static pack.constant.RideStatus.*;


@Service
public class UberRideService {

    @Autowired
    private UberRideRepository uberRideRepository;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private RideStatusWebhookHandler rideStatusWebhookHandler;

    @Autowired
    private Sender sender;

    @Resource(name = "nextRideStatusMap")
    private Map<RideStatus, RideStatus> nextRideStatusMap;

    public Optional<UberRide> getUberRideByUserChatId(long chatId) {
        return uberRideRepository.findByOrderUserChatId(chatId);
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

    public boolean confirmRide(User user) {
        UberRideResponse uberRideResponse = uberApiService.getUberNewTripResponse(user).get();
        UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
        uberRide.setRequest_id(uberRideResponse.getRequest_id());
        uberRideRepository.save(uberRide);
        return true;
    }

    // When receive webhook with trip status changed
    public void proceedStatusChangeWebhook(StatusChangedResponse response) {
        // Get user by uuid from response
        User user = userService.getByUuid(response.getMeta().getUser_id());
        // Get new status from response
        String updatedStatus = response.getMeta().getStatus();
        // And find corresponding RideStatus enum
        RideStatus updatedRideStatus = RideStatus.findByName(updatedStatus);

        // Get requestId from response
        String requestId = response.getMeta().getResource_id();

        // Get Optional of UberRide by user
        Optional<UberRide> uberRideOptional = getUberRideByUserChatId(user.getChatId());

        uberRideOptional.ifPresent(uberRide -> {
            // Check if webhook request is for current ride and if the updated status is appropriate
            // to the current one (I checked this because I had received too many requests from Uber)
            if (requestId.equals(uberRide.getRequest_id())
                    && ifRideStatusAppropriate(uberRide, updatedRideStatus)) {
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
                case COMPLETED:
                    newStatus = FINISHED;
                    break;
                default:
                    newStatus = currentStatus;
            }
            // If not FINISHED, make putRequest to update to the next status.
            // (Because when COMPLETED is recieved, Uber removes the trip.
            // So, FINISHED indicates that there's nothing to update)
            if (newStatus != FINISHED) {
                uberApiService.putSandboxRide(user, newStatus.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public UberRideResponse.Driver getDriverObject(User user) {
        UberRideResponse currentTrip = uberApiService.getCurrentTrip(user);
        return currentTrip.getDriver();
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
}
