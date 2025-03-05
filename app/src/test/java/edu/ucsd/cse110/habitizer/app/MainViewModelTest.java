package edu.ucsd.cse110.habitizer.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTimer;
import edu.ucsd.cse110.habitizer.lib.domain.SimpleRoutineRepository;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class MainViewModelTest {

    private MainViewModel mainViewModel;
    private RoutineRepository routineRepository;
    private InMemoryDataSource dataSource;

    List<Routine> actualRoutines = List.of(
            new Routine(0, "Morning Routine"),
            new Routine(1, "Evening Routine"));

    List<Task> actualMorningTasks = List.of(
            new Task(0, "Shower"),
            new Task(1, "Brush teeth"),
            new Task(2, "Dress"),
            new Task(3, "Make coffee"),
            new Task(4, "Make lunch"),
            new Task(5, "Dinner prep"),
            new Task(6, "Pack bag")
    );

    List<Task> actualEveningTasks = List.of(
            new Task(0, "Charge devices"),
            new Task(1, "Prepare Dinner"),
            new Task(2, "Eat Dinner"),
            new Task(3, "Wash dishes"),
            new Task(4, "Pack bag"),
            new Task(5, "Do homework")
    );

    @Before
    public void initialize() {
        dataSource = InMemoryDataSource.fromDefault();
        routineRepository = new SimpleRoutineRepository(dataSource);
        mainViewModel = new MainViewModel(routineRepository);
    }

    @Test
    public void testInitialize() {
        assertNotNull(mainViewModel.getRoutines().getValue());
        assertNotNull(mainViewModel.getMap().getValue());
    }

    @Test
    public void testGetMap() {
        Map<Routine, List<Task>> actualMap = new HashMap<>();
        actualMap.put(actualRoutines.get(0), actualMorningTasks);
        actualMap.put(actualRoutines.get(1), actualEveningTasks);

        Map<Routine, List<Task>> testMap = mainViewModel.getMap().getValue();

        assertEquals(actualMap, testMap);
    }

    @Test
    public void testGetRoutines() {
        List<Routine> testRoutines = mainViewModel.getRoutines().getValue();

        assertEquals(actualRoutines, testRoutines);
    }

    @Test
    public void testGetListRoutines() {
        List<Routine> testListRoutines = mainViewModel.getListRoutines();

        assertEquals(actualRoutines, testListRoutines);
    }

    @Test
    public void testAddTask() {
        Routine newRoutine = new Routine(2, "Weekend Routine");
        Task newTask = new Task(0, "Go to gym");

        dataSource.putRoutine(newRoutine);
        mainViewModel.addTask(newRoutine, newTask);

        List<Task> routineTasks = dataSource.getTasks(newRoutine);
        assertTrue(routineTasks.contains(newTask));
    }

    @Test
    public void testEndRoutine() {
        RoutineTimer routineTimer = new RoutineTimer(secondsElapsed -> {
        });
        routineTimer.setIsMocking(true);
        mainViewModel.setRoutineTimer(routineTimer);
        mainViewModel.endRoutine();
        assertFalse(routineTimer.getRunning());
    }

    @Test
    public void testToggleMockMode() {
        RoutineTimer routineTimer = new RoutineTimer(secondsElapsed -> {});
        routineTimer.setIsMocking(true);
        mainViewModel.setRoutineTimer(routineTimer);
        mainViewModel.toggleMockMode(true);
        assertTrue(routineTimer.getIsMocking());

        routineTimer.setIsMocking(true);
        mainViewModel.setRoutineTimer(routineTimer);
        mainViewModel.toggleMockMode(false);
        assertFalse(routineTimer.getIsMocking());
    }

    @Test
    public void testAdvanceMockTime() {
        RoutineTimer routineTimer = new RoutineTimer(secondsElapsed -> {});
        routineTimer.setIsMocking(true);
        routineTimer.setElapsedTime(60);
        mainViewModel.setRoutineTimer(routineTimer);
        mainViewModel.advanceMockTime();
        assertEquals(routineTimer.getElapsedTime(), 90);

        routineTimer.setIsMocking(false);
        routineTimer.setElapsedTime(60);
        mainViewModel.setRoutineTimer(routineTimer);
        mainViewModel.advanceMockTime();
        assertEquals(routineTimer.getElapsedTime(), 60);
    }
}