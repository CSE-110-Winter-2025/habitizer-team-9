package edu.ucsd.cse110.habitizer.app.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

public class RoomRoutineRepository extends RoutineRepository {
    private static final String TAG = "RoomRoutineRepository";
    private final AppDatabase db;
    private final PlainMutableSubject<List<Routine>> allRoutinesSubject;
    private final Map<Integer, PlainMutableSubject<Routine>> routineSubjects;
    private final PlainMutableSubject<Map<Routine, List<Task>>> allRoutineTasksSubject;
    private final Map<Routine, PlainMutableSubject<List<Task>>> routineTaskSubjects;
    
    public RoomRoutineRepository(AppDatabase db) {
        super(null); // We're not using the parent class's InMemoryDataSource
        this.db = db;
        this.allRoutinesSubject = new PlainMutableSubject<>(new ArrayList<>());
        this.routineSubjects = new HashMap<>();
        this.allRoutineTasksSubject = new PlainMutableSubject<>(new HashMap<>());
        this.routineTaskSubjects = new HashMap<>();
        
        // Initial load of data from database
        refreshData();
    }
    
    private void refreshData() {
        try {
            // Load all routines from database
            List<RoutineEntity> routineEntities = db.routineDao().getAll();
            List<Routine> routines = new ArrayList<>();
            Map<Routine, List<Task>> routineTaskMap = new HashMap<>();
            
            for (RoutineEntity entity : routineEntities) {
                Routine routine = entity.toDomain();
                routines.add(routine);
                
                // Load tasks for this routine
                List<TaskEntity> taskEntities = db.taskDao().getTasksByRoutineId(routine.id());
                List<Task> tasks = new ArrayList<>();
                
                for (TaskEntity taskEntity : taskEntities) {
                    tasks.add(taskEntity.toDomain());
                }
                
                routineTaskMap.put(routine, tasks);
                
                // Update routine subject if it exists
                if (routineSubjects.containsKey(routine.id())) {
                    routineSubjects.get(routine.id()).setValue(routine);
                }
                
                // Update task subject if it exists
                if (routineTaskSubjects.containsKey(routine)) {
                    routineTaskSubjects.get(routine).setValue(tasks);
                } else {
                    routineTaskSubjects.put(routine, new PlainMutableSubject<>(tasks));
                }
            }
            
            // Update all subjects
            allRoutinesSubject.setValue(routines);
            allRoutineTasksSubject.setValue(routineTaskMap);
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing data from database", e);
        }
    }
    
    @Override
    public Subject<Routine> find(int id) {
        if (!routineSubjects.containsKey(id)) {
            routineSubjects.put(id, new PlainMutableSubject<>());
            
            try {
                RoutineEntity entity = db.routineDao().getById(id);
                if (entity != null) {
                    routineSubjects.get(id).setValue(entity.toDomain());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error finding routine with id " + id, e);
            }
        }
        return routineSubjects.get(id);
    }
    
    @Override
    public Subject<List<Routine>> findAll() {
        return allRoutinesSubject;
    }
    
    @Override
    public Subject<Map<Routine, List<Task>>> findAllMappings() {
        return allRoutineTasksSubject;
    }
    
    @Override
    public void save(Routine routine) {
        try {
            // Save routine to database
            db.routineDao().insert(RoutineEntity.fromDomain(routine));
            
            // Update subjects
            if (routineSubjects.containsKey(routine.id())) {
                routineSubjects.get(routine.id()).setValue(routine);
            } else {
                routineSubjects.put(routine.id(), new PlainMutableSubject<>(routine));
            }
            
            // Refresh all data to ensure consistency
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error saving routine: " + routine.getName(), e);
        }
    }
    
    @Override
    public void save(List<Routine> routines) {
        try {
            // Convert to entities
            List<RoutineEntity> entities = new ArrayList<>();
            for (Routine routine : routines) {
                entities.add(RoutineEntity.fromDomain(routine));
            }
            
            // Save to database
            db.routineDao().insertAll(entities);
            
            // Refresh all data
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error saving routines", e);
        }
    }
    
    @Override
    public void addTask(Routine routine, Task task) {
        try {
            // Get the next order index
            Integer maxOrderIndex = db.taskDao().getMaxOrderIndex(routine.id());
            int orderIndex = (maxOrderIndex == null) ? 0 : maxOrderIndex + 1;
            
            // Get the highest task ID to ensure uniqueness
            Integer maxId = db.taskDao().getMaxTaskId();
            int taskId = (maxId == null) ? 0 : maxId + 1;
            
            // Create a new task with the unique ID
            Task taskToSave = new Task(taskId, task.getName());
            if (task.getIsCheckedOff()) {
                taskToSave.checkOff();
            }
            
            // Save task to database
            db.taskDao().insert(TaskEntity.fromDomain(taskToSave, routine.id(), orderIndex));
            
            // Update in-memory collections without full refresh
            // Get current tasks for this routine
            List<Task> currentTasks = new ArrayList<>();
            if (routineTaskSubjects.containsKey(routine)) {
                List<Task> existingTasks = routineTaskSubjects.get(routine).getValue();
                if (existingTasks != null) {
                    currentTasks.addAll(existingTasks);
                }
            }
            
            // Add new task to the list
            currentTasks.add(taskToSave);
            
            // Update the subject
            if (routineTaskSubjects.containsKey(routine)) {
                routineTaskSubjects.get(routine).setValue(currentTasks);
            } else {
                routineTaskSubjects.put(routine, new PlainMutableSubject<>(currentTasks));
            }
            
            // Update the map subject
            Map<Routine, List<Task>> currentMap = allRoutineTasksSubject.getValue();
            if (currentMap != null) {
                currentMap.put(routine, currentTasks);
                allRoutineTasksSubject.setValue(currentMap);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding task: " + task.getName() + " to routine: " + routine.getName(), e);
        }
    }
} 