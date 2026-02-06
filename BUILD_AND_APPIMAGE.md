# 🚀 BUILD & APPIMAGE GUIDE

**Java2Bedrock Bridge v1.0.0-alpha**  
**Build Date**: 6 de Fevereiro de 2026  
**Status**: ✅ Ready for Production Build  

---

## 📋 Verificação Pré-Build

Primeiro, execute o script de verificação:

```bash
chmod +x verify-build.sh
./verify-build.sh
```

Esperado:
```
✓ Java 17 or higher
✓ Gradle installed
✓ Git installed
✓ All files present
✓ Gradle build can run
```

---

## 🔨 Opção 1: Build Local Quick (Apenas JAR)

### Passos Rápidos

```bash
# 1. Buildar
./gradlew clean build --stacktrace

# 2. JAR gerado em:
build/libs/java2bedrock-bridge-1.0.0-alpha.jar

# 3. Testar
java -jar build/libs/java2bedrock-bridge-1.0.0-alpha.jar
```

**Tempo**: ~3-5 minutos

---

## 🐳 Opção 2: Build Local com AppImage (Linux)

### Requisitos
```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y \
    openjdk-17-jdk \
    gradle \
    appimage-builder \
    libfuse2 \
    desktop-file-utils
```

### Executar Build Completo

```bash
chmod +x build-appimage.sh
./build-appimage.sh
```

Este script fará:
1. ✅ Clean do build anterior
2. ✅ Build completo com Gradle
3. ✅ Geração da estrutura AppImage
4. ✅ Build do AppImage
5. ✅ Geração de checksum SHA256

**Tempo**: ~10-15 minutos

### Saída Esperada

```
Artifacts:
  JAR:      build/libs/java2bedrock-bridge-1.0.0-alpha.jar
  AppImage: java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage
  Checksum: java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage.sha256
```

---

## 🔄 Opção 3: Build Automático com GitHub Actions (CI/CD)

### Configuração

Workflows já estão criados em `.github/workflows/`:

| Workflow | Arquivo | Ação |
|----------|---------|------|
| **Gradle Build** | `gradle-build.yml` | Build multi-OS (Ubuntu/Windows/macOS) |
| **AppImage Release** | `appimage-release.yml` | Build AppImage + Instaladores |

### Como Triggar

#### 3a. Automático (Push para main)

```bash
# Qualquer push dispara o workflow
git add .
git commit -m "Build: Trigger CI/CD"
git push origin main

# Acesse: https://github.com/invencivel7920/Java2bedrock_bridge/actions
```

#### 3b. Manual (Dispatch Workflow)

1. Vá para GitHub: Actions → gradle-build.yml → Run workflow
2. Ou via CLI:

```bash
gh workflow run gradle-build.yml -r main
gh workflow run appimage-release.yml -r main
```

#### 3c. Release (Release Workflow)

```bash
# Tag and release
git tag -a v1.0.0-alpha -m "Release 1.0.0-alpha"
git push origin v1.0.0-alpha

# Cria GitHub Release + Dispara workflow
```

### Monitorar Build

```bash
# Via CLI
gh run list --workflow=gradle-build.yml
gh run view <run-id>

# Ou na Web
# GitHub.com → Actions → Seu repo → Ver workflows
```

### Download Artifacts

**Após build completar:**

```bash
# Via GitHub Web
# Actions → Seu workflow → Artifacts

# Ou via CLI
gh run download <run-id> -n appimage-linux
gh run download <run-id> -n windows-dist
gh run download <run-id> -n macos-app
```

---

## 📦 Estrutura de Output

### Local Build
```
build/
├── libs/
│   └── java2bedrock-bridge-1.0.0-alpha.jar
├── distributions/
│   └── java2bedrock-bridge-1.0.0-alpha.zip
└── reports/
    └── (test reports if applicable)

AppImage/                                    # Se no Linux
└── (AppImage structure)

java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage  # Se build bem-sucedido
java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage.sha256
```

### GitHub Artifacts
```
Cada workflow upload:
- appimage-linux          → .AppImage + SHA256
- windows-dist            → .jar + scripts
- macos-app               → .app bundle
- build-reports-*         → (se houver failures)
- java2bedrock-bridge-*   → JAR output
```

---

## ✅ Checklist de Build

### Pré-Build
- [ ] Executou `verify-build.sh` com sucesso
- [ ] Java 17 instalado: `java -version`
- [ ] Gradle funciona: `gradle --version`
- [ ] 0 erros de compilação
- [ ] 0 warnings

### Build Local
- [ ] `./gradlew clean build` completou
- [ ] JAR foi gerado em `build/libs/`
- [ ] JAR pode ser executado

### AppImage (Linux)
- [ ] `build-appimage.sh` completou
- [ ] AppImage foi gerado
- [ ] SHA256 foi criado
- [ ] AppImage executável: `./java2bedrock-bridge-*.AppImage`

### CI/CD (GitHub)
- [ ] Workflow `.github/workflows/gradle-build.yml` existe
- [ ] Workflow `.github/workflows/appimage-release.yml` existe
- [ ] Repositório público no GitHub
- [ ] Branch `main` existe

---

## 🚀 Deployment

### Linux (AppImage)
```bash
# Download AppImage
wget https://github.com/invencivel7920/Java2bedrock_bridge/releases/download/v1.0.0-alpha/java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage

# Tornar executável
chmod +x java2bedrock-bridge-*.AppImage

# Executar
./java2bedrock-bridge-*.AppImage
```

### Windows
```bash
# Download JAR
# Duplo clique em java2bedrock-bridge-1.0.0-alpha.jar
# Ou: java -jar java2bedrock-bridge-1.0.0-alpha.jar
```

### macOS
```bash
# Download da App bundle
# Ou usar via Java diretamente
java -jar java2bedrock-bridge-1.0.0-alpha.jar
```

---

## ⚠️ Troubleshooting

### Problema: "Java not found"
```bash
# Solução
sudo apt-get install openjdk-17-jdk
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

### Problema: "Gradle wrapper not found"
```bash
# Solução
gradle wrapper --gradle-version 8.5
chmod +x gradlew
```

### Problema: "AppImage build failed"
```bash
# Instalar dependências
sudo apt-get install appimage-builder libfuse2 desktop-file-utils

# Ou download manual
wget https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage
chmod +x appimagetool-x86_64.AppImage
sudo mv appimagetool-x86_64.AppImage /usr/local/bin/appimagetool
```

### Problema: "Out of memory"
```bash
# Aumentar memória JVM
export GRADLE_OPTS="-Xmx2g -Xms512m"
./gradlew build
```

### Problema: "Gradle build hangs"
```bash
# Adicionar timeout e debug
./gradlew build --max-workers=4 --stacktrace --debug
```

---

## 📊 Performance & Tempo

| Operação | Tempo | Notas |
|----------|-------|-------|
| Verify | 1-2 min | Checks rápidos |
| Build JAR | 3-5 min | First time: mais lento |
| AppImage Build | 5-10 min | Cria estrutura + empacula |
| GitHub CI | 10-20 min | Dependente servidores |
| Total (Local) | 12-20 min | Build + AppImage |

---

## 🔐 Verificação de Integridade

### Validar SHA256
```bash
# After download
sha256sum -c java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage.sha256

# Expected output:
# java2bedrock-bridge-1.0.0-alpha-x86_64.AppImage: OK
```

### Validar JAR
```bash
# Ver manifest
jar tf build/libs/java2bedrock-bridge-1.0.0-alpha.jar | head -20

# Ver Main-Class
jar xf build/libs/java2bedrock-bridge-1.0.0-alpha.jar META-INF/MANIFEST.MF
cat META-INF/MANIFEST.MF
```

---

## 📝 Logging & Debug

### Habilitar Debug Build
```bash
./gradlew build --debug --stacktrace
```

### Ver Logs Detalhados
```bash
# Gradle
./gradlew build -i

# AppImage
APPIMAGE_DEBUG=1 ./java2bedrock-bridge-*.AppImage
```

### Ver Logs da App
```
# Dentro da app:
Menu → Exibir → Logs
Ou arquivo: ~/.java2bedrock/logs/
```

---

## 🎯 Next Steps

1. **Verificar Build Local**
   ```bash
   ./verify-build.sh
   ./gradlew clean build
   ```

2. **Testar JAR**
   ```bash
   java -jar build/libs/java2bedrock-bridge-1.0.0-alpha.jar
   ```

3. **Build AppImage (Linux)**
   ```bash
   ./build-appimage.sh
   ```

4. **Push para GitHub**
   ```bash
   git push origin main
   # Workflows disparam automaticamente
   ```

5. **Download Artifacts**
   ```bash
   # GitHub Actions → Artifacts
   ```

6. **Criar Release**
   ```bash
   git tag v1.0.0-alpha
   git push origin v1.0.0-alpha
   # GitHub cria Release com AppImage
   ```

---

## 📞 Suporte

| Problema | Solução |
|----------|---------|
| Build falha | Veja [PRE_BUILD_VERIFICATION.md](PRE_BUILD_VERIFICATION.md) |
| AppImage erro | Verifique dependências em system |
| CI/CD não funciona | Verifique workflows em `.github/workflows/` |
| JAR não executa | Confirme Java 17+ e permissões |

---

## ✨ Resumo

```
╔════════════════════════════════════════════════════════╗
║                                                        ║
║          🚀 BUILD & APPIMAGE READY                    ║
║                                                        ║
║  Opção 1:  ./gradlew build                 (JAR)      ║
║  Opção 2:  ./build-appimage.sh        (AppImage)      ║
║  Opção 3:  git push origin main   (CI/CD Workflows)   ║
║                                                        ║
║  Status: PRODUCTION READY                             ║
║  Next: Choose your build method above                 ║
║                                                        ║
╚════════════════════════════════════════════════════════╝
```

---

*Java2Bedrock Bridge v1.0.0-alpha*  
*Build Guide - February 6, 2026*  
*GitHub Copilot - Claude Haiku 4.5*
