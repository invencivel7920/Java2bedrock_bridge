#!/bin/bash

# ================================================
# GitHub Actions Workflow Trigger Script
# ================================================
# Usage: ./trigger-workflow.sh [gradle|appimage|both]

set -e

COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_YELLOW='\033[1;33m'
COLOR_BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${COLOR_BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${COLOR_BLUE}║  GitHub Actions Workflow Trigger       ║${NC}"
echo -e "${COLOR_BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo -e "${COLOR_RED}✗ GitHub CLI not found.${NC}"
    echo ""
    echo "Install GitHub CLI:"
    echo "  macOS:   brew install gh"
    echo "  Linux:   sudo apt install gh"
    echo "  Windows: choco install gh"
    echo ""
    echo "Then: gh auth login"
    exit 1
fi

echo -e "${COLOR_GREEN}✓ GitHub CLI found: $(gh --version)${NC}"
echo ""

# Check authentication
if ! gh auth status &> /dev/null; then
    echo -e "${COLOR_RED}✗ Not authenticated with GitHub.${NC}"
    echo "Run: gh auth login"
    exit 1
fi

echo -e "${COLOR_GREEN}✓ Authenticated${NC}"
echo ""

# Get user input
WORKFLOW=${1:-"both"}

echo -e "${COLOR_YELLOW}Choose action:${NC}"
echo "  gradle   → Build with Gradle (multi-OS)"
echo "  appimage → Build AppImage + installers"
echo "  both     → Run all workflows"
echo "  list     → List recent workflow runs"
echo ""

# Default to both if no argument
if [ "$WORKFLOW" = "" ]; then
    WORKFLOW="both"
fi

# Function to trigger workflow
trigger_workflow() {
    local workflow_name=$1
    local display_name=$2
    
    echo -e "${COLOR_YELLOW}Triggering: $display_name${NC}"
    echo ""
    
    if gh workflow run "$workflow_name" -r main; then
        echo -e "${COLOR_GREEN}✓ Workflow triggered successfully!${NC}"
        echo ""
        echo "Monitor at:"
        echo "  https://github.com/invencivel7920/Java2bedrock_bridge/actions"
        echo ""
        echo "Or via CLI:"
        echo "  gh run list"
        echo "  gh run watch"
    else
        echo -e "${COLOR_RED}✗ Failed to trigger workflow${NC}"
        exit 1
    fi
}

# Handle user choice
case "$WORKFLOW" in
    gradle)
        trigger_workflow "gradle-build.yml" "Gradle Build (Ubuntu/Windows/macOS)"
        ;;
    appimage)
        trigger_workflow "appimage-release.yml" "AppImage Release (Linux/Windows/macOS)"
        ;;
    both)
        trigger_workflow "gradle-build.yml" "Gradle Build"
        echo ""
        sleep 2
        trigger_workflow "appimage-release.yml" "AppImage Release"
        ;;
    list)
        echo -e "${COLOR_YELLOW}Recent workflow runs:${NC}"
        echo ""
        gh run list -L 10
        ;;
    *)
        echo -e "${COLOR_RED}Unknown option: $WORKFLOW${NC}"
        echo "Use: ./trigger-workflow.sh [gradle|appimage|both|list]"
        exit 1
        ;;
esac

echo ""
echo -e "${COLOR_GREEN}Done!${NC}"
echo ""
echo "To watch progress:"
echo "  gh run watch"
echo ""
echo "To download artifacts when done:"
echo "  gh run download <run-id> -n appimage-linux"
