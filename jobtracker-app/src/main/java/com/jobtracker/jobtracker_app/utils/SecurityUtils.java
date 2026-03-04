package com.jobtracker.jobtracker_app.utils;

import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityUtils {
    UserRepository userRepository;

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String userId = authentication.getName();

        return userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
    }
}
