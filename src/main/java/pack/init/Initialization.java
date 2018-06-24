package pack.init;

import com.botscrew.messengercdk.model.outgoing.element.button.GetStartedButton;
import com.botscrew.messengercdk.model.outgoing.profile.Greeting;
import com.botscrew.messengercdk.model.outgoing.profile.menu.PersistentMenu;
import com.botscrew.messengercdk.model.outgoing.profile.menu.WebMenuItem;
import com.botscrew.messengercdk.service.Messenger;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pack.constant.MessageText;
import pack.constant.Payload;
import pack.constant.RideStatus;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static pack.constant.RideStatus.*;

@Component
public class Initialization {
    @Autowired
    Messenger messenger;

    @PostConstruct
    public void initMessengerProfile() {
        messenger.setGetStartedButton(new GetStartedButton(Payload.START));
        messenger.setGreeting(new Greeting(MessageText.BOT_GREETING));
        PersistentMenu menu = new PersistentMenu(
                Arrays.asList(
                        new WebMenuItem("Get help", "https://help.uber.com/")));
        messenger.setPersistentMenu(menu);
    }

    @Bean
    public Gson getGson() {
        return new Gson();
    }

    // Bean with appropriate next ride statuses. (In order to prevent "in_progress" -> "arriving"
    @Bean(value = "nextRideStatusMap")
    public Map<RideStatus, RideStatus> nextRideStatusMap() {
        Map<RideStatus, RideStatus> map = new HashMap<>();
        map.put(CREATED, PROCESSING);
        map.put(PROCESSING, ACCEPTED);
        map.put(ACCEPTED, ARRIVING);
        map.put(ARRIVING, IN_PROGRESS);
        map.put(IN_PROGRESS, COMPLETED);
        map.put(COMPLETED, FINISHED);
        return map;
    }
}