package shop.dodream.front.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequest {
    @NotBlank
    @Size(max = 50)
    private String userId;
    @NotBlank
    @Size(max = 200)
    private String password;
    @Email
    @NotBlank
    @Size(max = 100)
    private String email;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 20)
    private String phone;
    @NotNull
    private String birthDate; // "YYYY-MM-DD" 형식으로 입력
}
