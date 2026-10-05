package hotel.common;

/**
 * Đối tượng so khớp được với một từ khóa tìm kiếm.
 */
public interface Searchable {
    /**
     * @param keyword từ khóa đã được chuyển về chữ thường và bỏ khoảng trắng 2 đầu
     * @return true nếu đối tượng chứa từ khóa (theo mã, tên, ...)
     */
    boolean matches(String keyword);
}
