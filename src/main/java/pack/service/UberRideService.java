package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.*;
import pack.dao.UberRideRepository;
import pack.entity.*;
import pack.model.*;
import pack.service.api.UberApiService;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UberRideService {

    @Autowired
    private UberRideRepository uberRideRepository;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

    @Resource(name = "nextRideStatusMap")
    private Map<String, String> nextRideStatusMap;

    public UberRide getUberRideByUserChatId(long chatId) {
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
        UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId());
        uberRide.setRequest_id(uberRideResponse.getRequest_id());
        uberRideRepository.save(uberRide);
        return true;
    }

    // When receive webhook with trip status changed
    public void proceedStatusChangedWebhook(StatusChangedResponse response) {
        // Get user by uuid from response
        User user = userService.getByUuid(response.getMeta().getUser_id());
        // Get status and requestId
        String updatedStatus = response.getMeta().getStatus();
        RideStatusEnum rideStatusEnum = RideStatusEnum.findByName(updatedStatus);

        String requestId = response.getMeta().getResource_id();

        UberRide uberRide = getUberRideByUserChatId(user.getChatId());

        if (requestId.equals(uberRide.getRequest_id())
                && ifRideStatusAppropriate(user, updatedStatus)) {
            uberRide.setStatus(rideStatusEnum.getName());
            save(uberRide);
            userService.save(user, rideStatusEnum.getUserState());
            Request request = rideStatusEnum.getRequest(user);
            sender.send(request);
            fakeTripLogic(user, updatedStatus);
        }
    }

    private void fakeTripLogic(User user, String currentStatus) {
        try {
            TimeUnit.SECONDS.sleep(new SplittableRandom().nextInt(30, 50));

            String newStatus;
            // update the ride status to the next step
            switch (currentStatus) {
                case RideStatus.PROCESSING:
                    newStatus = RideStatus.ACCEPTED;
                    break;
                case RideStatus.ACCEPTED:
                    newStatus = RideStatus.ARRIVING;
                    break;
                case RideStatus.ARRIVING:
                    newStatus = RideStatus.IN_PROGRESS;
                    break;
                case RideStatus.IN_PROGRESS:
                    newStatus = RideStatus.COMPLETED;
                    break;
                default:
                    newStatus = RideStatus.UNDEFINED;
            }
            uberApiService.putSandboxRide(user, newStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO
    // Check if the new status received on webhook is appropriate to be the next
    private boolean ifRideStatusAppropriate(User user, String newStatus) {
        UberRide uberRide = getUberRideByUserChatId(user.getChatId());
        String currentStatus = uberRide.getStatus();
        if (nextRideStatusMap.get(currentStatus).equals(newStatus)) {
            return true;
        } else return false;
    }

    public UberRideResponse.Driver getDriverObject(User user) {
        UberRideResponse currentTrip = uberApiService.getCurrentTrip(user);
        return currentTrip.getDriver();
    }
}
