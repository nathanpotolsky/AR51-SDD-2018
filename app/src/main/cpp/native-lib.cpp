#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_nathan_myapplication_MainActivity_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_nathan_myapplication_MainActivity_nathanFromJNI(JNIEnv *env, jobject /* this */) {
    std::string greeting = "Hello from Nathan";
    return env->NewStringUTF(greeting.c_str());
}