# Phân công công việc

Mỗi đầu việc tương ứng 1 **issue** trên GitHub. Ai nhận issue nào thì *assign* cho mình và làm trên nhánh riêng (xem [`CONTRIBUTING.md`](../CONTRIBUTING.md)).

| Thành viên | Issue | Nội dung | Số lớp |
|---|---|---|---|
| **TV1** (trưởng nhóm) | #1 | Khung chung: interface, `BaseList`, `Person`, `Menu`/`CrudMenu`, tiện ích static, `HotelData`, `Main`, `MainMenu` | ~16 |
| | #8 | Thống kê, tích hợp các module, dữ liệu mẫu | 1 |
| **TV2** | #2 | Module Phòng | 7 |
| **TV3** | #3 | Module Khách hàng | 4 |
| | #4 | Module Dịch vụ | 3 |
| **TV4** | #5 | Module Nhân viên | 6 |
| **TV5** | #6 | Module Đặt phòng (check-in/check-out) | 5 |
| | #7 | Module Hóa đơn | 3 |
| **Cả nhóm** | #9 | UML, báo cáo, kịch bản demo, chuẩn bị vấn đáp | — |
| **Cả nhóm** | #10 | Checklist nghiệm thu theo yêu cầu đồ án | — |

## Thứ tự & mốc thời gian (gợi ý)

| Tuần | Việc |
|---|---|
| 1 | TV1 xong **#1** (khung chung) càng sớm càng tốt — các module khác phụ thuộc vào đây. Trong lúc chờ, các TV khác viết lớp model của mình. |
| 2 | TV2, TV3, TV4 hoàn thành module (#2, #3, #4, #5). TV5 viết `Booking`, `Invoice` model. |
| 3 | TV5 hoàn thành #6, #7 (cần Phòng + Khách hàng + Dịch vụ). TV1 làm #8. |
| 4 | Tích hợp, sửa lỗi, nhập dữ liệu mẫu, làm báo cáo + demo (#9, #10). |

## Sơ đồ phụ thuộc

```
#1 Khung chung ──┬── #2 Phòng ─────────┐
                 ├── #3 Khách hàng ────┤
                 ├── #4 Dịch vụ ───────┼── #6 Đặt phòng ── #7 Hóa đơn ── #8 Thống kê
                 └── #5 Nhân viên      │
                                       └──────────────────────────────── #9, #10
```

## Mỗi thành viên phải tự trả lời được (khi vấn đáp)

- Lớp mình viết kế thừa từ đâu, override phương thức nào, đa hình thể hiện ở dòng code nào.
- Dữ liệu module mình được đọc/ghi file như thế nào.
- Chỗ nào dùng `static`, `abstract`, `interface` trong module của mình.
