package com.ndk.opencv2;

/**
 * create by TIAN FENG on 2019/11/21
 */
public class Mat {

    long mPtr;
    private CVType type;
    public Mat(){
        mPtr = nativeMat();
    }

    public Mat(int rows,int cols,CVType type){
        this.type = type;
        mPtr = nativeMatIII(rows,cols,type.value);
    }


    public void put(int row, int col, float value) {
        if(type != CVType.CV_32FC1){
            throw new UnsupportedOperationException("Provider value nonsupport, please check CVType.");
        }
        nativePut(mPtr,row,col,value);
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        nativeDelete(mPtr);
    }

    // call native
    private native long nativeMat();

    private native long nativeMatIII(int rows,int cols,int type);

    private native void nativePut(long ptr, int row, int col, float value);

    private native void nativeDelete(long ptr);
}
