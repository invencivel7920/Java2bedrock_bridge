package com.javabedrock.bridge.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Utilitários de performance e monitoramento otimizados
 */
public class PerformanceUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int TIMEOUT_THREAD_POOL_SIZE = 2;
    private static final ExecutorService TIMEOUT_EXECUTOR = Executors.newFixedThreadPool(
        TIMEOUT_THREAD_POOL_SIZE,
        r -> {
            Thread t = new Thread(r, "JBB-Timeout-" + System.identityHashCode(r));
            t.setDaemon(true);
            return t;
        }
    );
    
    private PerformanceUtils() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }
    
    /**
     * Medir tempo de execução de uma operação com precisão em nanosegundos
     */
    public static <T> TimedResult<T> measureTime(Operation<T> operation) {
        Objects.requireNonNull(operation, "operation não pode ser nula");
        
        long startTime = System.nanoTime();
        try {
            T result = operation.execute();
            long elapsed = System.nanoTime() - startTime;
            return new TimedResult<>(result, elapsed);
        } catch (Exception e) {
            long elapsed = System.nanoTime() - startTime;
            LOGGER.debug("Operação falhou após {}ms", elapsed / 1_000_000);
            throw new RuntimeException("Operação falhou após " + (elapsed / 1_000_000) + "ms", e);
        }
    }
    
    /**
     * Executar operação com timeout usando ExecutorService (seguro para threads)
     */
    public static <T> T executeWithTimeout(long timeoutMs, Callable<T> operation) throws TimeoutException {
        Objects.requireNonNull(operation, "operation não pode ser nula");
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs deve ser > 0");
        }
        
        Future<T> future = TIMEOUT_EXECUTOR.submit(operation);
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new TimeoutException("Operação excedeu timeout de " + timeoutMs + "ms");
        } catch (InterruptedException e) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new TimeoutException("Operação foi interrompida durante timeout");
        } catch (ExecutionException e) {
            throw new TimeoutException("Operação falhou: " + e.getCause().getMessage());
        }
    }
    
    /**
     * Obter informações de memória em tempo real
     */
    public static MemoryStats getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        return new MemoryStats(maxMemory, totalMemory, usedMemory, freeMemory);
    }
    
    /**
     * Log de estatísticas de performance formatadas
     */
    public static void logPerformanceStats() {
        MemoryStats stats = getMemoryStats();
        Runtime runtime = Runtime.getRuntime();
        
        LOGGER.info("╔════════ Performance Stats ════════╗");
        LOGGER.info("║ CPU Cores: {}", String.format("%-24d║", runtime.availableProcessors()));
        LOGGER.info("║ Memory Usage: {}/{} MB ({:.1f}%)", 
            String.format("%-4d", stats.usedMemoryMB()),
            String.format("%-4d", stats.maxMemoryMB()),
            stats.percentageUsed());
        LOGGER.info("║ Free Memory: {} MB", String.format("%-24d║", stats.freeMemoryMB()));
        LOGGER.info("╚═══════════════════════════════════╝");
    }
    
    /**
     * Limpar recursos do executor de timeout
     */
    public static void shutdown() {
        try {
            TIMEOUT_EXECUTOR.shutdown();
            if (!TIMEOUT_EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                TIMEOUT_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            TIMEOUT_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    @FunctionalInterface
    public interface Operation<T> {
        T execute() throws Exception;
    }
    
    public static class TimedResult<T> {
        public final T result;
        public final long nanos;
        public final long millis;
        
        public TimedResult(T result, long nanos) {
            this.result = result;
            this.nanos = nanos;
            this.millis = nanos / 1_000_000;
        }
        
        @Override
        public String toString() {
            return String.format("TimedResult{time=%dms, result=%s}", millis, result);
        }
    }
    
    public record MemoryStats(
            long maxMemory,
            long totalMemory,
            long usedMemory,
            long freeMemory) {
        
        public long maxMemoryMB() { return maxMemory / (1024 * 1024); }
        public long totalMemoryMB() { return totalMemory / (1024 * 1024); }
        public long usedMemoryMB() { return usedMemory / (1024 * 1024); }
        public long freeMemoryMB() { return freeMemory / (1024 * 1024); }
        public double percentageUsed() { return (double) usedMemory / maxMemory * 100; }
    }
    
    public static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
    }
}
