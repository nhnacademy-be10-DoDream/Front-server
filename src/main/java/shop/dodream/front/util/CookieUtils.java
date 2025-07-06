package shop.dodream.front.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

public class CookieUtils {

    public static String extractCookieValue(List<String> cookies, String name) {
        if (cookies == null || name == null) return null;
        for (String cookie : cookies) {
            if (cookie.startsWith(name + "=")) {
                return cookie.split("=")[1].split(";")[0];
            }
        }
        return null;
    }

    public static String extractAccessToken(List<String> cookies) {
        return extractCookieValue(cookies, "accessToken");
    }

    public static String extractRefreshToken(List<String> cookies) {
        return extractCookieValue(cookies, "refreshToken");
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static List<String> convertToSetCookieList(Cookie[] cookies) {
        List<String> list = new ArrayList<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                list.add(cookie.getName() + "=" + cookie.getValue());
            }
        }
        return list;

    }
}
