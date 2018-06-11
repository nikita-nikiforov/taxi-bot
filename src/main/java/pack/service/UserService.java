//package pack.service;
//
//import com.botscrew.messengercdk.model.MessengerUser;
//import com.botscrew.messengercdk.service.UserProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import pack.dao.UserRepository;
//import pack.entity.User;
//
//@Service
//public class UserService implements UserProvider {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Override
//    public MessengerUser getByChatIdAndPageId(Long chatId, Long pageId) {
//        return userRepository.getOne(chatId);
//    }
//
//    public void save(MessengerUser user) {
//        User userToSave = (User) user;
//        userRepository.save(userToSave);
//    }
//}