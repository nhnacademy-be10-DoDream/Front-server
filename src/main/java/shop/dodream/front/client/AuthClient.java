package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.LoginRequest;

@FeignClient(name = "authClient", url = "http://localhost:10320")
public interface AuthClient {
    @PostMapping("/auth/login")
    ResponseEntity<Void> login(@RequestBody LoginRequest request);

    @PostMapping("/auth/refresh")
    ResponseEntity<Void> refresh(@RequestHeader(HttpHeaders.COOKIE) String refreshToken);

    @PostMapping("/auth/logout")
    ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.COOKIE) String refreshToken);

    @PostMapping("/auth/payco/callback")
    ResponseEntity<Void> paycoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    );
    @GetMapping("/auth/payco/authorize")
    ResponseEntity<Void> authorize();

    @GetMapping("/auth/payco/authorize")
    ResponseEntity<String> getAuthorizeUrl();

}
