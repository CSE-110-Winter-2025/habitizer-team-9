package edu.ucsd.cse110.habitizer.lib.data;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class InMemoryDataSource {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, PlainMutableSubject<Task>> taskSubjects = new HashMap<>();

    private final Subject<List<Task>> allTasksSubject = new PlainMutableSubject<>();
    private Instant startRoutineTimeStamp;
    private Instant previousTimeStamp;

    private String name;
    private Duration duration = Duration.ofMinutes(60);


    public List<Task> getTasks() {
        return List.copyOf(tasks.values());
    }

    public Task getTask(int id){
        return tasks.get(id);
    }

    public Instant getStartRoutineTimeStamp(){
        return startRoutineTimeStamp;
    }

    public Instant getPreviousTimeStamp(){
        return previousTimeStamp;
    }

    public void setPreviousTimeStamp(Instant now){
        previousTimeStamp = now;
    }

    public Duration getDuration(){
        return duration;
    }

    public void setDuration(long minutes){
        duration = Duration.ofMinutes(minutes);
    }

    public Subject<Task> getTaskSubject(int id){
        if(!taskSubjects.containsKey(id)){
            var subject = new PlainMutableSubject<Task>();
            subject.setValue(getTask(id));
            taskSubjects.put(id, subject);
        }
        return taskSubjects.get(id);
    }

    public Subject<List<Task>> getAllTasksSubject() { return allTasksSubject;}

    public void putTask(Task task){

    }

    public void putTasks(List<Task> tasks){

    }

    public void removeTask(int id){}


}
