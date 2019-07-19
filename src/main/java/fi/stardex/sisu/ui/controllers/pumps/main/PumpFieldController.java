package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.states.PumpsStartButtonState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

public class PumpFieldController implements ChangeListener<Pump> {

    @FXML private TextField pumpTextField;

    private PumpModel pumpModel;
    private PumpsStartButtonState pumpsStartButtonState;

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }

    @PostConstruct
    private void init() {

        pumpModel.pumpProperty().addListener(this);
        pumpsStartButtonState.startButtonProperty().addListener((observableValue, oldValue, newValue) -> pumpTextField.setDisable(newValue));
    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpTextField.setText(newValue != null ? newValue.getPumpCode() : "");

    }
}
