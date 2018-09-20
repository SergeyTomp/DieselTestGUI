package fi.stardex.sisu.util.spinners;

import fi.stardex.sisu.util.InputController;
import fi.stardex.sisu.util.tooltips.CustomTooltip;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.Random;

public class SpinnerManager {

    private static boolean escapePressed;

    private static final int MAX_LENGTH = 10;

    private static Random random = new Random();

    private static int generateRandomFakeInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static void setupSpinner(Spinner<Double> spinner, double initValue, double fake_value, CustomTooltip tooltip, SpinnerValueObtainer obtainer) {


        InputController.blockTextInputToSpinners(spinner, MAX_LENGTH);

        StringConverter<Double> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<Double>() {
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
                    spinner.getValueFactory().setValue(fake_value);
                    spinner.getValueFactory().setValue(initValue);
                    return spinner.getValue();
                }
            }
        });

        tooltip.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                obtainer.setInitialSpinnerOldValue(obtainer.getOldValue());
        });

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!escapePressed) {
                try {
                    obtainer.setOldValue(Double.parseDouble(oldValue));
                } catch (NumberFormatException ex) {
                    obtainer.setOldValue(obtainer.getInitialSpinnerOldValue());
                }
                // TODO: баг в расположении tooltips на экране, иногда в volt ampere dialog появляются относительно главной scene
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

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                spinner.getValueFactory().setValue(fake_value);
                spinner.getValueFactory().setValue((Double) obtainer.getInitialSpinnerOldValue());
                escapePressed = false;
            }
        });

        setupUtilityListeners(spinner, tooltip);
    }

    public static void setupSpinner(Spinner<Integer> spinner, int initValue, int minValue, int maxValue, CustomTooltip tooltip, SpinnerValueObtainer obtainer) {

        InputController.blockTextInputToSpinners(spinner, MAX_LENGTH);

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
                    if(obtainer instanceof WidthSpinnerValueObtainer) {
                        ((WidthSpinnerValueObtainer) obtainer).setGeneratedFakeValue(generateRandomFakeInt(minValue, maxValue));
                        spinner.getValueFactory().setValue(((WidthSpinnerValueObtainer) obtainer).getGeneratedFakeValue());
                    } else {
                        spinner.getValueFactory().setValue(generateRandomFakeInt(minValue, maxValue));
                    }
                    spinner.getValueFactory().setValue(initValue);
                    return spinner.getValue();
                }
            }
        });

        tooltip.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue)
                obtainer.setInitialSpinnerOldValue(obtainer.getOldValue());
        });

        spinner.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!escapePressed) {
                try {
                    obtainer.setOldValue(Integer.parseInt(oldValue));
                } catch (NumberFormatException ex) {
                    obtainer.setOldValue(obtainer.getInitialSpinnerOldValue());
                }
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

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                if(obtainer instanceof WidthSpinnerValueObtainer) {
                    ((WidthSpinnerValueObtainer) obtainer).setGeneratedFakeValue(generateRandomFakeInt(minValue, maxValue));
                    spinner.getValueFactory().setValue(((WidthSpinnerValueObtainer) obtainer).getGeneratedFakeValue());
                } else {
                    spinner.getValueFactory().setValue(generateRandomFakeInt(minValue, maxValue));
                }
                spinner.getValueFactory().setValue((Integer) obtainer.getInitialSpinnerOldValue());
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
