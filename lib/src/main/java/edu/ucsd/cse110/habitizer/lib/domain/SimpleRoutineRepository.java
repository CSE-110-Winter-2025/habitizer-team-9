package edu.ucsd.cse110.habitizer.lib.domain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

public class SimpleRoutineRepository implements RoutineRepository {
    private final InMemoryDataSource dataSource;

    public SimpleRoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Subject<Routine> find(int id){
        return dataSource.getRoutineSubject(id);
    }

    @Override
    public Subject<List<Routine>> findAll(){
        return dataSource.getAllRoutinesSubject();
    }

    @Override
    public Subject<Map<Routine, List<Task>>> findAllMappings(){
        return dataSource.getMapSubject();
    }

    @Override
    public void save(Routine routine){
        dataSource.putRoutine(routine);
    }

    @Override
    public void save(List<Routine> routines){
        dataSource.putRoutines(routines);
    }

    @Override
    public void addTask(Routine routine, Task task)
    {
        dataSource.putTask(routine, task);
    }

    @Override
    public void updateTaskName(int taskId, String newName) {
        var routineTaskMap = dataSource.getMapSubject().getValue();

        if (routineTaskMap == null) return;
        boolean updated = false;

        for (Map.Entry<Routine, List<Task>> entry : routineTaskMap.entrySet()) {
            List<Task> tasks = entry.getValue();
            for (Task task : tasks) {
                if (task.getId() != null && task.getId() == taskId) {
                    task.rename(newName);
                    updated = true;
                }
            }
            if (updated) {
                dataSource.getTasksSubject(entry.getKey()).setValue(new ArrayList<>(tasks));
            }
        }

        if (updated) {
            dataSource.getMapSubject().setValue(new HashMap<>(routineTaskMap));
        }
    }

    @Override
    public void addRoutine(Routine routine)
    {
        dataSource.putRoutine(routine);
    }

    @Override
    public void swapTaskOrder(Routine routine, int fromPosition, int toPosition) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        
        dataSource.swapTaskOrder(routine, fromPosition, toPosition);
    }

    @Override
    public void renameRoutine(Routine routine, String newName){
        routine.rename(newName);
    }

    @Override
    public void moveTaskUp(Routine routine, Task task) {dataSource.moveTaskUp(routine, task);}

    @Override
    public void moveTaskDown(Routine routine, Task task) {
        dataSource.moveTaskDown(routine, task);
    }

    @Override
    public void deleteTask(int taskId) {
        dataSource.deleteTask(taskId);
    }
    @Override
    public void deleteRoutine(int routineId){
        dataSource.deleteRoutine(routineId);
    }

    @Override
    public void updateGoalTime(int routineId, long newGoalTime){
        dataSource.updateGoalTime(routineId, newGoalTime);
    }

}
