//
// Created by Vivek.
//

#ifndef VCSPACE_LOGGER_H
#define VCSPACE_LOGGER_H

#include <android/log.h>

#define LOG_TAG "VCSpaceNativeLog"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif //VCSPACE_LOGGER_H
