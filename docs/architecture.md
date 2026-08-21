# Kiến trúc Hệ thống Mod ThrowAll & MoveAll (Minecraft 1.19 → 1.21.11)

Tài liệu này mô tả chi tiết thiết kế kiến trúc, cấu trúc thành phần, luồng xử lý dữ liệu và các sơ đồ kỹ thuật cho dự án ThrowAll & MoveAll Mod theo mô hình **Multi-Project Gradle**.

---

## 1. Tổng quan hệ thống (System Overview)
Mod được thiết kế là một **Client-side Mod** đa phiên bản dành cho Fabric Loader trên Minecraft từ 1.19 đến 1.21.11 (18 phiên bản chính thức). Mod xử lý các gói tin tương tác kho đồ trực tiếp tại client thông qua `ClientPlayerInteractionManager` nhằm giúp người chơi di chuyển (`MoveAll`) hoặc vứt (`ThrowAll`) toàn bộ vật phẩm trong kho một cách nhanh chóng. Hỗ trợ hệ thống **Config JSON ngoài** (`.minecraft/config/throwallmoveall.json`), phím tắt tổ hợp **Combo Keys** (`Alt + Key`, `Ctrl + Shift + Key`...) và tích hợp giao diện **ModMenu Config GUI**.

---

## 2. Công nghệ sử dụng (Tech Stack)
- **Ngôn ngữ lập trình:** Java 17 (Era 1: 1.19-1.20.4), Java 21 (Era 2: 1.20.5-1.21.11).
- **Build System:** Gradle Multi-Project (`settings.gradle` include `common` & 18 subproject `:versions:<ver>`).
- **Modding Framework:** Fabric Loader & Fabric API tương ứng từng phiên bản Minecraft.
- **Mapping:** Fabric Yarn Mappings (1.19 → 1.21.11).
- **Thư viện đồ họa & Input:** Lightweight Java Game Library (LWJGL3 / GLFW).
- **Cấu hình & Dữ liệu:** Google Gson (Tệp cấu hình JSON).
- **Tích hợp:** Mod Menu API.

---

## 3. Cấu trúc thư mục (Folder Structure)
```
throwallmoveall/
├── settings.gradle                           # Khai báo bao gồm 18 subproject versions
├── build.gradle                              # File cấu hình tổng (Root Task buildAll & collectJars)
├── README.md                                 # Hướng dẫn sử dụng & cài đặt bằng Tiếng Việt
├── dist/                                     # Thư mục tổng hợp các file .jar đầu ra (18 files)
├── docs/
│   ├── architecture.md                       # Tài liệu kiến trúc hệ thống
│   └── CHANGELOG.md                          # Nhật ký thay đổi phiên bản
├── common/                                   # Mã nguồn & tài nguyên chung (1.19 → 1.21.8)
│   └── src/main/
│       ├── java/com/example/throwallmoveall/ # Logic core nguyên bản
│       └── resources/assets/                 # Assets ngôn ngữ và icon
└── versions/                                 # Subprojects cấu hình riêng cho từng MC version
    ├── 1.19/ .. 1.21.8/                      # Kế thừa common/src/main/java
    └── 1.21.9/ .. 1.21.11/                   # Có thư mục src/main/java riêng (API Click/KeyInput mới)
```

---

## 4. Kiến trúc thành phần (Component Architecture)
- **Common Module (`common/`):** Chứa toàn bộ core business logic không phụ thuộc phiên bản (`InventoryHelper`, `ComboKeyHandler`, `ScreenMouseHandler`, `ModConfig`, `KeyBindings`).
- **Client EntryPoint Layer (`ThrowAllMoveAllMod`):** Khởi tạo tệp cấu hình JSON ngoài và đăng ký sự kiện `ClientTickEvents.END_CLIENT_TICK`.
- **Config Management Layer (`ModConfig`):** Đọc/ghi cài đặt phím tắt tổ hợp Combo tại `.minecraft/config/throwallmoveall.json`.
- **Combo Key Handler Layer (`ComboKeyHandler` & `ScreenMouseHandler`):** Đọc trạng thái GLFW phím chính và các phím Modifier (`Alt`, `Ctrl`, `Shift`) ở mức thấp.
- **Config GUI Layer (`ModConfigScreen` & `ModMenuIntegration`):** Cung cấp giao diện bấm nút tùy chỉnh phím tắt In-Game. Bản legacy (1.19.x) dùng `MatrixStack`, bản modern (1.20+) dùng `DrawContext`.
- **Inventory Handler Layer (`InventoryHelper`):** Truy vấn ô kho đồ đang được trỏ chuột bằng Reflection (có caching `MethodHandle`), áp bộ lọc an toàn và phát lệnh click slot qua `ClientPlayerInteractionManager`.

---

## 5. Luồng dữ liệu (Data Flow)
1. `ThrowAllMoveAllMod` nạp cài đặt từ `.minecraft/config/throwallmoveall.json` thông qua `ModConfig.load()`.
2. Trong mỗi Client Tick, `ComboKeyHandler` đọc trạng thái phím GLFW thấp và kiểm tra xem phím tổ hợp (VD: `Alt + Q` hoặc `Ctrl + Shift + V`) có được nhấn hay không.
3. Khi phím tổ hợp hợp lệ được bấm, `InventoryHelper` kiểm tra `client.currentScreen`:
   - Xác định `Slot` được trỏ chuột bằng Reflection (`MethodHandle` cached field).
   - Duyệt danh sách các `Slot` phù hợp trong kho đồ.
4. Gửi gói tin tương tác `clickSlot` với loại thao tác tương ứng (`QUICK_MOVE` hoặc `THROW`) tới Server.

---

## 6. Cơ chế bảo mật (Security Mechanisms)
- Tệp cấu hình JSON được lưu trữ an toàn trong thư mục chuẩn `config/` của Minecraft client.
- Bắt sự kiện bàn phím mức thấp nhưng tuân thủ nguyên tắc khóa phím khi không ở giao diện kho đồ thích hợp.

---

## 7. APIs / Routes cốt lõi (Core APIs/Routes)
- `ModConfig.load()` / `ModConfig.save()`: API quản lý tệp cấu hình JSON ngoài.
- `InputUtil.isKeyPressed(windowHandle, keyCode)`: API kiểm tra trạng thái phím GLFW.
- `ClientTickEvents.END_CLIENT_TICK.register(...)`: Vòng lặp lắng nghe client tick.
- `ModMenuApi.getModConfigScreenFactory()`: API đăng ký màn hình Cài đặt trong Mod Menu.

---

## 8. Sơ đồ trực quan (Visual Diagrams - Mermaid.js)

### Sơ đồ Luồng Kiến trúc Multi-Project (Flowchart)
```mermaid
graph TD
    Root["Root Project (build.gradle, settings.gradle)"] --> Common["common/ (Shared Core Logic & Assets)"]
    Root --> Sub1["versions/1.19 (Legacy Screen, Java 17)"]
    Root --> Sub2["versions/1.20.4 (Java 17, DrawContext)"]
    Root --> Sub3["versions/1.21.4 (Java 21, Loom 1.10)"]
    Root --> Sub4["versions/1.21.5 (Java 21, Fabric API)"]
    
    Sub1 --> Common
    Sub2 --> Common
    Sub3 --> Common
    Sub4 --> Common

    Sub1 --> Jar1["dist/throwallmoveall-1.5.1-mc1.19.jar"]
    Sub2 --> Jar2["dist/throwallmoveall-1.5.1-mc1.20.4.jar"]
    Sub3 --> Jar3["dist/throwallmoveall-1.5.1-mc1.21.4.jar"]
    Sub4 --> Jar4["dist/throwallmoveall-1.5.1-mc1.21.5.jar"]
```

### Sơ đồ Trình tự Thao tác Inventory (Sequence Diagram)
```mermaid
sequenceDiagram
    autonumber
    actor Player as Người chơi
    participant CK as ComboKeyHandler
    participant CFG as ModConfig (JSON)
    participant IH as InventoryHelper
    participant MC as Minecraft Client
    participant SVR as Minecraft Server

    Player->>CK: Nhấn tổ hợp phím (VD: Alt + Q)
    CK->>CFG: Đối chiếu cấu hình throwAllKey & Alt/Ctrl/Shift
    CFG-->>CK: Trả về trạng thái hợp lệ
    CK->>IH: Gọi executeThrowAll() / executeMoveAll()
    IH->>MC: Đọc slot trỏ chuột bằng MethodHandle (cached Field)
    loop Lặp qua từng Slot phù hợp
        IH->>MC: clickSlot(syncId, slotId, button, SlotActionType, player)
        MC->>SVR: Gửi C2SPacket (Player Action Inventory)
    end
    SVR-->>MC: Đồng bộ hóa trạng thái kho đồ
```

### Sơ đồ Mối quan hệ Thành phần (Component Relationship Diagram)
```mermaid
erDiagram
    ThrowAllMoveAllMod ||--|| ModConfig : "Nạp cấu hình JSON"
    ThrowAllMoveAllMod ||--|| ComboKeyHandler : "Lắng nghe phím Combo"
    ModMenuIntegration ||--|| ModConfigScreen : "Khởi tạo màn hình GUI"
    ModConfigScreen ||--|| ModConfig : "Lưu cài đặt phím"
    ComboKeyHandler ||--|| InventoryHelper : "Gọi Logic kho đồ"
    InventoryHelper ||--|| HandledScreen : "Đọc Slot trỏ chuột"
```
