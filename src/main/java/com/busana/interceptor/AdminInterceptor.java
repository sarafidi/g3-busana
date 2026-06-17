package com.busana.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Check if admin session exists
        if (session == null || session.getAttribute("admin") == null) {
            // Redirect to admin login if not authenticated
            response.sendRedirect(request.getContextPath() + "/admin/login");
            return false;
        }
        
        return true;
    }
}
