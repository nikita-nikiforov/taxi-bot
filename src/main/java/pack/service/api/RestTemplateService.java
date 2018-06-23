package pack.service.api;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pack.service.dao.UberCredentialService;

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

    public void deleteRequest(String url, HttpEntity httpEntity) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Object.class);
    }






}
