package edu.ucsd.cse110.habitizer.lib.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RoutineTimer {
    private Instant startTime;
    private ScheduledExecutorService scheduler;
    private boolean running = false;
    private Consumer<Integer> onTimeUpdate; // Observer

    public RoutineTimer(Consumer<Integer> onTimeUpdate) {
        this.onTimeUpdate = onTimeUpdate;
    }

    public void start() {
        if (!running) {
            running = true;
            startTime = Instant.now();
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(() -> {
                if (running) {
                    int secondsElapsed = (int) Duration.between(startTime, Instant.now()).getSeconds();
                    onTimeUpdate.accept(secondsElapsed); // Notify observer
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
        onTimeUpdate.accept(0); // Reset UI
    }
}
