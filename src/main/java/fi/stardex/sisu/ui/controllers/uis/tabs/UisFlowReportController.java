package fi.stardex.sisu.ui.controllers.uis.tabs;

import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.model.uis.UisFlowModel;
import fi.stardex.sisu.model.uis.UisFlowModel.UisFlowResult;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;

public class UisFlowReportController {


    @FXML private TableView<UisFlowResult> flowTableView;
    @FXML private TableColumn<UisFlowResult, Test> flowTestNameColumn;
    @FXML private TableColumn<UisFlowResult, String> flowNominalColumn;
    @FXML private TableColumn<UisFlowResult, String> flow1Column;
    @FXML private TableColumn<UisFlowResult, String> flow2Column;
    @FXML private TableColumn<UisFlowResult, String> flow3Column;
    @FXML private TableColumn<UisFlowResult, String> flow4Column;
    @FXML private TableColumn<UisFlowResult, String> flow5Column;
    @FXML private TableColumn<UisFlowResult, String> flow6Column;
    @FXML private TableColumn<UisFlowResult, String> flow7Column;
    @FXML private TableColumn<UisFlowResult, String> flow8Column;
    @FXML private TableColumn<UisFlowResult, Boolean> deleteColumn;

    private static final String CELL_COLOR_DEFAULT = "-fx-text-fill: #bf8248; -fx-alignment: CENTER";
    private static final String CELL_COLOR_ORANGE = "-fx-text-fill: orange; -fx-alignment: CENTER";
    private static final String CELL_COLOR_RED = "-fx-text-fill: red; -fx-alignment: CENTER";

    private I18N i18N;
    private MainSectionUisModel mainSectionUisModel;
    private UisFlowModel uisFlowModel;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setUisFlowModel(UisFlowModel uisFlowModel) {
        this.uisFlowModel = uisFlowModel;
    }

    @PostConstruct
    private void init() {
        setupTableColumns();
        bindingI18N();
        setupResultChangeListener();
    }

    private void setupTableColumns() {

        flowTestNameColumn.setCellValueFactory(c -> c.getValue().injectorTestProperty());
        flowNominalColumn.setCellValueFactory(c -> c.getValue().nominalFlowProperty());
        flow1Column.setCellValueFactory(c -> c.getValue().flow1Property());
        flow2Column.setCellValueFactory(c -> c.getValue().flow2Property());
        flow3Column.setCellValueFactory(c -> c.getValue().flow3Property());
        flow4Column.setCellValueFactory(c -> c.getValue().flow4Property());
        flow5Column.setCellValueFactory(c -> c.getValue().flow5Property());
        flow6Column.setCellValueFactory(c -> c.getValue().flow6Property());
        flow7Column.setCellValueFactory(c -> c.getValue().flow7Property());
        flow8Column.setCellValueFactory(c -> c.getValue().flow8Property());
        deleteColumn.setCellValueFactory(c -> new SimpleBooleanProperty());

        setCellFactory(flow1Column);
        setCellFactory(flow2Column);
        setCellFactory(flow3Column);
        setCellFactory(flow4Column);
        setCellFactory(flow5Column);
        setCellFactory(flow6Column);
        setCellFactory(flow7Column);
        setCellFactory(flow8Column);
        deleteColumn.setCellFactory(tableColumn -> new ButtonCell());
    }

    private void setupResultChangeListener(){

        ObservableList<UisFlowResult> flowResultsSource = FXCollections.observableArrayList();
        uisFlowModel.resultMapChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                flowResultsSource.clear();
                flowResultsSource.addAll(uisFlowModel.getResultObservableMap().values());
                flowTableView.setItems(flowResultsSource);
                flowTableView.refresh();
            }
        });
    }

    private void setCellFactory(TableColumn<UisFlowResult, String> column){

        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<UisFlowResult, String> call(TableColumn<UisFlowResult, String> param) {
                return new TableCell<>() {

                    @Override
                    protected void updateItem(String item, boolean empty) {

                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setStyle(null);
                        } else {
                            setText(item);

                            UisFlowResult flowTestResult = flowTableView.getItems().get(getTableRow().getIndex());

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

    private class ButtonCell extends TableCell<UisFlowResult, Boolean> {

        private final Button deleteButton;

        ButtonCell() {

            deleteButton = new Button("Delete");
            deleteButton.setStyle("-textFont-color: #1f1f2e");
            showNode(false, deleteButton);
            deleteButton.visibleProperty().bind(mainSectionUisModel.startButtonProperty().not());
            deleteButton.setOnAction(event -> uisFlowModel.deleteResult(getTableRow().getTableView().getItems().get(getTableRow().getIndex()).injectorTestProperty().get()));
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

    private void showNode(boolean show, Node... nodes) {
        for (Node node : nodes)
            node.setVisible(show);

    }

    private void bindingI18N() {

        flowTestNameColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.testName"));
        flowNominalColumn.textProperty().bind(i18N.createStringBinding("h4.report.table.label.nominal"));
    }
}
