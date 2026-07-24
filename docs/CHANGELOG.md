# Nhật ký thay đổi (CHANGELOG)

Tất cả những thay đổi quan trọng của dự án Mod Minecraft **ThrowAll & MoveAll** sẽ được ghi nhận tại tài liệu này.

---

### [v1.1.1] - 2026-07-24

- **[Sửa lỗi]** Sửa lỗi cảnh báo `broken icon` của Fabric Loader bằng cách re-encode tệp `icon.png` thành định dạng chuẩn 128x128 32-bit ARGB PNG.
- **[Sửa lỗi]** Khắc phục triệt để lỗi không gán được phím tổ hợp: Tự động nhận diện và cập nhật trạng thái phím bổ trợ (`Alt`, `Ctrl`, `Shift`) khi người dùng nhấn tổ hợp phím (như `Alt + Q`), đồng thời chờ phím chính để hoàn tất gán phím.
- **[Cập nhật]** Anh hóa (English-ized) toàn bộ giao diện Cài đặt In-Game (`ModConfigScreen`), các nhãn điều khiển, gợi ý hướng dẫn và các chuỗi nhật ký ứng dụng.
- **[Cập nhật]** Tăng phiên bản chính thức lên `v1.1.1` đồng bộ trên `pom.xml`, `gradle.properties` và `fabric.mod.json`.

---

### [v1.1.0] - 2026-07-24

- **[Thêm mới]** Tệp cấu hình JSON độc lập bên ngoài (`.minecraft/config/throwallmoveall.json`) hỗ trợ tự động lưu trữ và tùy biến cài đặt phím tắt không phụ thuộc vào menu Controls mặc định.
- **[Thêm mới]** Hỗ trợ phím tắt tổ hợp Combo phức tạp kết hợp cùng phím bổ trợ Modifier: `Alt + Key` (VD: `Alt + Q`), `Ctrl + Key`, `Shift + Key` hoặc kết hợp nhiều Modifier (`Ctrl + Shift + V`).
- **[Thêm mới]** Màn hình giao diện cài đặt In-Game `ModConfigScreen` trực quan giúp tùy chỉnh các nút bấm hotkey & bật tắt trạng thái Alt / Ctrl / Shift.
- **[Thêm mới]** Tích hợp nút Configure trong danh sách Mod Menu của game thông qua `ModMenuApi`.

---

### [v1.0.4] - 2026-07-24

- **[Cập nhật]** Đổi tên file mod thành `throwallmoveall-1.0.4.jar` để người dùng dễ dàng phân biệt và loại bỏ hoàn toàn các file mod cũ trong thư mục `.minecraft/mods/`.
- **[Cập nhật]** Tăng phiên bản chính thức lên `v1.0.4` trên `pom.xml`, `gradle.properties` và `fabric.mod.json`.

---

### [v1.0.3] - 2026-07-24

- **[Sửa lỗi]** Loại bỏ hoàn toàn Mixin khỏi dự án và chuyển sang dùng Reflection tự động dò tìm field `focusedSlot` có caching hiệu năng cao. Triệt tiêu 100% nguy cơ crash `SodiumPreLaunch` ClassNotFoundException.
- **[Cập nhật]** Tăng phiên bản chính thức lên `v1.0.3` trên `pom.xml`, `gradle.properties` và `fabric.mod.json`.

---

### [v1.0.2] - 2026-07-24

- **[Sửa lỗi]** Sửa lỗi crash game khi nạp cùng Sodium (`SodiumPreLaunch` ClassNotFoundException): Chuẩn hóa cấu hình Mixin Client Side trong `throwallmoveall.mixins.json` và bổ sung khối xử lý ngoại lệ an toàn cho `HandledScreenAccessor`.
- **[Cập nhật]** Tăng phiên bản chính thức lên `v1.0.2` đồng bộ trên `pom.xml`, `gradle.properties` và `fabric.mod.json`.

---

### [v1.0.1] - 2026-07-24

- **[Sửa lỗi]** Chuẩn hóa chuỗi hiển thị phiên bản trong `fabric.mod.json` (khắc phục lỗi hiển thị `${version}` trên Mod Menu / Prism Launcher).
- **[Sửa lỗi]** Xóa bỏ block `processResources expand` trong `build.gradle` đảm bảo Gradle copy chính xác file `fabric.mod.json` trực tiếp vào file nén `.jar` mà không bị ghi đè.
- **[Thêm mới]** Bổ sung tệp ngôn ngữ `en_us.json` và `vi_vn.json` hiển thị danh mục và tên phím tắt tùy chỉnh trong menu Cài đặt điều khiển (Controls -> Key Binds) của Minecraft.

---

### [v1.0.0] - 2026-07-24

- **[Thêm mới]** Cấu hình dự án chuẩn Maven POM (`pom.xml`) hỗ trợ Minecraft 1.20.4 trên nền tảng Fabric.
- **[Thêm mới]** Tích hợp cấu hình phụ trợ Gradle Loom (`build.gradle`, `gradle.properties`, `settings.gradle`) hỗ trợ remapping bytecode mượt mà.
- **[Thêm mới]** Tính năng **ThrowAll**: Thả toàn bộ vật phẩm cùng loại hoặc toàn bộ ô vật phẩm ra đất thông qua hotkey (mặc định phím `V`).
- **[Thêm mới]** Tính năng **MoveAll**: Di chuyển nhanh toàn bộ vật phẩm giữa kho cá nhân và Container/Rương thông qua hotkey (mặc định phím `X`).
- **[Thêm mới]** Hệ thống đăng ký phím tắt `KeyBindings` và lắng nghe sự kiện client tick `ClientTickEvents`.
- **[Cập nhật]** Tối ưu hóa toàn diện Client-side: Tích hợp `HandledScreenAccessor` Mixin để truy cập `focusedSlot` tốc độ cao không tốn chi phí Reflection, bổ sung bộ lọc an toàn `shouldSkipSlot` chống Server Desync và ngăn chặn kích hoạt ô chế tạo ngoài ý muốn (`CraftingResultInventory`).
- **[Cập nhật]** Tùy chỉnh tính năng `ThrowAll` và `MoveAll`: Vô hiệu hóa hoàn toàn phím tắt khi không mở giao diện GUI kho đồ/rương (tránh vứt nhầm vật phẩm khi đang di chuyển ngoài game).
- **[Xóa bỏ]** Loại bỏ tính năng di chuyển / vứt toàn bộ kho đồ khi nhấn phím tại ô trống (chỉ cho phép hoạt động khi con trỏ chuột trỏ trực tiếp vào ô có vật phẩm).
- **[Thêm mới]** Tài liệu kiến trúc `docs/architecture.md` và tài liệu hướng dẫn `README.md` bằng Tiếng Việt có dấu đầy đủ.
