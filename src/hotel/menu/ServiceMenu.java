package hotel.menu;

import hotel.list.ServiceList;
import hotel.model.Service;
import hotel.util.InputHelper;

import java.util.List;

/**
 * Menu quản lý Dịch vụ (xem, thêm, sửa, xóa, tìm kiếm + lọc, sắp xếp, bật/tắt).
 */
public class ServiceMenu extends CrudMenu<Service> {
    private final ServiceList serviceList;

    public ServiceMenu(ServiceList list) {
        super("QUẢN LÝ DỊCH VỤ", list);
        this.serviceList = list;
    }

    @Override
    protected Service inputNew() {
        String id = list.nextId(Service.ID_PREFIX);
        System.out.println("Mã dịch vụ mới: " + id);

        String name = InputHelper.readString("Tên dịch vụ: ");
        double price = InputHelper.readPositiveDouble("Đơn giá: ");
        String unit = InputHelper.readString("Đơn vị tính (suất, lượt, kg, ...): ");
        boolean active = InputHelper.confirm("Cho phép bán ngay?");
        return new Service(id, name, price, unit, active);
    }

    @Override
    protected Service inputEdit(Service old) {
        Service s = new Service(old);
        s.setName(InputHelper.readStringOrKeep("Tên dịch vụ", s.getName()));
        s.setPrice(InputHelper.readPositiveDoubleOrKeep("Đơn giá", s.getPrice()));
        s.setUnit(InputHelper.readStringOrKeep("Đơn vị tính", s.getUnit()));
        int status = InputHelper.choose("Trạng thái:",
                "Giữ nguyên (" + (s.isActive() ? "Đang bán" : "Ngừng bán") + ")",
                "Đang bán",
                "Ngừng bán");
        if (status == 1) {
            s.setActive(true);
        } else if (status == 2) {
            s.setActive(false);
        }
        return s;
    }

    // Xóa dịch vụ không ảnh hưởng booking cũ vì UsedService đã lưu sẵn đơn giá, nên dùng canRemove mặc định.

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
                "Xem dịch vụ đang bán",
                "Lọc theo khoảng giá",
                "Sắp xếp theo đơn giá",
                "Bật / tắt bán dịch vụ"
        };
    }

    @Override
    protected void handleExtra(int index) {
        switch (index) {
            case 0:
                serviceList.display(serviceList.getActiveServices());
                break;
            case 1:
                filterByPrice();
                break;
            case 2:
                sortByPrice();
                break;
            case 3:
                toggleActive();
                break;
            default:
                break;
        }
    }

    private void filterByPrice() {
        double min = InputHelper.readPositiveDouble("Giá thấp nhất: ");
        double max = InputHelper.readPositiveDouble("Giá cao nhất: ");
        if (min > max) {
            System.out.println("Giá thấp nhất phải nhỏ hơn hoặc bằng giá cao nhất.");
            return;
        }
        serviceList.display(serviceList.findByPriceRange(min, max));
    }

    private void sortByPrice() {
        int order = InputHelper.choose("Thứ tự sắp xếp:", "Giá tăng dần", "Giá giảm dần");
        List<Service> result = order == 0
                ? serviceList.sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice()))
                : serviceList.sorted((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        serviceList.display(result);
    }

    private void toggleActive() {
        Service s = askExisting("Nhập mã dịch vụ: ");
        if (s == null) {
            return;
        }
        s.setActive(!s.isActive());
        serviceList.saveToFile();
        System.out.println(s.getName() + " hiện đang: " + (s.isActive() ? "Đang bán" : "Ngừng bán"));
    }
}
