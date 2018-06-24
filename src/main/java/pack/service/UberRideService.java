package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.State;
import pack.dao.UberRideRepository;
import pack.entity.UberRide;
import pack.entity.User;
import pack.handler.RideWebhookHandler;
import pack.model.ProductItem;
import pack.model.ReceiptResponse;
import pack.model.StatusChangedResponse;
import pack.model.UberRideResponse;
import pack.service.api.UberApiService;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UberRideDaoService;
import pack.service.dao.UserService;
import java.util.List;
import java.util.Optional;

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
    private OrderDaoService orderDaoService;

    @Autowired
    private RideWebhookHandler rideWebhookHandler;

    @Autowired
    UberRideDaoService uberRideDaoService;

    // To determine if there's a taxi
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {
        return uberApiService.getProductsNearBy(user, coord);
    }

    // Confirm the ride. Return true, if request has successfully started new UberRide
    public boolean confirmRide(User user) {
        Optional<UberRideResponse> responseOptional = uberApiService.getUberNewRideResponse(user);
        if (responseOptional.isPresent()) {
            UberRideResponse uberRideResponse = responseOptional.get();
            UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
            uberRide.setRequest(uberRideResponse.getRequest_id());
            uberRideRepository.save(uberRide);
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
        // TODO
    }

    // Send request to cancel ride by rider. In turn, the receipt webhook will be caught and all
    // Order and UberRide info will be removed
    public void cancelUberRide(User user) {
        uberApiService.deleteRideRequest(user);
    }
}
