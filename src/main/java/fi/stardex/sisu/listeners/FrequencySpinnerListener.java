package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import javax.annotation.PostConstruct;

public class FrequencySpinnerListener implements ChangeListener<Double>, EventHandler<MouseEvent> {

    private Spinner<Double> freqCurrentSignal;

    private StringConverter<Double> converter;

    private InjectorSwitchManager injectorSwitchManager;

    private static final double DEFAULT_VALUE = 16.67;

    public FrequencySpinnerListener(InjectorSectionController injectorSectionController,
                                    InjectorSwitchManager injectorSwitchManager) {
        freqCurrentSignal = injectorSectionController.getFreqCurrentSignal();
        converter = freqCurrentSignal.getValueFactory().getConverter();
        this.injectorSwitchManager = injectorSwitchManager;
    }

    @PostConstruct
    public void init() {

        // convert invalid text input back to double
        freqCurrentSignal.getValueFactory().setConverter(new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return converter.toString(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    return converter.fromString(string);
                } catch (RuntimeException ex) {
                    freqCurrentSignal.getValueFactory().setValue(DEFAULT_VALUE);
                    return freqCurrentSignal.getValue();
                }
            }
        });

        freqCurrentSignal.valueProperty().addListener(this);

        freqCurrentSignal.addEventHandler(MouseEvent.MOUSE_EXITED, this);

        // commit the value without pressing ENTER
        freqCurrentSignal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                freqCurrentSignal.increment(0);
            }
        });
    }

    @Override
    public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        if (newValue <= 50 && newValue >= 0.5)
            injectorSwitchManager.sendRefreshedLeds();
    }

    // when cursor exits the spinner node the parent node of the spinner requests the focus
    @Override
    public void handle(MouseEvent event) {
        freqCurrentSignal.getParent().requestFocus();
    }
}
