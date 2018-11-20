package smart.management.security

import groovy.util.logging.Slf4j
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import smart.management.security.utils.JWT
import smart.management.user.User
import smart.management.user.UserService

import javax.servlet.http.HttpServletRequest
import java.lang.reflect.Method

@Aspect
@Configuration
@Slf4j
class AuthenticationAop {

    @Autowired JWT jwt
    @Autowired UserService userService

    @Around('execution(* smart.management.*.*Controller.*(..))')
    Object checkUserType(ProceedingJoinPoint joinPoint) {
        Method method = joinPoint.getSignature().getDeclaringType().getDeclaredMethods().find { it.name == joinPoint.getSignature().name }
        if (method?.isAnnotationPresent(AuthenticationAnnotation)) {
            AuthenticationAnnotation authenticationAnnotation = method.getAnnotation(AuthenticationAnnotation)
            RequestAttributes ra = RequestContextHolder.getRequestAttributes()
            ServletRequestAttributes sra = (ServletRequestAttributes) ra
            HttpServletRequest request = sra.getRequest()
            String jwtToken = request?.getCookies()?.find { it?.name == 'token' }?.value
            User user = userService.findByUsername(jwt.getUsername(jwtToken))
            if (!jwtToken || user?.type != authenticationAnnotation.type()) {
                throw new AuthenticationException('User cannot access')
            }
            log.info("user: ${user.id} / ${user.username} / ${user.type} access limitted function: ${method.toGenericString()}")
        }
        return joinPoint.proceed()
    }

}
