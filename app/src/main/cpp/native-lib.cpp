#include <jni.h>
#include <string>
#include <algorithm>
#include <climits>
#include <android/log.h>

#define LOG_TAG "JNI_DEMO"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_MainActivity_helloFromJNI(
        JNIEnv* env,
        jobject /* thiz */) {

    LOGI("helloFromJNI : execution dans la couche native");
    return env->NewStringUTF("Bonjour depuis le C++ natif via JNI !");
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_jnidemo_MainActivity_factorial(
        JNIEnv* env,
        jobject /* thiz */,
        jint n) {

    if (n < 0) {
        LOGE("factorial : valeur negative rejetee (%d)", n);
        return -1;
    }

    long long resultat = 1;
    for (int i = 2; i <= n; i++) {
        resultat *= i;
        if (resultat > INT_MAX) {
            LOGE("factorial : depassement detecte pour n=%d", n);
            return -2;
        }
    }

    LOGI("factorial(%d) = %lld", n, resultat);
    return static_cast<jint>(resultat);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_jnidemo_MainActivity_reverseString(
        JNIEnv* env,
        jobject /* thiz */,
        jstring javaString) {

    if (javaString == nullptr) {
        LOGE("reverseString : pointeur null recu");
        return env->NewStringUTF("");
    }

    const char* buffer = env->GetStringUTFChars(javaString, nullptr);
    if (buffer == nullptr) {
        LOGE("reverseString : echec GetStringUTFChars");
        return env->NewStringUTF("");
    }

    std::string texte(buffer);
    env->ReleaseStringUTFChars(javaString, buffer);
    std::reverse(texte.begin(), texte.end());

    LOGI("reverseString : resultat = %s", texte.c_str());
    return env->NewStringUTF(texte.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_jnidemo_MainActivity_sumArray(
        JNIEnv* env,
        jobject /* thiz */,
        jintArray array) {

    if (array == nullptr) {
        LOGE("sumArray : tableau null recu");
        return -1;
    }

    jsize taille   = env->GetArrayLength(array);
    jint* elements = env->GetIntArrayElements(array, nullptr);

    if (elements == nullptr) {
        LOGE("sumArray : echec GetIntArrayElements");
        return -2;
    }

    long long total = 0;
    for (jsize i = 0; i < taille; i++) {
        total += elements[i];
    }

    env->ReleaseIntArrayElements(array, elements, JNI_ABORT);

    if (total > INT_MAX) {
        LOGE("sumArray : depassement de la somme");
        return -3;
    }

    LOGI("sumArray : somme de %d elements = %lld", taille, total);
    return static_cast<jint>(total);
}