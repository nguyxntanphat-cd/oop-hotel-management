package hotel.util;

import java.time.format.DateTimeFormatter;

/**
 * Cấu hình dùng chung toàn chương trình: chỉ gồm hằng số static.
 */
public final class AppConfig {
    public static final String HOTEL_NAME = "OOP Hotel";

    /** Thư mục chứa file dữ liệu, tính từ thư mục chạy chương trình. */
    public static final String DATA_DIR = "data";

    /** Ký tự phân cách các trường trong file text. */
    public static final String SEPARATOR = "|";

    /** Dạng regex của SEPARATOR, dùng cho {@code line.split(AppConfig.SEPARATOR_REGEX, -1)}. */
    public static final String SEPARATOR_REGEX = "\\|";

    public static final String DATE_PATTERN = "dd/MM/yyyy";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /** Thuế VAT áp dụng cho hóa đơn (8%). */
    public static final double VAT_RATE = 0.08;

    /** Giờ nhận / trả phòng tiêu chuẩn (để in trên hóa đơn). */
    public static final String CHECK_IN_TIME = "14:00";
    public static final String CHECK_OUT_TIME = "12:00";

    private AppConfig() {
    }
}
