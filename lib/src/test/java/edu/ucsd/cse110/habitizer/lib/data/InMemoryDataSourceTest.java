package edu.ucsd.cse110.habitizer.lib.data;

import junit.framework.TestCase;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class InMemoryDataSourceTest extends TestCase {

    public void testFromDefault() {
        InMemoryDataSource dataSource = InMemoryDataSource.fromDefault();
        assertNotNull(dataSource);
        for (Routine defaultRoutine : InMemoryDataSource.DEFAULT_ROUTINES) {
            assertTrue(dataSource.getRoutines().contains(defaultRoutine));
        }
    }

    public void testPutTask() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);

        Task task = new Task();
        dataSource.putTask(routine, task);

        List<Task> tasksForRoutine = dataSource.getTasks(routine);
        assertThat(tasksForRoutine, hasItem(task));
    }

    public void testPutTasks() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);

        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task();
        dataSource.putTasks(routine, List.of(task1, task2, task3));

        List<Task> tasksForRoutine = dataSource.getTasks(routine);
        assertThat(tasksForRoutine, contains(task1, task2, task3));
    }

    public void testPutRoutine() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Routine routine = new Routine(0, "Morning Routine");
        dataSource.putRoutine(routine);

        assertThat(
                dataSource.getAllRoutinesSubject().getValue(),
                hasItem(routine)
        );
    }

    public void testPutRoutines() {
        InMemoryDataSource dataSource = new InMemoryDataSource();

        Routine routine1 = new Routine(0, "Morning Routine");
        Routine routine2 = new Routine(1, "Evening Routine");
        List<Routine> routinesToAdd = List.of(routine1, routine2);

        dataSource.putRoutines(routinesToAdd);

        List<Routine> currentRoutines = dataSource.getAllRoutinesSubject().getValue();
        assertThat(currentRoutines, hasItems(routine1, routine2));
    }
}