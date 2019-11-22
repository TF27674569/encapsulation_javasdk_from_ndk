# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
# 需要引入我们头文件,以这个配置的目录为基准
set( OpenCV_DIR F:/OpenCV-2.4.11-android-sdk/OpenCV-android-sdk/sdk/native/jni )

find_package(OpenCV REQUIRED )
if(OpenCV_FOUND)
    include_directories(${OpenCV_INCLUDE_DIRS})
    message(STATUS "OpenCV library status:")
    message(STATUS "    version: ${OpenCV_VERSION}")
    message(STATUS "    libraries: ${OpenCV_LIBS}")
    message(STATUS "    include path: ${OpenCV_INCLUDE_DIRS}")
else(OpenCV_FOUND)
    message(FATAL_ERROR "OpenCV library not found")
endif(OpenCV_FOUND)


add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        NativeMat.cpp
        cv_helper.cpp
        CVUtils.cpp
        native-lib.cpp)


find_library( # Sets the name of the path variable.
        log-lib
        log)


target_link_libraries( # Specifies the target library.
        native-lib
        ${OpenCV_LIBS}
        jnigraphics
        ${log-lib})