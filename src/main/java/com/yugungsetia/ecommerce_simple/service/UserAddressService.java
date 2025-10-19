package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.model.UserAddressRequest;
import com.yugungsetia.ecommerce_simple.model.UserAddressResponse;

import java.util.List;
import java.util.Optional;

public interface UserAddressService {

    UserAddressResponse create(Long userId, UserAddressRequest request);

    List<UserAddressResponse> findByUserId(Long userId);

    UserAddressResponse findById(Long id);

    UserAddressResponse update(Long addressId, UserAddressRequest request);

    void delete(Long addressId);

    UserAddressResponse setDefaultAddress(Long userId, Long addressId);
}
