# Contributing to Visual Code Space

This file explains the modules, assets, and the steps to be followed to contribute to the project.

## Modules:

- **app:** Main module of the project, containing the central logic of the application. 
- **common:** Module that stores frequently used classes throughout the application, providing a reusable code base. 
- **common-res:** Contains all application resources such as images, styles, strings and other essential resources. 
- **editor-ace:** Module that integrates the ACE editor. 
- **editor-sora:** Module that integrates the Sora editor. 
- **eventbus-events:** Module that contains events destined for the eventbus library, facilitating communication between different parts of the application. 
- **models:** Contains model classes that represent application data, helping with data organization and manipulation. 
- **editor-sora:lang-textmate:** Module that handles support for the TextMate language in the Sora editor. 

## Assets Folder

- **assets/files**: Assets for the file explorer.
  - Contains assets used by the file explorer.

- **assets/editor/ace-editor**: Assets for Ace Editor.
  - Includes assets specific to the Ace Editor.

- **assets/editor/sora-editor**: Assets for Sora Editor.
  - Contains assets related to the Sora Editor.

## How to Contribute to This Project:

1. **Fork the Repository:** Click the "Fork" button at the top-right corner of the repository's page to create your copy.

2. **Clone Your Fork:** Clone your forked repository to your local machine using the following command:
   
   ```
   git clone <your-fork-url>
   ```

3. **Open the Project:** Open the project in your preferred development environment.

4. **Make Your Changes:** Implement your code changes, fix issues, or add new features as needed.

5. **Create a Pull Request (PR):** When your changes are ready, create a pull request from your fork to the original repository. Please provide a descriptive title and a detailed description of your PR.

While working on this project, please adhere to these guidelines:

- Use a 2-space indent for code formatting.
- For Java code, follow the `GoogleStyle` formatting guidelines. You can use the `google-java-format` tool or import the code style from [this link](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml) to your Integrated Development Environment (IDE).
- For XML code, adhere to the standard formatting guidelines recommended by Android Studio or AndroidIDE.

We appreciate your contributions and look forward to collaborating with you to make this project even better!
