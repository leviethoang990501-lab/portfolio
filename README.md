# HSO Server

Source server Hiệp Sĩ Online viết bằng Java, sử dụng Maven và MySQL.

> Chỉ sử dụng source cho mục đích học tập, nghiên cứu và trong môi trường mà bạn có quyền quản trị.

## Yêu cầu

- Windows 10/11 hoặc Linux
- Java 17 trở lên
- MySQL Server 8.x
- Maven 3.8 trở lên nếu muốn build lại source

Kiểm tra Java và Maven:

```powershell
java -version
mvn -version
```

Nếu chỉ chạy file JAR có sẵn trong thư mục `target`, Maven không bắt buộc.

## 1. Chuẩn bị database

Tạo database `hso2` trong MySQL:

```sql
CREATE DATABASE hso2
CHARACTER SET utf8mb3
COLLATE utf8mb3_general_ci;
```

Sau đó import file `hso.sql` vào database `hso2`.

### Import bằng MySQL Workbench

1. Mở MySQL Workbench và kết nối tới MySQL Server.
2. Chọn **Server > Data Import**.
3. Chọn **Import from Self-Contained File**.
4. Chọn file `hso.sql` trong thư mục source.
5. Chọn schema mặc định là `hso2`.
6. Chọn **Start Import**.

### Import bằng dòng lệnh trên Windows

Chạy trong Command Prompt tại thư mục source:

```bat
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS hso2 CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci;"
mysql -u root -p hso2 < hso.sql
```

File SQL đi kèm có chứa dữ liệu mẫu/tồn tại sẵn, không phải database trắng. Nên dùng database riêng để thử nghiệm và không đưa dữ liệu này lên môi trường công khai.

## 2. Cấu hình server

Mở file `hso.conf` và thay thông tin mẫu bằng thông tin MySQL thực tế:

```text
port: 19129
mysql-host: 127.0.0.1
mysql-user: root
mysql-password: MAT_KHAU_MYSQL
mysql-database: hso2
debug: true
indexRes: 9
indexCharPar: 0
exp: 10
lvmax: 200
allow_ip_client: 50
time_login: 10000
event: 0
```

Nếu tài khoản MySQL không có mật khẩu, để trống sau dấu hai chấm:

```text
mysql-password:
```

Không commit hoặc chia sẻ file `hso.conf` sau khi đã điền mật khẩu thật. Phiên bản source hiện tại cũng in các giá trị cấu hình ra console khi khởi động, vì vậy không chia sẻ log có chứa mật khẩu.

## 3. Chạy server bằng JAR có sẵn

Mở PowerShell và chuyển vào đúng thư mục source:

```powershell
Set-Location -LiteralPath 'F:\Hoàng\hso-server-main'
java -server -jar '.\target\HSO_Re_2-1.0-jar-with-dependencies.jar'
```

Phải chạy lệnh từ thư mục gốc của dự án vì server đọc `hso.conf` và thư mục `data` bằng đường dẫn tương đối.

Khi khởi động thành công, console sẽ hiển thị các dòng tương tự:

```text
OPEN DataBase connect
cache loaded!
Server Đang Chạy Ở Cổng 19129
```

Kiểm tra server đang lắng nghe port:

```powershell
Test-NetConnection 127.0.0.1 -Port 19129
```

Dừng server bằng `Ctrl+C` để shutdown đúng cách và đóng kết nối database.

## 4. Build lại source

Tại thư mục gốc của dự án:

```powershell
mvn clean package
```

Sau khi build thành công, file chạy đầy đủ dependency nằm tại:

```text
target\HSO_Re_2-1.0-jar-with-dependencies.jar
```

Chạy bản vừa build:

```powershell
java -server -jar '.\target\HSO_Re_2-1.0-jar-with-dependencies.jar'
```

Nếu máy chưa cài Maven nhưng đã có JDK 17 trở lên, dừng server rồi chạy:

```text
build-local.bat
```

Script này biên dịch toàn bộ source vào thư mục tạm có đường dẫn ASCII, cập nhật JAR đầy đủ dependency đang có và tạo bản sao lưu `.before-local-build.bak`. Cách này cũng tránh lỗi `javac` khi dự án nằm trong thư mục có ký tự tiếng Việt.

## 5. Chạy client

Client nằm tại:

```text
client\hso-client.jar
```

Để mở đồng thời server và client, chạy `START-HSO.bat`. Nếu client lỗi ngay lúc mở, xem `client-startup.log` và `client-console.log` trong thư mục `HSO-Viet-Hoang-Client`; launcher sẽ trả mã lỗi thay vì đóng im lặng.

Đây là ứng dụng Java ME/MIDP, không phải ứng dụng Java desktop thông thường. Cần chạy bằng thiết bị hỗ trợ Java ME, J2ME Loader trên Android hoặc trình giả lập Java ME phù hợp.

Client hiện có cấu hình server `127.0.0.1`, địa chỉ LAN `192.168.1.25` và port `19129`.

- Nếu client/emulator chạy cùng máy với server, dùng `127.0.0.1`.
- Nếu client chạy trên điện thoại hoặc máy khác cùng mạng LAN, đổi IP client thành IPv4 LAN của máy chạy server.
- Nếu cho phép kết nối từ máy khác, mở TCP port `19129` trong Windows Firewall.
- Không mở port trực tiếp ra Internet khi chưa rà soát xác thực, phân quyền và bảo mật source.

Các class cấu hình client đang ở dạng đã biên dịch/obfuscate:

- IP: `dw.class`
- Port: `ft.class`

Muốn thay đổi trực tiếp các class này cần công cụ chỉnh sửa/decompile Java ME phù hợp. Port mặc định là `19129` (`0x4AB9`).

## 6. Lỗi thường gặp

### `UnknownHostException: <db_host>`

Chưa thay giá trị mẫu trong `hso.conf`. Đổi `mysql-host` thành `127.0.0.1` nếu MySQL chạy trên cùng máy.

### `Communications link failure`

Kiểm tra MySQL Server đã chạy, port MySQL mặc định `3306` đang mở và thông tin host/user/password chính xác.

### `Access denied for user`

Sai tài khoản hoặc mật khẩu MySQL, hoặc tài khoản chưa có quyền trên database `hso2`.

### Không tìm thấy `hso.conf` hoặc file trong `data`

Bạn đang chạy JAR từ sai thư mục. Chuyển vào thư mục gốc chứa `hso.conf`, `data`, `target` rồi chạy lại.

### `Address already in use: bind`

Port `19129` đang bị chương trình khác sử dụng. Dừng tiến trình đó hoặc đổi `port` trong `hso.conf` và đồng thời cập nhật port phía client.

## Cấu trúc chính

```text
hso-server-main/
|-- client/       Client Java ME đã biên dịch
|-- data/         Tài nguyên server
|-- src/          Source Java
|-- target/       JAR đã build
|-- hso.conf      Cấu hình server và MySQL
|-- hso.sql       Database dump
`-- pom.xml       Cấu hình Maven
```

## Disclaimer

Source code được cung cấp cho mục đích học tập, nghiên cứu và tham khảo. Người sử dụng tự chịu trách nhiệm về hoạt động phát sinh từ việc sử dụng source và phải tuân thủ pháp luật cũng như quyền sở hữu trí tuệ có liên quan.
