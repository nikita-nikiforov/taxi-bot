package pack.handler;

import com.botscrew.messengercdk.domain.action.GetEvent;
import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.model.incomming.Message;
import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.constant.MessageText;
import pack.service.UserService;

@Component
public class OutOfStateHandler {

    @Autowired
    private UserService userService;

    private Sender sender;

    public void handleOutOfState(GetEvent getEvent) {
        switch (getEvent.getEventType()) {
            case TEXT:
                handleText(getEvent);
                break;
        }
    }

    private void handleText(GetEvent getEvent) {
        MessengerUser user = getEvent.getMessengerUser();
        Message message = getEvent.getMessaging().getMessage();
        String text = message.getText();
            switch (text.toLowerCase()) {
                case "exit":
                    handleExit(user);
            }
    }

    private void handleExit(MessengerUser user) {
        userService.save(user.getChatId(), "INITIAL");
        sender.send(user, MessageText.EXIT.toString());
    }

    @Autowired
    public void setSender(Sender sender) {
        this.sender = sender;
    }
}