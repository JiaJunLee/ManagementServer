package smart.management.user

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationAnnotation
import smart.management.security.AuthenticationException
import smart.management.security.utils.HMAC
import smart.management.security.utils.JWT
import smart.management.user_group.UserGroup
import smart.management.user_group.UserGroupService
import smart.management.user_information.UserInformationService

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@Slf4j
@RestController
@RequestMapping('/user')
class UserController {

    private static final List<String> DOMAINS = [
            "129.204.16.191",
            "rongejinfu.cn",
            "www.rongejinfu.cn"
    ]

    @Autowired UserService userService
    @Autowired UserGroupService userGroupService
    @Autowired UserInformationService userInformationService
    @Autowired JWT jwt

    static final String DEFAULT_PASSWORD = '123456'

    @PostMapping("/register")
    @AuthenticationAnnotation
    ServerResponse register(String username) {
        String hsKey = HMAC.generateKey(HMAC.HMAC_SHA512)
        User user = new User(
                username: username,
                hsKey: hsKey,
                hsPassword: HMAC.digest(DEFAULT_PASSWORD, hsKey, HMAC.HMAC_SHA512),
                type: User.UserType.EMPLOYEE
        )
        userService.createUser(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, type: user.type], message: 'create successful')
    }

    @ExceptionHandler(UserService.UserAlreadyExistsException.class)
    ServerResponse handleUserAlreadyExistsException() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'user already exists!')
    }

    @RequestMapping('/join_user_group')
    @AuthenticationAnnotation
    ServerResponse joinUserGroup(String userId, String userGroupId) {
        User user = userService.findById(userId)
        UserGroup userGroup = userGroupService.findById(userGroupId)
        user.addUserGroup(userGroup.id)
        userService.save(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, 'userGroupIds': user.userGroupIds.join(',')], message: 'join successful')
    }

    @RequestMapping('/join_users_group')
    @AuthenticationAnnotation
    ServerResponse joinUsersGroup(String[] userIds, String userGroupId) {
        userIds?.each { userId ->
            User user = userService.findById(userId)
            user.addUserGroup(userGroupId)
            userService.save(user)
        }
        return new ServerResponse(message: 'join users group successful')
    }

    @RequestMapping('/exit_user_group')
    @AuthenticationAnnotation
    ServerResponse exitUserGroup(String userId, String userGroupId) {
        User user = userService.findById(userId)
        user.removeUserGroup(userGroupId)
        userService.save(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, 'userGroupIds': user.userGroupIds.join(',')], message: 'exit successful')
    }

    @ExceptionHandler(User.UserGroupAlreadyJoined.class)
    ServerResponse handleUserGroupAlreadyJoined() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'user already joined this group!')
    }

    @PostMapping('/change_password')
    ServerResponse changePassword(String userId, String password) {
        String hsKey = HMAC.generateKey(HMAC.HMAC_SHA512)
        User user = userService.findById(userId)
        user.setHsKey(hsKey)
        user.setHsPassword(HMAC.digest(password, hsKey, HMAC.HMAC_SHA512))
        userService.save(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, type: user.type], message: 'change successful')
    }

    @PostMapping('/login')
    ServerResponse authentication (String username, String password, HttpServletResponse httpServletResponse) {
        User user = userService.findByUsername(username)
        if (user == null) {
            throw new AuthenticationException('User cannot found')
        }
        if (!HMAC.validate(password, user?.hsKey, HMAC.HMAC_SHA512, user?.hsPassword)) {
            throw new AuthenticationException('Authentication failed')
        }

        String jwtToken = jwt?.sign(['id': user?.id, 'username': user?.username, 'hsKey': user?.hsKey, 'hsPassword': user?.hsPassword])

        DOMAINS?.each { currentDomain ->
            Cookie cookie = new Cookie('token', jwtToken)
            cookie.setHttpOnly(true)
            cookie.setDomain(currentDomain)
            cookie.setPath('/')
            cookie.setMaxAge(JWT.TOKEN_EXPIRE_TIME)
            httpServletResponse.addCookie(cookie)
        }

        log.info("user: ${user.id} / ${user.username} / ${user.type} login system")

        return new ServerResponse(content: ['id': user.id, 'username': user.username, 'type': user.type], message: 'login successful')
    }

    @RequestMapping('/denied')
    ServerResponse denied() {
        return new ServerResponse(message: 'Access denied', resultCode: ServerResponse.ServerResponseCode.REJECTED)
    }

    @RequestMapping('/employees')
    @AuthenticationAnnotation
    ServerResponse employees() {
        List<User> employees = userService.findAll().findAll { it.type == User.UserType.EMPLOYEE }
        employees?.each {
            it.hsPassword = null
            it.hsKey = null
            it.userInformation = userInformationService.findByUserId(it.id)
            it.userGroupIds.each { userGroupId ->
                UserGroup userGroup = userGroupService.findById(userGroupId)
                if (userGroup) {
                    it.userGroups.add(userGroup)
                }
            }
        }
        return new ServerResponse(content: employees, message: 'query successful')
    }

    @RequestMapping('/delete')
    @AuthenticationAnnotation
    ServerResponse delete(String userId) {
        userService.deleteById(userId)
        return new ServerResponse(message: 'delete successful')
    }

}
