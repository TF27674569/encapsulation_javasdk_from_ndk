package com.ndk.opencv2;

/**
 * create by TIAN FENG on 2019/11/21
 */
public enum CVType {
    CV_8UC1(0),
    CV_8UC2(8),
    CV_8UC3(25),
    CV_32FC1(5);

    int value;
    CVType(int value){
        this.value = value;
    }
}
