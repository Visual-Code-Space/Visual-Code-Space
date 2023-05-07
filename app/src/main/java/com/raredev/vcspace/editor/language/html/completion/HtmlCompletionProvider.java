package com.raredev.vcspace.editor.language.html.completion;

import com.blankj.utilcode.util.GsonUtils;
import com.raredev.vcspace.VCSpaceApplication;
import com.raredev.vcspace.editor.IDECodeEditor;
import com.raredev.vcspace.editor.completion.CompletionParams;
import com.raredev.vcspace.editor.completion.CompletionProvider;
import com.raredev.vcspace.editor.completion.SimpleCompletionIconDrawer;
import com.raredev.vcspace.editor.completion.SimpleCompletionItem;
import com.raredev.vcspace.editor.completion.SimpleCompletionItemKind;
import com.raredev.vcspace.editor.completion.SimpleSnippetCompletionItem;
import com.raredev.vcspace.editor.completion.VCSpaceCompletionItem;
import com.raredev.vcspace.util.FileUtil;
import io.github.rosemoe.sora.lang.completion.SnippetDescription;
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser;
import io.github.rosemoe.sora.text.Cursor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.DOMParser;

public class HtmlCompletionProvider extends CompletionProvider {

  private HtmlCompletion completion;

  public HtmlCompletionProvider() {
    /*Gson gson = new Gson();
    completion =
        gson.fromJson(
            FileUtil.readAssetFile(
                VCSpaceApplication.getInstance(), "textmate/html/completions.json"),
            HtmlCompletion.class);*/
    
    completion =
        GsonUtils.fromJson(
            FileUtil.readAssetFile(
                VCSpaceApplication.getInstance(), "textmate/html/completions.json"),
            HtmlCompletion.class);
  }

  @Override
  public List<VCSpaceCompletionItem> getCompletions(CompletionParams params) {
    String prefix = params.getPrefix();

    List<VCSpaceCompletionItem> completions = new ArrayList<>();
    if (completion == null) {
      return completions;
    }

    IDECodeEditor editor = params.getEditor();

    DOMDocument document = DOMParser.getInstance().parse(params.getContent(), "", null);

    if (prefix.startsWith("<") || prefix.startsWith("</")) {
      tagCompletions(completions, params);
    } else if (prefix.startsWith("\"")) {
      attribOptionCompletiobs(completions, params, document);
    } else {
      attributesCompletions(completions, params, document);
    }
    return completions;
  }

  private void tagCompletions(List<VCSpaceCompletionItem> completions, CompletionParams params) {
    IDECodeEditor editor = params.getEditor();
    String prefix = params.getPrefix();

    Map<String, HtmlCompletion.HTMLTag> tags = completion.getTags();
    for (String key : tags.keySet()) {
      HtmlCompletion.HTMLTag tag = tags.get(key);
      String tagString = "<" + key;
      if (prefix.startsWith("</")) {
        tagString = "</" + key;
      }
      if (tagString.startsWith(prefix) && completions.size() <= 20) {
        completions.add(
            new SimpleCompletionItem(
                key,
                tag.getDescription(),
                "Tag",
                SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.TAG),
                prefix.length(),
                tagString));
      }
    }
  }

  private void attributesCompletions(
      List<VCSpaceCompletionItem> completions, CompletionParams params, DOMDocument document) {
    IDECodeEditor editor = params.getEditor();
    String prefix = params.getPrefix();

    Cursor cursor = editor.getCursor();

    DOMNode nodeAt = document.findNodeAt(cursor.getLeft());
    String tagAt = nodeAt.getNodeName();

    if (nodeAt != null && tagAt != null && prefix.length() > 0) {
      HtmlCompletion.HTMLTag htmlTag = completion.getTags().get(tagAt);
      if (htmlTag == null) {
        return;
      }

      String[] atrributes = htmlTag.getAttributes();

      if (atrributes != null && atrributes.length > 0) {
        for (String attr : atrributes) {
          if (attr.startsWith(prefix) && completions.size() <= 20) {
            completions.add(
                new SimpleSnippetCompletionItem(
                    attr,
                    htmlTag.getDescription(),
                    "Attribute",
                    SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.ATTRIBUTE),
                    new SnippetDescription(
                        prefix.length(), CodeSnippetParser.parse(attr + "=\"$0\""), true)));
          }
        }
      }
    }
  }

  public void attribOptionCompletiobs(
      List<VCSpaceCompletionItem> completions, CompletionParams params, DOMDocument document) {
    IDECodeEditor editor = params.getEditor();
    String prefix = params.getPrefix();

    Cursor cursor = editor.getCursor();

    var attr = document.findAttrAt(cursor.getLeft());
    var ownerElement = attr.getOwnerElement();

    if (attr != null) {
      HtmlCompletion.Attribute attribute = completion.getAttributes().get(attr.getName());
      if (attribute == null) {
        return;
      }

      String[] values = attribute.getAttribOption();
      if (values != null) {
        for (String value : values) {
          String valueInsert = "\"" + value;

          if (valueInsert.startsWith(prefix) && completions.size() <= 20) {
            String type = attribute.getType();
            if (type == null) {
              type = "Value";
            }
            completions.add(
                new SimpleCompletionItem(
                    value,
                    attribute.getDescription(),
                    type,
                    SimpleCompletionIconDrawer.draw(SimpleCompletionItemKind.VALUE),
                    prefix.length(),
                    valueInsert));
          }
        }
      }
    }
  }
}
