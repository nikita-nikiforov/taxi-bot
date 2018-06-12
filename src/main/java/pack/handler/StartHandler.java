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
import pack.service.UserService;

@ChatEventsProcessor
public class StartHandler {

    @Autowired
    private UserService userService;

    private Sender sender;

    @Postback(value = "GET_STARTED", states = "INITIAL")
    public void handleGetStarted(MessengerUser user) {
        sender.send(user, MessageText.INITIAL.toString());
        userService.save(user.getChatId(), user.getState());
    }

    @Text(states = {"INITIAL"})
    public void handleInitialState(MessengerUser user, @Text String text) {
        userService.save(user.getChatId(), user.getState());
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL.toString())
                .postback("Order a taxi", "MAKE_ORDER")
                .postback("Show last trips", "SHOW_TRIPS")
                .build();

        sender.send(request);
    }

    @Postback(value = "MAKE_ORDER", states = "INITIAL")
    public void handleMakeOrder(User user) {
        userService.save(user.getChatId(), "START_INPUT");
        sender.send(user, MessageText.START_INPUT.toString());
    }

    @Postback(value = "SHOW_TRIPS", states = "INITIAL")
    public void handleDefaultButton(MessengerUser user) {
        sender.send(user, "AZAZA");
    }

    @Autowired
    public void setUserService(Sender sender) {
        this.sender = sender;
    }
}
