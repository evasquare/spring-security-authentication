package com.evasquare.username_password_auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.evasquare.username_password_auth.entity.UserEntity;
import com.evasquare.username_password_auth.models.JoinModel;
import com.evasquare.username_password_auth.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class JoinService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinModel joinModel) {
        boolean isUser = userRepository.existsByUsername(joinModel.getUsername());
        if (isUser) {
            return;
        }

        UserEntity data = new UserEntity();

        data.setUsername(joinModel.getUsername());
        data.setPassword(bCryptPasswordEncoder.encode(joinModel.getPassword()));
        data.setRole("ROLE_USER");

        userRepository.save(data);
    }
}
