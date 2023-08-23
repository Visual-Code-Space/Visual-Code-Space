package com.raredev.vcspace.ui.panels.editor;

import android.content.Context;
import android.view.LayoutInflater;
import com.raredev.vcspace.activity.EditorActivity;
import com.raredev.vcspace.databinding.LayoutWelcomePanelBinding;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.Panel;

public class WelcomePanel extends Panel {

  private LayoutWelcomePanelBinding binding;

  public WelcomePanel(Context context) {
    super(context);
    setTitle(getContext().getString(R.string.welcome));
    binding = LayoutWelcomePanelBinding.inflate(LayoutInflater.from(getContext()));

    binding.newFile.setOnClickListener(v -> newFile());

    binding.openFile.setOnClickListener(v -> openFile());
    setContentView(binding.getRoot());
  }

  @Override
  public void destroy() {
    binding = null;
  }

  @Override
  public void unselected() {}

  @Override
  public void selected() {}

  private void newFile() {
    Context context = getContext();
    if (context instanceof EditorActivity) {
      EditorActivity activity = (EditorActivity) context;
      activity.createFile.launch("untitled");
    }
  }

  private void openFile() {
    Context context = getContext();
    if (context instanceof EditorActivity) {
      EditorActivity activity = (EditorActivity) context;
      activity.pickFile.launch("text/*");
    }
  }
}
