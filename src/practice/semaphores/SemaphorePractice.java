package practice.semaphores;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Demonstrates how Semaphores restrict concurrent access to resources. A very
 * useful way to control thread work.
 *
 * @author Owen McMonagle.
 * @version 0.1
 * @since 25/11/2019
 */
public class SemaphorePractice
{
    // Id used to distinguish between worker threads.
    private static int count = 0;

    // Function used to simulate a thread working...
    private static final Function<Semaphore, Void> asynchronousTask = lock ->
    {
        // Increment count and assign it to id.
        int id = count++;
        try
        {
            // Aquire a permit from the Semaphore.
            lock.acquire();

            // Do some work, each iteration waiting for a second...
            for(int i = 0; i < 4; i ++)
            {
                System.out.println("Worker[" + id + "] is working..." );
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait(1000);
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally {
            // Release the permit back for other workers.
            lock.release();
        }
        return null;
    };



    public static void main(String[] args)
    {
        // Thread pool instance with five threads.
        final ExecutorService thread_pool = Executors.newFixedThreadPool(5);

        // Allows Two threads to work on the resources this Semaphore manages.
        final int work_permits = 2;

        // Semaphore with a max of two permits.
        final Semaphore semaphore_lock = new Semaphore(work_permits);

        // Start 5 threads...
        thread_pool.submit(() -> asynchronousTask.apply(semaphore_lock));
        thread_pool.submit(() -> asynchronousTask.apply(semaphore_lock));
        thread_pool.submit(() -> asynchronousTask.apply(semaphore_lock));
        thread_pool.submit(() -> asynchronousTask.apply(semaphore_lock));
        thread_pool.submit(() -> asynchronousTask.apply(semaphore_lock));


        // Shutdown the thread pool after Fifty seconds.
        // Which will end the program.
        try {
            thread_pool.awaitTermination(50, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
