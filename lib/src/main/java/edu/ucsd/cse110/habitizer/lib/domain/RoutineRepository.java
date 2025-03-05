package edu.ucsd.cse110.habitizer.lib.domain;

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

    void renameRoutine(Routine routine, String newName);
}
