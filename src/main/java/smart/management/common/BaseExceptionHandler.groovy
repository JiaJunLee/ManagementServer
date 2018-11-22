package smart.management.common

import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import smart.management.security.AuthenticationException

@ControllerAdvice
class BaseExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    ServerResponse handleException(Exception e) {
        return new ServerResponse(resultCode: ServerResponse.ServerResponseCode.ERROR, message: e.getCause().getMessage())
    }

}
