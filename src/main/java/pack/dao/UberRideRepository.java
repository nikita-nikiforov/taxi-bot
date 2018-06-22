package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;
import pack.entity.UberRide;

import java.util.Optional;

public interface UberRideRepository extends JpaRepository<UberRide, Integer> {
    Optional<UberRide> findByOrderUserChatId(long chatId);

    Optional<UberRide> findByRequest(String requestId);

    Optional<UberRide> findByOrder(Order order);

}
