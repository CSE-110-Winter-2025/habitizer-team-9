package edu.ucsd.cse110.habitizer.app.data;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

public class RoomRoutineRepository implements RoutineRepository {
    private static final String TAG = "RoomRoutineRepository";
    private final AppDatabase db;
    private final PlainMutableSubject<List<Routine>> allRoutinesSubject;
    private final Map<Integer, PlainMutableSubject<Routine>> routineSubjects;
    private final PlainMutableSubject<Map<Routine, List<Task>>> allRoutineTasksSubject;
    private final Map<Routine, PlainMutableSubject<List<Task>>> routineTaskSubjects;
    
    public RoomRoutineRepository(AppDatabase db) {
        this.db = db;
        this.allRoutinesSubject = new PlainMutableSubject<>(new ArrayList<>());
        this.routineSubjects = new HashMap<>();
        this.allRoutineTasksSubject = new PlainMutableSubject<>(new HashMap<>());
        this.routineTaskSubjects = new HashMap<>();
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
            
            // Create a subject for this routine if it doesn't exist
            if (!routineTaskSubjects.containsKey(routine)) {
                routineTaskSubjects.put(routine, new PlainMutableSubject<>(new ArrayList<>()));
            }
            
            // Update the subject
            routineTaskSubjects.get(routine).setValue(currentTasks);
            
            // Update the map subject
            Map<Routine, List<Task>> currentMap = allRoutineTasksSubject.getValue();
            if (currentMap == null) {
                currentMap = new HashMap<>();
            }
            currentMap.put(routine, currentTasks);
            allRoutineTasksSubject.setValue(currentMap);
            
            // Do a full refresh to ensure consistency
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error adding task: " + task.getName() + " to routine: " + routine.getName(), e);
        }
    }

    @Override
    public void updateTaskName(int taskId, String newName) {
        try {
            db.taskDao().updateTaskName(taskId, newName);
            for (Map.Entry<Routine, PlainMutableSubject<List<Task>>> entry : routineTaskSubjects.entrySet()) {
                List<Task> tasks = entry.getValue().getValue();
                if (tasks != null) {
                    for (Task task : tasks) {
                        if (task.getId() != null && task.getId() == taskId) {
                            task.rename(newName);
                        }
                    }
                    entry.getValue().setValue(new ArrayList<>(tasks));
                }
            }

            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error updating task name: " + taskId, e);
        }
    }

    @Override
    public void addRoutine(Routine routine) {
        try {
            // Insert into the database and get the generated ID
            long routineId = db.routineDao().insert(RoutineEntity.fromDomain(routine));

            // Create a new Routine object with the assigned ID
            Routine newRoutine = new Routine((int) routineId, routine.getName());

            // Update in-memory subjects
            List<Routine> updatedRoutines = new ArrayList<>(allRoutinesSubject.getValue());
            updatedRoutines.add(newRoutine);
            allRoutinesSubject.setValue(updatedRoutines);

            routineSubjects.put(newRoutine.id(), new PlainMutableSubject<>(newRoutine));

            // Update the routine-task mapping
            Map<Routine, List<Task>> currentMap = allRoutineTasksSubject.getValue();
            currentMap.put(newRoutine, new ArrayList<>());
            allRoutineTasksSubject.setValue(currentMap);

        } catch (Exception e) {
            Log.e(TAG, "Error adding routine: " + routine.getName(), e);
        }
    }

    @Override
    public void swapTaskOrder(Routine routine, int fromPosition, int toPosition) {
        try {
            List<TaskEntity> taskEntities = db.taskDao().getTasksByRoutineId(routine.id());
            
            if (taskEntities == null || taskEntities.size() <= 1) {
                return;
            }
            
            if (fromPosition < 0 || fromPosition >= taskEntities.size() || 
                toPosition < 0 || toPosition >= taskEntities.size()) {
                return;
            }
            
            TaskEntity fromTask = taskEntities.get(fromPosition);
            TaskEntity toTask = taskEntities.get(toPosition);
            
            int tempOrderIndex = fromTask.orderIndex;
            fromTask.orderIndex = toTask.orderIndex;
            toTask.orderIndex = tempOrderIndex;
            
            db.taskDao().updateTaskOrderIndex(fromTask.id, fromTask.orderIndex);
            db.taskDao().updateTaskOrderIndex(toTask.id, toTask.orderIndex);
            
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error swapping task order", e);
        }
    }

    @Override
    public void moveTaskUp(Routine routine, Task task){
        try {
            List<TaskEntity> taskEntities = db.taskDao().getTasksByRoutineId(routine.id());
            
            if (taskEntities == null || taskEntities.size() <= 1) {
                return;
            }
            
            List<TaskEntity> orderedTasks = new ArrayList<>(taskEntities);
            
            orderedTasks.sort((t1, t2) -> Integer.compare(t1.orderIndex, t2.orderIndex));
            
            int taskIndex = -1;
            for (int i = 0; i < orderedTasks.size(); i++) {
                if (orderedTasks.get(i).id == task.getId()) {
                    taskIndex = i;
                    break;
                }
            }
            
            if (taskIndex < 0) {
                return;
            }
            
            TaskEntity taskToMove = orderedTasks.remove(taskIndex);
            
            int newIndex = (taskIndex == 0) ? orderedTasks.size() : taskIndex - 1;
            orderedTasks.add(newIndex, taskToMove);
            
            for (int i = 0; i < orderedTasks.size(); i++) {
                TaskEntity t = orderedTasks.get(i);
                t.orderIndex = i;
                db.taskDao().updateTaskOrderIndex(t.id, i);
            }
            
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error moving task up: " + task.getName() + " in routine: " + routine.getName(), e);
        }
    }

    @Override
    public void moveTaskDown(Routine routine, Task task){
        try {
            List<TaskEntity> taskEntities = db.taskDao().getTasksByRoutineId(routine.id());
            
            if (taskEntities == null || taskEntities.size() <= 1) {
                return;
            }
            
            List<TaskEntity> orderedTasks = new ArrayList<>(taskEntities);
            
            orderedTasks.sort((t1, t2) -> Integer.compare(t1.orderIndex, t2.orderIndex));
            
            int taskIndex = -1;
            for (int i = 0; i < orderedTasks.size(); i++) {
                if (orderedTasks.get(i).id == task.getId()) {
                    taskIndex = i;
                    break;
                }
            }
            
            if (taskIndex < 0) {
                return;
            }
            
            TaskEntity taskToMove = orderedTasks.remove(taskIndex);
            
            int newIndex = (taskIndex == orderedTasks.size()) ? 0 : taskIndex + 1;
            orderedTasks.add(newIndex, taskToMove);
            
            for (int i = 0; i < orderedTasks.size(); i++) {
                TaskEntity t = orderedTasks.get(i);
                t.orderIndex = i;
                db.taskDao().updateTaskOrderIndex(t.id, i);
            }
            
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error moving task down: " + task.getName() + " in routine: " + routine.getName(), e);
        }
    }

    @Override
    public void renameRoutine(Routine routine, String newName) {
        try {
            db.routineDao().updateRoutineName(routine.id(), newName);
            routine.rename(newName);

            if (routineSubjects.containsKey(routine.id())) {
                routineSubjects.get(routine.id()).setValue(routine);
            }
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error renaming routine: " + routine.getName(), e);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        try {
            db.taskDao().deleteById(taskId);
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting task with ID: " + taskId, e);
        }
    }

    @Override
    public void deleteRoutine(int routineId) {
        try {
            db.routineDao().delete(routineId);
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting routine with ID: " + routineId, e);
        }
    }

    @Override
    public void updateGoalTime(int routineId, long newGoalTime){
        try {
            db.routineDao().updateGoalTime(routineId, newGoalTime);
            refreshData();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting routine with ID: " + routineId, e);
        }
    }


} 