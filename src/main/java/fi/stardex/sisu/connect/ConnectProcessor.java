package fi.stardex.sisu.connect;

import javax.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ConnectProcessor {

    private static final int THREAD_POOL_COUNT = 3;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_COUNT);

    public Future<Boolean> submit(Callable<Boolean> callable) {
        return executorService.submit(callable);
    }

    @PreDestroy
    public void shutDown() {
        executorService.shutdown();
    }

}
