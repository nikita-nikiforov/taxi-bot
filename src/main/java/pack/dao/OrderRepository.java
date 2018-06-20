package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer>{
    Order findByUserId(int id);

    Order findByUserChatId(long id);

    void deleteByUserChatId(long id);

    void deleteByUserId(int id);
}
