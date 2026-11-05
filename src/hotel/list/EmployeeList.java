package hotel.list;

import hotel.common.BaseList;
import hotel.model.Employee;
import hotel.model.Housekeeper;
import hotel.model.Manager;
import hotel.model.Receptionist;
import hotel.util.FileHelper;

import java.time.LocalDate;
import java.util.List;

/**
 * Danh sách quản lý nhân viên, đọc/ghi từ file employees.txt.
 */
public class EmployeeList extends BaseList<Employee> {

    public EmployeeList() {
        super();
    }

    @Override
    protected String getFileName() {
        return "employees.txt";
    }

    @Override
    protected Employee parseLine(String line) {
        String[] p = FileHelper.split(line);
        if (p.length < 8) {
            return null;
        }
        String type = p[0];
        String id = p[1];
        String fullName = p[2];
        String phone = p[3];
        String idCard = p[4];
        double baseSalary = Double.parseDouble(p[5]);
        LocalDate startDate = FileHelper.parseDate(p[6]);

        switch (type) {
            case Receptionist.TYPE:
                if (p.length != 9) {
                    return null;
                }
                return new Receptionist(id, fullName, phone, idCard, baseSalary, startDate,
                        p[7], Integer.parseInt(p[8]));
            case Manager.TYPE:
                if (p.length != 9) {
                    return null;
                }
                return new Manager(id, fullName, phone, idCard, baseSalary, startDate,
                        p[7], Double.parseDouble(p[8]));
            case Housekeeper.TYPE:
                if (p.length != 8) {
                    return null;
                }
                return new Housekeeper(id, fullName, phone, idCard, baseSalary, startDate,
                        Integer.parseInt(p[7]));
            default:
                return null;
        }
    }

    @Override
    protected String getHeader() {
        return Employee.header();
    }

    /**
     * Lọc danh sách nhân viên theo chức vụ (hỗ trợ cả tên hiển thị "Lễ tân", "Quản lý", "Tạp vụ"
     * hoặc mã loại "RECEPTIONIST", "MANAGER", "HOUSEKEEPER").
     */
    public List<Employee> findByRole(String role) {
        if (role == null) {
            return filter(e -> false);
        }
        String key = role.trim().toLowerCase();
        return filter(e -> e.getRole().toLowerCase().equals(key)
                || (e instanceof Receptionist && Receptionist.TYPE.toLowerCase().equals(key))
                || (e instanceof Manager && Manager.TYPE.toLowerCase().equals(key))
                || (e instanceof Housekeeper && Housekeeper.TYPE.toLowerCase().equals(key)));
    }

    /**
     * Tính tổng quỹ lương của toàn bộ nhân viên (thể hiện tính đa hình qua calculateSalary()).
     */
    public double getTotalSalary() {
        double total = 0.0;
        for (Employee e : items) {
            total += e.calculateSalary();
        }
        return total;
    }

    /**
     * Tìm nhân viên có mức lương thực lĩnh cao nhất.
     */
    public Employee getHighestPaid() {
        if (items.isEmpty()) {
            return null;
        }
        Employee highest = items.get(0);
        for (Employee e : items) {
            if (e.calculateSalary() > highest.calculateSalary()) {
                highest = e;
            }
        }
        return highest;
    }
}