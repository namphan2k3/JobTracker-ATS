package com.jobtracker.jobtracker_app.services.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.jobtracker_app.entities.Permission;
import com.jobtracker.jobtracker_app.entities.User;
import com.jobtracker.jobtracker_app.exceptions.AppException;
import com.jobtracker.jobtracker_app.exceptions.ErrorCode;
import com.jobtracker.jobtracker_app.repositories.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionCacheService {

    UserRepository userRepository;
    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper = new ObjectMapper();

    private static final String CACHE_PREFIX = "user_permissions:";
    private static final long TTL = 10; // minute

    @Transactional(readOnly = true)
    public List<String> getPermissions(String userId) {
        String key = CACHE_PREFIX + userId;

        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson != null) {
            try {
                return objectMapper.readValue(cachedJson, new TypeReference<List<String>>() {});
            } catch (JsonProcessingException e) {
                redisTemplate.delete(key);
            }
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getRole() == null || !Boolean.TRUE.equals(user.getRole().getIsActive()) || user.getRole().isDeleted()) {
            return List.of();
        }

        List<String> permissions = user.getRole().getRolePermissions().stream()
                .filter(rp -> !rp.getIsDeleted())
                .filter(rp -> rp.getPermission() != null
                        && Boolean.TRUE.equals(rp.getPermission().getIsActive())
                        && !rp.getPermission().isDeleted())
                .map(rp -> rp.getPermission().getName())
                .toList();

        try {
            String json = objectMapper.writeValueAsString(permissions);
            redisTemplate.opsForValue().set(key, json, TTL, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return permissions;
    }

    public void evict(String userId) {
        redisTemplate.delete(CACHE_PREFIX + userId);
    }
}
