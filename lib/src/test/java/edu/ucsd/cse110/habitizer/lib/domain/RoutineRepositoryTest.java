package edu.ucsd.cse110.habitizer.lib.domain;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.Subject;

public class RoutineRepositoryTest extends TestCase {

    public void testFind() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<Routine> routineSubject = repository.find(0);

        assertNotNull(routineSubject.getValue());
        assertEquals(routine, routineSubject.getValue());

    }

    public void testFindAll() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine morning = new Routine(0, "Morning Routine");
        Routine evening = new Routine(1, "Evening Routine");
        dataSource.putRoutine(morning);
        dataSource.putRoutine(evening);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<List<Routine>> allRoutinesSubject = repository.findAll();
        List<Routine> currentRoutines = allRoutinesSubject.getValue();

        assertNotNull(currentRoutines);
        assertEquals(2, currentRoutines.size());
        assertTrue(currentRoutines.contains(morning));
        assertTrue(currentRoutines.contains(evening));
    }

    public void testFindAllMappings() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);

        Task task = new Task();
        dataSource.putTask(routine, task);
        RoutineRepository repository = new RoutineRepository(dataSource);

        Subject<Map<Routine, List<Task>>> mapSubject = repository.findAllMappings();
        Map<Routine, List<Task>> routineTaskMap = mapSubject.getValue();

        assertNotNull(routineTaskMap);
        assertTrue(routineTaskMap.containsKey(routine));
        assertTrue(routineTaskMap.get(routine).contains(task));
    }

    public void testSaveRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        Routine routine = new Routine(0, "Morning Routine");
        repository.save(routine);

        Routine getRoutineResult = dataSource.getRoutine(0);
        assertNotNull(getRoutineResult);
        assertEquals(routine, getRoutineResult);
    }

    public void testTestSaveRoutineList() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        RoutineRepository repository = new RoutineRepository(dataSource);
        Routine morning = new Routine(0, "Morning Routine");
        Routine evening = new Routine(1, "Evening Routine");
        repository.save(List.of(morning, evening));

        assertEquals(morning, dataSource.getRoutine(0));
        assertEquals(evening, dataSource.getRoutine(1));
    }
}