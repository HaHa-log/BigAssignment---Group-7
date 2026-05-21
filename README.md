# BigAssignment --- Group-7

### 1. Giới thiệu dự án (Project Overview)
- **Tên dự án:** Phát triển Hệ thống đấu giá trực tuyến (Online Auction System)
- **Môn học:** Lập trình nâng cao 
- **Mô tả ngắn:** Một ứng dụng Client-Server cho phép người dùng đăng ký vai trò Bidder, Seller hoặc Admin để thực hiện các phiên đấu giá thời gian thực.
- **Thành viên nhóm (Team Members):**
  - Vương Thúy Hằng (25020144) - Backend
  - Bùi Hà Linh (25020233) - Frontend
  - Hoàng Ánh Ngọc (25020294) - Backend
  - Trần Thị Mai Uyên (25020424) - Frontend
- **Phân công nhiệm vụ cụ thể:** 

### 2. Cấu trúc thư mục (Project Structure)
<img width="932" height="702" alt="Demo_cau_truc_thu_muc_BG_gr7" src="https://github.com/user-attachments/assets/54de963d-9d5a-4690-ad67-cf00d3868af9" />

---

### 3. Các tính năng chính (Core Features)
- **Quản lý người dùng:** Đăng ký/đăng nhập với các vai trò Bidder, Seller, Admin.
- **Quản lý sản phẩm:** Thêm/sửa/xóa sản phẩm với thông tin giá, thời gian.
- **Đấu giá:** Quy trình đặt giá hợp lệ và cập nhật người dẫn đầu.
- **Kết thúc phiên:** Tự động xác định người thắng và chuyển trạng thái phiên (`OPEN` → `RUNNING` → `FINISHED`).
- **Tính năng nâng cao (nếu có):** Auto-bidding, Anti-sniping, hoặc Biểu đồ giá trực tiếp.

---

### 4. Công nghệ sử dụng (Tech Stack)
- **Ngôn ngữ:** Java ,CSS
- **Giao diện:** JavaFX 
- **Build tool:** Maven 
- **Kiểm thử:** JUnit (cho các logic quan trọng như xử lý đấu giá đồng thời)
- **Công cụ khác:** Scene Builder

---

### 5. Hướng dẫn cài đặt và chạy (Installation & Running)
- **Yêu cầu:** JDK 25, Maven, MySQL reachable from the values in `db.properties`.
- **Cấu trúc Client-Server:**
  - `server`: Spring Boot REST API, owns database access and business services.
  - `client`: JavaFX application, talks to the server through HTTP DTO/services.
  - `temp`: archived copy of the old monolithic source, no longer part of the Maven reactor.
- **Cách chạy Server:**
  ```bash
  mvn -pl server spring-boot:run
  ```
  Health check: `GET http://localhost:8080/api/health`
- **Cách chạy Client:**
  ```bash
  mvn -pl client javafx:run
  ```
  By default the client calls `http://localhost:8080`. To point it at another server:
  ```bash
  mvn -pl client javafx:run -Dserver.url=http://HOST:8080
  ```
- **Auth API hiện có:**
  - `POST /api/auth/register`
  - `POST /api/auth/login`
- **Auction/User API hiện có:**
  - `GET /api/auctions`
  - `GET /api/auctions/{id}`
  - `POST /api/auctions`
  - `POST /api/auctions/{id}/bids`
  - `POST /api/auctions/{id}/cancel`
  - `POST /api/auctions/{id}/confirm-receipt`
  - `GET /api/users`
  - `GET /api/users/{id}`
  - `POST /api/users/{id}/block`
  - `POST /api/users/{id}/unblock`

---


### 6. Minh chứng tiến độ
<img width="3619" height="1910" alt="Diagram_src_BTL_Gr7" src="https://github.com/user-attachments/assets/8ec92873-69a9-48c3-a3bc-3f9fed16cbfc" />
