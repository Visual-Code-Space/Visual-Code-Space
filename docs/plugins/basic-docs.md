
# Creating Plugins for Visual Code Space (VCSpace) - Beginner's Guide

This guide provides a comprehensive introduction to creating plugins for VCSpace. It's designed for beginners with basic Java knowledge.

## What are Plugins?

Plugins are small, independent programs that add new features or extend the functionality of an existing application. In VCSpace, plugins allow you to customize the editor to fit your specific needs.

## Plugin Structure and Key Components

VCSpace plugins are written in Java and interact with the application through defined interfaces. Here's a breakdown of the core components:

### 1. The `Plugin` Interface

This is the main entry point for your plugin. Your plugin class *must* implement this interface. It contains a single method:

```java
package com.vcspace.plugins;

import androidx.annotation.NonNull;

public interface Plugin {
    void onPluginLoaded(@NonNull PluginContext context);
}
```

-   `onPluginLoaded(@NonNull PluginContext context)`: This method is called when your plugin is loaded into VCSpace. The `PluginContext` object provides access to VCSpace's functionalities.

### 2. The `PluginContext` Interface

This interface is crucial for plugin development. It provides methods to interact with VCSpace:

```java
package com.vcspace.plugins;

// ... imports

public interface PluginContext {
    Context getAppContext(); // Get the application context
    Editor getEditor();      // Get the editor instance
    void registerCommand(EditorCommand command); // Register custom commands
    void addMenu(MenuItem menuItem);         // Add menu items
    void openFile(File file);               // Open a file
    void openFile(String filePath);         // Open file by path
    void setCursorPosition(Position position); // Set cursor position
    void addMenu(String title, int id, MenuAction action); // Add menu with action
    void log(String message);              // Log messages (for debugging)
}
```

### 3. The `Editor` Interface

This interface lets you interact with the editor itself:

```java
package com.vcspace.plugins;

// ... imports

public interface Editor {
    File getCurrentFile();    // Get the currently open file
    Context getContext();       // Get the application context
    Position getCursorPosition();// Get the cursor position
    void setCursorPosition(Position position); // Set the cursor position
}
```

### 4. The `Position` Class

Represents a position in the editor (line and column):

```java
package com.vcspace.plugins.editor;

public class Position {
    private final int lineNumber;
    private final int column;

    public Position(int lineNumber, int column) {
        this.lineNumber = lineNumber;
        this.column = column;
    }

    public int getLineNumber();
    public int getColumn();
}
```

### 5. `MenuAction` and `MenuItem` for Menu Integration

These are used for adding items to VCSpace's menu:

```java
package com.vcspace.plugins.menu;

public interface MenuAction {
    void doAction(); // The action to perform when the menu is clicked
}

public class MenuItem {
    // ... fields (title, id, shortcut, action) and constructor
    public String getTitle(); // ... getters
    public int getId();
    public String getShortcut();
    public MenuAction getAction();
}
```

## Creating Your First Plugin: "Hello, Plugin!"

Let's create a simple plugin that displays a "Hello, Plugin!" message in the log:

1.  **Create a Java Class:** Create a new Java class (e.g., `HelloPlugin.java`) in your plugin project.

2.  **Implement the `Plugin` Interface:**

```java
package com.example.myplugin; // Use your own package

import com.vcspace.plugins.Plugin;
import com.vcspace.plugins.PluginContext;

public class HelloPlugin implements Plugin {
    @Override
    public void onPluginLoaded(PluginContext context) {
        context.log("Hello, Plugin!"); // Log the message
    }
}
```

3.  **Build Your Plugin:** You'll need to compile your Java code into a `.jar` file. The exact build process will depend on your development environment (e.g., using Gradle or Maven).

4.  **Install the Plugin:** Place the compiled `.jar` file in VCSpace's plugin directory.

5.  **Run VCSpace:** When you start VCSpace, your plugin should be loaded, and you should see "Hello, Plugin!" in the logs.

## Example: Adding a Menu Item

This example shows how to add a menu item that prints the current file's path:

```java
package com.example.myplugin;

import com.vcspace.plugins.Plugin;
import com.vcspace.plugins.PluginContext;
import com.vcspace.plugins.menu.MenuAction;
import com.vcspace.plugins.menu.MenuItem;
import java.io.File;

public class FilePathPlugin implements Plugin {
    @Override
    public void onPluginLoaded(PluginContext context) {
        MenuAction showFilePathAction = new MenuAction() {
            @Override
            public void doAction() {
                File currentFile = context.getEditor().getCurrentFile();
                if (currentFile != null) {
                    context.log("Current File Path: " + currentFile.getAbsolutePath());
                } else {
                    context.log("No file open.");
                }
            }
        };

        MenuItem filePathMenuItem = new MenuItem("Show File Path", 123, null, showFilePathAction); // 123 is a unique ID

        context.addMenu(filePathMenuItem);
        //Or using the simplified addMenu method:
        //context.addMenu("Show File Path", 124, showFilePathAction);
    }
}
```
