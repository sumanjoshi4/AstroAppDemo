package com.app.astro.astroassignment.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vijay on 8/27/14.
 */
public class ThreadUtils {
    private static int             NUMBER_OF_CORES        = Runtime.getRuntime().availableProcessors();
    private static final String LOG_TAG                = "THREAD_SERVICE";
    private static ExecutorService defaultExecutorService = new ThreadPoolExecutor(
                                                                  NUMBER_OF_CORES, // Initial
                                                                                   // pool
                                                                                   // size
                                                                  NUMBER_OF_CORES, // Max
                                                                                   // pool
                                                                                   // size
                                                                  1, TimeUnit.SECONDS,
                                                                  new LinkedBlockingQueue<Runnable>());
    private static ExecutorService singleThreadedExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS,
                                                                  new LinkedBlockingQueue<Runnable>());
    private static ExecutorService lowExecutorService     = new ThreadPoolExecutor(
                                                                  NUMBER_OF_CORES, // Initial
                                                                                   // pool
                                                                                   // size
                                                                  NUMBER_OF_CORES, // Max
                                                                                   // pool
                                                                                   // size
                                                                  1, TimeUnit.SECONDS,
                                                                  new LinkedBlockingQueue<Runnable>(),
                                                                  new LowPriorityThreadFactory());

    private ThreadUtils() {
    }

    public static ExecutorService getDefaultExecutorService() {
        return defaultExecutorService;
    }

    public static ExecutorService getSingleThreadedExecutor() {
        return singleThreadedExecutor;
    }

    public static ExecutorService getLowExecutorService() {
        return lowExecutorService;
    }

    static class LowPriorityThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber   = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        LowPriorityThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.MIN_PRIORITY)
                t.setPriority(Thread.MIN_PRIORITY);
            return t;
        }
    }
}
