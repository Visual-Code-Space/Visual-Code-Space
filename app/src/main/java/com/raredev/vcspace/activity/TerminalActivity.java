package com.raredev.vcspace.activity;

import android.view.View;
import com.raredev.vcspace.databinding.ActivityTerminalBinding;

public class TerminalActivity extends VCSpaceActivity
    /*implements TerminalViewClient, TerminalSessionClient */{
  private ActivityTerminalBinding binding;

  //private TerminalSession session;
  
  @Override
  public View getLayout() {
    binding = ActivityTerminalBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate() {
    /*TerminalView terminal = binding.terminal;
    terminal.setTerminalViewClient(this);

    terminal.attachSession(createSession());
    terminal.setKeepScreenOn(true);
    terminal.setTextSize(Utils.pxToDp(14));*/
  }
  
  /*private TerminalSession createSession() {
    session =
        new TerminalSession(
            "/system/bin/sh",
            Environment.getExternalStorageDirectory().getAbsolutePath(),
            new String[] {},
            new String[] {"HOME=/data/data/com.raredev.vcspace/files/", "SYSROOT=" + getDataDir().getAbsolutePath(), "TERMUX_APP_PACKAGE_MANAGER=apt", "TERMUX_PKG_NO_MIRROR_SELECT=true"},
            TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
            this);
    try {
      final var file = new File(getDataDir(), "etc/apt/sources.list");
      final var out = new FileOutputStream(file);
      out.write(SOURCES_LIST_CONTENT);
      out.flush();
      out.close();
    } catch (Throwable th) {
      ILogger.error("", "Unable to update sources.list");
    }
    return session;
  }

  @Override
  public void onTextChanged(TerminalSession changedSession) {
    binding.terminal.onScreenUpdated();
  }

  @Override
  public void onTitleChanged(TerminalSession changedSession) {}

  @Override
  public void onSessionFinished(TerminalSession finishedSession) {
    finish();
  }

  @Override
  public void onCopyTextToClipboard(TerminalSession session, String text) {
    ClipboardUtils.copyText("VCSpace Terminal", text);
  }

  @Override
  public void onPasteTextFromClipboard(TerminalSession session) {
    String clip = ClipboardUtils.getText().toString();
    if (clip.trim().length() > 0 && binding.terminal.mEmulator != null) {
      binding.terminal.mEmulator.paste(clip);
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
    return false;
  }

  @Override
  public boolean readAltKey() {
    return false;
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
    if (binding.terminal.mEmulator != null) {
      binding.terminal.setTerminalCursorBlinkerState(start, true);
    }
  }

  private void showSoftInput() {
    binding.terminal.requestFocus();
    KeyboardUtils.showSoftInput(binding.terminal);
  }*/
}
