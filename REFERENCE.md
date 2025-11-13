# QUICK REFERENCE - Key Code Files & Pipeline

## 📊 Data Flow

```
📱 Camera (Hardware)
    ↓ (TextureView)
🎥 CameraPreview.kt
    ↓ (bitmap capture, 100ms intervals)
🔄 MainActivity.kt (startCameraPreview)
    ↓ (convert to RGBA ByteArray)
🔗 NativeLib.processFrame() [JNI call]
    ↓
⚙️ native-lib.cpp (C++)
    ├─ Convert RGBA → cv::Mat
    ├─ cvtColor to grayscale
    ├─ Canny edge detection (80, 160)
    └─ cvtColor grayscale → RGBA
    ↓ (return ByteArray)
🎨 GLRenderer.kt
    ├─ Create Bitmap from bytes
    ├─ Upload to OpenGL texture
    └─ Render to GLSurfaceView
    ↓
💾 File export (MainActivity.kt)
    └─ Save to /sdcard/Android/data/.../processed.png
    ↓
🌐 Web Viewer (index.html)
    ├─ Display frame.png
    └─ Show resolution stats
```

## 🔑 Key Code Snippets

### MainActivity.kt - Frame Processing Loop

```kotlin
private fun startCameraPreview() {
    val preview = CameraPreview(this, textureView) { bitmap ->
        val w = bitmap.width
        val h = bitmap.height
        val buffer = java.nio.ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(buffer)
        val array = buffer.array()

        // JNI call to native C++ processing
        val processed = NativeLib.processFrame(array, w, h)
        if (processed != null) {
            glRenderer.updateFrame(processed, w, h)
            glSurface.requestRender()
        }
    }
    preview.start()
}
```

### CameraPreview.kt - Bitmap Capture

```kotlin
exec.scheduleAtFixedRate({
    if (textureView.isAvailable) {
        val bmp = textureView.getBitmap()
        if (bmp != null) {
            onBitmap(bmp)  // callback with bitmap
        }
    }
}, 0, 100, java.util.concurrent.TimeUnit.MILLISECONDS) // 10 fps
```

### GLRenderer.kt - Texture Upload

```kotlin
override fun onDrawFrame(gl: GL10?) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    pendingFrame?.let { bytes ->
        val bmp = Bitmap.createBitmap(frameW, frameH, Config.ARGB_8888)
        val buf = ByteBuffer.wrap(bytes)
        bmp.copyPixelsFromBuffer(buf)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
        bmp.recycle()
        pendingFrame = null
    }
}
```

### native-lib.cpp - OpenCV Processing

```cpp
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_flam_NativeLib_processFrame(JNIEnv *env, jclass clazz, 
                                               jbyteArray input, jint w, jint h) {
    jbyte* data = env->GetByteArrayElements(input, nullptr);
    
    // RGBA → Mat
    Mat img(h, w, CV_8UC4, (unsigned char*)data);
    
    // → Grayscale
    Mat gray;
    cvtColor(img, gray, COLOR_RGBA2GRAY);
    
    // → Edge detection
    Mat edges;
    Canny(gray, edges, 80, 160);
    
    // → RGBA output
    Mat out;
    cvtColor(edges, out, COLOR_GRAY2RGBA);
    
    // Return bytes
    int outLen = out.total() * out.elemSize();
    jbyteArray outArray = env->NewByteArray(outLen);
    env->SetByteArrayRegion(outArray, 0, outLen, 
                           reinterpret_cast<const jbyte*>(out.data));
    
    env->ReleaseByteArrayElements(input, data, 0);
    return outArray;
}
```

### CMakeLists.txt - Build Configuration

```cmake
cmake_minimum_required(VERSION 3.4.1)

set(OpenCV_DIR "C:/opencv-android/sdk/native/jni")

find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

add_library(native-lib SHARED native-lib.cpp)
target_link_libraries(native-lib ${OpenCV_LIBS} android log)
```

## 🎯 Tuning Parameters

### CameraPreview.kt - Frame Rate

```kotlin
// Line 33: Change 100ms to different interval
exec.scheduleAtFixedRate({ ... }, 0, 100, TimeUnit.MILLISECONDS)
// 50ms  → 20 fps
// 100ms → 10 fps (default)
// 200ms → 5 fps
```

### native-lib.cpp - Canny Thresholds

```cpp
// Line 38: Adjust edge detection sensitivity
Canny(gray, edges, 80, 160);
// Lower values (40, 120) → more edges detected, noisier
// Higher values (150, 300) → fewer edges, cleaner
```

### GLRenderer.kt - Clear Color

```kotlin
// Line 42: Change background color
GLES20.glClearColor(0f, 0f, 0f, 1f)  // black
// GLES20.glClearColor(1f, 1f, 1f, 1f) // white
// GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1f) // dark gray
```

## 🔧 Build System

### Gradle Compilation

```bash
# From app/ folder
./gradlew assembleDebug        # Build APK
./gradlew installDebug         # Install to device
./gradlew assembleRelease      # Build release APK
./gradlew clean                # Clean build artifacts
./gradlew --tasks              # List available tasks
```

### CMake Build (handled by Gradle)

```bash
# Manual build if needed
mkdir build
cd build
cmake -DCMAKE_SYSTEM_NAME=Android \
      -DCMAKE_SYSTEM_VERSION=21 \
      -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a \
      -DCMAKE_ANDROID_NDK="/path/to/ndk" ..
make
```

## 📱 ADB Commands

```bash
# Device management
adb devices                    # List connected devices
adb shell                      # Open device shell
adb logcat                     # View device logs

# File operations
adb push local/path /device/path
adb pull /device/path local/path
adb shell ls /path/            # List directory

# App control
adb install app-debug.apk
adb uninstall com.example.flam
adb shell am start -n com.example.flam/.MainActivity  # Launch app

# Logs
adb logcat -v brief            # Verbose output
adb logcat | grep native-lib   # Filter by tag
adb logcat -c                  # Clear logs
```

## 🌐 Web Viewer

### TypeScript Build

```bash
cd web
npm install                    # Install dependencies
npm run build                  # Compile TypeScript → JavaScript
npm start                      # Start http-server on port 8080
```

### HTML Structure

```html
<img id="frame" src="frame.png" alt="Processed frame" />
<div id="stats">FPS: -- | Resolution: --</div>
<script type="module" src="./main.js"></script>
```

### TypeScript Logic

```typescript
const img = document.getElementById('frame') as HTMLImageElement;

img.onload = () => {
  const w = img.naturalWidth;
  const h = img.naturalHeight;
  console.log(`Frame loaded: ${w}x${h}`);
};
```

## 🧪 Testing Checklist

```bash
# 1. Verify OpenCV
ls C:/opencv-4.7.0-android-sdk/sdk/native/jni/

# 2. Verify Android SDK/NDK
adb --version
${ANDROID_NDK_HOME}/toolchains/arm-linux-androideabi-4.9/prebuilt/

# 3. Check Java
java -version
echo %JAVA_HOME%

# 4. Build Android
cd app && ./gradlew assembleDebug

# 5. Deploy
./gradlew installDebug

# 6. Check logs
adb logcat | grep native-lib

# 7. Pull frame
adb pull /sdcard/Android/data/com.example.flam/files/processed.png

# 8. Build web
cd web && npm run build

# 9. Start web
npm start

# 10. Open browser
http://localhost:8080
```

## 🐛 Debugging Tips

### Logcat Filtering

```bash
# See all native-lib logs
adb logcat -s native-lib

# Follow JNI calls
adb logcat | grep -i jni

# Watch GL errors
adb logcat | grep -i glrenderer

# Full activity lifecycle
adb logcat | grep -i mainactivity
```

### Crash Analysis

```bash
# Capture crash
adb logcat > crash.log

# Look for the stack trace and:
- UnsatisfiedLinkError → native library not found
- FileNotFoundException → path issue
- PermissionDenied → camera/storage permission
- NullPointerException → uninitialized variable
```

### Performance Monitoring

```bash
# Frame rate
adb shell dumpsys gfxinfo com.example.flam

# Memory
adb shell dumpsys meminfo com.example.flam

# CPU
adb shell top -n 1 | grep flam
```

## 📚 File Sizes & Complexity

| File | Lines | Purpose |
|------|-------|---------|
| MainActivity.kt | ~65 | Main activity, camera permission, frame pipeline |
| CameraPreview.kt | ~50 | Camera1 API, bitmap capture loop |
| GLRenderer.kt | ~75 | OpenGL ES 2.0, texture upload & rendering |
| native-lib.cpp | ~52 | JNI entry, OpenCV Canny processing |
| CMakeLists.txt | ~12 | CMake build config |
| build.gradle | ~48 | Android build, NDK, dependencies |
| index.html | ~45 | Web UI, frame display |
| main.ts | ~18 | TypeScript frame loader |

## 🎓 Learning Resources

- **Android Camera**: https://developer.android.com/guide/topics/media/camera
- **JNI**: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/
- **OpenCV**: https://docs.opencv.org/master/
- **OpenGL ES**: https://developer.android.com/guide/topics/graphics/opengl
- **CMake**: https://cmake.org/cmake/help/latest/

---

**Keep this reference handy while developing and debugging!**
