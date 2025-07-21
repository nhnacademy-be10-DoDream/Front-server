package shop.dodream.front.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDto (
        @NotBlank
        @Size(max = 200)
        String currentPassword,
        @NotBlank
        @Size(max = 200)
        String newPassword
) {}