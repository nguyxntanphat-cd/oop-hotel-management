# Hướng dẫn viết một module trên khung chung

Khung chung (issue #1) đã cài sẵn **xem / thêm / sửa / xóa / tìm kiếm / đọc-ghi file**.
Mỗi module chỉ cần viết **3 lớp**: lớp đối tượng (model), lớp danh sách (list), lớp menu.

Ví dụ dưới đây dùng lớp `Service` (#4), **rút gọn còn 3 thuộc tính** cho dễ đọc. Các module khác làm y hệt.
Thuộc tính, hàm public và định dạng file **đầy đủ** của từng lớp xem ở [`THIET_KE.md`](THIET_KE.md) mục 3 và mục 5.

## Bước 1 — Lớp đối tượng (`hotel.model`)

Phải implement 3 interface `Identifiable`, `FileSerializable`, `Searchable`
(hoặc kế thừa `Person` — `Person` đã implement sẵn cả 3).

```java
package hotel.model;

import hotel.common.FileSerializable;
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.FileHelper;

public class Service implements Identifiable, FileSerializable, Searchable {
    public static final String ID_PREFIX = "DV";
    private static int createdCount = 0;          // thuộc tính static

    private String id;
    private String name;
    private double price;

    public Service() {                            // constructor mặc định
        this("", "", 0);
    }

    public Service(String id, String name, double price) {   // constructor đầy đủ
        this.id = id;
        this.name = name;
        this.price = price;
        createdCount++;
    }

    public Service(Service other) {               // copy constructor (dùng khi sửa)
        this(other.id, other.name, other.price);
    }

    public static int getCreatedCount() {         // phương thức static
        return createdCount;
    }

    @Override
    public String getId() { return id; }

    @Override
    public String toFileString() {                // DV001|Ăn sáng|150000.0
        return FileHelper.join(id, name, price);
    }

    @Override
    public boolean matches(String keyword) {      // keyword đã là chữ thường
        return id.toLowerCase().contains(keyword) || name.toLowerCase().contains(keyword);
    }

    public static String header() {               // tiêu đề bảng, khớp với toString()
        return String.format("%-8s %-25s %15s", "Mã", "Tên dịch vụ", "Đơn giá");
    }

    @Override
    public String toString() {
        return String.format("%-8s %-25s %15s", id, name, FileHelper.formatMoney(price));
    }

    // getter / setter ...
}
```

## Bước 2 — Lớp danh sách (`hotel.list`)

Kế thừa `BaseList<T>`, chỉ cần 2 hàm trừu tượng (+ `getHeader` nếu muốn có tiêu đề bảng).

```java
package hotel.list;

import hotel.common.BaseList;
import hotel.model.Service;
import hotel.util.FileHelper;

import java.util.List;

public class ServiceList extends BaseList<Service> {

    @Override
    protected String getFileName() {
        return "services.txt";
    }

    @Override
    protected Service parseLine(String line) {
        String[] p = FileHelper.split(line);
        if (p.length != 3) {
            return null;                          // sai định dạng -> bỏ qua dòng
        }
        return new Service(p[0], p[1], Double.parseDouble(p[2]));
        // Lỗi NumberFormatException / DateTimeParseException được BaseList bắt và bỏ qua dòng
    }

    @Override
    protected String getHeader() {
        return Service.header();
    }

    // Các hàm riêng: dùng filter / sorted có sẵn
    public List<Service> findByPriceRange(double min, double max) {
        return filter(s -> s.getPrice() >= min && s.getPrice() <= max);
    }
}
```

**Lớp có nhiều loại con** (Room, Customer, Employee): ghi loại ở trường đầu, `parseLine` dựa vào đó để tạo đúng lớp con:

```java
@Override
protected Room parseLine(String line) {
    String[] p = FileHelper.split(line);
    switch (p[0]) {
        case "STANDARD": return new StandardRoom(...);
        case "DELUXE":   return new DeluxeRoom(...);
        case "SUITE":    return new SuiteRoom(...);
        default:         return null;
    }
}
```

## Bước 3 — Lớp menu (`hotel.menu`)

Kế thừa `CrudMenu<T>`: chỉ cần viết cách **nhập mới** và cách **sửa**. 5 chức năng chuẩn đã có.

```java
package hotel.menu;

import hotel.list.ServiceList;
import hotel.model.Service;
import hotel.util.InputHelper;

public class ServiceMenu extends CrudMenu<Service> {

    public ServiceMenu(ServiceList list) {
        super("QUẢN LÝ DỊCH VỤ", list);
    }

    @Override
    protected Service inputNew() {
        String id = list.nextId(Service.ID_PREFIX);    // DV001, DV002, ...
        System.out.println("Mã mới: " + id);
        String name = InputHelper.readString("Tên dịch vụ: ");
        double price = InputHelper.readPositiveDouble("Đơn giá: ");
        return new Service(id, name, price);
    }

    @Override
    protected Service inputEdit(Service old) {
        Service s = new Service(old);                    // sửa trên bản sao
        s.setName(InputHelper.readStringOrKeep("Tên dịch vụ", s.getName()));
        s.setPrice(InputHelper.readPositiveDoubleOrKeep("Đơn giá", s.getPrice()));
        return s;
    }

    // ----- Tùy chọn: chức năng riêng -----
    @Override
    protected String[] getExtraOptions() {
        return new String[]{"Sắp xếp theo giá"};       // hiện thành mục 6
    }

    @Override
    protected void handleExtra(int index) {
        if (index == 0) {
            list.display(list.sorted((a, b) -> Double.compare(a.getPrice(), b.getPrice())));
        }
    }

    // ----- Tùy chọn: chặn xóa -----
    @Override
    protected boolean canRemove(Service s) {
        return true;
    }
}
```

## Bước 4 — Gắn vào chương trình

1. `HotelData.java`: bỏ comment 3 dòng TODO của module mình (khai báo danh sách, `loadFromFile`, `saveToFile`).
   Module khác dùng danh sách của mình qua `HotelData.SERVICES`, nên phải có đủ hàm public ở `THIET_KE.md` mục 3.
2. `MainMenu.java`: thay `notReady(x);` bằng `new ServiceMenu(HotelData.SERVICES).run();`.
3. Thêm dữ liệu mẫu vào `data/<file>.txt`.

## Các tiện ích có sẵn

| Lớp | Hàm hay dùng |
|---|---|
| `InputHelper` | `readString`, `readInt(min,max)`, `readPositiveDouble`, `readDate`, `readDateAfter`, `readPhone`, `readIdCard`, `choose(title, options...)`, `confirm`, và bản `...OrKeep` cho chức năng sửa |
| `FileHelper` | `split(line)`, `join(fields...)`, `parseDate`, `formatDate`, `formatMoney` |
| `BaseList` | `findById`, `exists`, `search`, `filter(điều kiện)`, `sorted(comparator)`, `display(list)`, `nextId(prefix)`, `getAll`, `size` |
| `AppConfig` | `SEPARATOR`, `DATE_FORMAT`, `VAT_RATE`, `HOTEL_NAME` |
| `Person` | `baseFileString()` (= `id\|hoTen\|sdt\|cccd`), `header()` |

## Lưu ý

- **Không** tự tạo `new Scanner(System.in)` — luôn dùng `InputHelper`.
- Chuỗi người dùng nhập không được chứa `|` (đã được `InputHelper.readString` chặn).
- `matches(keyword)` nhận từ khóa đã ở dạng chữ thường.
- `CrudMenu` tự lưu file sau mỗi lần thêm/sửa/xóa và khi rời menu.
