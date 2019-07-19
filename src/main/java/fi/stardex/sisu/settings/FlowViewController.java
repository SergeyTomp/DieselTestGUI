package fi.stardex.sisu.settings;

import fi.stardex.sisu.util.enums.Dimension;
import fi.stardex.sisu.model.pump.FlowViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class FlowViewController {

    @FXML
    private ComboBox <Dimension> flowOutputDimensionsComboBox;

    private FlowViewModel flowViewModel;

    private Preferences rootPrefs;

    private static final String PREF_KEY = "flowOutputDimensionSelected";

    public void setFlowViewModel(FlowViewModel flowViewModel) {
        this.flowViewModel = flowViewModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    @PostConstruct
    public void init(){

        flowOutputDimensionsComboBox.setItems(FXCollections.observableArrayList(Dimension.LIMIT, Dimension.PLUS_OR_MINUS));
        flowViewModel.flowViewProperty().bind(flowOutputDimensionsComboBox.valueProperty());
        flowOutputDimensionsComboBox.getSelectionModel().select(Dimension.valueOf(rootPrefs.get(PREF_KEY, Dimension.LIMIT.name())));
        flowOutputDimensionsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));

    }

}
