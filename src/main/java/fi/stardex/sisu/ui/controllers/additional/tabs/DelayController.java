package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.model.DelayReportModel;
import fi.stardex.sisu.model.InjectorTestModel;
import fi.stardex.sisu.ui.controllers.additional.TabSectionController;
import fi.stardex.sisu.util.DelayCalculator;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

import static fi.stardex.sisu.util.SpinnerDefaults.*;

public class DelayController {

    private I18N i18N;

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

    private String injectorTestName;

    private int channelNumber;

    public ObservableList<XYChart.Data<Double, Double>> getDelayData() {
        return delayData;
    }

    private ObservableList<XYChart.Data<Double, Double>> delayData;

    private DelayCalculator delayCalculator;

    private TabSectionController tabSectionController;

    private DelayReportModel delayReportModel;

    private InjectorTestModel injectorTestModel;

    public int getAddingTimeValue() {
        return Integer.parseInt(addingTime.getText());
    }

    public Spinner<Double> getSensitivitySpinner() {
        return sensitivitySpinner;
    }

    public TextField getMinimumDelay() {
        return minimumDelay;
    }

    public TextField getMaximumDelay() {
        return maximumDelay;
    }

    public TextField getAverageDelay() {
        return averageDelay;
    }

    public Button getSaveDelayButton() {
        return saveDelayButton;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    private ObjectProperty<Boolean> isTabDelayShowing = new SimpleObjectProperty<>();

    public ObjectProperty<Boolean> isTabDelayShowingProperty() {
        return isTabDelayShowing;
    }

    public void setTabSectionController(TabSectionController tabSectionController) {
        this.tabSectionController = tabSectionController;
    }

    public void setDelayCalculator(DelayCalculator delayCalculator) {
        this.delayCalculator = delayCalculator;
    }

    public void setDelayReportModel(DelayReportModel delayReportModel) {
        this.delayReportModel = delayReportModel;
    }

    public void setInjectorTestName(String injectorTestName) {
        this.injectorTestName = injectorTestName;
    }

    public void setChannelNumber(int channelNumber) {
        this.channelNumber = channelNumber;
    }

    public void setInjectorTestModel(InjectorTestModel injectorTestModel) {
        this.injectorTestModel = injectorTestModel;
    }

    @PostConstruct
    private void init() {

        bindingI18N();

        setupDelayChart();

        setupAddingTime();

        resetDelayButton.setOnAction(event -> clearDelayResults());
        saveDelayButton.setOnAction(event-> delayReportModel.storeResult(channelNumber, injectorTestName, averageDelay.getText()));
        saveDelayButton.setDisable(true);

        sensitivitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(SENSITIVITY_SPINNER_MIN,
                                                                                            SENSITIVITY_SPINNER_MAX,
                                                                                            SENSITIVITY_SPINNER_INIT,
                                                                                            SENSITIVITY_SPINNER_STEP));
        
        isTabDelayShowing.bind(tabSectionController.getTabDelay().selectedProperty());

        injectorTestModel.injectorTestProperty().addListener((observableValue, oldValue, newValue) -> {

            clearDelayResults();
            if (newValue != null) {

                Measurement measurementType = newValue.getTestName().getMeasurement();
                switch (measurementType) {

                    case NO:
                        saveDelayButton.setDisable(true);
                        break;
                    default:
                        setInjectorTestName(newValue.getTestName().toString());
                        saveDelayButton.setDisable(false);
                        break;
                }
            }
        });
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

    public void clearDelayResults() {

        delayCalculator.clearDelayValuesList();
        minimumDelay.setText("");
        maximumDelay.setText("");
        averageDelay.setText("");

    }

    public void showAttentionLabel(boolean notSingle) {

        delayAttentionLabel.setVisible(notSingle);

        for (Node node : delayChart.getChildrenUnmodifiable()) {
            node.setVisible(!notSingle);
        }

        for(Node node : delayResults.getChildrenUnmodifiable()){
            node.setVisible(!notSingle);
        }
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
