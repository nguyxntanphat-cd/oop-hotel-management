# Quy trình làm việc nhóm

## 1. Nhánh (branch)

- `main`: luôn biên dịch được và chạy được. **Không push thẳng vào `main`.**
- Mỗi issue làm trên 1 nhánh riêng, đặt tên: `feature/<so-issue>-<ten-ngan>`
  ví dụ `feature/2-room`, `feature/5-employee`.

```bash
git checkout main
git pull
git checkout -b feature/2-room
# ... code ...
git add .
git commit -m "Room: thêm lớp Room, StandardRoom (#2)"
git push -u origin feature/2-room
```

Sau đó lên GitHub tạo **Pull Request** vào `main`, ghi `Closes #2` trong mô tả. Ít nhất 1 thành viên khác review rồi mới merge.

## 2. Quy ước code

- Package gốc `hotel`. Đặt lớp đúng package theo [`docs/THIET_KE.md`](docs/THIET_KE.md).
- Tên lớp `PascalCase`, phương thức/biến `camelCase`, hằng số `UPPER_CASE`.
- Thuộc tính để `private`/`protected`, truy cập qua getter/setter.
- **Mọi lớp phải có constructor** (mặc định + đầy đủ tham số).
- Nhập liệu chỉ dùng `InputHelper` (không tự tạo `Scanner` mới).
- Đọc/ghi file chỉ dùng `FileHelper` + `BaseList.loadFromFile()/saveToFile()`.
- File nguồn lưu **UTF-8**. Comment tiếng Việt thoải mái.

## 3. Tránh xung đột (conflict)

- Chỉ sửa file trong module của mình.
- File dùng chung `HotelData.java`, `MainMenu.java`: mỗi người chỉ sửa đúng dòng TODO của module mình, pull `main` mới nhất trước khi sửa.
- Muốn đổi interface / lớp trong `hotel.common`, `hotel.util`: tạo issue hoặc nhắn nhóm trước.

## 4. Trước khi tạo Pull Request

- [ ] `scripts/run.sh` (hoặc `scripts\run.bat`) chạy được, không lỗi biên dịch.
- [ ] Đã test đủ Xem / Thêm / Sửa / Xóa / Tìm kiếm của module.
- [ ] Thoát rồi mở lại chương trình, dữ liệu vẫn còn.
- [ ] CI (tab *Actions*) báo xanh.
