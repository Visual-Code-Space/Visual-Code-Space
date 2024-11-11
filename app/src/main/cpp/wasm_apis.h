//
// Created by Vivek.
//

#ifndef VCSPACE_WASM_APIS_H
#define VCSPACE_WASM_APIS_H

#include "jni_helper.h"
#include "wasm3.h"
#include <jni.h>

void showToast(JNIEnv* env, const char* message) {
  jstring jMessage = env->NewStringUTF(message);
  if (jMessage == nullptr) {
    return;
  }

  jclass provider = env->FindClass("com/teixeira/vcspace/plugins/wasm/WasmModuleProvider");
  if (provider == nullptr) {
    return;
  }

  jmethodID toastMethod = env->GetStaticMethodID(provider, "toast", "(Ljava/lang/String;)V");
  if (toastMethod == nullptr) {
    return;
  }

  env->CallStaticVoidMethod(provider, toastMethod, jMessage);

  env->DeleteLocalRef(jMessage);
  env->DeleteLocalRef(provider);
}

m3ApiRawFunction(toast) {
  m3ApiGetArg(int32_t, msgPtr);

  const char* msg = (const char*) m3ApiOffsetToPtr(msgPtr);
  JNIEnv* env = VCSpace::JNIHelper::getInstance().getEnv();

  showToast(env, msg);
  m3ApiSuccess();
}

#endif//VCSPACE_WASM_APIS_H
