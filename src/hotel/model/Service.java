package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.FileHelper;

/**
 * Dịch vụ khách sạn (ăn sáng, giặt ủi, spa, ...).
 */
public class Service implements Identifiable, FileSerializable, Searchable {
    public static final String ID_PREFIX = "DV";

    private static int createdCount = 0;

    private String id;
    private String name;
    private double price;
    private String unit;
    private boolean active;

    public Service() {
        this("", "", 0.0, "", true);
    }

    public Service(String id, String name, double price, String unit, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.active = active;
        createdCount++;
    }

    public Service(Service other) {
        this(other.id, other.name, other.price, other.unit, other.active);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toFileString() {
        // DV001|Ăn sáng buffet|150000.0|suất|true
        return FileHelper.join(id, name, price, unit, active);
    }

    @Override
    public boolean matches(String keyword) {
        return id.toLowerCase().contains(keyword)
                || name.toLowerCase().contains(keyword)
                || unit.toLowerCase().contains(keyword);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static String header() {
        return String.format("%-8s %-28s %14s %-8s %-12s", "Mã", "Tên dịch vụ", "Đơn giá", "Đơn vị", "Trạng thái");
    }

    @Override
    public String toString() {
        return String.format("%-8s %-28s %14s %-8s %-12s",
                id, name, FileHelper.formatMoney(price), unit, active ? "Đang bán" : "Ngừng bán");
    }
}
