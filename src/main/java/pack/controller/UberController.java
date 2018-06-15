package pack.controller;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pack.entity.Ride;
import pack.json.UberAccessTokenRequest;
import pack.json.UberAccessTokenResponse;
import pack.json.UberAuthorize;
import pack.service.UberService;

@Controller
public class UberController {

    @Autowired
    UberService uberService;

    @ResponseBody
    @GetMapping("uber-link")
    public String getUberCode(@RequestParam("code") String code) {
        uberService.handleCode(code);
        return code;
    }

    @GetMapping("get-access-token")
    public void getUberAccessToken(@RequestParam("access_token") String access_token) {
        System.out.println(access_token);

    }

    @ResponseBody
    @PostMapping("getJSON")
    public UberAccessTokenResponse returnRide(@RequestBody UberAccessTokenRequest json) {
        System.out.println("Get JSON: " + json);
        UberAccessTokenResponse response = new UberAccessTokenResponse("1", "2", "3", "4", "5", "6");

        return response;
    }

    @ResponseBody
    @GetMapping("olaola")
    public Ride getRide() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Ride> request = new HttpEntity<>(new Ride(666.0, 666.0, 666.0, 666.0));
        String url = "https://cd0d6cb4.ngrok.io/getJSON";
        Ride ride = restTemplate.postForObject(url, request, Ride.class);
        return ride;
    }

    @ResponseBody
    @GetMapping("test")
    public ResponseEntity testRestTemplate() {

//        RestTemplate rest = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
//        String url = "https://login.uber.com/oauth/v2/authorize";
//
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("client_id", "NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_")
//                .queryParam("response_type", "code")
//                .queryParam("redirect_uri", "https://af350a13.ngrok.io/uber-link");
//
//
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<String> response = rest.exchange(builder.toUriString(),
//                HttpMethod.GET, entity, String.class);
//        return response;

        RestTemplate rest = new RestTemplate();

        UberAuthorize request = new UberAuthorize("NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_",
                "code", "https://cd0d6cb4.ngrok.io/uber-link");

        HttpEntity<UberAuthorize> httpEntity = new HttpEntity<>(request);

//        ResponseEntity<String> response = rest.getForEntity("https://login.uber.com/oauth/v2/authorize",
//                request, String.class);

//        HttpHeaders headers = response.getHeaders();
//        URI location = headers.getLocation();
        return null;
    }

    @GetMapping("test2")
    public String testtest2() {
        uberService.authUser();
        return "hello";
    }

    @ResponseBody
    @GetMapping("test3")
    public UberAccessTokenResponse test3() {
        //https://login.uber.com/oauth/v2/token?
// client_secret=CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT
// &client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_
// &grant_type=authorization_code
// &redirect_uri=https://cd0d6cb4.ngrok.io/uber-link
// &code=OFpuipxXpAAh71dDgw0572B9QHI2Dt
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();

        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.setRequestFactory(factory);

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        UberAccessTokenRequest request = new UberAccessTokenRequest("CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT",
                "NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_", "authorization_code",
                "https://cd0d6cb4.ngrok.io/uber-link", "3Q1FPOgbL0CPNF0IDH1ag4F4n0X5DF");
        HttpEntity<UberAccessTokenRequest> httpEntity = new HttpEntity<>(request);

        UberAccessTokenResponse response = restTemplate.postForObject("https://login.uber.com/oauth/v2/authorize",
                httpEntity, UberAccessTokenResponse.class);
        return response;
    }

    @ResponseBody
    @GetMapping("geta")
    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://login.uber.com/oauth/v2/authorize?client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_&response_type=code&redirect_uri=https://cd0d6cb4.ngrok.io/uber-link";
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }





    // https://login.uber.com/oauth/v2/authorize?client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_&response_type=code&redirect_uri=https://cd0d6cb4.ngrok.io/uber-link
    // https://login.uber.com/oauth/v2/token?client_secret=CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT&client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_&grant_type=authorization_code&redirect_uri=https://cd0d6cb4.ngrok.io/get-access-token&code=VHNugPCc3JpQ9cQtMTHPyBDwjxsyYC
//    curl -F 'client_secret=CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT' \
//            -F 'client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_' \
//            -F 'grant_type=authorization_code' \
//            -F 'redirect_uri=https://af350a13.ngrok.io/get-access-token' \
//            -F 'code=crd.EA.CAESENqmbR3G-EnduUuerQBOfNYiATE.iINno0qhH0Zsas5TVY_NqYtsQG80Yt-lubnmcOyuDJQ' \
//    https://login.uber.com/oauth/v2/token
//    curl -F 'client_secret=CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT' \ -F 'client_id=NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_' \ -F 'grant_type=authorization_code' \ -F 'redirect_uri=https://af350a13.ngrok.io/get-access-token' \ -F 'code=crd.EA.CAESEBuQ7feDI0EzpAbJnZV9RSMiATE.I_5dtkWVc_0_8l_7mCuZixWupsbCrTRkE1iqAwifpZY' \ https://login.uber.com/oauth/v2/token

}
