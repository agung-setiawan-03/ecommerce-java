package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.entity.Role;
import com.yugungsetia.ecommerce_simple.entity.User;
import com.yugungsetia.ecommerce_simple.model.UserInfo;
import com.yugungsetia.ecommerce_simple.repository.RoleRepository;
import com.yugungsetia.ecommerce_simple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByKeyword(username)
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan dengan username " + username));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());

        return UserInfo.builder()
                .roles(roles)
                .user(user)
                .build();

    }
}
