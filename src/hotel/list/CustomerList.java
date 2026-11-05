package hotel.list;

import hotel.common.BaseList;
import hotel.model.Customer;
import hotel.model.VipCustomer;
import hotel.model.VipLevel;
import hotel.util.FileHelper;

import java.util.List;

/**
 * Danh sách khách hàng, đọc/ghi file customers.txt.
 */
public class CustomerList extends BaseList<Customer> {

    public CustomerList() {
        super();
    }

    @Override
    protected String getFileName() {
        return "customers.txt";
    }

    @Override
    protected Customer parseLine(String line) {
        String[] p = FileHelper.split(line);
        if (p.length < 7) {
            return null;
        }
        switch (p[0]) {
            case Customer.TYPE:
                if (p.length != 7) {
                    return null;
                }
                return new Customer(p[1], p[2], p[3], p[4], p[5], p[6]);
            case VipCustomer.TYPE:
                if (p.length != 9) {
                    return null;
                }
                return new VipCustomer(p[1], p[2], p[3], p[4], p[5], p[6],
                        VipLevel.valueOf(p[7]), Integer.parseInt(p[8]));
            default:
                return null;
        }
    }

    @Override
    protected String getHeader() {
        return Customer.header();
    }

    /** Tìm theo số điện thoại, null nếu không có. */
    public Customer findByPhone(String phone) {
        if (phone == null) {
            return null;
        }
        for (Customer c : items) {
            if (c.getPhone().equals(phone.trim())) {
                return c;
            }
        }
        return null;
    }

    /** Tìm theo CCCD, null nếu không có. */
    public Customer findByIdCard(String idCard) {
        if (idCard == null) {
            return null;
        }
        for (Customer c : items) {
            if (c.getIdCard().equals(idCard.trim())) {
                return c;
            }
        }
        return null;
    }

    public List<Customer> getVipCustomers() {
        return filter(c -> c.isVip());
    }
}
