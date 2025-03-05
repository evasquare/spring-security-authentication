package com.evasquare.username_password_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.evasquare.username_password_auth.entity.UserEntity;
import com.evasquare.username_password_auth.models.JoinModel;
import com.evasquare.username_password_auth.models.LoginModel;
import com.evasquare.username_password_auth.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginModel loginModel, HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginModel.getUsername(),
                        loginModel.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("user", authentication.getPrincipal());

        return ResponseEntity.ok("Login successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful!");
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinModel requestBody) {
        boolean isUser = userRepository.existsByUsername(requestBody.getUsername());
        if (isUser) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        UserEntity data = new UserEntity();
        data.setUsername(requestBody.getUsername());
        data.setPassword(bCryptPasswordEncoder.encode(requestBody.getPassword()));
        data.setRole("ROLE_USER");
        userRepository.save(data);

        return ResponseEntity.status(HttpStatus.OK).body("Join Successful!");
    }

    @GetMapping("/get-username")
    public String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/get-role")
    public String getRole() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        var authorities = authentication.getAuthorities();
        var iterator = authorities.iterator();

        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        return role;
    }
}
