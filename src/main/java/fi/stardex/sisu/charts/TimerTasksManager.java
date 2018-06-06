package fi.stardex.sisu.charts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class TimerTasksManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private List<ChartTask> listOfCharts;

    private static final int DELAY = 0;
    private static final int PERIOD = 100;

    private volatile boolean running;

    private Timer timer;

    private ApplicationContext applicationContext;

    public TimerTasksManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private ChartTask chartTaskOne() {
        return applicationContext.getBean("chartTaskOne", ChartTask.class);
    }

    private ChartTask chartTaskTwo() {
        return applicationContext.getBean("chartTaskTwo", ChartTask.class);
    }

    private ChartTask chartTaskThree() {
        return applicationContext.getBean("chartTaskThree", ChartTask.class);
    }

    private ChartTask chartTaskFour() {
        return applicationContext.getBean("chartTaskFour", ChartTask.class);
    }

    public void start() {
        if (running)
            return;

        listOfCharts = Arrays.asList(chartTaskOne(), chartTaskTwo(), chartTaskThree(), chartTaskFour());
        listOfCharts.forEach(e -> {
            e.setUpdateOSC(true);
            timer = new Timer();
            timer.schedule(e, DELAY, PERIOD);
        });

        running = true;
        logger.debug("START timers");
    }

    public void stop() {
        if (running) {
            timer.cancel();
            timer.purge();
            listOfCharts.forEach(e -> e.setUpdateOSC(false));
            running = false;
            logger.debug("STOP timers");
        }
    }
}
