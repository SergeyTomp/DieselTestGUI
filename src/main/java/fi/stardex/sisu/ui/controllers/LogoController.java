package fi.stardex.sisu.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

public class LogoController {

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label versionLabel;

    public BorderPane getRootBorderPane() {
        return rootBorderPane;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getVersionLabel() {
        return versionLabel;
    }
}
