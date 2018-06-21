package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.service.Sender;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pack.constant.RideStatus;
import pack.dao.UberTripRepository;
import pack.entity.UberTrip;
import pack.entity.User;
import pack.model.ProductItem;
import pack.model.StatusChangedResponse;
import pack.model.UberTripResponse;
import pack.service.api.UberApiService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UberOrderService {

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UberTripService uberTripService;

    @Autowired
    private Gson gson;

    @Autowired
    private Sender sender;

    @Autowired
    private UberTripRepository uberTripRepository;

    @Resource(name = "nextRideStatusMap")
    private Map<String, String> nextRideStatusMap;

    // To determine if there's a taxi
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {

        Map<String, String> params = new HashMap<>();
        params.put("latitude", coord.getLatitude().toString());
        params.put("longitude", coord.getLongitude().toString());

        // TODO
        JSONObject productsJson = uberApiService.retrieveJson(user, "https://api.uber.com/v1.2/products",
                HttpMethod.GET, params);
        JSONArray array = (JSONArray) productsJson.get("products");
        List<ProductItem> productItems = new ArrayList<>();

        // TODO here catch exception
        array.forEach(e -> {
            JSONObject productJson = (JSONObject) e;
            ProductItem productItem = gson.fromJson(productJson.toString(), ProductItem.class);
            productItems.add(productItem);
        });
        return productItems;
    }

    public boolean confirmRide(User user) {
        UberTripResponse uberTripResponse = uberApiService.getUberNewTripResponse(user).get();
        UberTrip uberTrip = uberTripRepository.findByOrderUserChatId(user.getChatId());
        uberTrip.setRequest_id(uberTripResponse.getRequest_id());
        uberTripRepository.save(uberTrip);
        return true;
    }

    // When receive webhook with trip status changed
    public void proceedStatusChangedWebhook(StatusChangedResponse response) {
        // Get user by uuid from response
        User user = userService.getByUuid(response.getMeta().getUser_id());
        // Get status and requestId
        String updatedStatus = response.getMeta().getStatus();
        String requestId = response.getMeta().getResource_id();

        UberTrip uberTrip = uberTripService.getUberTripByUserChatId(user.getChatId());

        if (requestId.equals(uberTrip.getRequest_id())
//                && !updatedStatus.equals(uberTrip.getStatus())
                && ifRideStatusIsNext(user, updatedStatus)) {
            uberTrip.setStatus(updatedStatus);
            uberTripService.save(uberTrip);
            userService.save(user, getUserStateByRideStatus(updatedStatus));
            sender.send(user, "Ride status changed: " + updatedStatus + ", request_id = " + response.getMeta().getResource_id());
            fakeTripLogic(user, updatedStatus);
        }
    }

    public void fakeTripLogic(User user, String currentStatus) {
        try {
            TimeUnit.SECONDS.sleep(15);

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
            uberApiService.updateSandboxRide(user, newStatus);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Check if the new status received on webhook is an appropriate next status
    private boolean ifRideStatusIsNext(User user, String newStatus) {
        UberTrip uberTrip = uberTripService.getUberTripByUserChatId(user.getChatId());
        String currentStatus = uberTrip.getStatus();
        if (nextRideStatusMap.get(currentStatus).equals(newStatus)) {
            return true;
        } else return false;
    }

    // Transform uber trip status to user state. E.g. "arrival" -> "UBER_ARRIVAL"
    private String getUserStateByRideStatus(String status) {
        return "UBER_" + status.toUpperCase();
    }
}
