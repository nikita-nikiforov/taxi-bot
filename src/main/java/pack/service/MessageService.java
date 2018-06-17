package pack.service;

import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.json.HistoryItem;

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

            String subtitle = historyItem.getDistance() +
                    " km. " + historyItem.getStart_time() +
                    " — " + historyItem.getEnd_time();

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
