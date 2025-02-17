package edu.ucsd.cse110.habitizer.lib.domain;

import junit.framework.TestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TaskTest extends TestCase {

    public void testCheckOff() {
        int id = 0;
        String name = "Shower";
        Task task = new Task(id, name);
        task.checkOff();

        assertThat(task.getId(), is(id));
        assertThat(task.getName(), is(name));
        assertThat(task.getIsCheckedOff(), is(true));
    }

    public void testRenameTask() {
        int id = 0;
        String oldName = "Shower";
        String newName = "Wash Face";

        Task task = new Task(id, oldName);
        Task expectedTask = new Task(id, newName);
        task.rename(newName);

        assertEquals(task, expectedTask);
    }
}