package com.raredev.vcspace.ui.editor.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.raredev.vcspace.R;

import io.github.rosemoe.sora.widget.CodeEditor;

/*
 * @author Rosemoe
 */

public class SymbolInputView extends LinearLayout {

    private CodeEditor editor;

    public SymbolInputView(Context context) {
        super(context);
        init();
    }

    public SymbolInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SymbolInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void bindEditor(CodeEditor editor) {
        this.editor = editor;
    }

    public void removeSymbols() {
        removeAllViews();
    }
    
    public void addSymbols(@NonNull String[] display, @NonNull final String[] insertText) {
        int count = Math.max(display.length, insertText.length);
        for (int i = 0; i < count; i++) {
            var btn = new Button(getContext(), null, android.R.attr.buttonStyleSmall);
            btn.setText(display[i]);
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            btn.setBackgroundResource(outValue.resourceId);
            addView(btn, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            int finalI = i;
            btn.setOnClickListener((view) -> {
                if (editor != null && editor.isEditable()) {
                    if ("\t".equals(insertText[finalI]) && editor.getSnippetController().isInSnippet()) {
                        editor.getSnippetController().shiftToNextTabStop();
                    } else {
                        editor.commitText(insertText[finalI]);
                    }
                }
            });
        }
    }

    public void forEachButton(@NonNull ButtonConsumer consumer) {
        for (int i = 0; i < getChildCount(); i++) {
            consumer.accept((Button) getChildAt(i));
        }
    }

    public interface ButtonConsumer {

        void accept(@NonNull Button btn);

    }

}
