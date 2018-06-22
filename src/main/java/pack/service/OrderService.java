package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.RideStatus;
import pack.dao.OrderRepository;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.factory.CoordinatesFactory;
import pack.model.FareRequest;
import pack.model.FareResponse;
import pack.model.ProductItem;
import pack.service.api.GeocodingService;
import pack.service.api.UberApiService;

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
    UberApiService uberApiService;

    @Autowired
    UberRideRepository uberRideRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    private UberRideService uberRideService;

    // Set start point. If Order is new, create it
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

    public Optional<FareResponse> getEstimateFare(User user) {
        Order order = orderRepository.findByUserChatId(user.getChatId());
        // Coords to get products nearby
        Coordinates coord = CoordinatesFactory.create(order.getStartLat(), order.getStartLong());

        // It's okay, because on START_INPUT we checked whether products are present
        List<ProductItem> productsNearBy = uberRideService.getProductsNearBy(user, coord);
        // Sort products and obtain the cheapiest one
        Optional<ProductItem> cheapProduct = productsNearBy.stream().min((p1, p2) -> {
            int price1 = Integer.valueOf(p1.getPrice_details().getMinimum());
            int price2 = Integer.valueOf(p2.getPrice_details().getMinimum());
            return price1 - price2;
        });
        ProductItem product = cheapProduct.get();

        // JSON body of request
        FareRequest jsonBody = new FareRequest(product.getProduct_id(), order.getStartLat(),
                order.getStartLong(), order.getEndLat(), order.getEndLong());
        // Make request and get response
        Optional<FareResponse> estimateResponse = uberApiService.getEstimateResponse(user, jsonBody);

        // If success, create new UberRide and save fareId and productId
        estimateResponse.ifPresent(e -> {
            UberRide uberRide = uberRideService.getByOrder(order).orElseGet(
                    () -> new UberRide(order, e.getFare().getFare_id(),
                            product.getProduct_id(), RideStatus.CREATED.getName()));
            uberRideService.save(uberRide);
        });
        return estimateResponse;
    }

    public Order getOrderByChatId(long chatId) {
        return orderRepository.findByUserChatId(chatId);
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

    public Coordinates getStartPointCoordinates(User user) {
        Order order = getOrderByChatId(user.getChatId());
        return CoordinatesFactory.create(order.getStartLat(), order.getStartLong());
    }

    public Coordinates getEndPointCoordinates(User user) {
        Order order = getOrderByChatId(user.getChatId());
        return CoordinatesFactory.create(order.getEndLat(), order.getEndLong());
    }

    public void removeByUser(User user) {
        Order order = getOrderByChatId(user.getChatId());
        UberRide uberRide = uberRideRepository.findByOrderUserChatId(user.getChatId()).get();
        uberRideRepository.delete(uberRide);
        orderRepository.delete(order);
    }

    // TODO
    public void stopTrip(User user) {
        removeByUser(user);
    }
}
