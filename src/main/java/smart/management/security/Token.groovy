package smart.management.security

import org.apache.shiro.authc.AuthenticationToken

class Token implements AuthenticationToken {

    String token

    @Override
    Object getPrincipal() {
        return this.token
    }

    @Override
    Object getCredentials() {
        return this.token
    }

}