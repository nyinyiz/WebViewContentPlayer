cmake_minimum_required(VERSION 3.10.2)

project("webviewcontentplayer")

# Define the library name
add_library(native-lib SHARED
        src/main/cpp/native-lib.cpp)

# Find the Android log library
find_library(log-lib log)

# Link libraries
target_link_libraries(native-lib
        ${log-lib})
