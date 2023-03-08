package com.raredev.vcspace.ui.editor.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.raredev.vcspace.databinding.LayoutSearcherBinding;

import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.EditorSearcher;

public class VCSpaceSearcher extends LinearLayout {
    private LayoutSearcherBinding mBinding;
    private CodeEditor editor;
    private MenuItem item;
    
    public boolean isShowing = false;
    
    public VCSpaceSearcher(Context context) {
        super(context);
        mBinding = LayoutSearcherBinding.inflate(LayoutInflater.from(context));
        
        mBinding.searchEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    editor.getSearcher().search(editable.toString(), new EditorSearcher.SearchOptions(true, true));
                } else {
                    editor.getSearcher().stopSearch();
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        
        mBinding.gotoLast.setOnClickListener((v) -> {
            gotoLast();
        });
        
        mBinding.gotoNext.setOnClickListener((v) -> {
            gotoNext();
        });
        
        mBinding.replace.setOnClickListener((v) -> {
            replace();
        });
        
        mBinding.replaceAll.setOnClickListener((v) -> {
            replaceAll();
        });

        addView(mBinding.getRoot(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
    public VCSpaceSearcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBinding = LayoutSearcherBinding.inflate(LayoutInflater.from(context));
        addView(mBinding.getRoot(), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
    public void bindEditor(CodeEditor editor) {
        this.editor = editor;
    }
    
    public void bindMenu(MenuItem item) {
        this.item = item;
    }
    
    public void showAndHide() {
        if(isShowing) {
            setVisibility(View.GONE);
            item.setChecked(false);
            editor.getSearcher().stopSearch();
            isShowing = false;
        } else {
            setVisibility(View.VISIBLE);
            mBinding.replaceEditor.setText("");
            mBinding.searchEditor.setText("");
            editor.getSearcher().stopSearch();
            item.setChecked(true);
            isShowing = true;
        }
        
        mBinding.searchEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    editor.getSearcher().search(editable.toString(), new EditorSearcher.SearchOptions(true, true));
                } else {
                    editor.getSearcher().stopSearch();
                }
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
        
        mBinding.gotoLast.setOnClickListener((v) -> {
            gotoLast();
        });
        
        mBinding.gotoNext.setOnClickListener((v) -> {
            gotoNext();
        });
        
        mBinding.replace.setOnClickListener((v) -> {
            replace();
        });
        
        mBinding.replaceAll.setOnClickListener((v) -> {
            replaceAll();
        });
        
    }
    
    private void gotoLast() {
        try {
            editor.getSearcher().gotoPrevious();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    private void gotoNext() {
        try {
            editor.getSearcher().gotoNext();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    private void replace() {
        try {
            editor.getSearcher().replaceThis(mBinding.replaceEditor.getText().toString());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    
    private void replaceAll() {
        try {
            editor.getSearcher().replaceAll(mBinding.replaceEditor.getText().toString());
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}