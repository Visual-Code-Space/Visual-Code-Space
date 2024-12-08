#include "gemini.h"
#include <aes.hpp>
#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_teixeira_vcspace_core_Secrets_getGenerativeAiApiKey(JNIEnv* env, jclass) {
  std::string key = AI::gemini::getApiKey();
  return env->NewStringUTF(key.c_str());
}
