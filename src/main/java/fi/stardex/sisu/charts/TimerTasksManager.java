package fi.stardex.sisu.charts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Timer;

public class TimerTasksManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private ChartTask chartTask;

    private static final int DELAY = 0;
    private static final int PERIOD = 100;

    private volatile boolean running;

    private Timer timer1;

    private ApplicationContext applicationContext;

    public TimerTasksManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private ChartTask chartTaskOne() {
        return applicationContext.getBean("chartTaskOne", ChartTask.class);
    }

    public void start(ChartTasks chartTasks) {
        if (running) {
            return;
        }

        if(chartTasks == ChartTasks.CHART_TASK_ONE) {
            chartTask = chartTaskOne();
            chartTask.setUpdateOSC(true);
            timer1 = new Timer();
            timer1.schedule(chartTask, DELAY, PERIOD);
            running = true;
            logger.debug("START timers");
        }
    }

    public void stop() {
        if (running) {
            timer1.cancel();
            timer1.purge();
            chartTask.setUpdateOSC(true);
            running = false;
            logger.debug("STOP timers");
        }
    }
}
