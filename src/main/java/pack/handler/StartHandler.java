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
import pack.constant.Payload;
import pack.constant.State;
import pack.entity.User;
import pack.model.EstimateRequest;
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

    @Postback(value = Payload.GET_STARTED, states = State.INITIAL)
    public void handleGetStarted(MessengerUser user) {
        userService.save(user.getChatId(), user.getState());

        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.GET_STARTED_INITIAL)
                .postback("Log in", Payload.UBER_AUTH)
                .build();
        sender.send(request);
    }

    @Text(states = {State.INITIAL})
    public void handleLoggedText(User user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.INITIAL)
                .postback("Log in", Payload.UBER_AUTH)
                .build();
        sender.send(request);
    }

    @Text(states = {State.LOGGED})
    public void handleAuthorizedState(MessengerUser user) {
        Request request = QuickReplies.builder()
                .user(user)
                .text(MessageText.AUTHORIZED)
                .postback("Order a taxi", Payload.MAKE_ORDER)
                .postback("Show last trips", Payload.SHOW_TRIPS)
                .postback("Test", "TEST_BUTTON")
                .build();
        sender.send(request);
    }

    @Postback(value = "TEST_BUTTON")
    public void handleTestButton(User user) {
        EstimateRequest request = new EstimateRequest();
        request.setStart_latitude(49.851991);
        request.setStart_longitude(24.024593);
        request.setEnd_latitude(49.8345207);
        request.setEnd_longitude(24.0181283);
        request.setProduct_id("99863383-8421-42e1-bd56-ffcb585099de");

        uberService.getEstimateResponse(user, request);


    }
}