package fi.stardex.sisu.charts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

@Service
public class TimerTasksManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private List<ChartTask> listOfCharts;

    private static final int DELAY = 0;
    private static final int PERIOD = 100;

    private volatile boolean running;

    private Timer timer;

    private List<Timer> timersList = new ArrayList<>();

    @Lookup
    @Qualifier("chartTaskOne")
    public ChartTaskOne getChartTaskOne() {
        return null;
    }

    @Lookup
    @Qualifier("chartTaskTwo")
    public ChartTaskTwo getChartTaskTwo() {
        return null;
    }

    @Lookup
    @Qualifier("chartTaskThree")
    public ChartTaskThree getChartTaskThree() {
        return null;
    }

    @Lookup
    @Qualifier("chartTaskFour")
    public ChartTaskFour getChartTaskFour() {
        return null;
    }

    public void start() {
        if (running)
            return;

        listOfCharts = new ArrayList<>(Arrays.asList(getChartTaskOne(), getChartTaskTwo(), getChartTaskThree(), getChartTaskFour()));

        listOfCharts.forEach(e -> {
            e.setUpdateOSC(true);
            timer = new Timer();
            timer.schedule(e, DELAY, PERIOD);
            timersList.add(timer);
        });

        running = true;
        logger.debug("START timers");
    }

    public void stop() {
        if (running) {
            timersList.forEach(e -> {
                e.cancel();
                e.purge();
            });
            timersList.clear();
            listOfCharts.forEach(e -> {
                e.setUpdateOSC(false);
                e.getData().clear();
            });
            running = false;
            logger.debug("STOP timers");
        }
    }
}
