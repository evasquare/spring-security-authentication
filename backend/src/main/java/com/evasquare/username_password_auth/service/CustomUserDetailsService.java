package com.evasquare.username_password_auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.evasquare.username_password_auth.models.CustomUserDetails;
import com.evasquare.username_password_auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userDataOptional = userRepository.findByUsername(username);

        if (userDataOptional.isPresent()) {
            return new CustomUserDetails(userDataOptional.get());
        }

        return null;
    }
}
