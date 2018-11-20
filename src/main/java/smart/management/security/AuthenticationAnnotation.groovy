package smart.management.security

import smart.management.user.User

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ ElementType.METHOD ])
@Retention(RetentionPolicy.RUNTIME)
@Documented
@interface AuthenticationAnnotation {

    User.UserType type() default User.UserType.ADMINISTRATOR

}
