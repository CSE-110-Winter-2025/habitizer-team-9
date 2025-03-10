package edu.ucsd.cse110.habitizer.lib.domain;

import junit.framework.TestCase;

public class RoutineTimerTest extends TestCase {

    private RoutineTimer timer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        timer = new RoutineTimer(secondsElapsed -> {});
    }

    public void testEnableMockMode() {
        timer.enableMockMode();
        timer.advanceMockTime(60); // Advance 1 minute
        assertEquals(1, timer.getElapsedMinutes()); // Verify time updated correctly in mock mode
    }

    public void testDisableMockModeSynchronizesElapsedTime() {
        timer.enableMockMode();
        timer.advanceMockTime(120); // Advance 2 minutes in mock mode
        timer.disableMockMode(); // Exit mock mode
        System.out.println("Elapsed minutes after disabling mock mode: " + timer.getElapsedMinutes());
        assertEquals(2, timer.getElapsedMinutes()); // Mock time should be retained after switching to real time
    }



    public void testAdvanceMockTimeWithoutEnablingThrowsException() {
        try {
            timer.advanceMockTime(60); // Attempt to advance time without enabling mock mode
            fail("Expected IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertEquals("Cannot advance time in real mode. Enable mock mode first.", e.getMessage());
        }
    }

    public void testReset() {
        timer.enableMockMode();
        timer.advanceMockTime(300); // Mock 5 minutes
        timer.reset(); // Reset timer
        assertEquals(0, timer.getElapsedMinutes()); // Ensure reset works
    }

    public void testSwitchBetweenMockAndRealTime() throws InterruptedException {
        System.out.println("Starting mock mode...");
        timer.enableMockMode();
        timer.advanceMockTime(120); // Advance 2 minutes in mock mode
        assertEquals(2, timer.getElapsedMinutes()); // Verify mock mode works

        System.out.println("Switching to real time...");
        timer.disableMockMode(); // Switch back to real time
         // Start real-time tracking
        Thread.sleep(5000); // Wait for 5 seconds instead of 2 seconds
        timer.stop();

        int elapsedMinutes = timer.getElapsedMinutes();
        System.out.println("Elapsed minutes after switching to real time: " + elapsedMinutes);
        assertTrue(elapsedMinutes >= 2); // Ensure mock time is retained
    }

    public void testPauseMock() {

        timer.enableMockMode();
        timer.pauseRoutine();
        timer.advanceMockTime(120); // Advance 2 minutes in mock mode
        assertEquals(0, timer.getElapsedMinutes());

        timer.resumeRoutine();
        timer.advanceMockTime(120); // Advance 2 minutes in mock mode
        assertEquals(2, timer.getElapsedMinutes()); // Verify mock mode works
    }

    public void testPauseRealTime() throws InterruptedException {
        timer.start(); // Start real-time tracking

        Thread.sleep(5000); // Wait for 5 seconds instead of 2 seconds
        assertTrue(timer.getElapsedTimeInSeconds() <= 6);

        timer.pauseRoutine();

        Thread.sleep(5000);

        assertTrue(timer.getElapsedTimeInSeconds() <= 6);

        timer.resumeRoutine();
        Thread.sleep(5000);
        assertTrue(timer.getElapsedTimeInSeconds() > 6);
        timer.stop();
    }




}
