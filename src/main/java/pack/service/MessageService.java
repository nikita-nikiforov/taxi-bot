package pack.service;

import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.model.EstimateResponse;
import pack.model.HistoryItem;

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
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();      // Start LocalDateTime

            LocalDateTime endTime = Instant.ofEpochSecond(Long.valueOf(historyItem.getEnd_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();      // End LocalDateTime

            String distanse = historyItem.getDistance().substring(0, 4) + " km";    // Distanse
            String date = endTime.toLocalDate().toString();                             // Date
            String startTimeString = startTime.toLocalTime().toString();                // Start time
            String endTimeString = endTime.toLocalTime().toString();                    // End time

            String subtitle = distanse + "\n" +
                  date + " " + startTimeString + " — " + endTimeString;

            String mapImageUrl = mapboxService.getHistoryItemMapUrl(historyItem);       // Image of map

            TemplateElement templateElement = TemplateElement.builder()
                    .title(title)
                    .subtitle(subtitle)
                    .imageUrl(mapImageUrl)
                    .build();
            result.add(templateElement);
        }
        return result;
    }

    public String getEstimateRide(EstimateResponse estimateResponse) {
        StringBuilder answer = new StringBuilder();
        answer.append("Pickup in ").append(estimateResponse.getPickup_estimate()).append(" minutes\n");
        answer.append("Trip duration: ").append(estimateResponse.getTrip().getDuration_estimate() / 60)
                .append(" minutes\n");
        answer.append("Trip distance: ").append(estimateResponse.getTrip().getDistance_estimate())
                .append(" ").append(estimateResponse.getTrip().getDistance_unit()).append("\n");
        answer.append("Price: ").append(estimateResponse.getFare().getDisplay());
        return answer.toString();
    }
}
