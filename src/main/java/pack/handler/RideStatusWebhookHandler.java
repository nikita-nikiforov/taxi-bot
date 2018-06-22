package pack.handler;

import com.botscrew.messengercdk.service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pack.entity.UberRide;
import pack.entity.User;

@Component
public class RideStatusWebhookHandler {

    @Autowired
    private Sender sender;

    @Autowired
    private UberRideHandler uberRideHandler;

    public void handleProcessing(User user, UberRide uberRide) {
        uberRideHandler.handleUberProcessingText(user);
    }

    public void handleAccepted(User user, UberRide uberRide) {
        uberRideHandler.handleUberAcceptedText(user);
    }

    public void handleArriving(User user, UberRide uberRide) {
        uberRideHandler.handleUberArrivingText(user);
    }

    public void handleInProgress(User user, UberRide uberRide) {
        uberRideHandler.handleUberInProgressText(user);
    }

    public void handleCompleted(User user, UberRide uberRide) {
        uberRideHandler.handleUberCompletedText(user);
    }
}
