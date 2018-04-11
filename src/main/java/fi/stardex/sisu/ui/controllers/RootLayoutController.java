package fi.stardex.sisu.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class RootLayoutController {

    @FXML
    private VBox vBoxBackground;
    @FXML
    private GridPane rootLayout;
    @FXML
    private GridPane sectionLayout;

    public GridPane getRootLayout() {
        return rootLayout;
    }

    public GridPane getSectionLayout() {
        return sectionLayout;
    }

    public VBox getvBoxBackground() {
        return vBoxBackground;
    }
}
