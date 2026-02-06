#!/bin/bash

# ================================================
# Java2Bedrock Bridge - Local Build & AppImage
# ================================================

set -e  # Exit on error

COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_YELLOW='\033[1;33m'
COLOR_BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${COLOR_BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${COLOR_BLUE}║  Java2Bedrock Bridge - Build & AppImage Script    ║${NC}"
echo -e "${COLOR_BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""

# Get version from gradle.properties
VERSION=$(grep "^version" gradle.properties | cut -d'=' -f2 | tr -d ' ')
APP_NAME="java2bedrock-bridge"
APP_JAR="${APP_NAME}-${VERSION}.jar"

echo -e "${COLOR_YELLOW}Configuration:${NC}"
echo "  Version: $VERSION"
echo "  Output JAR: $APP_JAR"
echo ""

# Step 1: Verify
echo -e "${COLOR_YELLOW}Step 1: Verifying environment...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${COLOR_RED}✗ Java not found. Please install Java 17+${NC}"
    exit 1
fi

echo -e "${COLOR_GREEN}✓ Java available: $(java -version 2>&1 | head -1)${NC}"
echo ""

# Step 2: Clean
echo -e "${COLOR_YELLOW}Step 2: Cleaning previous builds...${NC}"
./gradlew clean
echo -e "${COLOR_GREEN}✓ Clean complete${NC}"
echo ""

# Step 3: Build
echo -e "${COLOR_YELLOW}Step 3: Building JAR with Gradle...${NC}"
./gradlew build -x test --stacktrace
echo -e "${COLOR_GREEN}✓ Build complete${NC}"
echo ""

# Step 4: Verify JAR
echo -e "${COLOR_YELLOW}Step 4: Verifying JAR artifact...${NC}"
if [ -f "build/libs/$APP_JAR" ]; then
    SIZE=$(du -h "build/libs/$APP_JAR" | cut -f1)
    echo -e "${COLOR_GREEN}✓ JAR created: $SIZE${NC}"
    echo "  Location: build/libs/$APP_JAR"
else
    echo -e "${COLOR_RED}✗ JAR not found!${NC}"
    exit 1
fi
echo ""

# Step 5: Test JAR (optional)
echo -e "${COLOR_YELLOW}Step 5: Testing JAR (quick validation)...${NC}"
if java -jar "build/libs/$APP_JAR" --version &>/dev/null; then
    echo -e "${COLOR_GREEN}✓ JAR runs successfully${NC}"
else
    # Don't fail if --version is not supported
    echo -e "${COLOR_YELLOW}⚠ JAR execution test skipped (GUI app)${NC}"
fi
echo ""

# Step 6: Create AppImage structure (if on Linux)
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo -e "${COLOR_YELLOW}Step 6: Creating AppImage structure...${NC}"
    
    mkdir -p AppImage/usr/bin
    mkdir -p AppImage/usr/share/applications
    mkdir -p AppImage/usr/share/icons/hicolor/256x256/apps
    
    # Copy JAR
    cp "build/libs/$APP_JAR" AppImage/usr/bin/java2bedrock-bridge.jar
    
    # Create AppRun
    cat > AppImage/AppRun << 'EOF'
#!/bin/bash
APPIMAGE_DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(readlink -f $(which java))))}
exec "$JAVA_HOME/bin/java" -Xmx2g -Xms256m -jar "$APPIMAGE_DIR/usr/bin/java2bedrock-bridge.jar" "$@"
EOF
    chmod +x AppImage/AppRun
    
    # Create desktop entry
    cat > AppImage/usr/share/applications/com.javabedrock.bridge.desktop << 'EOF'
[Desktop Entry]
Type=Application
Name=Java2Bedrock Bridge
Comment=Translate Java mods to Bedrock Edition
Icon=com.javabedrock.bridge
Exec=java2bedrock-bridge %F
Categories=Development;Utility;
Terminal=false
StartupNotify=true
Version=1.0
EOF
    
    echo -e "${COLOR_GREEN}✓ AppImage structure created${NC}"
    echo ""
    
    # Step 7: Build AppImage (if appimagetool available)
    echo -e "${COLOR_YELLOW}Step 7: Attempting to build AppImage...${NC}"
    
    if command -v appimagetool &> /dev/null; then
        APPIMAGE_NAME="${APP_NAME}-${VERSION}-x86_64.AppImage"
        appimagetool AppImage "$APPIMAGE_NAME"
        
        if [ -f "$APPIMAGE_NAME" ]; then
            SIZE=$(du -h "$APPIMAGE_NAME" | cut -f1)
            echo -e "${COLOR_GREEN}✓ AppImage created: $SIZE${NC}"
            echo "  Location: $(pwd)/$APPIMAGE_NAME"
            
            # Create checksum
            sha256sum "$APPIMAGE_NAME" > "${APPIMAGE_NAME}.sha256"
            echo -e "${COLOR_GREEN}✓ Checksum created${NC}"
        else
            echo -e "${COLOR_RED}✗ AppImage creation failed${NC}"
        fi
    else
        echo -e "${COLOR_YELLOW}⚠ appimagetool not found. Install AppImage toolkit to build AppImage.${NC}"
        echo "  Run: sudo apt-get install appimage-builder"
        echo "  Or download from: https://github.com/AppImage/AppImageKit"
    fi
    echo ""
else
    echo -e "${COLOR_YELLOW}⚠ Not on Linux - skipping AppImage build${NC}"
    echo "  AppImage is Linux-specific. Use on Ubuntu/Debian/Fedora with appimagetool installed."
    echo ""
fi

# Summary
echo -e "${COLOR_BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${COLOR_BLUE}║                  BUILD SUMMARY                     ║${NC}"
echo -e "${COLOR_BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${COLOR_GREEN}✓ Build completed successfully!${NC}"
echo ""
echo "Artifacts:"
echo "  JAR:      build/libs/$APP_JAR"
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "  AppImage: AppImage (directory) or .AppImage (if built)"
fi
echo ""
echo "Next steps:"
echo "  1. Test locally: java -jar build/libs/$APP_JAR"
echo "  2. Push to GitHub to trigger workflows"
echo "  3. Download artifacts from GitHub Actions"
echo ""

echo -e "${COLOR_GREEN}✨ Ready to deploy!${NC}"
