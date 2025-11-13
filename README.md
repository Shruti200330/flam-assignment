# Flam — Real-Time Edge Detection Viewer

**Android + OpenCV C++ + OpenGL ES + Web**

A minimal but fully functional pipeline demonstrating real-time Canny edge detection on Android using camera input, native C++ processing via OpenCV, and OpenGL ES rendering with a TypeScript web viewer.

## 🏗️ Architecture

```
Camera (TextureView)
    ↓
MainActivity (Kotlin)
    ↓
CameraPreview (capture frames to bitmap)
    ↓
NativeLib.processFrame (JNI → C++)
    ↓
native-lib.cpp (OpenCV Canny edge detection)
    ↓
Return RGBA bytes
    ↓
GLRenderer (upload to OpenGL texture)
    ↓
GLSurfaceView (display rendered frame)
    ↓
Save to device storage
    ↓
Web viewer (TypeScript + HTML)
```

## ✨ Features Implemented

- ✅ Android camera capture using TextureView (Camera1 API)
- ✅ JNI bridge to native C++ code
- ✅ OpenCV Canny edge detection (threshold: 80, 160)
- ✅ GLSurfaceView for texture rendering
- ✅ Frame export to device storage (PNG format)
- ✅ TypeScript web viewer with HTML interface

## 📦 Project Structure

```
flam-assignment/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/flam/
│   │   │   ├── MainActivity.kt
│   │   │   ├── CameraPreview.kt
│   │   │   ├── GLRenderer.kt
│   │   ├── res/
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/strings.xml
│   │   │   ├── values/styles.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   ├── settings.gradle
│   └── local.properties (create locally)
├── jni/
│   ├── CMakeLists.txt
│   └── native-lib.cpp
├── web/
│   ├── src/main.ts
│   ├── index.html
│   ├── package.json
│   ├── tsconfig.json
│   └── dist/ (generated after build)
├── .gitignore
└── README.md (this file)
```

## 🔧 Prerequisites

### Required (Windows example — adapt for Linux/Mac)

1. **Java JDK 11+**
   - Download from [Temurin](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
   - Set `JAVA_HOME` environment variable to JDK installation path

2. **Android SDK & NDK**
   - Install Android Studio
   - Open Android Studio → SDK Manager
   - Install:
     - Android SDK (API 34)
     - NDK (r21 or later)
     - CMake 3.22.1 or later

3. **OpenCV Android SDK**
   - Download from [opencv.org](https://opencv.org/releases/)
   - Extract to a known location (e.g., `C:\opencv-4.7.0-android-sdk`)

4. **Environment Variables** (set on your system)
   ```
   JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.x
   ANDROID_HOME=C:\Users\<username>\AppData\Local\Android\Sdk
   ANDROID_NDK_HOME=C:\Users\<username>\AppData\Local\Android\Sdk\ndk\<version>
   ```

5. **Node.js & npm**
   - Install from [nodejs.org](https://nodejs.org/) (v18+ recommended)

6. **Git**
   - Install from [git-scm.com](https://git-scm.com/)
   - Configure: `git config --global user.name "Your Name"` and `git config --global user.email "your@email.com"`

## 🚀 Setup & Build

### Step 1: Initialize Repository

```bash
cd flam-assignment
git init
git add .gitignore
git commit -m "chore: init repo and add .gitignore"
```

### Step 2: Configure Android Build

Create `app/local.properties` (do NOT commit this file):

```properties
sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk\\ndk\\<version>
```

**Windows path example:**
```properties
sdk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653
```

### Step 3: Configure OpenCV Path

Edit `jni/CMakeLists.txt` and replace the `OpenCV_DIR` line:

```cmake
# Windows example:
set(OpenCV_DIR "C:/opencv-4.7.0-android-sdk/sdk/native/jni")

# Or with escaped backslashes:
set(OpenCV_DIR "C:\\path\\to\\opencv-android-sdk\\sdk\\native\\jni")
```

### Step 4: Build Android App

From the project root:

```bash
# Windows
cd app
.\gradlew assembleDebug
.\gradlew installDebug

# Linux/Mac
cd app
./gradlew assembleDebug
./gradlew installDebug
```

### Step 5: Run on Device/Emulator

- Connect an Android device (API 21+) via USB, OR
- Start an emulator from Android Studio
- Grant camera permission when prompted
- You should see camera preview with edge-detected overlay

### Step 6: Export Processed Frame

```bash
# Pull the processed frame from device
adb pull /sdcard/Android/data/com.example.flam/files/processed.png ./web/dist/frame.png
```

### Step 7: Build & Run Web Viewer

```bash
cd web
npm install
npm run build
cp index.html dist/index.html
# Ensure frame.png is in dist/
npm start
# Open http://localhost:8080
```

## 📝 Git Commit Checklist

Make clean, incremental commits:

```bash
# 1. Initial scaffolding
git add app jni
git commit -m "feat(android): scaffold Android app, TextureView, GLSurfaceView, and JNI"

# 2. Native code
git add jni/
git commit -m "feat(native): add CMakeLists and C++ OpenCV Canny pipeline"

# 3. After successful build
git add -A
git commit -m "feat(android): add build config and confirm camera preview"

# 4. Web viewer
git add web/
git commit -m "feat(web): add TypeScript viewer for processed frames"

# 5. Documentation
git add README.md
git commit -m "docs: add setup and architecture documentation"

# 6. Optional: final cleanup
git add -A
git commit -m "chore: final cleanup and minor improvements"
```

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| `CMake Error: OpenCV not found` | Verify `OpenCV_DIR` in `jni/CMakeLists.txt` points to correct path; check `sdk/native/jni` exists |
| `UnsatisfiedLinkError: native-lib` | Ensure `System.loadLibrary("native-lib")` matches CMake library name; check build output for compilation errors |
| App crashes on camera access | Grant camera permission in app settings; check Android version ≥ 21 |
| No texture visible in GLSurfaceView | Frame may be processing but not rendering; check logcat for GL errors |
| Bitmap colors garbled | May be RGBA/BGRA mismatch; try `COLOR_BGRA2GRAY` in `native-lib.cpp` line with `cvtColor` |
| Web viewer shows broken image | Ensure `frame.png` exists in `web/dist/`; verify file path with `adb pull` |

## 📱 Testing the Pipeline

1. **Verify camera works:**
   - App should show live camera feed in TextureView
   - Check logcat: `adb logcat | grep native-lib`

2. **Verify native processing:**
   - Look for log: `"Processing frame: X x Y"`
   - Check frame is being processed (may see white edges on black background)

3. **Verify GL rendering:**
   - Processed frames should appear in GLSurfaceView overlay
   - Log output: `"Surface created"` and `"Frame uploaded"`

4. **Verify file export:**
   - Check device storage: `adb shell ls /sdcard/Android/data/com.example.flam/files/`
   - Pull file: `adb pull /sdcard/Android/data/com.example.flam/files/processed.png`

5. **Test web viewer:**
   - Open http://localhost:8080
   - Should display processed frame with stats (resolution, status)

## 📚 Key Code Files

### MainActivity.kt
Handles lifecycle, camera permissions, and frame processing pipeline.

### CameraPreview.kt
Manages Camera1 API, TextureView surface, and periodic bitmap capture (~10 fps).

### GLRenderer.kt
OpenGL ES 2.0 renderer; uploads RGBA bytes to texture and clears screen.

### native-lib.cpp
JNI entry point; converts RGBA → grayscale → Canny → RGBA using OpenCV.

### web/src/main.ts
Displays processed PNG frame and updates stats.

## 🎯 Optional Enhancements

- [ ] Add Camera2 API for better performance
- [ ] Implement toggle button to switch raw/processed view
- [ ] Add FPS counter overlay on GLSurfaceView
- [ ] Implement WebSocket streaming for live web viewer
- [ ] Add Gaussian blur or other CV filters
- [ ] Reduce latency with ImageReader instead of TextureView.getBitmap()

## 🔗 References

- [Android Camera Guide](https://developer.android.com/guide/topics/media/camera)
- [JNI Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/)
- [OpenCV Android](https://docs.opencv.org/master/db/df8/tutorial_root.html)
- [OpenGL ES on Android](https://developer.android.com/guide/topics/graphics/opengl)

## 📄 License

MIT — use freely for learning and development.

---

**Built with ❤️ for real-time edge detection on Android.**
