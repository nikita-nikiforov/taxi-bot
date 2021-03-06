package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberRideRepository;
import pack.entity.UberRide;
import pack.entity.User;
import pack.handler.RideWebhookHandler;
import pack.model.ProductItem;
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

    // Send request to cancel ride by rider. In turn, the receipt webhook will be caught and all
    // Order and UberRide info will be removed
    public void cancelUberRide(User user) {
        uberApiService.deleteRideRequest(user);
    }
}
