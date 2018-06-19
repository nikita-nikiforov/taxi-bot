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
import pack.init.AppProperties;
import pack.model.HistoryItem;
import pack.service.MessageService;
import pack.service.UberService;
import pack.service.UserService;

import java.util.List;

@ChatEventsProcessor
public class UberHandler {

    @Autowired
    AppProperties appProperties;

    @Autowired
    Sender sender;

    @Autowired
    UberService uberService;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @Postback(value = Payload.SHOW_TRIPS)
    public void showTrips(User user) {
        List<HistoryItem> list = uberService.getHistoryList(user);
        List<TemplateElement> templateElements = messageService.getTemplateElements(list);

        Request request = GenericTemplate.builder()
                .elements(templateElements)
                .user(user)
                .build();

        sender.send(request);
    }
}
