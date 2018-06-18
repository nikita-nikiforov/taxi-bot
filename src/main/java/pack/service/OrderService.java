package pack.service;

import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.OrderRepository;
import pack.entity.Orderr;
import pack.entity.User;

import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    GeocodingService geocodingService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserService userService;

    public Optional<LatLng> handleAddress(String address) {
        Optional<LatLng> coordinates = geocodingService.getAddress(address);

        return coordinates;
    }

    public void createOrder(User user, LatLng latLng) {

        Orderr order = new Orderr();
        order.setStartLat(latLng.lat);
        order.setStartLong(latLng.lng);
        order.setUser(user);
        orderRepository.save(order);
    }

    public void addEndPoint(User user, LatLng latLng) {
//        Optional<User> daoUser = userService.getOptionalByChatId(user.getChatId());
        Orderr order = orderRepository.findByUserChatId(user.getChatId());
        order.setEndLat(latLng.lat);
        order.setEndLong(latLng.lng);
        orderRepository.save(order);
    }

    public Orderr getOrderByChatId(long chatId) {
        return orderRepository.findByUserChatId(chatId);
    }
}
