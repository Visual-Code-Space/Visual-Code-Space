package com.raredev.vcspace.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.raredev.vcspace.fragments.FileManagerFragment;
import com.raredev.vcspace.fragments.GitToolsFragment;

public class ToolsPagerAdapter extends FragmentStateAdapter {

  public ToolsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
  }

  @Override
  public int getItemCount() {
    return 2;
  }

  @Override
  public Fragment createFragment(int position) {
    switch (position) {
      case 0:
        return new FileManagerFragment();
      case 1:
        return new GitToolsFragment();
    }
    return null;
  }
}
