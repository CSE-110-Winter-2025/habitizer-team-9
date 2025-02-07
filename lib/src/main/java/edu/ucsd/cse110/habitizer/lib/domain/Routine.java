package edu.ucsd.cse110.habitizer.lib.domain;


import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

import java.time.Instant;
import java.time.Duration;
import java.util.List;

public class Routine {
    private final InMemoryDataSource dataSource;
    public Routine(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Subject<Task> find(int id){
        return dataSource.getTaskSubject(id);
    }

    public Subject<List<Task>> findAll(){
        return dataSource.getAllTasksSubject();
    }

    public void save(Task task){
        dataSource.putTask(task);
    }

    public void save(List<Task> tasks){
        dataSource.putTasks(tasks);
    }

    public void checkOff(Task task){
        // update task Instant
        dataSource.setPreviousTimeStamp(Instant.now());
    }


}
