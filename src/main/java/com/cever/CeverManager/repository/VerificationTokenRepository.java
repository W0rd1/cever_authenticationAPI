package com.cever.CeverManager.repository;


import com.cever.CeverManager.entity.UserEntity;
import com.cever.CeverManager.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long> {
    VerificationTokenEntity findByToken(String token);
}
