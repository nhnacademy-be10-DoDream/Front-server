package shop.dodream.front.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.dodream.front.holder.AccessTokenHolder;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor authTokenRelayInterceptor() {
        return requestTemplate -> {
            String token = AccessTokenHolder.get();
            if (token != null) {
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}
