//
// Created by Vivek.
//

#include "jni_helper.h"
#include <stdexcept>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wshadow"

VCSpace::JNIHelper::~JNIHelper() {
  cleanup();
}

VCSpace::JNIHelper& VCSpace::JNIHelper::getInstance() {
  static JNIHelper instance;
  return instance;
}

void VCSpace::JNIHelper::initialize(JNIEnv* env, jobject editorActivity) {
  if (initialized) return;

  this->javaVm = nullptr;
  env->GetJavaVM(&this->javaVm);

  this->editorActivity = env->NewGlobalRef(editorActivity);

  initialized = true;
}

JNIEnv* VCSpace::JNIHelper::getEnv() {
  if (!initialized) {
    throw std::runtime_error("JNIHelper not initialized");
  }

  JNIEnv* env;
  if (javaVm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
    javaVm->AttachCurrentThread(&env, nullptr);
  }
  return env;
}

jobject VCSpace::JNIHelper::getEditorActivity() {
  if (!initialized) {
    throwJavaException("Call WasmLoader.init() before calling getEditorActivity()");
  }
  if (!editorActivity) {
    throwJavaException("Editor activity is null");
  }
  return this->editorActivity;
}

void VCSpace::JNIHelper::cleanup() {
  if (editorActivity) {
    getEnv()->DeleteGlobalRef(editorActivity);
    editorActivity = nullptr;
  }

  initialized = false;
}

void VCSpace::JNIHelper::throwJavaException(const char* message) {
  JNIEnv* env = getEnv();
  jclass exceptionClass = env->FindClass("com/teixeira/vcspace/plugins/wasm/WasmRuntimeException");
  env->ThrowNew(exceptionClass, message);

  env->DeleteLocalRef(exceptionClass);
}

std::vector<std::string> VCSpace::JNIHelper::jobjectArrayToVector(jobjectArray jArray) {
  JNIEnv* env = getEnv();

  std::vector<std::string> result;

  jsize arrayLength = env->GetArrayLength(jArray);

  for (jsize i = 0; i < arrayLength; ++i) {
    auto jStr = (jstring) env->GetObjectArrayElement(jArray, i);

    const char* charStr = env->GetStringUTFChars(jStr, nullptr);
    result.emplace_back(charStr);

    env->ReleaseStringUTFChars(jStr, charStr);
  }

  return result;
}

#pragma clang diagnostic pop