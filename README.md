# ThrowAll & MoveAll Mod (Minecraft 1.20.4)

Một Client-side Mod dành cho Minecraft 1.20.4 được phát triển theo chuẩn Maven POM (`pom.xml`), trích xuất 2 tính năng chuyển đổi & vứt vật phẩm tiện lợi nhất từ Item Scroller: **ThrowAll** và **MoveAll**.

---

## 1. Tên dự án & Giới thiệu
- **Tên dự án:** ThrowAll & MoveAll Mod
- **Phiên bản Minecraft hỗ trợ:** 1.20.4 (Fabric Client)
- **Mục đích:** Cung cấp giải pháp tối ưu thao tác quản lý kho đồ trong Minecraft thông qua các phím tắt tiện lợi mà không cần cài đặt các thư viện nặng phức tạp.

---

## 2. Tính năng chính
- **ThrowAll (Thả toàn bộ vật phẩm cùng loại):**
  - Khi trỏ chuột vào một ô vật phẩm trong kho đồ/rương (GUI) và nhấn phím tắt, toàn bộ vật phẩm cùng loại ở mọi ô sẽ lập tức được vứt ra ngoài.
  - Chỉ hoạt động khi đang mở giao diện GUI kho đồ/rương và trỏ chuột vào ô chứa vật phẩm.
- **MoveAll (Di chuyển toàn bộ vật phẩm cùng loại):**
  - Tự động di chuyển tất cả các stack vật phẩm cùng loại từ rương/container sang kho cá nhân của người chơi (hoặc ngược lại) chỉ với 1 thao tác nhấn phím khi đang mở giao diện GUI và trỏ chuột vào ô chứa vật phẩm.

---

## 3. Yêu cầu hệ thống
- **Java Development Kit (JDK):** Java 17 trở lên.
- **Build Tool:** Apache Maven 3.8+ (hoặc Gradle 8.x với Fabric Loom).
- **Minecraft Launcher:** Prism Launcher, Modrinth App, hoặc Official Launcher cài sẵn **Fabric Loader 0.15.7+** cho Minecraft 1.20.4.

---

## 4. Hướng dẫn cài đặt
1. **Tải mã nguồn dự án:**
   ```bash
   git clone https://github.com/example/ThrowAllMoveAll.git
   cd ThrowAllMoveAll
   ```
2. **Biên dịch Mod:**
   - Dùng Maven:
     ```bash
     mvn clean package
     ```
   - Hoặc dùng Gradle:
     ```bash
     ./gradlew build
     ```
3. **Cài đặt vào Minecraft:**
   - Copy file `.jar` vừa biên dịch trong thư mục `target/` (hoặc `build/libs/`) vào thư mục `.minecraft/mods/`.

---

## 5. Biến môi trường
Dự án này không yêu cầu biến môi trường hệ thống đặc biệt. Tất cả thông số cấu hình phiên bản được quản lý trong file [pom.xml](file:///d:/CodeJava/ModMinecraft/ThowAllMoveAll/pom.xml) và [gradle.properties](file:///d:/CodeJava/ModMinecraft/ThowAllMoveAll/gradle.properties):

| Tên thuộc tính | Giá trị mặc định | Mô tả |
| :--- | :--- | :--- |
| `minecraft.version` | `1.20.4` | Phiên bản Minecraft mục tiêu |
| `fabric.loader.version` | `0.15.7` | Phiên bản Fabric Loader |
| `fabric.api.version` | `0.97.0+1.20.4` | Phiên bản Fabric API tương thích |

---

## 6. Hướng dẫn chạy & Sử dụng
1. **Chạy trong môi trường Development:**
   ```bash
   ./gradlew runClient
   ```
2. **Phím tắt mặc định trong game:**
   - **Vứt toàn bộ vật phẩm (ThrowAll):** Phím `V` (Có thể tùy chỉnh trong menu Controls -> Key Binds).
   - **Di chuyển toàn bộ vật phẩm (MoveAll):** Phím `X` (Có thể tùy chỉnh trong menu Controls -> Key Binds).
