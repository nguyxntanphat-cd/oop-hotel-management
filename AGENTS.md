# Hướng dẫn cho AI coding agent

File này dành cho các AI (Claude Code, Codex, Cursor, Copilot, Gemini...) làm việc trong repo.
Đọc hết file này trước khi viết code.

## Bối cảnh

- Đồ án môn **Lập trình hướng đối tượng** của một nhóm 5 sinh viên: chương trình **quản lý khách sạn**, **Java console**.
- Người dùng đang nói chuyện với bạn là **sinh viên**, và họ sẽ bị **vấn đáp** về chính phần code bạn viết.
  Vì vậy hãy ưu tiên code **đơn giản, dễ đọc, dễ giải thích** hơn code ngắn gọn hay "xịn".
- Trả lời người dùng bằng **tiếng Việt**.

## Yêu cầu của đồ án (giảng viên chấm)

Đủ chức năng **xem, thêm, sửa, xóa, tìm kiếm**; ≥ 15 lớp; mọi lớp có **constructor**; **kế thừa** hợp lý;
**đa hình**; **lớp danh sách** (mảng đối tượng); **đọc/ghi file text**; **thuộc tính + phương thức static**;
**lớp trừu tượng + hàm trừu tượng**; **interface**. Chương trình bị lỗi hoặc thiếu chức năng thì bị trừ điểm.

## Đọc trước khi code (theo thứ tự)

1. `docs/HUONG_DAN_MODULE.md`: cách viết 1 module trên khung có sẵn, có code mẫu. **Quan trọng nhất.**
2. `docs/THIET_KE.md`: tên lớp, package, chữ ký hàm, **định dạng file dữ liệu**, tiền tố mã. Đây là "hợp đồng" chung của nhóm.
3. Issue mà người dùng được giao (xem mục "Khi được giao một issue").
4. Code khung: `src/hotel/common/BaseList.java`, `src/hotel/menu/CrudMenu.java`, `src/hotel/model/Person.java`,
   `src/hotel/util/InputHelper.java`, `src/hotel/util/FileHelper.java`, `src/hotel/HotelData.java`, `src/hotel/menu/MainMenu.java`.
5. `CONTRIBUTING.md`: quy tắc nhánh và Pull Request.

## Khi được giao một issue (ví dụ "làm issue #2")

1. Lấy nội dung issue tại `https://github.com/nguyxntanphat-cd/oop-hotel-management/issues/<số>`.
   Nếu không truy cập được thì **hỏi người dùng dán nội dung issue**. Không tự đoán yêu cầu.
2. Kiểm tra đang ở nhánh riêng `feature/<số>-<tên>`, không phải `main`. Nếu đang ở `main` thì tạo nhánh mới.
3. Làm lần lượt từng mục trong checklist của issue, theo đúng mẫu ở `docs/HUONG_DAN_MODULE.md`:
   lớp model (`hotel.model`), lớp danh sách kế thừa `BaseList<T>` (`hotel.list`), lớp menu kế thừa `CrudMenu<T>` (`hotel.menu`).
4. Gắn module vào chương trình: bỏ comment **đúng các dòng TODO có số issue của mình** trong `HotelData.java` và `MainMenu.java`.
5. Thêm dữ liệu mẫu vào `data/<file>.txt` theo đúng định dạng trong `docs/THIET_KE.md`.
6. Biên dịch và chạy thử (mục "Kiểm tra").
7. Cuối cùng, giải thích ngắn cho người dùng: mỗi lớp làm gì, và **kế thừa / đa hình / abstract / interface / static / đọc-ghi file**
   nằm ở lớp nào, dòng nào trong phần họ làm, để họ chuẩn bị vấn đáp.

## Quy tắc bắt buộc

### Không được sửa
- `src/hotel/common/*`, `src/hotel/util/*`, `src/hotel/model/Person.java`, `src/hotel/menu/Menu.java`, `src/hotel/menu/CrudMenu.java`.
  Đây là khung chung cả nhóm dùng. Nếu thấy thiếu hàm hoặc có lỗi, **dừng lại và báo người dùng** để họ báo trưởng nhóm, đừng tự sửa.
- File của module người khác.
- `docs/THIET_KE.md`: không tự đổi tên lớp, chữ ký hàm, định dạng file.

### Được sửa có giới hạn
- `HotelData.java`, `MainMenu.java`: chỉ các dòng TODO của issue đang làm.

### Code
- Tương thích **Java 8**: không dùng `var`, `record`, switch dạng `->`, text block `"""`, `List.of(...)`.
- Mọi lớp có **constructor mặc định + constructor đủ tham số**; lớp model có thêm **copy constructor** (dùng trong `inputEdit`).
- Mỗi lớp model có biến `private static int createdCount` và `public static int getCreatedCount()`.
- Nhập liệu **chỉ dùng `InputHelper`**, không tạo `new Scanner(System.in)`.
- Đọc/ghi file **chỉ qua `BaseList.loadFromFile()/saveToFile()`**; tách và ghép dòng bằng `FileHelper.split()` / `FileHelper.join()`.
- Lớp có nhiều loại con (Room, Customer, Employee): trường đầu tiên trong file là **loại** (`STANDARD`, `VIP`, `MANAGER`...),
  `parseLine()` dựa vào đó để tạo đúng lớp con.
- Sinh mã bằng `list.nextId(PREFIX)` với tiền tố trong `docs/THIET_KE.md` (KH, NV, DV, DP, HD).
- Thuộc tính `private`/`protected` + getter/setter. Tên lớp `PascalCase`, hàm/biến `camelCase`, hằng `UPPER_CASE`.
- Comment và chuỗi hiển thị bằng **tiếng Việt có dấu**. File lưu **UTF-8**.
- Không thêm thư viện ngoài, không dùng Maven/Gradle.

## Cấu trúc

```
src/hotel/Main.java          điểm bắt đầu
src/hotel/HotelData.java     các danh sách static dùng chung giữa module
src/hotel/common/            interface + BaseList (khung, không sửa)
src/hotel/model/             lớp đối tượng
src/hotel/list/              lớp danh sách XxxList extends BaseList<Xxx>
src/hotel/menu/              lớp menu XxxMenu extends CrudMenu<Xxx>
src/hotel/util/              tiện ích static (khung, không sửa)
data/*.txt                   dữ liệu, phân cách bằng |
docs/                        thiết kế, hướng dẫn, phân công
```

## Kiểm tra

Chạy từ thư mục gốc repo:

```bash
# Biên dịch (phải không có lỗi)
mkdir -p out && javac -encoding UTF-8 --release 8 -d out $(find src -name '*.java')

# Chạy
java -cp out hotel.Main
# hoặc: ./scripts/run.sh  (Windows: scripts\run.bat)
```

Chạy thử đủ: **Xem, Thêm, Sửa (Enter giữ giá trị cũ), Xóa, Tìm kiếm**, các chức năng riêng trong issue,
nhập sai kiểu (chữ vào ô số, ngày sai) không làm chương trình dừng; **thoát rồi chạy lại thì dữ liệu vẫn còn**.
Không commit thư mục `out/`.

## Commit và Pull Request

- Commit trên nhánh `feature/<số>-<tên>`, message tiếng Việt, kèm số issue, ví dụ `Room: thêm RoomList và RoomMenu (#2)`.
- **Không push vào `main`.** Mở Pull Request vào `main`, mô tả theo mẫu `.github/pull_request_template.md`, ghi `Closes #<số>`.
- Chỉ commit/push khi người dùng đồng ý.
