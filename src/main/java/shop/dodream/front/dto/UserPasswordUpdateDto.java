package shop.dodream.front.dto;

public record UserPasswordUpdateDto (
        String currentPassword,
        String newPassword
) {}