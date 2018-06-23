package pack.service.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UberRideRepository;
import pack.entity.Order;
import pack.entity.UberRide;
import pack.entity.User;

import java.util.Optional;

@Service
public class UberRideDaoService {
    @Autowired
    private UberRideRepository uberRideRepository;

    public Optional<UberRide> getByUserChatId(long chatId) {
        return uberRideRepository.findByOrderUserChatId(chatId);
    }

    public Optional<UberRide> getByRequestId(String requestId) {
        return uberRideRepository.findByRequest(requestId);
    }

    public void save(UberRide uberRide) {
        uberRideRepository.save(uberRide);
    }

    public Optional<UberRide> getByOrder(Order order) {
        return uberRideRepository.findByOrder(order);
    }

    public void removeByUser(User user) {
        uberRideRepository.removeByOrderUser(user);
    }


}
