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
    private boolean isPaused = false;

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
            if(!isPaused) {
                start(); // Restart real-time tracking

                if (elapsedTime > 0) { // Ensure elapsed time is not reset before setting startTime
                    startTime = Instant.now().minusSeconds(elapsedTime);
                } else if (startTime == null) {
                    startTime = Instant.now();
                }
            }
        }
    }

    public void advanceMockTime(int seconds) {
        if (!mockMode) {
            throw new IllegalStateException("Cannot advance time in real mode. Enable mock mode first.");
        }
        if(!isPaused) {
            elapsedTime += seconds;
            onTimeUpdate.accept((int) elapsedTime);
        }
    }

    public int getElapsedMinutes() {
        return (int) (elapsedTime / 60);
    }

    public long getElapsedTimeInSeconds() {
        return elapsedTime;
    }

    public void pauseRoutine() {
        isPaused = true;
        stop();
    }

    public void resumeRoutine() {
        isPaused = false;

        start(); // Restart real-time tracking

        if (elapsedTime > 0) { // Ensure elapsed time is not reset before setting startTime
            startTime = Instant.now().minusSeconds(elapsedTime);
        } else if (startTime == null) {
            startTime = Instant.now();
        }
    }

    public boolean getIsMocking() {
        return mockMode;
    }

    public void setIsMocking(boolean mock) {
        mockMode = mock;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long time) {
        elapsedTime = time;
    }

    public boolean getRunning() {
        return running;
    }

    public boolean getIsPaused() {
        return isPaused;
    }
}
