package shop.dodream.front.aop;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SetCookieRelayAspect {

    @Around("execution(* shop.dodream.front.controller..*(..))")
    public Object relaySetCookieHeaders(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof ResponseEntity<?> responseEntity) {
            List<String> setCookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null && !setCookies.isEmpty()) {
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attr != null) {
                    HttpServletResponse response = attr.getResponse();
                    if (response != null) {
                        for (String cookie : setCookies) {
                            log.debug("Relaying Set-Cookie: {}", cookie);
                            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
                        }
                    }
                }
            }
        }

        return result;
    }
}
