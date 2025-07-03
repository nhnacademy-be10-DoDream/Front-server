package shop.dodream.front.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import shop.dodream.front.dto.UserDto;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUserSessionService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration EXPIRATION = Duration.ofHours(1);

    public void saveUser(String accessToken,UserDto user) {
        redisTemplate.opsForValue().set("LOGIN:"+accessToken, user,EXPIRATION);
    }

    public UserDto getUser(String accessToken) {
        Object obj = redisTemplate.opsForValue().get("LOGIN:"+ accessToken);
        return objectMapper.convertValue(obj, UserDto.class);
    }

    public void deleteUser(String accessToken) {
        redisTemplate.delete("LOGIN:"+accessToken);
    }


}
