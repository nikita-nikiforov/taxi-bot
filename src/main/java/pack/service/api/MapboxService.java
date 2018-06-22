package pack.service.api;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.init.AppProperties;
import pack.model.HistoryResponse.History;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class MapboxService {

    @Autowired
    private AppProperties appProperties;

    public String getHistoryMapUrl(History historyItem) {
        String lat = historyItem.getStart_city().getLatitude();
        String lng = historyItem.getStart_city().getLongitude();
        StringBuilder result = new StringBuilder();
        result.append("https://api.mapbox.com/styles/v1/mapbox/streets-v10/static/")
                .append(lng).append(",").append(lat)
                .append(",9,0,0/300x200?access_token=")
                .append(appProperties.getMAPBOX_ACCESS_TOKEN());
        return result.toString();
    }

    public String getConfirmAddressMapUrl(Coordinates coordinates) {
        String iconUrl = "";
        try {
            iconUrl = URLEncoder.encode(appProperties.getMAP_ICON_URL(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlTemplate = "https://api.mapbox.com/styles/v1/mapbox/streets-v10/static/" +
               "url-%4$s(%1$s,%2$s)/" +
                "%1$s,%2$s,12,0,0/300x200?access_token=%3$s";
        String lat = coordinates.getLatitude().toString();
        String lng = coordinates.getLongitude().toString();
        return String.format(urlTemplate, lng, lat, appProperties.getMAPBOX_ACCESS_TOKEN(),
                iconUrl);
    }

    public String getMarkeredMapUrl(Coordinates coordinates) {
        double lat = coordinates.getLatitude();
        double lng = coordinates.getLongitude();
        return appProperties.getBASE_URL() + "/map?lat=" + lat + "&lng=" + lng;
    }
}
