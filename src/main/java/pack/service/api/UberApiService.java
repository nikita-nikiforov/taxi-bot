package pack.service.api;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.dao.UberTripRepository;
import pack.entity.Order;
import pack.entity.UberTrip;
import pack.entity.User;
import pack.model.*;
import pack.model.HistoryResponse.History;
import pack.service.OrderService;
import pack.service.UberCredentialService;
import pack.service.UberTripService;
import pack.service.UserService;

import java.util.*;

@Service
public class UberApiService {

    @Autowired
    private Gson gson;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    UserService userService;

    @Autowired
    UberCredentialService uberCredentialService;

    @Autowired
    OrderService orderService;

    @Autowired
    private UberTripRepository uberTripRepository;      // TODO to service

    @Autowired
    private UberTripService uberTripService;

    // TODO
    public JSONObject makeOrder(User user) {
        return null;
    }

    public JSONObject infoAboutMe(User user) {
        return retrieveJson(user, "https://api.uber.com/v1.2/me", HttpMethod.GET);
    }

    public Optional<UberUserProfile> aboutMe(User user) {
        return restTemplateService.getRequestUberAuthed(user, "https://api.uber.com/v1.2/me", UberUserProfile.class);
    }

    public List<History> getHistoryList(User user) {
        List<History> result = new ArrayList<>();
        Optional<HistoryResponse> response = restTemplateService.getRequestUberAuthed(user, "https://api.uber.com/v1.2/history", HistoryResponse.class);
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
        return restTemplateService.postRequestUberAuthedOptional(user, url, fareRequest, FareResponse.class);
    }

    public Optional<UberTripResponse> getUberNewTripResponse(User user) {

        UberTrip uberTrip = uberTripRepository.findByOrderUserChatId(user.getChatId());
        Order order = orderService.getOrderByChatId(user.getChatId());
        UberTripRequest jsonBody = new UberTripRequest(order, uberTrip);
        String url = "https://sandbox-api.uber.com/v1.2/requests";

        Optional<UberTripResponse> tripResponse = restTemplateService
                .postRequestUberAuthedOptional(user, url, jsonBody, UberTripResponse.class);
        // TODO Handle errors
        return tripResponse;
    }

    public void updateSandboxRide(User user, String newStatus) {
        // Create request body for json
        SandboxPutRequest reqBody = new SandboxPutRequest(newStatus);
        // Get requestId by User
        String requestId = uberTripService.getUberTripByUserChatId(user.getChatId()).getRequest_id();
        String url = "https://sandbox-api.uber.com/v1.2/sandbox/requests/" + requestId;
        restTemplateService.putRequestUberAuthed(user, url, reqBody, Object.class);
    }
}