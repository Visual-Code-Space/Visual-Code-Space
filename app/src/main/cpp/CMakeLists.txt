# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

project("vcspace")

add_subdirectory(third-party/tiny-AES-c)

add_library(${CMAKE_PROJECT_NAME} SHARED
        vcspace.cpp
        gemini.cpp
        terminal/executor.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        tiny-aes
)
