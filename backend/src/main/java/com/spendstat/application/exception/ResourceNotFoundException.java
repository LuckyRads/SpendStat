package com.spendstat.application.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, UUID id) {
        super(resourceType + " not found: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
