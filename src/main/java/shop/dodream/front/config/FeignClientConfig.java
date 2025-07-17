package shop.dodream.front.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import shop.dodream.front.holder.AccessTokenHolder;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor authTokenRelayInterceptor() {
        return requestTemplate -> {
            // Authorization 전달
            String token = AccessTokenHolder.get();
            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }

            // User-Agent 전달
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null) {
                    requestTemplate.header("User-Agent", userAgent);
                }
            }
        };
    }
}
