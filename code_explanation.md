# Giải thích Luồng chạy và Cấu trúc Code

Tài liệu này giải thích chi tiết về cách hoạt động của hệ thống Quản lý Sinh viên và tác dụng của từng file trong dự án.

## 1. Luồng chạy của ứng dụng (Execution Flow)

### A. Đăng nhập (Login Flow)
1.  **User** truy cập `index.html`.
2.  Nhập `Student Code` và `Password`.
3.  **Frontend** (`auth.js`) gọi API `POST /api/auth/login`.
4.  **Backend** (`AuthResource.java`):
    *   Nhận thông tin đăng nhập.
    *   Kiểm tra trong Database (`students` table) hoặc tài khoản Admin cứng.
    *   Nếu đúng, tạo **JWT Token** chứa thông tin user và role (ADMIN hoặc STUDENT).
    *   Trả về Token cho Frontend.
5.  **Frontend**:
    *   Lưu Token vào `localStorage`.
    *   Chuyển hướng sang `dashboard.html`.

### B. Trang Dashboard (Dashboard Flow)
1.  **User** vào `dashboard.html`.
2.  **Frontend** (`dashboard.js`):
    *   Kiểm tra Token trong `localStorage`. Nếu không có -> đá về trang Login.
    *   Gọi API `GET /api/students` và `GET /api/courses` để lấy dữ liệu.
    *   Gửi kèm Token trong Header `Authorization: Bearer <token>`.
3.  **Backend** (`AuthenticationFilter.java`):
    *   Chặn các request này lại.
    *   Kiểm tra Token có hợp lệ không.
    *   Kiểm tra Role (nếu User đang cố Thêm/Sửa/Xóa).
    *   Nếu hợp lệ -> Cho phép request đi tiếp đến Resource.
4.  **Backend** (`StudentResource.java` / `CourseResource.java`):
    *   Truy vấn Database thông qua `DatabaseConnection.java`.
    *   Trả về dữ liệu JSON.
5.  **Frontend**:
    *   Nhận JSON và hiển thị lên bảng (Table).
    *   Nếu là **Sinh viên**: Chỉ hiện thông tin của mình và ẩn các nút Thêm/Xóa.
    *   Nếu là **Admin**: Hiện tất cả và cho phép thao tác đầy đủ.

---

## 2. Giải thích chi tiết từng file

### A. Backend (Java - `src/main/java`)

#### 1. Cấu hình & Tiện ích
*   **`com.example.student.config.ApplicationConfig.java`**:
    *   Cấu hình chính của Jersey (JAX-RS).
    *   Định nghĩa đường dẫn gốc cho API là `/api`.
    *   Khai báo các package cần quét để tìm API (Resource) và Filter.
*   **`com.example.student.util.DatabaseConnection.java`**:
    *   Quản lý kết nối tới MySQL Database.
    *   Chứa thông tin URL, User, Password của DB.
    *   Cung cấp hàm `getConnection()` để các Resource khác sử dụng.

#### 2. Bảo mật (Security)
*   **`com.example.student.filter.AuthenticationFilter.java`**:
    *   "Người gác cổng" của hệ thống.
    *   Tự động chạy trước mỗi request vào API.
    *   Nhiệm vụ:
        1.  Kiểm tra xem request có kèm Token không.
        2.  Xác thực Token (có đúng chữ ký không, hết hạn chưa).
        3.  Phân quyền (RBAC): Chặn Sinh viên gọi các API dành riêng cho Admin (như Thêm/Xóa).

#### 3. Mô hình dữ liệu (Model)
*   **`com.example.student.model.Student.java`**: Class đại diện cho Sinh viên (id, tên, mã, mật khẩu...).
*   **`com.example.student.model.Course.java`**: Class đại diện cho Khóa học.
*   **`com.example.student.model.User.java`**: Class dùng để hứng dữ liệu JSON khi đăng nhập.

#### 4. API Resources (Controller)
*   **`com.example.student.resource.AuthResource.java`**:
    *   Xử lý đăng nhập (`/auth/login`).
    *   Tạo ra JWT Token.
*   **`com.example.student.resource.StudentResource.java`**:
    *   Cung cấp các API CRUD cho Sinh viên (`/students`).
    *   Xử lý logic lọc sinh viên theo mã (cho user thường).
*   **`com.example.student.resource.CourseResource.java`**:
    *   Cung cấp API CRUD cho Khóa học (`/courses`).
*   **`com.example.student.resource.FinanceResource.java`**:
    *   API tính học phí (`/finance/tuition`).

---

### B. Frontend (Web - `src/main/webapp`)

#### 1. Giao diện (HTML)
*   **`index.html`**:
    *   Trang Đăng nhập.
    *   Chứa form nhập liệu đơn giản.
*   **`dashboard.html`**:
    *   Trang chính sau khi đăng nhập.
    *   Chứa các bảng hiển thị Sinh viên, Khóa học và công cụ tính học phí.

#### 2. Logic (JavaScript - `js/`)
*   **`js/api.js`**:
    *   Chứa tất cả các hàm gọi xuống Server (fetch API).
    *   Tự động gắn Token vào Header mỗi khi gọi API.
    *   Giúp code gọn gàng, không phải viết lại `fetch` nhiều lần.
*   **`js/auth.js`**:
    *   Quản lý việc Đăng nhập/Đăng xuất.
    *   Lưu và đọc Token từ `localStorage`.
    *   Giải mã Token để biết ai đang đăng nhập (Role là gì).
*   **`js/dashboard.js`**:
    *   Logic riêng cho trang Dashboard.
    *   Tải dữ liệu sinh viên/khóa học khi trang vừa load.
    *   Xử lý ẩn/hiện các nút bấm dựa trên Role (Admin thấy nút Xóa, Sinh viên thì không).

#### 3. Giao diện (CSS - `css/`)
*   **`css/style.css`**:
    *   Chứa toàn bộ style (màu sắc, bố cục) cho cả trang Login và Dashboard.

---

### C. Database
*   **`database.sql`**:
    *   Script SQL để khởi tạo cấu trúc bảng (`students`, `courses`) và dữ liệu mẫu ban đầu.
