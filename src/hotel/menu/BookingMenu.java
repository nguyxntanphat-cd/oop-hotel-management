package hotel.menu;

import hotel.list.BookingList;
import hotel.model.Booking;
import hotel.model.BookingStatus;
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
        // --- KHI NÀO TV3 GỘP CODE, BỎ COMMENT ĐOẠN NÀY ĐỂ TỰ ĐỘNG HIỂN THỊ TÊN KHÁCH ---
        /*
        hotel.model.Customer c = (hotel.model.Customer) hotel.HotelData.CUSTOMERS.findById(cId);
        if (c != null) {
            System.out.println("-> Tên khách hàng: " + c.getName());
        } else {
            System.out.println("-> Cảnh báo: Mã này chưa tồn tại trong danh sách Khách hàng!");
            // return null; // Nếu muốn bắt buộc phải có khách hàng mới cho đặt phòng thì mở luôn dòng này
        }
        */
        String rId = InputHelper.readString("Nhập mã phòng: ");
        LocalDate checkIn = InputHelper.readDate("Nhập ngày nhận phòng (dd/MM/yyyy): ");
        LocalDate checkOut = InputHelper.readDateAfter("Nhập ngày trả phòng (dd/MM/yyyy): ", checkIn);
        
        double price = 0;
        
        // --- CHỜ TV2 & TV3 GỘP CODE ĐỂ KIỂM TRA MÃ KH, MÃ PHÒNG VÀ TỰ ĐỘNG LẤY GIÁ ---
        /*
        if (!hotel.HotelData.CUSTOMERS.exists(cId)) {
            System.out.println("Lỗi: Mã khách hàng không tồn tại!"); return null;
        }
        hotel.model.Room room = hotel.HotelData.ROOMS.findById(rId);
        if (room == null || !room.isAvailable() || !((BookingList)list).isRoomAvailable(rId, checkIn, checkOut)) {
            System.out.println("Lỗi: Phòng không tồn tại, đang bảo trì, hoặc đã bị trùng lịch đặt!"); return null;
        }
        price = room.getPricePerNight();
        System.out.println("-> Hệ thống tự động lấy giá phòng: " + hotel.util.FileHelper.formatMoney(price) + " VNĐ/đêm");
        */
        
        // Tạm thời bắt nhập tay khi chưa có code TV2
        if (price == 0) price = InputHelper.readPositiveDouble("Nhập giá phòng lúc đặt (VNĐ/đêm): ");

        return new Booking(id, cId, rId, checkIn, checkOut, price, BookingStatus.RESERVED);
    }

    @Override
    protected Booking inputEdit(Booking old) {
        // Yêu cầu Issue: Chỉ cho phép sửa khi còn RESERVED
        if (old.getStatus() != BookingStatus.RESERVED) {
            System.out.println("Lỗi: Chỉ được phép sửa thông tin khi phiếu còn ở trạng thái Đã đặt (RESERVED)!");
            return null; // Trả về null để hủy thao tác sửa
        }

        Booking b = new Booking(old);
        System.out.println("Sửa đặt phòng: " + old.getId());
        b.setCustomerId(InputHelper.readStringOrKeep("Mã KH", b.getCustomerId()));
        b.setRoomNumber(InputHelper.readStringOrKeep("Mã Phòng", b.getRoomNumber()));
        
        return b;
    }

    // --- MENU MỞ RỘNG ĐÁP ỨNG TRỌN VẸN YÊU CẦU CỦA ISSUE #6 ---
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
                System.out.println("Lỗi: Không tìm thấy phiếu đặt phòng!"); return;
            }

            switch (index) {
                case 0: // Check-in
                    if (b.getStatus() == BookingStatus.RESERVED) {
                        b.setStatus(BookingStatus.CHECKED_IN);
                        list.saveToFile();
                        System.out.println("-> Check-in thành công! Khách bắt đầu lưu trú.");
                    } else {
                        System.out.println("Lỗi: Chỉ có thể Check-in các phiếu đang ở trạng thái RESERVED!");
                    }
                    break;

                case 1: // Thêm dịch vụ
                    if (b.getStatus() != BookingStatus.CHECKED_IN) {
                        System.out.println("Lỗi: Khách chưa Check-in, không thể gọi dịch vụ!"); return;
                    }
                    System.out.println("Tính năng gọi dịch vụ (Đang chờ TV3 gộp code...)");
                    break;

                case 2: // Check-out
                    if (b.getStatus() == BookingStatus.CHECKED_IN) {
                        b.setStatus(BookingStatus.CHECKED_OUT);
                        list.saveToFile();
                        
                        // Gọi tự động sang module Hóa đơn (TV5 - Issue #7)
                        String invId = hotel.HotelData.INVOICES.nextId(hotel.model.Invoice.ID_PREFIX);
                        hotel.model.Invoice newInv = hotel.model.Invoice.createFromBooking(invId, b); // Thêm c vào tham số thứ 3 khi TV3 gộp code
                        hotel.HotelData.INVOICES.add(newInv);
                        hotel.HotelData.INVOICES.saveToFile();
                        
                        System.out.println("-> Check-out thành công! Đã tự động tạo hóa đơn: " + invId);
                    } else {
                        System.out.println("Lỗi: Phải Check-in mới có thể Check-out!");
                    }
                    break;

                case 3: // Hủy phòng
                    if (b.getStatus() == BookingStatus.RESERVED) {
                        b.setStatus(BookingStatus.CANCELLED);
                        list.saveToFile();
                        System.out.println("-> Hủy phiếu đặt phòng thành công! (Trạng thái: CANCELLED)");
                    } else {
                        System.out.println("Lỗi: Không thể hủy khi khách đã Check-in hoặc Check-out!");
                    }
                    break;
            }
        } 
        else if (index == 4) { // Xem theo khách
            String cId = InputHelper.readString("Nhập mã Khách hàng: ");
            List<Booking> blist = ((BookingList)list).findByCustomer(cId);
            list.display(blist);
        } 
        else if (index == 5) { // Xem theo phòng
            String rId = InputHelper.readString("Nhập mã Phòng: ");
            List<Booking> blist = ((BookingList)list).findByRoom(rId);
            list.display(blist);
        }
    }
}