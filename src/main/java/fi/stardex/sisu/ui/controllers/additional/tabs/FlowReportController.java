package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.store.FlowReport.FlowTestResult;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.annotation.PostConstruct;

public class FlowReportController {

    @FXML
    private TableView<FlowTestResult> flowTableView;

    @FXML
    private TableColumn<FlowTestResult, String> flowTestNameColumn;

    @FXML
    private TableColumn<FlowTestResult, String> flowTypeColumn;

    @FXML
    private TableColumn<FlowTestResult, String> flowNominalColumn;

    @FXML
    private TableColumn<FlowTestResult, Double> flow1Column;

    @FXML
    private TableColumn<FlowTestResult, Double> flow2Column;

    @FXML
    private TableColumn<FlowTestResult, Double> flow3Column;

    @FXML
    private TableColumn<FlowTestResult, Double> flow4Column;

    public TableView<FlowTestResult> getFlowTableView() {
        return flowTableView;
    }

    @PostConstruct
    private void init() {

        setupTableColumns();

    }

    private void setupTableColumns() {

        flowTestNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));
        flowTypeColumn.setCellValueFactory(new PropertyValueFactory<>("flowType"));
        flowNominalColumn.setCellValueFactory(new PropertyValueFactory<>("nominalFlow"));
        flow1Column.setCellValueFactory(new PropertyValueFactory<>("flow1"));
        flow2Column.setCellValueFactory(new PropertyValueFactory<>("flow2"));
        flow3Column.setCellValueFactory(new PropertyValueFactory<>("flow3"));
        flow4Column.setCellValueFactory(new PropertyValueFactory<>("flow4"));

        setCellFactory(flow1Column);
        setCellFactory(flow2Column);
        setCellFactory(flow3Column);
        setCellFactory(flow4Column);

    }

    private void setCellFactory(TableColumn<FlowTestResult, Double> column) {

        column.setCellFactory(param -> new TableCell<FlowTestResult, Double>() {

            @Override
            protected void updateItem(Double item, boolean empty) {

                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle(null);
                } else {
                    setText(item.toString());

                    FlowTestResult flowTestResult = flowTableView.getItems().get(getTableRow().getIndex());

                    double flowRangeLeft = flowTestResult.getFlowRangeLeft();
                    double flowRangeRight = flowTestResult.getFlowRangeRight();
                    double acceptableFlowRangeLeft = flowTestResult.getAcceptableFlowRangeLeft();
                    double acceptableFlowRangeRight = flowTestResult.getAcceptableFlowRangeRight();

                    setStyle(getColorForCell(item, flowRangeLeft, flowRangeRight, acceptableFlowRangeLeft, acceptableFlowRangeRight));

                }

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

        if (cellValue == 0d)
            return "-fx-text-fill: #bf8248;"; // default color
        else if (range.inAcceptableRange())
            return "-fx-text-fill: orange;"; // orange color
        else if (range.beyondAcceptableRange())
            return "-fx-text-fill: red;"; // red color


        return null;

    }

}
