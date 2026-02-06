# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

## [1.0.0-alpha] - 2026-02-06

### Adicionado
- Estrutura base do projeto com separação de módulos
- Network Manager com suporte a Netty (EPoll/NIO)
- Translation Engine com sistema de cache
- Mod Integration Engine
- Performance Manager com otimizações de JVM
- Sistema de configuração via ForgeConfig
- Comandos in-game (`/j2b`)
- Logging estruturado com Log4j2

### Corrigido
- Removido código monolítico antigo
- Estrutura de pacotes reorganizada
- Dependências atualizadas para versões estáveis
- Configuração de mod Forge corrigida

### Planejado
- Implementação completa de BlockTranslator
- Suporte a ItemStack
- Entity synchronization
- Handlers para mods populares (Mekanism, Create, etc.)
- Sistema de resource packs
- Testes unitários

### Conhecido
- Alpha release - funcionalidade básica
- Tradução de blocos em desenvolvimento
- Sem suporte a mods de terceiros ainda
