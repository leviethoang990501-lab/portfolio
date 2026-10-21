# Server Việt Hoàng - bản tổng hợp tính năng test

## Cách chạy

1. Chạy `run-server.bat`.
2. Khi cần kiểm tra dữ liệu trước khi mở server, chạy `audit-server.bat`.
3. Chạy `test-regression.bat` sau mỗi lần sửa mã nguồn.

Chế độ tổng hợp được điều khiển trong `hso.conf`:

```text
viet-hoang-mode: true
viet-hoang-level: 150
```

Đổi `viet-hoang-mode` thành `false` nếu muốn trở lại quy trình tạo nhân vật bình thường.

## Hồ sơ nhân vật test

- Nhân vật đăng nhập trong chế độ Việt Hoàng tự nhận cấp tối thiểu 150, vàng/ngọc, 126 ô hành trang, đủ 21 skill theo class, đủ 12 pet và bộ vật phẩm kiểm thử. Các trang bị phù hợp được tự đặt vào ô trống, gồm đủ các vị trí 12-19 của tab `Trang bị 2`. Profile có phiên bản nên nhân vật cũ tự nhận phần bổ sung đúng một lần.
- Tài khoản cũ gõ `/viethoang` để nhận hồ sơ đúng một lần. Server ghi dấu nên không cấp lặp lại sau mỗi lần đăng nhập.
- Gõ `/map ID` để đi nhanh tới một map đã được server nạp, ví dụ `/map 61`.
- Gõ `/help` để xem lại lệnh test.

## Danh sách kiểm thử bắt buộc

### Skill và buff

- Tạo đủ 4 class: Chiến binh, Sát thủ, Pháp sư, Xạ thủ.
- Mỗi class phải hiển thị 21 skill; thử skill đánh đơn, đánh nhiều mục tiêu và skill buff.
- Buff phải trừ MP, có thời gian hồi và không cho dùng skill chưa học/sai class.
- Thử cộng điểm skill trên tài khoản thường; không được vượt cấp tối đa trong dữ liệu skill.

### Trang bị, pet, thú cưỡi và thời trang

- Thử mặc/tháo từng nhóm trang bị, mề đay, pet/trứng, thú cưỡi, cánh, mặt nạ và thời trang.
- Trang bị sai class hoặc chưa đủ cấp phải bị từ chối; item hợp lệ phải vào đúng ô.
- So sánh tài khoản mới với tài khoản test cũ sau khi dùng `/viethoang`.

### Rơi đồ và nâng cấp

- Đánh quái ở map cấp thấp, trung bình và cao; không được lỗi khi nhóm rơi đồ của một mốc cấp trống.
- Thử đập đồ ở các mốc +0, +6, +11 và +14; chỉ trừ nguyên liệu/ngọc sau khi mọi điều kiện hợp lệ.

### Mề đay và hành trang

- Tạo đủ 4 mề đay, nâng từ +0 đến tối đa, thử cả thành công và thất bại.
- Khi túi đầy, tạo mề đay phải dừng trước khi trừ nguyên liệu.
- Mở rộng hành trang phải đạt 126 ô.
- Mở `Trang bị 2` và kiểm tra các ô 12-19 đã có item đúng loại; client 1.8.2 đi kèm vốn đã có giao diện hai tab nên không cần sửa bytecode client.
- Thử chuyển đổi giữa trang bị thường, thời trang và mề đay; mỗi loại phải vào đúng ô trang bị.

### Bản đồ và hoạt động

- `audit-server.bat` kiểm tra toàn bộ map trong DB có file tài nguyên và mọi cổng dịch chuyển trỏ tới map tồn tại.
- Chiến trường: kiểm tra map chuẩn bị 53/55/57/59 và map chiến trường 61, vào trận, tính điểm và phát thưởng khi ít người.
- Đi buôn/cướp: thử nhận pet chở hàng, hồi máu, tăng tốc, bị cướp và giao hàng; không được lỗi khi chưa có pet.
- Vòng xoay: mỗi tài khoản đặt 10–200 triệu vàng; thử đủ hai người, người thoát mạng và kết thúc vòng.

## Phạm vi hoàn thiện

Bản này tổng hợp và làm ổn định các tính năng đã có trong source và dữ liệu đi kèm. Không thể xác nhận giống 100% một bản thương mại đã phát hành nếu không có đúng client, server gốc và bộ tiêu chí đối chiếu. Một tab giao diện không tồn tại trong client (ví dụ tab mề đay) cũng không thể được server tự vẽ thêm; phía server đã hỗ trợ luồng item, tạo và nâng cấp mề đay.
