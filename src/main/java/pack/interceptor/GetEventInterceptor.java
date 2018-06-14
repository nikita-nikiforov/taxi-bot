//package pack.interceptor;
//
//import com.botscrew.messengercdk.domain.MessengerInterceptor;
//import com.botscrew.messengercdk.domain.action.GetEvent;
//import com.botscrew.messengercdk.model.MessengerUser;
//import com.botscrew.messengercdk.model.incomming.EventType;
//import com.botscrew.messengercdk.model.incomming.Message;
//import org.springframework.stereotype.Component;
//
//@Component
//public class GetEventInterceptor implements MessengerInterceptor<GetEvent> {
//
//    @Override
//    public boolean onAction(GetEvent getEvent) {
//        MessengerUser user = getEvent.getMessengerUser();
//
//        Message message = getEvent.getMessaging().getMessage();
//
//        if (getEvent.getEventType() == EventType.TEXT) {
//            String text = getEvent.getMessaging().getMessage().getText();
//            switch (text.toLowerCase().trim()) {
//                case "exit":
//                    System.out.println("EXIT!!!");
//                    return false;
//            }
//        }
//
//        System.out.println("NOT EXIT!!!");
//
//        return true;
//    }
//}
