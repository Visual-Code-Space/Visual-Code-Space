
## Overview

The `Visual Code Space` app supports custom plugins written in BeanShell. Plugins are configured with a manifest file and a script file, enabling interaction with the app and providing additional functionality.

## Plugin Structure

A plugin consists of the following components:

1. **`manifest.json`**: Contains metadata about the plugin, such as its name, version, and the path to the script.
2. **Script File (`.bsh`)**: The BeanShell script that defines the plugin's behavior.

### 1. manifest.json

The `manifest.json` file is a JSON file that provides essential information about the plugin. Here’s an example:

```json
{
  "author": "Unknown",
  "description": "No description provided.",
  "name": "Plugin Name",
  "packageName": "com.example.plugin",
  "path": "main.bsh",
  "versionCode": 1,
  "versionName": "1.0.0"
}
```

- **`author`**: The author of the plugin.
- **`description`**: A brief description of what the plugin does.
- **`name`**: The name of the plugin.
- **`packageName`**: The package name for the plugin.
- **`path`**: The relative path to the main BeanShell script file.
- **`versionCode`**: The version code of the plugin (an integer).
- **`versionName`**: The version name of the plugin (a string).

### 2. main.bsh

The `main.bsh` file is where the core functionality of the plugin is implemented. It can interact with the Android application and use various utilities provided by the app.

Here’s an example `main.bsh`:

```java
import android.content.Intent;
import android.widget.Toast;

import com.teixeira.vcspace.activities.TerminalActivity;

Runnable runnable = new Runnable() {
  public void run() {
    // Display a toast message
    Toast.makeText(app, "Hello from plugin.", Toast.LENGTH_SHORT).show();
    
    // Start the TerminalActivity
    app.startActivity(new Intent(app, TerminalActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
  }
};

// Run the code on the UI thread
helper.runOnUiThread(runnable);
```

### Explanation:

- **Toast**: A simple toast message is displayed when the plugin is executed.
- **Intent**: The plugin starts the `TerminalActivity` within the app, using an `Intent` with the `FLAG_ACTIVITY_NEW_TASK` flag to ensure it launches properly.
- **`helper.runOnUiThread()`**: Ensures that the UI-related actions are executed on the main thread, as required by Android.

## Creating a Plugin

### Step 1: Create the Plugin Directory

Create a directory for your plugin under the application's files directory in your app’s file structure.

### Step 2: Add the manifest.json

Place a `manifest.json` file in your plugin directory, following the structure provided in the example above.

### Step 3: Write the main.bsh Script

Create a `.bsh` file in your plugin directory as specified in the `manifest.json` file. This file should contain the logic for your plugin.

### Step 4: Run the Plugin

Once the plugin is set up, the `Visual Code Space` app will load and execute the plugin when the Application starts.
