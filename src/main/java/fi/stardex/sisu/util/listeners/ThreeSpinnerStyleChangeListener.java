package fi.stardex.sisu.util.listeners;

import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;


public class ThreeSpinnerStyleChangeListener implements ChangeListener<Boolean> {

    private Spinner<Integer> press;
    private Spinner<Double> current;
    private Spinner<Double> duty;
    private RegActive activeParam;
    private static final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

    public ThreeSpinnerStyleChangeListener(Spinner<Integer> press, Spinner<Double> current, Spinner<Double> duty, RegActive activeParam) {
        this.press = press;
        this.current = current;
        this.duty = duty;
        this.activeParam = activeParam;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            switch (activeParam) {
                case PRESSURE:
                    press.getStyleClass().set(1, GREEN_STYLE_CLASS);
                    current.getStyleClass().set(1, "");
                    duty.getStyleClass().set(1, "");
                    break;
                case CURRENT:
                    press.getStyleClass().set(1, "");
                    current.getStyleClass().set(1, GREEN_STYLE_CLASS);
                    duty.getStyleClass().set(1, "");
                    break;
                case DUTY:
                    press.getStyleClass().set(1, "");
                    current.getStyleClass().set(1, "");
                    duty.getStyleClass().set(1, GREEN_STYLE_CLASS);
                    break;
            }
        }
    }
}
