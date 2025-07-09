package shop.dodream.front.dto;

public record UserUpdateDto(
        String email,
        String name,
        String phone,
        String birthDate
) {}