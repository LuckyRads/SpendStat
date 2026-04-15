package com.spendstat.infrastructure.security;

import com.spendstat.domain.shared.UserId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Returns the UserId of the currently authenticated user.
     * The principal name is set to the user's UUID string by {@link JwtAuthenticationFilter}.
     */
    public static UserId getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        return UserId.of(UUID.fromString(auth.getName()));
    }
}
