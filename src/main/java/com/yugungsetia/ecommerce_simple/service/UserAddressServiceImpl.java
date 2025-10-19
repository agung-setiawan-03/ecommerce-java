package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.ForbiddenAccessException;
import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import com.yugungsetia.ecommerce_simple.entity.UserAddress;
import com.yugungsetia.ecommerce_simple.model.UserAddressRequest;
import com.yugungsetia.ecommerce_simple.model.UserAddressResponse;
import com.yugungsetia.ecommerce_simple.repository.UserAddressRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    @Override
    @Transactional
    public UserAddressResponse create(Long userId, UserAddressRequest request) {
        UserAddress newAddress = UserAddress
                .builder()
                .userId(userId)
                .addressName(request.getAddressName())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault()) {
            Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(userId);
            existingDefault.ifPresent(userAddress -> {
                newAddress.setIsDefault(false);
                userAddressRepository.save(userAddress);
            });
        }

        UserAddress savedAddress = userAddressRepository.save(newAddress);
        return UserAddressResponse.fromUserAddress(savedAddress);
    }

    @Override
    public List<UserAddressResponse> findByUserId(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream()
                .map(UserAddressResponse::fromUserAddress)
                .toList();
    }

    @Override
    public UserAddressResponse findById(Long id) {
        UserAddress userAddress = userAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat dengan id " + id + " tidak ditemukan"));

        return UserAddressResponse.fromUserAddress(userAddress);
    }

    @Override
    @Transactional
    public UserAddressResponse update(Long addressId, UserAddressRequest request) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat dengan id " + addressId + " tidak ditemukan"));


        UserAddress updatedAddress = UserAddress
                .builder()
                .userAddressId(existingAddress.getUserAddressId())
                .addressName(request.getAddressName())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault() && !existingAddress.getIsDefault()) {
            Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(existingAddress.getUserAddressId());
            existingDefault.ifPresent(address -> {
                address.setIsDefault(false);
                userAddressRepository.save(address);
            });
        }

        UserAddress savedAddress = userAddressRepository.save(updatedAddress);

        return UserAddressResponse.fromUserAddress(savedAddress);
    }

    @Override
    public void delete(Long addressId) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat dengan id " + addressId + " tidak ditemukan"));


        userAddressRepository.delete(existingAddress);

        if (existingAddress.getIsDefault()) {
            List<UserAddress> remainingAddresses = userAddressRepository.findByUserId(existingAddress.getUserId());
            if (!remainingAddresses.isEmpty()) {
                UserAddress newDefaultAddress = remainingAddresses.getFirst();
                newDefaultAddress.setIsDefault(true);
                userAddressRepository.save(newDefaultAddress);
            }
        }
    }

    @Override
    public UserAddressResponse setDefaultAddress(Long userId, Long addressId) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Alamat dengan id " + addressId + " tidak ditemukan"));

        if (!existingAddress.getUserId().equals(userId)) {
            throw new ForbiddenAccessException("user id " + userId + " tidak dapat memperbaharui Alamat");
        }


        Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(existingAddress.getUserAddressId());
        existingDefault.ifPresent(address -> {
            address.setIsDefault(false);
            userAddressRepository.save(address);
        });
        existingAddress.setIsDefault(true);
        UserAddress userAddress =  userAddressRepository.save(existingAddress);

        return UserAddressResponse.fromUserAddress(userAddress);
    }
}
