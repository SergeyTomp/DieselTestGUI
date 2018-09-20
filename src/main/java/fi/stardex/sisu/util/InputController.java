package fi.stardex.sisu.util;


import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class InputController {

    public static void textReject (TextField textField, KeyEvent event, int maxLength){
        if (textField.getText().length() >= maxLength) {
            event.consume();
        }
        if (event.getCharacter().matches("[0-9.]")) {
            if (textField.getText().contains(".") && event.getCharacter().matches("[.]")) {
                event.consume();
            } else if (textField.getText().length() == 0 && event.getCharacter().matches("[.]")) {
                event.consume();
            }
        } else {
            event.consume();
        }
    }
    public static void blockTextInputToNumberFields(TextField field, int maxLength) {

        field.addEventFilter(KeyEvent.KEY_TYPED, e -> {
            TextField txt_TextField = (TextField) e.getSource();
            if (txt_TextField != null && txt_TextField.getText() != null) {
                textReject(txt_TextField, e, maxLength);
            }
        });
    }

    public static void blockTextInputToSpinners(Spinner spinner, int maxLength){
        spinner.getEditor().addEventFilter(KeyEvent.KEY_TYPED, e -> {
            TextField txt_TextField = (TextField) e.getSource();
            textReject(txt_TextField, e, maxLength);
        });
    }
}
