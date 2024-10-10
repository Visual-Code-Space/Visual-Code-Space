package com.teixeira.vcspace.activities

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat.getInsetsController
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ThreadUtils
import com.teixeira.vcspace.BuildConfig
import com.teixeira.vcspace.activities.base.BaseActivity
import com.teixeira.vcspace.databinding.ActivityTerminalBinding
import com.teixeira.vcspace.ui.virtualkeys.SpecialButton
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeyButton
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysConstants
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysInfo
import com.teixeira.vcspace.ui.virtualkeys.VirtualKeysView.IVirtualKeysView
import com.teixeira.vcspace.utils.Logger
import com.teixeira.vcspace.utils.TerminalPythonCommands
import com.termux.terminal.TerminalEmulator
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.terminal.TextStyle
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import java.io.File
import org.json.JSONException

/**
 * @see <a
 *   href="https://github.com/AndroidIDEOfficial/AndroidIDE/blob/dev/app/src/main/java/com/itsaky/androidide/activities/TerminalActivity.java">TerminalActivity</a>
 */
class TerminalActivity : BaseActivity(), TerminalViewClient, TerminalSessionClient {
  private val logger = Logger.newInstance("TerminalActivity")

  private var _binding: ActivityTerminalBinding? = null
  private var fontSize = SizeUtils.dp2px(14f)
  private lateinit var terminal: TerminalView

  private val binding: ActivityTerminalBinding
    get() = checkNotNull(_binding) { "Activity has been destroyed!" }

  private val workingDirectory: String
    get() {
      val extras = intent.extras
      if (extras != null && extras.containsKey(KEY_WORKING_DIRECTORY)) {
        val directory = extras.getString(KEY_WORKING_DIRECTORY, null)
        return if (directory != null && directory.trim { it <= ' ' }.isNotEmpty()) directory
        else PathUtils.getRootPathExternalFirst()
      }
      return PathUtils.getRootPathExternalFirst()
    }

  override val navigationBarColor: Int
    get() = ContextCompat.getColor(this, android.R.color.black)

  override val statusBarColor: Int
    get() = ContextCompat.getColor(this, android.R.color.black)

  override fun getLayout(): View {
    _binding = ActivityTerminalBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    window?.also {
      val controller = getInsetsController(it, it.decorView)
      controller.setAppearanceLightNavigationBars(false)
      controller.setAppearanceLightStatusBars(false)
    }
    super.onCreate(savedInstanceState)

    onBackPressedDispatcher.addCallback(
      this,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          terminal.mTermSession.finishIfRunning()
          finish()
        }
      },
    )

    setupTerminalView()
    compilePython()
  }

  override fun onResume() {
    super.onResume()
    showSoftInput()
    setTerminalCursorBlinkingState(true)
  }

  override fun onStop() {
    super.onStop()
    setTerminalCursorBlinkingState(false)
  }

  override fun onDestroy() {
    super.onDestroy()
    _binding = null
  }

  private fun setupTerminalView() {
    terminal = TerminalView(this, null)
    terminal.setTerminalViewClient(this)
    terminal.attachSession(createSession())
    terminal.keepScreenOn = true
    terminal.setTextSize(fontSize)
    val params = LinearLayout.LayoutParams(-1, 0)
    params.weight = 1f
    binding.root.addView(terminal, 0, params)
    try {
      binding.virtualKeys.virtualKeysViewClient = KeyListener(terminal)
      binding.virtualKeys.reload(
        VirtualKeysInfo(VIRTUAL_KEYS, "", VirtualKeysConstants.CONTROL_CHARS_ALIASES)
      )
    } catch (e: JSONException) {
      logger.e("Unable to parse terminal virtual keys json data", e)
    }
  }

  private fun createSession(): TerminalSession {
    var shell = "/bin/sh"
    if (File("/bin/sh").exists().not()) {
      shell = "/system/bin/sh"
    }
    return TerminalSession(
      shell,
      workingDirectory,
      arrayOf(),
      arrayOf(),
      TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
      this,
    )
  }

  private fun compilePython() {
    val filePath = intent?.extras?.getString(KEY_PYTHON_FILE_PATH, null) ?: return
    if (filePath.trim { it <= ' ' }.isNotEmpty()) {
      ThreadUtils.getMainHandler().post {
        terminal.mTermSession.write(
          "${TerminalPythonCommands.getInterpreterCommand(this, filePath)}\r"
        )
      }
    }
  }

  override fun onTextChanged(changedSession: TerminalSession) {
    terminal.onScreenUpdated()
  }

  override fun onTitleChanged(changedSession: TerminalSession) {}

  override fun onSessionFinished(finishedSession: TerminalSession) {
    finish()
  }

  override fun onCopyTextToClipboard(session: TerminalSession, text: String) {
    ClipboardUtils.copyText("VCSpace Terminal", text)
  }

  override fun onPasteTextFromClipboard(session: TerminalSession) {
    val clip = ClipboardUtils.getText().toString()
    if (clip.trim { it <= ' ' }.isNotEmpty() && terminal.mEmulator != null) {
      terminal.mEmulator.paste(clip)
    }
  }

  override fun onBell(session: TerminalSession) {}

  override fun onColorsChanged(session: TerminalSession) {}

  override fun onTerminalCursorStateChange(state: Boolean) {}

  override fun getTerminalCursorStyle(): Int {
    return TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE
  }

  override fun logError(tag: String?, message: String?) {
    if (BuildConfig.DEBUG && message != null) {
      Log.e(tag, message)
    }
  }

  override fun logWarn(tag: String?, message: String?) {
    if (BuildConfig.DEBUG && message != null) {
      Log.w(tag, message)
    }
  }

  override fun logInfo(tag: String?, message: String?) {
    if (BuildConfig.DEBUG && message != null) {
      Log.i(tag, message)
    }
  }

  override fun logDebug(tag: String?, message: String?) {
    if (BuildConfig.DEBUG && message != null) {
      Log.d(tag, message)
    }
  }

  override fun logVerbose(tag: String?, message: String?) {
    if (BuildConfig.DEBUG && message != null) {
      Log.v(tag, message)
    }
  }

  override fun logStackTraceWithMessage(tag: String?, message: String?, e: Exception?) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, message + "\n" + Log.getStackTraceString(e))
    }
  }

  override fun logStackTrace(tag: String?, e: Exception?) {
    if (BuildConfig.DEBUG) {
      Log.e(tag, Log.getStackTraceString(e))
    }
  }

  override fun onScale(scale: Float): Float {
    return fontSize.toFloat()
  }

  override fun onSingleTapUp(e: MotionEvent) {
    showSoftInput()
  }

  override fun shouldBackButtonBeMappedToEscape(): Boolean {
    return false
  }

  override fun shouldEnforceCharBasedInput(): Boolean {
    return true
  }

  override fun shouldUseCtrlSpaceWorkaround(): Boolean {
    return false
  }

  override fun isTerminalViewSelected(): Boolean {
    return true
  }

  override fun copyModeChanged(copyMode: Boolean) {}

  override fun onKeyDown(keyCode: Int, e: KeyEvent, session: TerminalSession): Boolean {
    if (keyCode == KeyEvent.KEYCODE_ENTER && !session.isRunning) {
      finish()
      return true
    }
    return false
  }

  override fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean {
    return false
  }

  override fun onLongPress(event: MotionEvent): Boolean {
    return false
  }

  override fun readControlKey(): Boolean {
    val state = binding.virtualKeys.readSpecialButton(SpecialButton.CTRL, true)
    return state != null && state
  }

  override fun readAltKey(): Boolean {
    val state = binding.virtualKeys.readSpecialButton(SpecialButton.ALT, true)
    return state != null && state
  }

  override fun readShiftKey(): Boolean {
    return false
  }

  override fun readFnKey(): Boolean {
    return false
  }

  override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession): Boolean {
    return false
  }

  override fun onEmulatorSet() {
    setTerminalCursorBlinkingState(true)
    binding.root.setBackgroundColor(
      terminal.mTermSession.emulator.mColors.mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND]
    )
  }

  private fun setTerminalCursorBlinkingState(start: Boolean) {
    if (terminal.mEmulator != null) {
      terminal.setTerminalCursorBlinkerState(start, true)
    }
  }

  private fun showSoftInput() {
    terminal.requestFocus()
    KeyboardUtils.showSoftInput(terminal)
  }

  private class KeyListener(private val terminal: TerminalView) : IVirtualKeysView {
    override fun onVirtualKeyButtonClick(view: View, buttonInfo: VirtualKeyButton, button: Button) {
      if (buttonInfo.isMacro) {
        val keys = buttonInfo.key.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var ctrlDown = false
        var altDown = false
        var shiftDown = false
        var fnDown = false
        for (key in keys) {
          if (SpecialButton.CTRL.key == key) {
            ctrlDown = true
          } else if (SpecialButton.ALT.key == key) {
            altDown = true
          } else if (SpecialButton.SHIFT.key == key) {
            shiftDown = true
          } else if (SpecialButton.FN.key == key) {
            fnDown = true
          } else {
            onTerminalExtraKeyButtonClick(key, ctrlDown, altDown, shiftDown, fnDown)
            ctrlDown = false
            altDown = false
            shiftDown = false
            fnDown = false
          }
        }
      } else {
        onTerminalExtraKeyButtonClick(
          buttonInfo.key,
          ctrlDown = false,
          altDown = false,
          shiftDown = false,
          fnDown = false,
        )
      }
    }

    private fun onTerminalExtraKeyButtonClick(
      key: String,
      ctrlDown: Boolean,
      altDown: Boolean,
      shiftDown: Boolean,
      fnDown: Boolean,
    ) {
      if (VirtualKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS.containsKey(key)) {
        val keyCode = VirtualKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS[key] ?: return
        var metaState = 0
        if (ctrlDown) {
          metaState = metaState or (KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON)
        }
        if (altDown) {
          metaState = metaState or (KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON)
        }
        if (shiftDown) {
          metaState = metaState or (KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON)
        }
        if (fnDown) {
          metaState = metaState or KeyEvent.META_FUNCTION_ON
        }
        val keyEvent = KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState)
        terminal.onKeyDown(keyCode, keyEvent)
      } else {
        // not a control char
        var off = 0
        while (off < key.length) {
          val codePoint = key.codePointAt(off)
          terminal.inputCodePoint(codePoint, ctrlDown, altDown)
          off += Character.charCount(codePoint)
        }
      }
    }

    override fun performVirtualKeyButtonHapticFeedback(
      view: View,
      buttonInfo: VirtualKeyButton,
      button: Button,
    ): Boolean {
      // No need to handle this
      // VirtualKeysView will take care of performing haptic feedback
      return false
    }
  }

  companion object {
    const val KEY_WORKING_DIRECTORY = "terminal_workingDirectory"
    const val KEY_PYTHON_FILE_PATH = "terminal_python_file"

    const val VIRTUAL_KEYS =
      ("[" +
        "\n  [" +
        "\n    \"ESC\"," +
        "\n    {" +
        "\n      \"key\": \"/\"," +
        "\n      \"popup\": \"\\\\\"" +
        "\n    }," +
        "\n    {" +
        "\n      \"key\": \"-\"," +
        "\n      \"popup\": \"|\"" +
        "\n    }," +
        "\n    \"HOME\"," +
        "\n    \"UP\"," +
        "\n    \"END\"," +
        "\n    \"PGUP\"" +
        "\n  ]," +
        "\n  [" +
        "\n    \"TAB\"," +
        "\n    \"CTRL\"," +
        "\n    \"ALT\"," +
        "\n    \"LEFT\"," +
        "\n    \"DOWN\"," +
        "\n    \"RIGHT\"," +
        "\n    \"PGDN\"" +
        "\n  ]" +
        "\n]")
  }
}
