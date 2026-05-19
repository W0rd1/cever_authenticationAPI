package com.cever.CeverManager.controller;


import com.cever.CeverManager.dto.AccountLockDTO;
import com.cever.CeverManager.entity.UserEntity;
import com.cever.CeverManager.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private UserRepository userRepo;

    public AdminUserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @PostMapping(path = "/lock-status", consumes = "application/json")
    @Transactional
    public ResponseEntity<?> changeAccountLockStatus(@RequestBody AccountLockDTO lockRequest){
        UserEntity user = userRepo.findByUsername(lockRequest.username());
        if(user == null){
            return ResponseEntity.notFound().build();
        }

        user.setAccountNonLocked(!lockRequest.lock());
        if(!lockRequest.lock()){
            user.setFailedAttemptCount(0);
        }
        String message = lockRequest.lock() ? "Account locked successfully" : "Account unlockoed successfully";
        return ResponseEntity.ok(message);

    }
}
