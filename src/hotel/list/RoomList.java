
package hotel.list;

import hotel.common.BaseList;
import hotel.model.DeluxeRoom;
import hotel.model.Room;
import hotel.model.RoomStatus;
import hotel.model.StandardRoom;
import hotel.model.SuiteRoom;
import hotel.util.FileHelper;

import java.util.List;

public class RoomList extends BaseList<Room> {

    public RoomList() {
        super();
    }

    @Override
    protected String getFileName() {
        return "rooms.txt";
    }

    // Đọc 1 dòng trong rooms.txt, tạo đúng loại phòng
    // dựa vào trường đầu tiên
    @Override
    protected Room parseLine(String line) {
        String[] p = FileHelper.split(line);

        try {
            if (p.length < 4) {
                return null;
            }

            String roomNumber = p[1];
            int floor = Integer.parseInt(p[2]);
            RoomStatus status = RoomStatus.valueOf(p[3]);

            switch (p[0]) {
                case StandardRoom.TYPE:
                    // STANDARD|101|1|AVAILABLE
                    if (p.length != 4) {
                        return null;
                    }
                    return new StandardRoom(roomNumber, floor, status);

                case DeluxeRoom.TYPE:
                    // DELUXE|201|2|OCCUPIED|true|false
                    if (p.length != 6) {
                        return null;
                    }

                    if (!isBoolean(p[4]) || !isBoolean(p[5])) {
                        return null;
                    }

                    return new DeluxeRoom(
                            roomNumber,
                            floor,
                            status,
                            Boolean.parseBoolean(p[4]),
                            Boolean.parseBoolean(p[5])
                    );

                case SuiteRoom.TYPE:
                    // SUITE|301|3|AVAILABLE|2
                    if (p.length != 5) {
                        return null;
                    }

                    int bedrooms = Integer.parseInt(p[4]);

                    return new SuiteRoom(
                            roomNumber,
                            floor,
                            status,
                            bedrooms
                    );

                default:
                    return null;
            }

        } catch (IllegalArgumentException e) {
            // Sai số hoặc sai tên trạng thái
            return null;
        }
    }

    private boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value)
                || "false".equalsIgnoreCase(value);
    }

    @Override
    protected String getHeader() {
        return Room.header();
    }

    // Các hàm theo hợp đồng trong THIET_KE.md mục 3.1

    public List<Room> findByStatus(RoomStatus status) {
        return filter(r -> r.getStatus() == status);
    }

    public List<Room> findByType(String roomType) {
        return filter(r -> r.getRoomType().equalsIgnoreCase(roomType));
    }

    public List<Room> findByPriceRange(double min, double max) {
        return filter(r -> r.getPricePerNight() >= min
                && r.getPricePerNight() <= max);
    }

    public List<Room> getAvailableRooms() {
        return filter(r -> r.isAvailable());
    }
}
