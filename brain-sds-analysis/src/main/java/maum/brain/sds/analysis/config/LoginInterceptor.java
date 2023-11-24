package maum.brain.sds.analysis.config;

import maum.brain.sds.analysis.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String url = request.getRequestURL().toString();

        if (url.startsWith("http://") && url.indexOf("localhost") < 0) {
            url = url.replaceAll("http://", "https://");
        }

        if(ObjectUtils.isEmpty(user)) {
            if (!url.contains("/resources") && !url.contains("/auth")
                    && !url.contains("/favicon.ico") && !url.contains("/error")) {
                if (url.substring(url.length() - 1).equals("/")) {
                    url += "auth/redtie";
                } else {
                    url += "/auth/redtie";
                }
                response.sendRedirect(url);
                return false;
            }
        }

        session.setMaxInactiveInterval(30*60);
        return true;
    }
}
