//
// Created by Vivek.
//

#ifndef VCSPACE_WASM_LOADER_H
#define VCSPACE_WASM_LOADER_H

#include "wasm3.h"
#include <cstdint>
#include <string>
#include <vector>

namespace VCSpace {
  class WasmLoader {
  public:
    static void executeWasmCode(const uint8_t* wasmCode, size_t codeSize, const std::vector<std::string>& functions);
    static void registerFunctions(IM3Module module);
  };
}// namespace VCSpace

#endif//VCSPACE_WASM_LOADER_H
