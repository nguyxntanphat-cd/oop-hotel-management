package hotel.model;

import hotel.util.FileHelper;

/**
 * Khách hàng thường, kế thừa Person. Lớp VipCustomer kế thừa lớp này.
 */
public class Customer extends Person {
    public static final String ID_PREFIX = "KH";
    public static final String TYPE = "NORMAL";

    private static int createdCount = 0;

    private String nationality;
    private String email;

    public Customer() {
        this("", "", "", "", "", "");
    }

    public Customer(String id, String fullName, String phone, String idCard,
                    String nationality, String email) {
        super(id, fullName, phone, idCard);
        this.nationality = nationality;
        this.email = email;
        createdCount++;
    }

    public Customer(Customer other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.nationality, other.email);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getRole() {
        return "Khách thường";
    }

    /** Khách thường không được giảm giá. VipCustomer ghi đè (đa hình). */
    public double getDiscountRate() {
        return 0.0;
    }

    public boolean isVip() {
        return false;
    }

    @Override
    public String toFileString() {
        // NORMAL|maKH|hoTen|sdt|cccd|quocTich|email
        return FileHelper.join(TYPE, baseFileString(), nationality, email);
    }

    @Override
    public boolean matches(String keyword) {
        String key = keyword.toLowerCase();
        return super.matches(key)
                || nationality.toLowerCase().contains(key)
                || email.toLowerCase().contains(key)
                || getRole().toLowerCase().contains(key);
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String header() {
        return String.format("%-8s %-25s %-12s %-14s %-18s %-12s %-26s %s",
                "Mã", "Họ tên", "SĐT", "CCCD", "Loại", "Quốc tịch", "Email", "Ghi chú");
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" %-12s %-26s", nationality, email);
    }
}
