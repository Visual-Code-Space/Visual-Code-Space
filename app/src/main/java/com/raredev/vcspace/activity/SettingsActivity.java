package com.raredev.vcspace.activity;

import android.view.View;
import com.raredev.vcspace.R;
import com.raredev.vcspace.databinding.ActivitySettingsBinding;

public class SettingsActivity extends VCSpaceActivity {
    private ActivitySettingsBinding mBinding;
    
    @Override
    public void findBinding() {
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
    }
    
    @Override
    public View getLayout() {
        return mBinding.getRoot();
    }
    
    @Override
    public void onCreate() {
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.menu_settings);
        mBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    
}
