package com.raredev.vcspace.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import com.blankj.utilcode.util.EncodeUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;
import com.raredev.vcspace.databinding.FragmentEditorDrawerBinding;
import com.raredev.vcspace.res.R;
import com.raredev.vcspace.ui.panels.Panel;
import com.raredev.vcspace.ui.panels.PanelArea;
import com.raredev.vcspace.ui.panels.PanelAreaListener;
import com.raredev.vcspace.ui.panels.file.FileExplorerPanel;
import com.raredev.vcspace.util.PanelUtils;
import java.util.List;

@Deprecated
public class EditorDrawerFragment extends Fragment {

  private static final String RECENT_PANELS_PATH =
      PathUtils.getExternalAppDataPath() + "/files/recentPanels/editorDrawerPanels.json";

  private FragmentEditorDrawerBinding binding;

  private PanelArea panelArea;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentEditorDrawerBinding.inflate(inflater, container, false);
    panelArea = new PanelArea(requireContext(), binding.root);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    panelArea.setPanelAreaListener(
        new PanelAreaListener() {
          @Override
          public PopupMenu createTabPopupMenu(Panel panel, View v) {
            final var pm = new PopupMenu(requireActivity(), v);
            pm.getMenu().add(R.string.close);
            pm.getMenu().add(R.string.close_others);
            pm.getMenu().add(R.string.close_all);
            pm.getMenu().add(panel.isPinned() ? R.string.unpin : R.string.pin);

            pm.setOnMenuItemClickListener(
                item -> {
                  if (item.getTitle().equals(getString(R.string.close))) {
                    panelArea.removePanel(panel);
                  } else if (item.getTitle().equals(getString(R.string.close_others))) {
                    panelArea.removeOthers();
                  } else if (item.getTitle().equals(getString(R.string.close_all))) {
                    panelArea.removeAllPanels();
                  } else if (item.getTitle().equals(getString(R.string.pin))
                      || item.getTitle().equals(getString(R.string.unpin))) {
                    panel.setPinned(!panel.isPinned());
                    item.setTitle(panel.isPinned() ? R.string.unpin : R.string.pin);
                    panelArea.updateTabs();
                  }
                  return true;
                });
            return pm;
          }

          @Override
          public void addAvailablePanels(PanelArea panelArea, Menu menu) {
            menu.add("File Explorer")
                .setOnMenuItemClickListener(
                    item -> {
                      panelArea.addPanel(new FileExplorerPanel(requireContext()), true);
                      return true;
                    });
          }

          @Override
          public void addPanel(Panel panel) {}

          @Override
          public void selectedPanel(Panel panel) {}

          @Override
          public void removedPanel(PanelArea panelArea) {}

          @Override
          public void removedPanel(Panel panel) {}
        });
    openRecentPanels();

    if (panelArea.getPanels().isEmpty()) {
      panelArea.addPanel(new FileExplorerPanel(requireActivity()), true);
    }
    panelArea.addPanelTopBarButtons();
  }

  @Override
  public void onPause() {
    savePanels();
    super.onPause();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void savePanels() {
    try {
      String json = PanelUtils.panelsToJson(panelArea.getPanels());
      FileIOUtils.writeFileFromString(
          RECENT_PANELS_PATH, EncodeUtils.base64Encode2String(json.getBytes()));
    } catch (Exception err) {
      err.printStackTrace();
    }
  }

  private void openRecentPanels() {
    try {
     /* List<Panel> panels =
          PanelUtils.jsonToPanels(
              requireContext(),
              new String(
                  EncodeUtils.base64Decode(FileIOUtils.readFile2String(RECENT_PANELS_PATH))));
      for (Panel panel : panels) {
        panelArea.addPanel(panel, false);
      }
      if (!panelArea.getPanels().isEmpty()) {
        panelArea.setSelectedPanel(panelArea.getPanels().get(0));
      }*/
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
