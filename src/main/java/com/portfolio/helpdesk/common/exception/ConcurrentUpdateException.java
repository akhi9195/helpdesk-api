package com.portfolio.helpdesk.common.exception;

public class ConcurrentUpdateException extends DomainException{
    public ConcurrentUpdateException(String resource, Object id) {
        super(ErrorCode.CONCURRENT_MODIFICATION,
                resource + " " + id + " was modified by another request; reload and try again");
    }
}
