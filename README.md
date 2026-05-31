# BigAssignment — Group 7

## 1. Giới thiệu dự án (Project Overview)

### Thông tin chung

* **Tên dự án:** Hệ thống đấu giá trực tuyến thời gian thực (Real-time Online Auction System)
* **Môn học:** Lập trình nâng cao (LTNC)
* **Phạm vi hệ thống:** Hệ thống phân tán theo mô hình Client–Server xử lý luồng đấu giá đồng thời (Concurrent Bidding), tự động hóa quy trình quản lý ví tiền, đóng băng số dư bảo đảm và cập nhật trạng thái bảng điện theo thời gian thực thông qua giao tiếp song công.

### Thành viên nhóm & Phân công nhiệm vụ (Team Members & Work Division)

| Thành viên        | MSSV     | Vai trò  |
| ----------------- | -------- | -------- |
| Vương Thúy Hằng   | 25020144 | Backend  |
| Hoàng Ánh Ngọc    | 25020294 | Backend  |
| Bùi Hà Linh       | 25020233 | Frontend |
| Trần Thị Mai Uyên | 25020424 | Frontend |

---

## 2. Công nghệ sử dụng & Yêu cầu hệ thống (Tech Stack & Environment)
 
### Công nghệ sử dụng
 
* **Ngôn ngữ:** Java 21 (LTS), CSS (JavaFX Style)
* **Build Tool:** Apache Maven 
* **Database:** MySQL Server 8.0+ / 9.x

### Server Side
 
* Spring Boot 3.3.4
* Spring Web 
* Spring WebSocket 
* Spring Data JPA 
* HikariCP 7.0.2

### Client Side
 
* JavaFX 21
* JavaFX Controls / FXML

### Kiểm thử chất lượng
 
* JUnit 5
* Mockito Framework
* Kiểm thử:
  * Equivalence Partitioning (EP)
  * Boundary Value Analysis (BVA)
* Phạm vi test: `common/src/test/` (DTO tests) + `server/src/test/` (~30 lớp test: model, service, controller, concurrency)

### Yêu cầu môi trường
 
* JDK/JRE 21 trở lên
* MySQL Server 8.0 hoặc 9.x
* Kết nối Internet 

---

## 3. Cấu trúc thư mục (Project Structure)
 
```text
BigAssignment/
│
├── artifacts/
│   ├── server.jar
│   └── client.jar
│
├── common/                         # Shared DTOs (dùng chung client & server)
│   ├── src/main/java/
│   │   └── com/group7/dto/         # AuctionResponse, BidRequest, AutoBidRequest, ...
│   └── pom.xml
│
├── server/                         # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── controllers/    # REST Controllers (@RestController)
│   │   │   │   ├── services/       # AuctionService, AutoBidService, TransactionService, ...
│   │   │   │   ├── repositories/   # DAO interfaces + impl/ (JDBC thuần)
│   │   │   │   ├── models/         # Auction, User, Bidder, Seller, Admin, Item, AutoBid, ...
│   │   │   │   ├── config/         # BidWebSocketHandler, DB config
│   │   │   │   └── server/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/              
│   └── pom.xml
│
├── client/                         # JavaFX desktop application
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── app/            # Entry point (ClientApp, Launcher)
│   │       │   ├── controllers/    # AuctionDetailController, AuctionListController, ...
│   │       │   ├── services/       # AuctionApiService, UserApiService, ... (HTTP/WS)
│   │       │   ├── models/         # Domain objects + Common/ (value objects)
│   │       │   ├── config/         # ApiConfig (base URL)
│   │       │   ├── utils/          # ImageFileValidator, ApiJson
│   │       │   └── exceptions/
│   │       ├── resources/          # FXML + CSS
│   │       └── module-info.java
│   └── pom.xml
│
├── pom.xml                         # Parent Maven configuration
└── README.md

```

## 4. Vị trí các file thực thi (Executable Artifacts)

| Thành phần | Đường dẫn              |
| ---------- | ---------------------- |
| Server     | `artifacts/server.jar` |
| Client     | `artifacts/client.jar` |


## 5. Hướng dẫn chạy hệ thống (Execution Guide)

> [!IMPORTANT]
> Cần mở các cửa sổ Terminal độc lập và thực hiện tuần tự các bước sau.

### Bước 1: Khởi động Server

Mở Terminal tại thư mục gốc của dự án:

```bash
java -jar artifacts/server.jar
```

### Bước 2: Khởi động Client

Mở một Terminal khác tại thư mục gốc của dự án:

```bash
java -jar artifacts/client.jar
```

> Có thể lặp lại bước này trên nhiều Terminal khác nhau để mô phỏng nhiều người dùng truy cập đồng thời.

---

## 6. Danh sách chức năng đã hoàn thành (Completed Features)
 
### 🔐 Xác thực & Quản lý tài khoản
 
* Đăng ký tài khoản
* Đăng nhập hệ thống
* Một người dùng có thể đồng thời đóng vai trò:
  * Seller
  * Bidder
### 💰 Quản lý ví điện tử
 
* Nạp tiền vào tài khoản
* Rút tiền khỏi tài khoản
* Đóng băng số dư khi người dùng đang dẫn đầu một phiên 
* Tự động hoàn tiền khi phiên bị hủy hoặc giao dịch hết hạn
### 📦 Quản lý sản phẩm
 
* Tạo sản phẩm mới 
* Cập nhật thông tin sản phẩm
* Quản lý danh sách sản phẩm cá nhân (Inventory)
### 🏷️ Quản lý phiên đấu giá
 
* Tạo phiên đấu giá từ sản phẩm đã có
* Theo dõi trạng thái phiên
* Hủy phiên theo nhiều kịch bản nghiệp vụ
### ❌ Cơ chế hủy phiên
 
* Admin có thể hủy bất kỳ phiên nào
* Seller có thể hủy phiên khi trạng thái là `OPEN`
* Tự động hủy nếu người thắng không xác nhận giao dịch trong thời hạn quy định (kiểm tra mỗi 5 giây qua `@Scheduled`)
### ⚡ Đấu giá thời gian thực (Real-Time Bidding)
 
* Kết nối song công thông qua WebSocket 
* Server broadcast tới tất cả client đang theo dõi phiên khi có bid mới
* Client cập nhật UI, không cần tải lại giao diện
### 🤖 Auto-Bidding
 
* Thiết lập giá trần tối đa (`maxBid`)
* Thiết lập bước tăng tự động (`increment`)
* Hệ thống tự động cạnh tranh giá theo cài đặt
* Bước tăng được tự động điều chỉnh theo `BidStepConfiguration` nếu không hợp lệ
* Tie-breaking: người đăng ký Auto-bid trước được ưu tiên giữ quyền dẫn đầu
### 👮 Quản trị hệ thống
 
* Block / Unblock người dùng
* Hủy các phiên đấu giá
### ⏰ Quản lý trạng thái tự động
 
* Vòng đời phiên: `OPEN → RUNNING → FINISHED → PAID / CANCELED`
* Scheduler `@Scheduled(fixedDelay = 5_000)` chạy nền kiểm tra mỗi **5 giây**
* Chuyển trạng thái thời gian thực cũng được kích hoạt khi `getById()` gọi `refreshTimedStatus()`
* Đóng băng số dư người thắng ngay khi phiên `FINISHED` (tạo `Transaction` trạng thái PENDING)
### 🛡️ Chống sniping (Anti-Sniping)
 
* Tự động gia hạn phiên nếu có bid trong **5 phút cuối** (`SNIPE_WINDOW_MINUTES = 5`)
* Mỗi lần gia hạn thêm **5 phút**, tối đa **5 lần** (`MAX_EXTENDS = 5`)
* Sau khi đạt giới hạn gia hạn: kích hoạt chế độ `isInCountDown = true` — phiên kết thúc theo giờ cố định
### 📊 Trực quan hóa dữ liệu
 
* Biểu đồ `LineChart` thời gian thực (JavaFX)
* Hiển thị lịch sử biến động giá đấu (trục X: timestamp `dd/MM HH:mm`, trục Y: giá USD)
* Cập nhật tự động qua WebSocket — không cần tải lại trang
### 🔒 Xử lý đồng thời (Concurrency)
 
* `ReentrantLock` trong lớp `Auction` bảo vệ toàn bộ các thao tác critical (đặt giá, chuyển trạng thái, đọc giá hiện tại)
* `ConcurrentHashMap` trong `BidWebSocketHandler` quản lý các WebSocket session đồng thời an toàn

---

## 7. Tài liệu báo cáo & Video Demo (Reports & Demo Videos)

### Báo cáo dự án

- 📄 [Project Report](https://drive.google.com/file/d/1GL3mgR60JDYJwQX-w1JMLA2OH4tJzZ5w/view?usp=sharing)

### Video Demo

- 🎥 [Watch Demo Video](https://drive.google.com/file/d/1qzfOFkzVyYAj0xuPr-qjIUmKIgfdZS2d/view)---

