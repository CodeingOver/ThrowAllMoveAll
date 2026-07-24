# Nhật ký thay đổi (CHANGELOG)

Tất cả những thay đổi quan trọng của dự án Mod Minecraft **ThrowAll & MoveAll** sẽ được ghi nhận tại tài liệu này.

---

### [v1.0.0] - 2026-07-24

- **[Thêm mới]** Cấu hình dự án chuẩn Maven POM (`pom.xml`) hỗ trợ Minecraft 1.20.4 trên nền tảng Fabric.
- **[Thêm mới]** Tích hợp cấu hình phụ trợ Gradle Loom (`build.gradle`, `gradle.properties`, `settings.gradle`) hỗ trợ remapping bytecode mượt mà.
- **[Thêm mới]** Tính năng **ThrowAll**: Thả toàn bộ vật phẩm cùng loại hoặc toàn bộ ô vật phẩm ra đất thông qua hotkey (mặc định phím `V`).
- **[Thêm mới]** Tính năng **MoveAll**: Di chuyển nhanh toàn bộ vật phẩm giữa kho cá nhân và Container/Rương thông qua hotkey (mặc định phím `X`).
- **[Thêm mới]** Hệ thống đăng ký phím tắt `KeyBindings` và lắng nghe sự kiện client tick `ClientTickEvents`.
- **[Cập nhật]** Chuẩn hóa cú pháp gán thuộc tính `url = uri(...)` trong file `build.gradle` xử lý cảnh báo deprecation của Gradle 8+.
- **[Thêm mới]** Cấu hình `exec-maven-plugin` trong `pom.xml` giúp tự động gọi `gradlew build` / `gradle build` khi người dùng nhấn nút `package` trên Maven Lifecycle Panel.
- **[Cập nhật]** Tối ưu hóa toàn diện Client-side: Tích hợp `HandledScreenAccessor` Mixin để truy cập `focusedSlot` tốc độ cao không tốn chi phí Reflection, bổ sung bộ lọc an toàn `shouldSkipSlot` chống Server Desync và ngăn chặn kích hoạt ô chế tạo ngoài ý muốn (`CraftingResultInventory`).
- **[Cập nhật]** Tùy chỉnh tính năng `ThrowAll` và `MoveAll`: Vô hiệu hóa hoàn toàn phím tắt khi không mở giao diện GUI kho đồ/rương (tránh vứt nhầm vật phẩm khi đang di chuyển ngoài game).
- **[Xóa bỏ]** Loại bỏ tính năng di chuyển / vứt toàn bộ kho đồ khi nhấn phím tại ô trống (chỉ cho phép hoạt động khi con trỏ chuột trỏ trực tiếp vào ô có vật phẩm).
- **[Cập nhật]** Nâng cấp Gradle Wrapper 8.7 và Fabric Loom 1.6-SNAPSHOT, biên dịch thành công sản phẩm `throwallmoveall-1.0.0.jar` qua lệnh `mvn package`.
- **[Sửa lỗi]** Chuẩn hóa chuỗi hiển thị phiên bản `"1.0.0"` trong `fabric.mod.json` (khắc phục lỗi hiển thị `${version}` trên Mod Menu / Prism Launcher).
- **[Thêm mới]** Bổ sung tệp ngôn ngữ `en_us.json` và `vi_vn.json` hiển thị danh mục và tên phím tắt tùy chỉnh trong menu Cài đặt điều khiển (Controls -> Key Binds) của Minecraft.
- **[Sửa lỗi]** Xóa bỏ block `processResources expand` trong `build.gradle` đảm bảo Gradle copy chính xác file `fabric.mod.json` với `"version": "1.0.0"` trực tiếp vào file nén `.jar` mà không bị ghi đè thành `${version}`.
- **[Thêm mới]** Tài liệu kiến trúc `docs/architecture.md` và tài liệu hướng dẫn `README.md` bằng Tiếng Việt có dấu đầy đủ.
