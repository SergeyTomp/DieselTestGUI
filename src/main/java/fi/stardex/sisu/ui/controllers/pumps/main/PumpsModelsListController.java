package fi.stardex.sisu.ui.controllers.pumps.main;

import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import javax.annotation.PostConstruct;

public class PumpsModelsListController implements ListChangeListener<Pump> {

    @FXML private VBox injectorsVBox;

    @FXML private ListView<Pump> modelListView;

    @FXML private TextField searchModelTextField;

    @FXML private RadioButton customRadioButton;

    @FXML private RadioButton defaultRadioButton;

    @FXML private ToggleGroup baseTypeToggleGroup;

    private PumpModel pumpModel;

    public VBox getInjectorsVBox() {
        return injectorsVBox;
    }

    public ListView<Pump> getModelListView() {
        return modelListView;
    }

    public TextField getSearchModelTextField() {
        return searchModelTextField;
    }

    public RadioButton getCustomRadioButton() {
        return customRadioButton;
    }

    public RadioButton getDefaultRadioButton() {
        return defaultRadioButton;
    }

    public ToggleGroup getBaseTypeToggleGroup() {
        return baseTypeToggleGroup;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    private void init() {

        pumpModel.getPumpObservableList().addListener(this);

    }

    @Override
    public void onChanged(Change<? extends Pump> change) {

        modelListView.getItems().setAll(change.getList());

    }

}
