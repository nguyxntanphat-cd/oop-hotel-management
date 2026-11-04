
package hotel.model;

public enum RoomStatus {

    AVAILABLE("Trống"),
    RESERVED("Đã đặt"),
    OCCUPIED("Đang ở"),
    CLEANING("Đang dọn"),
    MAINTENANCE("Bảo trì");

    private final String displayName;

    RoomStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
