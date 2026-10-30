package hotel.model;

import hotel.util.FileHelper;

import java.time.LocalDate;

/**
 * Nhân viên Tạp vụ (Buồng phòng): tính lương theo số phòng đã dọn.
 */
public class Housekeeper extends Employee {
    public static final String TYPE = "HOUSEKEEPER";
    public static final double BONUS_PER_ROOM = 25000.0;

    private static int createdCount = 0;

    private int roomsCleaned;

    public Housekeeper() {
        super();
        this.roomsCleaned = 0;
    }

    public Housekeeper(String id, String fullName, String phone, String idCard,
                       double baseSalary, LocalDate startDate,
                       int roomsCleaned) {
        super(id, fullName, phone, idCard, baseSalary, startDate);
        this.roomsCleaned = roomsCleaned;
        createdCount++;
    }

    public Housekeeper(Housekeeper other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.baseSalary, other.startDate,
                other.roomsCleaned);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getRole() {
        return "Tạp vụ";
    }

    @Override
    public double calculateSalary() {
        return baseSalary + (roomsCleaned * BONUS_PER_ROOM);
    }

    @Override
    public String toFileString() {
        return FileHelper.join(
                TYPE, id, fullName, phone, idCard,
                baseSalary, FileHelper.formatDate(startDate),
                roomsCleaned
        );
    }

    @Override
    public boolean matches(String keyword) {
        String key = keyword.toLowerCase();
        return super.matches(key) || getRole().toLowerCase().contains(key);
    }

    public int getRoomsCleaned() {
        return roomsCleaned;
    }

    public void setRoomsCleaned(int roomsCleaned) {
        this.roomsCleaned = roomsCleaned;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("   Số phòng đã dọn: %d", roomsCleaned);
    }
}
