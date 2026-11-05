package hotel.menu;

import hotel.HotelData;
import hotel.list.CustomerList;
import hotel.model.Customer;
import hotel.model.VipCustomer;
import hotel.model.VipLevel;
import hotel.util.InputHelper;

import java.util.Collections;

/**
 * Menu quản lý Khách hàng (xem, thêm, sửa, xóa, tìm kiếm + nâng hạng VIP).
 */
public class CustomerMenu extends CrudMenu<Customer> {
    private static final String EMAIL_REGEX = "[^@\\s|]+@[^@\\s|]+\\.[^@\\s|]+";
    private static final String EMAIL_ERROR = "Email không hợp lệ (ví dụ: an@gmail.com)!";
    private static final VipLevel[] LEVELS = VipLevel.values();

    private final CustomerList customerList;

    public CustomerMenu(CustomerList list) {
        super("QUẢN LÝ KHÁCH HÀNG", list);
        this.customerList = list;
    }

    // ===================== Thêm =====================

    @Override
    protected Customer inputNew() {
        int type = InputHelper.choose("Loại khách hàng:", "Khách thường", "Khách VIP");

        String id = list.nextId(Customer.ID_PREFIX);
        System.out.println("Mã khách hàng mới: " + id);

        String fullName = InputHelper.readString("Họ tên: ");
        String phone = inputPhone(null, null);
        String idCard = inputIdCard(null, null);
        String nationality = InputHelper.readString("Quốc tịch: ");
        String email = InputHelper.readPattern("Email: ", EMAIL_REGEX, EMAIL_ERROR);

        if (type == 0) {
            return new Customer(id, fullName, phone, idCard, nationality, email);
        }
        VipLevel level = chooseLevel("Chọn hạng VIP:");
        int points = InputHelper.readInt("Điểm tích lũy: ", 0, 1000000);
        return new VipCustomer(id, fullName, phone, idCard, nationality, email, level, points);
    }

    // ===================== Sửa =====================

    @Override
    protected Customer inputEdit(Customer old) {
        if (old instanceof VipCustomer) {
            VipCustomer copy = new VipCustomer((VipCustomer) old);
            editCommon(copy);
            copy.setVipLevel(chooseLevelOrKeep(copy.getVipLevel()));
            copy.setPoints(InputHelper.readIntOrKeep("Điểm tích lũy", copy.getPoints(), 0, 1000000));
            return copy;
        }
        Customer copy = new Customer(old);
        editCommon(copy);
        return copy;
    }

    private void editCommon(Customer copy) {
        copy.setFullName(InputHelper.readStringOrKeep("Họ tên", copy.getFullName()));
        copy.setPhone(inputPhone(copy.getId(), copy.getPhone()));
        copy.setIdCard(inputIdCard(copy.getId(), copy.getIdCard()));
        copy.setNationality(InputHelper.readStringOrKeep("Quốc tịch", copy.getNationality()));
        copy.setEmail(InputHelper.readPatternOrKeep("Email", copy.getEmail(), EMAIL_REGEX, EMAIL_ERROR));
    }

    /** Nhập SĐT, không cho trùng với khách khác. oldPhone = null nghĩa là đang thêm mới. */
    private String inputPhone(String selfId, String oldPhone) {
        while (true) {
            String phone = oldPhone == null
                    ? InputHelper.readPhone("Số điện thoại (10 số): ")
                    : InputHelper.readPhoneOrKeep("Số điện thoại", oldPhone);
            Customer other = customerList.findByPhone(phone);
            if (other == null || other.getId().equalsIgnoreCase(selfId)) {
                return phone;
            }
            System.out.println("  -> Số điện thoại này đã thuộc khách " + other.getId() + "!");
        }
    }

    /** Nhập CCCD, không cho trùng với khách khác. */
    private String inputIdCard(String selfId, String oldIdCard) {
        while (true) {
            String idCard = oldIdCard == null
                    ? InputHelper.readIdCard("CCCD (12 số): ")
                    : InputHelper.readIdCardOrKeep("CCCD", oldIdCard);
            Customer other = customerList.findByIdCard(idCard);
            if (other == null || other.getId().equalsIgnoreCase(selfId)) {
                return idCard;
            }
            System.out.println("  -> CCCD này đã thuộc khách " + other.getId() + "!");
        }
    }

    private VipLevel chooseLevel(String title) {
        String[] names = new String[LEVELS.length];
        for (int i = 0; i < LEVELS.length; i++) {
            names[i] = LEVELS[i].getDisplayName() + " (giảm " + (int) Math.round(LEVELS[i].getDiscountRate() * 100) + "%)";
        }
        return LEVELS[InputHelper.choose(title, names)];
    }

    private VipLevel chooseLevelOrKeep(VipLevel old) {
        String[] names = new String[LEVELS.length + 1];
        names[0] = "Giữ nguyên (" + old.getDisplayName() + ")";
        for (int i = 0; i < LEVELS.length; i++) {
            names[i + 1] = LEVELS[i].getDisplayName();
        }
        int choice = InputHelper.choose("Chọn hạng VIP:", names);
        return choice == 0 ? old : LEVELS[choice - 1];
    }

    // ===================== Xóa =====================

    @Override
    protected boolean canRemove(Customer customer) {
        if (HotelData.isCustomerInUse(customer.getId())) {
            System.out.println("Không thể xóa: khách " + customer.getId() + " đang có đặt phòng chưa kết thúc.");
            return false;
        }
        return true;
    }

    // ===================== Chức năng riêng =====================

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
                "Nâng khách thường lên VIP",
                "Cộng điểm cho khách VIP (tự nâng hạng khi đủ điểm)",
                "Xem danh sách khách VIP",
                "Tìm khách theo số điện thoại / CCCD"
        };
    }

    @Override
    protected void handleExtra(int index) {
        switch (index) {
            case 0:
                upgradeToVip();
                break;
            case 1:
                addPointsToVip();
                break;
            case 2:
                customerList.display(customerList.getVipCustomers());
                break;
            case 3:
                findByPhoneOrIdCard();
                break;
            default:
                break;
        }
    }

    private void upgradeToVip() {
        Customer customer = askExisting("Nhập mã khách cần nâng VIP: ");
        if (customer == null) {
            return;
        }
        if (customer.isVip()) {
            System.out.println("Khách này đã là VIP (" + customer.getRole() + ").");
            return;
        }
        VipLevel level = chooseLevel("Chọn hạng VIP khởi đầu:");
        VipCustomer vip = new VipCustomer(customer, level, level.getMinPoints());
        if (list.update(customer.getId(), vip)) {
            list.saveToFile();
            System.out.println("Đã nâng " + vip.getFullName() + " lên " + vip.getRole() + ".");
        }
    }

    private void addPointsToVip() {
        Customer customer = askExisting("Nhập mã khách VIP: ");
        if (customer == null) {
            return;
        }
        if (!customer.isVip()) {
            System.out.println("Khách này chưa phải VIP. Hãy dùng chức năng \"Nâng khách thường lên VIP\".");
            return;
        }
        VipCustomer vip = (VipCustomer) customer;
        int add = InputHelper.readInt("Số điểm cộng thêm: ", 1, 1000000);
        boolean upgraded = vip.addPoints(add);
        list.saveToFile();
        System.out.println("Điểm hiện tại: " + vip.getPoints() + " (" + vip.getRole() + ")");
        if (upgraded) {
            System.out.println("Chúc mừng! Khách được nâng lên hạng " + vip.getVipLevel().getDisplayName() + ".");
        }
    }

    private void findByPhoneOrIdCard() {
        int choice = InputHelper.choose("Tìm theo:", "Số điện thoại", "CCCD");
        String keyword = InputHelper.readString(choice == 0 ? "Nhập số điện thoại: " : "Nhập CCCD: ");
        Customer found = choice == 0
                ? customerList.findByPhone(keyword)
                : customerList.findByIdCard(keyword);
        if (found == null) {
            System.out.println("Không tìm thấy khách hàng.");
        } else {
            customerList.display(Collections.singletonList(found));
        }
    }
}
