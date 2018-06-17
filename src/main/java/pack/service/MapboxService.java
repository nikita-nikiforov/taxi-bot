package pack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pack.json.HistoryItem;

@Service
public class MapboxService {

    @Value("${mapbox.access-token}")
    private String MAPBOX_ACCESS_TOKEN;

    public String getHistoryItemMapUrl(HistoryItem historyItem) {
        String lat = historyItem.getStart_city().getLatitude();
        String lng = historyItem.getStart_city().getLongitude();

        StringBuilder result = new StringBuilder();
        result.append("https://api.mapbox.com/styles/v1/mapbox/streets-v10/static/")
                .append(lng).append(",").append(lat)
                .append(",10.2,0,0/300x200?access_token=pk.eyJ1IjoibmlraWZvcm92cGl6emEiLCJhIjoiY2ppaXZ3c3N4MW5mMjNxbGkxM2M2OXQwdSJ9.-ae7oZJHWun4nNTrVZ1atA");

        return result.toString();
    }
}
