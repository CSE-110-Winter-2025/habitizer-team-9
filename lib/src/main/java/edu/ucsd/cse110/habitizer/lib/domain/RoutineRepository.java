package edu.ucsd.cse110.habitizer.lib.domain;


import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

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
    
    /**
     * Swaps the order of two tasks in a routine
     * @param routine The routine containing the tasks
     * @param fromPosition The original position of the task
     * @param toPosition The new position for the task
     * @throws IllegalArgumentException if positions are invalid or routine doesn't exist
     * @throws IllegalStateException if a task has a null ID
     */
    public void swapTaskOrder(Routine routine, int fromPosition, int toPosition) {
        if (routine == null) {
            throw new IllegalArgumentException("Routine cannot be null");
        }
        
        try {
            dataSource.swapTaskOrder(routine, fromPosition, toPosition);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to swap task order: " + e.getMessage(), e);
        }
    }

}
