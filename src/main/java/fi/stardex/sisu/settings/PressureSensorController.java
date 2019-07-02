package fi.stardex.sisu.settings;

import fi.stardex.sisu.model.PressureSensorModel;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Region;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

public class PressureSensorController {

    @FXML
    private ComboBox <Integer> pressureSensorComboBox;

    private PressureSensorModel pressureSensorModel;

    private Preferences rootPrefs;

    private Alert alert;

    private StringProperty alertString = new SimpleStringProperty();

    private StringProperty yesButton = new SimpleStringProperty();

    private StringProperty noButton = new SimpleStringProperty();

    private static final String PREF_KEY = "pressureSensorSelected";

    private I18N i18N;

    private boolean alertProcessing;

    public void setPressureSensorModel(PressureSensorModel pressureSensorModel) {
        this.pressureSensorModel = pressureSensorModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init(){

        pressureSensorComboBox.setItems(FXCollections.observableArrayList(1500, 1800, 2000, 2200, 2400, 2700));
        pressureSensorModel.pressureSensorProperty().bind(pressureSensorComboBox.valueProperty());
        pressureSensorComboBox.getSelectionModel().select(Integer.valueOf(rootPrefs.getInt(PREF_KEY, 2000)));
        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.putInt(PREF_KEY, newValue));
        bindingI18N();
        setupSensorChangeListener();
    }


    private void setupSensorChangeListener() {

        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(alertProcessing) return;

            showAlert();
            if (alert.getResult() != ButtonType.YES) {

                alertProcessing = true;
                pressureSensorComboBox.getSelectionModel().select(oldValue);
                alertProcessing = false;
                return;
            }
        });
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES, ButtonType.NO);
            alert.initStyle(StageStyle.DECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).setDefaultButton(false);
            ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).setDefaultButton(true);
        }
        ((Button)alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        ((Button)alert.getDialogPane().lookupButton(ButtonType.NO)).textProperty().setValue(noButton.get());
        alert.setContentText(alertString.get());
        System.err.println(alert.getDialogPane().getHeight());
        alert.showAndWait();
    }

    private void bindingI18N() {

        alertString.bind(i18N.createStringBinding("alert.pressureSensorChange"));
        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        noButton.bind((i18N.createStringBinding("alert.noButton")));
    }
}
