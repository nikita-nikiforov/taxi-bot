package pack.service.api;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pack.entity.User;
import pack.service.UberCredentialService;
import java.util.Optional;

/*
* Class for sending REST requests
* */

@Service
public class RestTemplateService {

    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private Gson gson;

    public <T> Optional<T> getRequest(String url, HttpEntity<?> entity, Class<T> clazz) {
        Optional<T> result;                                     // to be returned
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, clazz);
            result = Optional.of(response.getBody());
        } catch (HttpClientErrorException e) {
            result = Optional.empty();
        }
        return result;
    }

    public <T> T postRequest(String url, HttpEntity<?> entity, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            return restTemplate.postForObject(url, entity, clazz);
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public <T> Optional<T> putRequest(String url, HttpEntity<?> entity, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, clazz);
        if (response.getBody() != null) {
            return Optional.of(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public <T> Optional<T> getRequestUberAuthed(User user, String url, Class<T> clazz) {
        // Get user access token
        String access_token = uberCredentialService.getAccessTokenByUser(user);
        HttpHeaders headers = new HttpHeaders();                    // Set headers
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return getRequest(url, entity, clazz);
    }

    public <T> T postRequestUberAuthed(User user, String url, Object request, Class<T> clazz) {
        String access_token = uberCredentialService.getAccessTokenByUser(user);
        HttpHeaders headers = new HttpHeaders();                        // Set headers
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Accept-Language", "en_US");
        headers.set("Content-Type", "application/json");
        String requestJson = gson.toJson(request);
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        return postRequest(url, entity, clazz);
        }

    public <T> Optional<T> postRequestUberAuthedOptional(User user, String url,
                                                         Object request, Class<T> clazz) {
        T t = postRequestUberAuthed(user, url, request, clazz);
        if (t != null) return Optional.of(t);
        else return Optional.empty();
    }

    public <T> Optional<T> putRequestUberAuthed(User user, String url, Object reqBody, Class<T> clazz) {
        String access_token = uberCredentialService.getAccessTokenByUser(user);
        HttpHeaders headers = new HttpHeaders();                        // Set headers
        headers.set("Authorization", "Bearer " + access_token);
        headers.set("Accept-Language", "en_US");
        headers.set("Content-Type", "application/json");
        String json = gson.toJson(reqBody);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        return putRequest(url, entity, clazz);
    }
}
