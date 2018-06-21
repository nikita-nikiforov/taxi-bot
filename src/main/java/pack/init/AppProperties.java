package pack.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Value("${ngrok-url}")
    private String BASE_URL;

    @Value("${uber-login-link}")
    private String LOGIN_LINK;

    @Value("${uber-logout-link}")
    private String LOGOUT_LINK;

    @Value("${my-uber-access-key}")
    private String MY_ACCESS_KEY;

    @Value("${uber.client-secret}")
    private String CLIENT_SECRET;

    @Value("${uber.client-id}")
    private String CLIENT_ID;

    @Value("${mapbox.access-token}")
    private String MAPBOX_ACCESS_TOKEN;

    @Value("${map-icon-url}")
    private String MAP_ICON_URL;

    public String getLOGIN_LINK() {
        return LOGIN_LINK;
    }

    public String getLOGOUT_LINK() {
        return LOGOUT_LINK;
    }

    public String getBASE_URL() {
        return BASE_URL;
    }

    public String getMY_ACCESS_KEY() {
        return MY_ACCESS_KEY;
    }

    public String getCLIENT_SECRET() {
        return CLIENT_SECRET;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public String getMAPBOX_ACCESS_TOKEN() {
        return MAPBOX_ACCESS_TOKEN;
    }

    public String getMAP_ICON_URL() {
        return MAP_ICON_URL;
    }
}
