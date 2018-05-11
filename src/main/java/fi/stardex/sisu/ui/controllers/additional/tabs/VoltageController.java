package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.ui.ViewHolder;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;

public class VoltageController {

    @Autowired
    @Qualifier(value = "voltAmpereProfileDialog")
    private ViewHolder voltAmpereProfileDialog;

    @FXML
    private LineChart<Double, Double> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @FXML
    private Spinner<Double> voltage;
    @FXML
    private Spinner<Double> voltageHold;
    @FXML
    private Spinner<Integer> firstWidth;
    @FXML
    private Spinner<Double> firstCurrent;
    @FXML
    private Spinner<Double> secondCurrent;
    @FXML
    private Spinner<Double> boostCurrent;
    @FXML
    private Spinner<Double> firstNegative;
    @FXML
    private Spinner<Double> secondNegative;

    @FXML
    private Label labelVoltage;
    @FXML
    private Label labelFirstWidth;
    @FXML
    private Label labelCurrent1;
    @FXML
    private Label labelCurrent2;
    @FXML
    private Label labelCurrentBoost;
    @FXML
    private Label labelVoltageHold;
    @FXML
    private Label labelVoltageFirst;
    @FXML
    private Label labelVoltageSecond;

    @FXML
    private Label labelUnitsBoostU;
    @FXML
    private Label labelUnitsFirstW;
    @FXML
    private Label labelUnitsFirstI;
    @FXML
    private Label labelUnitsSecondI;
    @FXML
    private Label labelUnitsBoostI;
    @FXML
    private Label labelUnitsBatteryU;
    @FXML
    private Label labelUnitsNegativeU1;
    @FXML
    private Label labelUnitsNegativeU2;
    @FXML
    private CheckBox checkBoxBoostDisable;

    @FXML
    private Button pulseSettingsButton;

    private Stage voapStage;

    @PostConstruct
    private void init() {
        pulseSettingsButton.setOnMouseClicked(event -> {
            if(voapStage == null) {
                voapStage = new Stage();
                voapStage.setTitle("Settings");
                voapStage.setScene(new Scene(voltAmpereProfileDialog.getView()));
                voapStage.setResizable(false);
                voapStage.initModality(Modality.APPLICATION_MODAL);
            }
            voapStage.show();
        });

        firstWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 10000, 100, 10));
        voltage.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(30, 350, 0, 1));
        voltageHold.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(11, 30, 11.0, 0.1));
        firstCurrent.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 25, 0, 0.1));
        secondCurrent.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 25, 0, 0.1));
        boostCurrent.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 25, 0, 0.1));
        firstNegative.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(12, 75, 12, 1));
        secondNegative.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 75, 0, 1));

        xAxis.setMinorTickVisible(false);
        yAxis.setLowerBound(-15);
        yAxis.setUpperBound(25);
        yAxis.setTickUnit(5);
        lineChart.setTitle("");
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.getXAxis().setAutoRanging(true);
        lineChart.getYAxis().setAutoRanging(false);
        lineChart.getXAxis().setTickMarkVisible(true);

        setVisibleVoltage(false);
    }

    public void setVisibleVoltage(final boolean value) {
        labelCurrentBoost.setVisible(value);
        labelVoltageHold.setVisible(value);
        labelVoltageFirst.setVisible(value);
        labelVoltageSecond.setVisible(value);

        boostCurrent.setVisible(value);
        voltageHold.setVisible(value);
        firstNegative.setVisible(value);
        secondNegative.setVisible(value);

        labelUnitsBoostI.setVisible(value);
        labelUnitsBatteryU.setVisible(value);
        labelUnitsNegativeU1.setVisible(value);
        labelUnitsNegativeU2.setVisible(value);
        checkBoxBoostDisable.setVisible(value);
    }
}
