package hotel.list;

import hotel.common.BaseList;
import hotel.model.Invoice;
import java.time.LocalDate;
import java.util.List;

public class InvoiceList extends BaseList<Invoice> {

    public InvoiceList() {
        super();
    }

    @Override
    protected String getFileName() {
        return "invoices.txt";
    }

    @Override
    protected Invoice parseLine(String line) {
        String[] p = line.split(hotel.util.AppConfig.SEPARATOR_REGEX, -1);
        if (p.length < 8) return null;
        return new Invoice(p[0], p[1], hotel.util.FileHelper.parseDate(p[2]),
                Double.parseDouble(p[3]), Double.parseDouble(p[4]),
                Double.parseDouble(p[5]), p[6], Boolean.parseBoolean(p[7]));
    }

    public Invoice findByBooking(String bookingId) {
        for (Invoice inv : items) {
            if (inv.getBookingId().equals(bookingId)) return inv;
        }
        return null;
    }

    public List<Invoice> findByDateRange(LocalDate from, LocalDate to) {
        return filter(inv -> !inv.getIssueDate().isBefore(from) && !inv.getIssueDate().isAfter(to));
    }

    public List<Invoice> getUnpaid() {
        return filter(inv -> !inv.isPaid());
    }

    // Nghiệp vụ thống kê: Chỉ cộng dồn các hóa đơn đã thanh toán (paid = true)
    public double getRevenue(LocalDate from, LocalDate to) {
        double total = 0;
        for (Invoice inv : findByDateRange(from, to)) {
            if (inv.isPaid()) {
                total += inv.calculateTotal();
            }
        }
        return total;
    }
}