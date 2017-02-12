package nachos.threads;

import nachos.machine.*;

/**
 * A Tester for the Alarm class.  This test starts 10 threads that all wait on
 * the same Alarm object.  Each thread is scheduled to run for a certain number
 * of ticks (10000, 9000, ..., 1000).  After waiting for that number of ticks,
 * the thread prints out the actual number of ticks it has waited.  The actual
 * time should be slightly longer than the scheduled time.  The threads complete
 * in the reverse order of when they were started.
 */
public class AlarmTest {
    /**
     * An inner class that implements the main execution point of each thread.
     */
    private static final class MyRunnable implements Runnable {
        /** The number of ticks this thread should sleep before finishing. */
        private final long duration;

        /**
         * The constructor initializes the class data.
         *
         * @param duration The number of ticks this thread should sleep before
         *                 finishing.
         */
        public MyRunnable(final long duration) {
            this.duration = duration;
        }

        /**
         * The main execution point of each thread.  This method prints a
         * message indicating the thread has started, sleeps some number of
         * ticks, and then prints a message before finishing.
         */
        public void run() {
            System.out.println("Thread waiting for " + this.duration + "-ticks alarm...");

            // Sleep the appropriate number of ticks and record the actual
            // number of ticks the thread was asleep.
            final long anchor = Machine.timer().getTime();
            ThreadedKernel.alarm.waitUntil(this.duration);
            final long actual = Machine.timer().getTime() - anchor;

            System.out.println("Thread waited " + actual + " ticks (>= " + this.duration + ").");
        }
    }
    
    /**
     * The main execution point for the Alarm test.
     */
    public static void runTest() {
        System.out.println("**** Alarm testing begins ****");

        // Declare 10 threads.
        final KThread[] testers = new KThread[10];

        // Loop over and start each thread, starting with the ones that sleep
        // the longest number of ticks.
        for (int i = 0; i < testers.length; i++) {
            final MyRunnable runner = new MyRunnable(1000 * (10 - i));
            testers[i] = new KThread(runner);
            testers[i].fork();
        }

        // Wait for each thread to finish.
        for (int i = 0; i < testers.length; i++) 
            testers[i].join();

        KThread.yield();
        System.out.println("**** Alarm testing end ****");
    }
}
