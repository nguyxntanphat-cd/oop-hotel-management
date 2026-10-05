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
2. `docs/THIET_KE.md`: sơ đồ lớp, **hợp đồng tích hợp** (mục 3: hàm public mỗi module bắt buộc có),
   luồng nghiệp vụ giữa các module (mục 4), **định dạng file dữ liệu** (mục 5), mã và dữ liệu mẫu dùng chung (mục 6).
   Đây là "hợp đồng" chung của nhóm. **Nếu issue ghi khác `docs/THIET_KE.md` thì làm theo `docs/THIET_KE.md`.**
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
6. **Tự đối chiếu hợp đồng:** mở `docs/THIET_KE.md` mục 3, phần module mình, kiểm tra **từng dòng** chữ ký hàm
   đã có trong code với đúng tên, tham số, kiểu trả về, `public`/`static`. Kiểm tra tên file trong `getFileName()`
   và định dạng `toFileString()` khớp mục 5. Thiếu hoặc sai thì sửa trước khi làm bước sau.
7. Biên dịch và chạy thử (mục "Kiểm tra").
8. Cuối cùng, giải thích ngắn cho người dùng: mỗi lớp làm gì, và **kế thừa / đa hình / abstract / interface / static / đọc-ghi file**
   nằm ở lớp nào, dòng nào trong phần họ làm, để họ chuẩn bị vấn đáp.

## Tra cứu nhanh: cần gì thì đọc ở đâu

| Cần biết | Đọc ở đâu |
|---|---|
| Lớp mình viết tên gì, nằm package nào | `docs/THIET_KE.md` mục 2 + bảng "File mỗi module tạo" bên dưới |
| Thuộc tính, quan hệ giữa các lớp | `docs/THIET_KE.md` mục 1.2 (sơ đồ lớp) |
| **Khai báo hàm public** của lớp mình (tên, tham số, kiểu trả về) | `docs/THIET_KE.md` **mục 3**, phần của module mình — chép **y nguyên** chữ ký |
| **Gọi hàm của module khác** | `docs/THIET_KE.md` **mục 3**, phần của module đó — chỉ gọi hàm có trong đó |
| Hàm abstract phải override | `docs/THIET_KE.md` mục 1.1, 1.2 (ký hiệu `*`) và mục 3 |
| Thứ tự đổi trạng thái phòng / booking | `docs/THIET_KE.md` mục 4 |
| Định dạng 1 dòng trong file `.txt` | `docs/THIET_KE.md` mục 5 |
| Mã dùng trong dữ liệu mẫu | `docs/THIET_KE.md` mục 6 |
| Cách viết lớp model / list / menu | `docs/HUONG_DAN_MODULE.md` |
| Hàm nhập liệu, đọc/ghi file có sẵn | `src/hotel/util/InputHelper.java`, `src/hotel/util/FileHelper.java` |
| Hàm danh sách có sẵn (`findById`, `filter`, `nextId`...) | `src/hotel/common/BaseList.java` |
| Hàm menu có sẵn (`canRemove`, `getExtraOptions`...) | `src/hotel/menu/CrudMenu.java` |
| Danh sách của module khác | `src/hotel/HotelData.java` |

## File mỗi module tạo

Tạo **đúng tên file, đúng thư mục** dưới đây (tên file = tên lớp). Không tạo thêm package khác.

| Issue | `src/hotel/model/` | `src/hotel/list/` | `src/hotel/menu/` | `data/` |
|---|---|---|---|---|
| #2 Phòng | `RoomStatus`, `Room`, `StandardRoom`, `DeluxeRoom`, `SuiteRoom` | `RoomList` | `RoomMenu` | `rooms.txt` |
| #3 Khách hàng | `VipLevel`, `Customer`, `VipCustomer` | `CustomerList` | `CustomerMenu` | `customers.txt` |
| #4 Dịch vụ | `Service` | `ServiceList` | `ServiceMenu` | `services.txt` |
| #5 Nhân viên | `Employee`, `Receptionist`, `Manager`, `Housekeeper` | `EmployeeList` | `EmployeeMenu` | `employees.txt` |
| #6 Đặt phòng | `BookingStatus`, `UsedService`, `Booking` | `BookingList` | `BookingMenu` | `bookings.txt` |
| #7 Hóa đơn | `Invoice` | `InvoiceList` | `InvoiceMenu` | `invoices.txt` |
| #8 Thống kê | | | `StatisticsMenu` | |

## Mẫu khai báo chung (mọi module viết giống nhau)

Viết giống hệt các mẫu dưới đây để code của 5 người ghép vào là biên dịch được.

### Đầu file: package và import

```java
package hotel.model;                 // đúng thư mục: hotel.model / hotel.list / hotel.menu

import hotel.common.FileSerializable; // import TỪNG lớp, không dùng dấu *
import hotel.common.Identifiable;
import hotel.common.Searchable;
import hotel.util.FileHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
```

- Import theo thứ tự: lớp của project (`hotel.*`) trước, rồi `java.*`. Không import thứ không dùng.
- **Chỉ được dùng thư viện chuẩn** của Java 8: `java.util.*` (`List`, `ArrayList`, `Comparator`), `java.time.*` (`LocalDate`, `ChronoUnit`).
  Không dùng `java.io` trực tiếp (đã có `FileHelper`), không dùng `Scanner` (đã có `InputHelper`), không thêm thư viện ngoài.
- Dùng lớp của module khác: `import hotel.model.Room;`, `import hotel.list.RoomList;`, `import hotel.HotelData;`.

### Lớp model

```java
public class Xxx implements Identifiable, FileSerializable, Searchable { // hoặc extends Person / extends Room ...
    public static final String ID_PREFIX = "KH";   // tiền tố mã (mục 6), lớp có mã tự sinh mới cần
    private static int createdCount = 0;            // thuộc tính static

    // thuộc tính private/protected, đúng tên trong sơ đồ mục 1.2

    public Xxx() { ... }                            // 1. constructor mặc định
    public Xxx(/* đủ tham số */) { ...; createdCount++; } // 2. constructor đủ tham số
    public Xxx(Xxx other) { this(/* các field của other */); } // 3. copy constructor

    public static int getCreatedCount() { return createdCount; }

    // hàm public trong THIET_KE.md mục 3, chép đúng chữ ký
    // getId(), toFileString(), matches(String keyword), toString(), getter/setter
    public static String header() { ... }           // tiêu đề bảng khớp với toString()
}
```

- `toFileString()` dùng `FileHelper.join(...)`. Lớp có lớp con thì trường đầu là **hằng chuỗi loại**
  khai báo `public static final String TYPE = "DELUXE";` trong từng lớp con.
- Enum ghi ra file bằng `status.name()`, đọc lại bằng `RoomStatus.valueOf(p[3])`.
  `boolean` đọc bằng `Boolean.parseBoolean(...)`, ngày bằng `FileHelper.parseDate(...)`, số bằng `Double.parseDouble(...)` / `Integer.parseInt(...)`.
- Hiển thị tiền bằng `FileHelper.formatMoney(...)`, ngày bằng `FileHelper.formatDate(...)`.

### Lớp danh sách

```java
public class XxxList extends BaseList<Xxx> {
    public XxxList() { super(); }
    @Override protected String getFileName() { return "xxx.txt"; } // đúng tên file ở bảng trên
    @Override protected Xxx parseLine(String line) { ... }          // dùng FileHelper.split(line), sai định dạng trả về null
    @Override protected String getHeader() { return Xxx.header(); }
    // hàm public trong THIET_KE.md mục 3, viết bằng filter(...) / vòng for
}
```

### Lớp menu

```java
public class XxxMenu extends CrudMenu<Xxx> {
    public XxxMenu(XxxList list) { super("QUẢN LÝ ...", list); } // MainMenu gọi: new XxxMenu(HotelData.XXX).run()
    @Override protected Xxx inputNew() { ... }                     // mã mới: list.nextId(Xxx.ID_PREFIX)
    @Override protected Xxx inputEdit(Xxx old) { ... }             // sửa trên new Xxx(old)
}
```

### Gọi sang module khác

```java
Room room = HotelData.ROOMS.findById(roomNumber);          // luôn qua HotelData, kiểm tra null
if (room == null) { System.out.println("Không tìm thấy phòng " + roomNumber); return; }
room.setStatus(RoomStatus.OCCUPIED);                       // chỉ dùng hàm có trong THIET_KE.md mục 3
HotelData.ROOMS.saveToFile();                              // sửa dữ liệu module khác thì lưu ngay
```

## Quy tắc bắt buộc

### Không được sửa
- `src/hotel/common/*`, `src/hotel/util/*`, `src/hotel/model/Person.java`, `src/hotel/menu/Menu.java`, `src/hotel/menu/CrudMenu.java`.
  Đây là khung chung cả nhóm dùng. Nếu thấy thiếu hàm hoặc có lỗi, **dừng lại và báo người dùng** để họ báo trưởng nhóm, đừng tự sửa.
- File của module người khác.
- `docs/THIET_KE.md`: không tự đổi tên lớp, chữ ký hàm, định dạng file.

### Được sửa có giới hạn
- `HotelData.java`, `MainMenu.java`: chỉ các dòng TODO của issue đang làm
  (riêng issue #6 sửa thêm thân hàm `isRoomInUse` / `isCustomerInUse` theo TODO).

### Ghép với module khác (để cả nhóm chạy chung được)
- Module của mình phải có **đủ và đúng** các hàm public ghi trong `docs/THIET_KE.md` mục 3 (đúng tên, tham số, kiểu trả về),
  vì module khác sẽ gọi chúng. Được thêm hàm khác.
- Chỉ gọi module khác qua đúng các hàm trong mục 3. Nếu module đó **chưa được merge vào `main`**,
  không tự viết thay lớp của người khác: báo người dùng và chỉ làm phần không phụ thuộc.
- Lấy danh sách của module khác qua `HotelData.ROOMS`, `HotelData.CUSTOMERS`, ... Không `new RoomList()` lần nữa.
- Chặn xóa phòng / khách đang có booking bằng `HotelData.isRoomInUse(...)` / `HotelData.isCustomerInUse(...)` trong `canRemove()`.
- Sửa dữ liệu của module khác (vd đổi trạng thái phòng) thì gọi `saveToFile()` của danh sách đó ngay sau khi sửa.
- Dữ liệu mẫu dùng đúng các mã ở `docs/THIET_KE.md` mục 6 để file của các module tham chiếu khớp nhau.

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
- Trước khi tạo Pull Request: `git pull origin main` (hoặc `git merge origin/main`) để lấy code mới nhất của cả nhóm,
  biên dịch lại, rồi mới push.
- Mở Pull Request **vào `main`**, mô tả theo mẫu `.github/pull_request_template.md`, ghi `Closes #<số>`.
- Chỉ commit/push khi người dùng đồng ý. **Trước khi push, nói rõ cho người dùng biết sẽ push lên nhánh nào.**

### Tuyệt đối KHÔNG làm (kể cả khi người dùng nhờ trong lúc vội, hãy nhắc họ làm đúng quy trình)

- **Không push vào `main`**: không `git push origin main`, không `git push origin HEAD:main`, không `git push` khi đang đứng ở nhánh `main`.
- **Không force push**: không `--force`, `-f`, `--force-with-lease`.
- **Không viết lại lịch sử**: không `git rebase` nhánh đã push, không `git commit --amend` commit đã push, không `git reset --hard` để bỏ commit đã push.
- **Không tự merge Pull Request** và không tự bấm đóng PR của mình. Việc merge là của trưởng nhóm (Nguyễn Tấn Phát) sau khi review.
- **Không push lên nhánh của người khác**, chỉ push lên đúng nhánh `feature/<số>-<tên>` của issue đang làm.
- **Không xóa nhánh** (local hoặc remote) mà người dùng không yêu cầu rõ ràng.
- **Không sửa cấu hình repo**: `.github/workflows/*`, `.gitignore`, `scripts/*`, cấu hình branch protection. Cần sửa thì báo người dùng để báo trưởng nhóm.
- **Không commit file build hoặc cá nhân**: thư mục `out/`, `*.class`, file cấu hình IDE (`.idea/`, `.vscode/`).
- **Không bỏ qua kiểm tra**: không dùng `--no-verify`; PR có CI (`compile`) đỏ thì phải sửa lỗi, không được tắt hay sửa CI cho qua.
- **Không tự ý đổi phạm vi**: PR chỉ chứa file của module trong issue (cộng các dòng TODO trong `HotelData.java`, `MainMenu.java`).
  Thấy file khác cần sửa thì dừng lại, báo người dùng.

Nếu bị GitHub từ chối khi push (vì nhánh được bảo vệ, thiếu quyền, hoặc xung đột), **dừng lại và báo người dùng nguyên văn thông báo lỗi**.
Không tìm cách lách (đổi tên nhánh, push bằng remote khác, tắt bảo vệ...).
