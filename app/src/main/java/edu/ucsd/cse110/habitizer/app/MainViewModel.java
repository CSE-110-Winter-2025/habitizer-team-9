package edu.ucsd.cse110.habitizer.app;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

public class MainViewModel extends ViewModel {
    private final RoutineRepository routineRepository;
    private RoutineTimer routineTimer;

    private final PlainMutableSubject<List<Routine>> routines;
    private final PlainMutableSubject<Map<Routine, List<Task>>> routineTasks;


    public static final ViewModelInitializer<MainViewModel> initializer = new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                var app = (HabitizerApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new MainViewModel(app.getRoutineRepository());
            }
    );

    public MainViewModel(RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
        this.routines = new PlainMutableSubject<>();
        this.routineTasks = new PlainMutableSubject<>();

        routineRepository.findAll().observe(routines -> {
           if(routines == null) return;
           this.routines.setValue(routines);
        });

        routineRepository.findAllMappings().observe(map -> {
           if(map == null) return;
           this.routineTasks.setValue(map);
        });
    }

    public void advanceMockTime() {
        if(routineTimer.getIsMocking()) {
            routineTimer.advanceMockTime(30);
        }
    }

    public void toggleMockMode(boolean isChecked) {
        if (isChecked) {
            routineTimer.enableMockMode();
        } else {
            routineTimer.disableMockMode();
        }
    }

    public String endRoutine() {
        getRoutineTimer().stop();

        // Calculate the total time
        long totalTime = (long) Math.ceil(getRoutineTimer().getElapsedTimeInSeconds() / 60.0);
        return "Routine Ended. Total time taken: " + totalTime + "m";
    }

    public Subject<Map<Routine, List<Task>>>  getMap() {return routineTasks;}

    public Subject<List<Routine>> getRoutines() {return routines;}

    public List<Routine> getListRoutines() {return routines.getValue();}

    public void addTask(Routine routine, Task task)
    {
        try {
            routineRepository.addTask(routine, task);
            
            // Wait a moment before refreshing data to avoid race conditions
            // We don't need to force a refresh now since our repository handles it
        } catch (Exception e) {
            Log.e("MainViewModel", "Error adding task: " + e.getMessage());
        }
    }
    
    // Add this method to force refresh data
    private void refreshData() {
        // Get fresh data from repository
        var freshRoutines = routineRepository.findAll().getValue();
        var freshMap = routineRepository.findAllMappings().getValue();
        
        // Update our subjects with fresh data
        if (freshRoutines != null) {
            this.routines.setValue(freshRoutines);
        }
        
        if (freshMap != null) {
            this.routineTasks.setValue(freshMap);
        }
    }

    public void updateTaskName(int taskId, String newName) {
        routineRepository.updateTaskName(taskId, newName);

        // Force refresh data to reflect the update in UI
        refreshData();
    }

    public void addRoutine(Routine routine)
    {
        routineRepository.addRoutine(routine);
    }

    public void moveTaskUp(Routine routine, Task task) {
        routineRepository.moveTaskUp(routine, task);
        Log.d("MVM", "Something is going on here!");
        refreshData();
    }

    public void moveTaskDown(Routine routine, Task task) {
        routineRepository.moveTaskDown(routine, task);
      
        refreshData();
    }
  
    public RoutineTimer getRoutineTimer() {
        return routineTimer;
    }

    public void setRoutineTimer(RoutineTimer routineTimer) {
        this.routineTimer = routineTimer;
    }
  
    public void renameRoutine(Routine routine, String newName) {
        routineRepository.renameRoutine(routine, newName);
    }

}
