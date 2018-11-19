package smart.management.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smart.management.common.ServerResponse
import smart.management.security.AuthenticationException
import smart.management.security.utils.HMAC
import smart.management.security.utils.JWT
import smart.management.user_group.UserGroup
import smart.management.user_group.UserGroupService

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping('/user')
class UserController {

    @Autowired UserService userService
    @Autowired UserGroupService userGroupService
    @Autowired JWT jwt

    @RequestMapping("/register")
    ServerResponse create(String username, String password) {
        String hsKey = HMAC.generateKey(HMAC.HMAC_SHA512)
        User user = new User(
                username: username,
                hsKey: hsKey,
                hsPassword: HMAC.digest(password, hsKey, HMAC.HMAC_SHA512),
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
    ServerResponse joinUserGroup(String userId, String userGroupId) {
        User user = userService.findById(userId)
        UserGroup userGroup = userGroupService.findById(userGroupId)
        user.addUserGroup(userGroup.id)
        userService.save(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, 'userGroupIds': user.userGroupIds.join(',')], message: 'join successful')
    }

    @ExceptionHandler(User.UserGroupAlreadyJoined.class)
    ServerResponse handleUserGroupAlreadyJoined() {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.REJECTED, message: 'user already joined this group!')
    }

    @RequestMapping('/change_password')
    ServerResponse changePassword(String userId, String password) {
        String hsKey = HMAC.generateKey(HMAC.HMAC_SHA512)
        User user = userService.findById(userId)
        user.setHsKey(hsKey)
        user.setHsPassword(HMAC.digest(password, hsKey, HMAC.HMAC_SHA512))
        userService.save(user)
        return new ServerResponse(content: ['id': user.id, 'username': user.username, type: user.type], message: 'change successful')
    }

    @RequestMapping('/login')
    ServerResponse authentication (String username, String password, HttpServletResponse httpServletResponse) {
        User user = userService.findByUsername(username)
        if (user == null) {
            throw new AuthenticationException('User cannot found')
        }
        if (!HMAC.validate(password, user?.hsKey, HMAC.HMAC_SHA512, user?.hsPassword)) {
            throw new AuthenticationException('Authentication failed')
        }

        String jwtToken = jwt?.sign(['id': user?.id, 'username': user?.username, 'hsKey': user?.hsKey, 'hsPassword': user?.hsPassword])
        Cookie cookie = new Cookie('token', jwtToken)
        cookie.setHttpOnly(true)
        cookie.setDomain('localhost')
        cookie.setPath('/')
        cookie.setMaxAge(JWT.TOKEN_EXPIRE_TIME)
        httpServletResponse.addCookie(cookie)

        return new ServerResponse(content: ['id': user.id, 'username': user.username, 'type': user.type], message: 'login successful')
    }

    @RequestMapping('/denied')
    ServerResponse denied() {
        return new ServerResponse(message: 'Access denied', resultCode: ServerResponse.ServerResponseCode.REJECTED)
    }

}
