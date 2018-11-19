package smart.management.security.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.springframework.stereotype.Component
import java.security.Key

@Component
class JWT {

    /**
     * JWT Token Expire Time
     */
    public static final int TOKEN_EXPIRE_TIME = 60 * 60 * 2

    /**
     * JWT Key
     */
    private static Key key

    static {
        key = MacProvider.generateKey()
    }

    String sign (Map claims) {
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + this.TOKEN_EXPIRE_TIME))
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact()
    }

    boolean verify (String token, Map claims) {
        if (token == null || token?.trim()?.isEmpty())
            return false
        def body = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
        if (body?.getExpiration()?.before(new Date()))
            return false
        return body?.equals(claims)
    }

    String getUsername (String token) {
        if (token == null || token?.trim()?.isEmpty())
            return null
        def body = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
        if (body == null || body?.getExpiration()?.before(new Date())) {
            return null
        }
        return body['username']
    }

    Long getId (String token) {
        if (token == null || token?.trim()?.isEmpty())
            return null
        def body = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
        if (body == null || body?.getExpiration()?.before(new Date())) {
            return null
        }
        return Long.valueOf(body['id'].toString())
    }

}
