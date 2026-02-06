#!/bin/bash
# Build Script para Java2Bedrock Bridge

set -e

echo "╔════════════════════════════════════════╗"
echo "║  Java2Bedrock Bridge - Build Script   ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Verificar Java
echo "[1/5] Verificando Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado! Por favor, instale Java 17+."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[0-9]+' || echo "unknown")
echo "✓ Java versão $JAVA_VERSION encontrada"
echo ""

# Limpar build anterior
echo "[2/5] Limpando build anterior..."
if [ -d "build" ]; then
    rm -rf build
    echo "✓ Limpeza completa"
else
    echo "✓ Nada para limpar"
fi
echo ""

# Compilar
echo "[3/5] Compilando projeto..."
if [ -x "./gradlew" ]; then
    ./gradlew clean build --no-daemon -q
elif command -v gradle &> /dev/null; then
    gradle clean build --no-daemon -q
else
    echo "❌ Gradle não encontrado! Instale Gradle ou use ./gradlew"
    exit 1
fi
echo "✓ Compilação completa"
echo ""

# Verificar JAR
echo "[4/5] Verificando artefato..."
JAR_FILE="build/libs/java2bedrock-bridge-1.0.0-alpha.jar"
if [ -f "$JAR_FILE" ]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo "✓ JAR criada: $JAR_FILE ($JAR_SIZE)"
else
    echo "❌ JAR não foi criada!"
    exit 1
fi
echo ""

# Done
echo "[5/5] Build completo!"
echo ""
echo "╔════════════════════════════════════════╗"
echo "║  ✓ Build Sucesso!                     ║"
echo "╚════════════════════════════════════════╝"
echo ""
echo "Para executar:"
echo "  java -jar $JAR_FILE"
echo ""
echo "Ou com mais memória:"
echo "  java -Xmx2G -jar $JAR_FILE"
echo ""
