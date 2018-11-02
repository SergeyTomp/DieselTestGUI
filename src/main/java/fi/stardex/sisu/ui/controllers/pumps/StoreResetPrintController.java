package fi.stardex.sisu.ui.controllers.pumps;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class StoreResetPrintController {

    @FXML private Button printButton;
    @FXML private Button storeButton;
    @FXML private Button resetButton;
    @FXML private HBox storePrintResetHBox;

    public Button getPrintButton() {
        return printButton;
    }

    public Button getStoreButton() {
        return storeButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public HBox getStorePrintResetHBox() {
        return storePrintResetHBox;
    }
}
