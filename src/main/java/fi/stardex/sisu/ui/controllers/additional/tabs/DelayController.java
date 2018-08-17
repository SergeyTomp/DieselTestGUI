package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.util.DelayCalculator;
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

public class DelayController {

    @FXML private LineChart<Double, Double> delayChart;

    @FXML private NumberAxis delayXAxis;

    @FXML private NumberAxis delayYAxis;

    @FXML private TextField minimumDelay;

    @FXML private TextField maximumDelay;

    @FXML private TextField averageDelay;

    @FXML private Spinner<Double> sensitivitySpinner;

    @FXML private Button resetDelayButton;

    @FXML private TextField addingTime;

    @FXML private Label delayAttentionLabel;

    @FXML private GridPane delayResults;

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

    public ObservableList<XYChart.Data<Double, Double>> getDelayData() {
        return delayData;
    }

    private ObservableList<XYChart.Data<Double, Double>> delayData;

    private DelayCalculator delayCalculator;

    private AdditionalSectionController additionalSectionController;

    private ObjectProperty<Boolean> isTabDelayShowing = new SimpleObjectProperty<>();

    public ObjectProperty<Boolean> isTabDelayShowingProperty() {
        return isTabDelayShowing;
    }

    public void setAdditionalSectionController(AdditionalSectionController additionalSectionController) {
        this.additionalSectionController = additionalSectionController;
    }

    public void setDelayCalculator(DelayCalculator delayCalculator) {
        this.delayCalculator = delayCalculator;
    }

    @PostConstruct
    private void init() {

        setupDelayChart();

        setupAddingTime();

        resetDelayButton.setOnAction(event -> clearDelayResults());

        sensitivitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 3.5, 1.7, 0.1));

        isTabDelayShowing.bind(additionalSectionController.getTabDelay().selectedProperty());

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

            addingTime.setText(newValue.isEmpty() ? "0" : oldValue);

        });

    }

    private void clearDelayResults() {

        delayCalculator.clearDelayValuesList();
        minimumDelay.setText("");
        maximumDelay.setText("");
        averageDelay.setText("");

    }

    public void showAttentionLabel(boolean notSingle) {

        delayAttentionLabel.setVisible(notSingle);

        for (Node node : delayChart.getChildrenUnmodifiable()) {
            node.setDisable(notSingle);
            node.setVisible(!notSingle);
        }

        for(Node node : delayResults.getChildrenUnmodifiable()){
            node.setDisable(notSingle);
        }

    }
}
