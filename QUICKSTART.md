# Quick Start Guide - Java2Bedrock Bridge

Bem-vindo! Este é um guia rápido para começar com o desenvolvimento.

## ⚡ 5 Minutos de Setup

### 1. Clonar o Repositório
```bash
git clone https://github.com/Java2bedrock/Java2bedrock_bridge.git
cd Java2bedrock_bridge
```

### 2. Importar no IDE

**IntelliJ IDEA:**
```bash
./gradlew genIntelliJRuns
# Abra o projeto em IDEA
# Gradle sync automático
```

**Eclipse:**
```bash
./gradlew eclipse
# Abra via Eclipse
```

### 3. Primeiro Build
```bash
./gradlew build
```
JAR estará em `build/libs/`

### 4. Testar
```bash
./gradlew runClient   # Cliente teste
./gradlew runServer   # Servidor teste
```

## 📁 Estrutura Rápida

- **core/** - Núcleo e inicialização
- **network/** - Conexão Bedrock via Netty
- **translation/** - Motor de tradução
- **integration/** - Integração com mods
- **config/** - Configurações
- **command/** - Comandos in-game

## 🔧 Principais Classes

| Classe | Responsabilidade |
|--------|------------------|
| `Java2BedrockBridge` | Classe principal @Mod |
| `BridgeCore` | Orquestrador central |
| `NetworkManager` | Gerencia conexão Bedrock |
| `TranslationEngine` | Traduz conteúdo |
| `ModIntegrationEngine` | Integra mods |

## 📝 Principais Arquivos

| Arquivo | Função |
|---------|--------|
| `build.gradle` | Build configuration |
| `j2b-config.toml` | Configuração padrão |
| `src/main/resources/META-INF/mods.toml` | Metadados do mod |

## 🎮 Comandos in-game

```
/j2b status    # Status e estatísticas
/j2b debug     # Toggle debug mode
/j2b reload    # Recarrega config
```

## 🐛 Debug Mode

Para ativar o modo debug, use o comando:
```
/j2b debug
```

Ou edite `j2b-config.toml`:
```toml
[debug]
enabled = true
log_level = "DEBUG"
```

Os logs estarão em `.minecraft/logs/latest.log`

## 🚀 Sua Primeira Tarefa

### Implementar BlockTranslator

1. Abra `src/main/java/com/javabedrock/bridge/translation/BlockTranslator.java`
2. Implemente o método `loadMappings()`:
   ```java
   public void loadMappings() {
       // TODO: Carregar mapeamentos de blocos
       LOGGER.debug("Blocos carregados");
   }
   ```
3. Build e teste:
   ```bash
   ./gradlew build
   ./gradlew runClient
   ```

## 📚 Documentação Importante

- [STRUCTURE.md](STRUCTURE.md) - Arquitetura detalhada
- [CONTRIBUTING.md](CONTRIBUTING.md) - Guia de contribuição
- [DEVELOPMENT.md](DEVELOPMENT.md) - Roadmap do projeto
- [README.md](README.md) - Documentação geral

## 🤝 Antes de Contribuir

1. Leia [CONTRIBUTING.md](CONTRIBUTING.md)
2. Entenda a [estrutura](STRUCTURE.md)
3. Crie uma branch: `git checkout -b feature/sua-feature`
4. Siga as convenções de código

## ❓ Dúvidas Frequentes

**Como adicionar um novo handler de mod?**
- Implemente a interface `ModHandler`
- Registre em `ModIntegrationEngine`

**Como adicionar um novo comando?**
- Adicione em `CommandEvents.java`
- Use `CommandDispatcher<CommandSourceStack>`

**Como debugar conexão de rede?**
- Ative modo debug: `/j2b debug`
- Verifique logs em `.minecraft/logs/latest.log`

## 🔗 Links Úteis

- Forge Docs: https://docs.minecraftforge.net
- GitHub: https://github.com/Java2bedrock/Java2bedrock_bridge
- Issues: https://github.com/Java2bedrock/Java2bedrock_bridge/issues

---

Pronto para começar! 🎉

Teste com:
```bash
./gradlew runClient
```

No jogo, execute:
```
/j2b status
```

Deve ver o status do bridge!
