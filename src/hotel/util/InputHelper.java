package hotel.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Tiện ích nhập liệu từ bàn phím có kiểm tra hợp lệ.
 * <p>
 * Mọi phương thức đều static và dùng chung MỘT Scanner duy nhất
 * (không tự tạo {@code new Scanner(System.in)} ở chỗ khác để tránh lỗi đọc input).
 * <p>
 * Các hàm {@code ...OrKeep} dùng khi SỬA: người dùng nhấn Enter thì giữ nguyên giá trị cũ.
 */
public final class InputHelper {
    private static final Scanner SCANNER = new Scanner(System.in, StandardCharsets.UTF_8.name());

    private static final String PHONE_REGEX = "0\\d{9}";
    private static final String ID_CARD_REGEX = "\\d{12}";

    private InputHelper() {
    }

    // ===================== Chuỗi =====================

    /** Nhập 1 dòng bất kỳ (có thể rỗng), đã bỏ khoảng trắng 2 đầu. */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            // Hết input (ví dụ chạy bằng file chuyển hướng) thì thoát an toàn
            System.out.println();
            System.exit(0);
        }
        return SCANNER.nextLine().trim();
    }

    /** Nhập chuỗi bắt buộc: không rỗng, không chứa ký tự phân cách file. */
    public static String readString(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (isValidText(value)) {
                return value;
            }
        }
    }

    public static String readStringOrKeep(String prompt, String oldValue) {
        while (true) {
            String value = readLine(keepPrompt(prompt, oldValue));
            if (value.isEmpty()) {
                return oldValue;
            }
            if (isValidText(value)) {
                return value;
            }
        }
    }

    /** Nhập chuỗi khớp biểu thức chính quy, ví dụ readPattern("Email: ", ".+@.+", "Sai email"). */
    public static String readPattern(String prompt, String regex, String errorMessage) {
        while (true) {
            String value = readLine(prompt);
            if (value.matches(regex)) {
                return value;
            }
            System.out.println("  -> " + errorMessage);
        }
    }

    public static String readPatternOrKeep(String prompt, String oldValue, String regex, String errorMessage) {
        while (true) {
            String value = readLine(keepPrompt(prompt, oldValue));
            if (value.isEmpty()) {
                return oldValue;
            }
            if (value.matches(regex)) {
                return value;
            }
            System.out.println("  -> " + errorMessage);
        }
    }

    /** Số điện thoại: 10 chữ số, bắt đầu bằng 0. */
    public static String readPhone(String prompt) {
        return readPattern(prompt, PHONE_REGEX, "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!");
    }

    public static String readPhoneOrKeep(String prompt, String oldValue) {
        return readPatternOrKeep(prompt, oldValue, PHONE_REGEX,
                "Số điện thoại phải gồm 10 chữ số và bắt đầu bằng 0!");
    }

    /** CCCD: 12 chữ số. */
    public static String readIdCard(String prompt) {
        return readPattern(prompt, ID_CARD_REGEX, "CCCD phải gồm đúng 12 chữ số!");
    }

    public static String readIdCardOrKeep(String prompt, String oldValue) {
        return readPatternOrKeep(prompt, oldValue, ID_CARD_REGEX, "CCCD phải gồm đúng 12 chữ số!");
    }

    // ===================== Số nguyên =====================

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("  -> Vui lòng nhập số nguyên!");
            }
        }
    }

    /** Nhập số nguyên trong đoạn [min, max]. */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("  -> Giá trị phải nằm trong khoảng " + min + " - " + max + "!");
        }
    }

    public static int readIntOrKeep(String prompt, int oldValue, int min, int max) {
        while (true) {
            String value = readLine(keepPrompt(prompt, String.valueOf(oldValue)));
            if (value.isEmpty()) {
                return oldValue;
            }
            try {
                int n = Integer.parseInt(value);
                if (n >= min && n <= max) {
                    return n;
                }
                System.out.println("  -> Giá trị phải nằm trong khoảng " + min + " - " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("  -> Vui lòng nhập số nguyên!");
            }
        }
    }

    // ===================== Số thực =====================

    public static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("  -> Vui lòng nhập số!");
            }
        }
    }

    /** Nhập số thực không âm (giá tiền, lương, ...). */
    public static double readPositiveDouble(String prompt) {
        while (true) {
            double value = readDouble(prompt);
            if (value >= 0) {
                return value;
            }
            System.out.println("  -> Giá trị không được âm!");
        }
    }

    public static double readPositiveDoubleOrKeep(String prompt, double oldValue) {
        while (true) {
            String value = readLine(keepPrompt(prompt, FileHelper.formatMoney(oldValue)));
            if (value.isEmpty()) {
                return oldValue;
            }
            try {
                double d = Double.parseDouble(value);
                if (d >= 0) {
                    return d;
                }
                System.out.println("  -> Giá trị không được âm!");
            } catch (NumberFormatException e) {
                System.out.println("  -> Vui lòng nhập số!");
            }
        }
    }

    // ===================== Ngày =====================

    /** Nhập ngày theo định dạng dd/MM/yyyy. */
    public static LocalDate readDate(String prompt) {
        while (true) {
            String value = readLine(prompt + " (" + AppConfig.DATE_PATTERN + "): ");
            try {
                return FileHelper.parseDate(value);
            } catch (DateTimeParseException e) {
                System.out.println("  -> Ngày không hợp lệ!");
            }
        }
    }

    /** Nhập ngày phải SAU ngày cho trước (ví dụ ngày trả phòng sau ngày nhận phòng). */
    public static LocalDate readDateAfter(String prompt, LocalDate after) {
        while (true) {
            LocalDate date = readDate(prompt);
            if (date.isAfter(after)) {
                return date;
            }
            System.out.println("  -> Ngày phải sau " + FileHelper.formatDate(after) + "!");
        }
    }

    public static LocalDate readDateOrKeep(String prompt, LocalDate oldValue) {
        while (true) {
            String value = readLine(keepPrompt(prompt + " (" + AppConfig.DATE_PATTERN + ")",
                    FileHelper.formatDate(oldValue)));
            if (value.isEmpty()) {
                return oldValue;
            }
            try {
                return FileHelper.parseDate(value);
            } catch (DateTimeParseException e) {
                System.out.println("  -> Ngày không hợp lệ!");
            }
        }
    }

    // ===================== Lựa chọn =====================

    /**
     * In danh sách lựa chọn đánh số từ 1 và trả về chỉ số (bắt đầu từ 0) người dùng chọn.
     * Ví dụ: {@code int i = InputHelper.choose("Loại phòng:", "Standard", "Deluxe", "Suite");}
     */
    public static int choose(String title, String... options) {
        System.out.println(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println("  " + (i + 1) + ". " + options[i]);
        }
        return readInt("Chọn: ", 1, options.length) - 1;
    }

    /** Hỏi Có/Không, trả về true nếu người dùng nhập y hoặc Y. */
    public static boolean confirm(String prompt) {
        return readLine(prompt + " (y/n): ").equalsIgnoreCase("y");
    }

    public static void pause() {
        readLine("Nhấn Enter để tiếp tục...");
    }

    // ===================== Nội bộ =====================

    private static String keepPrompt(String prompt, String oldValue) {
        return prompt + " [" + oldValue + "]: ";
    }

    private static boolean isValidText(String value) {
        if (value.isEmpty()) {
            System.out.println("  -> Không được để trống!");
            return false;
        }
        if (value.contains(AppConfig.SEPARATOR)) {
            System.out.println("  -> Không được chứa ký tự '" + AppConfig.SEPARATOR + "'!");
            return false;
        }
        return true;
    }
}
