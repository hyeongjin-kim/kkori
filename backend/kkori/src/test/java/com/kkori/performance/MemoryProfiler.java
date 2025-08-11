package com.kkori.performance;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * 무료 메모리 프로파일링 유틸리티
 * JVM 기본 제공 도구 활용
 */
public class MemoryProfiler {
    
    private final MemoryMXBean memoryBean;
    private MemorySnapshot beforeSnapshot;
    
    public MemoryProfiler() {
        this.memoryBean = ManagementFactory.getMemoryMXBean();
    }
    
    public void startProfiling() {
        // 강제 GC로 정확한 측정
        System.gc();
        Thread.yield(); // GC 완료 대기
        try {
            Thread.sleep(100); // GC 안정화 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        this.beforeSnapshot = takeSnapshot();
        System.out.println("🚀 메모리 프로파일링 시작");
        System.out.println("시작 시점 힙 사용량: " + formatBytes(beforeSnapshot.heapUsed));
    }
    
    public MemoryReport stopProfiling() {
        // 강제 GC 없이 현재 상태 측정 (실제 사용량 확인)
        MemorySnapshot afterSnapshot = takeSnapshot();
        
        long heapDiff = afterSnapshot.heapUsed - beforeSnapshot.heapUsed;
        long nonHeapDiff = afterSnapshot.nonHeapUsed - beforeSnapshot.nonHeapUsed;
        
        MemoryReport report = new MemoryReport(
                beforeSnapshot,
                afterSnapshot,
                heapDiff,
                nonHeapDiff
        );
        
        System.out.println("🏁 메모리 프로파일링 완료");
        System.out.println("종료 시점 힙 사용량: " + formatBytes(afterSnapshot.heapUsed));
        System.out.println("힙 메모리 증가량: " + formatBytes(heapDiff));
        
        return report;
    }
    
    private MemorySnapshot takeSnapshot() {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
        
        return new MemorySnapshot(
                heapUsage.getUsed(),
                heapUsage.getMax(),
                nonHeapUsage.getUsed(),
                System.currentTimeMillis()
        );
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
    }
    
    public static class MemorySnapshot {
        public final long heapUsed;
        public final long heapMax;
        public final long nonHeapUsed;
        public final long timestamp;
        
        public MemorySnapshot(long heapUsed, long heapMax, long nonHeapUsed, long timestamp) {
            this.heapUsed = heapUsed;
            this.heapMax = heapMax;
            this.nonHeapUsed = nonHeapUsed;
            this.timestamp = timestamp;
        }
    }
    
    public static class MemoryReport {
        public final MemorySnapshot before;
        public final MemorySnapshot after;
        public final long heapDifference;
        public final long nonHeapDifference;
        
        public MemoryReport(MemorySnapshot before, MemorySnapshot after, 
                           long heapDifference, long nonHeapDifference) {
            this.before = before;
            this.after = after;
            this.heapDifference = heapDifference;
            this.nonHeapDifference = nonHeapDifference;
        }
        
        public void printDetailedReport() {
            System.out.println("\n📊 상세 메모리 리포트");
            System.out.println("=" .repeat(50));
            System.out.println("🔸 힙 메모리");
            System.out.println("  - 시작: " + formatBytes(before.heapUsed));
            System.out.println("  - 종료: " + formatBytes(after.heapUsed));
            System.out.println("  - 증가: " + formatBytes(heapDifference));
            System.out.println("🔸 논힙 메모리");
            System.out.println("  - 시작: " + formatBytes(before.nonHeapUsed));
            System.out.println("  - 종료: " + formatBytes(after.nonHeapUsed));
            System.out.println("  - 증가: " + formatBytes(nonHeapDifference));
            System.out.println("=" .repeat(50));
        }
        
        private String formatBytes(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}