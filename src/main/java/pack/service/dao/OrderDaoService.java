package pack.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.OrderRepository;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import java.util.Optional;

@Service
public class OrderDaoService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UberRideRepository uberRideRepository;

    public Order getOrderByChatId(long chatId) {
        return orderRepository.findByUserChatId(chatId);
    }

    // Remove Order and its UberOrder by User
    public void removeByUser(User user) {
        Order order = getOrderByChatId(user.getChatId());
        Optional<UberRide> uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId());
        uberRide.ifPresent(u -> uberRideRepository.delete(u));
        orderRepository.delete(order);
    }

    // Return Optional<Order> by chatId
    public Optional<Order> getOrderOptionalByChatId(long chatId) {
        Optional<Order> optional = Optional.empty();
        Order foundOrder = orderRepository.findByUserChatId(chatId);
        if (foundOrder != null) {
            optional = Optional.of(foundOrder);
        }
        return optional;
    }
}