BigAssignment --- Group-7
1. Giới thiệu dự án (Project Overview)
Tên dự án: Hệ thống đấu giá trực tuyến thời gian thực (Real-time Online Auction System)

Môn học: Lập trình nâng cao (LTNC)

Phạm vi hệ thống: Hệ thống phân tán theo mô hình Client - Server xử lý luồng đấu giá đồng thời (Concurrent Bidding), tự động hóa quy trình quản lý ví tiền, đóng băng số dư bảo đảm và cập nhật trạng thái bảng điện realtime qua giao tiếp song công.

Thành viên nhóm & Phân công nhiệm vụ (Team Members & Work Division)
Vương Thúy Hằng (25020144) - Vai trò: Backend
Hoàng Ánh Ngọc (25020294) - Vai trò: Backend
Bùi Hà Linh (25020233) - Vai trò: Frontend
Trần Thị Mai Uyên (25020424) - Vai trò: Frontend

2. Công nghệ sử dụng & Yêu cầu hệ thống (Tech Stack & Environment)
Ngôn ngữ: Java 21 (LTS), CSS (Style FX)

Môi trường chạy: JRE/JDK 21 trở lên, MySQL Server (Bản 8.0 hoặc 9.x)

Cấu trúc lõi:

Server Side: Spring Boot (Web, Data JPA, WebSocket), HikariCP.

Client Side: JavaFX Controls & FXML.

Build tool: Apache Maven

Kiểm thử chất lượng: JUnit 5, Mockito Framework (Kiểm thử phân vùng tương đương EP và phân tích giá trị biên BVA).

3. Cấu trúc thư mục (Project Structure)
Dự án được quản lý theo mô hình Maven Multi-Module Blueprint:

BigAssignment/ (Thư mục gốc của Project)
├── artifacts/ <-- THƯ MỤC CHỨA SẢN PHẨM ĐÓNG GÓI THỰC THI (.JAR)
│   ├── server.jar (Fat JAR chứa Spring Boot App + Dependencies)
│   └── client.jar (Fat JAR chứa JavaFX App + Mồi MainLauncher)
├── common/ (Module chứa mã nguồn định nghĩa Models, DTOs, Utils dùng chung)
│   ├── src/main/java/com/group7/dto/
│   └── pom.xml
├── server/ (Module xử lý logic lõi nghiệp vụ và lưu trữ dữ liệu Backend)
│   ├── src/main/java/ (Controllers, Services, Repositories, Configurations)
│   └── pom.xml
├── client/ (Module xây dựng giao diện tương tác người dùng Desktop Client)
│   ├── src/main/java/app/ (ClientApp.java, MainLauncher.java)
│   ├── src/main/resources/ (Giao diện .fxml, tệp định kiểu .css, hình ảnh)
│   └── pom.xml
├── pom.xml (Parent POM quản lý tập trung Dependency Management & Checkstyle)
└── README.md (Tài liệu hướng dẫn vận hành hệ thống)

4. Vị trí các file .jar thực thi (Executable Artifacts Location)
Đường dẫn Server: artifacts/server.jar
Đường dẫn Client: artifacts/client.jar

5. Hướng dẫn chạy
CẦN MỞ CÁC CỬA SỔ TERMINAL ĐỘC LẬP và thực hiện tuần tự theo các bước sau:

Bước 1: Khởi động mạch Server
Mở một Terminal mớitại thư mục gốc của dự án và chạy lệnh:
java -jar artifacts/server.jar

(Đợi Terminal hiển thị logo Spring Boot và thông báo khởi tạo kết nối Port 8080 thành công).

Bước 2: Khởi động Client
Mở một Terminal độc lập khác tại thư mục gốc của dự án và chạy lệnh:
java -jar artifacts/client.jar

(Có thể lặp lại bước 2 trên nhiều terminal khác nhau để chạy nhiều client đồng thời).

6. Danh sách các chức năng đã hoàn thành
Xác thực hệ thống: Đăng ký, đăng nhập tài khoản, người dùng có thể đóng vai trò của cả Seller và Bidder.

Tạo sản phẩm: Seller có thể tạo sản phẩm với các thông tin: tên, mô tả, giá khời điểm.

Tạo phiên đấu giá: Seller có thể tạo phiên đấu giá từ các sản phẩm đã tạo trước đó.

Hỗ trợ nạp/rút tiền: Người dùng có thể nạp/rút tiền vào tài khoản để đấu giá sản phẩm

Hủy phiên: Phiên bị hủy trong các trường hợp: Bị hủy không bất kể trạng thái bởi quản trị viên, bị hủy bời Seller trong trạng thái OPEN, và bị hủy tự động sau khi quá hạn mà người thắng không xác nhận.

Luồng đặt giá Real-time: Cơ chế kết nối song công cập nhật tức thời bảng điện giá hiện tại, thông báo trạng thái người dẫn đầu phiên sang toàn bộ các client đang xem mà không cần tải lại trang.

Tự động hóa Đấu giá (Auto-bidding): Người dùng thiết lập cấu hình giá trần mong muốn và bước nhảy tăng tự động.

Quản lí người dùng: Admin có thể hủy các auction, block/unblock user

Quản lý trạng thái phiên tự động: Lịch trình chạy ngầm quét dọn chuyển đổi trạng thái (OPEN -> RUNNING -> FINISHED). Khóa số dư đóng băng tiền cọc của người thắng phiên bảo vệ quyền lợi giao dịch.

Trực quan hóa dữ liệu lịch sử: Tích hợp đồ thị đường tuyến tính (LineChart) theo thời gian thực mô tả biến động các bước giá đặt trong phiên đấu giá trực quan.

7. Tài liệu báo cáo & Video Demo nghiệp vụ
