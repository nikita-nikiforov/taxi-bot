package pack.service.api;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.model.*;
import pack.model.HistoryResponse.History;
import pack.service.OrderService;
import pack.service.UberCredentialService;
import pack.service.UberRideService;
import pack.service.UserService;

import java.util.*;

@Service
public class UberApiService {

    @Autowired
    private Gson gson;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private UserService userService;

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UberRideRepository uberRideRepository;      // TODO to service

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private UberRestService uberRestService;

    // TODO
    public JSONObject makeOrder(User user) {
        return null;
    }

    public JSONObject infoAboutMe(User user) {
        return retrieveJson(user, "https://api.uber.com/v1.2/me", HttpMethod.GET);
    }

    public Optional<UberUserProfile> aboutMe(User user) {
        return uberRestService.getRequest(user, "https://api.uber.com/v1.2/me", UberUserProfile.class);
    }

    public List<History> getHistoryList(User user) {
        List<History> result = new ArrayList<>();
        Optional<HistoryResponse> response = uberRestService.getRequest(user, "https://api.uber.com/v1.2/history", HistoryResponse.class);
        response.ifPresent(r -> result.addAll(Arrays.asList(r.getHistory())));
        return result;
    }

    public JSONObject retrieveJson(User user, String url, HttpMethod method, Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();

        String access_token = uberCredentialService.getAccessTokenByUser(user);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        params.forEach(builder::queryParam);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Accept-Language", "en_US ");
        headers.set("Content-Type", "application/json");

        HttpEntity entity = new HttpEntity<>(headers);

        ResponseEntity<String> tokenResponse = null;
        try {
            tokenResponse = restTemplate.exchange(builder.toUriString(), method, entity,
                    String.class);
            String body = tokenResponse.getBody();
            return new JSONObject(body);
        } catch (Exception e) {
            e.printStackTrace();
            return null; //TODO
        }
    }

    public JSONObject retrieveJson(User user, String url, HttpMethod method) {
        return retrieveJson(user, url, method, new HashMap<>());
    }

    public Optional<FareResponse> getEstimateResponse(User user, FareRequest fareRequest) {
        String url = "https://sandbox-api.uber.com/v1.2/requests/estimate";
        return uberRestService.postRequestOptional(user, url, fareRequest, FareResponse.class);
    }

    public Optional<UberRideResponse> getUberNewRideResponse(User user) {

        UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
        Order order = orderService.getOrderByChatId(user.getChatId());
        UberRideRequest jsonBody = new UberRideRequest(order, uberRide);
        String url = "https://sandbox-api.uber.com/v1.2/requests";

        Optional<UberRideResponse> tripResponse = uberRestService
                .postRequestOptional(user, url, jsonBody, UberRideResponse.class);
        // TODO Handle errors
        return tripResponse;
    }

    public UberRideResponse getCurrentTrip(User user) {
        String url = "https://sandbox-api.uber.com/v1.2/requests/current";
        Optional<UberRideResponse> response = uberRestService.getRequest(user, url, UberRideResponse.class);
        return response.get();
    }

    // To update the ride state, it's for the fake logic
    public void putSandboxRide(User user, String newStatus) {
        // Create request body for json
        SandboxPutRequest reqBody = new SandboxPutRequest(newStatus);
        // Get requestId by User
        String requestId = uberRideService.getByUserChatId(user.getChatId()).get().getRequest();
        String url = "https://sandbox-api.uber.com/v1.2/sandbox/requests/" + requestId;
        uberRestService.putRequest(user, url, reqBody, Object.class);
    }

    // To determine if there's Uber service by coordinates TODO
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", coord.getLatitude().toString());
        params.put("longitude", coord.getLongitude().toString());

        // TODO
        JSONObject productsJson = retrieveJson(user, "https://api.uber.com/v1.2/products",
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

    // To DELETE the ride by request_id
    public void deleteRideRequest(User user) {
        UberRide uberRide = uberRideService.getByUserChatId(user.getChatId()).get();
        String request_id = uberRide.getRequest();
        uberRestService.deleteRequest(user, request_id);
    }
}