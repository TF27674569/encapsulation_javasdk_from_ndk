### 封装 java 层 sdk 模型
###### java层的对象如何与NDK层的对象意义对应
利用句柄的形式，当初始化一个Ndk的对象时（malloc或new）会返回一个对象的头指针，在java对象中以long类型保存这个头指针的地址后，下次通过头指针的地址就能找到对象的实例。</br>
###### 以OpenCV的Mat对象为例，封装一个filter2D函数
为了使函数的调用不用一个效果改一次C++代码，而直接从java层修改参数得到所以进行</br>
细节拆分：</br>
filter2D函数可以单独使用</br>
Mat对象可以在java层初始化</br>
Bitmap和Mat互相转换</br></br></br>


先定义好java层如何使用,以头像掩模操作为例</br>
1.将Bitmap转为Mat</br>
2.创建一个掩模核（上下左右为-1，中间为5）</br>
3.经行filter2D操作</br>
4.将Mat转为Bitmap</br>

```java
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        Mat src = new Mat();
        // 将Bitmap转为Mat
        CVUtils.bitmap2Mat(bitmap, src);

        //创建一个掩模核
        Mat kernel = new Mat(3,3, CVType.CV_32FC1);

        kernel.put(0, 0, 0);
        kernel.put(0, 1, -1);
        kernel.put(0, 2, 0);
        kernel.put(1, 0, -1);
        kernel.put(1, 1, 5);
        kernel.put(1, 2, -1);
        kernel.put(2, 0, 0);
        kernel.put(2, 1, -1);
        kernel.put(2, 2, 0);
    
        Mat dst = new Mat();
        // 经行filter2D操作
        CVUtils.filter2D(src, dst, kernel);

        //将Mat转为Bitmap
        CVUtils.mat2Bitmap(dst, bitmap);
        
        imageView.setImageBitmap(bitmap);
```
</br></br>

###### 开始处理Java的Mat与c++的Mat对应关系

```java
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

    // call native
    private native long nativeMat();

    private native long nativeMatIII(int rows,int cols,int type);
}
```
利用long mPtr保存NDK的mat对象地址
```c++
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
```
###### 开始处理JavaMat的put函数，也就是掩模核需要添加值
这里注意一下，计算中需要兼容各种各样的掩模核，所以掩模核的值我们定义必须为，CV_32FC1，也就是float
```java 
class Mat{
    ...
    public void put(int row, int col, float value) {
        if(type != CVType.CV_32FC1){
            throw new UnsupportedOperationException("Provider value nonsupport, please check CVType.");
        }
        nativePut(mPtr,row,col,value);
    }
    
   private native void nativePut(long ptr, int row, int col, float value);
   ....
  }
```
对ndk层的掩模核进行赋值
```c++
extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_Mat_nativePut(JNIEnv *env, jobject instance, jlong ptr, jint row, jint col,
                                   jfloat value) {
    Mat *src = reinterpret_cast<Mat *>(ptr);
    src->at<float>(row, col) = value;
}
```

Utils中的三个函数就是普通的JNI函数了只要注意传Mat的时候，传入的是地址即可
```java
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
```
NDK层
```c++
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
```
###### 这样之后就能进行，在java层可以通过封装，java对象，实现c++的函数操作了，但是还有几个注意的点
1. 内存回收问题，java层有虚拟机自动回收，C++的对象什么时候释放
2. 提供出的接口，调用者不一定会用

第一个问题，gc在回收之前会调用对象的finalize函数（有点像c++的析构函数） ，在6.0之前系统都是通过在此函数中通知NDK进行资源释放
```java
public class Mat {

    long mPtr;
    ...

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        nativeDelete(mPtr);
    }

    private native void nativeDelete(long ptr);
    ...
}

```
NDK释放
```c++
extern "C"
JNIEXPORT void JNICALL
Java_com_ndk_opencv2_Mat_nativePut(JNIEnv *env, jobject instance, jlong ptr, jint row, jint col,
                                   jfloat value) {
    Mat *src = reinterpret_cast<Mat *>(ptr);
    src->at<float>(row, col) = value;
}
```
第二个问题，两种方式，第一种让调用者熟悉一下这种调用方式，第二种在java层进行二次封装。
