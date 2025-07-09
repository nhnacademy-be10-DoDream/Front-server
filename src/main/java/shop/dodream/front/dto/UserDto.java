package shop.dodream.front.dto;

import java.time.ZonedDateTime;
import java.util.Date;

public record UserDto(
        String userId,
        String email,
        String name,
        String phone,
        String status,
        Date birthDate,
        ZonedDateTime createdAt,
        ZonedDateTime lastLoginAt,
        String grade,
        long point
) {}