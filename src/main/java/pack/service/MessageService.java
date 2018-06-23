package pack.service;

import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.FareResponse;
import pack.model.HistoryResponse.History;
import pack.model.ReceiptResponse;
import pack.model.UberRideResponse.*;
import pack.service.api.MapboxService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MapboxService mapboxService;

    @Autowired
    private UberRideService uberRideService;

    public List<TemplateElement> getHistoryTemplateElements(List<History> historyList) {
        List<TemplateElement> result = new ArrayList<>();

        // Iterate over Histories from response and transform to TemplateElement
        for (History history : historyList) {
            // Start LocalDateTime transformed from UNIX timestamp
            LocalDateTime startLocalDateTime = Instant.ofEpochSecond(Long.valueOf(history.getStart_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            // End LocalDateTime
            LocalDateTime endLocalDateTime = Instant.ofEpochSecond(Long.valueOf(history.getEnd_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            String distanse = history.getDistance().substring(0, 4) + " km";       // Distanse
            String date = endLocalDateTime.toLocalDate().toString();                    // Date
            String startTimeString = startLocalDateTime.toLocalTime().toString();       // Start time
            String endTimeString = endLocalDateTime.toLocalTime().toString();           // End time

            String title = history.getStart_city().getDisplay_name();                   // Title
            String subtitle = distanse + "\n" +
                  date + " " + startTimeString + " — " + endTimeString;                 // Subtitle
            String mapImageUrl = mapboxService.getHistoryMapUrl(history);       // Image of map

            TemplateElement templateElement = TemplateElement.builder()
                    .title(title)
                    .subtitle(subtitle)
                    .imageUrl(mapImageUrl)
                    .build();
            result.add(templateElement);
        }
        return result;
    }

    public String getTripEstimate(FareResponse fareResponse) {
        StringBuilder answer = new StringBuilder();
        answer.append("Pickup in ").append(fareResponse.getPickup_estimate()).append(" minutes\n");
        answer.append("Trip duration: ").append(fareResponse.getTrip().getDuration_estimate() / 60)
                .append(" minutes\n");
        answer.append("Trip distance: ").append(fareResponse.getTrip().getDistance_estimate())
                .append(" ").append(fareResponse.getTrip().getDistance_unit()).append("\n");
        answer.append("Price: ").append(fareResponse.getFare().getDisplay());
        return answer.toString();
    }

    // Get Driver object for current trip
    public TemplateElement getDriverTemplate(User user) {
        Driver driver = uberRideService.getDriverResponse(user);
        String subtitle = "Rating: " + driver.getRating();
        TemplateElement templateElement = TemplateElement.builder()
                .title(driver.getName())
                .subtitle(subtitle)
                .imageUrl(driver.getPicture_url())
                .build();
        return templateElement;
    }

    public String getDriverPhone(User user) {
        Driver driver = uberRideService.getDriverResponse(user);
        return driver.getPhone_number();
    }

    // Get Vehicle object for current trip
    public TemplateElement getVehicleTemplate(User user) {
        Vehicle vehicle = uberRideService.getVehicleResponse(user);
        String title = vehicle.getMake() + " " + vehicle.getModel();
        String subtitle = "License plate: " + vehicle.getLicense_plate();
        TemplateElement templateElement = TemplateElement.builder()
                .title(title)
                .subtitle(subtitle)
                .imageUrl(vehicle.getPicture_url())
                .build();
        return templateElement;
    }

    public String getReceiptTemplate(ReceiptResponse response) {
        String result = "Your receipt: \nDistance: " + response.getDistance() + " "
                + response.getDistance_label() + " \nDuration: " + response.getDuration()
                + " \nFare: " + response.getTotal_charged();
        return result;
    }
}