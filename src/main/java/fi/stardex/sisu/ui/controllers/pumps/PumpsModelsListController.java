package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class PumpsModelsListController {
    @FXML private VBox injectorsVBox;
    @FXML private ListView modelListView;
    @FXML private TextField searchModelTextField;
    @FXML private RadioButton customRadioButton;
    @FXML private RadioButton defaultRadioButton;
    @FXML private ToggleGroup baseTypeToggleGroup;

    public VBox getInjectorsVBox() {
        return injectorsVBox;
    }

    public ListView getModelListView() {
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
}
