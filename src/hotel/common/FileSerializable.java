package hotel.common;

/**
 * Đối tượng ghi được ra 1 dòng trong file text.
 * Các trường nối với nhau bằng {@link hotel.util.AppConfig#SEPARATOR}.
 * Việc đọc ngược 1 dòng thành đối tượng do {@link BaseList#parseLine(String)} đảm nhiệm.
 */
public interface FileSerializable {
    String toFileString();
}
