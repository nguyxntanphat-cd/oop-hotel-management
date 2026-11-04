
package hotel.model;

import hotel.util.FileHelper;

public class DeluxeRoom extends Room {

    public static final String TYPE = "DELUXE";
    public static final double BASE_PRICE = 1000000;
    public static final double BATHTUB_SURCHARGE = 200000;
    public static final double BALCONY_SURCHARGE = 150000;

    private static int createdCount = 0;

    private boolean hasBathtub;
    private boolean hasBalcony;

    public DeluxeRoom() {
        super();
        this.hasBathtub = false;
        this.hasBalcony = false;
        createdCount++;
    }

    public DeluxeRoom(String roomNumber, int floor, RoomStatus status,
                      boolean hasBathtub, boolean hasBalcony) {
        super(roomNumber, floor, status);
        this.hasBathtub = hasBathtub;
        this.hasBalcony = hasBalcony;
        createdCount++;
    }

    public DeluxeRoom(DeluxeRoom other) {
        super(other);
        this.hasBathtub = other.hasBathtub;
        this.hasBalcony = other.hasBalcony;
        createdCount++;
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    public boolean isHasBathtub() {
        return hasBathtub;
    }

    public void setHasBathtub(boolean hasBathtub) {
        this.hasBathtub = hasBathtub;
    }

    public boolean isHasBalcony() {
        return hasBalcony;
    }

    public void setHasBalcony(boolean hasBalcony) {
        this.hasBalcony = hasBalcony;
    }

    @Override
    public double getPricePerNight() {
        double price = BASE_PRICE;

        if (hasBathtub) {
            price += BATHTUB_SURCHARGE;
        }

        if (hasBalcony) {
            price += BALCONY_SURCHARGE;
        }

        return price;
    }

    @Override
    public String getRoomType() {
        return "Deluxe";
    }

    @Override
    public int getMaxGuests() {
        return 3;
    }

    @Override
    public String toFileString() {
        return FileHelper.join(
                TYPE,
                getRoomNumber(),
                getFloor(),
                getStatus(),
                hasBathtub,
                hasBalcony
        );
    }
}
