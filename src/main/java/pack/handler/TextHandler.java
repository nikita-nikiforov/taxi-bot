package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.constant.MessageText;
import pack.service.UserService;

@ChatEventsProcessor
public class TextHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private OutOfStateHandler outOfStateHandler;

    private Sender sender;

    @Text(states = {"START_INPUT"})
    public void handleStartInput(MessengerUser user, @Text String text) {
        String answer;
        if (text.equals("Lviv")) {
            userService.save(user.getChatId(), "END_INPUT");
            answer = MessageText.START_INPUT_TRUE.toString();
        } else {
            answer = MessageText.START_INPUT_FALSE.toString();
        }
        sender.send(user, answer);
    }

    @Text(states = {"END_INPUT"})
    public void handleEndInput(MessengerUser user, @Text String text) {
        String answer;
        if (text.equals("Odesa")) {
            userService.save(user.getChatId(), "WAIT_FOR_CAR");
            answer = MessageText.END_INPUT_TRUE.toString();
        } else {
            answer = MessageText.END_INPUT_FALSE.toString();
        }
        sender.send(user, answer);
    }

    @Text(states = {"WAIT_FOR_CAR"})
    public void handleWaitForCar(MessengerUser user, @Text String text) {
        sender.send(user, MessageText.WAIT_FOR_CAR.toString());
    }

    @Text
    public void handleDefault(MessengerUser user, @Text String text) {
//        outOfStateHandler.handleOutOfState();
        sender.send(user, "I don't understand you");

    }

    @Autowired
    public void setSender(Sender sender) {
        this.sender = sender;
    }
}