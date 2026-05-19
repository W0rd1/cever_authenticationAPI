package com.cever.CeverManager.controller;

import com.cever.CeverManager.dto.*;
import com.cever.CeverManager.entity.UserEntity;
import com.cever.CeverManager.repository.UserRepository;
import com.cever.CeverManager.service.TokenProvider;
import com.cever.CeverManager.service.UserManagementService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@ResponseBody
public class AuthenticationController {

    private final UserManagementService userService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private PasswordEncoder passwordEncoder;

    public AuthenticationController(
            UserManagementService userService,
            UserRepository userRepository,
            TokenProvider tokenProvider,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }
    
    
    @PostMapping(path="/register",consumes = "application/json")
    public ResponseEntity<?> register(@RequestBody RegisterUserDTO registerUserRequest) {
        // 1. Validate against empty or null requests
        if (registerUserRequest == null || registerUserRequest.username() == null) {
            return ResponseEntity.badRequest().body("Registration data cannot be empty");
        }

        // 2. Check if the username or email already exists to prevent duplicates
        if (userService.existsByUsername(registerUserRequest.username())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken");
        }

        // 3. Map DTO to Entity and save via the service layer
        UserEntity savedUser = userService.addUser(registerUserRequest);

        // 4. Return success status code and a clean message or the saved resource
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping(path="/login",consumes = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // 1. Find user by username
        UserEntity user = userRepository.findByUsername(loginRequest.username());

        if(user != null && !user.isEnabled()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please activate your account via email before logging in.");
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        if(!user.isAccountNonLocked()){
            return ResponseEntity.status(HttpStatus.LOCKED).body("Account is locked due to too many failed attempts.");
        }

        // 2. Verify encrypted password (never use raw .equals())
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {

            userService.increaseFailedAttempts(user);

            if(user.getFailedAttemptCount() >= 3){
                userService.lockAccount(user);
                return ResponseEntity.status(HttpStatus.LOCKED).body("Account has been locked due to 3 failed attempts.");
            }


            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // 3. Generate a stateless token
        String token = tokenProvider.createToken(user.getUsername(), List.of(String.valueOf(user.getRole())));

        // 4. Return token wrapper JSON to Angular
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }


    @PostMapping(path = "/forgot-password", consumes = "application/json")
    public ResponseEntity<String> processForgetPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO){
        try{
            userService.createAndSendResetToken(forgotPasswordDTO.email());
            return ResponseEntity.ok("If that email exists in our system, a reset link has been sent.");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("An error occurred while processing your request.");
        }
    }

    @PostMapping(path = "/reset-password", consumes = "application/json")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordSubmitDTO resetPasswordSubmitDTO){
        boolean isResetSuccessful = userService.updateAndSavePassword(resetPasswordSubmitDTO.token(), resetPasswordSubmitDTO.newPassword());
        if(!isResetSuccessful){
            return ResponseEntity.badRequest().body("Invalid or expired reset token.");
        }
        return ResponseEntity.ok("Password updated successfully.");
    }

    @PostMapping(path = "/active", consumes = "application/json")
    public ResponseEntity<?> activeAccount(@RequestBody AccountActivationDTO activeDTO){
        boolean isActive = userService.activeUser(activeDTO.token());
        if(!isActive){
            return ResponseEntity.badRequest().body("Your verification link is invalid or has expired.");
        }
        return ResponseEntity.ok("Account verified successfully! You can now log in.");
    }

}