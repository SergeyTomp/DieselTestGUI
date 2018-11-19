package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

public class PumpFieldController implements ChangeListener<Pump> {

    @FXML private TextField pumpTextField;

    private PumpModel pumpModel;

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    private void init() {

        pumpModel.pumpProperty().addListener(this);

    }

    @Override
    public void changed(ObservableValue<? extends Pump> observableValue, Pump oldValue, Pump newValue) {

        pumpTextField.setText(newValue != null ? newValue.getPumpCode() : "");

    }
}
