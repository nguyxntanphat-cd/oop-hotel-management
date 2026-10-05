package hotel.common;

/**
 * Đối tượng có phát sinh số tiền cần thanh toán (đặt phòng, hóa đơn, ...).
 */
public interface Payable {
    double calculateTotal();
}
