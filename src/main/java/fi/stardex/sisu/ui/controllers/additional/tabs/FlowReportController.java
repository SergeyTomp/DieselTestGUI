package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.store.FlowReport;
import fi.stardex.sisu.store.FlowReport.FlowTestResult;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class FlowReportController {

    @FXML
    private Label flowReportAttentionLabel;

    @FXML
    private TableView<FlowTestResult> flowTableView;

    @FXML
    private TableColumn<FlowTestResult, InjectorTest> flowTestNameColumn;

    @FXML
    private TableColumn<FlowTestResult, String> flowTypeColumn;

    @FXML
    private TableColumn<FlowTestResult, String> flowNominalColumn;

    @FXML
    private TableColumn<FlowTestResult, String> flow1Column;

    @FXML
    private TableColumn<FlowTestResult, String> flow2Column;

    @FXML
    private TableColumn<FlowTestResult, String> flow3Column;

    @FXML
    private TableColumn<FlowTestResult, String> flow4Column;

    @FXML
    private TableColumn<FlowTestResult, Boolean> deleteColumn;

    private I18N i18N;

    private static final String CELL_COLOR_DEFAULT = "-fx-text-fill: #bf8248;";

    private static final String CELL_COLOR_ORANGE = "-fx-text-fill: orange;";

    private static final String CELL_COLOR_RED = "-fx-text-fill: red;";

    private FlowReport flowReport;

    public void setFlowReport(FlowReport flowReport) {
        this.flowReport = flowReport;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public TableView<FlowTestResult> getFlowTableView() {
        return flowTableView;
    }

    public Label getFlowReportAttentionLabel() {
        return flowReportAttentionLabel;
    }

    @PostConstruct
    private void init() {

        setupTableColumns();
        bindingI18N();
    }

    private void setupTableColumns() {

        flowTestNameColumn.setCellValueFactory(new PropertyValueFactory<>("injectorTest"));
        flowTypeColumn.setCellValueFactory(new PropertyValueFactory<>("flowType"));
        flowNominalColumn.setCellValueFactory(new PropertyValueFactory<>("nominalFlow"));
        flow1Column.setCellValueFactory(new PropertyValueFactory<>("flow1"));
        flow2Column.setCellValueFactory(new PropertyValueFactory<>("flow2"));
        flow3Column.setCellValueFactory(new PropertyValueFactory<>("flow3"));
        flow4Column.setCellValueFactory(new PropertyValueFactory<>("flow4"));
        deleteColumn.setCellValueFactory(param -> new SimpleBooleanProperty());

        setCellFactory(flow1Column);
        setCellFactory(flow2Column);
        setCellFactory(flow3Column);
        setCellFactory(flow4Column);

        deleteColumn.setCellFactory(tableColumn -> new ButtonCell());

    }

    private void setCellFactory(TableColumn<FlowTestResult, String> column) {

        column.setCellFactory(new Callback<TableColumn<FlowTestResult, String>, TableCell<FlowTestResult, String>>() {
            @Override
            public TableCell<FlowTestResult, String> call(TableColumn<FlowTestResult, String> param) {
                return new TableCell<FlowTestResult, String>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);

                            FlowTestResult flowTestResult = flowTableView.getItems().get(getTableRow().getIndex());

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
                return ((cellValue < flowRangeLeft) && (cellValue >= acceptableFlowRangeLeft)) || ((cellValue > flowRangeRight) && (cellValue <= acceptableFlowRangeRight));
            }

            private boolean beyondAcceptableRange() {
                return (cellValue < acceptableFlowRangeLeft) || (cellValue > acceptableFlowRangeRight);
            }

        }

        Range range = new Range();

        if (cellValue == 0d || range.beyondAcceptableRange())
            return CELL_COLOR_RED;
        else if (range.inAcceptableRange())
            return CELL_COLOR_ORANGE;
        else
            throw new RuntimeException("Invalid cell value");

    }

    private class ButtonCell extends TableCell<FlowTestResult, Boolean> {

        private final Button deleteButton = new Button("Delete");

        ButtonCell() {

            deleteButton.setOnAction(event -> flowReport.delete(getTableRow().getIndex()));

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
    private void bindingI18N() {
        flowTestNameColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        flowTypeColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.delRec"));
        flowNominalColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.nominal"));
    }
}
