package smart.management.security

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationFilter extends BasicHttpAuthenticationFilter {

    private static final List<String> IGNORE_PATH = [
            '/user/login',
            '/user/denied'
    ]

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        if (IGNORE_PATH.contains(((HttpServletRequest)request).getServletPath())) {
            return true
        } else {
            getSubject(request, response).login(new Token(token: ((HttpServletRequest) request)?.getCookies()?.find { it?.name == 'token' }?.value))
        }
        return true
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            return executeLogin(request, response)
        } catch (Exception e) {
            ((HttpServletResponse) response).sendRedirect("/user/denied")
            return false
        }
    }

}