package fi.stardex.sisu.charts;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.ui.controllers.common.GUI_TypeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TimerTasksManager {

    private final Logger logger = LoggerFactory.getLogger(TimerTasksManager.class);

    private List<ChartTask> listOfCharts;

    private static final int DELAY = 0;

    private static final int PERIOD = 100;

    private static final int DELAY_CHART_PERIOD = 3000;

    private volatile boolean running;

    private Timer timer;
    @Autowired
    private GUI_TypeModel gui_typeModel;

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

    @Lookup
    @Qualifier("delayChartTask")
    public DelayChartTask getDelayChartTask() {
        return null;
    }

    public void start() {

        if (running)
            return;

        //TODO - для UIS временно отключены задачи графиков 3, 4 и DelayChartTask(), пока не унифицируется задача DelayChartTask()
        if (gui_typeModel.guiTypeProperty().get() == GUI_TypeController.GUIType.CR_Inj) {

            listOfCharts = new ArrayList<>(Arrays.asList(getChartTaskOne(), getChartTaskTwo(), getChartTaskThree(), getChartTaskFour(), getDelayChartTask()));
        }else
            listOfCharts = new ArrayList<>(Arrays.asList(getChartTaskOne(), getChartTaskTwo(), getDelayChartTask()));


        listOfCharts.forEach(chartTask -> {

            chartTask.setUpdateOSC(true);

            timer = new Timer();

            if (chartTask.getClass().isAssignableFrom(DelayChartTask.class))
                timer.schedule(chartTask, DELAY, DELAY_CHART_PERIOD);
            else
                timer.schedule(chartTask, DELAY, PERIOD);

            timersList.add(timer);
        });

        running = true;
        logger.debug("START timers");

    }

    public void stop() {

        if (running) {

            timersList.forEach(timer -> {
                timer.cancel();
                timer.purge();
            });

            timersList.clear();

            listOfCharts.forEach(chartTask -> {
                chartTask.setUpdateOSC(false);
                chartTask.getData().clear();
            });

            running = false;
            logger.debug("STOP timers");

        }

    }

}
