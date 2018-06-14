package pack.service;

import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.core.auth.OAuth2Credentials;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.core.client.CredentialsSession;
import com.uber.sdk.core.client.ServerTokenSession;
import com.uber.sdk.core.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.model.UserProfile;
import com.uber.sdk.rides.client.services.RidesService;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UberService {


    public UberService() {
    }

    public ServerTokenSession getServerTokenSession() {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_")
                .setServerToken("zwRUhoz8pVi9vQ31Pb9n5Lwb7oXQ6wT5ZIxkBKkg")
                .build();
        return new ServerTokenSession(config);
    }

    public void authUser() {
        List<Scope> scopes = new ArrayList<>();
        scopes.add(Scope.PROFILE);
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("NJo0_9PAUJIAEy1nczsL6_TN0GCv1tm_")
                .setClientSecret("CXWW7BdFQsJxgXUumM8A-Z-oDOnBIkF3bBZ3AVZT")
                .setScopes(scopes)
                .setRedirectUri("https://cd0d6cb4.ngrok.io/uber-link")
                .build();

        OAuth2Credentials credentials = new OAuth2Credentials.Builder()
                .setSessionConfiguration(config)
                .build();

        try {
            String authUrl = credentials.getAuthorizationUrl();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String code = "EcN4X4lsytRWm3U4Xdbny0KcRZKQiG";
        String accessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6IjZkdEttYVBIUXFXVlAvYzF0R3Noemc9PSIsImV4cGlyZXNfYXQiOjE1MzE2NTY2MDAsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.sYuKpVZKlyt0uls6uTn2rkvVwAnd89fPYMukLWKE31w";
        Credential credential1 = credentials.authenticate(code, accessToken);
//        credentials.
        Credential credential = credentials.loadCredential(accessToken);
        CredentialsSession credentialsSession = new CredentialsSession(config, credential);
        RidesService ridesService = UberRidesApi.with(credentialsSession).build().createService();
        Call<UserProfile> userProfile = ridesService.getUserProfile();
        try {
            Response<UserProfile> response = ridesService.getUserProfile().execute();
            if (response.isSuccessful()) {
                UserProfile profile = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getCredentials() {
//        Credential credential = new Credential
//                .Builder(BearerToken.authorizationHeaderAccessMethod());


    }

}
