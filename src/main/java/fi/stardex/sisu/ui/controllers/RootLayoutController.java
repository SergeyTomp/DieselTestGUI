package fi.stardex.sisu.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class RootLayoutController {

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
}
