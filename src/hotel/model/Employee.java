package hotel.model;

import hotel.util.FileHelper;

import java.time.LocalDate;

/**
 * Lớp trừu tượng biểu diễn Nhân viên trong khách sạn, kế thừa từ Person.
 */
public abstract class Employee extends Person {
    public static final String ID_PREFIX = "NV";
    private static int createdCount = 0;

    protected double baseSalary;
    protected LocalDate startDate;

    public Employee() {
        super();
        this.baseSalary = 0.0;
        this.startDate = LocalDate.now();
    }

    public Employee(String id, String fullName, String phone, String idCard,
                    double baseSalary, LocalDate startDate) {
        super(id, fullName, phone, idCard);
        this.baseSalary = baseSalary;
        this.startDate = startDate;
        createdCount++;
    }

    public Employee(Employee other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.baseSalary, other.startDate);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    /**
     * Tính thực lĩnh của nhân viên (đa hình ở các lớp con).
     */
    public abstract double calculateSalary();

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public static String header() {
        return String.format("%-8s %-22s %-11s %-13s %-12s %14s %-12s %14s   %s",
                "Mã NV", "Họ tên", "SĐT", "CCCD", "Chức vụ", "Lương CB", "Ngày vào", "Thực lĩnh", "Thông tin riêng");
    }

    @Override
    public String toString() {
        return String.format("%-8s %-22s %-11s %-13s %-12s %14s %-12s %14s",
                id, fullName, phone, idCard, getRole(),
                FileHelper.formatMoney(baseSalary),
                FileHelper.formatDate(startDate),
                FileHelper.formatMoney(calculateSalary()));
    }
}