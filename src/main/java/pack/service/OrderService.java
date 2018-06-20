package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.OrderRepository;
import pack.dao.OrderUberInfoRepository;
import pack.entity.Order;
import pack.entity.OrderUberInfo;
import pack.entity.User;
import pack.model.EstimateRequest;
import pack.model.EstimateResponse;
import pack.model.ProductItem;
import pack.model.TripResponse;

import java.util.List;
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
    UberOrderService uberOrderService;

    @Autowired
    OrderUberInfoRepository orderUberInfoRepository;

    @Autowired
    MessageService messageService;

    public Optional<Coordinates> handleAddress(String address) {
        return geocodingService.getCoordinatesFromAddress(address);
    }

    public void setStartPoint(User user, Coordinates coord) {
        Optional<Order> foundOrder = getOrderOptionalByChatId(user.getChatId());
        Order result = foundOrder.orElseGet(() -> new Order(user));
        result.setStartLat(coord.getLatitude());
        result.setStartLong(coord.getLongitude());
        orderRepository.save(result);
    }


    public void setEndPoint(User user, Coordinates coord) {
        Order order = orderRepository.findByUserChatId(user.getChatId());
        order.setEndLat(coord.getLatitude());
        order.setEndLong(coord.getLongitude());
        orderRepository.save(order);
    }

    public String getEstimateFare(User user) {
        Order order = orderRepository.findByUserChatId(user.getChatId());

        EstimateRequest request = new EstimateRequest();
        Coordinates coord = new Coordinates();          // Coords to get products nearby
        coord.setLatitude(order.getStartLat());
        coord.setLongitude(order.getStartLong());

        // It's okay, because on START_INPUT we checked whether products are present
        List<ProductItem> productsNearBy = uberOrderService.getProductsNearBy(user, coord);
        ProductItem productItem = productsNearBy.get(0);

        request.setStart_latitude(order.getStartLat());
        request.setStart_longitude(order.getStartLong());
        request.setEnd_latitude(order.getEndLat());
        request.setEnd_longitude(order.getEndLong());
        request.setProduct_id(productItem.getProduct_id());

        EstimateResponse estimateResponse = uberService.getEstimateResponse(user, request);
        saveFareAndProductId(order, estimateResponse.getFare().getFare_id(), productItem.getProduct_id());
        return messageService.getEstimateRide(estimateResponse);
    }


    public Order getOrderByChatId(long chatId) {
        return orderRepository.findByUserChatId(chatId);
    }

    public Optional<Order> getOrderOptionalByChatId(long chatId) {     // Return optional
        Optional<Order> optional = Optional.empty();
        Order foundOrder = orderRepository.findByUserChatId(chatId);
        if (foundOrder != null) {
            optional = Optional.of(foundOrder);
        }
        return optional;
    }

    public void saveFareAndProductId(Order order, String fareId, String productId) {
        OrderUberInfo orderUberInfo = new OrderUberInfo(order, fareId, productId);
        orderUberInfoRepository.save(orderUberInfo);
    }

    public Coordinates getStartPointCoordinates(User user) {
        Order order = getOrderByChatId(user.getChatId());
        Coordinates coord = new Coordinates();
        coord.setLatitude(order.getStartLat());
        coord.setLongitude(order.getStartLong());
        return coord;
    }

    public Coordinates getEndPointCoordinates(User user) {
        Order order = getOrderByChatId(user.getChatId());
        Coordinates coord = new Coordinates();
        coord.setLatitude(order.getEndLat());
        coord.setLongitude(order.getEndLong());
        return coord;
    }

    public void removeByUser(User user) {
        Order order = getOrderByChatId(user.getChatId());
        orderRepository.delete(order);
    }

    // TODO
    public void stopTrip(User user) {
        removeByUser(user);
    }

    public boolean confirmRide(User user) {
        TripResponse tripResponse = uberService.getNewTripResponse(user);
        OrderUberInfo orderUberInfo = orderUberInfoRepository.findByOrderUserChatId(user.getChatId());
        orderUberInfo.setRequest_id(tripResponse.getRequest_id());
        orderUberInfoRepository.save(orderUberInfo);
        return true;
    }
}
