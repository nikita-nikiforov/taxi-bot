package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import pack.constant.RideStatus;
import pack.entity.User;
import pack.service.api.UberApiService;
import java.util.NoSuchElementException;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;
import static pack.constant.RideStatus.*;
import static pack.constant.RideStatus.COMPLETED;

@Service
public class FakeTripLogicService {

    @Autowired
    private UberApiService uberApiService;

    // Implements fake logic of Uber ride in Sandbox
    void updateRideStatus(User user, RideStatus currentStatus) {
        try {
            // Sleep for some random time
            TimeUnit.SECONDS.sleep(new SplittableRandom().nextInt(15, 20));
            // If not COMPLETED, make putRequest to update to the next status.
            // (Because when COMPLETED is recieved, Uber removes the trip.
            // So, COMPLETED indicates that there's nothing to update)
            if (currentStatus != COMPLETED) {
                RideStatus newStatus;
                // update the ride status to the next one
                switch (currentStatus) {
                    case PROCESSING:
                        newStatus = ACCEPTED;
                        break;
                    case ACCEPTED:
                        newStatus = ARRIVING;
                        break;
                    case ARRIVING:
                        newStatus = IN_PROGRESS;
                        break;
                    case IN_PROGRESS:
                        newStatus = COMPLETED;
                        break;
                    default:
                        newStatus = currentStatus;
                }
                uberApiService.putSandboxRide(user, newStatus.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchElementException | HttpClientErrorException e) {
            System.out.println("Current ride is deleted.");
        }
    }
}
