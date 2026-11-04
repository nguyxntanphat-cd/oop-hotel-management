
package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.FileHelper;

public abstract class Room implements Identifiable, FileSerializable, Searchable {

    private static int createdCount = 0;

    protected String roomNumber;
    protected int floor;
    protected RoomStatus status;

    // Constructor mặc định
    protected Room() {
        this("", 0, RoomStatus.AVAILABLE);
    }

    // Constructor đầy đủ
    protected Room(String roomNumber, int floor, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.status = status;
        createdCount++;
    }

    // Copy constructor
    protected Room(Room other) {
        this(other.roomNumber, other.floor, other.status);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getId() {
        return roomNumber;
    }

    public String getRoomNumber() {
        return getId();
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }

    // Các phương thức trừu tượng
    public abstract double getPricePerNight();

    public abstract String getRoomType();

    public abstract int getMaxGuests();

    // 4 trường đầu của mọi dòng trong rooms.txt
    // LOAI|soPhong|tang|trangThai
    protected String baseFileString() {
        return FileHelper.join(
                getRoomType().toUpperCase(),
                roomNumber,
                floor,
                status
        );
    }

    @Override
    public boolean matches(String keyword) {
        return roomNumber.toLowerCase().contains(keyword)
                || getRoomType().toLowerCase().contains(keyword)
                || status.getDisplayName().toLowerCase().contains(keyword);
    }

    public static String header() {
        return String.format(
                "%-10s | %-10s | %-6s | %-12s | %-15s | %-8s",
                "Số phòng", "Loại", "Tầng", "Trạng thái", "Giá/đêm", "Tối đa"
        );
    }

    @Override
    public String toString() {
        return String.format(
                "%-10s | %-10s | %-6d | %-12s | %-15s | %-8d",
                roomNumber,
                getRoomType(),
                floor,
                status.getDisplayName(),
                FileHelper.formatMoney(getPricePerNight()),
                getMaxGuests()
        );
    }
}
