package hotel.menu;

import hotel.HotelData;
import hotel.model.Booking;
import hotel.model.BookingStatus;
import hotel.model.Customer;
import hotel.model.DeluxeRoom;
import hotel.model.Employee;
import hotel.model.Housekeeper;
import hotel.model.Invoice;
import hotel.model.Manager;
import hotel.model.Receptionist;
import hotel.model.Room;
import hotel.model.RoomStatus;
import hotel.model.Service;
import hotel.model.StandardRoom;
import hotel.model.SuiteRoom;
import hotel.model.UsedService;
import hotel.model.VipCustomer;
import hotel.util.FileHelper;
import hotel.util.InputHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Menu thống kê tổng hợp toàn khách sạn (Issue #8).
 * Đọc dữ liệu từ cả 6 danh sách trong {@link HotelData}.
 */
public class StatisticsMenu extends Menu {

    public StatisticsMenu() {
        super("THỐNG KÊ & BÁO CÁO KHÁCH SẠN");
    }

    @Override
    protected String[] getOptions() {
        return new String[]{
                "Tổng quan khách sạn & Công suất phòng",
                "Doanh thu theo tháng / khoảng ngày",
                "Doanh thu theo loại phòng",
                "Top 5 khách hàng chi tiêu nhiều nhất",
                "Thống kê dịch vụ được sử dụng nhiều nhất",
                "Thống kê nhân sự & Tổng quỹ lương",
                "Số lượng đối tượng đã tạo (static createdCount)"
        };
    }

    @Override
    protected void handle(int choice) {
        switch (choice) {
            case 1:
                showOverviewAndOccupancy();
                break;
            case 2:
                showRevenueByPeriod();
                break;
            case 3:
                showRevenueByRoomType();
                break;
            case 4:
                showTopCustomers();
                break;
            case 5:
                showTopServices();
                break;
            case 6:
                showSalaryStatistics();
                break;
            case 7:
                showCreatedCounts();
                break;
            default:
                break;
        }
    }

    // ===================== 1. Tổng quan & Công suất phòng =====================

    private void showOverviewAndOccupancy() {
        int totalRooms = HotelData.ROOMS.size();
        int available = HotelData.ROOMS.findByStatus(RoomStatus.AVAILABLE).size();
        int reserved = HotelData.ROOMS.findByStatus(RoomStatus.RESERVED).size();
        int occupied = HotelData.ROOMS.findByStatus(RoomStatus.OCCUPIED).size();
        int cleaning = HotelData.ROOMS.findByStatus(RoomStatus.CLEANING).size();
        int maintenance = HotelData.ROOMS.findByStatus(RoomStatus.MAINTENANCE).size();

        double occupancyRate = totalRooms > 0 ? (occupied * 100.0) / totalRooms : 0.0;
        double activeRate = totalRooms > 0 ? ((occupied + reserved) * 100.0) / totalRooms : 0.0;

        int totalCustomers = HotelData.CUSTOMERS.size();
        int vipCustomers = HotelData.CUSTOMERS.getVipCustomers().size();
        int normalCustomers = totalCustomers - vipCustomers;

        int totalServices = HotelData.SERVICES.size();
        int activeServices = HotelData.SERVICES.getActiveServices().size();

        int totalEmployees = HotelData.EMPLOYEES.size();
        int totalBookings = HotelData.BOOKINGS.size();
        int totalInvoices = HotelData.INVOICES.size();
        int unpaidInvoices = HotelData.INVOICES.getUnpaid().size();

        System.out.println("\n================ TỔNG QUAN KHÁCH SẠN ================");
        System.out.printf("1. Phòng      : %d phòng%n", totalRooms);
        System.out.printf("   - Trống (AVAILABLE)      : %d%n", available);
        System.out.printf("   - Đã đặt (RESERVED)      : %d%n", reserved);
        System.out.printf("   - Đang ở (OCCUPIED)      : %d%n", occupied);
        System.out.printf("   - Đang dọn (CLEANING)    : %d%n", cleaning);
        System.out.printf("   - Bảo trì (MAINTENANCE)  : %d%n", maintenance);
        System.out.printf("   => Công suất phòng đang ở         : %.2f%% (%d/%d phòng)%n",
                occupancyRate, occupied, totalRooms);
        System.out.printf("   => Công suất tính cả phòng đã đặt : %.2f%% (%d/%d phòng)%n",
                activeRate, occupied + reserved, totalRooms);

        System.out.printf("2. Khách hàng : %d người (Thường: %d | VIP: %d)%n",
                totalCustomers, normalCustomers, vipCustomers);
        System.out.printf("3. Dịch vụ    : %d dịch vụ (Đang bán: %d | Ngừng bán: %d)%n",
                totalServices, activeServices, totalServices - activeServices);
        System.out.printf("4. Nhân viên  : %d người (Tổng quỹ lương: %s VNĐ)%n",
                totalEmployees, FileHelper.formatMoney(HotelData.EMPLOYEES.getTotalSalary()));
        System.out.printf("5. Đặt phòng  : %d phiếu (Đang ở: %d | Đã đặt: %d | Đã trả: %d | Đã hủy: %d)%n",
                totalBookings,
                HotelData.BOOKINGS.findByStatus(BookingStatus.CHECKED_IN).size(),
                HotelData.BOOKINGS.findByStatus(BookingStatus.RESERVED).size(),
                HotelData.BOOKINGS.findByStatus(BookingStatus.CHECKED_OUT).size(),
                HotelData.BOOKINGS.findByStatus(BookingStatus.CANCELLED).size());
        System.out.printf("6. Hóa đơn    : %d hóa đơn (Đã thanh toán: %d | Chưa thanh toán: %d)%n",
                totalInvoices, totalInvoices - unpaidInvoices, unpaidInvoices);
        System.out.println("=====================================================");
    }

    // ===================== 2. Doanh thu theo tháng / khoảng ngày =====================

    private void showRevenueByPeriod() {
        int mode = InputHelper.choose("Chọn kiểu thống kê doanh thu:",
                "Thống kê theo các tháng trong năm",
                "Thống kê theo khoảng ngày tùy chọn");

        if (mode == 0) {
            int year = InputHelper.readInt("Nhập năm cần thống kê (vd 2026): ", 2000, 2100);
            System.out.println("\n========== DOANH THU THEO THÁNG NĂM " + year + " ==========");
            System.out.printf("%-10s %15s %22s%n", "Tháng", "Số hóa đơn TT", "Doanh thu (VNĐ)");
            System.out.println("---------------------------------------------------");

            double yearTotal = 0.0;
            int yearCount = 0;

            for (int month = 1; month <= 12; month++) {
                LocalDate from = LocalDate.of(year, month, 1);
                LocalDate to = from.withDayOfMonth(from.lengthOfMonth());
                List<Invoice> inMonth = HotelData.INVOICES.findByDateRange(from, to);
                int paidCount = 0;
                for (Invoice inv : inMonth) {
                    if (inv.isPaid()) {
                        paidCount++;
                    }
                }
                double monthRev = HotelData.INVOICES.getRevenue(from, to);
                yearTotal += monthRev;
                yearCount += paidCount;

                if (paidCount > 0) {
                    System.out.printf("Tháng %-4d %15d %22s%n",
                            month, paidCount, FileHelper.formatMoney(monthRev));
                }
            }
            System.out.println("---------------------------------------------------");
            System.out.printf("TỔNG NĂM   %15d %22s%n",
                    yearCount, FileHelper.formatMoney(yearTotal));
        } else {
            LocalDate from = InputHelper.readDate("Từ ngày (dd/MM/yyyy)");
            LocalDate to = InputHelper.readDateAfter("Đến ngày (dd/MM/yyyy)", from);
            List<Invoice> list = HotelData.INVOICES.findByDateRange(from, to);
            double revenue = HotelData.INVOICES.getRevenue(from, to);

            System.out.println("\nDanh sách hóa đơn từ " + FileHelper.formatDate(from)
                    + " đến " + FileHelper.formatDate(to) + ":");
            HotelData.INVOICES.display(list);
            System.out.println("=> TỔNG DOANH THU ĐÃ THANH TOÁN: " + FileHelper.formatMoney(revenue) + " VNĐ");
        }
    }

    // ===================== 3. Doanh thu theo loại phòng =====================

    private void showRevenueByRoomType() {
        String[] types = {"Standard", "Deluxe", "Suite"};
        int[] bookingCounts = new int[types.length];
        double[] roomRevenues = new double[types.length];
        double[] totalRevenues = new double[types.length];

        for (Invoice inv : HotelData.INVOICES.getAll()) {
            if (!inv.isPaid()) {
                continue;
            }
            Booking b = HotelData.BOOKINGS.findById(inv.getBookingId());
            if (b == null) {
                continue;
            }
            Room r = HotelData.ROOMS.findById(b.getRoomNumber());
            if (r == null) {
                continue;
            }
            // Đa hình: r.getRoomType() trả về "Standard", "Deluxe", hoặc "Suite"
            String type = r.getRoomType();
            for (int i = 0; i < types.length; i++) {
                if (types[i].equalsIgnoreCase(type)) {
                    bookingCounts[i]++;
                    roomRevenues[i] += inv.getRoomCharge();
                    totalRevenues[i] += inv.calculateTotal();
                    break;
                }
            }
        }

        double grandTotal = 0.0;
        for (double v : totalRevenues) {
            grandTotal += v;
        }

        System.out.println("\n==================== DOANH THU THEO LOẠI PHÒNG ====================");
        System.out.printf("%-14s %12s %20s %20s %10s%n",
                "Loại phòng", "Số lượt ở", "Tiền phòng (VNĐ)", "Tổng HĐ có VAT", "Tỷ trọng");
        System.out.println("-------------------------------------------------------------------");

        for (int i = 0; i < types.length; i++) {
            double ratio = grandTotal > 0 ? (totalRevenues[i] * 100.0) / grandTotal : 0.0;
            System.out.printf("%-14s %12d %20s %20s %9.2f%%%n",
                    types[i],
                    bookingCounts[i],
                    FileHelper.formatMoney(roomRevenues[i]),
                    FileHelper.formatMoney(totalRevenues[i]),
                    ratio);
        }
        System.out.println("-------------------------------------------------------------------");
        System.out.printf("TỔNG DOANH THU: %s VNĐ%n", FileHelper.formatMoney(grandTotal));
    }

    // ===================== 4. Top 5 khách hàng chi tiêu nhiều nhất =====================

    private static class CustomerSpend {
        final Customer customer;
        int bookingCount;
        double totalSpent;

        CustomerSpend(Customer customer) {
            this.customer = customer;
        }
    }

    private void showTopCustomers() {
        List<CustomerSpend> stats = new ArrayList<>();
        for (Customer c : HotelData.CUSTOMERS.getAll()) {
            CustomerSpend cs = new CustomerSpend(c);
            List<Booking> bookings = HotelData.BOOKINGS.findByCustomer(c.getId());
            for (Booking b : bookings) {
                Invoice inv = HotelData.INVOICES.findByBooking(b.getId());
                if (inv != null && inv.isPaid()) {
                    cs.bookingCount++;
                    cs.totalSpent += inv.calculateTotal();
                }
            }
            if (cs.totalSpent > 0) {
                stats.add(cs);
            }
        }

        stats.sort(new Comparator<CustomerSpend>() {
            @Override
            public int compare(CustomerSpend a, CustomerSpend b) {
                return Double.compare(b.totalSpent, a.totalSpent);
            }
        });

        System.out.println("\n==================== TOP 5 KHÁCH HÀNG CHI TIÊU NHIỀU NHẤT ====================");
        if (stats.isEmpty()) {
            System.out.println("(Chưa có dữ liệu hóa đơn đã thanh toán)");
            return;
        }
        System.out.printf("%-5s %-8s %-24s %-15s %10s %18s%n",
                "Hạng", "Mã KH", "Họ tên", "Loại KH", "Số lần ở", "Tổng chi tiêu");
        System.out.println("------------------------------------------------------------------------------");

        int limit = Math.min(5, stats.size());
        for (int i = 0; i < limit; i++) {
            CustomerSpend cs = stats.get(i);
            Customer c = cs.customer;
            System.out.printf("#%-4d %-8s %-24s %-15s %10d %18s%n",
                    (i + 1),
                    c.getId(),
                    c.getFullName(),
                    c.getRole(),
                    cs.bookingCount,
                    FileHelper.formatMoney(cs.totalSpent));
        }
        System.out.println("==============================================================================");
    }

    // ===================== 5. Dịch vụ được dùng nhiều nhất =====================

    private static class ServiceUsage {
        final String serviceId;
        final String serviceName;
        final String unit;
        int totalQuantity;
        double totalRevenue;

        ServiceUsage(String serviceId, String serviceName, String unit) {
            this.serviceId = serviceId;
            this.serviceName = serviceName;
            this.unit = unit;
        }
    }

    private void showTopServices() {
        List<ServiceUsage> usages = new ArrayList<>();

        for (Booking b : HotelData.BOOKINGS.getAll()) {
            if (b.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }
            for (UsedService us : b.getServices()) {
                ServiceUsage found = null;
                for (ServiceUsage u : usages) {
                    if (u.serviceId.equalsIgnoreCase(us.getServiceId())) {
                        found = u;
                        break;
                    }
                }
                if (found == null) {
                    Service s = HotelData.SERVICES.findById(us.getServiceId());
                    String name = s != null ? s.getName() : "(Đã xóa)";
                    String unit = s != null ? s.getUnit() : "lần";
                    found = new ServiceUsage(us.getServiceId(), name, unit);
                    usages.add(found);
                }
                found.totalQuantity += us.getQuantity();
                found.totalRevenue += us.getAmount();
            }
        }

        usages.sort(new Comparator<ServiceUsage>() {
            @Override
            public int compare(ServiceUsage a, ServiceUsage b) {
                int cmp = Integer.compare(b.totalQuantity, a.totalQuantity);
                return cmp != 0 ? cmp : Double.compare(b.totalRevenue, a.totalRevenue);
            }
        });

        System.out.println("\n================ THỐNG KÊ DỊCH VỤ ĐƯỢC DÙNG NHIỀU NHẤT ================");
        if (usages.isEmpty()) {
            System.out.println("(Chưa có dịch vụ nào được sử dụng)");
            return;
        }
        System.out.printf("%-5s %-8s %-26s %12s %-8s %18s%n",
                "Hạng", "Mã DV", "Tên dịch vụ", "Tổng SL", "Đơn vị", "Doanh thu DV");
        System.out.println("-----------------------------------------------------------------------");

        for (int i = 0; i < usages.size(); i++) {
            ServiceUsage u = usages.get(i);
            System.out.printf("#%-4d %-8s %-26s %12d %-8s %18s%n",
                    (i + 1),
                    u.serviceId,
                    u.serviceName,
                    u.totalQuantity,
                    u.unit,
                    FileHelper.formatMoney(u.totalRevenue));
        }
        System.out.println("=======================================================================");
    }

    // ===================== 6. Tổng quỹ lương nhân viên =====================

    private void showSalaryStatistics() {
        String[] roles = {"Lễ tân", "Quản lý", "Tạp vụ"};
        System.out.println("\n================ THỐNG KÊ QUỸ LƯƠNG NHÂN VIÊN ================");
        System.out.printf("%-15s %12s %20s %20s%n",
                "Chức vụ", "Số nhân viên", "Tổng lương (VNĐ)", "Lương TB/người");
        System.out.println("--------------------------------------------------------------");

        for (String role : roles) {
            List<Employee> list = HotelData.EMPLOYEES.findByRole(role);
            double sum = 0.0;
            for (Employee e : list) {
                // Đa hình: gọi calculateSalary() của Receptionist / Manager / Housekeeper
                sum += e.calculateSalary();
            }
            double avg = list.isEmpty() ? 0.0 : sum / list.size();
            System.out.printf("%-15s %12d %20s %20s%n",
                    role, list.size(), FileHelper.formatMoney(sum), FileHelper.formatMoney(avg));
        }

        double totalSalary = HotelData.EMPLOYEES.getTotalSalary();
        int totalEmp = HotelData.EMPLOYEES.size();
        System.out.println("--------------------------------------------------------------");
        System.out.printf("TỔNG QUỸ LƯƠNG: %s VNĐ (%d nhân viên)%n",
                FileHelper.formatMoney(totalSalary), totalEmp);
        System.out.println("==============================================================");
    }

    // ===================== 7. Số đối tượng đã tạo (static createdCount) =====================

    private void showCreatedCounts() {
        System.out.println("\n========== SỐ ĐỐI TƯỢNG ĐÃ KHỞI TẠO (STATIC CREATED_COUNT) ==========");
        System.out.printf("%-28s %18s %18s%n", "Lớp (Model)", "Đã tạo (static)", "Đang lưu trong DS");
        System.out.println("---------------------------------------------------------------------");

        System.out.printf("%-28s %18d %18d%n", "Room (tổng)",
                Room.getCreatedCount(), HotelData.ROOMS.size());
        System.out.printf("  - StandardRoom             %18d %18d%n",
                StandardRoom.getCreatedCount(), HotelData.ROOMS.findByType("Standard").size());
        System.out.printf("  - DeluxeRoom               %18d %18d%n",
                DeluxeRoom.getCreatedCount(), HotelData.ROOMS.findByType("Deluxe").size());
        System.out.printf("  - SuiteRoom                %18d %18d%n",
                SuiteRoom.getCreatedCount(), HotelData.ROOMS.findByType("Suite").size());

        System.out.printf("%-28s %18d %18d%n", "Customer (tổng)",
                Customer.getCreatedCount(), HotelData.CUSTOMERS.size());
        System.out.printf("  - VipCustomer              %18d %18d%n",
                VipCustomer.getCreatedCount(), HotelData.CUSTOMERS.getVipCustomers().size());

        System.out.printf("%-28s %18d %18d%n", "Service",
                Service.getCreatedCount(), HotelData.SERVICES.size());

        System.out.printf("%-28s %18d %18d%n", "Employee (tổng)",
                Employee.getCreatedCount(), HotelData.EMPLOYEES.size());
        System.out.printf("  - Receptionist             %18d %18d%n",
                Receptionist.getCreatedCount(), HotelData.EMPLOYEES.findByRole("Lễ tân").size());
        System.out.printf("  - Manager                  %18d %18d%n",
                Manager.getCreatedCount(), HotelData.EMPLOYEES.findByRole("Quản lý").size());
        System.out.printf("  - Housekeeper              %18d %18d%n",
                Housekeeper.getCreatedCount(), HotelData.EMPLOYEES.findByRole("Tạp vụ").size());

        System.out.printf("%-28s %18d %18d%n", "Booking",
                Booking.getCreatedCount(), HotelData.BOOKINGS.size());
        System.out.printf("  - UsedService              %18d %18s%n",
                UsedService.getCreatedCount(), "-");
        System.out.printf("%-28s %18d %18d%n", "Invoice",
                Invoice.getCreatedCount(), HotelData.INVOICES.size());
        System.out.println("=====================================================================");
    }
}

