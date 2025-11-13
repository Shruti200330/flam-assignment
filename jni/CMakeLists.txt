cmake_minimum_required(VERSION 3.4.1)

# IMPORTANT: Replace this with your actual OpenCV Android SDK path
# Example: C:/opencv-4.7.0-android-sdk/sdk/native/jni
# Or on Windows: C:\\Users\\YourName\\Downloads\\opencv-android\\sdk\\native\\jni
set(OpenCV_DIR "C:/opencv-android/sdk/native/jni")

find_package(OpenCV REQUIRED)
message(STATUS "OpenCV libs: ${OpenCV_LIBS}")
include_directories(${OpenCV_INCLUDE_DIRS})

add_library(native-lib SHARED native-lib.cpp)

target_link_libraries(native-lib ${OpenCV_LIBS} android log)
