package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionUser implements Serializable {
    private String userId;
    private Role role;
}
