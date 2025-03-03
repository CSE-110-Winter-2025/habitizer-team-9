package edu.ucsd.cse110.habitizer.app;

import android.app.Application;
import android.util.Log;

import edu.ucsd.cse110.habitizer.app.data.AppDatabase;
import edu.ucsd.cse110.habitizer.app.data.RoomRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

import java.util.List;
import java.util.concurrent.Executors;

public class HabitizerApplication extends Application {
    private AppDatabase db;
    private RoutineRepository routineRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the Room database
        this.db = AppDatabase.getInstance(this);
        
        // Create the Room repository
        this.routineRepository = new RoomRoutineRepository(db);
        
        // Initialize with default data if needed
        initializeDefaultDataIfNeeded();
        
        Log.d("HabitizerApplication", "Data initialized with persistence");
    }
    
    private void initializeDefaultDataIfNeeded() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if we have any routines
            if (routineRepository.findAll().getValue() == null || 
                routineRepository.findAll().getValue().isEmpty()) {
                
                Log.d("HabitizerApplication", "Initializing default data");
                
                // Create default routines
                Routine morningRoutine = new Routine(0, "Morning Routine");
                Routine eveningRoutine = new Routine(1, "Evening Routine");
                
                // Save routines
                routineRepository.save(List.of(morningRoutine, eveningRoutine));
                
                // Add default tasks to morning routine
                routineRepository.addTask(morningRoutine, new Task(0, "Shower"));
                routineRepository.addTask(morningRoutine, new Task(1, "Brush teeth"));
                routineRepository.addTask(morningRoutine, new Task(2, "Dress"));
                routineRepository.addTask(morningRoutine, new Task(3, "Make coffee"));
                routineRepository.addTask(morningRoutine, new Task(4, "Make lunch"));
                routineRepository.addTask(morningRoutine, new Task(5, "Dinner prep"));
                routineRepository.addTask(morningRoutine, new Task(6, "Pack bag"));
                
                // Add default tasks to evening routine
                routineRepository.addTask(eveningRoutine, new Task(0, "Charge devices"));
                routineRepository.addTask(eveningRoutine, new Task(1, "Prepare Dinner"));
                routineRepository.addTask(eveningRoutine, new Task(2, "Eat Dinner"));
                routineRepository.addTask(eveningRoutine, new Task(3, "Wash dishes"));
                routineRepository.addTask(eveningRoutine, new Task(4, "Pack bag"));
                routineRepository.addTask(eveningRoutine, new Task(5, "Do homework"));
            }
        });
    }

    public RoutineRepository getRoutineRepository() {
        return routineRepository;
    }
}
