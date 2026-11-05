package hotel.model;

public class UsedService {
    private static int createdCount = 0;

    private String serviceId;
    private int quantity;
    private double unitPrice;

    public UsedService() {
        this("", 0, 0.0);
    }

    public UsedService(String serviceId, int quantity, double unitPrice) {
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        createdCount++;
    }

    public UsedService(UsedService other) {
        this(other.serviceId, other.quantity, other.unitPrice);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getAmount() {
        return quantity * unitPrice;
    }
}
