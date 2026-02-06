# 🚀 FIXE: GitHub Actions Explorer Error

**Problema**: Erro ao disparar workflows no VS Code  
**Mensagem**: `Cannot read properties of undefined (reading 'wf')`  
**Solução**: Use GitHub CLI (GNU/CLI em vez de VS Code Explorer)  

---

## ✅ SOLUÇÃO RÁPIDA (2 Minutos)

### 1. Instale GitHub CLI

**macOS**
```bash
brew install gh
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt update && sudo apt install gh
```

**Linux (Fedora/RHEL)**
```bash
sudo dnf install gh
```

**Windows**
```bash
choco install gh
# ou
winget install GitHub.cli
```

### 2. Autentique
```bash
gh auth login
# Pressione Enter até completar
# Será pedido um token: https://github.com/settings/tokens
```

### 3. Dispare Workflow
```bash
# Build Gradle
chmod +x trigger-workflow.sh
./trigger-workflow.sh gradle

# Ou AppImage
./trigger-workflow.sh appimage

# Ou ambos
./trigger-workflow.sh both
```

---

## 📋 OPÇÕES DE USO

### Usar Script Bash (Linux/Mac)
```bash
chmod +x trigger-workflow.sh
./trigger-workflow.sh gradle    # Build Gradle
./trigger-workflow.sh appimage  # AppImage
./trigger-workflow.sh both      # Ambos
./trigger-workflow.sh list      # Ver runs recentes
```

### Usar Script Batch (Windows)
```bash
trigger-workflow.bat gradle     # Build Gradle
trigger-workflow.bat appimage   # AppImage
trigger-workflow.bat both       # Ambos
trigger-workflow.bat list       # Ver runs recentes
```

### Usar GitHub CLI Diretamente
```bash
# Build Gradle
gh workflow run gradle-build.yml -r main

# AppImage Release
gh workflow run appimage-release.yml -r main

# Ver lista de runs
gh run list -L 10

# Ver détails de um run
gh run view <run-id>

# Acompanhar em tempo real
gh run watch <run-id>

# Download de artifacts
gh run download <run-id> -n appimage-linux
```

### Usar Git Push (Automático)
```bash
# Dispara gradle-build.yml
git push origin main

# Dispara appimage-release.yml
git tag v1.0.0-alpha
git push origin v1.0.0-alpha
```

---

## 🔄 FLUXO COMPLETO

### 1. Setup Inicial (Uma vez)
```bash
# Install gh
brew install gh  # ou seu package manager

# Authenticate
gh auth login
```

### 2. Disparar Build
```bash
cd /workspaces/Java2bedrock_bridge

# Opção A: Script
./trigger-workflow.sh gradle

# Opção B: Direct CLI
gh workflow run gradle-build.yml -r main

# Opção C: Git push
git push origin main
```

### 3. Monitorar
```bash
# Opção A: Via GitHub Web
# https://github.com/invencivel7920/Java2bedrock_bridge/actions

# Opção B: Via CLI
gh run list -L 5
gh run watch

# Opção C: Ver logs completos
gh run view <run-id> --log
```

### 4. Download Artifacts
```bash
# Listar run IDs
gh run list -L 5

# Download AppImage
gh run download <run-id> -n appimage-linux

# Download JAR
gh run download <run-id> -n java2bedrock-bridge-ubuntu-latest
```

---

## 🎯 Próximos Passos

### Passo 1: Instale GitHub CLI (1 min)
```bash
brew install gh  # macOS
# ou seu package manager
```

### Passo 2: Autentique (1 min)
```bash
gh auth login
```

### Passo 3: Dispare Workflow (30 seg)
```bash
cd /workspaces/Java2bedrock_bridge
./trigger-workflow.sh gradle
```

### Passo 4: Acompanhe (30 seg)
```bash
gh run watch
```

---

## 📊 Comparação: Métodos de Disparo

| Método | Como | Vantagem | Desvantagem |
|--------|------|----------|------------|
| **Script** | `./trigger-workflow.sh gradle` | Simples + Amigável | Requer chmod |
| **CLI** | `gh workflow run gradle-build.yml -r main` | Direto + Poderoso | Mais verboso |
| **Git Push** | `git push origin main` | Automático | Precisa commit |
| **VS Code** | UI Clicker | Visual | ❌ Bugado |

---

## ⚠️ Troubleshooting

### Erro: "gh: command not found"
```bash
# Instale GitHub CLI
# macOS: brew install gh
# Linux: sudo apt install gh
# Windows: choco install gh
```

### Erro: "Not authenticated"
```bash
gh auth login
# Siga os prompts
```

### Erro: "Permission denied" (script bash)
```bash
chmod +x trigger-workflow.sh
chmod +x verify-build.sh
chmod +x build-appimage.sh
```

### Workflow não dispara?
```bash
# Verifique sintaxe YAML
gh workflow view gradle-build.yml

# Verifique triggers
cat .github/workflows/gradle-build.yml | grep "^on:" -A 5
```

---

## ✨ Resumo Rápido

```bash
# Setup (1ª vez)
brew install gh
gh auth login

# Disparar workflows
./trigger-workflow.sh gradle      # Gradle Build
./trigger-workflow.sh appimage    # AppImage
./trigger-workflow.sh both        # Ambos

# Monitorar
gh run list
gh run watch

# Download
gh run download <ID> -n appimage-linux
```

---

## 📚 Referências

- [GitHub CLI Docs](https://cli.github.com/manual/)
- [GitHub Actions Docs](https://docs.github.com/en/actions)
- [Workflow Syntax](https://docs.github.com/en/actions/using-workflows/workflow-syntax-for-github-actions)

---

*Problema Resolvido! Use GitHub CLI para reliability 100%*
