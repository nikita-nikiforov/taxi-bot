package pack.service.api;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.service.UberCredentialService;

import java.util.Optional;

@Service
public class UberRestService {

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private Gson gson;

    public <T> Optional<T> getRequest(User user, String url, Class<T> clazz) {
        HttpHeaders headers = getAccessTokenedHeaders(user);    // Get access-tokened headers
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplateService.getRequest(url, entity, clazz);
    }

    public <T> T postRequest(User user, String url, Object request, Class<T> clazz) {
        HttpHeaders headers = getAccessTokenedHeaders(user);    // Get access-tokened headers
        String requestJson = gson.toJson(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        return restTemplateService.postRequest(url, entity, clazz);
    }

    public <T> Optional<T> postRequestOptional(User user, String url,
                                               Object request, Class<T> clazz) {
        T t = postRequest(user, url, request, clazz);
        if (t != null) return Optional.of(t);
        else return Optional.empty();
    }

    public <T> Optional<T> putRequest(User user, String url, Object reqBody, Class<T> clazz) {
        HttpHeaders headers = getAccessTokenedHeaders(user);
        String json = gson.toJson(reqBody);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return restTemplateService.putRequest(url, entity, clazz);
    }

    public void deleteRequest(User user, String url) {
        HttpHeaders headers = getAccessTokenedHeaders(user);        // Get access-tokened headers
        HttpEntity<String> entity = new HttpEntity<>(headers);      // Create entity
        restTemplateService.deleteRequest(url, entity);                                 // Make request
    }

    // To obtain request headers with user's Bearer token
    private HttpHeaders getAccessTokenedHeaders(User user) {
        HttpHeaders result = new HttpHeaders();
        String access_token = uberCredentialService.getAccessTokenByUser(user);
        result.set("Authorization", "Bearer " + access_token);
        result.set("Accept-Language", "en_US");
        result.set("Content-Type", "application/json");
        return result;
    }
}