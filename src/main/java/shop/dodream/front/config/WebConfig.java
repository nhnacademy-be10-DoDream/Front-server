package shop.dodream.front.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
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
        messageSource.setBasenames("message");

        return messageSource;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/auth/login-form").setViewName("auth/login");
        registry.addViewController("/auth/signup-form").setViewName("auth/signup");
        registry.addViewController("/admin/books/api-register-form").setViewName("admin/book-api-register");
        registry.addViewController("/admin/books/register-form").setViewName("admin/book-register");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor);
        registry.addInterceptor(requestInterceptor);

        registry.addInterceptor(categoryInterceptor)
                .addPathPatterns("/**");
    }
}
