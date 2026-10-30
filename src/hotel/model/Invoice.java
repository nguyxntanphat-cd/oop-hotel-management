package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Searchable;
import hotel.common.Identifiable;
import hotel.util.AppConfig;
import hotel.util.FileHelper;
import java.time.LocalDate;

public class Invoice implements Identifiable, FileSerializable, Searchable {
    public static final String ID_PREFIX = "HD";
    
    private String id;
    private String bookingId;
    private LocalDate issueDate;
    private double roomCharge;
    private double serviceCharge;
    private double discount;
    private String paymentMethod;
    private boolean paid;

    public Invoice() {}

    public Invoice(String id, String bookingId, LocalDate issueDate, double roomCharge, 
                   double serviceCharge, double discount, String paymentMethod, boolean paid) {
        this.id = id;
        this.bookingId = bookingId;
        this.issueDate = issueDate;
        this.roomCharge = roomCharge;
        this.serviceCharge = serviceCharge;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.paid = paid;
    }

    public Invoice(Invoice other) {
        this(other.id, other.bookingId, other.issueDate, other.roomCharge, 
             other.serviceCharge, other.discount, other.paymentMethod, other.paid);
    }

    @Override
    public String getId() { return id; }
    
    public String getBookingId() { return bookingId; }
    public LocalDate getIssueDate() { return issueDate; }
    public double getRoomCharge() { return roomCharge; }
    public double getServiceCharge() { return serviceCharge; }
    public double getDiscount() { return discount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    // --- CÔNG THỨC CHUẨN THIẾT KẾ ---
    public double getVat() {
        return (roomCharge + serviceCharge - discount) * AppConfig.VAT_RATE;
    }

    public double calculateTotal() {
        return (roomCharge + serviceCharge - discount) + getVat();
    }

    // --- HÀM TẠO HÓA ĐƠN TỰ ĐỘNG (CHỜ TV3 GỘP CODE) ---
    /*
    public static Invoice createFromBooking(String id, Booking booking, hotel.model.Customer customer) {
        double rCharge = booking.getRoomCharge();
        double sCharge = booking.getServiceCharge();
        double disc = (rCharge + sCharge) * (customer != null ? customer.getDiscountRate() : 0.0);
        return new Invoice(id, booking.getId(), LocalDate.now(), rCharge, sCharge, disc, "CASH", true);
    }
    */
    
    // Bản tạm thời:
    public static Invoice createFromBooking(String id, Booking booking) {
        return new Invoice(id, booking.getId(), LocalDate.now(), 
                           booking.getRoomCharge(), booking.getServiceCharge(), 0.0, "CASH", true);
    }
    @Override
    public String toString() {
        return String.format("%-10s %-10s %-15s %-15s %-15s %-15s",
                id, 
                bookingId,
                hotel.util.FileHelper.formatDate(issueDate),
                hotel.util.FileHelper.formatMoney(calculateTotal()),
                paymentMethod,
                paid ? "Đã TT" : "Chưa TT");
    }

    public String toFileString() {
        return String.join(AppConfig.SEPARATOR,
                id, bookingId, FileHelper.formatDate(issueDate),
                String.valueOf(roomCharge), String.valueOf(serviceCharge),
                String.valueOf(discount), paymentMethod, String.valueOf(paid)
        );
    }
    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String kw = keyword.toLowerCase();
        // Cho phép tìm kiếm hóa đơn theo Mã hóa đơn hoặc Mã đặt phòng
        return id.toLowerCase().contains(kw) || 
               bookingId.toLowerCase().contains(kw);
    }
}