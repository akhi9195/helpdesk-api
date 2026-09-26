package com.portfolio.helpdesk.common.exception;

public class InvalidStatusTransitionException extends DomainException{
    public InvalidStatusTransitionException(Object from, Object to) {
        super(ErrorCode.INVALID_STATUS_TRANSITION,
                "Cannot change ticket status from " + from + " to " + to);
    }
}
