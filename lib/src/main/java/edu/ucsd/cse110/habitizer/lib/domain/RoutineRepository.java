package edu.ucsd.cse110.habitizer.lib.domain;


import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

import java.awt.Dialog;
import java.util.List;
import java.util.Map;

public class RoutineRepository {
    private final InMemoryDataSource dataSource;

    public RoutineRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Subject<Routine> find(int id){
        return dataSource.getRoutineSubject(id);
    }

    public Subject<List<Routine>> findAll(){
        return dataSource.getAllRoutinesSubject();
    }

    public Subject<Map<Routine, List<Task>>> findAllMappings(){
        return dataSource.getMapSubject();
    }

    public void save(Routine routine){
        dataSource.putRoutine(routine);
    }

    public void save(List<Routine> routines){
        dataSource.putRoutines(routines);
    }

    public void addTask(Routine routine, Task task)
    {
        dataSource.putTask(routine, task);
    }

    public void moveTaskUp(Routine routine, Task task) {dataSource.moveTaskUp(routine, task);}

    public void moveTaskDown(Routine routine, Task task) {
        dataSource.moveTaskDown(routine, task);
    }

    public void addRoutine(Routine routine)
    {
        dataSource.putRoutine(routine);
    }
}
