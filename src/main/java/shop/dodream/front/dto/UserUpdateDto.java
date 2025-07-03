package shop.dodream.front.dto;

public record UserUpdateDto(
        String password,
        String email,
        String name,
        String phone,
        String status,
        String birthDate
) {}