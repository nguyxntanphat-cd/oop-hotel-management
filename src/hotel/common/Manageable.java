package hotel.common;

import java.util.List;

/**
 * Các thao tác quản lý bắt buộc của đồ án: xem, thêm, sửa, xóa, tìm kiếm.
 *
 * @param <T> kiểu đối tượng được quản lý
 */
public interface Manageable<T> {
    /** Xem: in toàn bộ danh sách. */
    void displayAll();

    /** Thêm: trả về false nếu trùng mã hoặc phần tử null. */
    boolean add(T item);

    /** Sửa: thay phần tử có mã id bằng newItem. Trả về false nếu không tìm thấy. */
    boolean update(String id, T newItem);

    /** Xóa: trả về false nếu không tìm thấy. */
    boolean remove(String id);

    /** Tìm chính xác theo mã (không phân biệt hoa thường). Trả về null nếu không có. */
    T findById(String id);

    /** Tìm gần đúng theo từ khóa. */
    List<T> search(String keyword);
}
