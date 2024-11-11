//
// Created by Vivek.
//

#include "wasm_loader.h"
#include <android/native_activity.h>
#include <jni.h>
#include <thread>

#include "jni_helper.h"
#include "logger.h"
#include "m3_api_libc.h"
#include "m3_env.h"
#include "wasm_apis.h"

using namespace VCSpace;

#define JNI_HELPER JNIHelper::getInstance()

void VCSpace::WasmLoader::executeWasmCode(const uint8_t* wasmCode, size_t codeSize, const std::vector<std::string>& functions) {
  IM3Environment env = m3_NewEnvironment();
  if (!env) {
    LOGE("Failed to create wasm3 environment");
    return;
  }

  IM3Runtime runtime = m3_NewRuntime(env, 1024, NULL);
  if (!runtime) {
    LOGE("Failed to create wasm3 runtime");
    m3_FreeEnvironment(env);
    return;
  }

  IM3Module module;
  M3Result result = m3_ParseModule(env, &module, wasmCode, codeSize);
  if (result) {
    LOGE("Failed to parse wasm module: %s", result);
    m3_FreeRuntime(runtime);
    m3_FreeEnvironment(env);
    return;
  }

  result = m3_LoadModule(runtime, module);
  if (result) {
    LOGE("Failed to load wasm module: %s", result);
    m3_FreeRuntime(runtime);
    m3_FreeEnvironment(env);
    return;
  }

  result = m3_LinkLibC(module);
  if (result) {
    LOGE("Failed to link libc: %s", result);
    m3_FreeRuntime(runtime);
    m3_FreeEnvironment(env);
    return;
  }

  registerFunctions(module);

  IM3Function f;
  for (const auto& functionName: functions) {
    result = m3_FindFunction(&f, runtime, functionName.c_str());
    if (result) {
      LOGE("Function '%s' not found: %s", functionName.c_str(), result);
      JNI_HELPER.throwJavaException(std::string("Function '" + functionName + "' not found: " + result).c_str());
    } else {
      result = m3_CallV(f);
      if (result) {
        LOGE("Failed to call function '%s': %s", functionName.c_str(), result);
        JNI_HELPER.throwJavaException(std::string("Failed to call function '" + functionName + "': " + result).c_str());
      } else {
        LOGI("Function '%s' called successfully", functionName.c_str());
      }
    }
  }

  m3_FreeRuntime(runtime);
  m3_FreeEnvironment(env);
}

void VCSpace::WasmLoader::registerFunctions(IM3Module module) {
  m3_LinkRawFunction(module, "env", "toast", "v(i)", toast);
}
