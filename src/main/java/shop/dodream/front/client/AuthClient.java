package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.dodream.front.dto.LoginRequest;

@FeignClient(name = "authClient", url = "http://localhost:10320")
public interface AuthClient {
    @PostMapping("/auth/login")
    Void login(@RequestBody LoginRequest request);
}
