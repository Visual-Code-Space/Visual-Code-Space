<div align="center">
  <img src="./images/ic_launcher.png" alt="Visual Code Space" width="90" height="90"/>
</div>

<h1 align="center"><b>Visual Code Space</b></h1>
<p align="center"><b>A Modern Code Editor for Android</b></p>

<div style="text-align: center;">
  <a href="https://github.com/Visual-Code-Space/Visual-Code-Space/actions/workflows/androidci.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/Visual-Code-Space/Visual-Code-Space/androidci.yml?branch=main" alt="Android CI">
  </a>
  <a href="https://opensource.org/licenses/GPL-3.0">
    <img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="License">
  </a>
</div>

## 📚 Overview

**Visual Code Space** is a powerful and feature-rich code editor designed for Android devices. With support for multiple programming languages, a tabbed editor, and a terminal emulator, it provides an efficient coding environment without any ads.

## 🚀 Features

- **Fast File Explorer**: Quickly navigate through your project files.
- **Multi-Language Support**: Code with syntax highlighting for various programming languages.
- **Tabbed Editor**: Manage multiple files with ease using a tabbed interface.
- **Terminal Emulator**: Access a terminal directly within the app.
- **No Ads**: Enjoy an uninterrupted coding experience.

## 📦 Installation

You can download the latest APK of Visual Code Space from the [Telegram Channel](https://t.me/visualcodespace).

## 📖 Documentation

### Creating Plugins

Visual Code Space supports custom plugins written in BeanShell. To create a plugin:

1. **Create the Plugin Directory**:
    - Create a plugin through the application settings. The plugin directory will be located at `/storage/emulated/0/VCSpace/plugins/<your-plugin-package>`.

2. **Modify the Entry Point (Optional)**:
    - To use a different function as the entry point, update the `entryPoint` field in the `manifest.json` file to the name of the desired function. For instance, if you want `startPlugin` as the entry point, set `entryPoint` to `"startPlugin"` in the manifest.

3. **Run the Plugin**:
    - Once the plugin is configured, the Visual Code Space app will automatically load and execute the plugin when the application starts.

For detailed instructions and examples, refer to the [Create a Plugin](docs/create_plugin.md) documentation.

## 🤝 Contributing

We welcome contributions to Visual Code Space! Please see our [CONTRIBUTING.md](https://github.com/Visual-Code-Space/Visual-Code-Space/blob/main/CONTRIBUTING.md) for guidelines on how to get involved.

## 💖 Thanks to

- [Rosemoe](https://github.com/Rosemoe) for the [sora-editor](https://github.com/Rosemoe/sora-editor)
- [VSCode](https://github.com/microsoft/vscode) for the [TextMate files](https://github.com/microsoft/vscode/tree/main/extensions)
- [Termux](https://github.com/termux) for the [Terminal Emulator](https://github.com/termux/termux-app)
- [Akash Yadav](https://github.com/itsaky) for the awesome [AndroidIDE](https://github.com/AndroidIDEOfficial/AndroidIDE)

## ✨️ Contributors

<a href="https://github.com/Visual-Code-Space/Visual-Code-Space/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Visual-Code-Space/Visual-Code-Space"  alt="Contributors"/>
</a>

## License

```
Visual Code Space is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Visual Code Space is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Visual Code Space.  If not, see <https://www.gnu.org/licenses/>.
```

Any violations to the license can be reported either by opening an issue or writing a mail to us
directly.
