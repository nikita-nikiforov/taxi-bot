package pack.handler;

import com.botscrew.botframework.annotation.ChatEventsProcessor;
import com.botscrew.botframework.annotation.Location;
import com.botscrew.botframework.annotation.Text;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import pack.entity.User;

@ChatEventsProcessor
public class DefaultHandler {
    @Autowired
    private Sender sender;

    @Text
    @Location
    public void handleDefault(User user) {
        sender.send(user, "Can't understand you.");
    }
}