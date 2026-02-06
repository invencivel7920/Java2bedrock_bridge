# ⚠️ ARQUIVO LEGADO

Este arquivo (`Java2bedrock_bridge.java`) foi refatorado e dividido em múltiplos módulos.

## O que aconteceu?

A estrutura monolítica original foi decomposta em uma arquitetura modular com separação clara de responsabilidades.

## Arquivo Original vs. Nova Estrutura

- **Original**: 1 arquivo com 610 linhas, múltiplas classes internas
- **Novo**: 16 arquivos Java organizados em 8 pacotes

## Localização das Classes

Procure pelas classes originais nestes locais:

```
Java2BedrockBridge.java          → src/main/java/com/javabedrock/bridge/core/Java2BedrockBridge.java
TranslationEngine                 → src/main/java/com/javabedrock/bridge/translation/TranslationEngine.java
BlockTranslator                   → src/main/java/com/javabedrock/bridge/translation/BlockTranslator.java
ItemTranslator                    → src/main/java/com/javabedrock/bridge/translation/ItemTranslator.java
EntityTranslator                  → src/main/java/com/javabedrock/bridge/translation/EntityTranslator.java
NetworkManager                    → src/main/java/com/javabedrock/bridge/network/NetworkManager.java
ModIntegrationEngine              → src/main/java/com/javabedrock/bridge/integration/ModIntegrationEngine.java
CommandSystem                     → src/main/java/com/javabedrock/bridge/command/CommandEvents.java
PerformanceManager                → src/main/java/com/javabedrock/bridge/util/PerformanceManager.java
BridgeConfig                      → src/main/java/com/javabedrock/bridge/config/BridgeConfig.java
```

## Por que foi refatorado?

1. ✅ Melhor manutenibilidade
2. ✅ Separação de responsabilidades
3. ✅ Testes unitários mais fáceis
4. ✅ Reutilização de código
5. ✅ Escalabilidade
6. ✅ Documentação clara

## Referência

Para entender a nova estrutura, consulte:
- [STRUCTURE.md](../STRUCTURE.md) - Arquitetura
- [INDEX.md](../INDEX.md) - Índice de documentação
- [QUICKSTART.md](../QUICKSTART.md) - Quick start

Este arquivo está sendo mantido como referência histórica.
