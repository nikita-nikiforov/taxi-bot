package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.entity.User;
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
    private Sender sender;

    @Postback(value = "GET_STARTED", states = "INITIAL")
    public void handleGetStarted(MessengerUser user) {
        userService.save(user.getChatId(), user.getState());

        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL.toString())
                .postback("Log in", "UBER_AUTH")
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

    @Text(states = {"LOGGED"})
        public void handleAuthorizedState(MessengerUser user, @Text String text) {
        userService.save(user.getChatId(), user.getState());
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.AUTHORIZED.toString())
                .postback("Order a taxi", "MAKE_ORDER")
                .postback("Show last trips", "SHOW_TRIPS")
                .postback("Uber auth", "UBER_AUTH")
                .build();

        sender.send(request);
    }

}