package hotel.menu;

import hotel.menu.CrudMenu;
import hotel.list.InvoiceList;
import hotel.model.Invoice;
import hotel.util.InputHelper;
import hotel.util.FileHelper;
import java.time.LocalDate;

public class InvoiceMenu extends CrudMenu<Invoice> {

    public InvoiceMenu(InvoiceList list) {
        super("QUẢN LÝ HÓA ĐƠN", list);
    }

    @Override
    protected Invoice inputNew() {
        String id = list.nextId(Invoice.ID_PREFIX);
        System.out.println("Mã Hóa đơn mới: " + id);
        
        String bId = InputHelper.readString("Nhập mã Đặt phòng cần xuất hóa đơn: ");
        hotel.model.Booking b = hotel.HotelData.BOOKINGS.findById(bId);
        
        if (b == null || b.getStatus() != hotel.model.BookingStatus.CHECKED_OUT) {
            System.out.println("Lỗi: Booking không tồn tại hoặc khách chưa Check-out!");
            return null;
        }
        if (((InvoiceList)list).findByBooking(bId) != null) {
            System.out.println("Lỗi: Phiếu đặt phòng này đã có hóa đơn rồi!");
            return null;
        }

        // Tạm thời gọi hàm rút gọn (Chờ TV3 gộp code)
        return Invoice.createFromBooking(id, b);
    }

    @Override
    protected Invoice inputEdit(Invoice old) {
        Invoice inv = new Invoice(old);
        System.out.println("Sửa Hóa đơn: " + old.getId());
        System.out.println("Lưu ý: Chỉ được phép sửa Phương thức thanh toán và Trạng thái.");
        
        inv.setPaymentMethod(InputHelper.readStringOrKeep("Phương thức TT (CASH/CARD/TRANSFER) [" + inv.getPaymentMethod() + "]", inv.getPaymentMethod()));
        
        String paidStr = InputHelper.readStringOrKeep("Đã thanh toán? (true/false) [" + inv.isPaid() + "]", String.valueOf(inv.isPaid()));
        inv.setPaid(Boolean.parseBoolean(paidStr));
        
        return inv;
    }

    // Nghiệp vụ: Chặn xóa hóa đơn đã thanh toán
    protected void doDelete() {
        String id = InputHelper.readString("Nhập mã hóa đơn cần xóa: ");
        Invoice inv = list.findById(id);
        if (inv != null && inv.isPaid()) {
            System.out.println("Lỗi: KHÔNG ĐƯỢC XÓA hóa đơn đã thanh toán để bảo toàn doanh thu!");
            return;
        }
        if (inv != null) {
            list.remove(id);
            list.saveToFile();
            System.out.println("Xóa thành công!");
        } else {
            System.out.println("Không tìm thấy mã này.");
        }
    }

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
            "Xem chi tiết hóa đơn (Biên lai)",
            "Xác nhận thanh toán (Đổi trạng thái)",
            "Xem Doanh thu theo khoảng ngày"
        };
    }

    @Override
    protected void handleExtra(int index) {
        if (index == 0) {
            String bId = hotel.util.InputHelper.readString("Nhập mã Đặt phòng: ");
            hotel.model.Invoice inv = ((hotel.list.InvoiceList)list).findByBooking(bId);
            
            if (inv != null) {
                // Gọi thêm đối tượng Booking để lấy thông tin ngày kết hợp với giờ quy định
                hotel.model.Booking b = hotel.HotelData.BOOKINGS.findById(bId);

                System.out.println("\n================ HÓA ĐƠN THANH TOÁN ================");
                System.out.println("Mã Hóa đơn:   " + inv.getId());
                System.out.println("Mã Đặt phòng: " + inv.getBookingId());
                System.out.println("Ngày lập:     " + hotel.util.FileHelper.formatDate(inv.getIssueDate()));
                
                if (b != null) {
                    System.out.println("Giờ nhận:     " + hotel.util.FileHelper.formatDate(b.getCheckInDate()) + " " + hotel.util.AppConfig.CHECK_IN_TIME);
                    System.out.println("Giờ trả:      " + hotel.util.FileHelper.formatDate(b.getCheckOutDate()) + " " + hotel.util.AppConfig.CHECK_OUT_TIME);
                }
                
                System.out.println("----------------------------------------------------");
                System.out.println("Tiền phòng:      " + hotel.util.FileHelper.formatMoney(inv.getRoomCharge()));
                System.out.println("Tiền dịch vụ:    " + hotel.util.FileHelper.formatMoney(inv.getServiceCharge()));
                System.out.println("Giảm giá:       -" + hotel.util.FileHelper.formatMoney(inv.getDiscount()));
                
                // Tự động in ra % thuế dựa vào AppConfig
                int vatPercent = (int) (hotel.util.AppConfig.VAT_RATE * 100);
                System.out.println("Thuế VAT (" + vatPercent + "%):   " + hotel.util.FileHelper.formatMoney(inv.getVat()));
                
                System.out.println("----------------------------------------------------");
                System.out.println("TỔNG THANH TOÁN: " + hotel.util.FileHelper.formatMoney(inv.calculateTotal()) + " VNĐ");
                System.out.println("Phương thức TT:  " + inv.getPaymentMethod());
                System.out.println("Trạng thái:      " + (inv.isPaid() ? "Đã thanh toán" : "Chưa thanh toán"));
                System.out.println("====================================================\n");
            } else {
                System.out.println("Không tìm thấy hóa đơn nào cho mã đặt phòng " + bId);
            }
        } 
        else if (index == 1) {
            String id = hotel.util.InputHelper.readString("Nhập mã Hóa đơn cần xác nhận: ");
            hotel.model.Invoice inv = list.findById(id);
            if (inv != null && !inv.isPaid()) {
                inv.setPaid(true);
                list.saveToFile();
                System.out.println("-> Đã xác nhận thanh toán thành công!");
            } else {
                System.out.println("Lỗi: Hóa đơn không tồn tại hoặc đã được thanh toán rồi.");
            }
        }
        else if (index == 2) {
            java.time.LocalDate from = hotel.util.InputHelper.readDate("Nhập ngày bắt đầu (dd/MM/yyyy): ");
            java.time.LocalDate to = hotel.util.InputHelper.readDateAfter("Nhập ngày kết thúc (dd/MM/yyyy): ", from);
            double rev = ((hotel.list.InvoiceList)list).getRevenue(from, to);
            System.out.println("=> TỔNG DOANH THU: " + hotel.util.FileHelper.formatMoney(rev) + " VNĐ");
        }
    }
}