package hotel.model;

import hotel.util.FileHelper;

public class SuiteRoom extends Room {

    public static final String TYPE = "SUITE";
    public static final double BASE_PRICE = 2000000;
    public static final double PRICE_PER_BEDROOM = 500000;

    private static int createdCount = 0;

    private int numberOfBedrooms;

    // Constructor mặc định
    public SuiteRoom() {
        super();
        this.numberOfBedrooms = 1;
        createdCount++;
    }

    // Constructor đầy đủ
    public SuiteRoom(String roomNumber, int floor, RoomStatus status,
                     int numberOfBedrooms) {
        super(roomNumber, floor, status);
        setNumberOfBedrooms(numberOfBedrooms);
        createdCount++;
    }

    // Copy constructor
    public SuiteRoom(SuiteRoom other) {
        super(other);
        this.numberOfBedrooms = other.numberOfBedrooms;
        createdCount++;
    }

    // Lấy số lượng đối tượng SuiteRoom đã khởi tạo
    public static int getCreatedCount() {
        return createdCount;
    }

    // Getter
    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    // Setter
    public void setNumberOfBedrooms(int numberOfBedrooms) {
        if (numberOfBedrooms < 1 || numberOfBedrooms > 5) {
            throw new IllegalArgumentException(
                    "Số phòng ngủ phải từ 1 đến 5."
            );
        }

        this.numberOfBedrooms = numberOfBedrooms;
    }

    // Tính giá phòng theo số phòng ngủ
    @Override
    public double getPricePerNight() {
        return BASE_PRICE
                + (numberOfBedrooms - 1) * PRICE_PER_BEDROOM;
    }

    // Lấy tên loại phòng
    @Override
    public String getRoomType() {
        return "Suite";
    }

    // Số khách tối đa
    @Override
    public int getMaxGuests() {
        return 4;
    }

    // Chuyển thông tin phòng thành chuỗi để lưu file
    @Override
    public String toFileString() {
        // Định dạng: SUITE|301|3|AVAILABLE|2
        return FileHelper.join(
                TYPE,
                roomNumber,
                floor,
                status,
                numberOfBedrooms
        );
    }
}