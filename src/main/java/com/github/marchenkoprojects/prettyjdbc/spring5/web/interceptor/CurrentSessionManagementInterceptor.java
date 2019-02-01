package com.github.marchenkoprojects.prettyjdbc.spring5.web.interceptor;

import com.github.marchenkoprojects.prettyjdbc.SessionFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This request interceptor performs the release of the current session if it was opened in the context of processing thread.
 *
 * <p>Many sessions can be opened during the request lifecycle; this has a negative effect on performance.
 * To solve this problem, usually open a session at the beginning of request and close it at the end.
 * In our case, the session opens lazily, only at the first access to the database in the current thread.
 *
 * @author Oleg Marchenko
 */

public class CurrentSessionManagementInterceptor implements HandlerInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
        SessionFactory.unbindSession();
    }
}
