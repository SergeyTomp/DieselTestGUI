package fi.stardex.sisu.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(LogbackUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        logger.error("Uncaught exception in thread: " + t.getName(), ex);
    }

}
