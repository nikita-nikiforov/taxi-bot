package pack.service;

import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UserRepository;
import pack.entity.User;

@Service
public class UserService implements UserProvider {

    @Autowired
    UserRepository userRepository;

    @Override
    public MessengerUser getByChatIdAndPageId(Long chatId, Long pageId) {
        User user = userRepository.findByChatId(chatId);
        if (user != null) {
            return user;
        } else {
            return new User(chatId, "INITIAL");
        }
    }

    public User getByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void save(long chatId, String state) {

        User user = getByChatId(chatId);
        if (user != null) {
            user.setState(state);
        } else {
            user = new User(chatId, state);
        }
        userRepository.save(user);
    }
}