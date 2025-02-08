cmake_minimum_required(VERSION 3.22.1)
project("simplemnnchat")

add_library(${CMAKE_PROJECT_NAME} SHARED
        llm_mnn_jni.cpp)

# Add library paths
set(LIB_PATH "${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")

# Link the shared libraries
add_library(llm SHARED IMPORTED)
set_target_properties(llm PROPERTIES IMPORTED_LOCATION "${LIB_PATH}/libllm.so")

add_library(MNN SHARED IMPORTED)
set_target_properties(MNN PROPERTIES IMPORTED_LOCATION "${LIB_PATH}/libMNN.so")

add_library(mnn_cl SHARED IMPORTED)
set_target_properties(mnn_cl PROPERTIES IMPORTED_LOCATION "${LIB_PATH}/libMNN_CL.so")

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        llm
        MNN
        mnn_cl)