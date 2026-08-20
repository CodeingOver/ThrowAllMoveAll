# ThrowAll & MoveAll Mod (Minecraft 1.19 → 26.2 Chaos Cubed)

Một Client-side Mod hỗ trợ đa phiên bản Minecraft (từ 1.19 đến 26.2), trích xuất 2 tính năng chuyển đổi & vứt vật phẩm tiện lợi nhất từ Item Scroller: **ThrowAll** và **MoveAll**. Hỗ trợ tệp cấu hình JSON ngoài độc lập và phím tắt tổ hợp Combo phức tạp (`Alt + Q`, `Ctrl + Shift + V`...).

---

## 1. Tên dự án & Giới thiệu
- **Tên dự án:** ThrowAll & MoveAll Mod
- **Phiên bản Minecraft hỗ trợ:** Tất cả phiên bản chính từ **Minecraft 1.19 đến 26.2** (Chaos Cubed)
- **Kiến trúc:** Multi-Project Gradle (`common/` + subprojects `versions/1.19` ... `versions/26.2`)
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
- **Hỗ trợ đa phiên bản (Multi-Version Support):**
  - Hỗ trợ 14 phiên bản Minecraft: `1.19`, `1.19.2`, `1.19.4`, `1.20`, `1.20.1`, `1.20.2`, `1.20.4`, `1.20.6`, `1.21`, `1.21.1`, `1.21.4`, `1.21.5`, `26.1` (Tiny Takeover), `26.2` (Chaos Cubed).

---

## 3. Yêu cầu hệ thống
- **Java Development Kit (JDK):**
  - **Era 1 (1.19 → 1.20.4):** Java 17.
  - **Era 2 (1.20.5 → 1.21.5):** Java 21.
  - **Era 3 (26.1 → 26.2+):** Java 25.
- **Build Tool:** Gradle 8.x/9.x (kèm Gradle Wrapper `gradlew`).
- **Minecraft Launcher:** Prism Launcher, Modrinth App, hoặc Official Launcher cài sẵn **Fabric Loader**.

---

## 4. Hướng dẫn cài đặt & Biên dịch

### 1. Tải mã nguồn dự án:
```bash
git clone https://github.com/example/ThrowAllMoveAll.git
cd ThrowAllMoveAll
```

### 2. Biên dịch Mod cho phiên bản cụ thể:
- Biên dịch cho Minecraft 1.20.4:
  ```bash
  ./gradlew :versions:1.20.4:build
  ```
- Biên dịch cho Minecraft 1.21.4:
  ```bash
  ./gradlew :versions:1.21.4:build
  ```
- Biên dịch cho Minecraft 26.2 (Chaos Cubed):
  ```bash
  ./gradlew :versions:26.2:build
  ```

### 3. Biên dịch tất cả các phiên bản cùng lúc:
Chạy lệnh duy nhất sau:
```bash
./gradlew buildAll
```
Tất cả các file `.jar` hoàn chỉnh của 14 phiên bản sẽ được tự động tổng hợp vào thư mục **`dist/`**.

### 4. Cài đặt vào Minecraft:
- Copy file `.jar` tương ứng từ `versions/<phiên-bản>/build/libs/` hoặc `dist/` vào thư mục `.minecraft/mods/`.

---

## 5. Cấu trúc Multi-Project Gradle

```text
throwallmoveall/
├── common/                  # Mã nguồn dùng chung không phụ thuộc phiên bản
│   ├── src/main/java/       # Core logic (InventoryHelper, KeyHandlers, ModConfig)
│   └── src/main/resources/  # Assets chung (icons, lang files)
├── versions/                # Subprojects riêng cho từng phiên bản Minecraft
│   ├── 1.19/                # Subproject MC 1.19 (Legacy Screen API)
│   ├── 1.20.4/              # Subproject MC 1.20.4 (Java 17, DrawContext)
│   ├── 1.21.4/              # Subproject MC 1.21.4 (Java 21, Fabric Loom 1.9)
│   └── 26.2/                # Subproject MC 26.2 (Java 25, Mojang Official Mappings)
├── dist/                    # Nơi chứa các file .jar đầu ra của tất cả phiên bản
├── build.gradle             # File cấu hình tổng (Multi-Project Task buildAll & collectJars)
└── settings.gradle          # Khai báo tất cả 14 subproject `:versions:<ver>`
```

---

## 6. Hướng dẫn chạy & Sử dụng
1. **Tùy chỉnh tệp cấu hình JSON ngoài:**
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
2. **Tùy chỉnh qua giao diện In-Game Mod Menu:**
   - Mở **Mod Menu** -> Tìm **ThrowAll & MoveAll Mod** -> Bấm **Configure** để chỉnh trực quan phím tắt & các cờ Alt/Ctrl/Shift.
