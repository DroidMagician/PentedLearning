#include <jni.h>
#include <string>

//stripe Key
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_pented_learningapp_myUtils_Keys_stripeKey(JNIEnv *env, jobject object, jboolean isLive) {
    if (isLive) {
        std::string databaseName = "pk_live_PkxtBPS34lLsbLnaCfxycogW00BSEeOtv3";
        return env->NewStringUTF(databaseName.c_str());
    } else {
        std::string databaseName = "pk_test_3x0K5YDvGFLEVvj3pTKkWJdv00DUcmsIP1";
        return env->NewStringUTF(databaseName.c_str());
    }
}

//stripe Key
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_pented_learningapp_myUtils_Keys_bucketRegion(JNIEnv *env, jobject object, jboolean isLive) {
    if (isLive) {
        std::string databaseName = "ap-south-1";
        return env->NewStringUTF(databaseName.c_str());
    } else {
        std::string databaseName = "ap-south-1";
        return env->NewStringUTF(databaseName.c_str());
    }
}

//stripe Key
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_pented_learningapp_myUtils_Keys_accessKey(JNIEnv *env, jobject object, jboolean isLive) {
    if (isLive) {
//        std::string databaseName = "AKIAZEUBPEPZL4BEWOUK";
        std::string databaseName = "ca576046c0863e0eTKUT";
        return env->NewStringUTF(databaseName.c_str());
    } else {
//        std::string databaseName = "AKIAZEUBPEPZL4BEWOUK";
        std::string databaseName = "ca576046c0863e0eTKUT";
        return env->NewStringUTF(databaseName.c_str());
    }
}

//stripe Key
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_pented_learningapp_myUtils_Keys_secretKey(JNIEnv *env, jobject object, jboolean isLive) {
    if (isLive) {
//        std::string databaseName = "UiL6O9p2QKO59tY2VavwMiFbcmc2EhVCju3ZpEcV";
        std::string databaseName = "8Mpmi9z4H0bENWfS8NjYQo197223hbJsdXNp0Fxv";
        return env->NewStringUTF(databaseName.c_str());
    } else {
//        std::string databaseName = "UiL6O9p2QKO59tY2VavwMiFbcmc2EhVCju3ZpEcV";
        std::string databaseName = "8Mpmi9z4H0bENWfS8NjYQo197223hbJsdXNp0Fxv";
        return env->NewStringUTF(databaseName.c_str());
    }
}
//stripe Key
//extern "C"
//JNIEXPORT jstring
//JNICALL
//Java_com_pented_learningapp_myUtils_Keys_s3BaseUrl(JNIEnv *env, jobject object, jboolean isLive) {
//    if (isLive) {
//        std::string databaseName = "https://s3.console.aws.amazon.com/s3/buckets/pentedsolutionvideos/";
//        return env->NewStringUTF(databaseName.c_str());
//    } else {
//        std::string databaseName = "https://s3.console.aws.amazon.com/s3/buckets/pentedsolutionvideos/";
//        return env->NewStringUTF(databaseName.c_str());
//    }
//}


//stripe Key
extern "C"
JNIEXPORT jstring
JNICALL
Java_com_pented_learningapp_myUtils_Keys_bucketName(JNIEnv *env, jobject object, jboolean isLive) {
    if (isLive) {
        std::string databaseName = "pentedapp";
        return env->NewStringUTF(databaseName.c_str());
    } else {
        std::string databaseName = "pentedapp";
        return env->NewStringUTF(databaseName.c_str());
    }
}
