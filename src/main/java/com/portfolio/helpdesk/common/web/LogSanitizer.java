package com.portfolio.helpdesk.common.web;

public final class LogSanitizer {
    private LogSanitizer() {}

    public static String maskEmail(String email) {
        if (email == null) return null;
        int at = email.indexOf('@');
        if (at <= 1) return "***" + (at >= 0 ? email.substring(at) : "");
        return email.charAt(0) + "***" + email.substring(at);   // a***@example.com
    }
}