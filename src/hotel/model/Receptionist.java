package hotel.model;

import hotel.util.FileHelper;

import java.time.LocalDate;

/**
 * Nhân viên Lễ tân: tính lương theo ca trực và số booking đã xử lý.
 */
public class Receptionist extends Employee {
    public static final String TYPE = "RECEPTIONIST";
    public static final double NIGHT_SHIFT_ALLOWANCE = 500000.0;
    public static final double BONUS_PER_BOOKING = 50000.0;

    private static int createdCount = 0;

    private String shift; // Sáng, Chiều, Đêm
    private int bookingsHandled;

    public Receptionist() {
        super();
        this.shift = "Sáng";
        this.bookingsHandled = 0;
    }

    public Receptionist(String id, String fullName, String phone, String idCard,
                        double baseSalary, LocalDate startDate,
                        String shift, int bookingsHandled) {
        super(id, fullName, phone, idCard, baseSalary, startDate);
        this.shift = shift;
        this.bookingsHandled = bookingsHandled;
        createdCount++;
    }

    public Receptionist(Receptionist other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.baseSalary, other.startDate,
                other.shift, other.bookingsHandled);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getRole() {
        return "Lễ tân";
    }

    @Override
    public double calculateSalary() {
        double nightAllowance = isNightShift() ? NIGHT_SHIFT_ALLOWANCE : 0.0;
        return baseSalary + nightAllowance + (bookingsHandled * BONUS_PER_BOOKING);
    }

    private boolean isNightShift() {
        if (shift == null) {
            return false;
        }
        String s = shift.trim().toLowerCase();
        return s.equals("đêm") || s.equals("dem") || s.equals("night");
    }

    @Override
    public String toFileString() {
        return FileHelper.join(
                TYPE, id, fullName, phone, idCard,
                baseSalary, FileHelper.formatDate(startDate),
                shift, bookingsHandled
        );
    }

    @Override
    public boolean matches(String keyword) {
        String key = keyword.toLowerCase();
        return super.matches(key)
                || getRole().toLowerCase().contains(key)
                || shift.toLowerCase().contains(key);
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getBookingsHandled() {
        return bookingsHandled;
    }

    public void setBookingsHandled(int bookingsHandled) {
        this.bookingsHandled = bookingsHandled;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("   Ca: %-6s | Booking: %d", shift, bookingsHandled);
    }
}