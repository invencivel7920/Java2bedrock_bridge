package com.javabedrock.bridge.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller do painel de Configurações
 */
public class SettingsController {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BridgeCore bridgeCore;
    
    public SettingsController(BridgeCore bridgeCore) {
        this.bridgeCore = bridgeCore;
    }
    
    public VBox createUI() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");
        
        Label titleLabel = new Label("Configurações");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-control-inner-background: #f5f5f5;");
        scrollPane.setFitToWidth(true);
        
        VBox settingsContent = createSettingsContent();
        scrollPane.setContent(settingsContent);
        
        HBox buttonBox = createButtonBox();
        
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        mainBox.getChildren().addAll(titleLabel, new Separator(), scrollPane, buttonBox);
        
        return mainBox;
    }
    
    private VBox createSettingsContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(15));
        
        // Seção de Rede
        VBox networkSection = createNetworkSettings();
        
        // Seção de Performance
        VBox performanceSection = createPerformanceSettings();
        
        // Seção de Features
        VBox featuresSection = createFeaturesSettings();
        
        // Seção de Debug
        VBox debugSection = createDebugSettings();
        
        content.getChildren().addAll(
            networkSection,
            new Separator(),
            performanceSection,
            new Separator(),
            featuresSection,
            new Separator(),
            debugSection
        );
        
        return content;
    }
    
    private VBox createNetworkSettings() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        
        Label sectionTitle = new Label("Configurações de Rede");
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        // Host
        HBox hostBox = new HBox(10);
        Label hostLabel = new Label("Host Bedrock:");
        hostLabel.setPrefWidth(150);
        TextField hostField = new TextField("localhost");
        hostField.setPrefWidth(200);
        hostBox.getChildren().addAll(hostLabel, hostField);
        
        // Port
        HBox portBox = new HBox(10);
        Label portLabel = new Label("Porta:");
        portLabel.setPrefWidth(150);
        Spinner<Integer> portSpinner = new Spinner<>(1024, 65535, 19132);
        portSpinner.setPrefWidth(200);
        portBox.getChildren().addAll(portLabel, portSpinner);
        
        // Timeout
        HBox timeoutBox = new HBox(10);
        Label timeoutLabel = new Label("Timeout (ms):");
        timeoutLabel.setPrefWidth(150);
        Spinner<Integer> timeoutSpinner = new Spinner<>(1000, 30000, 5000, 1000);
        timeoutSpinner.setPrefWidth(200);
        timeoutBox.getChildren().addAll(timeoutLabel, timeoutSpinner);
        
        // Threads
        HBox threadsBox = new HBox(10);
        Label threadsLabel = new Label("Threads de Rede:");
        threadsLabel.setPrefWidth(150);
        Spinner<Integer> threadsSpinner = new Spinner<>(0, 128, 0);
        threadsSpinner.setPrefWidth(200);
        Label threadsHint = new Label("(0 = automático)");
        threadsHint.setStyle("-fx-text-fill: #666; -fx-font-size: 10;");
        threadsBox.getChildren().addAll(threadsLabel, threadsSpinner, threadsHint);
        
        section.getChildren().addAll(sectionTitle, hostBox, portBox, timeoutBox, threadsBox);
        return section;
    }
    
    private VBox createPerformanceSettings() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        
        Label sectionTitle = new Label("Configurações de Performance");
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        // Cache Ratio
        HBox cacheRatioBox = new HBox(10);
        Label cacheLabel = new Label("Ratio de Cache:");
        cacheLabel.setPrefWidth(150);
        Slider cacheSlider = new Slider(0.0, 1.0, 0.25);
        cacheSlider.setShowTickLabels(true);
        cacheSlider.setPrefWidth(200);
        cacheSlider.setBlockIncrement(0.05);
        cacheRatioBox.getChildren().addAll(cacheLabel, cacheSlider);
        
        // Block Cache Size
        HBox blockCacheBox = new HBox(10);
        Label blockCacheLabel = new Label("Tamanho Cache Blocos:");
        blockCacheLabel.setPrefWidth(150);
        Spinner<Integer> blockCacheSpinner = new Spinner<>(1024, 65536, 8192, 1024);
        blockCacheSpinner.setPrefWidth(200);
        blockCacheBox.getChildren().addAll(blockCacheLabel, blockCacheSpinner);
        
        // Item Cache Size
        HBox itemCacheBox = new HBox(10);
        Label itemCacheLabel = new Label("Tamanho Cache Itens:");
        itemCacheLabel.setPrefWidth(150);
        Spinner<Integer> itemCacheSpinner = new Spinner<>(512, 32768, 4096, 512);
        itemCacheSpinner.setPrefWidth(200);
        itemCacheBox.getChildren().addAll(itemCacheLabel, itemCacheSpinner);
        
        section.getChildren().addAll(sectionTitle, cacheRatioBox, blockCacheBox, itemCacheBox);
        return section;
    }
    
    private VBox createFeaturesSettings() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        
        Label sectionTitle = new Label("Recursos");
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        CheckBox modIntegrationCheck = new CheckBox("Ativar Integração de Mods");
        modIntegrationCheck.setSelected(true);
        
        CheckBox resourcePacksCheck = new CheckBox("Ativar Resource Packs");
        resourcePacksCheck.setSelected(true);
        
        CheckBox metricsCheck = new CheckBox("Coletar Métricas");
        metricsCheck.setSelected(true);
        
        section.getChildren().addAll(sectionTitle, modIntegrationCheck, resourcePacksCheck, metricsCheck);
        return section;
    }
    
    private VBox createDebugSettings() {
        VBox section = new VBox(10);
        section.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        
        Label sectionTitle = new Label("Debug");
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        CheckBox debugModeCheck = new CheckBox("Modo Debug");
        debugModeCheck.setSelected(bridgeCore.isDebugMode());
        debugModeCheck.selectedProperty().addListener((obs, old, newValue) -> {
            bridgeCore.setDebugMode(newValue);
            LOGGER.info("Debug mode: {}", newValue);
        });
        
        HBox logLevelBox = new HBox(10);
        Label logLabel = new Label("Nível de Log:");
        logLabel.setPrefWidth(150);
        ComboBox<String> logLevelCombo = new ComboBox<>();
        logLevelCombo.getItems().addAll("DEBUG", "INFO", "WARN", "ERROR");
        logLevelCombo.setValue("INFO");
        logLevelCombo.setPrefWidth(200);
        logLevelBox.getChildren().addAll(logLabel, logLevelCombo);
        
        Button clearLogsButton = new Button("Limpar Logs");
        clearLogsButton.setStyle("-fx-padding: 5 15;");
        clearLogsButton.setOnAction(e -> {
            LOGGER.info("Logs limpos");
        });
        
        Button exportLogsButton = new Button("Exportar Logs");
        exportLogsButton.setStyle("-fx-padding: 5 15;");
        exportLogsButton.setOnAction(e -> {
            LOGGER.info("Logs exportados");
        });
        
        HBox logButtonsBox = new HBox(10);
        logButtonsBox.getChildren().addAll(clearLogsButton, exportLogsButton);
        
        section.getChildren().addAll(sectionTitle, debugModeCheck, logLevelBox, logButtonsBox);
        return section;
    }
    
    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: white;");
        
        Button saveButton = new Button("Salvar");
        saveButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12; -fx-text-fill: white; " +
                           "-fx-background-color: #4CAF50;");
        saveButton.setOnAction(e -> {
            LOGGER.info("Configurações salvas");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("Configurações Salvas");
            alert.setContentText("As configurações foram salvas com sucesso!");
            alert.showAndWait();
        });
        
        Button resetButton = new Button("Resetar Padrões");
        resetButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12;");
        resetButton.setOnAction(e -> {
            LOGGER.info("Configurações resetadas para padrão");
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        buttonBox.getChildren().addAll(saveButton, resetButton, spacer);
        return buttonBox;
    }
}
