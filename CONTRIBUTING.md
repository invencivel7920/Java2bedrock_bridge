# Contribuindo para Java2Bedrock Bridge

Obrigado por estar interessado em contribuir! Aqui estão as diretrizes para fazer isso.

## Como Começar

1. **Fork** o repositório
2. **Clone** o seu fork
   ```bash
   git clone https://github.com/seu-usuario/Java2bedrock_bridge.git
   cd Java2bedrock_bridge
   ```
3. **Configure** o ambiente de desenvolvimento
   ```bash
   ./gradlew idea
   ```

## Desenvolvimento

### Setup do IDE (IntelliJ IDEA)
```bash
./gradlew genIntelliJRuns
```

Abra o projeto em IDEA e configure o JDK 17.

### Build e Teste
```bash
./gradlew build          # Build completo
./gradlew runClient      # Executar cliente teste
./gradlew runServer      # Executar servidor teste
```

## Estrutura de Código

Mantenha a seguinte estrutura:

```
com.javabedrock.bridge.{subsystem}.{ClassName}
```

### Subsistemas
- `core` - Coordenação central
- `network` - Networking
- `translation` - Motor de tradução
- `integration` - Integração com mods
- `config` - Configuração
- `command` - Comandos
- `util` - Utilitários

## Conventions

### Naming
- Classes: `PascalCase`
- Métodos: `camelCase`
- Constantes: `UPPER_SNAKE_CASE`
- Pacotes: `lowercase.delimited.by.dots`

### Javadoc
```java
/**
 * Descrição breve da classe.
 * 
 * Descrição detalhada se necessário.
 * 
 * @author Nome do Autor
 * @since 1.0.0
 */
```

### Logging
```java
private static final Logger LOGGER = LogManager.getLogger();

LOGGER.info("Mensagem informativa");
LOGGER.warn("Aviso");
LOGGER.error("Erro", exception);
LOGGER.debug("Debug info");
```

## Pull Request Process

1. **Update** suas bases com `origin/master`
2. **Create** uma branch: `git checkout -b feature/sua-feature`
3. **Commit** suas mudanças: `git commit -m "type: descrição"`
4. **Push** para seu fork: `git push origin feature/sua-feature`
5. **Open** um Pull Request

### Tipos de Commit
- `feat:` Nova feature
- `fix:` Correção de bug
- `docs:` Alterações de documentação
- `refactor:` Refatoração de código
- `perf:` Melhorias de performance
- `test:` Adição de testes
- `chore:` Alterações de build/deps

Exemplo:
```
feat: adicionar suporte a novos mods

- Implementar ModHandler interface
- Criar handlers para Mekanism
- Adicionar testes
```

## Reporte de Bugs

Use a template de issue no GitHub:
- Descrição clara do problema
- Passos para reproduzir
- Comportamento esperado vs atual
- Logs relevantes
- Sistema operacional e versão

## Sugestões

Discusses novas ideias em "Discussions" antes de abrir PRs.

## Código de Conduta

- Seja respeitoso
- Aceite críticas construtivas
- Focado em melhoria do projeto
- Sem discriminação

---

Obrigado por contribuir! 🎉
