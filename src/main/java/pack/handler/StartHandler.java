package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.model.outgoing.builder.GenericTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.entity.User;
import pack.json.HistoryItem;
import pack.service.MessageService;
import pack.service.UberService;
import pack.service.UserService;

import java.util.List;

@ChatEventsProcessor
public class StartHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UberService uberService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private Sender sender;

    @Postback(value = "GET_STARTED", states = "INITIAL")
    public void handleGetStarted(MessengerUser user) {
        userService.save(user.getChatId(), user.getState());

        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL.toString())
                .postback("Log in", "AUTH_UBER")
                .build();
        sender.send(request);
    }

    @Text(states = {"INITIAL"})
    public void handleLoggedText(User user, @Text String text) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL.toString())
                .postback("Log in", "AUTH_UBER")
                .build();
        sender.send(request);
    }

    @Postback(value = "AUTH_UBER", states = "INITIAL")
    public void authInUber(User user) {
        uberService.authUser(user);
        // TODO if okay, then save
        userService.save(user, "LOGGED");
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.AUTHORIZED.toString())
                .postback("Order a taxi", "MAKE_ORDER")
                .postback("Show last trips", "SHOW_TRIPS")
                .postback("MAKE MAGIC", "UBER_CALL")
                .build();

        sender.send(request);

    }

    @Text(states = {"LOGGED"})
    public void handleAuthorizedState(MessengerUser user, @Text String text) {
        userService.save(user.getChatId(), user.getState());
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.AUTHORIZED.toString())
                .postback("Order a taxi", "MAKE_ORDER")
                .postback("Show last trips", "SHOW_TRIPS")
                .postback("MAKE MAGIC", "UBER_CALL")
                .build();

        sender.send(request);
    }



    @Postback(value = "SHOW_TRIPS")
    public void handleDefaultButton(User user) {
        List<HistoryItem> list = uberService.getHistoryList(user);
//        String answer = messageService.getHistoryRides(list);
        List<TemplateElement> templateElements = messageService.getTemplateElements(list);

        Request request = GenericTemplate.builder()
                .elements(templateElements)
                .user(user)
                .build();

        sender.send(request);
    }

}