package fi.stardex.sisu.charts;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTasksManager {

    private static final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private ChartTask chartTask;

    private static final int DELAY = 0;
    private static final int PERIOD = 100;

    private boolean updateOSC;

    private volatile boolean running;

    private Timer timer1;

    public TimerTasksManager(ChartTask chartTask) {
        this.chartTask = chartTask;
    }

    public void setUpdateOSC(boolean updateOSC) {
        this.updateOSC = updateOSC;
    }

    public boolean isUpdateOSC() {
        return updateOSC;
    }

    public void start(ChartTasks chartTasks) {
        if (running) {
            return;
        }

        if(chartTasks == ChartTasks.CHART_TASK_ONE) {
            updateOSC = true;
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
            updateOSC = false;
            running = false;
            logger.debug("STOP timers");
        }
    }
}
