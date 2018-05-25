package fi.stardex.sisu.util.tooltips;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Field;

public class CustomTooltip extends Tooltip {

    private Number spinnerOldValue;

    private Number spinnerNewValue;

    public CustomTooltip(String message) {
        super(message);
        hackTooltipStartTiming();
    }

    private void hackTooltipStartTiming() {
        try {
            Field fieldBehavior = Tooltip.class.getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(this);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(250)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Number getSpinnerOldValue() {
        return spinnerOldValue;
    }

    public void setSpinnerOldValue(Number spinnerOldValue) {
        this.spinnerOldValue = spinnerOldValue;
    }

    public Number getSpinnerNewValue() {
        return spinnerNewValue;
    }

    public void setSpinnerNewValue(Number spinnerNewValue) {
        this.spinnerNewValue = spinnerNewValue;
    }
}
