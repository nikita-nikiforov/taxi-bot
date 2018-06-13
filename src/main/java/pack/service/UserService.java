package pack.service;

import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.dao.UserRepository;
import pack.entity.User;

import java.util.Optional;

@Service
public class UserService implements UserProvider {

    @Autowired
    UserRepository userRepository;

    @Override
    public MessengerUser getByChatIdAndPageId(Long chatId, Long pageId) {
        Optional<User> result = getByChatId(chatId);
        // If present, return user. Otherwise, create new one
        return result.orElseGet(() -> new User(chatId, "INITIAL"));
    }

    public Optional<User> getByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public void save(long chatId, String state) {
        Optional<User> result = getByChatId(chatId);
        result.ifPresent(user -> user.setState(state));             // If present, set new state
        userRepository.save(result.orElse(new User(chatId, state))); // If absent, create new User
    }

    public void save(MessengerUser user, String state) {
        save(user.getChatId(), state);
    }
}