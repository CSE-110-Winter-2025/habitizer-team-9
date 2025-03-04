package edu.ucsd.cse110.habitizer.lib.data;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class InMemoryDataSource {
    private final Map<Integer, Routine> routines = new HashMap<>();

    private final Map<Routine, List<Task>> routineTaskMap = new HashMap<>();

    private final Map<Routine, PlainMutableSubject<List<Task>>> routineTaskSubjects = new HashMap<>();
    private final PlainMutableSubject<Map<Routine, List<Task>>> allRoutineTasks = new PlainMutableSubject<>();
    private final Map<Integer, PlainMutableSubject<Routine>> routineSubjects = new HashMap<>();
    private final PlainMutableSubject<List<Routine>> allRoutinesSubject = new PlainMutableSubject<>();

    public InMemoryDataSource() {

    }

    public final static List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(0,"Morning Routine"),
            new Routine(1, "Evening Routine"));

    public final static List<Task> DEFAULT_MORNING_TASKS = List.of(
            new Task(0, "Shower"),
            new Task(1, "Brush teeth"),
            new Task(2, "Dress"),
            new Task(3, "Make coffee"),
            new Task(4, "Make lunch"),
            new Task(5, "Dinner prep"),
            new Task(6, "Pack bag")
    );

    public final static List<Task> DEFAULT_EVENING_TASKS = List.of(
            new Task(0, "Charge devices"),
            new Task(1, "Prepare Dinner"),
            new Task(2, "Eat Dinner"),
            new Task(3, "Wash dishes"),
            new Task(4, "Pack bag"),
            new Task(5, "Do homework")
    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putRoutines(DEFAULT_ROUTINES);
        data.putTasks(DEFAULT_ROUTINES.get(0), DEFAULT_MORNING_TASKS);
        data.putTasks(DEFAULT_ROUTINES.get(1), DEFAULT_EVENING_TASKS);
        return data;
    }
    public List<Routine> getRoutines() {
        return List.copyOf(routines.values());
    }

    public List<Task> getTasks(Routine routine){
        return List.copyOf(routineTaskMap.get(routine));
    }

    public Routine getRoutine(int id){
        return routines.get(id);
    }

    public Subject<Routine> getRoutineSubject(int id){
        if(!routineSubjects.containsKey(id)){
            var subject = new PlainMutableSubject<Routine>();
            subject.setValue(getRoutine(id));
            routineSubjects.put(id, subject);
        }
        return routineSubjects.get(id);
    }

    public Subject<List<Routine>> getAllRoutinesSubject() { return allRoutinesSubject;}

    public Subject<Map<Routine, List<Task>>> getMapSubject(){ return allRoutineTasks;}

    public Subject<List<Task>> getTasksSubject(Routine routine){
        return routineTaskSubjects.get(routine);
    }

    public void putTask(Routine routine, Task task){
        routineTaskMap.get(routine).add(task);

        if (routineTaskMap.containsKey(routine)) {
            routineTaskSubjects.get(routine).setValue(routineTaskMap.get(routine));
        }

        allRoutineTasks.setValue(routineTaskMap);
    }

    public void putTasks(Routine routine, List<Task> tasks){
        tasks.forEach(task -> routineTaskMap.get(routine).add(task));

        tasks.forEach(task -> {
            if (routineTaskMap.containsKey(routine)) {
                routineTaskSubjects.get(routine).setValue(routineTaskMap.get(routine));
            }
        });
        allRoutineTasks.setValue(routineTaskMap);
    }

    public void putRoutine(Routine routine){
        routines.put(routine.id(), routine);

        if (!routineTaskMap.containsKey(routine)) {
            routineTaskMap.put(routine, new ArrayList<>());
            routineTaskSubjects.put(routine, new PlainMutableSubject<>(new ArrayList<>()));
        }

        if (routineSubjects.containsKey(routine.id())) {
            routineSubjects.get(routine.id()).setValue(routine);
        }

        allRoutinesSubject.setValue(getRoutines());

    }

    public void putRoutines(List<Routine> routines){

        routines.forEach(routine -> this.routines.put(routine.id(), routine));

        routines.forEach(routine -> {
            if (!routineTaskMap.containsKey(routine)) {
                routineTaskMap.put(routine, new ArrayList<>());
                routineTaskSubjects.put(routine, new PlainMutableSubject<>(new ArrayList<>()));
            }
            if (routineSubjects.containsKey(routine.id())) {
                routineSubjects.get(routine.id()).setValue(routine);
            }
        });
        allRoutinesSubject.setValue(getRoutines());
    }

    public void moveTaskUp(Routine routine, Task task){
        List<Task> tasks = routineTaskMap.get(routine);

        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i) == task){
                if(i == 0){
                    Task top = tasks.get(0);
                    tasks.set(0, tasks.get(tasks.size() - 1));
                    tasks.set(tasks.size() - 1, top);
                }else{
                    Task top = tasks.get(i);
                    tasks.set(i, tasks.get(i - 1));
                    tasks.set(i - 1, top);
                }
                break;
            }
        }

        routineTaskSubjects.get(routine).setValue(routineTaskMap.get(routine));
        allRoutineTasks.setValue(routineTaskMap);

    }

    public void moveTaskDown(Routine routine, Task task){
        List<Task> tasks = routineTaskMap.get(routine);

        for(int i = 0; i < tasks.size(); i++){
            if(tasks.get(i) == task){
                if(i == tasks.size() - 1){
                    Task bottom = tasks.get(tasks.size()-1);
                    tasks.set(tasks.size() - 1, tasks.get(0));
                    tasks.set(0, bottom);
                }else{
                    Task top = tasks.get(i);
                    tasks.set(i, tasks.get(i + 1));
                    tasks.set(i + 1, top);
                }
                break;
            }
        }

        routineTaskSubjects.get(routine).setValue(routineTaskMap.get(routine));
        allRoutineTasks.setValue(routineTaskMap);
    }

    public void removeRoutine(int id){
        // NOT YET IMPLEMENTED
    }




}
