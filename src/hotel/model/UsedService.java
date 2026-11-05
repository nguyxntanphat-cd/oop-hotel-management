package hotel.model;

public class UsedService {
    private String serviceId;
    private int quantity;
    private double unitPrice;

    public UsedService(String serviceId, int quantity, double unitPrice) {
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getServiceId() { return serviceId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    
    public double getAmount() {
        return quantity * unitPrice;
    }
}