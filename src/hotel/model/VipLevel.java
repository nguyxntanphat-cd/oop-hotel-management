package hotel.model;

/**
 * Hạng thành viên VIP. Mỗi hạng có tỉ lệ giảm giá và số điểm tối thiểu để đạt hạng.
 */
public enum VipLevel {
    SILVER("Bạc", 0.05, 0),
    GOLD("Vàng", 0.10, 100),
    PLATINUM("Bạch kim", 0.15, 300);

    private final String displayName;
    private final double discountRate;
    private final int minPoints;

    VipLevel(String displayName, double discountRate, int minPoints) {
        this.displayName = displayName;
        this.discountRate = discountRate;
        this.minPoints = minPoints;
    }

    /** Tỉ lệ giảm giá khi lập hóa đơn: 0.05, 0.10, 0.15. */
    public double getDiscountRate() {
        return discountRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinPoints() {
        return minPoints;
    }

    /** Hạng cao nhất mà số điểm này đạt được. */
    public static VipLevel fromPoints(int points) {
        VipLevel result = SILVER;
        for (VipLevel level : values()) {
            if (points >= level.minPoints) {
                result = level;
            }
        }
        return result;
    }
}
