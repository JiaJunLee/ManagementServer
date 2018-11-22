package smart.management.security

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import smart.management.security.utils.HMAC
import smart.management.user.User
import smart.management.user.UserService

@Component
@Slf4j
class AdminUserAutoCreation implements CommandLineRunner {

    private static final String username = 'admin'
    private static final String password = '123456'

    @Autowired UserService userService

    @Override
    void run(String... args) throws Exception {
        List<User> users = userService.findAllByType(User.UserType.ADMINISTRATOR)
        if (users == null || users.isEmpty()) {
            String hsKey = HMAC.generateKey(HMAC.HMAC_SHA512)
            User user = new User(
                    username: username,
                    hsKey: hsKey,
                    hsPassword: HMAC.digest(password, hsKey, HMAC.HMAC_SHA512),
                    type: User.UserType.ADMINISTRATOR
            )
            userService.createUser(user)
            log.warn('Administrator user create successful, default username: admin, password: 123456, please login and changed password for your security!')
        }
    }

}
