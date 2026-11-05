
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
        this.roomList = list;
        this.deletedRooms = new DeletedRoomList();
        this.deletedRooms.loadFromFile();
    }

    // ================= TÍNH TẦNG TỰ ĐỘNG =================

    private int getFloorFromRoomNumber(String number) {
        return Integer.parseInt(number) / 100;
    }

    // ================= HÀM NHẬP LIỆU DÙNG CHUNG =================

    private void printBack() {
        System.out.println(BACK + ". Quay lại");
    }

    private boolean askYesNo(String question) {
        while (true) {
            String s = InputHelper.readString(
                    question + " (y/n): "
            ).trim().toLowerCase();

            if (s.equals("y")) {
                return true;
            }

            if (s.equals("n")) {
                return false;
            }

            System.out.println("Chỉ nhập y hoặc n.");
        }
    }

    private int askChoice(String prompt, int min, int max) {
        while (true) {
            String text = InputHelper.readString(prompt).trim();

            try {
                int value = Integer.parseInt(text);

                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // Nhập sai thì yêu cầu nhập lại
            }

            System.out.println(
                    "Vui lòng nhập số từ " + min + " đến " + max + "."
            );
        }
    }

    private Integer askIntOrBack(String prompt, int min, int max) {
        printBack();

        while (true) {
            String text = InputHelper.readString(
                    prompt + ": "
            ).trim();

            if (text.equals(BACK)) {
                return null;
            }

            try {
                int value = Integer.parseInt(text);

                if (value >= min && value <= max) {
                    return value;
                }

            } catch (NumberFormatException e) {
                // Nhập sai thì yêu cầu nhập lại
            }

            System.out.println(
                    "Vui lòng nhập số từ " + min + " đến " + max + "."
            );
        }
    }

    private Room askExistingRoom(String prompt) {
        printBack();

        while (true) {
            String number = InputHelper.readString(
                    prompt + ": "
            ).trim();

            if (number.equals(BACK)) {
                return null;
            }

            Room room = roomList.findById(number);

            if (room != null) {
                return room;
            }

            System.out.println(
                    "Không có phòng " + number + ", mời nhập lại."
            );
        }
    }

    // ================= NHẬP SỐ PHÒNG =================

    private String askNewRoomNumber(String currentId, boolean allowBack) {

        if (allowBack) {
            printBack();
        }

        while (true) {
            String hint = allowBack
                    ? "Số phòng (chỉ nhập số): "
                    : "Số phòng mới (chỉ nhập số): ";

            String number = InputHelper.readString(hint).trim();

            if (allowBack && number.equals(BACK)) {
                return null;
            }

            if (!number.matches("[1-9][0-9]*")) {
                System.out.println(
                        "Số phòng chỉ gồm chữ số và không bắt đầu bằng 0."
                );
                continue;
            }

            try {
                int floor = getFloorFromRoomNumber(number);

                if (floor < 1 || floor > 50) {
                    System.out.println(
                            "Số phòng phải tương ứng với tầng từ 1 đến 50."
                    );

                } else if (!number.equals(currentId)
                        && roomList.exists(number)) {

                    System.out.println(
                            "Số phòng đã tồn tại, nhập số khác."
                    );

                } else {
                    return number;
                }

            } catch (NumberFormatException e) {
                System.out.println("Số phòng không hợp lệ.");
            }
        }
    }

    private String yesNoText(boolean value) {
        return value ? "có" : "không";
    }

    private String changeText(String oldValue, String newValue) {
        if (oldValue.equals(newValue)) {
            return oldValue;
        }

        return oldValue + " -> " + newValue + " (chưa lưu)";
    }

    // ================= THÔNG TIN RIÊNG TỪNG LOẠI PHÒNG =================

    private String bathtubText(Room r) {
        return (r instanceof DeluxeRoom)
                ? yesNoText(((DeluxeRoom) r).isHasBathtub())
                : "-";
    }

    private String balconyText(Room r) {
        return (r instanceof DeluxeRoom)
                ? yesNoText(((DeluxeRoom) r).isHasBalcony())
                : "-";
    }

    private String bedroomsText(Room r) {
        return (r instanceof SuiteRoom)
                ? String.valueOf(((SuiteRoom) r).getNumberOfBedrooms())
                : "-";
    }

    // ================= 2. THÊM PHÒNG =================

    @Override
    protected Room inputNew() {

        String number = askNewRoomNumber(null, true);

        if (number == null) {
            return null;
        }

        int floor = getFloorFromRoomNumber(number);

        System.out.println("Số phòng: " + number);
        System.out.println("Tầng tự động: " + floor);

        System.out.println(
                "Loại phòng: 1. Standard   2. Deluxe   3. Suite"
        );

        int type = InputHelper.readInt(
                "Chọn loại (1-3): ", 1, 3
        );

        return createRoom(
                type,
                number,
                floor,
                RoomStatus.AVAILABLE
        );
    }

    private Room createRoom(
            int type,
            String number,
            int floor,
            RoomStatus status
    ) {

        if (type == 1) {
            return new StandardRoom(number, floor, status);

        } else if (type == 2) {

            boolean bathtub = askYesNo("Có bồn tắm?");
            boolean balcony = askYesNo("Có ban công?");

            return new DeluxeRoom(
                    number,
                    floor,
                    status,
                    bathtub,
                    balcony
            );

        } else {

            int bedrooms = InputHelper.readInt(
                    "Số phòng ngủ (1-5): ", 1, 5
            );

            return new SuiteRoom(
                    number,
                    floor,
                    status,
                    bedrooms
            );
        }
    }

    @Override
    protected void doAdd() {

        Room room = inputNew();

        if (room == null) {
            System.out.println("Đã hủy thêm.");
            return;
        }

        if (roomList.add(room)) {
            roomList.saveToFile();
            System.out.println(
                    "Đã thêm phòng " + room.getId()
                            + " - Tầng " + room.getFloor() + "."
            );

        } else {

            System.out.println(
                    "Thêm thất bại: số phòng "
                            + room.getId() + " đã tồn tại."
            );
        }
    }

    // ================= 3. SỬA PHÒNG =================

    @Override
    protected void doEdit() {

        Room old = askExistingRoom("Nhập số phòng cần sửa");

        if (old == null) {
            return;
        }

        System.out.println(old);

        Room edited = inputEdit(old);

        if (edited == null) {
            System.out.println(
                    "Đã hủy sửa, dữ liệu không thay đổi."
            );
            return;
        }

        if (edited.getId().equals(old.getId())) {

            if (roomList.update(old.getId(), edited)) {
                roomList.saveToFile();
                System.out.println("Cập nhật thành công.");
            } else {
                System.out.println("Cập nhật thất bại.");
            }

            return;
        }

        if (HotelData.isRoomInUse(old.getId())) {

            System.out.println(
                    "Phòng đang có đặt phòng, không thể đổi số phòng."
            );

            return;
        }

        if (!roomList.remove(old.getId())) {
            System.out.println("Không thể cập nhật phòng.");
            return;
        }

        if (roomList.add(edited)) {
            roomList.saveToFile();
            System.out.println(
                    "Cập nhật thành công. Số phòng mới: "
                            + edited.getId()
                            + " - Tầng " + edited.getFloor()
            );

        } else {

            roomList.add(old);

            System.out.println("Cập nhật thất bại.");
        }
    }

    // ================= NHẬP THÔNG TIN SỬA PHÒNG =================

    @Override
    protected Room inputEdit(Room old) {

        Room room = copyOf(old);

        while (true) {

            System.out.println(
                    "\nChọn thông tin muốn sửa:"
            );

            System.out.println(
                    "1. Số phòng: "
                            + changeText(old.getId(), room.getId())
            );

            System.out.println(
                    "2. Loại phòng: "
                            + changeText(old.getRoomType(), room.getRoomType())
            );

            int max = 3;

            if (room instanceof DeluxeRoom) {

                System.out.println(
                        "3. Bồn tắm: "
                                + changeText(
                                bathtubText(old),
                                bathtubText(room)
                        )
                );

                System.out.println(
                        "4. Ban công: "
                                + changeText(
                                balconyText(old),
                                balconyText(room)
                        )
                );

                System.out.println(
                        "5. Trạng thái: "
                                + changeText(
                                old.getStatus().getDisplayName(),
                                room.getStatus().getDisplayName()
                        )
                );

                max = 5;

            } else if (room instanceof SuiteRoom) {

                System.out.println(
                        "3. Số phòng ngủ: "
                                + changeText(
                                bedroomsText(old),
                                bedroomsText(room)
                        )
                );

                System.out.println(
                        "4. Trạng thái: "
                                + changeText(
                                old.getStatus().getDisplayName(),
                                room.getStatus().getDisplayName()
                        )
                );

                max = 4;

            } else {

                System.out.println(
                        "3. Trạng thái: "
                                + changeText(
                                old.getStatus().getDisplayName(),
                                room.getStatus().getDisplayName()
                        )
                );
            }

            System.out.println("Tầng: " + room.getFloor());

            System.out.println(
                    "Giá/đêm (tự tính theo loại phòng): "
                            + changeText(
                            FileHelper.formatMoney(old.getPricePerNight()),
                            FileHelper.formatMoney(room.getPricePerNight())
                    )
            );

            System.out.println("0. Lưu thay đổi");
            System.out.println("-1. Hủy, không lưu");

            int choice = askChoice("Chọn: ", -1, max);

            if (choice == -1) {
                return null;
            }

            if (choice == 0) {
                return room;
            }

            if (choice == 1) {

                String newNumber = askNewRoomNumber(
                        old.getId(),
                        false
                );

                room = withNumber(room, newNumber);

                System.out.println(
                        "Đã đổi sang phòng " + newNumber
                                + " - Tầng " + room.getFloor()
                );

            } else if (choice == 2) {

                room = changeType(room);

            } else if (room instanceof DeluxeRoom && choice == 3) {

                ((DeluxeRoom) room).setHasBathtub(
                        askYesNo("Có bồn tắm?")
                );

            } else if (room instanceof DeluxeRoom && choice == 4) {

                ((DeluxeRoom) room).setHasBalcony(
                        askYesNo("Có ban công?")
                );

            } else if (room instanceof SuiteRoom && choice == 3) {

                ((SuiteRoom) room).setNumberOfBedrooms(
                        InputHelper.readInt(
                                "Số phòng ngủ mới (1-5): ", 1, 5
                        )
                );

            } else {

                changeRoomStatus(room);
            }
        }
    }

    // ================= SỬA TRẠNG THÁI PHÒNG =================

    private void changeRoomStatus(Room room) {

        RoomStatus[] statuses = RoomStatus.values();

        System.out.println("\nDanh sách trạng thái:");

        for (int i = 0; i < statuses.length; i++) {
            System.out.println(
                    (i + 1) + ". " + statuses[i].getDisplayName()
            );
        }

        int choice = InputHelper.readInt(
                "Chọn trạng thái mới (1-" + statuses.length + "): ",
                1,
                statuses.length
        );

        room.setStatus(statuses[choice - 1]);

        System.out.println(
                "Đã chọn trạng thái: "
                        + room.getStatus().getDisplayName()
        );
    }

    // ================= ĐỔI LOẠI PHÒNG =================

    private Room changeType(Room room) {

        System.out.println(
                "Loại phòng: 1. Standard   2. Deluxe   3. Suite"
        );

        int type = InputHelper.readInt(
                "Chọn loại mới (1-3): ", 1, 3
        );

        if ((type == 1 && room instanceof StandardRoom)
                || (type == 2 && room instanceof DeluxeRoom)
                || (type == 3 && room instanceof SuiteRoom)) {

            return room;
        }

        return createRoom(
                type,
                room.getId(),
                room.getFloor(),
                room.getStatus()
        );
    }

    // ================= TẠO BẢN SAO PHÒNG =================

    private Room copyOf(Room old) {

        if (old instanceof DeluxeRoom) {
            return new DeluxeRoom((DeluxeRoom) old);
        }

        if (old instanceof SuiteRoom) {
            return new SuiteRoom((SuiteRoom) old);
        }

        return new StandardRoom((StandardRoom) old);
    }

    // ================= ĐỔI SỐ PHÒNG =================

    private Room withNumber(Room r, String number) {

        int floor = getFloorFromRoomNumber(number);

        if (r instanceof DeluxeRoom) {

            DeluxeRoom d = (DeluxeRoom) r;

            return new DeluxeRoom(
                    number,
                    floor,
                    d.getStatus(),
                    d.isHasBathtub(),
                    d.isHasBalcony()
            );
        }

        if (r instanceof SuiteRoom) {

            SuiteRoom s = (SuiteRoom) r;

            return new SuiteRoom(
                    number,
                    floor,
                    s.getStatus(),
                    s.getNumberOfBedrooms()
            );
        }

        return new StandardRoom(
                number,
                floor,
                r.getStatus()
        );
    }

    // ================= 4. XÓA PHÒNG =================

    @Override
    protected void doRemove() {

        Room room = askExistingRoom("Nhập số phòng cần xóa");

        if (room == null) {
            return;
        }

        System.out.println(room);

        if (!canRemove(room)) {
            return;
        }

        if (!askYesNo("Bạn chắc chắn muốn xóa?")) {

            System.out.println(
                    "Đã hủy xóa, dữ liệu không thay đổi."
            );

            return;
        }

        deletedRooms.remove(room.getId());

        if (!deletedRooms.add(room)) {
            System.out.println(
                    "Không thể lưu phòng vào danh sách đã xóa."
            );
            return;
        }

        if (!roomList.remove(room.getId())) {
            deletedRooms.remove(room.getId());
            System.out.println("Xóa phòng thất bại.");
            return;
        }

        roomList.saveToFile();
        deletedRooms.saveToFile();

        System.out.println(
                "Đã xóa phòng " + room.getId()
                        + " (thông tin được lưu trong danh sách phòng đã xóa)."
        );
    }

    // ================= 5. TÌM KIẾM PHÒNG =================

    @Override
    protected void doSearch() {
        printBack();
        String keyword = InputHelper.readLine("Nhập từ khóa (số phòng, loại, trạng thái): ").trim();
        if (keyword.equals(BACK)) {
            return;
        }
        showResult(roomList.search(keyword));
    }

    // ================= CHỨC NĂNG RIÊNG =================

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

            case 0:
                filterByType();
                break;

            case 1:
                filterByStatus();
                break;

            case 2:
                filterByPrice();
                break;

            case 3:
                viewDeleted();
                break;

            default:
                break;
        }
    }

    // ================= HIỂN THỊ KẾT QUẢ =================

    private void showResult(List<Room> result) {

        if (result.isEmpty()) {

            System.out.println(
                    "Không có phòng nào phù hợp."
            );

        } else {

            roomList.display(result);
        }
    }

    // ================= LỌC THEO LOẠI =================

    private void filterByType() {

        System.out.println(
                "Loại phòng: 1. Standard   2. Deluxe   3. Suite"
        );

        Integer type = askIntOrBack(
                "Chọn loại (1-3)", 1, 3
        );

        if (type == null) {
            return;
        }

        String name = (type == 1)
                ? "Standard"
                : (type == 2)
                ? "Deluxe"
                : "Suite";

        showResult(
                roomList.findByType(name)
        );
    }

    // ================= LỌC THEO TRẠNG THÁI =================

    private void filterByStatus() {

        RoomStatus[] all = RoomStatus.values();

        for (int i = 0; i < all.length; i++) {

            System.out.println(
                    (i + 1) + ". " + all[i].getDisplayName()
            );
        }

        Integer choice = askIntOrBack(
                "Chọn trạng thái (1-" + all.length + ")",
                1,
                all.length
        );

        if (choice == null) {
            return;
        }

        showResult(
                roomList.findByStatus(all[choice - 1])
        );
    }

    // ================= LỌC THEO KHOẢNG GIÁ =================

    // Lọc theo khoảng giá: nhập -1 ở câu đầu để quay lại
    private void filterByPrice() {
        printBack();
        double min = 0;
        while (true) {
            String text = InputHelper.readString("Giá thấp nhất: ").trim();
            if (text.equals(BACK)) {
                return;
            }
            try {
                min = Double.parseDouble(text);
                if (min > 0) {
                    break;
                }
            } catch (NumberFormatException e) {
                // nhập chữ thì báo lỗi bên dưới rồi cho nhập lại
            }
            System.out.println("Vui lòng nhập số lớn hơn 0.");
        }

        double max = InputHelper.readPositiveDouble("Giá cao nhất: ");
        while (min >= max) {
            System.out.println("Giá thấp nhất phải nhỏ hơn giá cao nhất, mời nhập lại giá cao nhất.");
            max = InputHelper.readPositiveDouble("Giá cao nhất: ");
        }
        showResult(roomList.findByPriceRange(min, max));
    }

    // ================= XEM PHÒNG ĐÃ XÓA =================

    private void viewDeleted() {

        if (deletedRooms.size() == 0) {

            System.out.println(
                    "Chưa có phòng nào bị xóa."
            );

        } else {

            deletedRooms.displayAll();
        }
    }

    // ================= CHẶN XÓA PHÒNG ĐANG ĐƯỢC SỬ DỤNG =================

    @Override
    protected boolean canRemove(Room room) {

        if (room.getStatus() == RoomStatus.OCCUPIED) {

            System.out.println(
                    "Không thể xóa phòng đang có khách ở (OCCUPIED)."
            );

            return false;
        }

        if (room.getStatus() == RoomStatus.RESERVED) {

            System.out.println(
                    "Không thể xóa phòng đã được đặt (RESERVED)."
            );

            return false;
        }

        if (HotelData.isRoomInUse(room.getId())) {

            System.out.println(
                    "Phòng đang được sử dụng trong đơn đặt phòng, không thể xóa."
            );

            return false;
        }

        return true;
    }

    // ================= THOÁT QUẢN LÝ PHÒNG =================

    @Override
    protected void onExit() {

        super.onExit();

        deletedRooms.saveToFile();

        System.out.println("Đã lưu thay đổi.");
    }
}
