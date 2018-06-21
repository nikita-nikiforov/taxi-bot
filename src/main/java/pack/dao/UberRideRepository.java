package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;
import pack.entity.UberRide;

import java.util.Optional;

public interface UberRideRepository extends JpaRepository<UberRide, Integer> {
    UberRide findByOrderUserChatId(long chatId);

    Optional<UberRide> findByOrder(Order order);

}
