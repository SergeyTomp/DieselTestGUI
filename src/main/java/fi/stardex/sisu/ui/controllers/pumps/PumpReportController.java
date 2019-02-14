package fi.stardex.sisu.ui.controllers.pumps;

import fi.stardex.sisu.model.PumpReportModel;
import fi.stardex.sisu.model.PumpReportModel.PumpFlowResult;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.states.PumpsStartButtonState;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.enums.TestPassed;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.enums.Measurement.BACK_FLOW;
import static fi.stardex.sisu.util.enums.Measurement.DELIVERY;

public class PumpReportController {

    @FXML private TableView<PumpFlowResult> flowTableView;
    @FXML private TableColumn<PumpFlowResult, PumpTest> testNameColumn;
    @FXML private TableColumn<PumpFlowResult, String> rpmColumn;
    @FXML private TableColumn<PumpFlowResult, String> pressColumn;
    @FXML private TableColumn<PumpFlowResult, String> scvColumn;
    @FXML private TableColumn<PumpFlowResult, String> pcvColumn;
    @FXML private TableColumn<PumpFlowResult, String> deliveryNominalColumn;
    @FXML private TableColumn<PumpFlowResult, String> deliveryFlowColumn;
    @FXML private TableColumn<PumpFlowResult, String> backFlowNominalColumn;
    @FXML private TableColumn<PumpFlowResult, String> backFlowColumn;
    @FXML private TableColumn<PumpFlowResult, Boolean> deleteColumn;
    @FXML private TableColumn<PumpFlowResult, TestPassed> passedColumn;

    private I18N i18N;
    private PumpReportModel pumpReportModel;
    private PumpsStartButtonState pumpsStartButtonState;
    private static final String CELL_COLOR_DEFAULT = "-fx-text-fill: #bf8248; -fx-alignment: CENTER;";
    private static final String CELL_COLOR_ORANGE = "-fx-text-fill: orange;  -fx-alignment: CENTER;";
    private static final String CELL_COLOR_RED = "-fx-text-fill: red;  -fx-alignment: CENTER;";

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }
    public void setPumpsStartButtonState(PumpsStartButtonState pumpsStartButtonState) {
        this.pumpsStartButtonState = pumpsStartButtonState;
    }

    @PostConstruct
    public void init() {

        setupResultChangeListener();
        bindingI18N();
        setupTableColumns();
    }

    private void setupTableColumns() {

        testNameColumn.setCellValueFactory(param -> param.getValue().pumpTestProperty());
        rpmColumn.setCellValueFactory(param -> param.getValue().rotatesPerMinuteProperty());
        pressColumn.setCellValueFactory(param -> param.getValue().standPressureProperty());
        scvColumn.setCellValueFactory(param -> param.getValue().scvCurrentProperty());
        pcvColumn.setCellValueFactory(param -> param.getValue().psvCurrentProperty());
        deliveryNominalColumn.setCellValueFactory(param -> param.getValue().nominalDeliveryFlowProperty());
        deliveryFlowColumn.setCellValueFactory(param -> param.getValue().deliveryFlowProperty());
        backFlowNominalColumn.setCellValueFactory(param -> param.getValue().nominalBackFlowProperty());
        backFlowColumn.setCellValueFactory(param -> param.getValue().backFlowProperty());
        deleteColumn.setCellValueFactory(param -> new SimpleBooleanProperty());
        passedColumn.setCellValueFactory(param -> param.getValue().testPassedProperty());

        setCellFactory(deliveryFlowColumn, DELIVERY);
        setCellFactory(backFlowColumn, BACK_FLOW);

        deleteColumn.setCellFactory(tableColumn -> new ButtonCell());

        centerColumnText(rpmColumn);
        centerColumnText(pressColumn);
        centerColumnText(scvColumn);
        centerColumnText(pcvColumn);
        centerColumnText(deliveryNominalColumn);
        centerColumnText(backFlowNominalColumn);

        passedColumn.setCellFactory(param -> new PassedCell<>());
    }

    private void centerColumnText(TableColumn tableColumn) {

        tableColumn.setStyle( "-fx-alignment: CENTER;");
    }

    private void setCellFactory(TableColumn<PumpFlowResult, String> column, Measurement flowType) {

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PumpFlowResult, String> call(TableColumn<PumpFlowResult, String> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);

                            PumpFlowResult flowTestResult = flowTableView.getItems().get(getTableRow().getIndex());

                            double flowRangeLeft = flowTestResult.getFlowRangeLeft(flowType);
                            double flowRangeRight = flowTestResult.getFlowRangeRight(flowType);
                            double acceptableFlowRangeLeft = flowTestResult.getAcceptableFlowRangeLeft(flowType);
                            double acceptableFlowRangeRight = flowTestResult.getAcceptableFlowRangeRight(flowType);

                            if (item.equals("-") || (acceptableFlowRangeLeft == 0 && acceptableFlowRangeRight == 0)){

                                setStyle(CELL_COLOR_DEFAULT);

                            }
                            else {
                                setStyle(getColorForCell(convertDataToDouble(item), flowRangeLeft, flowRangeRight, acceptableFlowRangeLeft, acceptableFlowRangeRight));
                            }

                        }
                    }
                };
            }
        });
    }
    private static String getColorForCell(Double cellValue, double flowRangeLeft, double flowRangeRight, double acceptableFlowRangeLeft, double acceptableFlowRangeRight) {

        class Range {

            private boolean inAcceptableRange() {
                return ((cellValue <= flowRangeLeft) && (cellValue >= acceptableFlowRangeLeft)) || ((cellValue >= flowRangeRight) && (cellValue <= acceptableFlowRangeRight));
            }

            private boolean beyondAcceptableRange() {
                return (cellValue < acceptableFlowRangeLeft) || (cellValue > acceptableFlowRangeRight);
            }
            private boolean inRange(){
                return (cellValue > flowRangeLeft && cellValue < flowRangeRight);
            }
        }

        Range range = new Range();

        if (cellValue == 0d || range.beyondAcceptableRange())
            return CELL_COLOR_RED;
        else if (range.inAcceptableRange())
            return CELL_COLOR_ORANGE;
        else if(range.inRange())
            return CELL_COLOR_DEFAULT;
        else throw new RuntimeException("Invalid cell value");
    }

    private void setupResultChangeListener(){

        ObservableList<PumpFlowResult> flowResultsSource = FXCollections.observableArrayList();
        pumpReportModel.getResultObservableMap().addListener((MapChangeListener<PumpTest, PumpFlowResult>) change -> {
            flowResultsSource.clear();
            flowResultsSource.addAll(pumpReportModel.getResultObservableMap().values());
            flowTableView.setItems(flowResultsSource);
            flowTableView.refresh();
        });
    }

    private void bindingI18N() {

        testNameColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        rpmColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.motorSpeed"));
        pressColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.pressure"));
        deliveryNominalColumn.textProperty().bind(i18N.createStringBinding("flow.label.deliveryNominal"));
        deliveryFlowColumn.textProperty().bind(i18N.createStringBinding("flow.label.delivery"));
        backFlowNominalColumn.textProperty().bind(i18N.createStringBinding("flow.label.backFlowNominal"));
        backFlowColumn.textProperty().bind(i18N.createStringBinding("flow.label.backflow"));
    }


    private class ButtonCell extends TableCell<PumpFlowResult, Boolean> {

        private final Button deleteButton;

        ButtonCell() {

            deleteButton = new Button("Delete");
            deleteButton.setStyle("-textFont-color: #1f1f2e");
            deleteButton.setVisible(false);
            deleteButton.visibleProperty().bind(pumpsStartButtonState.startButtonProperty().not());
            deleteButton.setOnAction(event -> pumpReportModel.deleteResult(getTableRow().getTableView().getItems().get(getTableRow().getIndex()).getPumpTest()));
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {

            super.updateItem(item, empty);

            if (item == null || empty)
                setGraphic(null);
            else
                setGraphic(deleteButton);
        }
    }

    private class PassedCell<T> extends TableCell<T, TestPassed> {

        private final ImageView image;

        public PassedCell() {
            // add ImageView as graphic to display it in addition
            // to the text in the cell
            image = new ImageView();
            image.setFitWidth(16);
            image.setFitHeight(16);
            image.setPreserveRatio(true);

            setGraphic(image);
        }

        @Override
        protected void updateItem(TestPassed item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                // set back to look of empty cell
                image.setImage(null);
            } else {
                // set image and text for non-empty cell
                image.setImage(item.getPict());
            }
        }
    }
}
