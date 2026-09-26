package com.portfolio.helpdesk.common.exception;

public class InvalidRoleChangeException extends DomainException{
    public InvalidRoleChangeException(String message) {
        super(ErrorCode.INVALID_ROLE_CHANGE, message);
    }
}
