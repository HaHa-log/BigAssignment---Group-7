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

* Spring Boot
* Spring Web
* Spring Data JPA
* Spring WebSocket
* HikariCP

### Client Side

* JavaFX
* JavaFX Controls
* JavaFX FXML

### Kiểm thử chất lượng

* JUnit 5
* Mockito Framework
* Kiểm thử:

  * Equivalence Partitioning (EP)
  * Boundary Value Analysis (BVA)

### Yêu cầu môi trường

* JDK/JRE 21 trở lên
* MySQL Server 8.0 hoặc 9.x

---

## 3. Cấu trúc thư mục (Project Structure)

```text
BigAssignment/
│
├── artifacts/
│   ├── server.jar
│   └── client.jar
│
├── common/                         # Shared DTOs
│   ├── src/main/java/
│   └── pom.xml
│
├── server/                         # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   ├── controllers/
│   │   │   │   ├── services/
│   │   │   │   ├── repositories/
│   │   │   │   ├── models/
│   │   │   │   ├── config/
│   │   │   │   └── server/
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/
│   └── pom.xml
│
├── client/                         # JavaFX desktop application
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   ├── app/
│   │       │   ├── controllers/
│   │       │   ├── services/
│   │       │   ├── models/
│   │       │   ├── config/
│   │       │   ├── utils/
│   │       │   └── exceptions/
│   │       ├── resources/
│   │       └── module-info.java
│   └── pom.xml
│
├── pom.xml                         # Parent Maven configuration
└── README.md
---
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
* Kiểm soát số dư phục vụ đấu giá

### 📦 Quản lý sản phẩm

* Tạo sản phẩm mới
* Cập nhật thông tin sản phẩm
* Quản lý danh sách sản phẩm cá nhân

### 🏷️ Quản lý phiên đấu giá

* Tạo phiên đấu giá từ sản phẩm đã có
* Theo dõi trạng thái phiên
* Hủy phiên theo nhiều kịch bản nghiệp vụ

### ❌ Cơ chế hủy phiên

* Admin có thể hủy bất kỳ phiên nào
* Seller có thể hủy phiên khi trạng thái là `OPEN`
* Tự động hủy nếu người thắng không xác nhận giao dịch trong thời hạn quy định

### ⚡ Đấu giá thời gian thực (Real-Time Bidding)

* Kết nối song công thông qua WebSocket
* Đồng bộ bảng giá tức thời
* Không cần tải lại giao diện

### 🤖 Auto-Bidding

* Thiết lập giá trần tối đa
* Thiết lập bước tăng tự động
* Hệ thống tự động cạnh tranh giá theo cài đặt

### 👮 Quản trị hệ thống

* Block / Unblock người dùng
* Hủy các phiên đấu giá 

### ⏰ Quản lý trạng thái tự động

* Chuyển trạng thái:

  * `OPEN → RUNNING → FINISHED`
* Scheduler chạy nền kiểm tra định kỳ
* Đóng băng số dư của người thắng phiên để đảm bảo giao dịch

### 📊 Trực quan hóa dữ liệu

* Biểu đồ LineChart thời gian thực
* Hiển thị lịch sử biến động giá đấu
* Theo dõi diễn biến phiên trực quan
  
* ### 🤖 Handle - sniping

* Tự động gia hạn phiên đấu giá nếu có người đặt bid ở 5 phút cuối
* Thời gian gia hạn mỗi lần 5 phút, tối đa 5 lần

---

## 7. Tài liệu báo cáo & Video Demo (Reports & Demo Videos)

### Báo cáo dự án


### Video Demo


---

