package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.AuthRequest;
import com.yugungsetia.ecommerce_simple.model.UserInfo;

public interface AuthService {
    UserInfo authenticate(AuthRequest authRequest);
}
