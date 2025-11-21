# Student Management API

Dự án quản lý sinh viên sử dụng Java JAX-RS (Jersey), MySQL và JWT Authentication.

## 1. Cấu trúc dự án

```
student-management/
├── src/main/java/com/example/student/
│   ├── config/
│   │   └── ApplicationConfig.java      # Cấu hình Jersey API (/api/*)
│   ├── filter/
│   │   └── AuthenticationFilter.java   # Filter kiểm tra JWT Token
│   ├── model/
│   │   ├── Student.java                # Model Sinh viên (có password)
│   │   ├── Course.java                 # Model Khóa học
│   │   ├── CourseStatus.java           # Enum trạng thái khóa học
│   │   └── User.java                   # Model User (dùng cho login)
│   ├── resource/
│   │   ├── AuthResource.java           # API Login (/auth/login)
│   │   ├── StudentResource.java        # API Sinh viên (CRUD)
│   │   ├── CourseResource.java         # API Khóa học
│   │   └── FinanceResource.java        # API Tính học phí
│   └── util/
│       └── DatabaseConnection.java     # Kết nối MySQL (JDBC)
├── src/main/webapp/
│   ├── WEB-INF/web.xml                 # Descriptor triển khai Servlet
│   ├── index.html                      # Trang Login
│   └── dashboard.html                  # Trang xem danh sách sinh viên
├── database.sql                        # Script tạo database và dữ liệu mẫu
├── pom.xml                             # Quản lý dependency (Maven)
└── README.md                           # Hướng dẫn sử dụng
```

## 2. Yêu cầu hệ thống

*   **Java**: JDK 17 trở lên.
*   **Maven**: 3.8+.
*   **Tomcat**: 9.0+.
*   **Docker** (để chạy MySQL).

## 3. Cài đặt và Chạy

### Bước 1: Khởi động Database (MySQL)
Chạy lệnh Docker sau để tạo container MySQL:
```bash
docker run --name student-mysql -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=student_db -p 3306:3306 -d mysql:8.0
```

### Bước 2: Khởi tạo dữ liệu
Chạy script SQL trong file `database.sql` để tạo bảng và dữ liệu mẫu. Bạn có thể dùng MySQL Workbench hoặc DBeaver kết nối tới `localhost:3306` (User: `root`, Pass: `1234`).

**Nội dung script (tóm tắt):**
```sql
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    student_code VARCHAR(50) NOT NULL UNIQUE,
    major VARCHAR(100),
    password VARCHAR(255) NOT NULL
);
-- ... (xem file database.sql để có đầy đủ script)
```

### Bước 3: Build và Deploy (Dùng Terminal)
1.  Mở terminal tại thư mục dự án.
2.  Chạy lệnh build:
    ```bash
    mvn clean install
    ```
3.  Copy file `.war` trong thư mục `target/` vào thư mục `webapps/` của Tomcat.
4.  Khởi động Tomcat (`bin/startup.sh` hoặc `bin/startup.bat`).

### Bước 4: Cài đặt và Chạy trên IntelliJ IDEA (Khuyên dùng)

Nếu bạn sử dụng IntelliJ IDEA (Ultimate Edition là tốt nhất cho Web App), hãy làm theo các bước sau:

1.  **Mở dự án**:
    *   Chọn `File` -> `Open`.
    *   Trỏ tới thư mục `student-management` (nơi chứa file `pom.xml`).
    *   Chọn `Open as Project`.

2.  **Cấu hình Tomcat**:
    *   Trên thanh công cụ, chọn `Add Configuration...`.
    *   Nhấn dấu `+` -> Chọn `Tomcat Server` -> `Local`.
    *   Trong tab **Server**:
        *   Nhấn `Configure...` để trỏ tới thư mục cài đặt Tomcat của bạn.
        *   Đặt `HTTP port` là `8080`.
    *   Trong tab **Deployment**:
        *   Nhấn dấu `+` -> `Artifact...`.
        *   Chọn `student-management:war exploded`.
        *   Đảm bảo `Application context` là `/` (hoặc để mặc định, nhưng URL sẽ dài hơn).
    *   Nhấn `OK`.

3.  **Chạy dự án**:
    *   Nhấn nút `Run` (hình tam giác xanh) hoặc `Debug` (hình con bọ).
    *   IntelliJ sẽ tự động build và deploy lên Tomcat.
    *   Trình duyệt sẽ tự mở trang chủ (thường là `http://localhost:8080/`).

## 4. Hướng dẫn sử dụng

### Cách 1: Sử dụng Web UI
1.  Truy cập: `http://localhost:8080/`
2.  Đăng nhập bằng tài khoản sinh viên mẫu:
    *   **Student Code**: `SV001`
    *   **Password**: `123456`
3.  Sau khi đăng nhập thành công, bạn sẽ thấy danh sách sinh viên.

### Cách 2: Sử dụng Postman
Import file `StudentManagement_Postman_Collection.json` vào Postman.

#### 1. Login (Lấy Token)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/auth/login`
*   **Body**:
    ```json
    {
        "username": "SV001",
        "password": "123456"
    }
    ```
*   **Kết quả**: Trả về `token`. Postman sẽ tự động lưu token này vào biến `jwt_token`.

#### 2. Gọi API bảo mật (Ví dụ: Thêm sinh viên)
*   **Method**: `POST`
*   **URL**: `http://localhost:8080/api/students`
*   **Auth**: Chọn Type là `Bearer Token`, điền `{{jwt_token}}`.
*   **Body**:
    ```json
    {
        "fullName": "Nguyen Van Moi",
        "studentCode": "SV999",
        "major": "AI",
        "password": "123"
    }
    ```

## 5. Các API chính

| Method | Endpoint | Mô tả | Yêu cầu Auth |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/login` | Đăng nhập lấy Token | Không |
| **GET** | `/api/students` | Lấy danh sách sinh viên | Không |
| **POST** | `/api/students` | Thêm sinh viên mới | **Có** |
| **PUT** | `/api/students/{id}` | Cập nhật sinh viên | **Có** |
| **DELETE** | `/api/students/{id}` | Xóa sinh viên | **Có** |
| **GET** | `/api/courses` | Lấy danh sách khóa học | Không |
| **POST** | `/api/courses` | Thêm khóa học | Không |
| **GET** | `/api/finance/tuition` | Tính học phí | Không |

## 6. Lưu ý
*   Mật khẩu database được cấu hình trong `src/main/java/com/example/student/util/DatabaseConnection.java` là `1234`.
*   Token có hiệu lực trong 1 giờ.
