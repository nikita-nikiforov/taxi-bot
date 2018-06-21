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
import pack.model.HistoryResponse.History;
import pack.service.MessageService;
import pack.service.api.UberApiService;
import pack.service.UserService;

import java.util.List;

@ChatEventsProcessor
public class HistoryHandler {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private Sender sender;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private StartHandler startHandler;

    @Postback(value = Payload.SHOW_TRIPS)
    public void showTrips(User user) {
        List<History> list = uberApiService.getHistoryList(user);
        List<TemplateElement> templateElements = messageService.getHistoryTemplateElements(list);

        Request request = GenericTemplate.builder()
                .elements(templateElements)
                .user(user)
                .build();
        sender.send(request);
        startHandler.handleLoggedState(user);
    }
}
