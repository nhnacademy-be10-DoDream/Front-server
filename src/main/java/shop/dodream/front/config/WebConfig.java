package shop.dodream.front.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import shop.dodream.front.interceptor.CategoryInterceptor;
import shop.dodream.front.interceptor.LoginUserInterceptor;
import shop.dodream.front.interceptor.RequestInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserInterceptor loginUserInterceptor;
    private final RequestInterceptor requestInterceptor;
    private final CategoryInterceptor categoryInterceptor;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasenames("message", "mypage");

        return messageSource;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor);
        registry.addInterceptor(requestInterceptor);

        registry.addInterceptor(categoryInterceptor)
                .addPathPatterns("/**");
    }
}
