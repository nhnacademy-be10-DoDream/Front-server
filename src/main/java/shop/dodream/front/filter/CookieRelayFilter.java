package shop.dodream.front.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Order(1)
public class CookieRelayFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        SetCookieCapturingResponseWrapper capturingResponse = new SetCookieCapturingResponseWrapper(response);

        // 체인 실행
        filterChain.doFilter(request, capturingResponse);

        // 내부 응답에서 Set-Cookie 추출 후 브라우저로 전달
        Collection<String> setCookies = capturingResponse.getHeaderValues(HttpHeaders.SET_COOKIE);
        for (String cookie : setCookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
    }

    private static class SetCookieCapturingResponseWrapper extends HttpServletResponseWrapper {
        private final Map<String, List<String>> headers = new HashMap<>();

        public SetCookieCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {
            if (HttpHeaders.SET_COOKIE.equalsIgnoreCase(name)) {
                headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
            }
            super.addHeader(name, value);
        }

        public Collection<String> getHeaderValues(String name) {
            return headers.getOrDefault(name, Collections.emptyList());
        }
    }
}