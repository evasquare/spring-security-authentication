package com.evasquare.username_password_auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.evasquare.username_password_auth.entity.UserEntity;
import com.evasquare.username_password_auth.models.ChangePasswordModel;
import com.evasquare.username_password_auth.models.JoinModel;
import com.evasquare.username_password_auth.models.LoginModel;
import com.evasquare.username_password_auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginModel loginModel,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already logged in.");
        }

        var context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginModel.getUsername(),
                            loginModel.getPassword()));
        } catch (InternalAuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User doesn't exist.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password.");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }

        // Original implementation from #AuthenticationFilter
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);

        session.setAttribute("user", authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.OK).body("Login successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not found.");
        }

        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.OK).body("Logout successful!");
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinModel requestBody) {
        boolean isUsernameTaken = userRepository.existsByUsername(requestBody.getUsername());
        if (isUsernameTaken) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        if (!requestBody.getPassword().equals(requestBody.getConfirmationPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Confirmation password doesn't match.");
        }

        try {
            var data = new UserEntity();
            data.setUsername(requestBody.getUsername());
            data.setPassword(bCryptPasswordEncoder.encode(requestBody.getPassword()));
            data.setRole("ROLE_USER");
            userRepository.save(data);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request.");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Join Successful!");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordModel body) {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();

        var userOptional = userRepository.findByUsername(authentication.getName());
        var user = userOptional.get();

        if (!bCryptPasswordEncoder.matches(body.getOriginalPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Original password doesn't match.");
        }

        System.out.println(body.getNewPassword());

        if (!body.getNewPassword().equals(body.getNewPasswordConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Confirmation password doesn't match.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(body.getNewPassword()));
        userRepository.save(user);


        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully!");

    }

    @GetMapping("/get-username")
    public String getUsername() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/get-role")
    public String getRole() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    @PostMapping("/validation")
    public ResponseEntity<Boolean> validateSession() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
    }
}
