//
// Created by Vivek.
//

#ifndef VCSPACE_JNI_HELPER_H
#define VCSPACE_JNI_HELPER_H

#include <jni.h>
#include <string>
#include <vector>

namespace VCSpace {
  class JNIHelper {
  private:
    JNIHelper() : javaVm(nullptr), editorActivity(nullptr), initialized(false) {};

    ~JNIHelper();

  private:
    JavaVM* javaVm;
    jobject editorActivity;
    bool initialized;

  public:
    JNIHelper(const JNIHelper&) = delete;

    JNIHelper& operator=(const JNIHelper&) = delete;

  public:
    static JNIHelper& getInstance();

    void initialize(JNIEnv* env, jobject editorActivity);

    void throwJavaException(const char* message);

    std::vector<std::string> jobjectArrayToVector(jobjectArray jArray);

    JNIEnv* getEnv();

    jobject getEditorActivity();

    void cleanup();
  };
}// namespace VCSpace

#endif//VCSPACE_JNI_HELPER_H
