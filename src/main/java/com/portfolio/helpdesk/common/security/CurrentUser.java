package com.portfolio.helpdesk.common.security;

import java.util.Objects;

public record CurrentUser(Long id, Role role) {

    public CurrentUser {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(role, "role");
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /** SUPPORT or ADMIN: may see every ticket. */
    public boolean isStaff() {
        return role == Role.SUPPORT || role == Role.ADMIN;
    }
}