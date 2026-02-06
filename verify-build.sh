#!/bin/bash

# ============================================
# Java2Bedrock Bridge - Pre-Compile Checklist
# ============================================

COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_YELLOW='\033[1;33m'
COLOR_BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${COLOR_BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${COLOR_BLUE}║  Java2Bedrock Bridge - Pre-Build Check  ║${NC}"
echo -e "${COLOR_BLUE}╚════════════════════════════════════════╝${NC}"
echo ""

# Counter
CHECKS_PASSED=0
CHECKS_FAILED=0

# Function to check requirement
check_requirement() {
    local name=$1
    local command=$2
    
    if eval "$command" &> /dev/null; then
        echo -e "${COLOR_GREEN}✓${NC} $name"
        ((CHECKS_PASSED++))
    else
        echo -e "${COLOR_RED}✗${NC} $name"
        ((CHECKS_FAILED++))
    fi
}

# Function to check file
check_file() {
    local name=$1
    local path=$2
    
    if [ -f "$path" ]; then
        echo -e "${COLOR_GREEN}✓${NC} $name"
        ((CHECKS_PASSED++))
    else
        echo -e "${COLOR_RED}✗${NC} $name (missing: $path)"
        ((CHECKS_FAILED++))
    fi
}

# Function to check directory
check_dir() {
    local name=$1
    local path=$2
    
    if [ -d "$path" ]; then
        echo -e "${COLOR_GREEN}✓${NC} $name"
        ((CHECKS_PASSED++))
    else
        echo -e "${COLOR_RED}✗${NC} $name (missing: $path)"
        ((CHECKS_FAILED++))
    fi
}

echo -e "${COLOR_YELLOW}Verifying Environment...${NC}"
echo ""

check_requirement "Java 17 or higher" "java -version 2>&1 | grep -q '17'"
check_requirement "Gradle installed" "gradle --version"
check_requirement "Git installed" "git --version"
check_requirement "Docker installed (for AppImage)" "docker --version"

echo ""
echo -e "${COLOR_YELLOW}Verifying Project Structure...${NC}"
echo ""

check_file "build.gradle" "build.gradle"
check_file "gradle.properties" "gradle.properties"
check_file "gradle.properties" "gradle.properties"
check_file "README.md" "README.md"
check_file "LICENSE" "LICENSE"

echo ""
echo -e "${COLOR_YELLOW}Verifying Source Code...${NC}"
echo ""

check_dir "src/main/java" "src/main/java"
check_dir "src/main/resources" "src/main/resources"
check_dir "src/main/java/com/javabedrock/bridge" "src/main/java/com/javabedrock/bridge"

echo ""
echo -e "${COLOR_YELLOW}Verifying Configuration...${NC}"
echo ""

check_file "j2b-config.toml" "j2b-config.toml"
check_file "mods.toml" "src/main/resources/META-INF/mods.toml"

echo ""
echo -e "${COLOR_YELLOW}Verifying Documentation...${NC}"
echo ""

check_file "INDEX.md" "INDEX.md"
check_file "PROJECT_STATUS.md" "PROJECT_STATUS.md"
check_file "CODE_OPTIMIZATION_REPORT.md" "CODE_OPTIMIZATION_REPORT.md"

echo ""
echo -e "${COLOR_YELLOW}Running Gradle Check...${NC}"
echo ""

if gradle check --dry-run &> /dev/null; then
    echo -e "${COLOR_GREEN}✓${NC} Gradle build can run"
    ((CHECKS_PASSED++))
else
    echo -e "${COLOR_RED}✗${NC} Gradle build validation failed"
    ((CHECKS_FAILED++))
fi

echo ""
echo -e "${COLOR_YELLOW}Summary${NC}"
echo "─────────────────────────"
echo -e "Passed: ${COLOR_GREEN}${CHECKS_PASSED}${NC}"
echo -e "Failed: ${COLOR_RED}${CHECKS_FAILED}${NC}"

if [ $CHECKS_FAILED -eq 0 ]; then
    echo ""
    echo -e "${COLOR_GREEN}╔════════════════════════════════════════╗${NC}"
    echo -e "${COLOR_GREEN}║     ✓ ALL CHECKS PASSED - READY!       ║${NC}"
    echo -e "${COLOR_GREEN}╚════════════════════════════════════════╝${NC}"
    exit 0
else
    echo ""
    echo -e "${COLOR_RED}╔════════════════════════════════════════╗${NC}"
    echo -e "${COLOR_RED}║  ✗ Some checks failed - See above      ║${NC}"
    echo -e "${COLOR_RED}╚════════════════════════════════════════╝${NC}"
    exit 1
fi
