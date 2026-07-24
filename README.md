# ThrowAll & MoveAll Mod (Minecraft 1.20.4)

Một Client-side Mod dành cho Minecraft 1.20.4 được phát triển theo chuẩn Maven POM (`pom.xml`), trích xuất 2 tính năng chuyển đổi & vứt vật phẩm tiện lợi nhất từ Item Scroller: **ThrowAll** và **MoveAll**. Hỗ trợ tệp cấu hình JSON ngoài độc lập và phím tắt tổ hợp Combo phức tạp (`Alt + Q`, `Ctrl + Shift + V`...).

---

## 1. Tên dự án & Giới thiệu
- **Tên dự án:** ThrowAll & MoveAll Mod
- **Phiên bản Minecraft hỗ trợ:** 1.20.4 (Fabric Client)
- **Mục đích:** Cung cấp giải pháp tối ưu thao tác quản lý kho đồ trong Minecraft thông qua các phím tắt tiện lợi mà không cần cài đặt các thư viện nặng phức tạp.

---

## 2. Tính năng chính
- **Cấu hình độc lập ngoài (`.minecraft/config/throwallmoveall.json`):**
  - Tự động tạo và lưu trữ tệp cấu hình JSON ngoài. Không phụ thuộc vào menu Controls mặc định.
- **Hỗ trợ phím tắt tổ hợp Combo:**
  - Cho phép gán các phím tổ hợp dạng `Alt + Key` (VD: `Alt + Q`), `Ctrl + Key`, `Shift + Key` hoặc kết hợp đồng thời (`Ctrl + Shift + V`).
- **Giao diện Cài đặt In-Game (ModMenu GUI):**
  - Tích hợp nút **Configure** trong danh sách Mod Menu của game, cho phép đổi nút bấm trực quan.
- **ThrowAll (Thả toàn bộ vật phẩm cùng loại):**
  - Khi trỏ chuột vào một ô vật phẩm trong kho đồ/rương (GUI) và nhấn phím tắt tổ hợp, toàn bộ vật phẩm cùng loại ở mọi ô sẽ lập tức được vứt ra ngoài.
- **MoveAll (Di chuyển toàn bộ vật phẩm cùng loại):**
  - Tự động di chuyển tất cả các stack vật phẩm cùng loại từ rương/container sang kho cá nhân của người chơi (hoặc ngược lại) chỉ với 1 thao tác nhấn phím tổ hợp khi đang mở giao diện GUI.

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
   - Copy file `throwallmoveall-1.1.0.jar` vừa biên dịch trong thư mục `target/` (hoặc `build/libs/`) vào thư mục `.minecraft/mods/`.

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
2. **Tùy chỉnh tệp cấu hình JSON ngoài:**
   - Mở tệp `.minecraft/config/throwallmoveall.json`:
     ```json
     {
       "throwAllKey": 81,
       "throwAllAlt": true,
       "throwAllCtrl": false,
       "throwAllShift": false,
       "moveAllKey": 88,
       "moveAllAlt": false,
       "moveAllCtrl": true,
       "moveAllShift": true
     }
     ```
3. **Tùy chỉnh qua giao diện In-Game Mod Menu:**
   - Mở **Mod Menu** -> Tìm **ThrowAll & MoveAll Mod** -> Bấm **Configure** để chỉnh trực quan phím tắt & các cờ Alt/Ctrl/Shift.
