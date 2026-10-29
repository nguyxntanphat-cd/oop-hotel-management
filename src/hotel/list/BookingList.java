package hotel.list;

import hotel.common.BaseList;
import hotel.model.Booking;
import hotel.model.BookingStatus;
import hotel.model.UsedService;
import hotel.util.FileHelper;
import java.time.LocalDate;
import java.util.List;

public class BookingList extends BaseList<Booking> {

    @Override
    protected String getFileName() {
        return "bookings.txt";
    }

    @Override
    protected Booking parseLine(String line) {
        String[] p = FileHelper.split(line);
        if (p.length < 7) return null;

        Booking b = new Booking(p[0], p[1], p[2], 
                                FileHelper.parseDate(p[3]), 
                                FileHelper.parseDate(p[4]), 
                                Double.parseDouble(p[5]), 
                                BookingStatus.valueOf(p[6]));
        
        if (p.length == 8 && !p[7].trim().isEmpty()) {
            String[] svcs = p[7].split(",");
            for (String svc : svcs) {
                String[] sP = svc.split(":");
                if (sP.length == 3) {
                    b.getServices().add(new UsedService(sP[0], Integer.parseInt(sP[1]), Double.parseDouble(sP[2])));
                }
            }
        }
        return b;
    }

    @Override
    protected String getHeader() {
        return Booking.header();
    }

    // Các hàm giao tiếp theo Hợp đồng THIET_KE.md
    public List<Booking> findByCustomer(String customerId) {
        return filter(b -> b.getCustomerId().equals(customerId));
    }

    public List<Booking> findByRoom(String roomNumber) {
        return filter(b -> b.getRoomNumber().equals(roomNumber));
    }

    public List<Booking> findByStatus(BookingStatus status) {
        return filter(b -> b.getStatus() == status);
    }

    public boolean isRoomAvailable(String roomNumber, LocalDate from, LocalDate to) {
        for (Booking b : getAll()) {
            if (b.getRoomNumber().equals(roomNumber) && b.isActive()) {
                if (from.isBefore(b.getCheckOutDate()) && to.isAfter(b.getCheckInDate())) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasActiveBookingForRoom(String roomNumber) {
        return filter(b -> b.getRoomNumber().equals(roomNumber) && b.isActive()).size() > 0;
    }

    public boolean hasActiveBookingForCustomer(String customerId) {
        return filter(b -> b.getCustomerId().equals(customerId) && b.isActive()).size() > 0;
    }
    public List<Booking> findByDateRange(java.time.LocalDate from, java.time.LocalDate to) {
        return filter(b -> !b.getCheckInDate().isBefore(from) && !b.getCheckOutDate().isAfter(to));
    }
}