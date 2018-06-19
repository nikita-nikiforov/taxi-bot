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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.entity.User;
import pack.init.Initialization;
import pack.model.EstimateRequest;
import pack.model.EstimateResponse;
import pack.model.HistoryItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // TODO
    public JSONObject makeOrder(User user) {
        return null;
    }

    public JSONObject infoAboutMe(User user) {
        return retrieveJson(user, "https://api.uber.com/v1.2/me", HttpMethod.GET);
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

    public EstimateResponse getEstimateResponse(User user, EstimateRequest estimateRequest) {

        String url = "https://sandbox-api.uber.com/v1.2/requests/estimate";

        return postWithJsonRequest(user, url, estimateRequest, EstimateResponse.class);
    }

    public <T> T postWithJsonRequest(User user, String url, Object request, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        String access_token = uberCredentialService.getAccessTokenByUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Accept-Language", "en_US");
        headers.set("Content-Type", "application/json ");

        String requestJson = gson.toJson(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        T response = restTemplate.postForObject(url, entity, clazz);
        return response;
    }
}