package pack.service;

import com.botscrew.messengercdk.model.MessengerUser;
import com.botscrew.messengercdk.service.UserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.constant.State;
import pack.dao.UserRepository;
import pack.entity.UberRide;
import pack.entity.User;

import java.util.Optional;

@Service
public class UserService implements UserProvider {

    @Autowired
    private UserRepository userRepository;

    @Override
    public MessengerUser getByChatIdAndPageId(Long chatId, Long pageId) {
        Optional<User> result = getOptionalByChatId(chatId);
        // If present, return user. Otherwise, create new one
        return result.orElseGet(() -> new User(chatId, State.INITIAL));
    }

    public Optional<User> getOptionalByChatId(long chatId) {
        return userRepository.findByChatId(chatId);
    }

    public User getUserByChatId(long chatId) {
            return userRepository.findByChatId(chatId).get();
    }

    public void save(long chatId, String state) {
        Optional<User> result = getOptionalByChatId(chatId);
        result.ifPresent(user -> user.setState(state));             // If present, set new state
        userRepository.save(result.orElse(new User(chatId, state))); // If absent, create new User
    }

    public void save(User user, String state) {
        save(user.getChatId(), state);
    }

    public User getByUuid(String uuid) {
        return userRepository.findByCredentialUuid(uuid);
    }

    public User getByUberRide(UberRide uberRide) {
        return userRepository.findByOrderUberRide(uberRide);
    }
}