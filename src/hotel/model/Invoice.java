package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Payable;
import hotel.common.Searchable;
import hotel.util.AppConfig;
import hotel.util.FileHelper;
import java.time.LocalDate;

public class Invoice implements Identifiable, FileSerializable, Searchable, Payable {
    public static final String ID_PREFIX = "HD";
    private static int createdCount = 0;

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
        createdCount++;
    }

    public Invoice(Invoice other) {
        this(other.id, other.bookingId, other.issueDate, other.roomCharge,
             other.serviceCharge, other.discount, other.paymentMethod, other.paid);
    }

    public static int getCreatedCount() {
        return createdCount;
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

    @Override
    public double calculateTotal() {
        return (roomCharge + serviceCharge - discount) + getVat();
    }

    // --- HÀM TẠO HÓA ĐƠN TỰ ĐỘNG THEO HỢP ĐỒNG THIET_KE.md MỤC 3.6 ---
    public static Invoice createFromBooking(String id, Booking booking, Customer customer) {
        double rCharge = booking.getRoomCharge();
        double sCharge = booking.getServiceCharge();
        double disc = (rCharge + sCharge) * (customer != null ? customer.getDiscountRate() : 0.0);
        return new Invoice(id, booking.getId(), LocalDate.now(), rCharge, sCharge, disc, "CASH", true);
    }

    public static Invoice createFromBooking(String id, Booking booking) {
        Customer c = hotel.HotelData.CUSTOMERS.findById(booking.getCustomerId());
        return createFromBooking(id, booking, c);
    }

    public static String header() {
        return String.format("%-10s %-10s %-15s %-15s %-15s %-15s",
                "Mã HĐ", "Mã ĐP", "Ngày lập", "Tổng tiền", "Phương thức", "Trạng thái");
    }

    @Override
    public String toString() {
        return String.format("%-10s %-10s %-15s %-15s %-15s %-15s",
                id,
                bookingId,
                FileHelper.formatDate(issueDate),
                FileHelper.formatMoney(calculateTotal()),
                paymentMethod,
                paid ? "Đã TT" : "Chưa TT");
    }

    @Override
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
        return id.toLowerCase().contains(kw) ||
               bookingId.toLowerCase().contains(kw);
    }
}
