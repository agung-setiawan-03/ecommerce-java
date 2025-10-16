package com.yugungsetia.ecommerce_simple.common.errors;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

}
