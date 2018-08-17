package fi.stardex.sisu.connect;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SchedulerNotifier {

    private final Lock lock;

    private final Condition condition;

    public SchedulerNotifier(Lock lock, Condition condition) {

        this.lock = lock;
        this.condition = condition;

    }

    public void signal() {

        lock.lock();

        try {
            condition.signal();
        } finally {
            lock.unlock();
        }

    }

}
