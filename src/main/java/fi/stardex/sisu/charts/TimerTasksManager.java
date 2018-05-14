package fi.stardex.sisu.charts;

import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTasksManager {

    private InjectorSectionController injectorSectionController;

    private static final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private static final int DELAY = 0;
    private static final int PERIOD = 100;

    private volatile boolean running;

    private Timer timer1;

    public TimerTasksManager(InjectorSectionController injectorSectionController) {
        this.injectorSectionController = injectorSectionController;
    }

    public void start(TimerTask timerTask) {
        if (running) {
            return;
        }
        injectorSectionController.setUpdateOSC(true);
        timer1 = new Timer();
        timer1.schedule(timerTask, DELAY, PERIOD);
        running = true;
        logger.debug("START timers");
    }


    public void stop() {
        if (running) {
            timer1.cancel();
            timer1.purge();
            injectorSectionController.setUpdateOSC(false);
            running = false;
            logger.debug("STOP timers");
        }
    }
}
