package hotel.common;

/**
 * Đối tượng có mã (ID) duy nhất. Mọi phần tử trong {@link BaseList} đều phải có mã.
 */
public interface Identifiable {
    String getId();
}
