package com.ndk.opencv2;

import android.graphics.Bitmap;

/**
 * create by TIAN FENG on 2019/11/21
 */
public class CVUtils {

    public static void bitmap2Mat(Bitmap bitmap, Mat src) {
        nativeBitmap2Mat(bitmap, src.mPtr);
    }


    public static void mat2Bitmap(Mat dst, Bitmap bitmap) {
        nativeMat2Bitmap(dst.mPtr, bitmap);
    }

    /**
     * 卷积操作
     *
     * @param src    输入图像
     * @param dst    结果图像
     * @param kernel 卷积核
     */
    public static void filter2D(Mat src, Mat dst, Mat kernel) {
        nativeFilter2D(src.mPtr, dst.mPtr, kernel.mPtr);
    }

    private static native void nativeFilter2D(long srcPtr, long dstPtr, long kernelPtr);

    private static native void nativeBitmap2Mat(Bitmap bitmap, long ptr);

    private static native void nativeMat2Bitmap(long mPtr, Bitmap bitmap);
}
