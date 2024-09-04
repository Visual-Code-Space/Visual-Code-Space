
## Plugin Documentation

### Overview

The `Visual Code Space` app supports custom plugins written in BeanShell. Plugins are configured with a manifest file and a script file, allowing interaction with the app and providing additional functionality.

### Plugin Structure

A plugin consists of the following components:

1. **`manifest.json`**: Contains metadata about the plugin, such as its name, version, and the script file details.
2. **Script File (`.bsh`)**: The BeanShell script that defines the plugin's behavior.

### 1. manifest.json

The `manifest.json` file provides essential information about the plugin. Here’s an example:

```json
{
  "author": "Unknown",
  "description": "No description provided",
  "name": "New Plugin",
  "packageName": "com.example.plugin",
  "scripts": [
    {
      "entryPoint": "main",
      "name": "main.bsh"
    }
  ],
  "versionCode": 1,
  "versionName": "1.0.0"
}
```

- **`author`**: The author of the plugin.
- **`description`**: A brief description of what the plugin does.
- **`name`**: The name of the plugin.
- **`packageName`**: The package name for the plugin.
- **`scripts`**: An array of script file objects. Each object includes:
    - **`entryPoint`**: The function to be executed when the plugin is loaded. Default is `"main"`.
    - **`name`**: The relative path to the BeanShell script file.
- **`versionCode`**: The version code of the plugin (an integer).
- **`versionName`**: The version name of the plugin (a string).

### 2. main.bsh

The `main.bsh` file is where the core functionality of the plugin is implemented. It interacts with the Android application and utilizes various utilities provided by the app.

Here’s an example `main.bsh`:

```java
// This is a sample plugin script for Visual Code Space.
// 
// This script serves as the entry point for your plugin. The 'main' function is
// the default method that will be executed when your plugin is loaded and started.
// 
// Follow this template to create your own plugin by adding custom logic inside the 'main' function.
// You can use Android-specific features and classes, as well as interact with the app using
// provided objects like 'app' and 'helper'.
// 
// The 'app' object refers to the Application instance of this app, allowing you to access various
// application-level features and context. 
// 
// The 'helper' object provides additional utility methods to assist with plugin development.
// 
// Example Usage:
// - Display a Toast message to the user
// - Add custom behavior or UI elements to your app
// - Interact with existing app components
// 
// Modifying the Entry Point:
// - By default, the entry point is set to the 'main' function.
// - You can change the entry point by modifying the 'entryPoint' field in the plugin's manifest.
// - Set 'entryPoint' to the name of any other function in this script that you want to execute when the plugin is loaded.
// - Example: To use 'startPlugin' as the entry point, set 'entryPoint: "startPlugin"' in the manifest.

void main() {
  // Display a simple Toast message when the plugin is loaded
  Toast.makeText(app, "Hello from plugin!", Toast.LENGTH_SHORT).show();
  
  // Add your custom plugin logic here
  // For example, you could interact with other components of the app, create new UI elements, etc.
  
  // Additional Example 1: Print a message to the logcat
  // Log.d("Plugin", "Plugin is running!");
  
  // Additional Example 2: Start an Android Activity from the plugin
  // Intent intent = new Intent(app, YourTargetActivity.class);
  // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  // app.startActivity(intent);
  
  // Remember to keep the 'main' function parameterless for it to be correctly identified and executed.
}

// Example of an alternative entry point
void startPlugin() {
  // Custom logic that can be used as the entry point if specified in the manifest
  Toast.makeText(app, "startPlugin function executed!", Toast.LENGTH_SHORT).show();
}
```

### Creating a Plugin

1. **Create the Plugin Directory**:
    - Create a plugin through the application settings. The plugin directory will be located at `/storage/emulated/0/VCSpace/plugins/<your-plugin-package>`.

2. **Modify the Entry Point (Optional)**:
    - To use a different function as the entry point, update the `entryPoint` field in the `manifest.json` file to the name of the desired function. For instance, if you want `startPlugin` as the entry point, set `entryPoint` to `"startPlugin"` in the manifest.

3. **Run the Plugin**:
    - Once the plugin is configured, the `Visual Code Space` app will automatically load and execute the plugin when the application starts.

---

This documentation is designed to help new developers understand how to create and configure plugins for `Visual Code Space`, including details on modifying the entry point function and utilizing Android-specific functionality.