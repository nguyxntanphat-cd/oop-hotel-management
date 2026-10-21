package hotel.model;

public class Invoice {
    private String invoiceId;
    private Booking booking;
    private double roomPricePerDay;

    public Invoice(String invoiceId, Booking booking, double roomPricePerDay) {
        this.invoiceId = invoiceId;
        this.booking = booking;
        this.roomPricePerDay = roomPricePerDay;
    }

    // Tính tổng tiền = số ngày lưu trú nhân với đơn giá phòng
    public double calculateTotal() {
        if (booking == null) return 0.0;
        return booking.getStayDays() * roomPricePerDay;
    }

    public void printInvoice() {
        System.out.println("===== HÓA ĐƠN THANH TOÁN =====");
        System.out.println("Mã hóa đơn: " + invoiceId);
        System.out.println("Mã đặt phòng: " + (booking != null ? booking.getBookingId() : "N/A"));
        System.out.println("Tổng tiền: " + calculateTotal() + " VNĐ");
        System.out.println("==============================");
    }
}