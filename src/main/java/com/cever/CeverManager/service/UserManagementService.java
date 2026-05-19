package com.cever.CeverManager.service;

import com.cever.CeverManager.config.Roles;
import com.cever.CeverManager.dto.RegisterUserDTO;
import com.cever.CeverManager.entity.PasswordResetTokenEntity;
import com.cever.CeverManager.entity.UserEntity;
import com.cever.CeverManager.entity.VerificationTokenEntity;
import com.cever.CeverManager.repository.PasswordResetTokenRepository;
import com.cever.CeverManager.repository.UserRepository;
import com.cever.CeverManager.repository.VerificationTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserManagementService {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private PasswordResetTokenRepository resetRepo;
    private VerificationTokenRepository verification;

    public UserManagementService(
            UserRepository userRepo,
            PasswordEncoder passwordEncoder,
            PasswordResetTokenRepository resetRepo,
            VerificationTokenRepository verification
    ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.resetRepo = resetRepo;
        this.verification = verification;
    }

    public UserEntity toUserEntity(RegisterUserDTO registerUserDTO) {
        return UserEntity.builder()
                .username(registerUserDTO.username())
                .password(passwordEncoder.encode(registerUserDTO.password()))
                .email(registerUserDTO.email())
                .role(Roles.USER.getRole())
                .enable(false)
                .build();
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @Transactional
    public void createAndSendResetToken(String email){
        UserEntity user = userRepo.findByEmail(email);
        if(user == null){
            return;
        }
        resetRepo.deleteByUser(user);
        String token = UUID.randomUUID().toString();
        PasswordResetTokenEntity entity = PasswordResetTokenEntity.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        resetRepo.save(entity);
        System.out.println("Password reset link: http://localhost:4200/reset-password?token=" + token);
    }

    @Transactional
    public boolean updateAndSavePassword(String token, String newPassword){
        PasswordResetTokenEntity restToken = resetRepo.findByToken(token);
        if(restToken == null || restToken.getExpiryDate().isBefore(LocalDateTime.now())){
            return false;
        }

        UserEntity user = restToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetRepo.delete(restToken);

        return true;
    }

    @Transactional
    public void increaseFailedAttempts(UserEntity user){
        int newAttempts = user.getFailedAttemptCount() + 1;
        user.setFailedAttemptCount(newAttempts);
        userRepo.save(user);
    }

    @Transactional
    public void lockAccount(UserEntity user){
        user.setAccountNonLocked(false);
        userRepo.save(user);
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        UserEntity user = userRepo.findByUsername(username);
        if (user != null) {
            user.setFailedAttemptCount(0);
            userRepo.save(user);
        }
    }

    @Transactional
    public UserEntity addUser(RegisterUserDTO registerUserDTO) {
        UserEntity savedUser = userRepo.save(toUserEntity(registerUserDTO));

        String token = UUID.randomUUID().toString();
        VerificationTokenEntity verificationToken = VerificationTokenEntity.builder()
                .token(token)
                .user(savedUser)
                .expireDate(LocalDateTime.now().plusHours(24))
                .build();

        verification.save(verificationToken);

        System.out.println("Activation Link: http://localhost:4200/verify-account?token=" + token);

        return savedUser;
    }

    @Transactional
    public boolean activeUser(String token){
        VerificationTokenEntity userByToken = verification.findByToken(token);
        if(userByToken == null || userByToken.getExpireDate().isBefore(LocalDateTime.now())){
            return false;
        }

        UserEntity user = userByToken.getUser();
        user.setEnable(true);
        userRepo.save(user);

        verification.delete(userByToken);

        return true;
    }
}