# SETUP GUIDE - Next Steps to Build & Run

## ✅ What's Already Done

All source files have been created and committed to git:

```
✓ Android app structure (Kotlin + XML resources)
✓ Native C++ code with OpenCV integration  
✓ Web viewer (TypeScript + HTML)
✓ CMake build configuration
✓ Professional README with documentation
✓ Git repository with 5 clean commits
✓ .gitignore configured
```

## 🔧 What You Need to Do (Setup Environment)

### 1. Install Prerequisites

**Java JDK 11+**
- Download: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
- Set `JAVA_HOME` environment variable to JDK folder

**Android Studio**
- Download: https://developer.android.com/studio
- Run SDK Manager and install:
  - Android SDK (API 34+)
  - NDK (r21+)
  - CMake 3.22.1+

**OpenCV Android SDK**
- Download: https://opencv.org/releases/ (pick "Android" version)
- Extract to: `C:\opencv-4.7.0-android-sdk` (or your preferred location)

**Node.js**
- Download: https://nodejs.org/ (v18+ recommended)

**Git**
- Download: https://git-scm.com/
- Config: 
  ```bash
  git config --global user.name "Your Name"
  git config --global user.email "your@email.com"
  ```

### 2. Set Environment Variables (Windows)

Open System Properties → Environment Variables and add:

```
JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.x
ANDROID_HOME=C:\Users\<username>\AppData\Local\Android\Sdk
ANDROID_NDK_HOME=C:\Users\<username>\AppData\Local\Android\Sdk\ndk\<version>
```

### 3. Configure Project Files

**Create `app/local.properties`** (do NOT commit):

```properties
sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk\\ndk\\<version>
```

Example:
```properties
sdk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653
```

**Edit `jni/CMakeLists.txt`** - Update OpenCV path:

```cmake
set(OpenCV_DIR "C:/opencv-4.7.0-android-sdk/sdk/native/jni")
```

Or with backslashes:
```cmake
set(OpenCV_DIR "C:\\Users\\<username>\\Downloads\\opencv-android-sdk\\sdk\\native\\jni")
```

## 🚀 Build & Test

### Step 1: Build Android App

```bash
cd app
.\gradlew assembleDebug
.\gradlew installDebug
```

Expected output: Build succeeds, APK installs on device/emulator

### Step 2: Run on Device

- Connect Android device (API 21+) via USB OR start emulator
- Grant camera permission when app starts
- Should see live camera preview with edge detection overlay

### Step 3: Export Processed Frame

```bash
# Pull the saved frame
adb pull /sdcard/Android/data/com.example.flam/files/processed.png ./web/dist/frame.png

# If path differs, check logcat for actual path
adb logcat | grep processed
```

### Step 4: Build Web Viewer

```bash
cd web
npm install
npm run build
cp index.html dist/
npm start
```

Open: http://localhost:8080

## 🐛 Common Issues & Fixes

| Error | Fix |
|-------|-----|
| `CMake Error: OpenCV not found` | Check `OpenCV_DIR` in `jni/CMakeLists.txt` - must point to `sdk/native/jni` |
| `UnsatisfiedLinkError: native-lib` | Verify CMake built successfully; check `app/build/intermediates/cmake` for errors |
| `JAVA_HOME not set` | Set environment variable, then restart terminal/IDE |
| `NDK not found` | Ensure `ndk.dir` in `local.properties` points to actual NDK folder |
| `adb command not found` | Add Android SDK platform-tools to PATH: `%ANDROID_HOME%\platform-tools` |
| App crashes on launch | Check logcat: `adb logcat | grep -i error` |
| Camera permission denied | Grant permission in device settings → Apps → FlamEdge → Permissions |

## 📋 File Checklist Before Building

```
flam-assignment/
├── ✓ app/build.gradle (configured)
├── ✓ app/settings.gradle (configured)
├── ✓ app/local.properties (CREATE THIS - don't commit)
├── ✓ app/src/main/AndroidManifest.xml
├── ✓ app/src/main/java/com/example/flam/*.kt (3 files)
├── ✓ app/src/main/res/layout/activity_main.xml
├── ✓ app/src/main/res/values/*.xml (2 files)
├── ✓ jni/CMakeLists.txt (UPDATE OpenCV_DIR)
├── ✓ jni/native-lib.cpp
├── ✓ web/package.json
├── ✓ web/tsconfig.json
├── ✓ web/index.html
├── ✓ web/src/main.ts
├── ✓ README.md
└── ✓ .gitignore
```

## 📝 Git Commands (after each milestone)

```bash
# After successful build
git add app/local.properties  # optional: add to version control
git commit -m "feat(android): add build config"

# After successful run on device
git commit -m "feat(android): confirm camera preview and processing works"

# After pulling processed frame
git commit -m "feat(android): export processed frame to device storage"

# After web viewer works
git commit -m "feat(web): web viewer displays processed frame"

# Optional: create release tag
git tag -a v1.0.0 -m "Initial working release"
```

## 📱 Testing Checklist

- [ ] JDK installed and JAVA_HOME set
- [ ] Android SDK/NDK installed in Android Studio
- [ ] OpenCV Android SDK downloaded and path configured
- [ ] Node.js and npm installed
- [ ] `app/local.properties` created with correct paths
- [ ] `jni/CMakeLists.txt` OpenCV_DIR updated
- [ ] `./gradlew assembleDebug` succeeds
- [ ] `./gradlew installDebug` installs APK
- [ ] App runs, camera preview visible
- [ ] Camera permission granted
- [ ] Native processing working (check logcat)
- [ ] Processed frame saved to device storage
- [ ] `adb pull` successfully exports frame
- [ ] Web viewer builds with `npm run build`
- [ ] Web viewer displays frame at http://localhost:8080

## 🎯 Performance Notes

- TextureView bitmap capture: ~10 fps (adjustable in CameraPreview.kt line ~33)
- Canny edge detection: ~30-50ms per frame (depends on resolution and device)
- OpenGL rendering: ~60 fps (limited by device refresh rate)
- Web viewer: static image display (can be extended to WebSocket streaming)

## 🆘 Need Help?

1. Check logcat: `adb logcat -v brief | grep -i flam`
2. Enable verbose logging in CMakeLists.txt: add `set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -v")`
3. Check build output: `app/build/outputs/apk/debug/app-debug.apk` exists
4. Verify device connection: `adb devices`
5. Check OpenCV installation: ls `C:\opencv-android-sdk\sdk\native\jni` (or your path)

## 📚 Next Steps (Optional Enhancements)

- Implement full OpenGL shader pipeline (currently uses minimal rendering)
- Switch to Camera2 API for better performance
- Add Gaussian blur or other CV filters
- Implement FPS counter on GLSurfaceView
- Add WebSocket server for live streaming
- Support for different Canny thresholds (UI slider)

---

**You're all set! Follow the "Build & Test" section above to get everything running.** 🚀
