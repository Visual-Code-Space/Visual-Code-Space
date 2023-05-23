package com.raredev.vcspace.activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import com.raredev.vcspace.databinding.ActivityMarkdownViewBinding;
import io.noties.markwon.Markwon;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

public class MarkdownViewActivity extends BaseActivity {
  public static final String EXTRA_MARKDOWN = "markdown";

  private ActivityMarkdownViewBinding binding;

  @Override
  public View getLayout() {
    binding = ActivityMarkdownViewBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setSupportActionBar(binding.toolbar);
    binding.toolbar.setNavigationOnClickListener((v) -> onBackPressed());

    if (getIntent() == null) binding.markdownText.setText(":)");
    else {
      var markdown = getIntent().getStringExtra(EXTRA_MARKDOWN);
      var markwon =
          Markwon.builder(this)
              .usePlugin(LinkifyPlugin.create())
              .usePlugin(ImagesPlugin.create())
              .usePlugin(GlideImagesPlugin.create(this))
              .build();

      binding.markdownText.setMovementMethod(LinkMovementMethod.getInstance());
      binding.markdownText.setText(markwon.toMarkdown(markdown));
    }
  }
}
