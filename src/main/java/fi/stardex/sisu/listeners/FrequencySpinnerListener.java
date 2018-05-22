package fi.stardex.sisu.listeners;

import fi.stardex.sisu.injectors.InjectorSwitchManager;
import fi.stardex.sisu.ui.controllers.cr.InjectorSectionController;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.util.StringConverter;

import javax.annotation.PostConstruct;

public class FrequencySpinnerListener implements ChangeListener<Double> {

    private Spinner<Double> freqCurrentSignal;

    private DoubleProperty spinnerValue = new SimpleDoubleProperty();

    private StringConverter<Double> converter;

    private InjectorSwitchManager injectorSwitchManager;

    public FrequencySpinnerListener(InjectorSectionController injectorSectionController, InjectorSwitchManager injectorSwitchManager) {
        freqCurrentSignal = injectorSectionController.getFreqCurrentSignal();
        spinnerValue.bind(freqCurrentSignal.valueProperty());
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
                } catch(RuntimeException ex) {
                    freqCurrentSignal.getValueFactory().setValue(16.67);
                    return spinnerValue.get();
                }
            }
        });

        freqCurrentSignal.valueProperty().addListener(this);

        // commit the value without pressing ENTER
        freqCurrentSignal.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                freqCurrentSignal.increment(0);
            }
        });
    }


    @Override
    public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        System.err.println("Frequency:" + newValue);
        injectorSwitchManager.sendRefreshedLeds();
    }
}
