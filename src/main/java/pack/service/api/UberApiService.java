package pack.service.api;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.init.AppProperties;
import pack.model.*;
import pack.model.HistoryResponse.History;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UberCredentialService;
import pack.service.dao.UberRideDaoService;

import java.util.*;

@Service
public class UberApiService {
    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private OrderDaoService orderDaoService;

    @Autowired
    private UberRideDaoService uberRideDaoService;

    @Autowired
    private UberRestService uberRestService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private Gson gson;

    public List<History> getHistoryList(User user) {
        List<History> result = new ArrayList<>();
        Optional<HistoryResponse> response = uberRestService.getRequest(user, "https://api.uber.com/v1.2/history", HistoryResponse.class);
        response.ifPresent(r -> result.addAll(Arrays.asList(r.getHistory())));
        return result;
    }

    public Optional<FareResponse> getEstimateResponse(User user, FareRequest fareRequest) {
        String url = "https://sandbox-api.uber.com/v1.2/requests/estimate";
        return uberRestService.postRequestOptional(user, url, fareRequest, FareResponse.class);
    }

    // Make request to start new ride and get response with request_id.
    // It uses Order coords and UberRide fare_id and product_id
    public Optional<UberRideResponse> getUberNewRideResponse(User user) {
        UberRide uberRide = uberRideDaoService.getByUserChatId(user.getChatId()).get();
        Order order = orderDaoService.getOrderByChatId(user.getChatId());
        UberRideRequest jsonBody = new UberRideRequest(order, uberRide);
        String url = "https://sandbox-api.uber.com/v1.2/requests";
        return uberRestService.postRequestOptional(user, url, jsonBody, UberRideResponse.class);
    }

    public UberRideResponse getCurrentTrip(User user) {
        String url = "https://sandbox-api.uber.com/v1.2/requests/current";
        Optional<UberRideResponse> response = uberRestService.getRequest(user, url, UberRideResponse.class);
        return response.get();
    }

    // To determine if there's Uber service by coordinates
    public List<ProductItem> getProductsNearBy(User user, Coordinates coord) {
        Map<String, String> params = new HashMap<>();
        params.put("latitude", coord.getLatitude().toString());
        params.put("longitude", coord.getLongitude().toString());
        // TODO
        JSONObject productsJson = retrieveJson(user, "https://api.uber.com/v1.2/products",
                HttpMethod.GET, params);
        JSONArray array = (JSONArray) productsJson.get("products");
        List<ProductItem> productItems = new ArrayList<>();
        try {
            array.forEach(e -> {
                JSONObject productJson = (JSONObject) e;
                ProductItem productItem = gson.fromJson(productJson.toString(), ProductItem.class);
                productItems.add(productItem);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productItems;
    }

    // To update the ride state, it's for the fake logic
    public void putSandboxRide(User user, String newStatus) {
        // Create request body for json
        SandboxPutRequest reqBody = new SandboxPutRequest(newStatus);
        // Get requestId by User
        String requestId = uberRideDaoService.getByUserChatId(user.getChatId()).get().getRequest();
        String url = "https://sandbox-api.uber.com/v1.2/sandbox/requests/" + requestId;
        uberRestService.putRequest(user, url, reqBody, Object.class);
    }

    // To DELETE the ride by request_id
    public void deleteRideRequest(User user) {
        UberRide uberRide = uberRideDaoService.getByUserChatId(user.getChatId()).get();
        String request_id = uberRide.getRequest();
        String url = "https://sandbox-api.uber.com/v1.2/requests/" + request_id;
        uberRestService.deleteRequest(user, url);
    }

    public Optional<ReceiptResponse> getReceiptResponse(User user, String requestId) {
        String url = "https://api.uber.com/v1/requests/" + requestId + "/receipt";
        return uberRestService.getRequest(user, url, ReceiptResponse.class);
    }

    public Optional<ReceiptResponse> getReceiptResponse(User user, StatusChangedResponse statusChangedResponse) {
        String url = statusChangedResponse.getResource_href();
        return uberRestService.getRequest(user, url, ReceiptResponse.class);
    }

    public UberAccessTokenResponse getAccessTokenResponse(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://login.uber.com/oauth/v2/token";
        MultiValueMap<String, String> params = getParamsToObtainAccessToken(code);

        // set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        UberAccessTokenResponse tokenResponse = null;
        try {
            tokenResponse = restTemplate.postForObject(url,
                    entity, UberAccessTokenResponse.class, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenResponse;
    }

    private MultiValueMap<String, String> getParamsToObtainAccessToken(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
        request.add("client_secret", appProperties.getCLIENT_SECRET());
        request.add("client_id", appProperties.getCLIENT_ID());
        request.add("grant_type", "authorization_code");
        request.add("redirect_uri", appProperties.getLOGIN_REDIRECT_URL());
        request.add("code", code);
        return request;
    }

    public Optional<Place> getFavoritePlace(User user, String place) {
        String url = "https://api.uber.com/v1.2/places/" + place;
        return uberRestService.getRequest(user, url, Place.class);
    }

    public ProductResponse.Product getProductById(User user, String product_id) {
        String url = "https://api.uber.com/v1.2/products/" + product_id;
        Optional<ProductResponse.Product> request = uberRestService.getRequest(user, url, ProductResponse.Product.class);
        return request.get();
    }

    public Optional<Place> putPlace(User user, String id, String address) {
        Optional<Place> result;
        String url = "https://api.uber.com/v1.2/places/" + id;
        Place requetBody = new Place(address);
        try {
            result = uberRestService.putRequest(user, url, requetBody, Place.class);
        } catch (HttpClientErrorException e) {          // Catch HttpClientErrorException: 422 Unprocessable Entity,
            result = Optional.empty();                  // when user inputs wrong address
        }
        return result;
    }

    public Optional<UberUserProfile> aboutMe(User user) {
        return uberRestService.getRequest(user, "https://api.uber.com/v1.2/me", UberUserProfile.class);
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
}