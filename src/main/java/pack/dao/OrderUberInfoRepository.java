package pack.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pack.entity.OrderUberInfo;

public interface OrderUberInfoRepository extends JpaRepository<OrderUberInfo, Integer> {
    OrderUberInfo findByOrderUserChatId(long chatId);

}
