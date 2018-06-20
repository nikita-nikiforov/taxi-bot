package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;
import pack.entity.UberTrip;

public interface UberTripRepository extends JpaRepository<UberTrip, Integer> {
    UberTrip findByOrderUserChatId(long chatId);

    UberTrip findByOrder(Order order);

}
