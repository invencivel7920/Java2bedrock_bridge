# Getting Started - Java2Bedrock Bridge

Bem-vindo ao Java2Bedrock Bridge! Este guia irá ajudá-lo a começar em poucos minutos.

## 📋 Pré-requisitos

- **Java 17+** instalado e configurado no PATH
- **Servidor Bedrock** rodando (local ou remoto)
- **4GB+ de RAM** recomendado

### Verificar Java

```bash
java -version
```

Deve mostrar: `java version "17...` ou superior

## 🚀 Instalação Rápida

### Opção 1: Executar JAR

1. **Download** do arquivo `java2bedrock-bridge-1.0.0-alpha.jar`
2. **Execute** com clique duplo OU via terminal:
   ```bash
   java -jar java2bedrock-bridge-1.0.0-alpha.jar
   ```

A GUI abrirá automaticamente!

### Opção 2: Build do Código Fonte

```bash
# 1. Clone
git clone https://github.com/Java2bedrock/Java2bedrock_bridge.git
cd Java2bedrock_bridge

# 2. Build
./gradlew build

# 3. Execute
java -jar build/libs/java2bedrock-bridge-1.0.0-alpha.jar
```

## 🎮 Primeiro Uso

### 1️⃣ Configurar Conexão

Ao iniciar, vá até a aba **Configurações**:

1. **Host**: Endereço do seu servidor Bedrock
   - Local: `localhost`
   - Remoto: `seu-ip-ou-domain`

2. **Porta**: Padrão `19132` (não mude a menos que necessário)

3. **Timeout**: Deixe em `5000ms`

4. **Threads**: Deixe em `0` (automático)

Clique em **Salvar**

### 2️⃣ Conectar

Na aba **Dashboard**, clique em **Conectar**

Você verá:
- ✅ Status mudará para "Conectado"
- 📊 Ping será exibido
- 🟢 Luz verde de status

### 3️⃣ Monitorar

No **Dashboard**, acompanhe:
- Conexão ativa
- Blocos traduzidos em tempo real  
- Pacotes de rede enviados
- Latência da conexão

## 🎯 Operações Comuns

### Enviar Dados para Tradução

```
Aba Dashboard → Clique em "Iniciar Tradução"
```

O aplicativo irá:
1. Conectar ao servidor Bedrock
2. Iniciar tradução de blocos/itens
3. Exibir progresso em tempo real

### Habilitar Modo Debug

```
Aba Configurações → Desça até Debug → Ative "Modo Debug"
```

Você verá logs detalhados na aba **Logs**

### Ver Logs

```
Aba Logs → ScrollPane com logs em tempo real
```

Opções:
- **Filtrar** - Buscar por texto
- **Limpar** - Apagar todos os logs
- **Exportar** - Salvar logs em arquivo

## ⚙️ Configurações Avançadas

### Melhorar Performance

**Aba Configurações → Performance:**

- ↑ **Cache Ratio**: Aumentar para más blocos em cache (use com cuidado)
- ↑ **threads**: Aumentar se tiver muitos cores disponíveis

### Ativar Resource Packs

**Aba Configurações → Recursos:**

- ✓ Ativar Resource Packs
- ✓ Coletar Métricas

### Debug Completo

```toml
[debug]
enabled = true
log_level = "DEBUG"
```

Edit `j2b-config.toml` e reinicie

## 🔧 Troubleshooting

### "Conexão recusada"

**Solução:**
1. Verifique se o servidor Bedrock está rodando
2. Confirme host e porta (padrão: `localhost:19132`)
3. Tente reconectar clicando em "Desconectar" depois "Conectar"

### "Memória insuficiente"

**Solução:**
```bash
# Execute com mais memória
java -Xmx2G -jar java2bedrock-bridge-1.0.0-alpha.jar
```

### "Threads muito lentas"

**Solução:**
1. Va para aba **Configurações**
2. Aumente "Threads de Rede" para `8` ou `16`
3. Salve e reconecte

### GUI não abre

**Solução:**
```bash
# Verifique a saída de erro
java -jar java2bedrock-bridge-1.0.0-alpha.jar 2>&1 | tee error.log

# Verifique logs em:
# Windows: %APPDATA%/.j2b/logs/
# macOS: ~/Library/.j2b/logs/
# Linux: ~/.j2b/logs/
```

## 📚 Próximos Passos

1. **Ler [README.md](README.md)** - Documentação completa
2. **Ver [STRUCTURE.md](STRUCTURE.md)** - Arquitetura
3. **Contribuir em [CONTRIBUTING.md](CONTRIBUTING.md)**
4. **Abrir issues** - Reportar problemas

## 💡 Dicas

- ✨ Use "Modo Debug" para ver o que está acontecendo
- 📊 Monitore "Latência" para qualidade da conexão
- 💾 Exporte logs regularmente para análise
- 🔄 Reconecte periodicamente se tiver problemas
- ⚡ Aumente cache ratio se CPU está baixo

## 🆘 Precisa de Ajuda?

1. **Documentação**: Veja [INDEX.md](INDEX.md)
2. **GitHub Issues**: https://github.com/Java2bedrock/Java2bedrock_bridge/issues
3. **Discussões**: https://github.com/Java2bedrock/Java2bedrock_bridge/discussions

## 🎮 Características por Vir

- [ ] Import/Export de configuração
- [ ] Histórico de tradução
- [ ] Presets de configuração
- [ ] Multi-servidor suporte
- [ ] Dark mode
- [ ] Scheduler de tarefas

---

**Pronto para começar?** 🚀

1. Configure o host/porta
2. Clique em "Conectar"
3. Inicie a tradução
4. Acompanhe no Dashboard!

Qualquer dúvida, abra uma issue no GitHub! 😊
