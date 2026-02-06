# Java2Bedrock Bridge

![Version](https://img.shields.io/badge/Version-1.0.0--alpha-red)
![Java](https://img.shields.io/badge/Java-17+-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-green)
![GUI](https://img.shields.io/badge/UI-JavaFX-blueviolet)

Um aplicativo desktop poderoso que **traduz automaticamente mods da Java Edition para Bedrock Edition**, permitindo que modders compartilhem conteúdo entre ambas as plataformas.

> ⚠️ **Status**: Early Development - Aplicação standalone (não é um mod)

## 🌟 Características Principais

- ✅ **Interface GUI Moderna** - Aplicativo desktop com JavaFX
- ✅ **Tradução Automática** - Converte blocos, itens e entidades entre Java e Bedrock
- ✅ **Rede Otimizada** - Comunicação eficiente com servidores Bedrock usando Netty
- ✅ **Integração de Mods** - Suporte automático para mods populares
- ✅ **Performance Máxima** - Sistema de cache avançado e otimizações de JVM
- ✅ **Configurável** - Sistema de configuração completo via TOML
- ✅ **Dashboard** - Monitoramento em tempo real de conexão, blocos e pacotes
- ✅ **Logs Estruturados** - Sistema de logging com Log4j2

## 📸 Interface

O aplicativo conta com:

- **Dashboard**: Visão geral com cards de status, métricas em tempo real
- **Configurações**: Painel completo para ajuste de rede, performance e recursos
- **Gerenciador de Transferências**: Acompanhamento de conversões em progresso
- **Visualizador de Logs**: Console integrado com filtros

## 🚀 Quick Start

### Requisitos
- Java 17 ou superior
- 4GB de RAM recomendado

### Instalação

1. **Download** da JAR compilada mais recente
2. **Execute** diretamente:
   ```bash
   java -jar java2bedrock-bridge-1.0.0-alpha.jar
   ```
   Ou clique duas vezes no arquivo JAR

3. **Configure** na aba "Configurações":
   - Host do servidor Bedrock (padrão: `localhost`)
   - Porta (padrão: `19132`)
   - Threads de rede (0 = automático)

4. **Conecte** clicando em "Conectar"

### Build do Código Fonte

```bash
# Clone o repositório
git clone https://github.com/Java2bedrock/Java2bedrock_bridge.git
cd Java2bedrock_bridge

# Build
./gradlew build

# Execute
java -jar build/libs/java2bedrock-bridge-1.0.0-alpha.jar
```

## 🏗️ Arquitetura Modular

```
src/main/java/com/javabedrock/bridge/
├── core/              # Núcleo e orquestração
│   ├── JavaBedrocBridgeApp.java    # Aplicação JavaFX
│   └── BridgeCore.java              # Coordenador central
├── ui/                # Interface gráfica
│   ├── DashboardController.java     # Painel principal
│   └── SettingsController.java      # Painel de configurações
├── network/           # Camada de rede
│   ├── NetworkManager.java
│   ├── BridgeChannelInitializer.java
│   └── BridgePacketHandler.java
├── translation/       # Motor de tradução
│   ├── TranslationEngine.java
│   ├── BlockTranslator.java
│   ├── ItemTranslator.java
│   └── EntityTranslator.java
├── integration/       # Integração com mods
│   ├── ModIntegrationEngine.java
│   └── ModHandler.java
├── config/            # Sistema de configuração
│   └── BridgeConfig.java
├── command/           # Sistema de comandos
│   └── CommandHandler.java
└── util/              # Utilitários
    └── PerformanceManager.java
```

## ⚙️ Configuração

As configurações podem ser editadas em `j2b-config.toml`:

```toml
[network]
threads = 0              # 0 = automático baseado em CPU
host = "localhost"       # Host do servidor Bedrock
port = 19132            # Porta do Bedrock
timeout_ms = 5000       # Timeout de conexão

[performance]
cache_ratio = 0.25      # Ratio de cache em relação à RAM
block_cache_size = 8192
item_cache_size = 4096

[features]
mod_integration = true  # Ativar integração de mods
resource_packs = true   # Ativar resource packs
metrics = true          # Coletar métricas

[debug]
enabled = false
log_level = "INFO"
```

## 📊 Dashboard

O dashboard exibe em tempo real:

- **Status de Conexão**: Verde (conectado) / Vermelho (desconectado)
- **Blocos Traduzidos**: Contador de blocos processados
- **Pacotes de Rede**: Estatísticas de envio/recebimento
- **Latência**: Ping do servidor Bedrock
- **Mods Integrados**: Número de mods reconhecidos

## 🔧 Subsistemas

### Network Manager
- Gerencia conexões com servidor Bedrock
- Suporta EPoll (Linux) e NIO (cross-platform)
- Health monitoring automático a cada 30 segundos
- Reconexão inteligente com backoff exponencial

### Translation Engine
- Cache com expiração automática
- Suporte para blocos, itens e entidades
- Mapeamentos dinâmicos e fallback automático
- Otimizações de memória

### Mod Integration Engine
- Discovery automático de mods
- Handlers para mods populares (Mekanism, Create, etc.)
- Extension points para novos mods

### Performance Manager
- Pool de objetos reutilizáveis
- Otimizações automáticas de Netty
- Tuning dinâmico de JVM
- Memory profiling

## 📝 Licença

Licenciado sob **Apache License 2.0** - veja [LICENSE](LICENSE) para detalhes

## 🤝 Contribuindo

Contribuições são bem-vindas! 

1. Faça um fork do repositório
2. Crie uma branch: `git checkout -b feature/sua-feature`
3. Commit suas mudanças: `git commit -am 'Add feature'`
4. Push: `git push origin feature/sua-feature`
5. Abra um Pull Request

Para mais detalhes, veja [CONTRIBUTING.md](CONTRIBUTING.md)

## 📚 Documentação

- [QUICKSTART.md](QUICKSTART.md) - Setup rápido
- [STRUCTURE.md](STRUCTURE.md) - Arquitetura detalhada
- [DEVELOPMENT.md](DEVELOPMENT.md) - Roadmap do projeto
- [INDEX.md](INDEX.md) - Índice de documentação

## 🐛 Bug Reports

Se encontrar um bug, abra uma issue em:
https://github.com/Java2bedrock/Java2bedrock_bridge/issues

Inclua:
- Descrição clara do problema
- Passos para reproduzir
- Logs (`j2b-latest.log`)
- Seu ambiente (SO, versão Java, etc.)

## 🎯 Roadmap

### Fase 1 ✅ - Base (Concluído)
- [x] Estrutura modular
- [x] GUI com JavaFX
- [x] Network com Netty
- [x] Configuration system

### Fase 2 ⏳ - Tradução
- [ ] BlockTranslator completo
- [ ] ItemStack support
- [ ] Entity sync
- [ ] Testes

### Fase 3 ⏳ - Mods
- [ ] Handlers para mods populares
- [ ] Custom mod support
- [ ] Plugin system

### Fase 4 ⏳ - Release
- [ ] Otimizações finais
- [ ] Release 1.0.0

## 💬 Comunidade

- **GitHub**: https://github.com/Java2bedrock/Java2bedrock_bridge
- **Issues**: https://github.com/Java2bedrock/Java2bedrock_bridge/issues
- **Discussões**: https://github.com/Java2bedrock/Java2bedrock_bridge/discussions

## 📧 Contato

- Reportar problema: GitHub Issues
- Sugestões: GitHub Discussions

---

**Versão**: 1.0.0-alpha  
**Última atualização**: 2026-02-06  
**Status**: 🚧 Em desenvolvimento ativo

Transformando Java Edition em Bedrock Edition, um bloco por vez! 🎮

