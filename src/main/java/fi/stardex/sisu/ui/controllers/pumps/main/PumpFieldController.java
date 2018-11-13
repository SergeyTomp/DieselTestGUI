package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;

public class PumpFieldController {

    @FXML private TextField pumpTextField;

    private PumpModel pumpModel;

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    private void init() {

        pumpTextField.textProperty().bind(pumpModel.pumpCodeProperty());

    }

}
