package com.jobtracker.jobtracker_app.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jobtracker.jobtracker_app.dto.requests.auth.ChangePasswordRequest;
import com.jobtracker.jobtracker_app.dto.requests.user.UserUpdateProfileRequest;
import com.jobtracker.jobtracker_app.dto.responses.user.UploadAvatarResponse;
import com.jobtracker.jobtracker_app.dto.responses.user.UserResponse;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.mappers.UserMapper;
import com.jobtracker.jobtracker_app.repositories.UserRepository;
import com.jobtracker.jobtracker_app.services.UserService;
import com.jobtracker.jobtracker_app.utils.SecurityUtils;
import com.jobtracker.jobtracker_app.validator.file.FileValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    Cloudinary cloudinary;
    FileValidator imageFileValidator;
    SecurityUtils securityUtils;

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserResponse getProfile() {
        User user = securityUtils.getCurrentUser();

        return userMapper.toUserResponse(user);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public UserResponse updateProfile(UserUpdateProfileRequest request) {
        User user = securityUtils.getCurrentUser();

        userMapper.updateUserProfile(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = securityUtils.getCurrentUser();
        boolean authenticated = passwordEncoder.matches(request.getCurrentPassword(), user.getPassword());

        if (!authenticated) {
            throw new AppException(ErrorCode.INCORRECT_CURRENT_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UploadAvatarResponse uploadAvatar(MultipartFile file) throws IOException {
        imageFileValidator.validate(file);

        User user = securityUtils.getCurrentUser();

        String folderPath = "jobtracker_ats/user/" + user.getId() + "/avatars";

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folderPath,
                        "resource_type", "image"
                )
        );

        String newUrl = (String) result.get("secure_url");
        String newPublicId = (String) result.get("public_id");

        if (newUrl == null || newPublicId == null) {
            throw new AppException(ErrorCode.UPLOAD_FILE_FAILED);
        }

        String oldPublicId = user.getAvatarPublicId();

        try {
            user.setAvatarUrl(newUrl);
            user.setAvatarPublicId(newPublicId);
            userRepository.save(user);
        } catch (Exception e) {
            cloudinary.uploader().destroy(newPublicId, ObjectUtils.emptyMap());
            throw e;
        }

        if (oldPublicId != null) {
            cloudinary.uploader().destroy(oldPublicId, ObjectUtils.emptyMap());
        }

        return UploadAvatarResponse.builder()
                .avatarUrl(newUrl)
                .build();
    }
}
