package com.yugungsetia.ecommerce_simple.common.errors;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}
