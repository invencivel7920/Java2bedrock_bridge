package com.javabedrock.bridge.data;

import java.util.UUID;

/**
 * Representa uma sessão de jogador conectado
 */
public class PlayerSession {
    private final UUID playerUuid;
    private final String playerName;
    private final long createdAt;
    
    private volatile boolean active = true;
    private volatile long lastActivity;
    
    public PlayerSession(UUID playerUuid, String playerName) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.createdAt = System.currentTimeMillis();
        this.lastActivity = createdAt;
    }
    
    public UUID getPlayerUuid() { return playerUuid; }
    public String getPlayerName() { return playerName; }
    public long getCreatedAt() { return createdAt; }
    public long getLastActivity() { return lastActivity; }
    public boolean isActive() { return active; }
    
    public void updateActivity() {
        this.lastActivity = System.currentTimeMillis();
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public long getSessionDuration() {
        return System.currentTimeMillis() - createdAt;
    }
}
