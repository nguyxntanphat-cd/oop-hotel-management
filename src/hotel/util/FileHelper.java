package hotel.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiện ích đọc/ghi file text (UTF-8) và chuyển đổi dữ liệu khi đọc file.
 * Mọi phương thức đều static.
 */
public final class FileHelper {
    private FileHelper() {
    }

    /** Đường dẫn file dữ liệu trong thư mục data/, ví dụ dataPath("rooms.txt"). */
    public static String dataPath(String fileName) {
        return AppConfig.DATA_DIR + File.separator + fileName;
    }

    /**
     * Đọc tất cả các dòng có dữ liệu: bỏ dòng trống và dòng bắt đầu bằng '#' (chú thích).
     * Nếu file chưa tồn tại thì trả về danh sách rỗng.
     */
    public static List<String> readLines(String path) {
        List<String> lines = new ArrayList<>();
        File file = new File(path);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.replace("﻿", "").trim(); // bỏ BOM do Notepad thêm vào
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Lỗi đọc file " + path + ": " + e.getMessage());
        }
        return lines;
    }

    /** Ghi đè toàn bộ file bằng các dòng cho trước. Tự tạo thư mục nếu chưa có. */
    public static boolean writeLines(String path, List<String> lines) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Lỗi ghi file " + path + ": " + e.getMessage());
            return false;
        }
    }

    /** Tách 1 dòng thành các trường (đã trim), giữ cả trường rỗng ở cuối. */
    public static String[] split(String line) {
        String[] parts = line.split(AppConfig.SEPARATOR_REGEX, -1);
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    /** Nối các trường thành 1 dòng để ghi file. */
    public static String join(Object... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(AppConfig.SEPARATOR);
            }
            Object f = fields[i];
            if (f instanceof LocalDate) {
                sb.append(formatDate((LocalDate) f));
            } else if (f != null) {
                sb.append(f);
            }
        }
        return sb.toString();
    }

    /** Chuyển chuỗi dd/MM/yyyy thành LocalDate (ném DateTimeParseException nếu sai). */
    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text.trim(), AppConfig.DATE_FORMAT);
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(AppConfig.DATE_FORMAT);
    }

    /** Định dạng tiền để hiển thị, ví dụ 1500000 -> "1,500,000". */
    public static String formatMoney(double amount) {
        return String.format("%,.0f", amount);
    }
}
