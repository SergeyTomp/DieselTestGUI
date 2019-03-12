package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;

public class CalibrationTestErrorController {

    @FXML private Button okButton;
    @FXML private Label errorLabel;

    private Stage errorStage;
    private Parent errorParent;
    private Parent rootParent;

    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setErrorParent(Parent errorParent) {
        this.errorParent = errorParent;
    }
    public void setRootParent(Parent rootParent) {
        this.rootParent = rootParent;
    }

    @PostConstruct
    public void init() {

        okButton.setOnAction(actionEvent -> errorStage.close());
        bindingI18N();
    }

    public void initErrorStage() {

        if (errorStage == null) {

            errorStage = new Stage(StageStyle.UNDECORATED);
            errorStage.setScene(new Scene(errorParent));
            errorStage.initModality(Modality.WINDOW_MODAL);
            errorStage.initOwner(rootParent.getScene().getWindow());
        }
        errorStage.show();
    }

    private void bindingI18N() {

        errorLabel.textProperty().bind(i18N.createStringBinding("pump.test.calibrationTestError"));
        okButton.textProperty().bind(i18N.createStringBinding("dialog.customer.close"));
    }

}
