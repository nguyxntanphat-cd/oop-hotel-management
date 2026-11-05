package hotel.list;

import hotel.common.BaseList;
import hotel.model.Service;
import hotel.util.FileHelper;

import java.util.List;

/**
 * Danh sách dịch vụ, đọc/ghi file services.txt.
 */
public class ServiceList extends BaseList<Service> {

    public ServiceList() {
        super();
    }

    @Override
    protected String getFileName() {
        return "services.txt";
    }

    @Override
    protected Service parseLine(String line) {
        String[] p = FileHelper.split(line);
        if (p.length != 5) {
            return null;
        }
        if (!p[4].equals("true") && !p[4].equals("false")) {
            return null;
        }
        return new Service(p[0], p[1], Double.parseDouble(p[2]), p[3], Boolean.parseBoolean(p[4]));
    }

    @Override
    protected String getHeader() {
        return Service.header();
    }

    /** Các dịch vụ đang bán (module Đặt phòng dùng khi khách gọi dịch vụ). */
    public List<Service> getActiveServices() {
        return filter(s -> s.isActive());
    }

    public List<Service> findByPriceRange(double min, double max) {
        return filter(s -> s.getPrice() >= min && s.getPrice() <= max);
    }
}
