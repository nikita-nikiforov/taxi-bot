package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.UberCredential;
import pack.entity.User;
import pack.model.UberAccessTokenResponse;
import pack.model.UberUserProfile;
import pack.service.api.UberApiService;
import pack.service.dao.UberCredentialService;
import pack.service.dao.UserService;

@Service
public class UberAuthService {
    @Autowired
    private UberCredentialService uberCredentialService;

    @Autowired
    private UserService userService;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private FavoritePlaceService favoritePlaceService;

    // TODO
    public boolean authorizeUser(Long chatId, String code) {
        UberCredential myCredential = new UberCredential();     // Credential entity to setStartPoint
        User user = userService.getUserByChatId(chatId);        // Get user by ChatId
        UberAccessTokenResponse accessTokenResponse = uberApiService.getAccessTokenResponse(code); // Get access_token
        // Here handle exception TODO
        // Get access_token and set it
        myCredential.setAccess_token(accessTokenResponse.getAccess_token());
        myCredential.setUser(user);                       // Set user to credential
        uberCredentialService.save(myCredential);         // Save 'cause the following methods need saved user in DB
        // Get user profile via /me and set uuid
        UberUserProfile uberUserProfile = uberApiService.aboutMe(user).get();
        myCredential.setUuid(uberUserProfile.getUuid());
        // If user has favorite places in Uber, then true
        favoritePlaceService.getPlacesList(user).ifPresent(l -> myCredential.setHas_fav_places(true));
        return uberCredentialService.save(myCredential);               // Save
    }
}