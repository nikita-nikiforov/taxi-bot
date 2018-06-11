package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constants.Messages;
import pack.service.UserService;

@ChatEventsProcessor
public class TextHandler {

    @Autowired
    UserService userService;

    private final Sender sender;

    @Autowired
    public TextHandler(Sender sender) {
        this.sender = sender;
    }

    @Text(states = {"INITIAL"})
    public void handleInitialState(MessengerUser user, @Text String text) {
        userService.save(user.getChatId(), "START_INPUT");
        sender.send(user, Messages.INITIAL);
    }

    @Text(states = {"START_INPUT"})
    public void handleStartInput(MessengerUser user, @Text String text) {
        String answer;
        if (text.equals("Lviv")) {
            userService.save(user.getChatId(), "END_INPUT");
            answer = Messages.START_INPUT_TRUE;
        } else {
            answer = Messages.START_INPUT_FALSE;
        }
        sender.send(user, answer);
    }

    @Text(states = {"END_INPUT"})
    public void handleEndInput(MessengerUser user, @Text String text) {
        String answer;
        if (text.equals("Odesa")) {
            userService.save(user.getChatId(), "WAIT_FOR_CAR");
            answer = Messages.END_INPUT_TRUE;
        } else {
            answer = Messages.END_INPUT_FALSE;
        }
        sender.send(user, answer);
    }

    @Text(states = {"WAIT_FOR_CAR"})
    public void handleWaitForCar(MessengerUser user, @Text String text) {
        sender.send(user, Messages.WAIT_FOR_CAR);
    }
}