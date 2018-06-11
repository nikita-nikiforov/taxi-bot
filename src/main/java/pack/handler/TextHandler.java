package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
//import pack.service.UserService;

@ChatEventsProcessor
public class TextHandler {

    @Autowired
//    UserService userService;

    private final Sender sender;

    @Autowired
    public TextHandler(Sender sender) {
        this.sender = sender;
    }

    @Text
    public void handleText(MessengerUser user, @Text String text) {
        StringBuilder answer = new StringBuilder("The answer from handleText: \"")
                .append(text).append("\"").append(user.getChatId());
        sender.send(user, answer.toString());
//        userService.getByChatIdAndPageId()
    }
}