package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;
import pack.entity.UberTrip;

import java.util.Optional;

public interface UberTripRepository extends JpaRepository<UberTrip, Integer> {
    UberTrip findByOrderUserChatId(long chatId);

    Optional<UberTrip> findByOrder(Order order);

}
