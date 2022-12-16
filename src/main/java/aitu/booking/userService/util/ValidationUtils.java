package aitu.booking.userService.util;

public class ValidationUtils {
    public static final String PATTERN_PHONE = "^\\+77\\d{9}$";
    public static final String PATTERN_PHONE_OR_BLANK = "^|(\\+77\\d+)$";
    public static final String PATTERN_EMAIL = "^\\s*$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)$";
    public static final String PATTERN_EMAIL_OR_BLANK = "^|\\s*$|^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2}|com|org|net|edu|gov|mil|biz|info|mobi|name|aero|asia|jobs|museum)$";
}
