package fi.stardex.sisu.ui.controllers.additional.tabs;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Created
 * by eduard
 * on 25.04.17.
 */
public class CodingController {

    @FXML
    private TextField injectorCode1TextField;
    @FXML
    private TextField injectorCode2TextField;
    @FXML
    private TextField injectorCode3TextField;
    @FXML
    private TextField injectorCode4TextField;

    public TextField getInjectorCode1TextField() {
        return injectorCode1TextField;
    }

    public TextField getInjectorCode2TextField() {
        return injectorCode2TextField;
    }

    public TextField getInjectorCode3TextField() {
        return injectorCode3TextField;
    }

    public TextField getInjectorCode4TextField() {
        return injectorCode4TextField;
    }
}
