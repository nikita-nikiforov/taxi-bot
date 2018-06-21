package pack.init;

import com.botscrew.messengercdk.model.outgoing.element.button.GetStartedButton;
import com.botscrew.messengercdk.model.outgoing.profile.Greeting;
import com.botscrew.messengercdk.model.outgoing.profile.menu.PersistentMenu;
import com.botscrew.messengercdk.model.outgoing.profile.menu.PostbackMenuItem;
import com.botscrew.messengercdk.model.outgoing.profile.menu.WebMenuItem;
import com.botscrew.messengercdk.service.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pack.constant.RideStatus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class Initialization {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    Messenger messenger;

    @PostConstruct
    public void initMessengerProfile() {
        messenger.setGetStartedButton(new GetStartedButton("GET_STARTED"));
        messenger.setGreeting(new Greeting("Hi!"));
        PersistentMenu menu = new PersistentMenu(
                Arrays.asList(
                        new PostbackMenuItem("Call support", "CALL_SUPPORT"),
                        new WebMenuItem("Visit our website", "https://uber.com"))
        );
        messenger.setPersistentMenu(menu);
    }

    // Bean with appropriate next ride statuses. (In order to prevent "in_progress" -> "arriving"
    @Bean(value = "nextRideStatusMap")
    public Map<String, String> nextRideStatusMap() {
        Map<String, String> map = new HashMap<>();
        map.put(RideStatus.UNDEFINED, RideStatus.PROCESSING);
        map.put(RideStatus.PROCESSING, RideStatus.ACCEPTED);
        map.put(RideStatus.ACCEPTED, RideStatus.ARRIVING);
        map.put(RideStatus.ARRIVING, RideStatus.IN_PROGRESS);
        map.put(RideStatus.IN_PROGRESS, RideStatus.COMPLETED);
        return map;
    }

//    @PostConstruct
//    public void updateWebhook() {
//        System.out.println(BASE_URL);
//        messenger.setWebHook("https://5230e671.ngrok.io" + "/messenger/events",
//                Arrays.asList(
//                        WebHook.Field.MESSAGES,
//                        WebHook.Field.POSTBACKS
//                ));
//    }
}