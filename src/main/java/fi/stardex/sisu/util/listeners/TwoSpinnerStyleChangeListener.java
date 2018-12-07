package fi.stardex.sisu.util.listeners;

import fi.stardex.sisu.util.enums.RegActive;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;


public class TwoSpinnerStyleChangeListener implements ChangeListener<Boolean> {

    private Spinner<Double> current;
    private Spinner<Double> duty;
    private RegActive activeParam;
    private static final String GREEN_STYLE_CLASS = "regulator-spinner-selected";

    public TwoSpinnerStyleChangeListener(Spinner<Double> current, Spinner<Double> duty, RegActive activeParam) {

        this.current = current;
        this.duty = duty;
        this.activeParam = activeParam;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            switch (activeParam) {

                case CURRENT:
                    current.getStyleClass().set(1, GREEN_STYLE_CLASS);
                    duty.getStyleClass().set(1, "");
                    break;
                case DUTY:
                    current.getStyleClass().set(1, "");
                    duty.getStyleClass().set(1, GREEN_STYLE_CLASS);
                    break;
            }
        }
    }
}
