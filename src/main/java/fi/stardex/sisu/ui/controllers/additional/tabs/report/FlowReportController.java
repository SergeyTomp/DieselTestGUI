package fi.stardex.sisu.ui.controllers.additional.tabs.report;

import fi.stardex.sisu.model.FlowReportModel;
import fi.stardex.sisu.model.FlowReportModel.FlowResult;
import fi.stardex.sisu.model.MainSectionModel;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class FlowReportController {

    @FXML
    private Label flowReportAttentionLabel;

    @FXML
    private TableView<FlowResult> flowTableView;

    @FXML
    private TableColumn<FlowResult, InjectorTest> flowTestNameColumn;

    @FXML
    private TableColumn<FlowResult, String> flowTypeColumn;

    @FXML
    private TableColumn<FlowResult, String> flowNominalColumn;

    @FXML
    private TableColumn<FlowResult, String> flow1Column;

    @FXML
    private TableColumn<FlowResult, String> flow2Column;

    @FXML
    private TableColumn<FlowResult, String> flow3Column;

    @FXML
    private TableColumn<FlowResult, String> flow4Column;

    @FXML
    private TableColumn<FlowResult, Boolean> deleteColumn;

    private I18N i18N;

    private ToggleButton mainSectionStartToggleButton;

    private MainSectionModel mainSectionModel;

    private static final String CELL_COLOR_DEFAULT = "-fx-text-fill: #bf8248;";

    private static final String CELL_COLOR_ORANGE = "-fx-text-fill: orange;";

    private static final String CELL_COLOR_RED = "-fx-text-fill: red;";

    private FlowReportModel flowReportModel;

    private BooleanProperty resultSourceChanged;

    public void setFlowReportModel(FlowReportModel flowReportModel) {
        this.flowReportModel = flowReportModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setMainSectionStartToggleButton(ToggleButton mainSectionStartToggleButton) {
        this.mainSectionStartToggleButton = mainSectionStartToggleButton;
    }

    public TableView<FlowResult> getFlowTableView() {
        return flowTableView;
    }

    public Label getFlowReportAttentionLabel() {
        return flowReportAttentionLabel;
    }

    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    @PostConstruct
    private void init() {
        setupTableColumns();
        bindingI18N();
        setupResultChangeListener();
        setupTestModeListener();
    }

    private void setupTableColumns() {

        flowTestNameColumn.setCellValueFactory(param -> param.getValue().injectorTestProperty());
        flowTypeColumn.setCellValueFactory(param -> param.getValue().flowTypeProperty());
        flowNominalColumn.setCellValueFactory(param -> param.getValue().nominalFlowProperty());
        flow1Column.setCellValueFactory(param -> param.getValue().flow1Property());
        flow2Column.setCellValueFactory(param -> param.getValue().flow2Property());
        flow3Column.setCellValueFactory(param -> param.getValue().flow3Property());
        flow4Column.setCellValueFactory(param -> param.getValue().flow4Property());

        deleteColumn.setCellValueFactory(param -> new SimpleBooleanProperty());

        setCellFactory(flow1Column);
        setCellFactory(flow2Column);
        setCellFactory(flow3Column);
        setCellFactory(flow4Column);

        deleteColumn.setCellFactory(tableColumn -> new ButtonCell());
    }

    private void setupResultChangeListener(){

        ObservableList<FlowResult> flowResultsSource = FXCollections.observableArrayList();
        resultSourceChanged = flowReportModel.resultMapChangedProperty();
        resultSourceChanged.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                flowResultsSource.clear();
                flowResultsSource.addAll(flowReportModel.getResultObservableMap().values());
                flowTableView.setItems(flowResultsSource);
                flowTableView.refresh();
                resultSourceChanged.setValue(false);
            }
        });
    }

    private void setupTestModeListener() {

        mainSectionModel.testTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            switch (newValue) {
                case AUTO:
                    showFlowReport(true);
                    break;
                case TESTPLAN:
                    showFlowReport(true);   // to switch off report table in TESTPLAN mode set false
                    break;
                case CODING:
                    showFlowReport(true);   // to switch off report table in CODING mode set false
                    break;
            }
        });
    }



    private void setCellFactory(TableColumn<FlowResult, String> column) {

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FlowResult, String> call(TableColumn<FlowResult, String> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);

                            FlowResult flowTestResult = flowTableView.getItems().get(getTableRow().getIndex());

                            double flowRangeLeft = flowTestResult.getFlowRangeLeft();
                            double flowRangeRight = flowTestResult.getFlowRangeRight();
                            double acceptableFlowRangeLeft = flowTestResult.getAcceptableFlowRangeLeft();
                            double acceptableFlowRangeRight = flowTestResult.getAcceptableFlowRangeRight();

                            if (item.equals("-"))
                                setStyle(CELL_COLOR_DEFAULT);
                            else
                                setStyle(getColorForCell(convertDataToDouble(item), flowRangeLeft, flowRangeRight, acceptableFlowRangeLeft, acceptableFlowRangeRight));
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



    private class ButtonCell extends TableCell<FlowResult, Boolean> {

        private final Button deleteButton;

        ButtonCell() {

            deleteButton = new Button("Delete");

            deleteButton.setStyle("-textFont-color: #1f1f2e");

            showNode(false, deleteButton);

            deleteButton.visibleProperty().bind(mainSectionStartToggleButton.selectedProperty().not());

            deleteButton.setOnAction(event -> flowReportModel.deleteResult(getTableRow().getTableView().getItems().get(getTableRow().getIndex()).getInjectorTest()));
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

    private void showFlowReport(boolean isTestAuto) { //could be false if some cases in setupTestModeListener() defined as false

        showNode(!isTestAuto, flowReportAttentionLabel);
        showNode(isTestAuto, flowTableView);
        flowReportModel.clearResults();
    }

    private void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }
    private void bindingI18N() {

        flowTestNameColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        flowTypeColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.delRec"));
        flowNominalColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.nominal"));
    }
}
