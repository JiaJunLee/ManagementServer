package smart.management.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Service
import smart.management.common.BaseService
import smart.management.user.User.UserType
import smart.management.user_information.UserInformation
import smart.management.user_information.UserInformationRepository

@Service
class UserService extends BaseService<User, String> {

    class UserAlreadyExistsException extends Exception {}

    @Autowired UserRepository userRepository
    @Autowired UserInformationRepository userInformationRepository

    @Override
    PagingAndSortingRepository<User, String> getRepository() {
        return this.userRepository
    }

    User createUser(User user) {
        if (userRepository.findByUsername(user.username) == null) {
            userRepository.save(user)
            userInformationRepository.save(new UserInformation(userId: user.id))
        } else {
            throw new UserAlreadyExistsException()
        }
        return user
    }

    List<User> findAllByType(UserType type) {
        return userRepository.findAllByType(type)
    }

    User findByUsername(String username) {
        return userRepository.findByUsername(username)
    }

}
