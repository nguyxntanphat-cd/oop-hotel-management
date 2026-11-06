
package hotel.menu;

import hotel.HotelData;
import hotel.list.RoomList;
import hotel.model.*;
import hotel.util.FileHelper;
import hotel.util.InputHelper;

import java.util.List;

public class RoomMenu extends CrudMenu<Room> {

    private static final String BACK = "-1";
    private final RoomList roomList;
    private final RoomList deletedRooms;

    private static class DeletedRoomList extends RoomList {
        @Override
        protected String getFileName() {
            return "rooms_deleted.txt";
        }
    }

    public RoomMenu(RoomList list) {
        super("QUẢN LÝ PHÒNG", list);
        roomList = list;
        deletedRooms = new DeletedRoomList();
        deletedRooms.loadFromFile();
    }

    private int getFloor(String number) {
        return Integer.parseInt(number) / 100;
    }

    private void printBack() {
        System.out.println(BACK + ". Quay lại");
    }

    private boolean askYesNo(String question) {
        while (true) {
            String s = InputHelper.readString(question + " (y/n): ")
                    .trim().toLowerCase();

            if (s.equals("y")) return true;
            if (s.equals("n")) return false;

            System.out.println("Vui lòng chỉ nhập y hoặc n.");
        }
    }

    // Nhập số, có thể cho phép quay lại bằng -1
    private Integer askNumber(String prompt, int min, int max,
                              boolean allowBack) {
        if (allowBack) printBack();

        while (true) {
            String text = InputHelper.readString(prompt + ": ").trim();

            if (allowBack && text.equals(BACK)) return null;

            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException e) {
                // Nhập sai
            }

            System.out.println("Vui lòng nhập số từ " + min + " đến " + max + ".");
        }
    }

    private Room askExistingRoom(String prompt) {
        printBack();

        while (true) {
            String number = InputHelper.readString(prompt + ": ").trim();
            if (number.equals(BACK)) return null;

            Room room = roomList.findById(number);
            if (room != null) return room;

            System.out.println("Không tìm thấy phòng " + number + ".");
        }
    }

    private String askNewRoomNumber(String currentId, boolean allowBack) {
        if (allowBack) printBack();

        while (true) {
            String prompt = allowBack
                    ? "Số phòng (chỉ nhập số): "
                    : "Số phòng mới (chỉ nhập số): ";

            String number = InputHelper.readString(prompt).trim();

            if (allowBack && number.equals(BACK)) return null;

            if (!number.matches("[1-9][0-9]*")) {
                System.out.println("Số phòng chỉ gồm chữ số và không bắt đầu bằng 0.");
                continue;
            }

            try {
                int floor = getFloor(number);

                if (floor < 1 || floor > 50) {
                    System.out.println("Số phòng phải thuộc tầng từ 1 đến 50.");
                } else if (!number.equals(currentId) && roomList.exists(number)) {
                    System.out.println("Số phòng đã tồn tại.");
                } else {
                    return number;
                }
            } catch (NumberFormatException e) {
                System.out.println("Số phòng không hợp lệ.");
            }
        }
    }

    // Kiểm tra trạng thái phòng và booking
    private boolean isRoomLocked(Room room) {
        if (room == null) return false;

        boolean locked = room.getStatus() == RoomStatus.RESERVED
                || room.getStatus() == RoomStatus.OCCUPIED
                || HotelData.isRoomInUse(room.getId());

        if (locked) {
            System.out.println("Phòng " + room.getId()
                    + " đã được đặt hoặc đang có khách ở, không thể sửa hoặc xóa!");
        }

        return locked;
    }

    private String changeText(String oldValue, String newValue) {
        return oldValue.equals(newValue)
                ? oldValue : oldValue + " -> " + newValue + " (chưa lưu)";
    }

    // Hiển thị thông tin riêng của từng loại phòng
    private String bathtubText(Room room) {
        if (!(room instanceof DeluxeRoom)) return "-";
        return ((DeluxeRoom) room).isHasBathtub() ? "Có" : "Không";
    }

    private String balconyText(Room room) {
        if (!(room instanceof DeluxeRoom)) return "-";
        return ((DeluxeRoom) room).isHasBalcony() ? "Có" : "Không";
    }

    private String bedroomsText(Room room) {
        if (!(room instanceof SuiteRoom)) return "-";
        return String.valueOf(((SuiteRoom) room).getNumberOfBedrooms());
    }

    // ==================== THÊM PHÒNG ====================

    @Override
    protected Room inputNew() {
        String number = askNewRoomNumber(null, true);
        if (number == null) return null;

        int floor = getFloor(number);

        System.out.println("Số phòng: " + number);
        System.out.println("Tầng: " + floor);
        System.out.println("1. Standard   2. Deluxe   3. Suite");

        int type = InputHelper.readInt("Chọn loại phòng (1-3): ", 1, 3);
        return createRoom(type, number, floor, RoomStatus.AVAILABLE);
    }

    private Room createRoom(int type, String number, int floor,
                            RoomStatus status) {
        switch (type) {
            case 1:
                return new StandardRoom(number, floor, status);
            case 2:
                return new DeluxeRoom(number, floor, status,
                        askYesNo("Phòng có bồn tắm không?"),
                        askYesNo("Phòng có ban công không?"));
            default:
                return new SuiteRoom(number, floor, status,
                        InputHelper.readInt("Số phòng ngủ (1-5): ", 1, 5));
        }
    }

    @Override
    protected void doAdd() {
        Room room = inputNew();

        if (room == null) {
            System.out.println("Đã hủy thao tác thêm phòng.");
            return;
        }

        if (roomList.add(room)) {
            roomList.saveToFile();
            System.out.println("Thêm phòng thành công: " + room.getId());
        } else {
            System.out.println("Thêm phòng thất bại: số phòng đã tồn tại.");
        }
    }

    // ==================== SỬA PHÒNG ====================

    @Override
    protected void doEdit() {
        Room old = askExistingRoom("Nhập số phòng cần sửa");
        if (old == null || isRoomLocked(old)) return;

        System.out.println(old);
        Room edited = inputEdit(old);

        if (edited == null) {
            System.out.println("Đã hủy thao tác sửa phòng.");
            return;
        }

        if (edited.getId().equals(old.getId())) {
            if (roomList.update(old.getId(), edited)) {
                roomList.saveToFile();
                System.out.println("Cập nhật phòng thành công.");
            } else {
                System.out.println("Cập nhật phòng thất bại.");
            }
            return;
        }

        if (!roomList.remove(old.getId())) {
            System.out.println("Không thể cập nhật phòng.");
            return;
        }

        if (roomList.add(edited)) {
            roomList.saveToFile();
            System.out.println("Đổi số phòng thành công: " + edited.getId());
        } else {
            roomList.add(old);
            roomList.saveToFile();
            System.out.println("Cập nhật thất bại. Đã khôi phục phòng cũ.");
        }
    }

    @Override
    protected Room inputEdit(Room old) {
        Room room = copyOf(old);

        while (true) {
            System.out.println("\nChọn thông tin muốn sửa:");
            System.out.println("1. Số phòng: "
                    + changeText(old.getId(), room.getId()));
            System.out.println("2. Loại phòng: "
                    + changeText(old.getRoomType(), room.getRoomType()));

            int max = 3;

            if (old instanceof DeluxeRoom || room instanceof DeluxeRoom) {
                System.out.println("3. Bồn tắm: "
                        + changeText(bathtubText(old), bathtubText(room)));
                System.out.println("4. Ban công: "
                        + changeText(balconyText(old), balconyText(room)));
                System.out.println("5. Trạng thái: "
                        + changeText(old.getStatus().getDisplayName(),
                        room.getStatus().getDisplayName()));
                max = 5;
            } else if (old instanceof SuiteRoom || room instanceof SuiteRoom) {
                System.out.println("3. Số phòng ngủ: "
                        + changeText(bedroomsText(old), bedroomsText(room)));
                System.out.println("4. Trạng thái: "
                        + changeText(old.getStatus().getDisplayName(),
                        room.getStatus().getDisplayName()));
                max = 4;
            } else {
                System.out.println("3. Trạng thái: "
                        + changeText(old.getStatus().getDisplayName(),
                        room.getStatus().getDisplayName()));
            }

            System.out.println("Tầng: " + room.getFloor());
            System.out.println("Giá/đêm: "
                    + changeText(FileHelper.formatMoney(old.getPricePerNight()),
                    FileHelper.formatMoney(room.getPricePerNight())));
            System.out.println("0. Lưu thay đổi");
            System.out.println("-1. Hủy");

            int choice = askNumber("Chọn", -1, max, false);

            if (choice == -1) return null;
            if (choice == 0) return room;

            if (choice == 1) {
                room = withNumber(room,
                        askNewRoomNumber(old.getId(), false));
            } else if (choice == 2) {
                room = changeType(room);
            } else if (choice == 3 && room instanceof DeluxeRoom) {
                ((DeluxeRoom) room).setHasBathtub(
                        askYesNo("Phòng có bồn tắm không?"));
            } else if (choice == 4 && room instanceof DeluxeRoom) {
                ((DeluxeRoom) room).setHasBalcony(
                        askYesNo("Phòng có ban công không?"));
            } else if (choice == 3 && room instanceof SuiteRoom) {
                ((SuiteRoom) room).setNumberOfBedrooms(
                        InputHelper.readInt("Số phòng ngủ mới (1-5): ", 1, 5));
            } else {
                changeRoomStatus(room);
            }
        }
    }

    private void changeRoomStatus(Room room) {
        RoomStatus currentStatus = room.getStatus();

        System.out.println("\n--- SỬA TRẠNG THÁI PHÒNG ---");
        System.out.println("Trạng thái hiện tại: " + currentStatus);

        if (currentStatus == RoomStatus.AVAILABLE) {
            System.out.println("Bạn có muốn chuyển trạng thái sang BẢO TRÌ không?");
            if (askYesNo("Xác nhận chuyển sang bảo trì?")) {
                room.setStatus(RoomStatus.MAINTENANCE);
                System.out.println("Đã chuyển phòng sang trạng thái BẢO TRÌ.");
            } else {
                System.out.println("Đã hủy thay đổi.");
            }

        } else if (currentStatus == RoomStatus.CLEANING
                || currentStatus == RoomStatus.MAINTENANCE) {

            System.out.println("Bạn có muốn chuyển trạng thái sang TRỐNG không?");
            if (askYesNo("Xác nhận chuyển sang trống?")) {
                room.setStatus(RoomStatus.AVAILABLE);
                System.out.println("Đã chuyển phòng sang trạng thái TRỐNG.");
            } else {
                System.out.println("Đã hủy thay đổi.");
            }

        } else {
            System.out.println("Không thể sửa trạng thái phòng đang "
                    + currentStatus + ".");
        }
    }
    private Room changeType(Room room) {
        System.out.println("1. Standard   2. Deluxe   3. Suite");
        int type = InputHelper.readInt("Chọn loại phòng mới (1-3): ", 1, 3);

        if ((type == 1 && room instanceof StandardRoom)
                || (type == 2 && room instanceof DeluxeRoom)
                || (type == 3 && room instanceof SuiteRoom)) {
            System.out.println("Phòng đã thuộc loại này.");
            return room;
        }

        return createRoom(type, room.getId(), room.getFloor(), room.getStatus());
    }

    private Room copyOf(Room room) {
        if (room instanceof DeluxeRoom) return new DeluxeRoom((DeluxeRoom) room);
        if (room instanceof SuiteRoom) return new SuiteRoom((SuiteRoom) room);
        return new StandardRoom((StandardRoom) room);
    }

    private Room withNumber(Room room, String number) {
        int floor = getFloor(number);

        if (room instanceof DeluxeRoom) {
            DeluxeRoom d = (DeluxeRoom) room;
            return new DeluxeRoom(number, floor, d.getStatus(),
                    d.isHasBathtub(), d.isHasBalcony());
        }

        if (room instanceof SuiteRoom) {
            SuiteRoom s = (SuiteRoom) room;
            return new SuiteRoom(number, floor, s.getStatus(),
                    s.getNumberOfBedrooms());
        }

        return new StandardRoom(number, floor, room.getStatus());
    }

    // ==================== XÓA PHÒNG ====================

    @Override
    protected void doRemove() {
        Room room = askExistingRoom("Nhập số phòng cần xóa");
        if (room == null) return;

        System.out.println(room);
        if (!canRemove(room)) return;

        if (!askYesNo("Bạn có chắc chắn muốn xóa phòng này không?")) {
            System.out.println("Đã hủy thao tác xóa.");
            return;
        }

        deletedRooms.remove(room.getId());

        if (!deletedRooms.add(room)) {
            System.out.println("Không thể lưu phòng vào danh sách đã xóa.");
            return;
        }

        if (!roomList.remove(room.getId())) {
            deletedRooms.remove(room.getId());
            System.out.println("Xóa phòng thất bại.");
            return;
        }

        roomList.saveToFile();
        deletedRooms.saveToFile();
        System.out.println("Đã xóa phòng " + room.getId());
    }

    @Override
    protected boolean canRemove(Room room) {
        return !isRoomLocked(room);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    protected void doSearch() {
        printBack();
        String keyword = InputHelper.readLine("Nhập số phòng: ").trim();

        if (!keyword.equals(BACK)) {
            showResult(roomList.search(keyword));
        }
    }

    // ==================== LỌC VÀ CHỨC NĂNG RIÊNG ====================

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
                "Lọc phòng theo loại",
                "Lọc phòng theo trạng thái",
                "Lọc phòng theo giá",
                "Xem phòng đã xóa"
        };
    }

    @Override
    protected void handleExtra(int index) {
        switch (index) {
            case 0: filterByType(); break;
            case 1: filterByStatus(); break;
            case 2: filterByPrice(); break;
            case 3: viewDeleted(); break;
            default: break;
        }
    }

    private void showResult(List<Room> result) {
        if (result.isEmpty()) {
            System.out.println("Không tìm thấy phòng phù hợp.");
        } else {
            roomList.display(result);
        }
    }

    private void filterByType() {
        String[] names = {"Standard", "Deluxe", "Suite"};
        System.out.println("1. Standard   2. Deluxe   3. Suite");

        Integer type = askNumber("Chọn loại phòng", 1, 3, true);
        if (type != null) showResult(roomList.findByType(names[type - 1]));
    }

    private void filterByStatus() {
        RoomStatus[] statuses = RoomStatus.values();

        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". "
                    + statuses[i].getDisplayName());
        }

        Integer choice = askNumber("Chọn trạng thái phòng",
                1, statuses.length, true);

        if (choice != null) {
            showResult(roomList.findByStatus(statuses[choice - 1]));
        }
    }

    private void filterByPrice() {
        printBack();
        double min;

        while (true) {
            String text = InputHelper.readString("Nhập giá thấp nhất: ").trim();
            if (text.equals(BACK)) return;

            try {
                min = Double.parseDouble(text);
                if (min > 0) break;
            } catch (NumberFormatException e) {
                // Nhập sai
            }

            System.out.println("Vui lòng nhập giá lớn hơn 0.");
        }

        double max = InputHelper.readPositiveDouble("Nhập giá cao nhất: ");

        while (min >= max) {
            System.out.println("Giá cao nhất phải lớn hơn giá thấp nhất.");
            max = InputHelper.readPositiveDouble("Nhập lại giá cao nhất: ");
        }

        showResult(roomList.findByPriceRange(min, max));
    }

    private void viewDeleted() {
        if (deletedRooms.size() == 0) {
            System.out.println("Chưa có phòng nào bị xóa.");
        } else {
            deletedRooms.displayAll();
        }
    }

    @Override
    protected void onExit() {
        super.onExit();
        deletedRooms.saveToFile();
        System.out.println("Đã lưu thay đổi.");
    }
}