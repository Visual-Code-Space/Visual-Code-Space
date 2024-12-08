
#include "gemini.h"
#include <aes.hpp>

namespace AI {
  std::string gemini::getApiKey() {
    size_t cipherLen = sizeof(AI::gemini_api_key) / sizeof(AI::gemini_api_key[0]);
    uint8_t plainText[cipherLen];
    memcpy(plainText, AI::gemini_api_key, cipherLen);

    struct AES_ctx ctx{};
    AES_init_ctx_iv(&ctx, AI::encryption_key, AI::iv);
    AES_CBC_decrypt_buffer(&ctx, plainText, cipherLen);

    size_t padLen = plainText[cipherLen - 1];
    if (padLen <= 16) {
      plainText[cipherLen - padLen] = '\0';
    }

    return {reinterpret_cast<char*>(plainText)};
  }
}// namespace AI