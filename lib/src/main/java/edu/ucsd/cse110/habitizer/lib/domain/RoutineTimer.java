package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RoutineTimer {
    private Instant startTime;
    private long elapsedTime = 0; // Store elapsed time in seconds
    public long oldElapsedTime = 0;
    private boolean mockMode = false;
    private ScheduledExecutorService scheduler;
    private boolean running = false;
    private final Consumer<Integer> onTimeUpdate;

    public RoutineTimer(Consumer<Integer> onTimeUpdate) {
        this.onTimeUpdate = onTimeUpdate;
    }

    public void start() {
        if (!running) {
            running = true;
            if (!mockMode && startTime == null) {
                startTime = Instant.now();
            }
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(() -> {
                if (running) {
                    if (mockMode) {
                        onTimeUpdate.accept((int) elapsedTime);
                    } else {
                        elapsedTime = Duration.between(startTime, Instant.now()).getSeconds();
                        onTimeUpdate.accept((int) elapsedTime);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }


    public void stop() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public void reset() {
        stop();
        elapsedTime = 0;
        onTimeUpdate.accept(0);
    }

    public void enableMockMode() {
        mockMode = true;
        stop(); // Stop real-time tracking when entering mock mode
    }

    public void disableMockMode() {
        if (mockMode) {
            mockMode = false;
            if (startTime == null) {
                startTime = Instant.now();
            }
            // Synchronize elapsed time
            startTime = Instant.now().minusSeconds(elapsedTime);
        }
    }



    public void advanceMockTime(int seconds) {
        if (!mockMode) {
            throw new IllegalStateException("Cannot advance time in real mode. Enable mock mode first.");
        }
        elapsedTime += seconds;
        onTimeUpdate.accept((int) elapsedTime);
    }

    public int getElapsedMinutes() {
        return (int) (elapsedTime / 60);
    }

    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }
}
