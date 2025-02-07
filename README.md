# Android WebView Content Player

A native Android application that implements a WebView-based slideshow player with C/C++ native integration, built using modern Android development tools including Jetpack Compose for the UI layer.

## 🎥 Proof of Work

### Video Demonstration
[Show testing video]

## 🌟 Features

### WebView Integration
- Full-screen WebView implementation
- Custom JavaScript bridge interface (SC_INTERFACE)
- Screenshot capture functionality
- Device information retrieval (Kotlin & C++)
- Smooth content playback optimization

### Native Features (C++)
- RAM information retrieval
- CPU cores and frequency detection
- Kernel version information
- Build fingerprint access
- Hardware serial number retrieval

### UI Components (Jetpack Compose)
- Full-screen content display
- Floating action buttons for screenshots and device info
- Dialog display for device information
- Screenshot preview functionality
- Modern Material Design implementation

### JavaScript Bridge Interface
```kotlin
interface SC_INTERFACE {
    @JavascriptInterface
    fun device_info(): String  // Returns device info in JSON format
    
    @JavascriptInterface
    fun take_screenshot(callback: String)  // Captures WebView content
    
    @JavascriptInterface
    fun get_name(): String  // Returns developer name
}
```

### Device Info JSON Structure
```json
{
    "app_version": "1.0",
    "package_name": "io.screencloud.assignment.android_sen",
    "screen_width": 2560,
    "screen_height": 1688,
    "screen_density": 2,
    "android_version": 30,
    "device_manufacturer": "Genymobile",
    "device_model": "Pixel C",
    "native_info": {
        "ram_total": "3148120064",
        "cpu_info": {
            "cpu_cores": "3",
            "cpu_freq": "3072.000"
        },
        "kernel_version": "11",
        "build_fingerprint": "google/vbox86p/vbox86p:11/RQ",
        "hardware_serial": "vbox86"
    }
}
```

## 🛠 Tech Stack

### Android
- Kotlin
- Jetpack Compose for UI
- Android SDK
- WebView with JavaScript Interface
- Android Asset Manager

### Native
- C/C++ for system information retrieval
- CMake build system

### Web
- HTML/CSS/JavaScript
- Custom JavaScript interface handling

## 🚀 Quick Start

1. **Prerequisites**
   ```bash
   # Required
   - Android Studio
   - JDK 8 or higher
   - Android SDK (minimum API level recommended: 21)
   - Android NDK
   - CMake 3.10.2 or higher
   ```

2. **Project Setup**
   ```bash
   # Clone the repository
   git clone https://github.com/nyinyiz/WebViewContentPlayer.git

   # Open in Android Studio
   File -> Open -> Select project directory

   # Sync NDK in Android Studio
   File -> Settings -> Android SDK -> SDK Tools -> NDK
   ```

## 📁 Project Structure

```
project-root/
├── app/
│   └── src/
│       ├── main/
│       │   ├── assets/
│       │   │   └── slideshow.html    # Slideshow content
│       │   ├── cpp/                  # Native C/C++ source files
│       │   │   ├── native-lib.cpp
│       │   ├── java/
│       │   │   └── com/nyinyi/assignment/webviewcontentplayer/   # Kotlin source files
│       │   └── res/                  # Android resources
│       └── androidTest/              # Instrumented tests
├── gradle/
└── build.gradle
```

## 💻 Development

### Native Development

The project includes C/C++ components for native functionality:

1. Native code is located in `app/src/main/cpp/`
2. CMake configuration in `app/CMakeLists.txt`
3. JNI implementations in `native-lib.cpp`

### UI Development

The UI is built using Jetpack Compose:

- Modern declarative UI patterns
- Material Design components
- Responsive layouts
- Custom composables for slideshow controls

### WebView Content

The slideshow content is located in `app/src/main/assets/slideshow.html`. To modify the slideshow:

1. Navigate to `app/src/main/assets/`
2. Edit `slideshow.html` to update content
3. Follow HTML/CSS best practices for WebView optimization

## 🔧 Configuration

### Development Requirements

- Android Studio Arctic Fox or later
- Gradle 7.0 or later
- Android SDK with minimum API level 21
- Android NDK 21.0 or later
- CMake 3.10.2 or later

### Building Native Libraries

```bash
# Build native libraries
./gradlew externalNativeBuildDebug
./gradlew externalNativeBuildRelease
```

### Building the Project

1. Open project in Android Studio
2. Sync Gradle files
3. Build native libraries
4. Build project (Build -> Make Project)

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run native tests
./gradlew ctest
```

## 📦 Building for Production

```bash
# Generate debug APK
./gradlew assembleDebug

# Generate release APK
./gradlew assembleRelease
```

## 🔄 Version History

- 1.0.0
  - Full WebView implementation
  - Native C++ system information integration
  - Screenshot functionality
  - Device information display
  - Compose UI implementation

---

Made with ❤️ by Nyi