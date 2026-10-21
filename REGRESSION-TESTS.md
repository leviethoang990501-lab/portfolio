# Điểm kiểm thử bản tổng hợp HSO

## Tài khoản mới

- Tạo đủ bốn class, đăng nhập lại được và có đúng trang bị khởi đầu.
- Dữ liệu skill cũ hoặc vượt cấp được chuẩn hóa theo số level thật của từng skill, không làm lỗi đăng nhập.
- Các trường `pointarena`, `dropnlmd`, `detu`, `point_active` có giá trị an toàn, không còn phụ thuộc dữ liệu của tài khoản test.
- Lưu toàn bộ người chơi không làm lệch các cột `hieuchien`, `chuyensinh`, `dropnlmd`, `typeexp` và các điểm thuộc tính.
- Dung lượng `maxbag` phải được lưu ở cả lưu định kỳ, lưu khi thoát và flush toàn bộ; đăng nhập lại vẫn giữ 126 ô.

## Trang bị, mề đay và thú cưỡi

- Mề đay (item type 16) luôn vào wear slot 12.
- Trang bị sai class, thiếu level, chỉ số túi sai hoặc item không còn trong túi đều bị server từ chối.
- Slot trang bị cũ bị lệch được chuẩn hóa khi đăng nhập.
- Cánh thời trang dùng `part + 41` cho các ID không có ánh xạ đặc biệt.
- Thú cưỡi cưỡi trực tiếp vẫn đi theo luồng item category 4; trang phục/thú cưỡi dạng wear type 15 đi slot 11.

## Pet

- Có luồng mua trứng, ấp, nở, đeo/tháo và cho ăn.
- Không thể gửi chỉ số pet/trứng/thức ăn ngoài phạm vi.
- Khi dữ liệu cũ có nhiều pet cùng `is_follow`, server chỉ giữ một pet đang theo.

## Rơi và nhặt đồ

- Không sinh item ID 0 khi bảng drop của đúng dải level rỗng; dùng pool gần nhất hoặc bỏ lượt drop.
- Không đọc ngoài mảng item map khi client gửi ID nhặt sai.
- Lượng vàng rơi được chặn trong giới hạn protocol `short`.

## Cường hóa

- Chưa chọn trang bị, item đã biến mất, tier/loại tiền sai đều bị từ chối.
- Chỉ trừ đúng loại tiền đã chọn và chỉ trừ sau khi đủ toàn bộ nguyên liệu.
- Tier 0–5 cần 2 loại đá; 6–10 cần 3; 11–14 cần 4.
- Chống gửi nhanh được kiểm tra trước khi trừ tiền/nguyên liệu.

Chạy `test-regression.bat` để kiểm tra tự động các quy tắc lõi. Các kiểm thử gameplay tương tác vẫn cần chạy client vì chúng phụ thuộc gói tin, map và dữ liệu MySQL đang dùng.
