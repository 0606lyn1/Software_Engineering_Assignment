package com.example.tiyu.security;

import java.util.Set;

public final class RoleNames {
    public static final String ADMIN = "ADMIN";
    public static final String STAFF = "STAFF";
    public static final String TEACHER = "TEACHER";
    public static final String STUDENT = "STUDENT";
    public static final String USER = "USER";

    private static final Set<String> VALID_ROLES = Set.of(ADMIN, STAFF, TEACHER, STUDENT, USER);

    private RoleNames() {
    }

    public static String normalize(String role) {
        if (role == null || role.isBlank()) {
            return STUDENT;
        }
        String normalized = role.trim().toUpperCase();
        if (!VALID_ROLES.contains(normalized)) {
            return STUDENT;
        }
        return USER.equals(normalized) ? STUDENT : normalized;
    }
}
