package shop.dodream.front.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.filter.AccessTokenInjectionFilter;
import shop.dodream.front.filter.RoleCheckFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthClient authClient;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/auth/login-form");
                        })
                )
                .addFilterBefore(new AccessTokenInjectionFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RoleCheckFilter(authClient), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
