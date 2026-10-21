package hotel.model;

public class Booking {
    private String bookingId;
    private String customerId;
    private String roomId;
    private int stayDays;

    public Booking(String bookingId, String customerId, String roomId, int stayDays) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomId = roomId;
        this.stayDays = stayDays;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public int getStayDays() { return stayDays; }
    public void setStayDays(int stayDays) { this.stayDays = stayDays; }
}