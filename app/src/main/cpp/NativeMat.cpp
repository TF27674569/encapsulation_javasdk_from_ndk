//
// Created by TIAN FENG on 2019/11/21.
//
#include <jni.h>
#include "opencv2/opencv.hpp"

using namespace cv;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_ndk_opencv2_Mat_nativeMat(JNIEnv *env, jobject instance) {
    Mat *mat = new Mat();
    return reinterpret_cast<jlong >(mat);
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_ndk_opencv2_Mat_nativeMatIII(JNIEnv *env, jobject instance, jint rows, jint cols,
                                      jint type) {
    Mat *mat = new Mat(rows, cols, type);
    return reinterpret_cast<jlong >(mat);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_Mat_nativePut(JNIEnv *env, jobject instance, jlong ptr, jint row, jint col,
                                   jfloat value) {
    Mat *src = reinterpret_cast<Mat *>(ptr);
    src->at<float>(row, col) = value;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_Mat_nativeDelete(JNIEnv *env, jobject instance, jlong ptr) {
    Mat *src = reinterpret_cast<Mat *>(ptr);
    delete (src);
}