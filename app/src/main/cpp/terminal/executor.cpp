#include <android/log.h>
#include <jni.h>
#include <string>
#include <sys/wait.h>
#include <unistd.h>

#define LOG_TAG "ExecutorNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jint JNICALL
Java_com_teixeira_vcspace_terminal_Executor_runBinary(
    JNIEnv* env,
    jobject /* this */,
    jstring binaryPath,
    jobjectArray argsArray,
    jobjectArray envArray,
    jstring linkerPath) {
    const char* binary = env->GetStringUTFChars(binaryPath, 0);
    const char* linker = env->GetStringUTFChars(linkerPath, 0);

    // Convert argsArray to a C-style array
    jsize argsLen = env->GetArrayLength(argsArray);
    std::vector<char*> args;
    args.push_back(strdup(linker));// First argument: linker path
    args.push_back(strdup(binary));// Second argument: binary path
    for (int i = 0; i < argsLen; i++) {
        jstring arg = (jstring) env->GetObjectArrayElement(argsArray, i);
        const char* argStr = env->GetStringUTFChars(arg, 0);
        args.push_back(strdup(argStr));
        env->ReleaseStringUTFChars(arg, argStr);
    }
    args.push_back(nullptr);// Null-terminate the array

    // Convert envArray to a C-style array
    jsize envLen = env->GetArrayLength(envArray);
    std::vector<char*> envp;
    for (int i = 0; i < envLen; i++) {
        auto envVar = (jstring) env->GetObjectArrayElement(envArray, i);
        const char* envStr = env->GetStringUTFChars(envVar, 0);
        envp.push_back(strdup(envStr));
        env->ReleaseStringUTFChars(envVar, envStr);
    }
    envp.push_back(nullptr);// Null-terminate the array

    // Create a pipe for capturing output
    int pipefd[2];
    if (pipe(pipefd) < 0) {
        LOGE("Failed to create pipe: %s", strerror(errno));
        return errno;
    }

    // Fork and execute
    pid_t pid = fork();
    if (pid == 0) {
        // Child process
        close(pipefd[0]);              // Close the read end of the pipe
        dup2(pipefd[1], STDOUT_FILENO);// Redirect stdout to the pipe
        dup2(pipefd[1], STDERR_FILENO);// Redirect stderr to the pipe
        close(pipefd[1]);              // Close the write end after duplication

        // Execute the binary
        execve(linker, args.data(), envp.data());
        LOGE("execve failed with error: %s", strerror(errno));
        _exit(errno);// Exit with errno if execve fails
    }

    // Parent process
    close(pipefd[1]);// Close the write end of the pipe

    // Read the output from the pipe
    char buffer[1024];
    ssize_t bytesRead;
    while ((bytesRead = read(pipefd[0], buffer, sizeof(buffer) - 1)) > 0) {
        buffer[bytesRead] = '\0';// Null-terminate the output
        LOGI("%s", buffer);      // Log the output to logcat
    }
    close(pipefd[0]);// Close the read end of the pipe

    env->ReleaseStringUTFChars(binaryPath, binary);
    env->ReleaseStringUTFChars(linkerPath, linker);

    for (char* arg: args) free(arg);
    for (char* envVar: envp) free(envVar);

    if (pid < 0) {
        LOGE("Fork failed with error: %s", strerror(errno));
        return errno;// Fork failed
    }

    // Wait for the child process
    int status;
    waitpid(pid, &status, 0);
    if (WIFEXITED(status)) {
        return WEXITSTATUS(status);// Return child process exit status
    } else if (WIFSIGNALED(status)) {
        LOGE("Child process terminated by signal: %d", WTERMSIG(status));
        return 128 + WTERMSIG(status);// Signal termination
    }

    return -1;// Unexpected exit condition
}
