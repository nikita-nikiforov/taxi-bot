package pack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberTripRepository;
import pack.entity.Order;
import pack.entity.UberTrip;

@Service
public class UberTripService {

    @Autowired
    private UberTripRepository uberTripRepository;

    public UberTrip getUberTripByUserChatId(long chatId) {
        return uberTripRepository.findByOrderUserChatId(chatId);
    }

    public void save(UberTrip uberTrip) {
        uberTripRepository.save(uberTrip);
    }

    public UberTrip getByOrder(Order order) {
        return uberTripRepository.findByOrder(order);
    }

    public void saveFareAndProductId(Order order, String fareId, String productId) {
        UberTrip uberTrip = new UberTrip(order, fareId, productId, "undefined");
        uberTripRepository.save(uberTrip);
    }
}
