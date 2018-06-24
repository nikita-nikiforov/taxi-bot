package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import com.botscrew.messengercdk.model.outgoing.element.TemplateElement;
import com.botscrew.messengercdk.model.outgoing.element.WebAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.entity.User;
import pack.model.FareResponse;
import pack.model.ReceiptResponse;
import pack.model.UberRideResponse.Driver;
import pack.model.UberRideResponse.Vehicle;
import pack.model.custom.HistoryItem;
import pack.model.custom.PlaceItem;
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

    public List<TemplateElement> getHistoryTemplateElements(List<HistoryItem> historyList) {
        List<TemplateElement> result = new ArrayList<>();

        // Iterate over Histories from response and transform to TemplateElement
        for (HistoryItem history : historyList) {
            // Start LocalDateTime transformed from UNIX timestamp
            LocalDateTime startLocalDateTime = Instant.ofEpochSecond(Long.valueOf(history.getStart_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            // End LocalDateTime
            LocalDateTime endLocalDateTime = Instant.ofEpochSecond(Long.valueOf(history.getEnd_time()))
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            String distanse = String.valueOf(history.getDistance()).substring(0, 4) + " km";       // Distanse
            String date = endLocalDateTime.toLocalDate().toString();                    // Date
            String startTimeString = startLocalDateTime.toLocalTime().toString();       // Start time
            String endTimeString = endLocalDateTime.toLocalTime().toString();           // End time
            String productName = history.getProduct().getDisplay_name();                // Get product name (e.g. "UberX")
            StringBuilder fare = new StringBuilder();
            // Fare can be absent because of Uber privacy rules
            history.getReceipt().ifPresent(r -> fare.append("\nFare: ").append(r.getTotal_fare()));

            String title = history.getStart_city().getDisplay_name() + ", " + productName;       // Title
            String subtitle = date + "\n" + startTimeString + " — " + endTimeString +
                    "\nDistance: " + distanse + fare;                                           // Subtitle
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

    // Get Vehicle object for current user's trip
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


    public List<TemplateElement> getPlaceTemplates(List<PlaceItem> placeItems) {
        List<TemplateElement> result = new ArrayList<>();

        placeItems.forEach(placeItem -> {
            // Set title, subtitle and get url of markered map
            String title = placeItem.getName();
            String subtitle = placeItem.getAddress();
            String mapImageUrl = mapboxService.getConfirmAddressMapUrl(placeItem.getCoordinates());

            TemplateElement templateElement = getOpenableMapTemplate(placeItem.getCoordinates(), title);
            templateElement.setTitle(title);
            templateElement.setSubtitle(subtitle);
            result.add(templateElement);                        // Add to result list
        });
        return result;
    }

    public TemplateElement getConfirmMapTemplate(Coordinates coords) {
        TemplateElement templateELement = getOpenableMapTemplate(coords, "Found place");
        templateELement.setTitle("Pinned Location");
        return templateELement;
    }

    private TemplateElement getOpenableMapTemplate(Coordinates coords, String title) {
        return TemplateElement.builder()
                .imageUrl(mapboxService.getConfirmAddressMapUrl(coords))
                .defaultAction(WebAction.builder()
                        .url(mapboxService.getMarkeredMapUrl(coords, title))
                        .makeCompactWebView()
                        .build())
                .build();
    }
}