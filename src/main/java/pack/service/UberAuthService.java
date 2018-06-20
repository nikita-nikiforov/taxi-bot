package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import pack.entity.UberCredential;
import pack.entity.User;
import pack.init.AppProperties;
import pack.init.Initialization;
import pack.model.UberAccessTokenResponse;

@Service
public class UberAuthService {

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private UserService userService;

    @Autowired
    private Initialization initialization;

    @Autowired
    private AppProperties appProperties;

    // TODO
    public boolean authorizeUser(Long chatId, String code) {
        UberCredential myCredential = new UberCredential();     // Credential entity to setStartPoint
        User user = userService.getUserByChatId(chatId);        // Get user by ChatId
        UberAccessTokenResponse accessTokenResponse = getAccessTokenResponse(code); // Get access_token

        // Get access_token and set it
        myCredential.setAccess_token(accessTokenResponse.getAccess_token());
        myCredential.setUser(user);                             // Set user to credential

        return uberCredentialService.save(myCredential);               // Save in DB
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


    private MultiValueMap getParamsToObtainAccessToken(String code) {
        MultiValueMap<String, String> request = new LinkedMultiValueMap();
        request.add("client_secret", appProperties.getCLIENT_SECRET());
        request.add("client_id", appProperties.getCLIENT_ID());
        request.add("grant_type", "authorization_code");
        request.add("redirect_uri", appProperties.getBASE_URL() + "uber-link");
        request.add("code", code);
        return request;
    }

}
