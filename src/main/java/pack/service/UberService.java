package pack.service;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.dao.OrderUberInfoRepository;
import pack.entity.Order;
import pack.entity.OrderUberInfo;
import pack.entity.User;
import pack.init.Initialization;
import pack.model.*;

import java.util.*;

@Service
public class UberService {

    @Autowired
    private Gson gson;

    @Autowired
    private Initialization initialization;

    @Autowired
    UserService userService;

    @Autowired
    UberCredentialService uberCredentialService;

    @Autowired
    OrderService orderService;

    @Autowired
    private OrderUberInfoRepository orderUberInfoRepository;

    // TODO
    public JSONObject makeOrder(User user) {
        return null;
    }

    public JSONObject infoAboutMe(User user) {
        return retrieveJson(user, "https://api.uber.com/v1.2/me", HttpMethod.GET);
    }

    public Optional<UberUserProfile> aboutMe(User user) {
        return getRequest(user, "https://api.uber.com/v1.2/me", UberUserProfile.class);
    }

    public List<HistoryItem> getHistoryList(User user) {
        JSONObject json = retrieveJson(user, "https://api.uber.com/v1.2/history", HttpMethod.GET);
        JSONArray array = (JSONArray) json.get("history");      // "history" object from JSON response
        List<HistoryItem> historyList = new ArrayList<>();

        array.forEach(e -> {
            JSONObject historyItemJson = (JSONObject) e;
            HistoryItem historyItem = gson.fromJson(historyItemJson.toString(), HistoryItem.class);
            historyList.add(historyItem);
        });

        return historyList;
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

    public Optional<EstimateResponse> getEstimateResponse(User user, EstimateRequest estimateRequest) {

        String url = "https://sandbox-api.uber.com/v1.2/requests/estimate";

        return postWithJsonRequestOptional(user, url, estimateRequest, EstimateResponse.class);
    }

    public <T> T postWithJsonRequest(User user, String url, Object request, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        String access_token = uberCredentialService.getAccessTokenByUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Accept-Language", "en_US");
        headers.set("Content-Type", "application/json");

        String requestJson = gson.toJson(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        T response;
        try {
            response = restTemplate.postForObject(url, entity, clazz);
        } catch (HttpClientErrorException e) {
            response = null;
        }

        return response;
    }

    public <T> Optional<T> getRequest(User user, String url, Class<T> clazz) {
        Optional<T> result;       // to be returned

        RestTemplate restTemplate = new RestTemplate();
        String access_token = uberCredentialService.getAccessTokenByUser(user);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, clazz);
            result = Optional.of(response.getBody());
        } catch (HttpClientErrorException e) {
            result = Optional.empty();
        }
        return result;
    }

    public <T> Optional<T> postWithJsonRequestOptional(User user, String url, Object request, Class<T> clazz) {
        Optional<T> result;
        T t = postWithJsonRequest(user, url, request, clazz);
        if (t != null) {
            result = Optional.of(t);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    public Optional<TripResponse> getNewTripResponse(User user) {

        OrderUberInfo uberInfo = orderUberInfoRepository.findByOrderUserChatId(user.getChatId());
        Order order = orderService.getOrderByChatId(user.getChatId());
        MakeTripRequest jsonBody = new MakeTripRequest(order, uberInfo);

        Optional<TripResponse> tripResponse = postWithJsonRequestOptional(user, "https://sandbox-api.uber.com/v1.2/requests", jsonBody, TripResponse.class);
        // TODO Handle errors
        return tripResponse;
    }


//    public TripResponse getTripResponse() {
//
//    }
}