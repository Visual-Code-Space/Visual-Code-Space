package com.raredev.vcspace.activity;

import static com.raredev.vcspace.util.Environment.getEnvironment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.terminal.TerminalEmulator;
import com.raredev.terminal.TerminalSession;
import com.raredev.terminal.TerminalSessionClient;
import com.raredev.terminal.TextStyle;
import com.raredev.terminal.view.TerminalView;
import com.raredev.terminal.view.TerminalViewClient;
import com.raredev.vcspace.databinding.ActivityTerminalBinding;
import com.raredev.vcspace.ui.virtualkeys.SpecialButton;
import com.raredev.vcspace.ui.virtualkeys.VirtualKeyButton;
import com.raredev.vcspace.ui.virtualkeys.VirtualKeysConstants;
import com.raredev.vcspace.ui.virtualkeys.VirtualKeysInfo;
import com.raredev.vcspace.ui.virtualkeys.VirtualKeysView;
import com.raredev.vcspace.util.ILogger;
import com.raredev.vcspace.util.PreferencesUtils;
import com.raredev.vcspace.util.Utils;
import java.util.Map;
import org.json.JSONException;

/*
 *  @see <a href="https://github.com/AndroidIDEOfficial/AndroidIDE/blob/dev/app/src/main/java/com/itsaky/androidide/activities/TerminalActivity.java">TerminalActivity</a>
 */

public class TerminalActivity extends BaseActivity
    implements TerminalViewClient, TerminalSessionClient {

  public static final String KEY_WORKING_DIRECTORY = "terminal_workingDirectory";
  public static final String KEY_EXECUTE_SH = "terminal_executeSh";
  private ActivityTerminalBinding binding;

  private TerminalSession session;
  private TerminalView terminal;
  private KeyListener listener;

  public static void startTerminalWithDir(Context context, String path) {
    Intent it = new Intent(context, TerminalActivity.class);
    it.putExtra(KEY_WORKING_DIRECTORY, path);
    context.startActivity(it);
  }

  @Override
  public View getLayout() {
    binding = ActivityTerminalBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setStatusBarColor(Color.BLACK);
    getWindow().setNavigationBarColor(Color.BLACK);
    setupTerminalView();
  }

  @Override
  protected void onResume() {
    super.onResume();
    showSoftInput();
    setTerminalCursorBlinkingState(true);
  }

  @Override
  protected void onStop() {
    super.onStop();
    setTerminalCursorBlinkingState(false);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    binding = null;
  }

  private void setupTerminalView() {
    terminal = new TerminalView(this, null);
    terminal.setTerminalViewClient(this);
    terminal.attachSession(createSession());
    terminal.setKeepScreenOn(true);
    terminal.setTextSize(Utils.pxToDp(14));
    terminal.setTypeface(ResourcesCompat.getFont(this, PreferencesUtils.getSelectedFont()));

    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, 0);
    params.weight = 1f;

    binding.getRoot().addView(terminal, 0, params);
    try {
      binding.virtualKeys.setVirtualKeysViewClient(getKeyListener());
      binding.virtualKeys.reload(
          new VirtualKeysInfo(VIRTUAL_KEYS, "", VirtualKeysConstants.CONTROL_CHARS_ALIASES));
    } catch (JSONException e) {
      ILogger.error("TerminalActivity", "Unable to parse terminal virtual keys json data", e);
    }
  }

  private KeyListener getKeyListener() {
    return listener == null ? listener = new KeyListener(terminal) : listener;
  }

  private TerminalSession createSession() {
    final Map<String, String> environment = getEnvironment();
    final String[] env = new String[environment.size()];
    int i = 0;
    for (Map.Entry<String, String> entry : environment.entrySet()) {
      env[i] = entry.getKey() + "=" + entry.getValue();
      i++;
    }
    session =
        new TerminalSession(
            "/system/bin/sh",
            getWorkingDirectory(),
            new String[] {},
            env,
            TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            this);
    return session;
  }

  @NonNull
  private String getWorkingDirectory() {
    final Bundle extras = getIntent().getExtras();
    if (extras != null && extras.containsKey(KEY_WORKING_DIRECTORY)) {
      String directory = extras.getString(KEY_WORKING_DIRECTORY, null);
      if (directory == null || directory.trim().length() <= 0) {
        directory = PathUtils.getRootPathExternalFirst();
      }
      return directory;
    }
    return PathUtils.getRootPathExternalFirst();
  }

  @Override
  public void onTextChanged(TerminalSession changedSession) {
    terminal.onScreenUpdated();
  }

  @Override
  public void onTitleChanged(TerminalSession changedSession) {}

  @Override
  public void onSessionFinished(TerminalSession finishedSession) {
    // finish();
  }

  @Override
  public void onCopyTextToClipboard(TerminalSession session, String text) {
    ClipboardUtils.copyText("VCSpace Terminal", text);
  }

  @Override
  public void onPasteTextFromClipboard(TerminalSession session) {
    String clip = ClipboardUtils.getText().toString();
    if (clip.trim().length() > 0 && terminal.mEmulator != null) {
      terminal.mEmulator.paste(clip);
    }
  }

  @Override
  public void onBell(TerminalSession session) {}

  @Override
  public void onColorsChanged(TerminalSession session) {}

  @Override
  public void onTerminalCursorStateChange(boolean state) {}

  @Override
  public Integer getTerminalCursorStyle() {
    return TerminalEmulator.DEFAULT_TERMINAL_CURSOR_STYLE;
  }

  @Override
  public void logError(String tag, String message) {
    ILogger.error(tag, message);
  }

  @Override
  public void logWarn(String tag, String message) {
    ILogger.warning(tag, message);
  }

  @Override
  public void logInfo(String tag, String message) {
    ILogger.info(tag, message);
  }

  @Override
  public void logDebug(String tag, String message) {
    ILogger.debug(tag, message);
  }

  @Override
  public void logVerbose(String tag, String message) {
    ILogger.verbose(tag, message);
  }

  @Override
  public void logStackTraceWithMessage(String tag, String message, Exception e) {
    ILogger.warning(tag, Log.getStackTraceString(e));
  }

  @Override
  public void logStackTrace(String tag, Exception e) {
    ILogger.warning(tag, Log.getStackTraceString(e));
  }

  @Override
  public float onScale(float scale) {
    return 14;
  }

  @Override
  public void onSingleTapUp(MotionEvent e) {
    showSoftInput();
  }

  @Override
  public boolean shouldBackButtonBeMappedToEscape() {
    return false;
  }

  @Override
  public boolean shouldEnforceCharBasedInput() {
    return true;
  }

  @Override
  public boolean shouldUseCtrlSpaceWorkaround() {
    return false;
  }

  @Override
  public boolean isTerminalViewSelected() {
    return true;
  }

  @Override
  public void copyModeChanged(boolean copyMode) {}

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
    if (keyCode == KeyEvent.KEYCODE_ENTER && !session.isRunning()) {
      finish();
      return true;
    }
    return false;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent e) {
    return false;
  }

  @Override
  public boolean onLongPress(MotionEvent event) {
    return false;
  }

  @Override
  public boolean readControlKey() {
    Boolean state = binding.virtualKeys.readSpecialButton(SpecialButton.CTRL, true);
    return state != null && state;
  }

  @Override
  public boolean readAltKey() {
    Boolean state = binding.virtualKeys.readSpecialButton(SpecialButton.ALT, true);
    return state != null && state;
  }

  @Override
  public boolean readShiftKey() {
    return false;
  }

  @Override
  public boolean readFnKey() {
    return false;
  }

  @Override
  public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
    return false;
  }

  @Override
  public void onEmulatorSet() {
    setTerminalCursorBlinkingState(true);
    if (session != null) {
      binding
          .getRoot()
          .setBackgroundColor(
              session.getEmulator().mColors.mCurrentColors[TextStyle.COLOR_INDEX_BACKGROUND]);
    }
  }

  private void setTerminalCursorBlinkingState(boolean start) {
    if (terminal.mEmulator != null) {
      terminal.setTerminalCursorBlinkerState(start, true);
    }
  }

  private void showSoftInput() {
    terminal.requestFocus();
    KeyboardUtils.showSoftInput(terminal);
  }

  private static final class KeyListener implements VirtualKeysView.IVirtualKeysView {

    private final TerminalView terminal;

    public KeyListener(TerminalView terminal) {
      this.terminal = terminal;
    }

    @Override
    public void onVirtualKeyButtonClick(View view, VirtualKeyButton buttonInfo, Button button) {
      if (terminal == null) {
        return;
      }
      if (buttonInfo.isMacro()) {
        String[] keys = buttonInfo.getKey().split(" ");
        boolean ctrlDown = false;
        boolean altDown = false;
        boolean shiftDown = false;
        boolean fnDown = false;
        for (String key : keys) {
          if (SpecialButton.CTRL.getKey().equals(key)) {
            ctrlDown = true;
          } else if (SpecialButton.ALT.getKey().equals(key)) {
            altDown = true;
          } else if (SpecialButton.SHIFT.getKey().equals(key)) {
            shiftDown = true;
          } else if (SpecialButton.FN.getKey().equals(key)) {
            fnDown = true;
          } else {
            onTerminalExtraKeyButtonClick(key, ctrlDown, altDown, shiftDown, fnDown);
            ctrlDown = false;
            altDown = false;
            shiftDown = false;
            fnDown = false;
          }
        }
      } else {
        onTerminalExtraKeyButtonClick(buttonInfo.getKey(), false, false, false, false);
      }
    }

    private void onTerminalExtraKeyButtonClick(
        String key, boolean ctrlDown, boolean altDown, boolean shiftDown, boolean fnDown) {
      if (VirtualKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS.containsKey(key)) {
        Integer keyCode = VirtualKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS.get(key);
        if (keyCode == null) {
          return;
        }
        int metaState = 0;
        if (ctrlDown) {
          metaState |= KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON;
        }
        if (altDown) {
          metaState |= KeyEvent.META_ALT_ON | KeyEvent.META_ALT_LEFT_ON;
        }
        if (shiftDown) {
          metaState |= KeyEvent.META_SHIFT_ON | KeyEvent.META_SHIFT_LEFT_ON;
        }
        if (fnDown) {
          metaState |= KeyEvent.META_FUNCTION_ON;
        }

        KeyEvent keyEvent = new KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState);
        terminal.onKeyDown(keyCode, keyEvent);
      } else {
        // not a control char
        for (int off = 0; off < key.length(); ) {
          int codePoint = key.codePointAt(off);
          terminal.inputCodePoint(codePoint, ctrlDown, altDown);
          off += Character.charCount(codePoint);
        }
      }
    }

    @Override
    public boolean performVirtualKeyButtonHapticFeedback(
        View view, VirtualKeyButton buttonInfo, Button button) {
      // No need to handle this
      // VirtualKeysView will take care of performing haptic feedback
      return false;
    }
  }

  public static final String VIRTUAL_KEYS =
      "["
          + "\n  ["
          + "\n    \"ESC\","
          + "\n    {"
          + "\n      \"key\": \"/\","
          + "\n      \"popup\": \"\\\\\""
          + "\n    },"
          + "\n    {"
          + "\n      \"key\": \"-\","
          + "\n      \"popup\": \"|\""
          + "\n    },"
          + "\n    \"HOME\","
          + "\n    \"UP\","
          + "\n    \"END\","
          + "\n    \"PGUP\""
          + "\n  ],"
          + "\n  ["
          + "\n    \"TAB\","
          + "\n    \"CTRL\","
          + "\n    \"ALT\","
          + "\n    \"LEFT\","
          + "\n    \"DOWN\","
          + "\n    \"RIGHT\","
          + "\n    \"PGDN\""
          + "\n  ]"
          + "\n]";
}
