package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.model.outgoing.builder.ButtonTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.element.button.WebButton;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.init.AppProperties;
import pack.service.MessageService;
import pack.service.UberAuthService;
import pack.service.UberService;
import pack.service.UserService;

@ChatEventsProcessor
public class StartHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private UberService uberService;

    @Autowired
    private UberAuthService uberAuthService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private Sender sender;

    @Postback(value = Payload.GET_STARTED, states = State.INITIAL)
    public void handleGetStarted(MessengerUser user) {
        userService.save(user.getChatId(), user.getState());

        Request request = ButtonTemplate.builder()
                .user(user)
                .text(MessageText.GET_STARTED_INITIAL)
                .addButton(new WebButton.Builder()
                        .title("Log in")
                        .url(appProperties.getLOGIN_LINK() + "&state=" + user.getChatId())
                        .makeTallWebView()
                        .build())
                .addButton(new WebButton("Log out", appProperties.getLOGOUT_LINK()))
                .build();
        sender.send(request);
    }

    @Text(states = {State.INITIAL})
    public void handleInitialText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL)
                .postback("Log in", Payload.UBER_AUTH)  // TODO
                .build();
        sender.send(request);
    }

    @Text(states = {State.LOGGED})
    public void handleAuthorizedState(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.LOGGED)
                .postback("Order a taxi", Payload.MAKE_ORDER)
                .postback("Show last trips", Payload.SHOW_TRIPS)
                .build();
        sender.send(request);
    }

}