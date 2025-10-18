package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.*;
import com.yugungsetia.ecommerce_simple.entity.Role;
import com.yugungsetia.ecommerce_simple.entity.User;
import com.yugungsetia.ecommerce_simple.entity.UserRole;
import com.yugungsetia.ecommerce_simple.model.UserRegisterRequest;
import com.yugungsetia.ecommerce_simple.model.UserResponse;
import com.yugungsetia.ecommerce_simple.model.UserUpdateRequest;
import com.yugungsetia.ecommerce_simple.repository.RoleRepository;
import com.yugungsetia.ecommerce_simple.repository.UserRepository;
import com.yugungsetia.ecommerce_simple.repository.UserRoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponse register(UserRegisterRequest registerRequest) {
        if (existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username sudah tersedia");
        }

        if (existsByEmail(registerRequest.getEmail())) {
            throw new UsernameAlreadyExistsException("Email  sudah tersedia");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirmation())) {
            throw new BadRequestException("Password dan Konfirmasi tidak sama");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .enabled(true)
                .password(encodedPassword)
                .build();

        userRepository.save(user);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Default Role tidak ditemukan"));

        UserRole userRoleRelation = UserRole.builder()
                .id(new UserRole.UserRoleId(user.getUserId(), userRole.getRoleId()))
                .build();

        userRoleRepository.save(userRoleRelation);

        return UserResponse.fromUserAndRoles(user, List.of(userRole));
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User tidak ditemukan dengan id " + id));

        List<Role> roles = roleRepository.findByUserId(id);

        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public UserResponse findByKeyword(String keyword) {
        User user = userRepository.findByKeyword(keyword)
                .orElseThrow(() -> new UserNotFoundException("User tidak ditemukan dengan username / email " + keyword));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());

        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User tidak ditemukan dengan id " + id));

        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Password salah");
            }

            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            user.setPassword(encodedPassword);
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username " + request.getUsername() + " sudah tersedia");
            }

            user.setUsername(request.getUsername());
        }


        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new UsernameAlreadyExistsException("Email " + request.getEmail() + " sudah tersedia");
            }

            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User tidak ditemukan dengan id " + id));

        userRoleRepository.deleteByIdUserId(id);

        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
       return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
