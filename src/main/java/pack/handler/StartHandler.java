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
import pack.service.UserService;

@ChatEventsProcessor
public class StartHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private Sender sender;

    @Postback(value = "GET_STARTED", states = "INITIAL")
    public void handleGetStarted(MessengerUser user) {
        Request request = getInitialRequest(user);
        userService.save(user.getChatId(), user.getState());
        sender.send(request);
    }

    @Text(states = {"INITIAL"})
    public void handleInitialState(MessengerUser user, @Text String text) {
        userService.save(user.getChatId(), user.getState());
        Request request = getInitialRequest(user);

        sender.send(request);
    }



    @Postback(value = "SHOW_TRIPS")
    public void handleDefaultButton(MessengerUser user) {
        sender.send(user, "AZAZA");
    }

    private Request getInitialRequest(MessengerUser user) {

        return QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL.toString())
                .postback("Order a taxi", "MAKE_ORDER")
                .postback("Show last trips", "SHOW_TRIPS")
                .postback("Authorize in uber", "AUTH_UBER")
                .build();
    }
}