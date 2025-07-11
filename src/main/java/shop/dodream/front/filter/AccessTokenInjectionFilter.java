package shop.dodream.front.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import shop.dodream.front.holder.AccessTokenHolder;
import shop.dodream.front.util.CookieUtils;

import java.io.IOException;

@Slf4j
public class AccessTokenInjectionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String accessToken = CookieUtils.extractAccessCookie(httpRequest);

            if (accessToken != null) {
                AccessTokenHolder.set(accessToken);
            }

            chain.doFilter(request, response);

        } finally {
            AccessTokenHolder.clear();
        }
    }
}
