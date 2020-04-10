package fi.stardex.sisu.ui.controllers.common;

import fi.stardex.sisu.main.LogoController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class StardexLogoController extends LogoController {

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label versionLabel;

    @Override
    public BorderPane getRootBorderPane() {
        return rootBorderPane;
    }
    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    @Override
    public Label getVersionLabel() {
        return versionLabel;
    }
}
