#include "jni_helper.h"
#include "logger.h"
#include "wasm_loader.h"

#include <fstream>
#include <jni.h>
#include <vector>

using namespace VCSpace;

extern "C" JNIEXPORT void JNICALL
Java_com_teixeira_vcspace_plugins_wasm_WasmLoader_runWasm(JNIEnv* env, jclass clazz, jstring path, jobjectArray function_name) {
  const char* wasmFilePath = env->GetStringUTFChars(path, 0);
  if (!wasmFilePath) {
    return;
  }

  std::ifstream wasmFile(wasmFilePath, std::ios::binary | std::ios::ate);
  if (!wasmFile) {
    LOGE("Failed to open wasm file: %s", wasmFilePath);
    env->ReleaseStringUTFChars(path, wasmFilePath);
    return;
  }

  std::streamsize wasmSize = wasmFile.tellg();
  wasmFile.seekg(0, std::ios::beg);

  std::vector<uint8_t> wasmBytes(wasmSize);
  if (!wasmFile.read(reinterpret_cast<char*>(wasmBytes.data()), wasmSize)) {
    LOGE("Failed to read wasm file: %s", wasmFilePath);
    env->ReleaseStringUTFChars(path, wasmFilePath);
    return;
  }

  WasmLoader::executeWasmCode(wasmBytes.data(), wasmSize, JNIHelper::getInstance().jobjectArrayToVector(function_name));

  env->ReleaseStringUTFChars(path, wasmFilePath);
}

extern "C" JNIEXPORT jint JNICALL
Java_com_teixeira_vcspace_plugins_wasm_WasmLoader_init(JNIEnv* env, jclass clazz, jobject editor_activity) {
  JNIHelper::getInstance().initialize(env, editor_activity);
  return JNI_OK;
}