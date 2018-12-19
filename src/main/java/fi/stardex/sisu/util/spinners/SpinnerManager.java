package fi.stardex.sisu.util.spinners;

import fi.stardex.sisu.util.InputController;
import javafx.geometry.Point2D;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.util.Random;

public class SpinnerManager {

    private static boolean escapePressed;

    private static final int MAX_LENGTH = 10;

    private static Random random = new Random();

    private static int generateRandomFakeInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static void setupSpinner(Spinner<Double> spinner, double initValue, double fake_value, Tooltip tooltip, SpinnerValueObtainer obtainer) {

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
                    spinner.getValueFactory().setValue(fake_value);
                    spinner.getValueFactory().setValue(initValue);
                    return spinner.getValue();
                }
            }
        });

        spinner.addEventHandler(KeyEvent.ANY, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                spinner.requestFocus();
                escapePressed = true;
                spinner.getValueFactory().setValue(fake_value);
                spinner.getValueFactory().setValue((Double) obtainer.getInitialSpinnerOldValue());
                escapePressed = false;
            }else if(event.getCode() == KeyCode.ENTER){
                if (spinner.getEditor().textProperty().get().isEmpty()){
                    spinner.getValueFactory().setValue((Double) obtainer.getInitialSpinnerOldValue());
                }
                obtainer.setInitialSpinnerOldValue(spinner.getValue());
            }
        });

        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue){
                obtainer.setInitialSpinnerOldValue(spinner.getValue());
                obtainer.setOldValue(spinner.getValue());
            }

            if(!newValue){

                spinner.getValueFactory().setValue(obtainer.getInitialSpinnerOldValue().doubleValue());
            }
        });

        setupUtilityListeners(spinner, tooltip, obtainer);
    }

    public static void setupSpinner(Spinner<Integer> spinner, int initValue, int minValue, int maxValue, Tooltip tooltip, SpinnerValueObtainer obtainer) {

        StringConverter<Integer> converter = spinner.getValueFactory().getConverter();

        spinner.getValueFactory().setConverter(new StringConverter<>() {

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
                    if (obtainer instanceof WidthSpinnerValueObtainer) {
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
            }else if(event.getCode() == KeyCode.ENTER){
                if (spinner.getEditor().textProperty().get().isEmpty()){
                    spinner.getValueFactory().setValue((Integer) obtainer.getInitialSpinnerOldValue());
                }
                obtainer.setInitialSpinnerOldValue(spinner.getValue());
            }
        });

        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue){
                obtainer.setInitialSpinnerOldValue(spinner.getValue());
                obtainer.setOldValue(spinner.getValue());
            }

            if(!newValue){

                spinner.getValueFactory().setValue((Integer) obtainer.getInitialSpinnerOldValue());
            }
        });

        setupUtilityListeners(spinner, tooltip, obtainer);
    }

    private static void setupUtilityListeners(Spinner<? extends Number> spinner, Tooltip tooltip, SpinnerValueObtainer obtainer) {

        obtainer.setInitialSpinnerOldValue(spinner.getValue());

        InputController.blockTextInputToSpinners(spinner, MAX_LENGTH);

        tooltip.setText("Press ENTER to commit changes");

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

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {

            spinner.setTooltip(tooltip);
            spinner.getTooltip().hide();
            spinner.setTooltip(null);
        });

        spinner.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {

            if(event.getButton() == MouseButton.PRIMARY){

                double screenX = event.getScreenX();
                double screenY = event.getScreenY();

                Point2D point2Dspinner = spinner.localToScene(0, 0);
                double xSpToScene = point2Dspinner.getX();
                double ySpToScene = point2Dspinner.getY();

                Point2D point2DTxtField = spinner.getEditor().localToScene(0,0);
                double xTfToScene = point2DTxtField.getX();

                double xSceneToAppWind = spinner.getScene().getX();
                double ySceneToAppWind = spinner.getScene().getY();

                double xAppWindToScreen = spinner.getScene().getWindow().getX();
                double yAppWindToScreen = spinner.getScene().getWindow().getY();

                double sumX = xSpToScene + xSceneToAppWind + xAppWindToScreen;
                double sumY = ySpToScene + ySceneToAppWind + yAppWindToScreen;
                
                if(xSpToScene == xTfToScene){
                    if((screenX <= sumX + spinner.getWidth() && screenX >= sumX + spinner.getEditor().getWidth())
                            && (screenY <= sumY + spinner.getHeight() && screenY >= sumY)){
                        obtainer.setInitialSpinnerOldValue(spinner.getValue());
                    }
                }else{
                    if((screenX <= sumX + spinner.getWidth() - spinner.getEditor().getWidth() && screenX >= sumX)
                            && (screenY <= sumY + spinner.getHeight() && screenY >= sumY)) {
                        obtainer.setInitialSpinnerOldValue(spinner.getValue());
                    }
                }
            }
        });
    }
}
