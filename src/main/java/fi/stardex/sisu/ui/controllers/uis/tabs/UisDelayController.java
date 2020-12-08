package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.ChartTaskDataModel;
import fi.stardex.sisu.model.uis.UisDelayModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisInjectorSectionModel;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class UisDelayController {

    @FXML private LineChart<Double, Double> delayChart;
    @FXML private NumberAxis delayXAxis;
    @FXML private NumberAxis delayYAxis;
    @FXML private TextField minimumDelay;
    @FXML private TextField maximumDelay;
    @FXML private TextField averageDelay;
    @FXML private Spinner<Double> sensitivitySpinner;
    @FXML private Button resetDelayButton;
    @FXML private Button saveDelayButton;
    @FXML private TextField addingTime;
    @FXML private Label delayAttentionLabel;
    @FXML private Label minDelay;
    @FXML private Label maxDelay;
    @FXML private Label avgDelay;
    @FXML private Label addTime;
    @FXML private Label sensitivity;
    @FXML private GridPane delayResults;

    private ObservableList<XYChart.Data<Double, Double>> delayData;
    private DelayCalculator delayCalculator;
    private MainSectionUisModel mainSectionUisModel;
    private I18N i18N;
    private ChartTaskDataModel chartTaskDataModel;
    private UisDelayModel uisDelayModel;
    private UisInjectorSectionModel uisInjectorSectionModel;

    public void setDelayCalculator(DelayCalculator delayCalculator) {
        this.delayCalculator = delayCalculator;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setChartTaskDataModel(ChartTaskDataModel chartTaskDataModel) {
        this.chartTaskDataModel = chartTaskDataModel;
    }
    public void setUisDelayModel(UisDelayModel uisDelayModel) {
        this.uisDelayModel = uisDelayModel;
    }

    public void setUisInjectorSectionModel(UisInjectorSectionModel uisInjectorSectionModel) {
        this.uisInjectorSectionModel = uisInjectorSectionModel;
    }

    @PostConstruct
    public void init() {
        bindingI18N();
        setupDelayChart();
        setupAddingTime();
        sensitivitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SENSITIVITY_SPINNER_MIN,
                SENSITIVITY_SPINNER_MAX,
                SENSITIVITY_SPINNER_INIT,
                SENSITIVITY_SPINNER_STEP));

        resetDelayButton.setOnAction(event -> clearDelayResults());
        saveDelayButton.setOnAction(event-> {
            uisDelayModel.setAverageDelay(averageDelay.getText());
            uisDelayModel.storeResult();
        });

        uisInjectorSectionModel.getSaveDelayButton().setOnAction(event -> saveDelayButton.fire());

        mainSectionUisModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == null) {
                saveDelayButton.setDisable(true);
                uisDelayModel.clearResults();
                return;
            }
            saveDelayButton.setDisable(false);
        });

        mainSectionUisModel.modelProperty().addListener((observableValue, oldValue, newValue) -> uisDelayModel.clearResults());

        addingTime.textProperty().addListener((observableValue, oldValue, newValue)
                -> uisDelayModel.addingTimeProperty().setValue(Integer.parseInt(newValue)));

        chartTaskDataModel.getDelayChartDataList().addListener((ListChangeListener<XYChart.Data<Double, Double>>) change -> {
            delayData.clear();
            delayData.setAll(change.getList());
        });

        chartTaskDataModel.delayValueProperty().addListener((observableValue, oldValue, newValue)
                -> averageDelay.setText(String.format("%.0f", newValue.doubleValue())));
        chartTaskDataModel.minDelayValueProperty().addListener((observableValue, oldValue, newValue)
                -> minimumDelay.setText(String.format("%.0f", newValue.doubleValue())));
        chartTaskDataModel.maxDelayValueProperty().addListener((observableValue, oldValue, newValue)
                -> maximumDelay.setText(String.format("%.0f", newValue.doubleValue())));
    }
    private void setupDelayChart() {

        XYChart.Series<Double, Double> delaySeries = new XYChart.Series<>();
        delaySeries.setName("");
        delayData = FXCollections.observableArrayList();
        delaySeries.setData(delayData);
        delayChart.getData().add(delaySeries);
        delayXAxis.setMinorTickVisible(false);
        delayYAxis.setTickUnit(0.5);
        delayYAxis.setLowerBound(0.0);
        delayYAxis.setUpperBound(3.5);
        delayChart.setTitle("");
        delayChart.setAnimated(false);
        delayChart.getYAxis().setAutoRanging(false);
        delayChart.setLegendVisible(false);
        delayChart.getXAxis().setAutoRanging(true);
        delayChart.getXAxis().setTickMarkVisible(true);

    }

    private void setupAddingTime() {

        addingTime.setText("0");
        Pattern p = Pattern.compile("^([1-9]\\d*)?$");

        addingTime.textProperty().addListener((observable, oldValue, newValue) -> {

            if (!p.matcher(newValue).matches()) {
                addingTime.setText(oldValue);
                return;
            }
            if (!newValue.isEmpty() && Integer.parseInt(newValue) > 5000) {
                addingTime.setText("5000");
                return;
            }
            addingTime.setText(newValue.isEmpty() ? "0" : newValue);
        });
    }

    private void clearDelayResults() {

        delayCalculator.clearDelayValuesList();
        minimumDelay.setText("");
        maximumDelay.setText("");
        averageDelay.setText("");

    }

    private void bindingI18N() {
        minDelay.textProperty().bind(i18N.createStringBinding("h4.delay.label.MinDelay"));
        maxDelay.textProperty().bind(i18N.createStringBinding("h4.delay.label.MaxDelay"));
        avgDelay.textProperty().bind(i18N.createStringBinding("h4.delay.label.AverDelay"));
        addTime.textProperty().bind(i18N.createStringBinding("h4.delay.label.AddingTime"));
        sensitivity.textProperty().bind(i18N.createStringBinding("h4.delay.label.Sens"));
        delayXAxis.labelProperty().bind(i18N.createStringBinding("h4.voltage.chars.time"));
        delayYAxis.labelProperty().bind(i18N.createStringBinding("h4.voltage.label.volt"));
        resetDelayButton.textProperty().bind(i18N.createStringBinding("h4.delay.button.reset"));
        saveDelayButton.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        delayAttentionLabel.textProperty().bind(i18N.createStringBinding("h4.delay.label.attention"));
    }
}
