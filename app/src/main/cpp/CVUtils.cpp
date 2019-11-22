//
// Created by TIAN FENG on 2019/11/21.
//
#include <jni.h>
#include "opencv2/opencv.hpp"
#include "cv_helper.h"

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_CVUtils_nativeBitmap2Mat(JNIEnv *env, jclass type, jobject bitmap, jlong ptr) {
    Mat *src = reinterpret_cast<Mat *>(ptr);
    cv_helper::bitmap2Mat(env, bitmap, *src);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_CVUtils_nativeMat2Bitmap(JNIEnv *env, jclass type, jlong ptr,
                                              jobject bitmap) {

    Mat *mat = reinterpret_cast<Mat*>(ptr);
    cv_helper::mat2Bitmap(env,*mat,bitmap);

}

extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_CVUtils_nativeFilter2D(JNIEnv *env, jclass type, jlong srcPtr, jlong dstPtr,
                                            jlong kernelPtr) {
    Mat *src = reinterpret_cast<Mat *>(srcPtr);
    Mat *dst = reinterpret_cast<Mat *>(dstPtr);
    Mat *kernel = reinterpret_cast<Mat *>(kernelPtr);

    Mat bgr;
    cvtColor(*src, bgr, COLOR_BGRA2BGR);

    filter2D(bgr, *dst, src->depth(), *kernel);
}