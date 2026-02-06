# 🔧 SOLUÇÃO: Erro GitHub Actions Explorer

**Erro**: `Cannot read properties of undefined (reading 'wf')`  
**Causa**: Bug na extensão GitHub Actions Explorer do VS Code  
**Data**: 6 de Fevereiro de 2026  

---

## 🚀 SOLUÇÃO 1: Usar GitHub CLI (Recomendado - Funciona 100%)

### Pré-requisito
```bash
# Instale GitHub CLI se não tem
# macOS
brew install gh

# Linux
sudo apt install gh

# Windows
winget install GitHub.cli

# Verificar instalação
gh --version
```

### Autenticar
```bash
gh auth login
# Escolha: GitHub.com
# Escolha: HTTPS
# Escolha: Y (para colar token)
# Token: Vá para https://github.com/settings/tokens e crie um
```

### Disparar Workflows com GitHub CLI

#### **Workflow: gradle-build**
```bash
# Opção 1: Disparar no branch main
gh workflow run gradle-build.yml -r main

# Opção 2: Disparar com inputs customizados
gh workflow run gradle-build.yml -r main --raw-field version=1.0.0-alpha
```

#### **Workflow: appimage-release**
```bash
# Disparar AppImage build
gh workflow run appimage-release.yml -r main

# Com input de versão
gh workflow run appimage-release.yml -r main -f version=1.0.0-alpha
```

### Ver Status do Workflow
```bash
# Listar últimas execuções
gh workflow view gradle-build.yml

# Ver todos os runs
gh run list --workflow=gradle-build.yml -L 10

# Ver detalhes de um run específico
gh run view <run-id>

# Acompanhar em tempo real
gh run watch <run-id>

# Download de artifacts
gh run download <run-id> -n appimage-linux
```

---

## 🔄 SOLUÇÃO 2: Disparar via Git Push (Automático)

Este método **dispara workflows automaticamente** sem extensão:

### Para Build Gradle
```bash
# Just push to main - gradle-build dispara automaticamente
git add .
git commit -m "feat: Trigger gradle build"
git push origin main
```

### Para AppImage Release
```bash
# Cria uma tag e push
git tag -a v1.0.0-alpha -m "Release 1.0.0-alpha"
git push origin v1.0.0-alpha

# Ou usando GitHub CLI
gh release create v1.0.0-alpha --title "v1.0.0-alpha" --notes "Gradle build and AppImage release"
```

### Ver Progresso
```bash
# Terminal
gh run list -L 5

# Ou Web
# https://github.com/invencivel7920/Java2bedrock_bridge/actions
```

---

## 🛠️ SOLUÇÃO 3: Reinstalar Extensão (Se quiser usar GUI)

Se preferir usar o VS Code Explorer:

### Passo 1: Desinstalar
```
VS Code → Extensions → Procure "GitHub Actions"
Clique em "Uninstall" em qualquer extensão relacionada
```

### Passo 2: Limpar Cache
```bash
# Linux/Mac
rm -rf ~/.vscode/extensions/GitHub.vscode-github-actions*

# Windows
rmdir %USERPROFILE%\.vscode\extensions\github.vscode-github-actions*
```

### Passo 3: Reinstalar
```
VS Code → Extensions → Search "GitHub Actions"
Instale: "GitHub Actions" by GitHub (oficial)
```

### Passo 4: Reload
```
VS Code → Command Palette (Ctrl+Shift+P)
Developer: Reload Window
```

---

## ✅ COMPARAÇÃO DAS 3 SOLUÇÕES

| Solução | Tipo | Vantagens | Desvantagens |
|---------|------|-----------|--------------|
| **CLI** | Terminal | ✅ Confiável, sem bug, controle total | ⚠️ Requer CLI |
| **Git Push** | Terminal | ✅ Automático, simples | ⚠️ Precisa fazer commit |
| **Extensão** | VS Code GUI | ✅ Interface visual | ❌ Pode ter bugs |

---

## 📝 MINHA RECOMENDAÇÃO

### Opção A: Usar GitHub CLI (Melhor)
```bash
# 1. Install
brew install gh  # ou apt install gh / winget install

# 2. Authenticate
gh auth login

# 3. Disparar
gh workflow run gradle-build.yml -r main

# 4. Acompanhar
gh run watch
```

### Opção B: Simple Git Push
```bash
# Build Gradle (automático)
git push origin main

# AppImage Release
git tag v1.0.0-alpha
git push origin v1.0.0-alpha
```

---

## 🔍 VERIFICAR STATUS DO WORKFLOW

### Via CLI
```bash
# Ver todos os workflows
gh workflow list

# Ver runs recentes
gh run list -L 10

# Detalhes de um run
gh run view <run-id>

# Ver logs
gh run view <run-id> --log

# Download artifacts
gh run download <run-id> --name appimage-linux
```

### Via Web
```
GitHub.com → Seu Repo → Actions → Ver workflows rodando
```

---

## 🎯 PASSO-A-PASSO: Get Started Agora

### Usando GitHub CLI (Confiável)

```bash
# 1. Instale gh
# macOS: brew install gh
# Linux: sudo apt install gh
# Windows: choco install gh

# 2. Autentique
gh auth login
# Siga os prompts... 

# 3. Disparar gradle-build
gh workflow run gradle-build.yml -r main

# 4. Acompanhar
gh run list -L 5

# 5. Ver logs
gh run view <run-id> --log

# 6. Download artifacts (quando completo)
gh run download <run-id> -n appimage-linux
```

---

## 📋 WORKFLOWS DISPONÍVEIS

Você tem 2 workflows funcionalários:

### 1. **gradle-build.yml**
```
Disparo: Push para main/develop ou workflow_dispatch
O que faz: Build + test em Ubuntu/Windows/macOS
Output: JAR + build reports
```

### 2. **appimage-release.yml**
```
Disparo: Release publicada ou workflow_dispatch
O que faz: AppImage Linux, Windows installer, macOS bundle
Output: AppImage + SHA256 + GitHub Release
```

---

## ⚠️ Se Ainda Tiver Erro de Extensão

### Verificação
```
1. VS Code → Output → GitHub Actions (tab)
2. Veja qual extensão está instalada
3. Verifique versão: Should be latest
```

### Alternativas
```
✅ Usar GitHub CLI (recomendado)
✅ Desinstalar extensão bugada
✅ Usar GitHub Web (github.com/actions)
```

---

## ✨ Resumo Rápido

| Necessidade | Comando |
|-------------|---------|
| **Build Gradle** | `gh workflow run gradle-build.yml -r main` |
| **Build AppImage** | `gh workflow run appimage-release.yml -r main` |
| **Ver status** | `gh run list` |
| **Ver logs** | `gh run view <id> --log` |
| **Download** | `gh run download <id> -n appimage-linux` |

---

## 🚀 Próxima Ação

Escolha uma opção acima e execute! Recomendo **GitHub CLI** por ser mais confiável.

Se tiver dúvidas, posso ajudar com qualquer um dos comandos acima.

---

*Erro Resolvido - Use GitHub CLI para resultado garantido!*
