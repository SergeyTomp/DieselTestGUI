package fi.stardex.sisu.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;

public class RootLayoutController {

    @FXML
    private GridPane rootLayout;
    @FXML
    private GridPane sectionLayout;

    @PostConstruct
    private void init() {
        rootLayout.add();
    }
}
