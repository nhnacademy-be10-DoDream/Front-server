package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.dto.LoginRequest;
import shop.dodream.front.dto.TokenResponse;

@FeignClient(name = "authClient", url = "${gateway.url}")
public interface AuthClient {
    @PostMapping("/auth/login")
    ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request);

    @PostMapping("/auth/refresh")
    ResponseEntity<TokenResponse> refresh(@RequestHeader(HttpHeaders.COOKIE) String refreshToken);

    @PostMapping("/auth/logout")
    ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.COOKIE) String refreshToken);

    @PostMapping("/auth/payco/callback")
    ResponseEntity<TokenResponse> paycoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state
    );
    @GetMapping("/auth/payco/authorize")
    ResponseEntity<String> getAuthorizeUrl();

    @PostMapping("/auth/dormant/request")
    ResponseEntity<Void> sendDormantCode(@RequestParam("userId") String userId);

    @PostMapping("/auth/dormant/verify")
    ResponseEntity<String> verifyDormantCode(@RequestParam("userId") String userId,
                                             @RequestParam("code") String code);

}
