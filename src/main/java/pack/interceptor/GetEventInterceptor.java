package pack.interceptor;

import com.botscrew.messengercdk.domain.MessengerInterceptor;
import com.botscrew.messengercdk.domain.action.GetEvent;
import com.botscrew.messengercdk.model.MessengerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.handler.OutOfStateHandler;

@Component
public class GetEventInterceptor implements MessengerInterceptor<GetEvent> {

    @Autowired
    OutOfStateHandler outOfStateHandler;

    @Override
    public boolean onAction(GetEvent getEvent) {
        MessengerUser user = getEvent.getMessengerUser();
//        Message message = getEvent.getMessaging().getMessage();
//        outOfStateHandler.handleOutOfState(user, message);
        outOfStateHandler.handleOutOfState(getEvent);
        return true;
    }
}
