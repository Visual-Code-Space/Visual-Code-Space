import java.io.File
import org.gradle.kotlin.dsl.provideDelegate

/**
 * Information about the CI build.
 *
 * @author Akash Yadav
 */
object CI {

  /** The short commit hash. */
  val commitHash by lazy {
    val sha = System.getenv("GITHUB_SHA") ?: "HEAD"
    cmdOutput("git", "rev-parse", "--short", sha)
  }

  /** Name of the current branch. */
  val branchName by lazy {
    System.getenv("GITHUB_REF_NAME")
        ?: cmdOutput("git", "rev-parse", "--abbrev-ref", "HEAD") // by default, 'main'
  }

  /** Whether the current build is a CI build. */
  val isCiBuild by lazy { "true" == System.getenv("CI") }

  private fun cmdOutput(vararg args: String): String {
    return ProcessBuilder(*args)
        .directory(File("."))
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
        .trim()
  }
}
