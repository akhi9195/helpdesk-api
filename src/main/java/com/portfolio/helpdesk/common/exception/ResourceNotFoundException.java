package com.portfolio.helpdesk.common.exception;

public class ResourceNotFoundException extends DomainException{
    public ResourceNotFoundException(String resource, Object id) {
        super(ErrorCode.RESOURCE_NOT_FOUND, resource + " " + id + " not found");
    }
}
