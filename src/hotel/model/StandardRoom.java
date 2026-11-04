
package hotel.model;

import hotel.util.FileHelper;

public class StandardRoom extends Room {

    public static final String TYPE = "STANDARD";
    public static final double BASE_PRICE = 500000;

    private static int createdCount = 0;

    public StandardRoom() {
        super();
        createdCount++;
    }

    public StandardRoom(String roomNumber, int floor, RoomStatus status) {
        super(roomNumber, floor, status);
        createdCount++;
    }

    public StandardRoom(StandardRoom other) {
        super(other);
        createdCount++;
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public double getPricePerNight() {
        return BASE_PRICE;
    }

    @Override
    public String getRoomType() {
        return "Standard";
    }

    @Override
    public int getMaxGuests() {
        return 2;
    }

    @Override
    public String toFileString() {
        // STANDARD|101|1|AVAILABLE
        return FileHelper.join(TYPE, roomNumber, floor, status);
    }
}
