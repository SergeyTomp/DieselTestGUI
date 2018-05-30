package fi.stardex.sisu.util;

import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

//TODO: баги в спиннерах нужно тестить (Escape при фокусе на спиннере, fake_value для Spinner<Integer>)
public class SpinnerManager {

    private static boolean escapePressed;

    /**
     * regex checks for presence of characters/dots and digits both at a string
     */
    public static void setupSpinner(Spinner<Double> spinner, double initValue, double fake_value, CustomTooltip tooltip) {
        StringConverter<Double> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<Double>() {

            @Override
            public String toString(Double object) {
                return converter.toString(object);
            }

            @Override
            public Double fromString(String string) {
                try {
                    String regex = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9.]+$";
                    if (string.isEmpty() || string.matches(regex))
                        throw new RuntimeException("Invalid spinner value!");
                    return converter.fromString(string);
                } catch (RuntimeException ex) {
                    spinner.getValueFactory().setValue(fake_value);
                    spinner.getValueFactory().setValue(initValue);
                    return spinner.getValue();
                }
            }
        });

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!escapePressed) {
                try {
                    tooltip.setSpinnerOldValue(Double.parseDouble(oldValue));
                } catch (NumberFormatException ex) {
                    tooltip.setSpinnerOldValue(tooltip.getInitialSpinnerOldValue());
                }
                // TODO: баг в расположении tooltips на экране
                Point2D p = spinner.localToScene(0.0, 0.0);
                spinner.setTooltip(tooltip);
                if (!tooltip.isShowing()) {
                    tooltip.show(spinner,
                            p.getX() + spinner.getScene().getX() + spinner.getScene().getWindow().getX(),
                            p.getY() + spinner.getScene().getY() + spinner.getHeight() + spinner.getScene().getWindow().getY());
                }

                spinner.setTooltip(null);
            }
        });

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                spinner.getValueFactory().setValue(fake_value);
                spinner.getValueFactory().setValue((Double) tooltip.getInitialSpinnerOldValue());
                escapePressed = false;
            }
        });

        setupUtilityListeners(spinner, tooltip);
    }

    public static void setupSpinner(Spinner<Integer> spinner, int initValue, int fake_value, CustomTooltip tooltip) {
        StringConverter<Integer> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return converter.toString(object);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    if (string.isEmpty())
                        throw new RuntimeException("Invalid spinner value!");
                    return converter.fromString(string);
                } catch (RuntimeException ex) {
                    spinner.getValueFactory().setValue(fake_value);
                    spinner.getValueFactory().setValue(initValue);
                    return spinner.getValue();
                }
            }
        });

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!escapePressed) {
                try {
                    tooltip.setSpinnerOldValue(Integer.parseInt(oldValue));
                } catch (NumberFormatException ex) {
                    tooltip.setSpinnerOldValue(tooltip.getInitialSpinnerOldValue());
                }
                Point2D p = spinner.localToScene(0.0, 0.0);
                spinner.setTooltip(tooltip);
                if (!tooltip.isShowing()) {
                    tooltip.show(spinner,
                            p.getX() + spinner.getScene().getX() + spinner.getScene().getWindow().getX(),
                            p.getY() + spinner.getScene().getY() + spinner.getHeight() + spinner.getScene().getWindow().getY());
                }

                spinner.setTooltip(null);
            }
        });

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                spinner.getValueFactory().setValue(fake_value);
                spinner.getValueFactory().setValue((Integer) tooltip.getInitialSpinnerOldValue());
                escapePressed = false;
            }
        });

        setupUtilityListeners(spinner, tooltip);
    }

    private static void setupUtilityListeners(Spinner<? extends Number> spinner, CustomTooltip tooltip) {

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            spinner.setTooltip(tooltip);
            spinner.getTooltip().hide();
            spinner.setTooltip(null);
        });

        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && tooltip.isShowing()) {
                Event.fireEvent(spinner, new KeyEvent(null, spinner,
                        KeyEvent.ANY, "", "", KeyCode.ESCAPE,
                        false, false, false, false));
            }
        });
    }
}
