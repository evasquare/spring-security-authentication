package com.evasquare.username_password_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.evasquare.username_password_auth.models.JoinModel;
import com.evasquare.username_password_auth.models.LoginModel;
import com.evasquare.username_password_auth.service.JoinService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JoinService joinService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
    public String join(@RequestBody JoinModel requestBody) {
        System.out.println(requestBody.getUsername());
        joinService.joinProcess(requestBody);

        return new String();
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
