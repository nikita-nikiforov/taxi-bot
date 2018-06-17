package pack.service;

import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.json.HistoryItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MapboxService mapboxService;

    public String getHistoryRides(List<HistoryItem> list) {
        StringBuilder builder = new StringBuilder("Your last trips:\n");

        list.forEach(h -> {
            Date date = Date.from(Instant.ofEpochSecond(Long.valueOf(h.getStart_time())));
//            String time = date.
            builder.append(h.getStart_city().getDisplay_name()).append(date.getDate()).append("\n");

        });
        return builder.toString();
    }

    public List<TemplateElement> getTemplateElements(List<HistoryItem> historyList) {
        List<TemplateElement> result = new ArrayList<>();
        for (HistoryItem historyItem : historyList) {

            String title = historyItem.getStart_city().getDisplay_name();    // Title

            StringBuilder subtitle = new StringBuilder();                       // Subtitle
            subtitle.append(historyItem.getDistance())
                    .append(" km. ").append(historyItem.getStart_time())
                    .append(" — ").append(historyItem.getEnd_time());

            String mapImageUrl = mapboxService.getHistoryItemMapUrl(historyItem);

            TemplateElement templateElement = TemplateElement.builder()
                    .title(title)
                    .subtitle(subtitle.toString())
                    .imageUrl(mapImageUrl)
                    .build();
            result.add(templateElement);
        }

        return result;
    }



}
