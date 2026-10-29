package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.common.Payable;
import hotel.util.FileHelper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking implements Identifiable, FileSerializable, Searchable, Payable {
    public static final String ID_PREFIX = "DP";
    private static int createdCount = 0;

    private String id;
    private String customerId;
    private String roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double roomPrice;
    private BookingStatus status;
    private List<UsedService> services;

    public Booking() {
        this.services = new ArrayList<>();
    }

    public Booking(String id, String customerId, String roomNumber, LocalDate checkInDate, LocalDate checkOutDate, double roomPrice, BookingStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomPrice = roomPrice;
        this.status = status;
        this.services = new ArrayList<>();
        createdCount++;
    }

    public Booking(Booking other) {
        this(other.id, other.customerId, other.roomNumber, other.checkInDate, other.checkOutDate, other.roomPrice, other.status);
        for (UsedService us : other.services) {
            this.services.add(new UsedService(us.getServiceId(), us.getQuantity(), us.getUnitPrice()));
        }
    }

    public static int getCreatedCount() { return createdCount; }

    @Override
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getRoomPrice() { return roomPrice; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public List<UsedService> getServices() { return services; }

    public long getNights() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights > 0 ? nights : 1;
    }

    public double getRoomCharge() {
        return getNights() * roomPrice;
    }

    public double getServiceCharge() {
        double total = 0;
        for (UsedService s : services) {
            total += s.getAmount();
        }
        return total;
    }

    @Override
    public double calculateTotal() {
        return getRoomCharge() + getServiceCharge();
    }

    public boolean isActive() {
        return status == BookingStatus.RESERVED || status == BookingStatus.CHECKED_IN;
    }

    public static String header() {
        return String.format("%-8s %-8s %-8s %-12s %-12s %-12s %-15s", "Mã ĐP", "Mã KH", "Mã Ph", "Ngày nhận", "Ngày trả", "Giá/đêm", "Trạng thái");
    }

    @Override
    public String toString() {
        return String.format("%-8s %-8s %-8s %-12s %-12s %-12s %-15s", 
                id, customerId, roomNumber, 
                FileHelper.formatDate(checkInDate), 
                FileHelper.formatDate(checkOutDate), 
                FileHelper.formatMoney(roomPrice), 
                status.name());
    }

    @Override
    public boolean matches(String keyword) {
        String kw = keyword.toLowerCase();
        return id.toLowerCase().contains(kw) || customerId.toLowerCase().contains(kw) || roomNumber.toLowerCase().contains(kw);
    }

    @Override
    public String toFileString() {
        List<String> serviceStrings = new ArrayList<>();
        for (UsedService s : services) {
            serviceStrings.add(s.getServiceId() + ":" + s.getQuantity() + ":" + s.getUnitPrice());
        }
        String servicesStr = String.join(",", serviceStrings);
        
        return FileHelper.join(id, customerId, roomNumber, 
                               FileHelper.formatDate(checkInDate), 
                               FileHelper.formatDate(checkOutDate), 
                               String.valueOf(roomPrice), 
                               status.name(), 
                               servicesStr);
    }
}