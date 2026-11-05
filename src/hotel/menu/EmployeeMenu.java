package hotel.menu;

import hotel.list.EmployeeList;
import hotel.model.Employee;
import hotel.model.Housekeeper;
import hotel.model.Manager;
import hotel.model.Receptionist;
import hotel.util.FileHelper;
import hotel.util.InputHelper;

import java.time.LocalDate;
import java.util.List;

/**
 * Menu quản lý Nhân viên.
 */
public class EmployeeMenu extends CrudMenu<Employee> {
    private static final String[] SHIFTS = {"Sáng", "Chiều", "Đêm"};

    private final EmployeeList employeeList;

    public EmployeeMenu(EmployeeList list) {
        super("QUẢN LÝ NHÂN VIÊN", list);
        this.employeeList = list;
    }

    @Override
    protected Employee inputNew() {
        int typeChoice = InputHelper.choose("Chọn chức vụ nhân viên:",
                "Lễ tân (Receptionist)",
                "Quản lý (Manager)",
                "Tạp vụ (Housekeeper)");

        String id = list.nextId(Employee.ID_PREFIX);
        System.out.println("Mã nhân viên mới: " + id);

        String fullName = InputHelper.readString("Họ tên: ");
        String phone = InputHelper.readPhone("Số điện thoại (10 số): ");
        String idCard = InputHelper.readIdCard("CCCD (12 số): ");
        double baseSalary = InputHelper.readPositiveDouble("Lương cơ bản: ");
        LocalDate startDate = InputHelper.readDate("Ngày vào làm");

        switch (typeChoice) {
            case 0: {
                int shiftIdx = InputHelper.choose("Chọn ca trực:", SHIFTS);
                String shift = SHIFTS[shiftIdx];
                int bookingsHandled = InputHelper.readInt("Số booking đã xử lý: ", 0, 100000);
                return new Receptionist(id, fullName, phone, idCard, baseSalary, startDate,
                        shift, bookingsHandled);
            }
            case 1: {
                String department = InputHelper.readString("Phòng ban quản lý: ");
                double allowance = InputHelper.readPositiveDouble("Phụ cấp chức vụ: ");
                return new Manager(id, fullName, phone, idCard, baseSalary, startDate,
                        department, allowance);
            }
            default: {
                int roomsCleaned = InputHelper.readInt("Số phòng đã dọn: ", 0, 100000);
                return new Housekeeper(id, fullName, phone, idCard, baseSalary, startDate,
                        roomsCleaned);
            }
        }
    }

    @Override
    protected Employee inputEdit(Employee old) {
        System.out.println("Đang sửa nhân viên: " + old.getFullName() + " (" + old.getRole() + ")");
        System.out.println("(Nhấn Enter để giữ nguyên giá trị cũ)");

        if (old instanceof Receptionist) {
            Receptionist copy = new Receptionist((Receptionist) old);
            readCommonEdit(copy);
            copy.setShift(chooseShiftOrKeep(copy.getShift()));
            copy.setBookingsHandled(InputHelper.readIntOrKeep("Số booking đã xử lý",
                    copy.getBookingsHandled(), 0, 100000));
            return copy;
        } else if (old instanceof Manager) {
            Manager copy = new Manager((Manager) old);
            readCommonEdit(copy);
            copy.setDepartment(InputHelper.readStringOrKeep("Phòng ban", copy.getDepartment()));
            copy.setAllowance(InputHelper.readPositiveDoubleOrKeep("Phụ cấp chức vụ", copy.getAllowance()));
            return copy;
        } else if (old instanceof Housekeeper) {
            Housekeeper copy = new Housekeeper((Housekeeper) old);
            readCommonEdit(copy);
            copy.setRoomsCleaned(InputHelper.readIntOrKeep("Số phòng đã dọn",
                    copy.getRoomsCleaned(), 0, 100000));
            return copy;
        }
        return old;
    }

    /** Chọn ca trực khi sửa: chọn 1 để giữ nguyên ca cũ. */
    private String chooseShiftOrKeep(String oldShift) {
        String[] options = {"Giữ nguyên (" + oldShift + ")", "Sáng", "Chiều", "Đêm"};
        int choice = InputHelper.choose("Chọn ca trực:", options);
        if (choice == 0) {
            return oldShift;
        }
        return SHIFTS[choice - 1];
    }

    private void readCommonEdit(Employee copy) {
        copy.setFullName(InputHelper.readStringOrKeep("Họ tên", copy.getFullName()));
        copy.setPhone(InputHelper.readPhoneOrKeep("Số điện thoại", copy.getPhone()));
        copy.setIdCard(InputHelper.readIdCardOrKeep("CCCD", copy.getIdCard()));
        copy.setBaseSalary(InputHelper.readPositiveDoubleOrKeep("Lương cơ bản", copy.getBaseSalary()));
        copy.setStartDate(InputHelper.readDateOrKeep("Ngày vào làm", copy.getStartDate()));
    }

    @Override
    protected String[] getExtraOptions() {
        return new String[]{
                "Bảng lương nhân viên (tính lương & tổng quỹ lương)",
                "Lọc nhân viên theo chức vụ",
                "Sắp xếp nhân viên theo lương thực lĩnh"
        };
    }

    @Override
    protected void handleExtra(int index) {
        switch (index) {
            case 0:
                showPayroll();
                break;
            case 1:
                filterByRole();
                break;
            case 2:
                sortBySalary();
                break;
            default:
                break;
        }
    }

    private void showPayroll() {
        List<Employee> all = employeeList.getAll();
        if (all.isEmpty()) {
            System.out.println("Danh sách nhân viên đang trống.");
            return;
        }
        System.out.println("\n============================== BẢNG LƯƠNG NHÂN VIÊN ==============================");
        System.out.printf("%-8s %-22s %-12s %15s %15s %16s%n",
                "Mã NV", "Họ tên", "Chức vụ", "Lương cơ bản", "Thưởng/Phụ cấp", "Thực lĩnh");
        System.out.println("----------------------------------------------------------------------------------");

        for (Employee e : all) {
            double totalSalary = e.calculateSalary(); // Đa hình: tự gọi đúng hàm của lớp con
            double extra = totalSalary - e.getBaseSalary();
            System.out.printf("%-8s %-22s %-12s %15s %15s %16s%n",
                    e.getId(),
                    e.getFullName(),
                    e.getRole(),
                    FileHelper.formatMoney(e.getBaseSalary()),
                    FileHelper.formatMoney(extra),
                    FileHelper.formatMoney(totalSalary));
        }

        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("Tổng số nhân viên : %d người%n", employeeList.size());
        System.out.printf("Tổng quỹ lương    : %s%n", FileHelper.formatMoney(employeeList.getTotalSalary()));
        Employee highest = employeeList.getHighestPaid();
        if (highest != null) {
            System.out.printf("Lương cao nhất    : %s (%s - %s)%n",
                    highest.getFullName(), highest.getRole(),
                    FileHelper.formatMoney(highest.calculateSalary()));
        }
    }

    private void filterByRole() {
        String[] roles = {"Lễ tân", "Quản lý", "Tạp vụ"};
        int choice = InputHelper.choose("Chọn chức vụ cần lọc:", roles);
        List<Employee> result = employeeList.findByRole(roles[choice]);
        employeeList.display(result);
    }

    private void sortBySalary() {
        int order = InputHelper.choose("Chọn thứ tự sắp xếp lương thực lĩnh:",
                "Giảm dần (Cao -> Thấp)",
                "Tăng dần (Thấp -> Cao)");
        List<Employee> sortedList;
        if (order == 0) {
            sortedList = employeeList.sorted((a, b) -> Double.compare(b.calculateSalary(), a.calculateSalary()));
        } else {
            sortedList = employeeList.sorted((a, b) -> Double.compare(a.calculateSalary(), b.calculateSalary()));
        }
        employeeList.display(sortedList);
    }
}