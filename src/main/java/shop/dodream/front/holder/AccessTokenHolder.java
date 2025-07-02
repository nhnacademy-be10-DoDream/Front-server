package shop.dodream.front.holder;

public class AccessTokenHolder {
    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void set(String token) {
        holder.set(token);
    }

    public static String get() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }
}
