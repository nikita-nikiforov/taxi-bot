package pack.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    @Value("${ngrok.url}")
    private String BASE_URL;

    @Value("${uber.login-url}")
    private String LOGIN_URL;

    @Value("${uber.login-redirect-url}")
    private String LOGIN_REDIRECT_URL;

    @Value("${uber.logout-url}")
    private String LOGOUT_URL;

    @Value("${uber.client-secret}")
    private String CLIENT_SECRET;

    @Value("${uber.client-id}")
    private String CLIENT_ID;

    @Value("${mapbox.access-token}")
    private String MAPBOX_ACCESS_TOKEN;

    @Value("${map-icon-url}")
    private String MAP_ICON_URL;

    public String getLOGIN_URL() {
        return LOGIN_URL;
    }

    public String getLOGIN_REDIRECT_URL() {
        return LOGIN_REDIRECT_URL;
    }

    public String getLOGOUT_URL() {
        return LOGOUT_URL;
    }

    public String getBASE_URL() {
        return BASE_URL;
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
