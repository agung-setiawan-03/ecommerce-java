package com.yugungsetia.ecommerce_simple.common.errors;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
