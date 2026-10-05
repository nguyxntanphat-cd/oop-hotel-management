# 🏨 OOP Hotel Management

Đồ án môn **Lập trình hướng đối tượng** — Chương trình **quản lý khách sạn** viết bằng **Java (console)**.

Nhóm 5 thành viên. Bảng phân công: [`docs/PHAN_CONG.md`](docs/PHAN_CONG.md) · Thiết kế lớp: [`docs/THIET_KE.md`](docs/THIET_KE.md) · Hướng dẫn viết module: [`docs/HUONG_DAN_MODULE.md`](docs/HUONG_DAN_MODULE.md) · Quy trình làm việc: [`CONTRIBUTING.md`](CONTRIBUTING.md)

## Chức năng

| Module | Xem | Thêm | Sửa | Xóa | Tìm kiếm | Chức năng riêng |
|---|:-:|:-:|:-:|:-:|:-:|---|
| Phòng | ✅ | ✅ | ✅ | ✅ | ✅ | Lọc theo loại / trạng thái / khoảng giá |
| Khách hàng | ✅ | ✅ | ✅ | ✅ | ✅ | Nâng hạng VIP |
| Nhân viên | ✅ | ✅ | ✅ | ✅ | ✅ | Tính lương, bảng lương |
| Dịch vụ | ✅ | ✅ | ✅ | ✅ | ✅ | |
| Đặt phòng | ✅ | ✅ | ✅ | ✅ (hủy) | ✅ | Check-in, thêm dịch vụ, check-out |
| Hóa đơn | ✅ | (tự sinh khi check-out) | ✅ | ✅ | ✅ | Xem chi tiết, doanh thu |
| Thống kê | | | | | | Doanh thu, công suất phòng, top khách |

## Đáp ứng yêu cầu đồ án

| Yêu cầu | Cài đặt trong project |
|---|---|
| ≥ 15 lớp đối tượng | ~35 lớp, xem [`docs/THIET_KE.md`](docs/THIET_KE.md) |
| Lớp có constructor | Mọi lớp có constructor mặc định + constructor đủ tham số (+ copy constructor cho lớp model) |
| Kế thừa hợp lý | `Person → Customer → VipCustomer`, `Person → Employee → Receptionist/Manager/Housekeeper`, `Room → Standard/Deluxe/SuiteRoom`, `BaseList → *List`, `Menu → CrudMenu → *Menu` |
| Đa hình | `Room.getPricePerNight()`, `Employee.calculateSalary()`, `Customer.getDiscountRate()`, `Menu.handle()`… gọi qua tham chiếu lớp cha |
| Lớp mảng đối tượng | `RoomList`, `CustomerList`, `EmployeeList`, `ServiceList`, `BookingList`, `InvoiceList` (kế thừa `BaseList<T>`) |
| Đọc/ghi file text | Mỗi danh sách đọc/ghi 1 file trong `data/`, phân cách bằng `\|` |
| Thuộc tính / phương thức static | `AppConfig` (hằng số), `InputHelper`, `FileHelper`, `IdGenerator` (phương thức static), `HotelData` (danh sách static), biến đếm `createdCount` trong lớp model |
| Lớp trừu tượng, hàm trừu tượng | `Person`, `Employee`, `Room`, `BaseList`, `Menu`, `CrudMenu` |
| Interface | `Identifiable`, `FileSerializable`, `Searchable`, `Manageable<T>`, `Payable` |

## Cấu trúc thư mục

```
oop-hotel-management/
├── src/hotel/
│   ├── Main.java          # Điểm bắt đầu chương trình
│   ├── HotelData.java     # Giữ tất cả danh sách (static) để các module dùng chung
│   ├── common/            # Interface + lớp trừu tượng dùng chung (BaseList)
│   ├── model/             # Lớp đối tượng: Person, Customer, Room, Booking, ...
│   ├── list/              # Lớp danh sách: RoomList, CustomerList, ...
│   ├── menu/              # Menu console: MainMenu, RoomMenu, ...
│   └── util/              # Tiện ích static: nhập liệu, đọc/ghi file, sinh mã
├── data/                  # File dữ liệu text (*.txt)
├── docs/                  # Phân công, thiết kế, UML, báo cáo
├── scripts/               # run.sh (Linux/macOS), run.bat (Windows)
└── .github/               # Mẫu issue/PR, CI kiểm tra biên dịch
```

## Chạy chương trình

Yêu cầu: **JDK 8+**. Chạy từ thư mục gốc của repo (để chương trình tìm thấy thư mục `data/`).

```bash
# Linux / macOS
./scripts/run.sh

# Windows
scripts\run.bat
```

Hoặc mở bằng IntelliJ / Eclipse / NetBeans: đánh dấu `src` là *Sources Root*, chạy `hotel.Main`, đặt *Working directory* là thư mục gốc repo và encoding UTF-8.

## Thành viên

| # | Họ tên | MSSV | GitHub | Phụ trách |
|---|---|---|---|---|
| TV1 | Nguyễn Tấn Phát | 3124560068 | [@nguyxntanphat-cd](https://github.com/nguyxntanphat-cd) | Trưởng nhóm — Khung chung, Thống kê, tích hợp |
| TV2 | Nguyễn Duy Khang | 3125410076 | [@duykhang3224-sgp](https://github.com/duykhang3224-sgp) | Module Phòng |
| TV3 | Phạm Phú Khang | 3125410078 | [@khangpham4399-art](https://github.com/khangpham4399-art) | Module Khách hàng + Dịch vụ |
| TV4 | Bùi Khắc Cao Văn | 3125410202 | [@Vanes-void](https://github.com/Vanes-void) | Module Nhân viên |
| TV5 | Đỗ Duy Anh | 3125410003 | [@doduyanh258-hash](https://github.com/doduyanh258-hash) | Module Đặt phòng + Hóa đơn |
