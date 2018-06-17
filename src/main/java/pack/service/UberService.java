package pack.service;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pack.entity.Orderr;
import pack.entity.UberCredential;
import pack.entity.User;
import pack.init.Initialization;
import pack.json.HistoryItem;
import pack.json.UberAccessTokenResponse;

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

    @Value("${my-uber-access-key}")
    private String MY_ACCESS_KEY;

    public void authUser(User user) {
        UberCredential myCredential = new UberCredential();
        myCredential.setUser(user);
        myCredential.setAccess_token(MY_ACCESS_KEY);
        uberCredentialService.save(myCredential);
    }

    public void authorize(String code) {
        UberAccessTokenResponse accessTokenResponse = getAccessTokenResponse(code);
        saveUserAccessToken(accessTokenResponse);
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

    // TODO
    public void saveUserAccessToken(UberAccessTokenResponse response) {
//        UberCredential
    }

    private MultiValueMap getParamsToObtainAccessToken(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap();
        request.add("client_secret", "CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT");
        request.add("client_id", "NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_");
        request.add("grant_type", "authorization_code");
        request.add("redirect_uri", initialization.getBASE_URL() + "uber-link");
        request.add("code", code);
        return request;
    }

    public JSONObject makeMagic(User user) {
        return makeOrder(user);
//        infoAboutMe(user);
    }

    public JSONObject makeOrder(User user) {
        Orderr order = orderService.getOrderByChatId(user.getChatId());

        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.valueOf(order.getStartLat()) + "0");
        params.put("longitude", String.valueOf(order.getStartLong()) + "0");

        return retrieveJson(user, "https://api.uber.com/v1.2/products", HttpMethod.GET, params);
    }

    public JSONObject infoAboutMe(User user) {
        return retrieveJson(user, "https://api.uber.com/v1.2/me", HttpMethod.GET);
    }

    public List<HistoryItem> getHistoryList(User user) {
        JSONObject json = retrieveJson(user, "https://api.uber.com/v1.2/history", HttpMethod.GET);
        JSONArray array = (JSONArray) json.get("history");
        List<HistoryItem> historyList = new ArrayList<>();

        array.forEach(o -> {
            JSONObject temp = (JSONObject) o;
            HistoryItem historyItem = gson.fromJson(temp.toString(), HistoryItem.class);
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
        headers.set("Content-Type", "application/json ");

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


//    public ServerTokenSession getServerTokenSession() {
//        SessionConfiguration config = new SessionConfiguration.Builder()
//                .setClientId("NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_")
//                .setServerToken("zwRUhoz8pVi9vQ31Pb9n5Lwb7oXQ6wT5ZIxkBKkg")
//                .build();
//        return new ServerTokenSession(config);
//    }

//    public void withUberSDK() {
//        List<Scope> scopes = new ArrayList<>();
//        scopes.add(Scope.PROFILE);
//        SessionConfiguration config = new SessionConfiguration.Builder()
//                .setClientId("NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_")
//                .setClientSecret("CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT")
//                .setScopes(scopes)
//                .setRedirectUri("https://cd0d6cb4.ngrok.io/uber-link")
//                .build();
//
//        OAuth2Credentials credentials = new OAuth2Credentials.Builder()
//                .setSessionConfiguration(config)
//                .build();
//
//        try {
//            String authUrl = credentials.getAuthorizationUrl();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        String code = "EcN4X4lsytRWm3U4Xdbny0KcRZKQiG";
//        String accessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6IjZkdEttYVBIUXFXVlAvYzF0R3Noemc9PSIsImV4cGlyZXNfYXQiOjE1MzE2NTY2MDAsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.sYuKpVZKlyt0uls6uTn2rkvVwAnd89fPYMukLWKE31w";
//        Credential credential1 = credentials.authenticate(code, accessToken);
////        credentials.
//        Credential credential = credentials.loadCredential(accessToken);
//        CredentialsSession credentialsSession = new CredentialsSession(config, credential);
//        RidesService ridesService = UberRidesApi.with(credentialsSession).build().createService();
//        Call<UserProfile> userProfile = ridesService.getUserProfile();
//        try {
//            Response<UserProfile> response = ridesService.getUserProfile().execute();
//            if (response.isSuccessful()) {
//                UserProfile profile = response.body();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
