package smart.management.security

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.springframework.beans.factory.annotation.Autowired
import smart.management.security.utils.JWT
import smart.management.user.User
import smart.management.user.UserService

class SmartRealm extends AuthorizingRealm {

    @Autowired JWT jwt
    @Autowired UserService userService

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws Exception {

        String jwtToken = (String) authenticationToken?.getCredentials()
        String username = jwt?.getUsername(jwtToken)

        if (username == null) {
            throw new AuthenticationException('Token cannot be parse')
        }

        User user = userService.findByUsername(username)

        if (user == null) {
            throw new AuthenticationException('User cannot found')
        }

        if (! jwt.verify(jwtToken, ['id': user?.id, 'username': user?.username, 'hsKey': user?.hsKey, 'hsPassword': user?.hsPassword])) {
            throw new AuthenticationException('Authentication Failed')
        }

        return new SimpleAuthenticationInfo(jwtToken, jwtToken, this.getName())
    }

    @Override
    boolean supports(AuthenticationToken token) {
        return token instanceof Token
    }

    @Override
    String getName() {
        return 'SMART-REALM'
    }

}