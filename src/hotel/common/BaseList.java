package hotel.common;

import hotel.util.FileHelper;
import hotel.util.IdGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Lớp danh sách (mảng đối tượng) dùng chung cho mọi module.
 * <p>
 * Đã cài sẵn: xem, thêm, sửa, xóa, tìm theo mã, tìm theo từ khóa, lọc, sắp xếp, đọc/ghi file.
 * Lớp con chỉ cần cài đặt 2 hàm trừu tượng {@link #getFileName()} và {@link #parseLine(String)}.
 *
 * @param <T> kiểu phần tử: phải có mã, ghi được ra file và tìm kiếm được
 */
public abstract class BaseList<T extends Identifiable & FileSerializable & Searchable>
        implements Manageable<T> {

    protected final List<T> items;

    public BaseList() {
        this.items = new ArrayList<>();
    }

    // ===================== Hàm trừu tượng =====================

    /** Tên file dữ liệu trong thư mục data/, ví dụ "rooms.txt". */
    protected abstract String getFileName();

    /**
     * Chuyển 1 dòng trong file thành đối tượng.
     * Trả về null nếu dòng sai định dạng (dòng đó sẽ bị bỏ qua khi đọc file).
     */
    protected abstract T parseLine(String line);

    /** Dòng tiêu đề in trước bảng khi hiển thị. Lớp con nên ghi đè cho khớp với toString(). */
    protected String getHeader() {
        return null;
    }

    // ===================== Xem =====================

    @Override
    public void displayAll() {
        display(items);
    }

    /** In một danh sách bất kỳ (dùng lại cho kết quả tìm kiếm / lọc). */
    public void display(List<T> list) {
        if (list.isEmpty()) {
            System.out.println("(Danh sách trống)");
            return;
        }
        String header = getHeader();
        if (header != null) {
            System.out.println(header);
            System.out.println(repeat('-', header.length()));
        }
        for (T item : list) {
            System.out.println(item);
        }
        System.out.println("Tổng cộng: " + list.size());
    }

    // ===================== Thêm / Sửa / Xóa =====================

    @Override
    public boolean add(T item) {
        if (item == null || item.getId() == null || findById(item.getId()) != null) {
            return false;
        }
        items.add(item);
        return true;
    }

    @Override
    public boolean update(String id, T newItem) {
        if (id == null || newItem == null) {
            return false;
        }
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equalsIgnoreCase(id.trim())) {
                items.set(i, newItem);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean remove(String id) {
        T item = findById(id);
        return item != null && items.remove(item);
    }

    // ===================== Tìm kiếm / Lọc / Sắp xếp =====================

    @Override
    public T findById(String id) {
        if (id == null) {
            return null;
        }
        for (T item : items) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    public boolean exists(String id) {
        return findById(id) != null;
    }

    @Override
    public List<T> search(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (key.isEmpty() || item.matches(key)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Lọc theo điều kiện bất kỳ, ví dụ:
     * {@code rooms.filter(r -> r.getStatus() == RoomStatus.AVAILABLE)}
     */
    public List<T> filter(Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Trả về bản sao đã sắp xếp (không làm thay đổi thứ tự gốc), ví dụ:
     * {@code services.sorted(Comparator.comparingDouble(Service::getPrice))}
     */
    public List<T> sorted(Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(items);
        copy.sort(comparator);
        return copy;
    }

    // ===================== Đọc / Ghi file =====================

    /** Đọc lại toàn bộ danh sách từ file (xóa dữ liệu cũ trong bộ nhớ). */
    public void loadFromFile() {
        items.clear();
        String path = FileHelper.dataPath(getFileName());
        int lineNumber = 0;
        for (String line : FileHelper.readLines(path)) {
            lineNumber++;
            T item = null;
            try {
                item = parseLine(line);
            } catch (RuntimeException e) {
                // parseLine lỗi (sai số, sai ngày, ...) thì coi như dòng sai định dạng
            }
            if (item == null) {
                System.out.println("[Cảnh báo] " + getFileName() + " dòng dữ liệu thứ " + lineNumber
                        + " sai định dạng, bỏ qua: " + line);
            } else if (!add(item)) {
                System.out.println("[Cảnh báo] " + getFileName() + " trùng mã " + item.getId() + ", bỏ qua.");
            }
        }
    }

    /** Ghi đè toàn bộ danh sách xuống file. */
    public boolean saveToFile() {
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(item.toFileString());
        }
        return FileHelper.writeLines(FileHelper.dataPath(getFileName()), lines);
    }

    // ===================== Khác =====================

    /** Sinh mã mới kế tiếp, ví dụ nextId("KH") -> "KH006". */
    public String nextId(String prefix) {
        return IdGenerator.next(prefix, items);
    }

    /** Danh sách chỉ đọc, dùng khi module khác cần duyệt qua. */
    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
