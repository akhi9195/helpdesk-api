package com.portfolio.helpdesk.common.exception;

public class OperationNotPermittedException extends DomainException{
    public OperationNotPermittedException(String message) {
        super(ErrorCode.ACCESS_DENIED, message);
    }
}
