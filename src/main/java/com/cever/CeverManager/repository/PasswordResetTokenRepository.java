package com.cever.CeverManager.repository;


import com.cever.CeverManager.entity.PasswordResetTokenEntity;
import com.cever.CeverManager.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    void deleteByUser(UserEntity user);
    PasswordResetTokenEntity findByToken(String token);
}
