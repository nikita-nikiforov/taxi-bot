package pack.service;

import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.json.HistoryItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MapboxService mapboxService;

    public List<TemplateElement> getTemplateElements(List<HistoryItem> historyList) {
        List<TemplateElement> result = new ArrayList<>();

        for (HistoryItem historyItem : historyList) {

            String title = historyItem.getStart_city().getDisplay_name();    // Title

            LocalDateTime startTime = Instant.ofEpochSecond(Long.valueOf(historyItem.getStart_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            LocalDateTime endTime = Instant.ofEpochSecond(Long.valueOf(historyItem.getEnd_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            String distanse = historyItem.getDistance().substring(0, 4) + " km";
            String date = endTime.toLocalDate().toString();
            String startTimeString = startTime.toLocalTime().toString();
            String endTimeString = endTime.toLocalTime().toString();

            String subtitle = distanse + "\n" +
                  date + " " + startTimeString + " — " + endTimeString;

            String mapImageUrl = mapboxService.getHistoryItemMapUrl(historyItem);

            TemplateElement templateElement = TemplateElement.builder()
                    .title(title)
                    .subtitle(subtitle)
                    .imageUrl(mapImageUrl)
                    .build();
            result.add(templateElement);
        }
        return result;
    }
}
