# 📋 GETTING STARTED - QUICK CHECKLIST

## ✅ Project Status: COMPLETE

All files created, committed, and ready to build.

---

## 📖 READ THESE FIRST (in order)

- [ ] **COMPLETION_SUMMARY.md** - Overview of what was built
- [ ] **README.md** - Architecture and features
- [ ] **SETUP_GUIDE.md** - Environment setup steps
- [ ] **REFERENCE.md** - Code snippets and debugging

---

## 🔧 BEFORE YOU BUILD

### Step 1: Install Prerequisites (Windows example)

- [ ] **Java JDK 11+**
  - Download from: https://adoptium.net/
  - Install and set `JAVA_HOME` environment variable
  - Verify: `java -version`

- [ ] **Android Studio**
  - Download from: https://developer.android.com/studio
  - Install and open SDK Manager
  - Install: Android SDK, NDK (r21+), CMake 3.22.1

- [ ] **OpenCV Android SDK**
  - Download from: https://opencv.org/releases/
  - Extract to: `C:\opencv-4.7.0-android-sdk` (or similar)
  - Verify: Check `sdk\native\jni` folder exists

- [ ] **Node.js & npm**
  - Download from: https://nodejs.org/
  - Verify: `npm --version`

- [ ] **Git**
  - Download from: https://git-scm.com/
  - Verify: `git --version`

### Step 2: Set Environment Variables

Windows → System Properties → Environment Variables → Add:

```
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-11.0.x
ANDROID_HOME = C:\Users\<username>\AppData\Local\Android\Sdk
ANDROID_NDK_HOME = C:\Users\<username>\AppData\Local\Android\Sdk\ndk\<version>
```

Verify by opening new terminal and typing:
```bash
echo %JAVA_HOME%
echo %ANDROID_HOME%
echo %ANDROID_NDK_HOME%
```

### Step 3: Create local.properties

Create file: `flam-assignment/app/local.properties`

**DO NOT commit this file** (already in .gitignore)

```properties
sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk\\ndk\\<version>
```

Example with actual paths:
```properties
sdk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk
ndk.dir=C:\\Users\\john\\AppData\\Local\\Android\\Sdk\\ndk\\25.2.9519653
```

### Step 4: Update OpenCV Path

Edit: `flam-assignment/jni/CMakeLists.txt`

Find this line:
```cmake
set(OpenCV_DIR "C:/opencv-android/sdk/native/jni")
```

Replace with your actual path (use forward slashes or double backslashes):
```cmake
set(OpenCV_DIR "C:/Users/john/Downloads/opencv-4.7.0-android-sdk/sdk/native/jni")
```

---

## 🚀 BUILD COMMANDS

### Build Android App

```bash
cd flam-assignment/app
./gradlew assembleDebug
./gradlew installDebug
```

**Troubleshooting build errors:**
- Check `local.properties` paths are correct
- Verify OpenCV_DIR in CMakeLists.txt
- Check NDK is installed: `%ANDROID_NDK_HOME%` should exist
- Try: `./gradlew clean` then rebuild

### Test on Device

- Connect Android device (API 21+) via USB OR start Android emulator
- Grant camera permission when app starts
- Should see camera preview with white edge lines on black background

### Export Processed Frame

```bash
# Pull the frame from device
adb pull /sdcard/Android/data/com.example.flam/files/processed.png ./web/dist/frame.png
```

### Build Web Viewer

```bash
cd web
npm install
npm run build
cp index.html dist/
npm start
```

Open browser: `http://localhost:8080`

---

## ✅ SUCCESS CHECKLIST

- [ ] Prerequisites installed (Java, Android Studio, OpenCV, Node.js, Git)
- [ ] Environment variables set (JAVA_HOME, ANDROID_HOME, ANDROID_NDK_HOME)
- [ ] `app/local.properties` created with correct paths
- [ ] `jni/CMakeLists.txt` OpenCV_DIR updated
- [ ] `./gradlew assembleDebug` completes successfully
- [ ] `./gradlew installDebug` installs APK
- [ ] App launches and shows camera preview
- [ ] Camera permission granted (in app)
- [ ] Edge detection visible on camera preview
- [ ] Frame exports to device storage (check logcat)
- [ ] `adb pull` successfully downloads `processed.png`
- [ ] `npm run build` builds TypeScript successfully
- [ ] `npm start` starts web server on http://localhost:8080
- [ ] Web viewer displays the processed frame
- [ ] You celebrate! 🎉

---

## 🐛 QUICK FIXES FOR COMMON ERRORS

| Error | Check |
|-------|-------|
| `CMake Error: OpenCV not found` | Verify `OpenCV_DIR` in CMakeLists.txt - must exist |
| `UnsatisfiedLinkError: native-lib` | Check build succeeded; look at: `app/build/intermediates/cmake` |
| `Cannot find JAVA_HOME` | Set environment variable and restart terminal |
| `Cannot find NDK` | Verify `ndk.dir` in local.properties is correct |
| `adb: command not found` | Add to PATH: `%ANDROID_HOME%\platform-tools` |
| `Cannot start emulator` | Open Android Studio → AVD Manager → create emulator |
| `App crashes on startup` | Check logcat: `adb logcat | grep -i error` |
| `No camera preview` | Grant permission in app settings |
| `Web viewer shows broken image` | Ensure `frame.png` exists in `web/dist/` |

---

## 📂 FILE LOCATIONS TO VERIFY

**Windows example:**

```
C:\Program Files\Eclipse Adoptium\jdk-11.0.x              ← JAVA_HOME
C:\Users\john\AppData\Local\Android\Sdk                   ← ANDROID_HOME
C:\Users\john\AppData\Local\Android\Sdk\ndk\25.2.9519653  ← ANDROID_NDK_HOME
C:\Users\john\Downloads\opencv-4.7.0-android-sdk          ← OpenCV location
  └─ sdk\native\jni                                        ← OpenCV_DIR
```

---

## 🎯 PROJECT STRUCTURE

```
flam-assignment/
├── app/
│   ├── build.gradle                    ← Android build config
│   ├── settings.gradle
│   ├── local.properties                ← CREATE THIS (don't commit)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/flam/
│       │   ├── MainActivity.kt          ← Main activity
│       │   ├── CameraPreview.kt         ← Camera capture
│       │   └── GLRenderer.kt            ← OpenGL rendering
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/*.xml
│
├── jni/
│   ├── CMakeLists.txt                  ← UPDATE OpenCV_DIR
│   └── native-lib.cpp                  ← OpenCV Canny processing
│
├── web/
│   ├── package.json
│   ├── tsconfig.json
│   ├── index.html
│   └── src/main.ts
│
├── README.md                           ← Architecture overview
├── SETUP_GUIDE.md                      ← Setup instructions
├── REFERENCE.md                        ← Code snippets
├── COMPLETION_SUMMARY.md               ← Project status
├── GETTING_STARTED.md                  ← This file
└── .gitignore
```

---

## 📞 NEED HELP?

1. **Build errors?** → Check `SETUP_GUIDE.md` → Troubleshooting section
2. **Code questions?** → See `REFERENCE.md` → Code snippets
3. **Architecture confused?** → Read `README.md` → Architecture section
4. **Don't know what to do?** → Follow this checklist step-by-step

---

## 🎓 KEY FILES TO UNDERSTAND

1. **MainActivity.kt** - Frame processing pipeline
2. **native-lib.cpp** - OpenCV Canny edge detection
3. **GLRenderer.kt** - OpenGL texture rendering
4. **CMakeLists.txt** - Native build configuration
5. **build.gradle** - Android build configuration

---

## 📊 EXPECTED OUTPUT

### When app runs:
```
Camera preview → Live camera feed visible
Edge detection → White edges on black background
Processing → ~10 fps (update every 100ms)
Export → processed.png saved to device
```

### When web viewer runs:
```
http://localhost:8080 → Display processed frame
Resolution shown → e.g., "1920x1080"
Status → "Loaded"
```

---

## ✨ YOU'RE ALL SET!

**Next step:** Follow SETUP_GUIDE.md → "Setup & Build" section

Good luck! 🚀

---

*Last updated: $(date)*
*All files created and committed to git*
*Professional git history with 8 semantic commits*
