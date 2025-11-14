# Flam - Real-Time Edge Detection App

**Android + OpenCV + JNI + Web Viewer**

This is my Software Engineering Intern (R&D) assignment. I built an Android app that captures camera frames in real-time, processes them using OpenCV's Canny edge detection via JNI, renders the output using OpenGL ES, and displays results on both the mobile app and a web viewer.

---

## What I Built

### Android App Features
- Real-time camera capture using TextureView
- Frame processing at ~10 fps
- Proper camera permission handling
- Smooth frame rendering without blocking UI

### Native C++ Processing
- JNI bridge connecting Java and C++
- OpenCV integration for image processing
- Canny edge detection (thresholds: 80, 160)
- RGBA to grayscale conversion
- Memory management and error handling

### Graphics Rendering
- OpenGL ES 2.0 for GPU acceleration
- Efficient texture management
- Smooth display on GLSurfaceView

### Web Component
- TypeScript-based frame viewer
- Displays processed frames from the app
- Shows image resolution
- Simple HTTP server for testing

---

## How It Works

The data flows through these components:

```
Camera
   ↓
TextureView (captures frames)
   ↓
CameraPreview.kt (captures bitmap every 100ms)
   ↓
MainActivity.kt (converts to RGBA bytes)
   ↓
JNI Bridge (NativeLib.processFrame)
   ↓
C++ Code (native-lib.cpp)
   - Convert RGBA to grayscale
   - Apply Canny edge detection
   - Convert back to RGBA
   ↓
GLRenderer.kt (uploads to GPU texture)
   ↓
GLSurfaceView (displays on screen)
   ↓
Saves frame to device
   ↓
Web Viewer (displays result)
```

### Why This Architecture?

- **TextureView**: Efficient camera preview that doesn't block the main thread
- **JNI**: Allows high-performance C++ code for image processing
- **OpenCV**: Industry standard for computer vision tasks
- **OpenGL**: GPU acceleration for smooth rendering
- **Web Viewer**: Easy way to view and share results

---

## Setup Instructions

### What You Need to Install

1. **Java JDK 11+**
   - Download: https://adoptium.net/
   - Set `JAVA_HOME` environment variable

2. **Android Studio**
   - Download: https://developer.android.com/studio
   - Use SDK Manager to install Android SDK, NDK, and CMake

3. **OpenCV Android SDK**
   - Download: https://opencv.org/releases/ (choose Android version)
   - Extract to a folder like `C:\opencv-android-sdk`

4. **Node.js** (for web viewer)
   - Download: https://nodejs.org/
   - Version 18+ recommended

5. **Git**
   - Download: https://git-scm.com/

### Configuration

1. **Set environment variables** (Windows):
   ```
   JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-11.0.x
   ANDROID_HOME = C:\Users\<your-username>\AppData\Local\Android\Sdk
   ANDROID_NDK_HOME = C:\Users\<your-username>\AppData\Local\Android\Sdk\ndk\<version>
   ```

2. **Create `app/local.properties`**:
   ```properties
   sdk.dir=C:\Users\<your-username>\AppData\Local\Android\Sdk
   ndk.dir=C:\Users\<your-username>\AppData\Local\Android\Sdk\ndk\<version>
   ```

3. **Update OpenCV path in `jni/CMakeLists.txt`**:
   ```cmake
   set(OpenCV_DIR "C:/path/to/opencv-android-sdk/sdk/native/jni")
   ```

### Build and Run

```bash
# Build the app
cd app
./gradlew assembleDebug

# Install on emulator or device
./gradlew installDebug
```

Grant camera permission when prompted. You should see live camera feed with edge detection!

### Run Web Viewer

```bash
cd web
npm install
npm run build
npm start
# Open http://localhost:8080
```

---

## Git Commit History

I made meaningful commits throughout development:

1. `chore: init repo and add .gitignore` - Initial setup
2. `feat(android): scaffold Android app with TextureView and GLSurfaceView` - Android structure
3. `feat(native): add CMakeLists.txt and C++ OpenCV Canny pipeline` - Native code
4. `feat(web): add TypeScript viewer for displaying frames` - Web component
5. Plus documentation commits

Each commit represents working code, not a single final upload.

---

## Screenshots

Here is a screenshot of the app running on my emulator/device. The image shows the camera preview with Canny edge detection applied.

![App - Edge detection](docs/screenshots/img1.png)

_Description: Camera preview with Canny edges visible (approx 10 fps)._ 

---

## Troubleshooting

**CMake Error: OpenCV not found**
- Verify `OpenCV_DIR` in `jni/CMakeLists.txt` points to the correct path
- Check that `sdk/native/jni` folder exists

**UnsatisfiedLinkError: native-lib**
- Ensure `System.loadLibrary("native-lib")` is called
- Verify native library built successfully

**App crashes on startup**
- Check Android logcat for errors
- Grant camera permission when prompted

**Native build failing**
- Verify NDK path in `local.properties`
- Check OpenCV path in CMakeLists.txt

---

## What I Learned

Building this project taught me:
- Android app development and lifecycle
- JNI programming for Java-C++ communication
- CMake and NDK for native compilation
- OpenCV for computer vision
- OpenGL for GPU-accelerated graphics
- Structuring complex multi-language projects
- Git workflow and meaningful commits

---

## Project Structure

```
flam-assignment/
├── app/                          # Android app
│   ├── build.gradle
│   ├── local.properties          # (create this)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/flam/
│       │   ├── MainActivity.kt
│       │   ├── CameraPreview.kt
│       │   └── GLRenderer.kt
│       └── res/
│           ├── layout/
│           └── values/
├── jni/                          # Native C++ code
│   ├── CMakeLists.txt
│   └── native-lib.cpp
├── web/                          # Web viewer
│   ├── src/main.ts
│   ├── index.html
│   └── package.json
└── README.md                      # This file
```

---

## Repository

GitHub: https://github.com/Shruti200330/flam-assignment

6. **Git**
   - Install from [git-scm.com](https://git-scm.com/)
   - Configure: `git config --global user.name "Your Name"` and `git config --global user.email "your@email.com"`

## Setup & Build

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

## Git Commit Checklist

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

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `CMake Error: OpenCV not found` | Verify `OpenCV_DIR` in `jni/CMakeLists.txt` points to correct path; check `sdk/native/jni` exists |
| `UnsatisfiedLinkError: native-lib` | Ensure `System.loadLibrary("native-lib")` matches CMake library name; check build output for compilation errors |
| App crashes on camera access | Grant camera permission in app settings; check Android version ≥ 21 |
| No texture visible in GLSurfaceView | Frame may be processing but not rendering; check logcat for GL errors |
| Bitmap colors garbled | May be RGBA/BGRA mismatch; try `COLOR_BGRA2GRAY` in `native-lib.cpp` line with `cvtColor` |
| Web viewer shows broken image | Ensure `frame.png` exists in `web/dist/`; verify file path with `adb pull` |

## Testing the Pipeline

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

## Key Code Files

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

## Optional Enhancements

- [ ] Add Camera2 API for better performance
- [ ] Implement toggle button to switch raw/processed view
- [ ] Add FPS counter overlay on GLSurfaceView
- [ ] Implement WebSocket streaming for live web viewer
- [ ] Add Gaussian blur or other CV filters
- [ ] Reduce latency with ImageReader instead of TextureView.getBitmap()

## References

- [Android Camera Guide](https://developer.android.com/guide/topics/media/camera)
- [JNI Documentation](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/)
- [OpenCV Android](https://docs.opencv.org/master/db/df8/tutorial_root.html)
- [OpenGL ES on Android](https://developer.android.com/guide/topics/graphics/opengl)

## License

MIT — use freely for learning and development.

---

**Built for real-time edge detection on Android.**
