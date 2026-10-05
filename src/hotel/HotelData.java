package hotel;

/**
 * Nơi giữ TẤT CẢ danh sách của chương trình dưới dạng thuộc tính static,
 * để các module tra cứu lẫn nhau (ví dụ BookingMenu cần ROOMS và CUSTOMERS).
 * <p>
 * Khi hoàn thành module, mỗi thành viên bỏ comment dòng TODO của mình
 * (khai báo danh sách + dòng trong loadAll() / saveAll()).
 * <p>
 * Module khác luôn lấy dữ liệu qua đây, ví dụ {@code HotelData.ROOMS.findById("101")};
 * không tự tạo {@code new RoomList()} vì sẽ thành 2 danh sách khác nhau.
 * Xem "Hợp đồng tích hợp" trong docs/THIET_KE.md.
 */
public final class HotelData {
    // TODO #2 Phòng:      public static final RoomList ROOMS = new RoomList();
    // TODO #3 Khách hàng: public static final CustomerList CUSTOMERS = new CustomerList();
    // TODO #4 Dịch vụ:    public static final ServiceList SERVICES = new ServiceList();
    // TODO #5 Nhân viên:  public static final EmployeeList EMPLOYEES = new EmployeeList();
    // TODO #6 Đặt phòng:  public static final BookingList BOOKINGS = new BookingList();
    // TODO #7 Hóa đơn:    public static final InvoiceList INVOICES = new InvoiceList();

    private HotelData() {
    }

    /** Đọc toàn bộ dữ liệu từ thư mục data/ khi khởi động. */
    public static void loadAll() {
        // TODO #2: ROOMS.loadFromFile();
        // TODO #3: CUSTOMERS.loadFromFile();
        // TODO #4: SERVICES.loadFromFile();
        // TODO #5: EMPLOYEES.loadFromFile();
        // TODO #6: BOOKINGS.loadFromFile();
        // TODO #7: INVOICES.loadFromFile();
    }

    /** Ghi toàn bộ dữ liệu xuống thư mục data/ khi thoát. */
    public static void saveAll() {
        // TODO #2: ROOMS.saveToFile();
        // TODO #3: CUSTOMERS.saveToFile();
        // TODO #4: SERVICES.saveToFile();
        // TODO #5: EMPLOYEES.saveToFile();
        // TODO #6: BOOKINGS.saveToFile();
        // TODO #7: INVOICES.saveToFile();
    }

    // ===================== Kiểm tra ràng buộc giữa các module =====================
    // RoomMenu (#2) và CustomerMenu (#3) gọi 2 hàm này trong canRemove() để chặn xóa.
    // Hiện luôn trả về false để code #2, #3 biên dịch được trước khi có BookingList.
    // TV5 sửa thân hàm khi xong #6, TV2 và TV3 không phải sửa gì thêm.

    /** Phòng đang có booking chưa kết thúc (đã đặt hoặc đang ở) thì không được xóa. */
    public static boolean isRoomInUse(String roomNumber) {
        // TODO #6: return BOOKINGS.hasActiveBookingForRoom(roomNumber);
        return false;
    }

    /** Khách đang có booking chưa kết thúc (đã đặt hoặc đang ở) thì không được xóa. */
    public static boolean isCustomerInUse(String customerId) {
        // TODO #6: return BOOKINGS.hasActiveBookingForCustomer(customerId);
        return false;
    }
}
