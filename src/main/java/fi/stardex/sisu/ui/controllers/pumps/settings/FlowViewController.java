package fi.stardex.sisu.ui.controllers.pumps.settings;

import fi.stardex.sisu.combobox_values.Dimension;
import fi.stardex.sisu.state.FlowViewState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class FlowViewController {

    @FXML
    private ComboBox <Dimension> flowOutputDimensionsComboBox;
    private FlowViewState flowViewState;
    private Preferences rootPrefs;
    private final String PREF_KEY = "flowOutputDimensionSelected";

    public ComboBox <Dimension> getFlowOutputDimensionsComboBox() {
        return flowOutputDimensionsComboBox;
    }

    public void setFlowViewState(FlowViewState flowViewState) {
        this.flowViewState = flowViewState;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){
        flowOutputDimensionsComboBox.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));
        flowViewState.flowViewStateProperty().bind(flowOutputDimensionsComboBox.valueProperty());
        flowOutputDimensionsComboBox.getSelectionModel().select(Dimension.valueOf(rootPrefs.get(PREF_KEY, Dimension.LIMIT.name())));
        flowOutputDimensionsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));
    }
}
