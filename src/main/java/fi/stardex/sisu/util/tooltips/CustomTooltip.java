package fi.stardex.sisu.util.tooltips;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import java.lang.reflect.Field;

public class CustomTooltip extends Tooltip implements ChangeListener<Boolean> {

    private Number spinnerOldValue;

    private Number initialSpinnerOldValue;

    public CustomTooltip() {
        this("Press ENTER to commit changes");
    }

    public CustomTooltip(String message) {
        super(message);
        hackTooltipStartTiming();
        showingProperty().addListener(this);
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

    /**
     * @param spinnerOldValue saves all last value when spinner textProperty listener is fired
     */
    public void setSpinnerOldValue(Number spinnerOldValue) {
        this.spinnerOldValue = spinnerOldValue;
    }

    public Number getInitialSpinnerOldValue() {
        return initialSpinnerOldValue;
    }

    /**
     * saves the last spinner value before the tooltip starts showing
     */
    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue)
            initialSpinnerOldValue = spinnerOldValue;
    }
}
