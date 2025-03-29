#!/bin/bash

set -e

# Configuration
OUTPUT_DIR="app/build/outputs/apk/release"
INPUT_APK="${OUTPUT_DIR}/app-release-unsigned.apk"
ZIP_OUTPUT_DIR="app/build/zip"
PLUGIN_PROPERTIES="plugin.properties"
BASE_NAME=$(basename $(pwd))
ZIP_FILE="${ZIP_OUTPUT_DIR}/${BASE_NAME}.zip"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to detect architecture
detect_architecture() {
    ARCH=$(uname -m)
    case "$ARCH" in
        x86_64)
            echo "x86_64"
            ;;
        aarch64|arm64)
            echo "aarch64"
            ;;
        armv7l)
            echo "arm"
            ;;
        i686|i386)
            echo "x86"
            ;;
        *)
            echo "Unsupported architecture: $ARCH"
            exit 1
            ;;
    esac
}

# Function to install Android SDK
install_android_sdk() {
    # First check for Termux-specific Android SDK locations
    if [ -n "$TERMUX_VERSION" ]; then
        # Check common Termux Android SDK locations
        for termux_sdk_path in "$PREFIX/share/android-sdk" "$HOME/android-sdk"; do
            if [ -d "$termux_sdk_path" ] && ([ -d "$termux_sdk_path/tools" ] || [ -d "$termux_sdk_path/platform-tools" ] || [ -d "$termux_sdk_path/build-tools" ]); then
                export ANDROID_HOME="$termux_sdk_path"
                echo "Found Android SDK in Termux at $ANDROID_HOME"
                save_android_home
                return 0
            fi
        done

        # Check if sdkmanager exists in Termux
        if [ -f "$PREFIX/bin/sdkmanager" ]; then
            echo "Found sdkmanager in Termux, assuming SDK is installed"
            # In Termux, the SDK is typically at $PREFIX/share/android-sdk
            export ANDROID_HOME="$PREFIX/share/android-sdk"
            echo "Setting ANDROID_HOME to $ANDROID_HOME"
            save_android_home
            return 0
        fi
    fi

    # Standard checks for all environments
    # Check if ANDROID_HOME is already set and valid
    if [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
        echo "Android SDK already set up at $ANDROID_HOME"
        return 0
    fi

    # Check if sdkmanager exists
    if command_exists sdkmanager; then
        echo "Android SDK tools found. Determining ANDROID_HOME..."

        # Try to find ANDROID_HOME from sdkmanager path
        SDKMANAGER_PATH=$(which sdkmanager)
        if [ -n "$SDKMANAGER_PATH" ]; then
            # First get the real path (resolve symlinks)
            REAL_SDKMANAGER_PATH=$(readlink -f "$SDKMANAGER_PATH" 2>/dev/null || echo "$SDKMANAGER_PATH")

            # Try different directory structures
            # For classic SDK structure (tools/bin/sdkmanager)
            potential_path=$(dirname $(dirname $(dirname "$REAL_SDKMANAGER_PATH")))
            if [ -d "$potential_path/platforms" ] || [ -d "$potential_path/platform-tools" ]; then
                export ANDROID_HOME="$potential_path"
                echo "Found ANDROID_HOME at $ANDROID_HOME"
                save_android_home
                return 0
            fi

            # For new SDK structure (cmdline-tools/latest/bin/sdkmanager)
            potential_path=$(dirname $(dirname $(dirname $(dirname "$REAL_SDKMANAGER_PATH"))))
            if [ -d "$potential_path/platforms" ] || [ -d "$potential_path/platform-tools" ]; then
                export ANDROID_HOME="$potential_path"
                echo "Found ANDROID_HOME at $ANDROID_HOME"
                save_android_home
                return 0
            fi
        fi
    fi

    # Check common SDK locations
    for sdk_path in "$HOME/Android/Sdk" "$HOME/android-sdk" "/usr/local/android-sdk" "/usr/lib/android-sdk" "/opt/android-sdk"; do
        if [ -d "$sdk_path" ] && ([ -d "$sdk_path/platforms" ] || [ -d "$sdk_path/platform-tools" ]); then
            export ANDROID_HOME="$sdk_path"
            echo "Found ANDROID_HOME at $ANDROID_HOME"
            save_android_home
            return 0
        fi
    done

    # Install Android SDK if not found
    echo "Android SDK not found or not properly set up. Installing..."

    if [ -n "$TERMUX_VERSION" ]; then
        install_android_sdk_termux
    else
        install_android_sdk_system
    fi
}

# Install Android SDK on Termux
install_android_sdk_termux() {
    echo "Detected Termux, installing Android SDK..."

    # First check if Android SDK is already installed in common Termux locations
    for sdk_dir in "$PREFIX/share/android-sdk" "$HOME/android-sdk"; do
        if [ -d "$sdk_dir" ] && ([ -d "$sdk_dir/tools" ] || [ -d "$sdk_dir/platform-tools" ] || [ -d "$sdk_dir/build-tools" ]); then
            echo "Android SDK already installed at $sdk_dir"
            export ANDROID_HOME="$sdk_dir"
            save_android_home
            return 0
        fi
    done

    # Check if sdkmanager exists but ANDROID_HOME wasn't found
    if [ -f "$PREFIX/bin/sdkmanager" ]; then
        echo "sdkmanager exists but SDK directory not found. Setting up ANDROID_HOME..."
        export ANDROID_HOME="$PREFIX/share/android-sdk"
        mkdir -p "$ANDROID_HOME"
        save_android_home
        return 0
    fi

    # Install required packages
    echo "Installing required packages..."
    apt update
    apt install -y openjdk-17 unzip wget

    ARCH=$(detect_architecture)
    SDK_URL=""

    case "$ARCH" in
        aarch64)
            SDK_URL="https://github.com/Lzhiyong/termux-ndk/releases/download/android-sdk/android-sdk-aarch64.zip"
            ;;
        arm)
            SDK_URL="https://github.com/Lzhiyong/termux-ndk/releases/download/android-sdk/android-sdk-arm.zip"
            ;;
        x86_64|x86)
            echo "No prebuilt Android SDK available for $ARCH in Termux, using aarch64 version..."
            SDK_URL="https://github.com/Lzhiyong/termux-ndk/releases/download/android-sdk/android-sdk-aarch64.zip"
            ;;
    esac

    if [ -z "$SDK_URL" ]; then
        echo "No suitable Android SDK found for your architecture"
        exit 1
    fi

    # Download and extract Android SDK
    echo "Downloading Android SDK..."
    wget -O android-sdk.zip "$SDK_URL"
    mkdir -p "$PREFIX/share/android-sdk"
    echo "Extracting Android SDK..."
    unzip -q android-sdk.zip -d "$PREFIX/share"

    # Create sdkmanager wrapper if it doesn't exist
    if [ ! -f "$PREFIX/bin/sdkmanager" ]; then
        echo "Creating sdkmanager wrapper..."
        echo "#!/data/data/com.termux/files/usr/bin/bash" > "$PREFIX/bin/sdkmanager"
        echo "/data/data/com.termux/files/usr/share/android-sdk/tools/bin/sdkmanager --sdk_root=/data/data/com.termux/files/usr/share/android-sdk \"\$@\"" >> "$PREFIX/bin/sdkmanager"
        chmod +x "$PREFIX/bin/sdkmanager"
    fi

    # Set ANDROID_HOME
    export ANDROID_HOME="$PREFIX/share/android-sdk"
    echo "Android SDK installed at $ANDROID_HOME"
    save_android_home
}

# Install Android SDK on regular Linux systems
install_android_sdk_system() {
    echo "Installing Android SDK on standard Linux system..."

    # Check if we can use package manager
    if command_exists apt; then
        apt update
        if apt-cache show android-sdk >/dev/null 2>&1; then
            apt install -y android-sdk
            # Try to find the SDK location
            if [ -d "/usr/lib/android-sdk" ]; then
                export ANDROID_HOME="/usr/lib/android-sdk"
            elif [ -d "/opt/android-sdk" ]; then
                export ANDROID_HOME="/opt/android-sdk"
            else
                # Search for potential locations
                for dir in "/usr/local/android-sdk" "/usr/share/android-sdk" "/opt/android-sdk"; do
                    if [ -d "$dir" ]; then
                        export ANDROID_HOME="$dir"
                        break
                    fi
                done
            fi
        else
            # No package available, download the command line tools
            install_android_sdk_manually
        fi
    else
        # No apt, install manually
        install_android_sdk_manually
    fi

    save_android_home
}

# Install Android SDK manually by downloading command line tools
install_android_sdk_manually() {
    echo "Installing Android SDK manually..."

    # Install dependencies
    for pkg_manager in apt yum dnf pacman; do
        if command_exists "$pkg_manager"; then
            case "$pkg_manager" in
                apt)
                    apt update
                    apt install -y openjdk-11-jdk wget unzip
                    ;;
                yum|dnf)
                    $pkg_manager install -y java-11-openjdk-devel wget unzip
                    ;;
                pacman)
                    pacman -Sy --noconfirm jdk11-openjdk wget unzip
                    ;;
            esac
            break
        fi
    done

    # Create directory for Android SDK
    SDK_DIR="$HOME/android-sdk"
    mkdir -p "$SDK_DIR"

    # Download the command line tools
    ARCH=$(detect_architecture)
    CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip"

    wget -O cmdline-tools.zip "$CMDLINE_TOOLS_URL"
    mkdir -p "$SDK_DIR/cmdline-tools"
    unzip cmdline-tools.zip -d "$SDK_DIR/cmdline-tools"

    # Rename the directory to meet the expected structure
    mv "$SDK_DIR/cmdline-tools/cmdline-tools" "$SDK_DIR/cmdline-tools/latest" 2>/dev/null || true

    export ANDROID_HOME="$SDK_DIR"

    # Add SDK to PATH
    export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

    # Install basic platform tools
    yes | "$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" "platform-tools" "platforms;android-30" "build-tools;30.0.3"
}

# Save ANDROID_HOME to user profile
save_android_home() {
    if [ -n "$ANDROID_HOME" ]; then
        # For Termux, use its specific profile files
        if [ -n "$TERMUX_VERSION" ]; then
            # Termux uses ~/.bashrc and ~/.bash_profile
            PROFILE_FILES=("$HOME/.bashrc" "$HOME/.bash_profile")

            # Create .bashrc if it doesn't exist
            touch "$HOME/.bashrc"

            # Remove any existing ANDROID_HOME settings
            for file in "${PROFILE_FILES[@]}"; do
                if [ -f "$file" ]; then
                    sed -i '/export ANDROID_HOME=/d' "$file" 2>/dev/null
                    sed -i '/ANDROID_HOME.*platform-tools/d' "$file" 2>/dev/null
                fi
            done

            # Add new ANDROID_HOME settings to .bashrc
            echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> "$HOME/.bashrc"
            echo "export PATH=\"\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools:\$ANDROID_HOME/tools/bin:\$ANDROID_HOME/cmdline-tools/latest/bin:\$PATH\"" >> "$HOME/.bashrc"

            echo "ANDROID_HOME set to $ANDROID_HOME and saved to Termux profile"
        else
            # Standard Linux profiles
            # Add to .bashrc
            if ! grep -q "export ANDROID_HOME=" ~/.bashrc 2>/dev/null; then
                echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> ~/.bashrc
                echo "export PATH=\"\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools:\$ANDROID_HOME/tools/bin:\$ANDROID_HOME/cmdline-tools/latest/bin:\$PATH\"" >> ~/.bashrc
            fi

            # Also add to .profile for wider compatibility
            if ! grep -q "export ANDROID_HOME=" ~/.profile 2>/dev/null; then
                echo "export ANDROID_HOME=\"$ANDROID_HOME\"" >> ~/.profile
                echo "export PATH=\"\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools:\$ANDROID_HOME/tools/bin:\$ANDROID_HOME/cmdline-tools/latest/bin:\$PATH\"" >> ~/.profile
            fi

            echo "ANDROID_HOME set to $ANDROID_HOME and saved to profile"
        fi
    else
        echo "ANDROID_HOME is not set, could not save to profile"
    fi
}

# Detect Termux environment
if [ -n "$PREFIX" ] && [ -d "/data/data/com.termux" ]; then
    export TERMUX_VERSION=1
    echo "Termux environment detected"
fi

# Install Android SDK if needed
install_android_sdk

# Add ANDROID_HOME to current environment if it's not set
if [ -z "$ANDROID_HOME" ]; then
    echo "Warning: ANDROID_HOME is not set even after installation attempt"
    exit 1
fi

echo "ANDROID_HOME is set to $ANDROID_HOME"

# Validate that essential SDK components exist
if [ ! -d "$ANDROID_HOME/build-tools" ] && [ ! -d "$ANDROID_HOME/platform-tools" ]; then
    echo "Warning: Android SDK at $ANDROID_HOME appears to be incomplete"
    echo "Missing essential components (build-tools and/or platform-tools)"

    # Try to install essential components if sdkmanager exists
    if command_exists sdkmanager; then
        echo "Attempting to install essential SDK components..."
        yes | sdkmanager "platform-tools" "build-tools;33.0.2" "platforms;android-33"
    fi
fi

echo "Running Gradle assembleRelease..."

# Fix AAPT2 issues on Termux
if [ -n "$TERMUX_VERSION" ]; then
    echo "Detected Termux, applying AAPT2 fixes..."

    # Clear AAPT2 cache to force rebuild
    GRADLE_CACHE_DIR="$HOME/.gradle"
    AAPT2_CACHE_DIR=$(find "$GRADLE_CACHE_DIR" -path "*/transforms/*" -name "aapt2-*-linux" -type d 2>/dev/null)

    if [ -n "$AAPT2_CACHE_DIR" ]; then
        echo "Removing problematic AAPT2 cache: $AAPT2_CACHE_DIR"
        rm -rf "$AAPT2_CACHE_DIR"
    fi

    # Force the use of bundled AAPT2 from Android SDK if available
    if [ -d "$ANDROID_HOME/build-tools" ]; then
        # Get the latest build tools version
        BUILD_TOOLS_VERSION=$(ls -1 "$ANDROID_HOME/build-tools" | sort -V | tail -1)
        if [ -n "$BUILD_TOOLS_VERSION" ] && [ -f "$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION/aapt2" ]; then
            echo "Using AAPT2 from build-tools/$BUILD_TOOLS_VERSION"

            # Create a temporary gradle.properties file or append to existing one
            GRADLE_PROPS="gradle.properties"
            if [ -f "$GRADLE_PROPS" ]; then
                # Remove any existing android.aapt2FromMavenOverride property
                sed -i '/android.aapt2FromMavenOverride/d' "$GRADLE_PROPS"
            fi

            # Add the path to the AAPT2 executable
            echo "android.aapt2FromMavenOverride=$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION/aapt2" >> "$GRADLE_PROPS"
        fi
    fi

    # Set Gradle daemon JVM arguments
    mkdir -p "$HOME/.gradle"
    echo "org.gradle.jvmargs=-Xmx1536m -Dfile.encoding=UTF-8" > "$HOME/.gradle/gradle.properties"
fi

# Make sure gradlew is executable
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew

    # When running on Termux, use specific parameters to avoid memory issues
    if [ -n "$TERMUX_VERSION" ]; then
        bash gradlew assembleRelease --no-daemon
    else
        bash gradlew assembleRelease
    fi
else
    if [ -n "$TERMUX_VERSION" ]; then
        gradle assembleRelease --no-daemon
    else
        gradle assembleRelease
    fi
fi

if [ ! -f "$INPUT_APK" ]; then
    echo "Error: APK file not found at $INPUT_APK"
    exit 1
fi

if [ ! -f "$PLUGIN_PROPERTIES" ]; then
    echo "Error: plugin.properties file not found."
    exit 1
fi

mkdir -p "$ZIP_OUTPUT_DIR"

echo "Creating ZIP file..."
zip -j "$ZIP_FILE" "$INPUT_APK" "$PLUGIN_PROPERTIES"

echo "ZIP file created successfully: $ZIP_FILE"
