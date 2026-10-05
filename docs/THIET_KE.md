# Thiết kế lớp

Tài liệu này là **"hợp đồng" chung** giữa các thành viên: tên lớp, package, phương thức chính.
Muốn đổi tên / chữ ký phương thức dùng chung thì báo cả nhóm trước.

## 1. Sơ đồ kế thừa

```
«interface» Identifiable      getId()
«interface» FileSerializable  toFileString()
«interface» Searchable        matches(keyword)
«interface» Payable           calculateTotal()
«interface» Manageable<T>     displayAll() add() update() remove() findById() search()

«abstract» Person  (Identifiable, FileSerializable, Searchable)
 ├── Customer
 │    └── VipCustomer
 └── «abstract» Employee
      ├── Receptionist
      ├── Manager
      └── Housekeeper

«abstract» Room  (Identifiable, FileSerializable, Searchable)
 ├── StandardRoom
 ├── DeluxeRoom
 └── SuiteRoom

Service      (Identifiable, FileSerializable, Searchable)
UsedService  (dịch vụ khách đã dùng trong 1 lần đặt phòng)
Booking      (Identifiable, FileSerializable, Searchable, Payable)
Invoice      (Identifiable, FileSerializable, Searchable, Payable)

«abstract» BaseList<T>  (Manageable<T>)   ← lớp mảng đối tượng dùng chung
 ├── RoomList  ├── CustomerList  ├── EmployeeList
 ├── ServiceList  ├── BookingList  └── InvoiceList

«abstract» Menu
 ├── MainMenu
 ├── StatisticsMenu
 └── «abstract» CrudMenu<T>     ← sẵn 5 chức năng Xem/Thêm/Sửa/Xóa/Tìm
      ├── RoomMenu  ├── CustomerMenu  ├── EmployeeMenu
      ├── ServiceMenu  ├── BookingMenu  └── InvoiceMenu

Tiện ích static: AppConfig, InputHelper, FileHelper, IdGenerator, HotelData
Enum: RoomStatus, BookingStatus
```

## 2. Danh sách lớp theo package

| Package | Lớp | Loại | Người làm |
|---|---|---|---|
| `hotel` | `Main`, `HotelData` | class (static) | TV1 |
| `hotel.common` | `Identifiable`, `FileSerializable`, `Searchable`, `Payable`, `Manageable<T>` | interface | TV1 |
| `hotel.common` | `BaseList<T>` | abstract class | TV1 |
| `hotel.util` | `AppConfig`, `InputHelper`, `FileHelper`, `IdGenerator` | class (static) | TV1 |
| `hotel.menu` | `Menu`, `CrudMenu<T>` | abstract class | TV1 |
| `hotel.menu` | `MainMenu`, `StatisticsMenu` | class | TV1 |
| `hotel.model` | `Person` | abstract class | TV1 |
| `hotel.model` | `Room`, `StandardRoom`, `DeluxeRoom`, `SuiteRoom`, `RoomStatus` | abstract / class / enum | TV2 |
| `hotel.list` / `hotel.menu` | `RoomList`, `RoomMenu` | class | TV2 |
| `hotel.model` | `Customer`, `VipCustomer` | class | TV3 |
| `hotel.model` | `Service` | class | TV3 |
| `hotel.list` / `hotel.menu` | `CustomerList`, `CustomerMenu`, `ServiceList`, `ServiceMenu` | class | TV3 |
| `hotel.model` | `Employee`, `Receptionist`, `Manager`, `Housekeeper` | abstract / class | TV4 |
| `hotel.list` / `hotel.menu` | `EmployeeList`, `EmployeeMenu` | class | TV4 |
| `hotel.model` | `Booking`, `BookingStatus`, `UsedService`, `Invoice` | class / enum | TV5 |
| `hotel.list` / `hotel.menu` | `BookingList`, `InvoiceList`, `BookingMenu`, `InvoiceMenu` | class | TV5 |

## 3. Chữ ký phương thức chính (đa hình / trừu tượng)

```java
// Person
public abstract String getRole();                 // "Khách thường", "Khách VIP", "Lễ tân", ...

// Customer / VipCustomer
public double getDiscountRate();                  // Customer: 0, VipCustomer: theo hạng (override)

// Employee
public abstract double calculateSalary();         // mỗi loại nhân viên tính lương khác nhau

// Room
public abstract double getPricePerNight();        // Standard / Deluxe / Suite giá khác nhau
public abstract String getRoomType();

// Booking, Invoice  (implements Payable)
public double calculateTotal();

// BaseList<T>
protected abstract String getFileName();          // vd "rooms.txt"
protected abstract T parseLine(String line);      // 1 dòng file -> 1 đối tượng
public void loadFromFile();
public boolean saveToFile();

// Menu
protected abstract String[] getOptions();
protected abstract void handle(int choice);

// CrudMenu<T>
protected abstract T inputNew();                  // nhập đối tượng mới
protected abstract T inputEdit(T old);            // nhập sửa (Enter = giữ nguyên)
```

## 4. Quy ước file dữ liệu (`data/*.txt`)

- Mã hóa **UTF-8**, mỗi dòng 1 đối tượng, các trường phân cách bằng `|`. Dòng bắt đầu bằng `#` là chú thích.
- Ngày dạng `dd/MM/yyyy`. Số tiền là số thực, không có dấu phẩy ngăn cách.
- Lớp có nhiều loại con thì **trường đầu tiên là loại** để biết tạo đối tượng lớp nào khi đọc.

| File | Định dạng 1 dòng |
|---|---|
| `rooms.txt` | `LOAI\|soPhong\|tang\|trangThai\|<trường riêng>` — vd `DELUXE\|201\|2\|AVAILABLE\|true` |
| `customers.txt` | `LOAI\|maKH\|hoTen\|sdt\|cccd\|quocTich\|<trường riêng>` — vd `VIP\|KH002\|Trần B\|0901...\|0792...\|Việt Nam\|GOLD` |
| `employees.txt` | `LOAI\|maNV\|hoTen\|sdt\|cccd\|luongCoBan\|<trường riêng>` |
| `services.txt` | `maDV\|tenDV\|donGia\|donVi` |
| `bookings.txt` | `maDP\|maKH\|soPhong\|ngayNhan\|ngayTra\|trangThai\|maDV:soLuong,maDV:soLuong` |
| `invoices.txt` | `maHD\|maDP\|ngayLap\|tienPhong\|tienDichVu\|giamGia\|VAT\|tongTien` |

## 5. Quy ước mã tự sinh

| Đối tượng | Tiền tố | Ví dụ |
|---|---|---|
| Khách hàng | `KH` | KH001 |
| Nhân viên | `NV` | NV001 |
| Dịch vụ | `DV` | DV001 |
| Đặt phòng | `DP` | DP001 |
| Hóa đơn | `HD` | HD001 |
| Phòng | số phòng | 101, 202 |
