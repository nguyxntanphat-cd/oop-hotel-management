package hotel.menu;

import hotel.HotelData;
import hotel.util.AppConfig;

/**
 * Menu chính của chương trình.
 * Mỗi thành viên thay dòng notReady(...) của module mình bằng lệnh mở menu tương ứng.
 */
public class MainMenu extends Menu {

    public MainMenu() {
        super(AppConfig.HOTEL_NAME.toUpperCase() + " - MENU CHÍNH");
    }

    @Override
    protected String[] getOptions() {
        return new String[]{
                "Quản lý phòng",
                "Quản lý khách hàng",
                "Quản lý dịch vụ",
                "Quản lý nhân viên",
                "Đặt phòng / Check-in / Check-out",
                "Quản lý hóa đơn",
                "Thống kê"
        };
    }

    @Override
    protected void handle(int choice) {
        switch (choice) {
            case 1:
                notReady(2); // TODO #2: new RoomMenu(HotelData.ROOMS).run();
                break;
            case 2:
                notReady(3); // TODO #3: new CustomerMenu(HotelData.CUSTOMERS).run();
                break;
            case 3:
                notReady(4); // TODO #4: new ServiceMenu(HotelData.SERVICES).run();
                break;
            case 4:
                notReady(5); // TODO #5: new EmployeeMenu(HotelData.EMPLOYEES).run();
                break;
            case 5:
                notReady(6); // TODO #6: new BookingMenu(HotelData.BOOKINGS).run();
                break;
            case 6:
                notReady(7); // TODO #7: new InvoiceMenu(HotelData.INVOICES).run();
                break;
            case 7:
                notReady(8); // TODO #8: new StatisticsMenu().run();
                break;
            default:
                break;
        }
    }

    private void notReady(int issue) {
        System.out.println("Chức năng đang được phát triển (issue #" + issue + ").");
    }

    @Override
    protected String getExitLabel() {
        return "Thoát chương trình";
    }

    @Override
    protected void onExit() {
        HotelData.saveAll();
        System.out.println("Đã lưu dữ liệu. Tạm biệt!");
    }
}
