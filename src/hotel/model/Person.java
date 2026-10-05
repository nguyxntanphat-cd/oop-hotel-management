package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.AppConfig;

/**
 * Lớp trừu tượng cha của Khách hàng (Customer) và Nhân viên (Employee).
 */
public abstract class Person implements Identifiable, FileSerializable, Searchable {
    protected String id;
    protected String fullName;
    protected String phone;
    protected String idCard; // CCCD

    public Person() {
        this("", "", "", "");
    }

    public Person(String id, String fullName, String phone, String idCard) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.idCard = idCard;
    }

    /** Constructor sao chép, dùng cho copy constructor của lớp con. */
    public Person(Person other) {
        this(other.id, other.fullName, other.phone, other.idCard);
    }

    /** Vai trò hiển thị, ví dụ "Khách VIP", "Lễ tân". Lớp con bắt buộc cài đặt. */
    public abstract String getRole();

    /** Tìm theo mã, họ tên, số điện thoại, CCCD. Lớp con có thể ghi đè để tìm thêm trường riêng. */
    @Override
    public boolean matches(String keyword) {
        String key = keyword.toLowerCase();
        return id.toLowerCase().contains(key)
                || fullName.toLowerCase().contains(key)
                || phone.contains(key)
                || idCard.contains(key);
    }

    /**
     * Phần chung của dòng dữ liệu trong file: {@code id|fullName|phone|idCard}.
     * Lớp con ghép thêm trường loại ở đầu và các trường riêng ở cuối.
     */
    protected String baseFileString() {
        return String.join(AppConfig.SEPARATOR, id, fullName, phone, idCard);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    /** Tiêu đề bảng khớp với {@link #toString()}. */
    public static String header() {
        return String.format("%-8s %-25s %-12s %-14s %-18s", "Mã", "Họ tên", "SĐT", "CCCD", "Vai trò");
    }

    @Override
    public String toString() {
        return String.format("%-8s %-25s %-12s %-14s %-18s", id, fullName, phone, idCard, getRole());
    }
}
