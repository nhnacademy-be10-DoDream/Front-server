package shop.dodream.front.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import shop.dodream.front.client.AuthClient;
import shop.dodream.front.dto.SessionUser;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RoleCheckFilter extends OncePerRequestFilter {
    private final AuthClient authClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();
            if (uri.startsWith("/auth")||uri.startsWith("/css")||uri.startsWith("/js")||uri.startsWith("/images")||uri.contains("devtools")) {
                filterChain.doFilter(request, response);
                return;
            }

            SessionUser sessionUser = authClient.getSessionUser().getBody();
            if (sessionUser != null) {
                List<GrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + sessionUser.getRole().name()));

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(sessionUser, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}