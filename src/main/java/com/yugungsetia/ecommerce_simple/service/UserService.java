package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.UserRegisterRequest;
import com.yugungsetia.ecommerce_simple.model.UserResponse;
import com.yugungsetia.ecommerce_simple.model.UserUpdateRequest;

public interface UserService {
    UserResponse register(UserRegisterRequest registerRequest);

    UserResponse findById(Long id);

    UserResponse findByKeyword(String keyword);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
