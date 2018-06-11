package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
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
        sender.send(user, "Hi! Welcome to Taxi Bot. Here you can book a taxi." +
                "Please, enter the start point.");
    }

    @Text(states = {"START_INPUT"})
    public void handleStartInput(MessengerUser user, @Text String text) {
        String answer;
        if (text.equals("Lviv")) {
            userService.save(user.getChatId(), "END_INPUT");
            answer = "Okay. Enter the end point";
        } else{
            answer = "Your location is not supported";
        }
        sender.send(user, answer);
    }
}