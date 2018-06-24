package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.Payload;
import pack.entity.User;
import pack.model.custom.HistoryItem;
import pack.service.HistoryService;
import pack.service.MessageService;

import java.util.List;

@ChatEventsProcessor
public class HistoryHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StartHandler startHandler;

    @Postback(value = Payload.SHOW_TRIPS)
    public void handleShowHistory(User user) {
        List<HistoryItem> list = historyService.getHistoryItemList(user);           // Get History items
        // Pass them to messageService to get TemplateElements
        List<TemplateElement> templateElements = messageService.getHistoryTemplateElements(list);

        Request request = GenericTemplate.builder()
                .user(user)
                .elements(templateElements)
                .build();
        sender.send(request);
        startHandler.handleLoggedState(user);
    }
}