package hotel.model;

import hotel.util.FileHelper;

/**
 * Khách VIP: có hạng thành viên và điểm tích lũy, được giảm giá theo hạng.
 */
public class VipCustomer extends Customer {
    public static final String TYPE = "VIP";

    private static int createdCount = 0;

    private VipLevel vipLevel;
    private int points;

    public VipCustomer() {
        super();
        this.vipLevel = VipLevel.SILVER;
        this.points = 0;
    }

    public VipCustomer(String id, String fullName, String phone, String idCard,
                       String nationality, String email, VipLevel vipLevel, int points) {
        super(id, fullName, phone, idCard, nationality, email);
        this.vipLevel = vipLevel;
        this.points = points;
        createdCount++;
    }

    /** Nâng một khách thường thành VIP, giữ nguyên thông tin cá nhân. */
    public VipCustomer(Customer customer, VipLevel vipLevel, int points) {
        this(customer.getId(), customer.getFullName(), customer.getPhone(), customer.getIdCard(),
                customer.getNationality(), customer.getEmail(), vipLevel, points);
    }

    public VipCustomer(VipCustomer other) {
        this(other.id, other.fullName, other.phone, other.idCard,
                other.getNationality(), other.getEmail(), other.vipLevel, other.points);
    }

    public static int getCreatedCount() {
        return createdCount;
    }

    @Override
    public String getRole() {
        return "VIP " + vipLevel.getDisplayName();
    }

    /** Ghi đè: giảm giá theo hạng VIP. */
    @Override
    public double getDiscountRate() {
        return vipLevel.getDiscountRate();
    }

    @Override
    public boolean isVip() {
        return true;
    }

    /**
     * Cộng điểm tích lũy. Nếu đủ điểm lên hạng cao hơn thì tự nâng hạng (không bao giờ tự hạ hạng).
     *
     * @return true nếu hạng được nâng lên
     */
    public boolean addPoints(int add) {
        if (add > 0) {
            points += add;
        }
        VipLevel reached = VipLevel.fromPoints(points);
        if (reached.ordinal() > vipLevel.ordinal()) {
            vipLevel = reached;
            return true;
        }
        return false;
    }

    @Override
    public String toFileString() {
        // VIP|maKH|hoTen|sdt|cccd|quocTich|email|hangVip|diem
        return FileHelper.join(TYPE, baseFileString(), getNationality(), getEmail(),
                vipLevel.name(), points);
    }

    public VipLevel getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(VipLevel vipLevel) {
        this.vipLevel = vipLevel;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("Điểm: %d | Giảm %.0f%%", points, getDiscountRate() * 100);
    }
}
