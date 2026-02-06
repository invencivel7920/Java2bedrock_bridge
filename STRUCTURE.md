## Resumo da Estrutura do Projeto Java2Bedrock Bridge

### Organização de Pacotes

```
com.javabedrock.bridge
├── core/
│   ├── Java2BedrockBridge.java      # Classe principal @Mod
│   └── BridgeCore.java              # Orquestrador central
│
├── network/
│   ├── NetworkManager.java          # Gerenciador de conexão
│   ├── BridgeChannelInitializer.java # Pipeline do Netty
│   └── BridgePacketHandler.java     # Handler de pacotes
│
├── translation/
│   ├── TranslationEngine.java       # Orquestrador
│   ├── BlockTranslator.java         # Tradução de blocos
│   ├── ItemTranslator.java          # Tradução de itens
│   └── EntityTranslator.java        # Tradução de entidades
│
├── integration/
│   ├── ModIntegrationEngine.java    # Orquestrador
│   └── ModHandler.java              # Interface para handlers
│
├── config/
│   └── BridgeConfig.java            # Configuração ForgeConfig
│
├── command/
│   └── CommandEvents.java           # Registrador de comandos
│
├── event/
│   └── BridgeEvents.java            # Definição de eventos
│
├── data/
│   └── PlayerSession.java           # Dados de sessão
│
└── util/
    └── PerformanceManager.java      # Otimizações
```

### Componentes Principais

**BridgeCore** - Núcleo que coordena todos os subsistemas
- Inicializa os managers
- Executa verificações periódicas
- Gerencia ciclo de vida

**NetworkManager** - Comunicação com Bedrock
- Conexão via Netty (EPoll/NIO)
- Health monitoring
- Reconexão automática

**TranslationEngine** - Tradução de conteúdo
- Cache com expiração
- Tradução de blocos/itens/entidades
- Suporte a mod overrides

**ModIntegrationEngine** - Integração com mods
- Discovery automático
- Handlers customizados
- Extension points

**PerformanceManager** - Otimizações
- Configurações de JVM
- Pooling de objetos
- Debug mode

### Configuração

Arquivo: `j2b-config.toml`
- Network settings
- Performance tuning
- Feature flags

### Comandos

- `/j2b status` - Status atual
- `/j2b debug` - Toggle modo debug
- `/j2b reload` - Recarrega config

### Fluxo de Inicialização

1. Classe principal `@Mod` é carregada
2. `BridgeCore` é instanciado
3. Subsistemas são inicializados:
   - PerformanceManager (otimizações JVM)
   - TranslationEngine (carrega mapeamentos)
   - ModIntegrationEngine (descobre mods)
   - NetworkManager (conecta ao Bedrock)
4. Health checks são agendados

### Extensibilidade

- **Novos Tradutores**: Estender classes de Translator
- **Novos Mods**: Implementar ModHandler interface
- **Eventos**: Usar BridgeEvents para hooks
- **Comandos**: Agregar em CommandEvents

### Dependências Principais

- Minecraft 1.20.1
- Forge 47.2.0+
- Netty 4.1.96
- Log4j2 2.20.0
- Guava 32.1.3
- Night-Config 3.6.7
- ForgeConfig (integrado)

### Próximas Implementações

- [ ] Terminar BlockTranslator
- [ ] ItemStack support
- [ ] Entity sync
- [ ] Handlers para mods (Mekanism, Create, etc.)
- [ ] Resource packs
- [ ] Métricas/telemetria
- [ ] Sistema de cache distribuído
- [ ] Replicação de mundo
