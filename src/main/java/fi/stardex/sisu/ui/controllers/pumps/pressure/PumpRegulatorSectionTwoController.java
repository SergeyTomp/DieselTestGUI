package fi.stardex.sisu.ui.controllers.pumps.pressure;

import eu.hansolo.medusa.Gauge;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

public class PumpRegulatorSectionTwoController {

    @FXML private Label currentLabel;
    @FXML private Label dutyLabel;
    @FXML private Label labelRegNumber;
    @FXML private ToggleButton regToggleButton;
    @FXML private StackPane rootStackPane;
    @FXML private Spinner currentSpinner;
    @FXML private Spinner dutySpinner;
    @FXML private VBox gaugeVBox;

    private Gauge gauge1;
    private I18N i18N;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){
        gaugeVBox.getChildren().add(1, GaugeCreator.createPumpGauge());
        bindingI18N();
    }

    private void bindingI18N() {

        currentLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.amp"));
        dutyLabel.textProperty().bind(i18N.createStringBinding("highPressure.label.cycle"));
        labelRegNumber.textProperty().bind(i18N.createStringBinding("highPressure.label.reg2.name"));

    }
}
