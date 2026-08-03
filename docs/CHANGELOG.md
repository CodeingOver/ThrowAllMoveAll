# Nhật ký thay đổi (CHANGELOG)

Tất cả những thay đổi quan trọng của dự án Mod Minecraft **ThrowAll & MoveAll** sẽ được ghi nhận tại tài liệu này.

---

### [v1.5.0] - 2026-08-03

- **[Thêm mới]** Hệ thống build đa phiên bản Minecraft trong một project duy nhất:
  - **`build.gradle`** được thiết kế lại hoàn toàn với **Version Database** nhúng sẵn, hỗ trợ 14 phiên bản từ 1.19 đến 26.2 (Chaos Cubed).
  - Lệnh build theo version: `./gradlew build -PmcVersion=1.21.4` hoặc `./gradlew buildAll` để build tất cả.
  - Tự động điều chỉnh: Java version (17 / 21 / 25), Yarn mappings, Fabric API, ModMenu, Fabric Loom phù hợp từng phiên bản.
  - **Era 3 (26.x):** Tự động chuyển sang `loom.officialMojangMappings()` (Mojang Official Mappings) thay Yarn do game 26.x không còn obfuscation.
- **[Thêm mới]** `src/legacy/java/` — Thư mục source riêng chứa `ModConfigScreen` phiên bản Legacy cho Minecraft 1.19.x:
  - Sử dụng API `MatrixStack` (1.19.x) thay vì `DrawContext` (1.20+).
  - Tự động được áp dụng khi build với `-PmcVersion=1.19`, `-PmcVersion=1.19.2`, `-PmcVersion=1.19.4`.
- **[Thêm mới]** `fabric.mod.json` template — Tự động inject đúng `minecraft_version`, `java_min`, `loader_min`, `mod_version` vào metadata thông qua Gradle `processResources`.
- **[Thêm mới]** `scripts/build-all.bat` — Script Windows tự động build tất cả 14 phiên bản, thu thập jar vào `dist/`, hỗ trợ cấu hình `JAVA17_HOME`, `JAVA21_HOME`, `JAVA25_HOME`.
- **[Thêm mới]** `scripts/create-branches.ps1` — Script PowerShell tham khảo (nếu muốn dùng Git branch per version sau này).
- **[Thêm mới]** Tích hợp plugin `foojay-resolver-convention` (`0.8.0`) vào `settings.gradle` giúp Gradle tự động nhận diện và chuyển đổi linh hoạt giữa JDK 17, JDK 21 và JDK 25 qua Gradle Toolchains.
- **[Cập nhật]** Nâng cấp `fabric-loom` cho subproject 26.1 và 26.2 lên **`1.15-SNAPSHOT`** để hỗ trợ thư viện ASM phân tích lớp bytecode **Java 25** (`major version 69`).
- **[Sửa lỗi]** Đã sửa lỗi tham chiếu `ModConfigScreen` trong 1.19/1.19.2/1.19.4 bằng closure loại trừ chính xác tệp `common`, và đồng bộ hóa hàm `renderBackground(ctx)` đa phiên bản cho 1.20 và 1.20.1.
- **[Sửa lỗi]** Sửa định dạng URL tải Gradle Wrapper thành `gradle-9.4.0-bin.zip` (chuẩn 3 chữ số phiên bản của Gradle Server).
- **[Cập nhật]** Đồng bộ hóa phiên bản `fabric-loom`: dùng `1.9-SNAPSHOT` cho subproject 1.19 → 1.21.4, và `1.10-SNAPSHOT` cho 1.21.5, 26.1, 26.2.
- **[Sửa lỗi]** Chuẩn hóa file `.gitignore` (loại bỏ dòng `/build.gradle` bị nhầm lẫn), bổ sung quy tắc bỏ qua các thư mục sinh ra khi build (`.gradle/`, `build/`, `dist/`, `run/`, `bin/`, `out/`, `.vscode/`, `.idea/`).
- **[Xóa bỏ]** Dọn dẹp thư mục dư thừa `bin/` ra khỏi dự án.

---

### [v1.4.0] - 2026-07-25

- **[Cập nhật]** Tối ưu hiệu năng tầng sâu (round 2) — zero-overhead trên hot path:
  - **`InventoryHelper`**: Thay `Field.get()` bằng **`MethodHandle.invoke()`** — sau JIT warm-up biên dịch thành direct field load (~1 ns, không còn reflection overhead); `filterSameSide` loop tách thành 2 nhánh riêng để loại bỏ conditional check mỗi iteration; `syncId` đọc 1 lần trước loop thay vì N lần trong `clickSlot`.
  - **`ComboKeyHandler`**: Kiểm tra modifier **trước** khi query key state — khi modifier không khớp, bỏ qua 1 GLFW JNI call; Java `||` short-circuit đảm bảo RIGHT_* modifier chỉ query khi LEFT_* = false.
  - **`ScreenMouseHandler`**: Fast-exit ngay khi cả 2 combo đều bind bàn phím; kiểm tra button code **trước** khi đọc modifier state — tránh 3 `Screen.has*Down()` với click không liên quan; modifier được đọc 1 lần dùng chung cho cả 2 check.
  - **`ModConfig`**: `getComboDisplayString()` trả thẳng `keyName` khi không có modifier — không tạo `StringBuilder`; thêm F1-F12 vào switch table (O(1) jump table); `volatile INSTANCE` đảm bảo safe publication; `disableHtmlEscaping()` trên GSON; `Integer.toString(-code)` thay string concatenation.
  - **`ThrowAllMoveAllMod`**: Method reference `ComboKeyHandler::checkInput` thay lambda wrapper — loại bỏ anonymous class allocation lúc đăng ký.

---

### [v1.3.0] - 2026-07-25


- **[Sửa lỗi]** Sửa triệt để lỗi `ALT + LEFT_CLICK` không hoạt động:
  - Thay thế raw GLFW callback (`MouseComboHandler`) bằng **Fabric `ScreenMouseEvents.allowMouseClick`** — chuẩn xác theo cách Item Scroller và các mod tương tự sử dụng.
  - `allowMouseClick` fires TRƯỚC khi `HandledScreen.mouseClicked()` xử lý, dùng `Screen.hasAltDown()` để đọc ALT state chuẩn xác 100%.
  - Trả về `false` để cancel original click, tránh Minecraft double-act lên slot.
  - Thêm lớp `ScreenMouseHandler.java` (Fabric `ScreenEvents.BEFORE_INIT`).
  - Xóa `MouseComboHandler.java` (GLFW callback — sai timing).
- **[Thêm mới]** Nút **RESET tự động mờ (dimmed)** khi binding đang ở giá trị mặc định — không thể nhấn khi không cần thiết (theo chuẩn Item Scroller).
- **[Cập nhật]** Đồng bộ version `v1.3.0` trên `pom.xml`, `gradle.properties`, `fabric.mod.json`.
- **[Cập nhật]** Tối ưu hiệu năng toàn diện Client-side:
  - **`ComboKeyHandler`**: Early-exit trước tất cả GLFW calls khi không ở màn hình inventory; skip toàn bộ khi cả 2 combo đều bind chuột; đọc modifier state 1 lần dùng cho cả 2 combo; truyền `MinecraftClient` thay vì gọi `getInstance()`.
  - **`InventoryHelper`**: Nhận `MinecraftClient` qua tham số (không dùng volatile static read); slot loop dùng index thay iterator (nhanh hơn); `canTakeItems()` chỉ gọi 1 lần/slot; logic loop MoveAll/ThrowAll hợp nhất thành 1 helper `executeOnMatchingSlots()`; cache `fieldSearchFailed` để không retry reflection khi field không tìm thấy.
  - **`ScreenMouseHandler`**: Bỏ guard `instanceof HandledScreen` thừa; dùng phép tính số học thay switch table; `Screen.has*Down()` đọc boolean đã cache (không gọi GLFW); return sớm thay biến `consumed`.
  - **`ModConfig`**: I/O dùng NIO `Files.readString/writeString` (ít syscall hơn, không cần đóng stream thủ công); error log qua SLF4J thay `printStackTrace()`; `StringBuilder` được pre-size; kiểm tra constant trước khi gọi GLFW JNI.
  - **`ModConfigScreen`**: State `throwResetActive/moveResetActive` được cache trong `refreshLabels()` (chạy khi config thay đổi), không tính lại trong `render()` ở 60 fps.

---

### [v1.2.0] - 2026-07-24




- **[Thêm mới]** Nâng cấp hệ thống gán phím tổ hợp chuẩn **Item Scroller**: Hiển thị nút đơn biểu diễn toàn bộ chuỗi phím (VD: `LEFT_ALT + Q`, `LEFT_SHIFT + LEFT_CLICK`, `BUTTON_3`...).
- **[Thêm mới]** Hỗ trợ gán phím bằng các nút click chuột (`LEFT_CLICK`, `RIGHT_CLICK`, `MIDDLE_CLICK`, `BUTTON_4`...).
- **[Cập nhật]** Tăng phiên bản chính thức lên `v1.2.0` trên `pom.xml`, `gradle.properties` và `fabric.mod.json`.

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
