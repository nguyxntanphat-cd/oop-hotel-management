package hotel.model;

import hotel.util.FileHelper;

import java.time.LocalDate;

/**
 * Nhân viên Quản lý: tính lương theo hệ số quản lý và phụ cấp.
 */
public class Manager extends Employee {
    public static final String TYPE = "MANAGER";
    public static final double MANAGEMENT_COEFFICIENT = 1.2;

    private static int createdCount = 0;

    private String department;
    private double allowance;

    public Manager() {
        super();
        this.department = "";
        this.allowance = 0.0;
    }

    public Manager(String id, String fullName, String phone, String idCard,
                   double baseSalary, LocalDate startDate,
                   String department, double allowance) {
        super(id, fullName, phone, idCard, baseSalary, startDate);
        this.department = department;
        this.allowance = allowance;
        createdCount++;
    }

    public Manager(Manager other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.baseSalary, other.startDate,
                other.department, other.allowance);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getRole() {
        return "Quản lý";
    }

    @Override
    public double calculateSalary() {
        return (baseSalary * MANAGEMENT_COEFFICIENT) + allowance;
    }

    @Override
    public String toFileString() {
        return FileHelper.join(
                TYPE, id, fullName, phone, idCard,
                baseSalary, FileHelper.formatDate(startDate),
                department, allowance
        );
    }

    @Override
    public boolean matches(String keyword) {
        String key = keyword.toLowerCase();
        return super.matches(key)
                || getRole().toLowerCase().contains(key)
                || department.toLowerCase().contains(key);
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("   PB: %-10s | Phụ cấp: %s",
                department, FileHelper.formatMoney(allowance));
    }
}