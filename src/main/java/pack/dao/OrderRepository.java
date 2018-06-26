package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{

    Order findByUserChatId(long id);
}
