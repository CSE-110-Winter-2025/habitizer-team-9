package edu.ucsd.cse110.habitizer.lib.domain;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

import java.awt.Dialog;

import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.observables.Subject;

public interface RoutineRepository {
    Subject<Routine> find(int id);

    Subject<List<Routine>> findAll();

    Subject<Map<Routine, List<Task>>> findAllMappings();

    void save(Routine routine);

    void save(List<Routine> routines);

    void addTask(Routine routine, Task task);

    void updateTaskName(int taskId, String newName);

    void addRoutine(Routine routine);

    /**
     * Swaps the order of two tasks in a routine
     * @param routine The routine containing the tasks
     * @param fromPosition The original position of the task
     * @param toPosition The new position for the task
     * @throws IllegalArgumentException if positions are invalid or routine doesn't exist
     * @throws IllegalStateException if a task has a null ID
     */
    void swapTaskOrder(Routine routine, int fromPosition, int toPosition);

    void moveTaskUp(Routine routine, Task task);

    void moveTaskDown(Routine routine, Task task);

    void renameRoutine(Routine routine, String newName);

    void deleteTask(int taskId);

    void deleteRoutine(int routineId);

    void updateGoalTime(int routineId, long newGoalTime);
}