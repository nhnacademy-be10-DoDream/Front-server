package shop.dodream.front.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Email
        @NotBlank
        @Size(max = 100)
        String email,
        @NotBlank
        @Size(max = 50)
        String name,
        @NotBlank
        @Size(max = 20)
        String phone,
        @NotBlank
        String birthDate
) {}