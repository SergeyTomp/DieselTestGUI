package fi.stardex.sisu.util.spinners;

import fi.stardex.sisu.util.InputController;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

public class SpinnerManager {

    private static boolean escapePressed;

    private static final int MAX_LENGTH = 10;

    public static void setupDoubleSpinner(Spinner<Double> spinner) {

        StringConverter<Double> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Double object) {
                return converter.toString(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    if (string.isEmpty())
                        throw new RuntimeException("Invalid spinner value!");
                    return converter.fromString(string);
                } catch (RuntimeException ex) {
                    spinner.cancelEdit();
                    return spinner.getValue();
                }
            }
        });

        setupUtilityListeners(spinner);
    }

    public static void setupIntegerSpinner(Spinner<Integer> spinner) {

        StringConverter<Integer> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<>() {

            @Override
            public String toString(Integer object) {
                return converter.toString(object);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    if (string.isEmpty()){
                        throw new RuntimeException("Invalid spinner value!");
                    }

                    return converter.fromString(string);
                } catch (RuntimeException ex) {

                    spinner.cancelEdit();
                    return spinner.getValue();
                }
            }
        });

        setupUtilityListeners(spinner);
    }

    private static void setupUtilityListeners(Spinner<? extends Number> spinner) {

        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                Platform.runLater(spinner.getEditor()::selectAll);
            }
        });

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                spinner.cancelEdit();
                escapePressed = false;
            }
        });

        InputController.blockTextInputToSpinners(spinner, MAX_LENGTH);

        Tooltip tooltip = new Tooltip("Press ESCAPE to reject changes");

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {

            if (!escapePressed) {
                Point2D p = spinner.localToScene(0.0, 0.0);
                spinner.setTooltip(tooltip);
                if (!tooltip.isShowing() && spinner.getScene() != null) {
                    tooltip.show(spinner,
                            p.getX() + spinner.getScene().getX() + spinner.getScene().getWindow().getX(),
                            p.getY() + spinner.getScene().getY() + spinner.getHeight() + spinner.getScene().getWindow().getY());
                }

                spinner.setTooltip(null);
            }
        });

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {

            spinner.setTooltip(tooltip);
            spinner.getTooltip().hide();
            spinner.setTooltip(null);
        });
    }
}
