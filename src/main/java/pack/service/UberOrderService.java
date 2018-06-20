package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.service.Sender;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.ProductItem;
import pack.model.StatusChangedResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UberOrderService {

    @Autowired
    private UberService uberService;

    @Autowired
    private UserService userService;

    @Autowired
    private Gson gson;

    @Autowired
    private Sender sender;

    // To determine whether there's a taxi
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", coord.getLatitude().toString());
        params.put("longitude", coord.getLongitude().toString());

        JSONObject productsJson = uberService.retrieveJson(user, "https://api.uber.com/v1.2/products",
                HttpMethod.GET, params);
        JSONArray array = (JSONArray) productsJson.get("products");
        List<ProductItem> productItems = new ArrayList<>();

        array.forEach(e -> {
            JSONObject productJson = (JSONObject) e;
            ProductItem productItem = gson.fromJson(productJson.toString(), ProductItem.class);
            productItems.add(productItem);
        });
        return productItems;
    }

    public void proceedStatusChanged(StatusChangedResponse response) {
        User user = userService.getByUuid(response.getMeta().getUser_id());
        String status = response.getMeta().getStatus();
        String state = user.getState();
        

        sender.send(user, "Ride status changed: " + status + ", request_id = " + response.getMeta().getResource_id());
    }
}
