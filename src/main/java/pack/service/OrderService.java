package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.OrderRepository;
import pack.entity.Orderr;
import pack.entity.User;
import pack.model.EstimateRequest;
import pack.model.EstimateResponse;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    GeocodingService geocodingService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserService userService;

    @Autowired
    UberService uberService;

    @Autowired
    MessageService messageService;

    public Optional<Coordinates> handleAddress(String address) {
        return geocodingService.getCoordinatesFromAddress(address);
    }

    public void setStartLocation(User user, Coordinates coord) {
        Optional<Orderr> foundOrder = getOrderOptionalByChatId(user.getChatId());
        Orderr result = foundOrder.orElseGet(() -> new Orderr(user));
        result.setStartLat(coord.getLatitude());
        result.setStartLong(coord.getLongitude());
        orderRepository.save(result);
    }


    public void addEndPoint(User user, Coordinates coord) {
        Orderr order = orderRepository.findByUserChatId(user.getChatId());
        order.setEndLat(coord.getLatitude());
        order.setEndLong(coord.getLongitude());
        orderRepository.save(order);
    }

    public String getEstimateFare(User user) {
        Orderr order = orderRepository.findByUserChatId(user.getChatId());

        EstimateRequest request = new EstimateRequest();
        request.setStart_latitude(order.getStartLat());
        request.setStart_longitude(order.getStartLong());
        request.setEnd_latitude(order.getEndLat());
        request.setEnd_longitude(order.getEndLong());
        request.setProduct_id("99863383-8421-42e1-bd56-ffcb585099de");

        EstimateResponse estimateResponse = uberService.getEstimateResponse(user, request);
        saveFareId(order, estimateResponse.getFare().getFare_id());
        return messageService.getEstimateRide(estimateResponse);
    }


    public Orderr getOrderByChatId(long chatId) {
        return orderRepository.findByUserChatId(chatId);
    }

    public Optional<Orderr> getOrderOptionalByChatId(long chatId) {     // Return optional
        Optional<Orderr> optional = Optional.empty();
        Orderr foundOrder = orderRepository.findByUserChatId(chatId);
        if (foundOrder != null) {
            optional = Optional.of(foundOrder);
        }
        return optional;
    }

    public void saveFareId(Orderr order, String fareId) {
        order.setFare_id(fareId);
        orderRepository.save(order);
    }

    public void updateStartPoint(Orderr order, Coordinates coord) {
        order.setStartLat(coord.getLatitude());
        order.setStartLong(coord.getLongitude());
        orderRepository.save(order);
    }

}
