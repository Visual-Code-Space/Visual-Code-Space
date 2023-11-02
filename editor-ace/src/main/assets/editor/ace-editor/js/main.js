ace.require("ace/ext/language_tools");
ace.require("ace/ext/emmet");
ace.require("ace/ext/elastic_tabstops_lite");
let modelist = ace.require("ace/ext/modelist");

let editor = ace.edit("editor-container");
editor.setTheme("ace/theme/github_dark");

let initialValue = editor.getValue();

VCSpace.setHasUndo(editor.session.getUndoManager().hasUndo());
VCSpace.setHasRedo(editor.session.getUndoManager().hasRedo());
VCSpace.setValue(editor.getValue());

function setLanguageFromFile(filePath) {
  let mode = modelist.getModeForPath(filePath).mode;
  editor.session.setMode(mode);
}

editor.session.on('change', function(e) {
  console.log("Content changed: ", e);
  VCSpace.onContentChange(initialValue, editor.getValue());
  VCSpace.setHasUndo(editor.session.getUndoManager().hasUndo());
  VCSpace.setHasRedo(editor.session.getUndoManager().hasRedo());
  VCSpace.setValue(editor.getValue());
  
  // Update cursor position here if necessary
  VCSpace.setCursorPosition(editor.getCursorPosition().row, editor.getCursorPosition().column);
});
