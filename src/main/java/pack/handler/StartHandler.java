package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Postback;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.outgoing.builder.ButtonTemplate;
import com.botscrew.messengercdk.model.outgoing.builder.QuickReplies;
import com.botscrew.messengercdk.model.outgoing.builder.TextMessage;
import com.botscrew.messengercdk.model.outgoing.element.button.WebButton;
import com.botscrew.messengercdk.model.outgoing.request.Request;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.init.AppProperties;
import pack.service.UserService;

@ChatEventsProcessor
public class StartHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private Sender sender;

    @Text(states = State.INITIAL)
    @Postback(value = Payload.GET_STARTED, states = State.INITIAL)
    public void handleGetStarted(User user) {
        userService.save(user.getChatId(), user.getState());
        String login_url = appProperties.getLOGIN_URL() + "&state=" + user.getChatId();
        Request request = ButtonTemplate.builder()
                .user(user)
                .text(MessageText.GET_STARTED_INITIAL)
                .addButton(new WebButton.Builder()
                        .title("Log in")
                        .url(login_url)
                        .makeTallWebView()
                        .build())
                .addButton(new WebButton("Log out", appProperties.getLOGOUT_URL()))
                .build();
        sender.send(request);
    }

    // unused
    public void failedToLogin(User user) {
        Request request = TextMessage.builder()
                .user(user)
                .text(MessageText.LOGIN_FAILED)
                .build();
        sender.send(request);
    }

    @Postback(value = Payload.GET_STARTED)
    @Text(states = {State.LOGGED})
    @Location(states = State.LOGGED)
    public void handleLoggedState(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.LOGGED)
                .postback("Order a taxi", Payload.MAKE_ORDER)
                .postback("Show last trips", Payload.SHOW_TRIPS)
                .build();
        sender.send(request);
    }
}