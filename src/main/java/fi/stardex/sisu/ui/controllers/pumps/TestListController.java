package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class TestListController {

    @FXML private ListView testListView;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private HBox testListHBox;

    public ListView getTestListView() {
        return testListView;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }

}
