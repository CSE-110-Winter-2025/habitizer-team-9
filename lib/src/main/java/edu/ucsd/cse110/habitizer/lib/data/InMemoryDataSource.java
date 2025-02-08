package edu.ucsd.cse110.habitizer.lib.data;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.observables.PlainMutableSubject;
import edu.ucsd.cse110.observables.Subject;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class InMemoryDataSource {
    private final Map<Integer, Routine> routines = new HashMap<>();
    private final Map<Integer, PlainMutableSubject<Routine>> routineSubjects = new HashMap<>();
    private final PlainMutableSubject<List<Routine>> allRoutinesSubject = new PlainMutableSubject<>();

    public InMemoryDataSource() {

    }

    public final static List<Routine> DEFAULT_ROUTINES = List.of(
            new Routine(0,"Morning Routine"),
            new Routine(1, "Evening Routine"));

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putRoutines(DEFAULT_ROUTINES);
        return data;
    }
    public List<Routine> getRoutines() {
        return List.copyOf(routines.values());
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

    public void putRoutine(Routine routine){

        routines.put(routine.id(), routine);

        if (routineSubjects.containsKey(routine.id())) {
            routineSubjects.get(routine.id()).setValue(routine);
        }

        allRoutinesSubject.setValue(getRoutines());

    }

    public void putRoutines(List<Routine> routines){

        routines.forEach(routine -> this.routines.put(routine.id(), routine));

        routines.forEach(routine -> {
            if (routineSubjects.containsKey(routine.id())) {
                routineSubjects.get(routine.id()).setValue(routine);
            }
        });
        allRoutinesSubject.setValue(getRoutines());
    }

    public void removeRoutine(int id){}


}
