package hotel.menu;

import hotel.HotelData;
import hotel.list.RoomList;
import hotel.model.DeluxeRoom;
import hotel.model.Room;
import hotel.model.RoomStatus;
import hotel.model.StandardRoom;
import hotel.model.SuiteRoom;
import hotel.util.FileHelper;
import hotel.util.InputHelper;

import java.util.List;

public class RoomMenu extends CrudMenu<Room> {
    private static final String BACK = "-1";
    private static final String TYPES = "1. Standard   2. Deluxe   3. Suite";

    private final RoomList roomList;
    private final RoomList deletedRooms;   // phòng đã xóa, lưu trong rooms_deleted.txt
    private Room lastFound;                // phòng vừa được chọn ở askExisting

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

    // ==================== HÀM DÙNG CHUNG ====================

    private int getFloor(String number) {
        return Integer.parseInt(number) / 100;
    }

    private void printBack() {
        System.out.println(BACK + ". Quay lại");
    }

    private boolean askYesNo(String question) {
        while (true) {
            String s = InputHelper.readString(question + " (y/n): ").trim().toLowerCase();
            if (s.equals("y")) return true;
            if (s.equals("n")) return false;
            System.out.println("Vui lòng chỉ nhập y hoặc n.");
        }
    }

    // Nhập số nguyên min..max; allowBack = true thì nhập -1 để quay lại (trả về null)
    private Integer askNumber(String prompt, int min, int max, boolean allowBack) {
        if (allowBack) printBack();
        while (true) {
            String text = InputHelper.readString(prompt + ": ").trim();
            if (allowBack && text.equals(BACK)) return null;
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException e) {
                // nhập chữ thì báo lỗi bên dưới
            }
            System.out.println("Vui lòng nhập số từ " + min + " đến " + max + ".");
        }
    }

    // Nhập giá tiền > 0; allowBack = true thì nhập -1 để quay lại (trả về null)
    private Double askPrice(String prompt, boolean allowBack) {
        if (allowBack) printBack();
        while (true) {
            String text = InputHelper.readString(prompt + ": ").trim();
            if (allowBack && text.equals(BACK)) return null;
            try {
                double value = Double.parseDouble(text);
                if (value > 0) return value;
            } catch (NumberFormatException e) {
                // nhập chữ thì báo lỗi bên dưới
            }
            System.out.println("Vui lòng nhập giá lớn hơn 0.");
        }
    }

    // Ghi đè hàm của CrudMenu (Sửa và Xóa dùng): nhập sai thì hỏi lại, nhập -1 để quay lại
    @Override
    protected Room askExisting(String prompt) {
        printBack();
        while (true) {
            String number = InputHelper.readString(prompt).trim();
            if (number.equals(BACK)) return null;
            lastFound = roomList.findById(number);
            if (lastFound != null) return lastFound;
            System.out.println("Không tìm thấy phòng " + number + ", vui lòng nhập lại.");
        }
    }

    // Số phòng gồm 3-4 chữ số, tầng = số phòng / 100 (tối đa 50), không trùng
    private String askNewRoomNumber(String currentId, boolean allowBack) {
        if (allowBack) printBack();
        while (true) {
            String number = InputHelper.readString("Số phòng (3-4 chữ số): ").trim();
            if (allowBack && number.equals(BACK)) return null;
            if (!number.matches("[1-9][0-9]{2,3}") || getFloor(number) > 50) {
                System.out.println("Số phòng gồm 3-4 chữ số, không bắt đầu bằng 0, tầng tối đa 50.");
            } else if (!number.equals(currentId) && roomList.exists(number)) {
                System.out.println("Số phòng đã tồn tại.");
            } else {
                return number;
            }
        }
    }

    // Phòng đã đặt, đang có khách hoặc đang có booking thì không được sửa / xóa
    private boolean isLocked(Room room) {
        boolean locked = room.getStatus() == RoomStatus.RESERVED
                || room.getStatus() == RoomStatus.OCCUPIED
                || HotelData.isRoomInUse(room.getId());
        if (locked) {
            System.out.println("Phòng " + room.getId()
                    + " đã được đặt hoặc đang có khách ở, không thể sửa hoặc xóa!");
        }
        return locked;
    }

    // Khi đang sửa: chưa đổi thì hiện giá trị cũ, đổi rồi thì hiện "cũ -> mới (chưa lưu)"
    private String changeText(String oldValue, String newValue) {
        return oldValue.equals(newValue) ? oldValue : oldValue + " -> " + newValue + " (chưa lưu)";
    }

    private String yesNo(boolean value) {
        return value ? "Có" : "Không";
    }

    private String bathtubText(Room r) {
        return r instanceof DeluxeRoom ? yesNo(((DeluxeRoom) r).isHasBathtub()) : "-";
    }

    private String balconyText(Room r) {
        return r instanceof DeluxeRoom ? yesNo(((DeluxeRoom) r).isHasBalcony()) : "-";
    }

    private String bedroomsText(Room r) {
        return r instanceof SuiteRoom ? String.valueOf(((SuiteRoom) r).getNumberOfBedrooms()) : "-";
    }

    // ==================== THÊM (CrudMenu.doAdd gọi inputNew) ====================

    @Override
    protected Room inputNew() {
        String number = askNewRoomNumber(null, true);
        if (number == null) return null;

        int floor = getFloor(number);
        System.out.println("Tầng: " + floor);
        System.out.println(TYPES);
        int type = InputHelper.readInt("Chọn loại phòng (1-3): ", 1, 3);
        return createRoom(type, number, floor, RoomStatus.AVAILABLE);
    }

    private Room createRoom(int type, String number, int floor, RoomStatus status) {
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

    // ==================== SỬA ====================

    // Phải viết lại doEdit vì có đổi số phòng (update của CrudMenu chỉ đổi theo mã cũ)
    @Override
    protected void doEdit() {
        Room old = askExisting("Nhập số phòng cần sửa: ");
        if (old == null || isLocked(old)) return;

        System.out.println(old);
        Room edited = inputEdit(old);
        if (edited == null) {
            System.out.println("Đã hủy sửa.");
            return;
        }

        boolean ok;
        if (edited.getId().equals(old.getId())) {
            ok = roomList.update(old.getId(), edited);
        } else {                                    // đổi số phòng: xóa phòng cũ rồi thêm phòng mới
            roomList.remove(old.getId());
            ok = roomList.add(edited);
            if (!ok) roomList.add(old);             // lỗi thì trả lại phòng cũ
        }
        roomList.saveToFile();
        System.out.println(ok ? "Cập nhật thành công." : "Cập nhật thất bại.");
    }

    // Sửa trên BẢN SAO: chọn 0 mới trả bản sao về để lưu, chọn -1 là hủy
    @Override
    protected Room inputEdit(Room old) {
        Room room = copyOf(old);
        while (true) {
            System.out.println("\nChọn thông tin muốn sửa:");
            System.out.println("1. Số phòng: " + changeText(old.getId(), room.getId()));
            System.out.println("2. Loại phòng: " + changeText(old.getRoomType(), room.getRoomType()));

            int last = 3;                           // mục cuối cùng luôn là Trạng thái
            if (room instanceof DeluxeRoom) {
                System.out.println("3. Bồn tắm: " + changeText(bathtubText(old), bathtubText(room)));
                System.out.println("4. Ban công: " + changeText(balconyText(old), balconyText(room)));
                last = 5;
            } else if (room instanceof SuiteRoom) {
                System.out.println("3. Số phòng ngủ: " + changeText(bedroomsText(old), bedroomsText(room)));
                last = 4;
            }
            System.out.println(last + ". Trạng thái: " + changeText(
                    old.getStatus().getDisplayName(), room.getStatus().getDisplayName()));
            System.out.println("Tầng: " + room.getFloor() + "   Giá/đêm: " + changeText(
                    FileHelper.formatMoney(old.getPricePerNight()),
                    FileHelper.formatMoney(room.getPricePerNight())));
            System.out.println("0. Lưu thay đổi");
            System.out.println("-1. Hủy, không lưu");

            int choice = askNumber("Chọn", -1, last, false);
            if (choice == -1) return null;
            if (choice == 0) return room;

            if (choice == 1) {
                room = withNumber(room, askNewRoomNumber(old.getId(), false));
            } else if (choice == 2) {
                room = changeType(room);
            } else if (choice == last) {
                changeRoomStatus(room);
            } else if (room instanceof DeluxeRoom) {
                DeluxeRoom d = (DeluxeRoom) room;
                if (choice == 3) {
                    d.setHasBathtub(askYesNo("Phòng có bồn tắm không?"));
                } else {
                    d.setHasBalcony(askYesNo("Phòng có ban công không?"));
                }
            } else {
                ((SuiteRoom) room).setNumberOfBedrooms(InputHelper.readInt("Số phòng ngủ mới (1-5): ", 1, 5));
            }
        }
    }

    // Trạng thái chỉ đổi: Trống -> Bảo trì, Bảo trì / Đang dọn -> Trống (hỏi y/n)
    private void changeRoomStatus(Room room) {
        RoomStatus current = room.getStatus();
        boolean toMaintenance = current == RoomStatus.AVAILABLE;

        System.out.println("Trạng thái hiện tại: " + current.getDisplayName());
        String question = toMaintenance
                ? "Bạn có muốn chuyển phòng sang Bảo trì không?"
                : "Phòng đã sẵn sàng đón khách, chuyển sang Trống?";

        if (askYesNo(question)) {
            room.setStatus(toMaintenance ? RoomStatus.MAINTENANCE : RoomStatus.AVAILABLE);
            System.out.println("Đã chọn trạng thái: " + room.getStatus().getDisplayName() + ".");
        } else {
            System.out.println("Đã hủy thay đổi.");
        }
    }

    private Room changeType(Room room) {
        System.out.println(TYPES);
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
            return new DeluxeRoom(number, floor, d.getStatus(), d.isHasBathtub(), d.isHasBalcony());
        }
        if (room instanceof SuiteRoom) {
            SuiteRoom s = (SuiteRoom) room;
            return new SuiteRoom(number, floor, s.getStatus(), s.getNumberOfBedrooms());
        }
        return new StandardRoom(number, floor, room.getStatus());
    }

    // ==================== XÓA ====================

    @Override
    protected boolean canRemove(Room room) {
        return !isLocked(room);
    }

    // Dùng lại CrudMenu.doRemove (hỏi mã, xác nhận, xóa, lưu), chỉ thêm bước lưu vào danh sách đã xóa
    @Override
    protected void doRemove() {
        lastFound = null;
        super.doRemove();

        if (lastFound != null && !roomList.exists(lastFound.getId())) {   // đã xóa thật
            deletedRooms.remove(lastFound.getId());     // nếu từng xóa số phòng này thì ghi đè
            deletedRooms.add(lastFound);
            deletedRooms.saveToFile();
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
        System.out.println(TYPES);
        Integer type = askNumber("Chọn loại phòng", 1, 3, true);
        if (type != null) showResult(roomList.findByType(names[type - 1]));
    }

    private void filterByStatus() {
        RoomStatus[] all = RoomStatus.values();
        for (int i = 0; i < all.length; i++) {
            System.out.println((i + 1) + ". " + all[i].getDisplayName());
        }
        Integer choice = askNumber("Chọn trạng thái phòng", 1, all.length, true);
        if (choice != null) showResult(roomList.findByStatus(all[choice - 1]));
    }

    private void filterByPrice() {
        Double min = askPrice("Nhập giá thấp nhất", true);
        if (min == null) return;

        Double max = askPrice("Nhập giá cao nhất", false);
        while (max <= min) {
            System.out.println("Giá cao nhất phải lớn hơn giá thấp nhất.");
            max = askPrice("Nhập lại giá cao nhất", false);
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