package com.healthcare.api.utils.security;

import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Get email of the currently logged-in user
     * @return email (String)
     * @throws AppException if user is not authenticated
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return authentication.getName();
    }
}
