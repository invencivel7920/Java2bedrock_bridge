package com.javabedrock.bridge;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.javabedrock.bridge.ui.DashboardController;
import com.javabedrock.bridge.ui.SettingsController;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Aplicação JavaFX principal do Java2Bedrock Bridge
 * Interface gráfica para gerenciamento da tradução Java->Bedrock
 */
public class JavaBedrocBridgeApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String APP_TITLE = "Java2Bedrock Bridge v1.0.0-alpha";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;
    private static final long STATUS_UPDATE_INTERVAL_MS = 1000; // 1 segundo
    
    private BridgeCore bridgeCore;
    private TabPane mainTabs;
    private DashboardController dashboardController;
    private SettingsController settingsController;
    private ScheduledExecutorService statusUpdateExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    
    @Override
    public void start(Stage primaryStage) {
        try {
            LOGGER.info("Iniciando Java2Bedrock Bridge - Aplicação Desktop");
            
            // Inicializar core
            bridgeCore = new BridgeCore();
            bridgeCore.initialize();
            
            // Criar interface
            createUI(primaryStage);
            
            LOGGER.info("Interface criada com sucesso");
        } catch (Exception e) {
            LOGGER.error("Erro ao iniciar aplicação", e);
            showErrorDialog("Erro de Inicialização", "Falha ao iniciar Java2Bedrock Bridge", e.getMessage());
        }
    }
    
    private void createUI(Stage primaryStage) {
        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', 'Arial'; -fx-font-size: 11;");
        
        // Menu bar
        root.setTop(createMenuBar());
        
        // Main tabs
        mainTabs = createMainTabs();
        root.setCenter(mainTabs);
        
        // Status bar
        root.setBottom(createStatusBar());
        
        // Scene e stage
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> onApplicationClose());
        primaryStage.show();
        
        LOGGER.debug("Janela principal exibida");
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Menu Arquivo
        Menu fileMenu = new Menu("Arquivo");
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);
        
        // Menu Editar
        Menu editMenu = new Menu("Editar");
        MenuItem settingsItem = new MenuItem("Configurações");
        settingsItem.setOnAction(e -> mainTabs.getSelectionModel().select(1));
        editMenu.getItems().addAll(
            settingsItem,
            new SeparatorMenuItem(),
            new MenuItem("Limpar Cache")
        );
        
        // Menu Exibir
        Menu viewMenu = new Menu("Exibir");
        CheckMenuItem debugItem = new CheckMenuItem("Modo Debug");
        debugItem.selectedProperty().addListener((obs, old, newValue) -> {
            bridgeCore.setDebugMode(newValue);
        });
        viewMenu.getItems().add(debugItem);
        
        // Menu Ajuda
        Menu helpMenu = new Menu("Ajuda");
        MenuItem aboutItem = new MenuItem("Sobre");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().addAll(
            aboutItem,
            new MenuItem("Documentação"),
            new MenuItem("Reportar Problema")
        );
        
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, helpMenu);
        return menuBar;
    }
    
    private TabPane createMainTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Aba Dashboard
        Tab dashboardTab = new Tab("Dashboard", createDashboard());
        dashboardTab.setStyle("-fx-text-base-color: #000;");
        
        // Aba Configurações
        Tab settingsTab = new Tab("Configurações", createSettings());
        
        // Aba Transferências
        Tab transferTab = new Tab("Transferências", createTransfers());
        
        // Aba Logs
        Tab logsTab = new Tab("Logs", createLogs());
        
        tabPane.getTabs().addAll(dashboardTab, settingsTab, transferTab, logsTab);
        return tabPane;
    }
    
    private VBox createDashboard() {
        dashboardController = new DashboardController(bridgeCore);
        return dashboardController.createUI();
    }
    
    private VBox createSettings() {
        settingsController = new SettingsController(bridgeCore);
        return settingsController.createUI();
    }
    
    private VBox createTransfers() {
        VBox transferBox = new VBox(10);
        transferBox.setPadding(new Insets(15));
        
        Label titleLabel = new Label("Gerenciador de Transferências");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        TableView<TransferItem> table = new TableView<>();
        
        TableColumn<TransferItem, String> nameCol = new TableColumn<>("Nome");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        TableColumn<TransferItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        TableColumn<TransferItem, Double> progressCol = new TableColumn<>("Progresso");
        progressCol.setCellValueFactory(cellData -> cellData.getValue().progressProperty().asObject());
        
        table.getColumns().addAll(nameCol, statusCol, progressCol);
        
        VBox.setVgrow(table, Priority.ALWAYS);
        transferBox.getChildren().addAll(titleLabel, table);
        
        return transferBox;
    }
    
    private VBox createLogs() {
        VBox logsBox = new VBox(10);
        logsBox.setPadding(new Insets(15));
        
        HBox toolbarBox = new HBox(10);
        toolbarBox.setPadding(new Insets(5));
        
        TextField filterBox = new TextField();
        filterBox.setPromptText("Filtrar logs...");
        filterBox.setPrefWidth(200);
        
        Button clearButton = new Button("Limpar");
        clearButton.setStyle("-fx-padding: 5 15;");
        
        Button exportButton = new Button("Exportar");
        exportButton.setStyle("-fx-padding: 5 15;");
        
        toolbarBox.getChildren().addAll(filterBox, clearButton, exportButton);
        
        TextArea logsArea = new TextArea();
        logsArea.setWrapText(false);
        logsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        logsArea.setText("[INFO] Java2Bedrock Bridge iniciado\n" +
                        "[DEBUG] Core systems initialized\n" +
                        "[INFO] Conexão com Bedrock estabelecida\n");
        
        VBox.setVgrow(logsArea, Priority.ALWAYS);
        logsBox.getChildren().addAll(toolbarBox, logsArea);
        
        return logsBox;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        
        Label statusLabel = new Label("Status: Conectado");
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        
        Label blockCountLabel = new Label("Blocos: 0");
        Label packetCountLabel = new Label("Pacotes: 0");
        Label connectionLabel = new Label("Conexão: Ativa");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("v1.0.0-alpha");
        versionLabel.setStyle("-fx-text-fill: #666;");
        
        statusBar.getChildren().addAll(
            statusLabel, new Separator(), blockCountLabel, 
            packetCountLabel, connectionLabel, spacer, versionLabel
        );
        
        // Atualizar labels periodicamente
        updateStatusBar(statusLabel, blockCountLabel, packetCountLabel);
        
        return statusBar;
    }
    
    private void updateStatusBar(Label status, Label blocks, Label packets) {
        // Usar ScheduledExecutorService para atualizar status bar seguramente
        statusUpdateExecutor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "JBB-StatusUpdater");
            t.setDaemon(true);
            return t;
        });
        
        isRunning.set(true);
        
        statusUpdateExecutor.scheduleAtFixedRate(() -> {
            if (!isRunning.get()) {
                return; // Para a execução se a app foi fechada
            }
            
            try {
                long blocksCount = bridgeCore.getTotalTranslatedBlocks().get();
                long packetsCount = bridgeCore.getTotalNetworkPackets().get();
                boolean isConnected = bridgeCore.getNetworkManager().isConnected();
                
                // Atualizar UI de forma thread-safe
                Platform.runLater(() -> {
                    blocks.setText("Blocos: " + blocksCount);
                    packets.setText("Pacotes: " + packetsCount);
                    
                    String statusText = isConnected ? "✓ Conectado" : "✗ Desconectado";
                    status.setText("Status: " + statusText);
                    status.setStyle("-fx-text-fill: " + 
                        (isConnected ? "green" : "red") + "; -fx-font-weight: bold;");
                });
            } catch (Exception e) {
                LOGGER.debug("Erro ao atualizar status bar", e);
            }
        }, 0, STATUS_UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sobre Java2Bedrock Bridge");
        alert.setHeaderText("Java2Bedrock Bridge v1.0.0-alpha");
        alert.setContentText("Tradutor inteligente de mods Java para Bedrock Edition\n\n" +
                           "© 2026 Java2Bedrock Team\n" +
                           "Licença: Apache 2.0\n\n" +
                           "GitHub: https://github.com/Java2bedrock/Java2bedrock_bridge");
        alert.showAndWait();
    }
    
    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void onApplicationClose() {
        LOGGER.info("Encerrando aplicação...");
        
        // Parar status update
        isRunning.set(false);
        if (statusUpdateExecutor != null && !statusUpdateExecutor.isShutdown()) {
            try {
                statusUpdateExecutor.shutdown();
                if (!statusUpdateExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    statusUpdateExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupção ao desligar status updater", e);
                statusUpdateExecutor.shutdownNow();
            }
        }
        
        // Desligar bridge core
        if (bridgeCore != null) {
            try {
                bridgeCore.shutdown();
            } catch (Exception e) {
                LOGGER.error("Erro ao desligar bridge core", e);
            }
        }
        
        LOGGER.info("Aplicação encerrada");
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // Classe para itens da tabela de transferências
    public static class TransferItem {
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty status;
        private final javafx.beans.property.SimpleDoubleProperty progress;
        
        public TransferItem(String name, String status, double progress) {
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.progress = new javafx.beans.property.SimpleDoubleProperty(progress);
        }
        
        public javafx.beans.property.StringProperty nameProperty() { return name; }
        public javafx.beans.property.StringProperty statusProperty() { return status; }
        public javafx.beans.property.DoubleProperty progressProperty() { return progress; }
    }
}
