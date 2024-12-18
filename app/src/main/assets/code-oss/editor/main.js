let editor;

require.config({
    paths: {
        vs: "https://appassets.androidplatform.net/assets/code-oss/editor/monaco-editor/min/vs",
    },
});

window.MonacoEnvironment = {
    getWorkerUrl: () => proxy,
};

let proxy = URL.createObjectURL(new Blob([`
    self.MonacoEnvironment = {
        baseUrl: 'https://appassets.androidplatform.net/assets/code-oss/editor/monaco-editor/min/'
    };
    importScripts('https://appassets.androidplatform.net/assets/code-oss/editor/monaco-editor/min/vs/base/worker/workerMain.js');
`], { type: 'text/javascript' }));


// Create the editor
require(["vs/editor/editor.main"], function () {
    editor = monaco.editor.create(document.getElementById("container"), {
        value: "",
        language: "plaintext",
        theme: "vs-dark",
        fontFamily: 'JetBrains Mono'
    });

    window.MonacoAndroid.setCanUndo(editor.getModel().canUndo());
    window.MonacoAndroid.setCanRedo(editor.getModel().canRedo());

    editor.onDidChangeModelContent(() => {
        if (window.MonacoAndroid && window.MonacoAndroid.onTextChanged) {
            const content = editor.getValue();
            const model = editor.getModel();
            const isModified = model.getAlternativeVersionId() !== model.getVersionId();
            
            window.MonacoAndroid.setModified(isModified);
            window.MonacoAndroid.setValue(content);
            window.MonacoAndroid.setCanUndo(editor.getModel().canUndo());
            window.MonacoAndroid.setCanRedo(editor.getModel().canRedo());
            window.MonacoAndroid.onTextChanged(content);
        }
    });
});

function setText(content) {
    if (editor) {
        editor.setValue(content);
    }
}

function setLanguage(language) {
    if (editor) {
        monaco.editor.setModelLanguage(editor.getModel(), language);
    }
}

function setTheme(theme) {
    if (editor) {
        monaco.editor.setTheme(theme);
    }
}

function focusEditor() {
    if (editor) {
        editor.focus();
    }
}

function setEditorOptions(option, value) {
    if (editor) {
        const options = {};
        options[option] = value;
        editor.updateOptions(options);
    }
}

function setCursorStyle(styleValue) {
    const cursorStyleMap = {
        1: 'line',
        2: 'block',
        3: 'underline',
        4: 'line-thin',
        5: 'block-outline',
        6: 'underline-thin'
    };
    const cursorStyle = cursorStyleMap[styleValue];
    if (cursorStyle) {
        editor.updateOptions({ cursorStyle });
    } else {
        console.error('Invalid cursor style value:', styleValue);
    }
}

function applyMinimapOptions(optionsJson) {
    try {
        const options = JSON.parse(optionsJson);
        editor.updateOptions({
            minimap: {
                enabled: options.enabled,
                autohide: options.autohide,
                side: options.side,
                size: options.size,
                showSlider: options.showSlider,
                renderCharacters: options.renderCharacters,
                maxColumn: options.maxColumn,
                scale: options.scale,
                showRegionSectionHeaders: options.showRegionSectionHeaders,
                showMarkSectionHeaders: options.showMarkSectionHeaders,
                sectionHeaderFontSize: options.sectionHeaderFontSize,
                sectionHeaderLetterSpacing: options.sectionHeaderLetterSpacing,
            },
        });
    } catch (error) {
        console.error("Error applying minimap options:", error);
    }
}

function undo() {
    if (editor) {
        editor.trigger('keyboard', 'undo');
    }
}

function redo() {
    if (editor) {
        editor.trigger('keyboard', 'redo');
    }
}

