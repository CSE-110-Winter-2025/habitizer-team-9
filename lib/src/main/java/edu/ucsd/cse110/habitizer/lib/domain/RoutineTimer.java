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
    public long oldElapsedTime = 0; // For calculating task time
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
                        //onTimeUpdate.accept((int) taskTime);
                    } else {
                        elapsedTime = Duration.between(startTime, Instant.now()).getSeconds();
                        //taskTime = Duration.between(startTime, Instant.now()).getSeconds();
                        onTimeUpdate.accept((int) elapsedTime);
                        //onTimeUpdate.accept((int) taskTime);
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
        //taskTime = 0;
        onTimeUpdate.accept(0);
    }

    public void enableMockMode() {
        mockMode = true;
        stop(); // Stop real-time tracking when entering mock mode
    }

    public void disableMockMode() {
        if (mockMode) {
            mockMode = false;
            if (elapsedTime > 0) { // Ensure elapsed time is not reset before setting startTime
                startTime = Instant.now().minusSeconds(elapsedTime);
            } else if (startTime == null) {
                startTime = Instant.now();
            }
            start(); // Restart real-time tracking
        }
    }

    public void advanceMockTime(int seconds) {
        if (!mockMode) {
            throw new IllegalStateException("Cannot advance time in real mode. Enable mock mode first.");
        }
        elapsedTime += seconds;
        //taskTime += seconds;
        onTimeUpdate.accept((int) elapsedTime);
        //onTimeUpdate.accept((int) taskTime);
    }

    public int getElapsedMinutes() {
        return (int) (elapsedTime / 60);
    }

    public long getElapsedTimeInSeconds() {
        return elapsedTime;
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

    // change to return in seconds. make sure to update the 60 in other files
    public int getTaskTime() {
        return (int)((elapsedTime - oldElapsedTime));
    }
}
