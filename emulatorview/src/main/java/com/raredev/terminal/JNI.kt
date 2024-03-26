package com.raredev.terminal

/** Native methods for creating and managing pseudoterminal subprocesses. */
internal object JNI {
  init {
    System.loadLibrary("vcspace-terminal")
  }

  /**
   * Create a subprocess. Differs from [ProcessBuilder] in that a pseudoterminal is used to
   * communicate with the subprocess.
   *
   * Callers are responsible for calling [.close] on the returned file descriptor.
   *
   * @param cmd The command to execute
   * @param cwd The current working directory for the executed command
   * @param args An array of arguments to the command
   * @param envVars An array of strings of the form "VAR=value" to be added to the environment of
   *   the process
   * @param processId A one-element array to which the process ID of the started process will be
   *   written.
   * @return the file descriptor resulting from opening /dev/ptmx master device. The sub process
   *   will have opened the slave device counterpart (/dev/pts/$N) and have it as stdint, stdout and
   *   stderr.
   */
  @JvmStatic
  external fun createSubprocess(
    cmd: String?,
    cwd: String?,
    args: Array<String?>?,
    envVars: Array<String?>?,
    processId: IntArray?,
    rows: Int,
    columns: Int
  ): Int

  /**
   * Set the window size for a given pty, which allows connected programs to learn how large their
   * screen is.
   */
  @JvmStatic external fun setPtyWindowSize(fd: Int, rows: Int, cols: Int)

  /**
   * Causes the calling thread to wait for the process associated with the receiver to finish
   * executing.
   *
   * @return if >= 0, the exit status of the process. If < 0, the signal causing the process to stop
   *   negated.
   */
  @JvmStatic external fun waitFor(processId: Int): Int

  /** Close a file descriptor through the close(2) system call. */
  @JvmStatic external fun close(fileDescriptor: Int)
}
