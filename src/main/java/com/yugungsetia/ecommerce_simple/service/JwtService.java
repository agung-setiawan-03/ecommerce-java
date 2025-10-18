package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.UserInfo;

public interface JwtService {

    String generateToken(UserInfo userInfo);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);
}
