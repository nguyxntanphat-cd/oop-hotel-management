
package hotel.model;

import hotel.util.FileHelper;

public class SuiteRoom extends Room {

    public static final String TYPE = "SUITE";
    public static final double BASE_PRICE = 2000000;
    public static final double PRICE_PER_BEDROOM = 500000;

    private static int createdCount = 0;

    private int numberOfBedrooms;

    public SuiteRoom() {
        super();
        this.numberOfBedrooms = 1;
        createdCount++;
    }

    public SuiteRoom(String roomNumber, int floor, RoomStatus status,
                     int numberOfBedrooms) {
        super(roomNumber, floor, status);
        this.numberOfBedrooms = numberOfBedrooms;
        createdCount++;
    }

    public SuiteRoom(SuiteRoom other) {
        super(other);
        this.numberOfBedrooms = other.numberOfBedrooms;
        createdCount++;
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }

    // Giá tăng theo số phòng ngủ, phòng ngủ đầu tiên đã nằm trong giá cơ bản
    @Override
    public double getPricePerNight() {
        return BASE_PRICE + (numberOfBedrooms - 1) * PRICE_PER_BEDROOM;
    }

    @Override
    public String getRoomType() {
        return "Suite";
    }

    @Override
    public int getMaxGuests() {
        return 4;
    }

    @Override
    public String toFileString() {
        // SUITE|301|3|AVAILABLE|2
        return FileHelper.join(
                TYPE,
                roomNumber,
                floor,
                status,
                numberOfBedrooms
        );
    }
}
