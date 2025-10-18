package com.yugungsetia.ecommerce_simple.common.errors;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message) {
        super(message);
    }
}
