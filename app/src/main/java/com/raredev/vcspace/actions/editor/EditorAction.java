package com.raredev.vcspace.actions.editor;

import androidx.annotation.StringRes;
import com.raredev.vcspace.actions.Action;
import com.raredev.vcspace.actions.ActionEvent;
import com.raredev.vcspace.actions.ActionPlaces;
import com.raredev.vcspace.actions.Presentation;

public abstract class EditorAction extends Action {

  @Override
  public void update(ActionEvent event) {
    super.update(event);
    Presentation presentation = event.getPresentation();
    presentation.setVisible(false);

    if (!event.getPlace().equals(ActionPlaces.EDITOR)) {
      return;
    }

    presentation.setVisible(true);
    presentation.setTitle(getTitle());
  }

  @StringRes
  public abstract int getTitle();
}
