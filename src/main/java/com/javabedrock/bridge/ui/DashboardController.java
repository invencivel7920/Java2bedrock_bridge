package com.javabedrock.bridge.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller do painel Dashboard
 */
public class DashboardController {
    private static final Logger LOGGER = LogManager.getLogger();
    private final BridgeCore bridgeCore;
    
    public DashboardController(BridgeCore bridgeCore) {
        this.bridgeCore = bridgeCore;
    }
    
    public VBox createUI() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");
        
        // Título
        Label titleLabel = new Label("Dashboard");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Cards de status
        HBox statusCards = createStatusCards();
        
        // Gráficos
        VBox chartsBox = createCharts();
        
        // Botões de ação
        HBox actionButtons = createActionButtons();
        
        VBox.setVgrow(chartsBox, Priority.ALWAYS);
        mainBox.getChildren().addAll(
            titleLabel,
            statusCards,
            new Separator(),
            chartsBox,
            actionButtons
        );
        
        return mainBox;
    }
    
    private HBox createStatusCards() {
        HBox cardsBox = new HBox(15);
        cardsBox.setStyle("-fx-padding: 10;");
        
        // Card 1: Conexão
        VBox card1 = createStatusCard(
            "Conexão",
            "Ativa",
            "#4CAF50",
            "localhost:19132"
        );
        
        // Card 2: Blocos Traduzidos
        VBox card2 = createStatusCard(
            "Blocos",
            "1,247",
            "#2196F3",
            "traduzidos"
        );
        
        // Card 3: Mods Carregados
        VBox card3 = createStatusCard(
            "Mods",
            "8",
            "#FF9800",
            "integrados"
        );
        
        // Card 4: Ping
        VBox card4 = createStatusCard(
            "Latência",
            "12ms",
            "#9C27B0",
            "conexão estável"
        );
        
        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);
        
        cardsBox.getChildren().addAll(card1, card2, card3, card4);
        return cardsBox;
    }
    
    private VBox createStatusCard(String title, String value, String color, String subtitle) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5; " +
                     "-fx-border-color: " + color + "; -fx-border-width: 2;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999;");
        
        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }
    
    private VBox createCharts() {
        VBox chartsBox = new VBox(15);
        chartsBox.setPadding(new Insets(10));
        chartsBox.setStyle("-fx-background-color: white;");
        
        Label chartsTitle = new Label("Métricas em Tempo Real");
        chartsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        // Placeholder para gráficos
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab blocksTab = new Tab("Blocos Traduzidos");
        blocksTab.setContent(createPlaceholder("Gráfico de blocos traduzidos ao longo do tempo"));
        
        Tab packetsTab = new Tab("Pacotes de Rede");
        packetsTab.setContent(createPlaceholder("Gráfico de pacotes enviados/recebidos"));
        
        Tab performanceTab = new Tab("Performance");
        performanceTab.setContent(createPlaceholder("Gráfico de CPU e Memória"));
        
        tabPane.getTabs().addAll(blocksTab, packetsTab, performanceTab);
        
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        chartsBox.getChildren().addAll(chartsTitle, tabPane);
        
        return chartsBox;
    }
    
    private VBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-background-color: white;");
        
        Button connectButton = new Button("Conectar");
        connectButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12;");
        connectButton.setOnAction(e -> {
            if (!bridgeCore.getNetworkManager().isConnected()) {
                bridgeCore.getNetworkManager().reconnect();
                LOGGER.info("Conexão iniciada");
            }
        });
        
        Button disconnectButton = new Button("Desconectar");
        disconnectButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12;");
        disconnectButton.setOnAction(e -> {
            bridgeCore.getNetworkManager().shutdown();
            LOGGER.info("Desconectado");
        });
        
        Button refreshButton = new Button("Atualizar");
        refreshButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12;");
        refreshButton.setOnAction(e -> {
            LOGGER.info("Dados atualizados");
        });
        
        Button startTranslationButton = new Button("Iniciar Tradução");
        startTranslationButton.setStyle("-fx-padding: 8 20; -fx-font-size: 12; -fx-text-fill: white; " +
                                       "-fx-background-color: #4CAF50;");
        startTranslationButton.setOnAction(e -> {
            LOGGER.info("Tradução iniciada");
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        buttonBox.getChildren().addAll(
            connectButton, disconnectButton, refreshButton, 
            spacer, startTranslationButton
        );
        
        return buttonBox;
    }
    
    private StackPane createPlaceholder(String text) {
        StackPane pane = new StackPane();
        pane.setStyle("-fx-background-color: #f9f9f9;");
        
        Label placeholder = new Label(text);
        placeholder.setStyle("-fx-text-fill: #999; -fx-font-size: 13;");
        
        pane.getChildren().add(placeholder);
        return pane;
    }
}
