#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"native-lib",__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,"native-lib",__VA_ARGS__)

using namespace cv;

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_example_flam_NativeLib_processFrame(JNIEnv *env, jclass clazz, jbyteArray input, jint w, jint h) {
    // get input bytes
    jsize len = env->GetArrayLength(input);
    jbyte* data = env->GetByteArrayElements(input, nullptr);
    if (data == nullptr) {
        LOGE("Failed to get input array");
        return nullptr;
    }

    LOGI("Processing frame: %d x %d, len=%d", w, h, len);

    try {
        // Create Mat from RGBA bytes (CV_8UC4)
        Mat img(h, w, CV_8UC4, (unsigned char*)data);

        // Convert to grayscale
        Mat gray;
        cvtColor(img, gray, COLOR_RGBA2GRAY);

        // Canny edge detection
        Mat edges;
        Canny(gray, edges, 80, 160);

        // Convert edges (single channel) to RGBA output
        Mat out;
        cvtColor(edges, out, COLOR_GRAY2RGBA);

        // Prepare return byte array
        int outLen = out.total() * out.elemSize();
        jbyteArray outArray = env->NewByteArray(outLen);
        env->SetByteArrayRegion(outArray, 0, outLen, reinterpret_cast<const jbyte*>(out.data));

        LOGI("Frame processed successfully, output len=%d", outLen);

        // release
        env->ReleaseByteArrayElements(input, data, 0);
        return outArray;
    } catch (const std::exception& e) {
        LOGE("Exception in processFrame: %s", e.what());
        env->ReleaseByteArrayElements(input, data, 0);
        return nullptr;
    }
}
