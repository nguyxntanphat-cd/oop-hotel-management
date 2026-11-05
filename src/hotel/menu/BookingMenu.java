package hotel.menu;

import hotel.HotelData;
import hotel.list.BookingList;
import hotel.model.Booking;
import hotel.model.BookingStatus;
import hotel.model.Customer;
import hotel.model.Invoice;
import hotel.model.Room;
import hotel.model.RoomStatus;
import hotel.model.Service;
import hotel.model.UsedService;
import hotel.util.FileHelper;
import hotel.util.InputHelper;
import java.time.LocalDate;
import java.util.List;

public class BookingMenu extends CrudMenu<Booking> {

    public BookingMenu(BookingList list) {
        super("QUẢN LÝ ĐẶT PHÒNG", list);
    }

    @Override
    protected Booking inputNew() {
        String id = list.nextId(Booking.ID_PREFIX);
        System.out.println("Mã Đặt phòng mới: " + id);

        String cId = InputHelper.readString("Nhập mã khách hàng: ");
        Customer c = HotelData.CUSTOMERS.findById(cId);
        if (c == null) {
            System.out.println("Lỗi: Mã khách hàng không tồn tại!");
            return null;
        }
        System.out.println("-> Tên khách hàng: " + c.getFullName() + " (" + c.getRole() + ")");

        String rId = InputHelper.readString("Nhập mã phòng: ");
        LocalDate checkIn = InputHelper.readDate("Nhập ngày nhận phòng (dd/MM/yyyy): ");
        LocalDate checkOut = InputHelper.readDateAfter("Nhập ngày trả phòng (dd/MM/yyyy): ", checkIn);

        Room room = HotelData.ROOMS.findById(rId);
        if (room == null || !room.isAvailable() || !((BookingList) list).isRoomAvailable(rId, checkIn, checkOut)) {
            System.out.println("Lỗi: Phòng không tồn tại, đang bận/bảo trì, hoặc đã bị trùng lịch đặt!");
            return null;
        }

        double price = room.getPricePerNight();
        System.out.println("-> Hệ thống tự động lấy giá phòng: " + FileHelper.formatMoney(price) + " VNĐ/đêm");

        room.setStatus(RoomStatus.RESERVED);
        HotelData.ROOMS.saveToFile();

        return new Booking(id, c.getId(), room.getId(), checkIn, checkOut, price, BookingStatus.RESERVED);
    }

    @Override
    protected Booking inputEdit(Booking old) {
        if (old.getStatus() != BookingStatus.RESERVED) {
            System.out.println("Lỗi: Chỉ được phép sửa thông tin khi phiếu còn ở trạng thái Đã đặt (RESERVED)!");
            return null;
        }

        Booking b = new Booking(old);
        System.out.println("Sửa đặt phòng: " + old.getId());
        String newCId = InputHelper.readStringOrKeep("Mã KH", b.getCustomerId());
        if (!HotelData.CUSTOMERS.exists(newCId)) {
            System.out.println("Lỗi: Mã khách hàng không tồn tại!");
            return null;
        }
        b.setCustomerId(newCId);

        String newRId = InputHelper.readStringOrKeep("Mã Phòng", b.getRoomNumber());
        if (!newRId.equalsIgnoreCase(b.getRoomNumber())) {
            Room newRoom = HotelData.ROOMS.findById(newRId);
            if (newRoom == null || !newRoom.isAvailable()
                    || !((BookingList) list).isRoomAvailable(newRId, b.getCheckInDate(), b.getCheckOutDate())) {
                System.out.println("Lỗi: Phòng mới không tồn tại hoặc không còn trống!");
                return null;
            }
            Room oldRoom = HotelData.ROOMS.findById(b.getRoomNumber());
            if (oldRoom != null) {
                oldRoom.setStatus(RoomStatus.AVAILABLE);
            }
            newRoom.setStatus(RoomStatus.RESERVED);
            HotelData.ROOMS.saveToFile();
            b.setRoomNumber(newRoom.getId());
        }

        return b;
    }

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
            "Check-in (Nhận phòng)",
            "Thêm dịch vụ (chỉ áp dụng phòng đang ở)",
            "Check-out (Trả phòng & Tự động xuất hóa đơn)",
            "Hủy đặt phòng",
            "Xem Booking theo mã Khách hàng",
            "Xem Booking theo mã Phòng"
        };
    }

    @Override
    protected void handleExtra(int index) {
        if (index >= 0 && index <= 3) {
            String bId = InputHelper.readString("Nhập mã Đặt phòng: ");
            Booking b = list.findById(bId);
            if (b == null) {
                System.out.println("Lỗi: Không tìm thấy phiếu đặt phòng!");
                return;
            }

            switch (index) {
                case 0: // Check-in
                    if (b.getStatus() == BookingStatus.RESERVED) {
                        b.setStatus(BookingStatus.CHECKED_IN);
                        list.saveToFile();
                        Room r = HotelData.ROOMS.findById(b.getRoomNumber());
                        if (r != null) {
                            r.setStatus(RoomStatus.OCCUPIED);
                            HotelData.ROOMS.saveToFile();
                        }
                        System.out.println("-> Check-in thành công! Khách bắt đầu lưu trú.");
                    } else {
                        System.out.println("Lỗi: Chỉ có thể Check-in các phiếu đang ở trạng thái RESERVED!");
                    }
                    break;

                case 1: // Thêm dịch vụ
                    if (b.getStatus() != BookingStatus.CHECKED_IN) {
                        System.out.println("Lỗi: Khách chưa Check-in, không thể gọi dịch vụ!");
                        return;
                    }
                    List<Service> activeServices = HotelData.SERVICES.getActiveServices();
                    if (activeServices.isEmpty()) {
                        System.out.println("Hiện không có dịch vụ nào đang mở bán.");
                        return;
                    }
                    HotelData.SERVICES.display(activeServices);
                    String sId = InputHelper.readString("Nhập mã dịch vụ muốn thêm: ");
                    Service s = HotelData.SERVICES.findById(sId);
                    if (s == null || !s.isActive()) {
                        System.out.println("Lỗi: Dịch vụ không tồn tại hoặc đang ngừng bán!");
                        return;
                    }
                    int qty = InputHelper.readInt("Số lượng (" + s.getUnit() + "): ", 1, 1000);
                    b.getServices().add(new UsedService(s.getId(), qty, s.getPrice()));
                    list.saveToFile();
                    System.out.println("-> Đã thêm " + qty + " " + s.getUnit() + " \"" + s.getName()
                            + "\" (" + FileHelper.formatMoney(qty * s.getPrice()) + " VNĐ) vào booking " + b.getId() + ".");
                    break;

                case 2: // Check-out
                    if (b.getStatus() == BookingStatus.CHECKED_IN) {
                        b.setStatus(BookingStatus.CHECKED_OUT);
                        list.saveToFile();

                        Room r = HotelData.ROOMS.findById(b.getRoomNumber());
                        if (r != null) {
                            r.setStatus(RoomStatus.CLEANING);
                            HotelData.ROOMS.saveToFile();
                        }

                        Customer c = HotelData.CUSTOMERS.findById(b.getCustomerId());
                        String invId = HotelData.INVOICES.nextId(Invoice.ID_PREFIX);
                        Invoice newInv = Invoice.createFromBooking(invId, b, c);
                        HotelData.INVOICES.add(newInv);
                        HotelData.INVOICES.saveToFile();

                        System.out.println("-> Check-out thành công! Phòng " + b.getRoomNumber() + " chuyển sang Đang dọn.");
                        System.out.println("-> Đã tự động tạo hóa đơn: " + invId
                                + " (Tổng thanh toán: " + FileHelper.formatMoney(newInv.calculateTotal()) + " VNĐ)");
                    } else {
                        System.out.println("Lỗi: Phải Check-in mới có thể Check-out!");
                    }
                    break;

                case 3: // Hủy phòng
                    if (b.getStatus() == BookingStatus.RESERVED) {
                        b.setStatus(BookingStatus.CANCELLED);
                        list.saveToFile();
                        if (!HotelData.BOOKINGS.hasActiveBookingForRoom(b.getRoomNumber())) {
                            Room r = HotelData.ROOMS.findById(b.getRoomNumber());
                            if (r != null) {
                                r.setStatus(RoomStatus.AVAILABLE);
                                HotelData.ROOMS.saveToFile();
                            }
                        }
                        System.out.println("-> Hủy phiếu đặt phòng thành công! (Trạng thái: CANCELLED)");
                    } else {
                        System.out.println("Lỗi: Không thể hủy khi khách đã Check-in hoặc Check-out!");
                    }
                    break;
            }
        }
        else if (index == 4) { // Xem theo khách
            String cId = InputHelper.readString("Nhập mã Khách hàng: ");
            List<Booking> blist = ((BookingList) list).findByCustomer(cId);
            list.display(blist);
        }
        else if (index == 5) { // Xem theo phòng
            String rId = InputHelper.readString("Nhập mã Phòng: ");
            List<Booking> blist = ((BookingList) list).findByRoom(rId);
            list.display(blist);
        }
    }
}
