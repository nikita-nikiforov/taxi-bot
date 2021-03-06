package pack.service;

import com.botscrew.messengercdk.model.incomming.Coordinates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.RideStatus;
import pack.dao.OrderRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;
import pack.factory.CoordinatesFactory;
import pack.model.FareRequest;
import pack.model.FareResponse;
import pack.model.ProductItem;
import pack.service.api.UberApiService;
import pack.service.dao.OrderDaoService;
import pack.service.dao.UberRideDaoService;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderDaoService orderDaoService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UberApiService uberApiService;

    @Autowired
    private UberRideService uberRideService;

    @Autowired
    private UberRideDaoService uberRideDaoService;

    // Set start point. If Order is new, create it
    public void setStartPoint(User user, Coordinates coord) {
        Optional<Order> foundOrder = orderDaoService.getOrderOptionalByChatId(user.getChatId());
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
        FareRequest jsonBody = new FareRequest(order, product);
        // Make request and get response
        Optional<FareResponse> estimateResponse = uberApiService.getEstimateResponse(user, jsonBody);

        estimateResponse.ifPresent(e -> {
            // Get Order from DB or create new
            UberRide uberRide = uberRideDaoService.getByOrder(order).orElseGet(
                    () -> new UberRide(order,
                            product.getProduct_id(), RideStatus.CREATED.getName()));
            uberRide.setFare_id(e.getFare().getFare_id());          // Update fare_id
            uberRideDaoService.save(uberRide);                         // Save UberRide
        });
        return estimateResponse;
    }

    public Coordinates getStartPointCoordinates(User user) {
        Order order = orderDaoService.getOrderByChatId(user.getChatId());
        return CoordinatesFactory.create(order.getStartLat(), order.getStartLong());
    }

    public Coordinates getEndPointCoordinates(User user) {
        Order order = orderDaoService.getOrderByChatId(user.getChatId());
        return CoordinatesFactory.create(order.getEndLat(), order.getEndLong());
    }
}
