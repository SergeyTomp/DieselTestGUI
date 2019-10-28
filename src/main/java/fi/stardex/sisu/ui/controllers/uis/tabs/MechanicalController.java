package fi.stardex.sisu.ui.controllers.uis.tabs;

import eu.hansolo.enzo.lcd.Lcd;
import fi.stardex.sisu.model.updateModels.UisHardwareUpdateModel;
import fi.stardex.sisu.registers.writers.ModbusRegisterProcessor;
import fi.stardex.sisu.util.GaugeCreator;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.OpeningPressureReset;

public class MechanicalController {

    @FXML private Label realPressLabel;
    @FXML private Label openingPressLabel;
    @FXML private StackPane currentPressureStackPane;
    @FXML private StackPane openingPressureStackPane;
    @FXML private Button resetOpeningPressureButton;

    private Lcd currentPressureLCD;
    private Lcd openingPressureLCD;

    private ModbusRegisterProcessor ultimaModbusWriter;
    private UisHardwareUpdateModel uisHardwareUpdateModel;
    private I18N i18N;

    public void setUltimaModbusWriter(ModbusRegisterProcessor ultimaModbusWriter) {
        this.ultimaModbusWriter = ultimaModbusWriter;
    }
    public void setUisHardwareUpdateModel(UisHardwareUpdateModel uisHardwareUpdateModel) {
        this.uisHardwareUpdateModel = uisHardwareUpdateModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    public void init() {

        currentPressureLCD = GaugeCreator.createLcd("bar");
        openingPressureLCD = GaugeCreator.createLcd("bar");
        currentPressureStackPane.getChildren().add(currentPressureLCD);
        openingPressureStackPane.getChildren().add(openingPressureLCD);
        resetOpeningPressureButton.setOnAction(event -> ultimaModbusWriter.add(OpeningPressureReset, true));
        uisHardwareUpdateModel.lcdPressureProperty().addListener((observableValue, oldValue, newValue) -> currentPressureLCD.setValue(newValue.doubleValue()));
        uisHardwareUpdateModel.maxLcdPressureProperty().addListener((observableValue, oldValue, newValue) -> openingPressureLCD.setValue(newValue.doubleValue()));
        bindingI18N();
    }

    private void bindingI18N() {
        realPressLabel.textProperty().bind(i18N.createStringBinding("additional.mechanical.currentPress"));
        openingPressLabel.textProperty().bind(i18N.createStringBinding("additional.mechanical.openingPress"));
    }
}
