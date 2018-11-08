package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javax.annotation.PostConstruct;

public class SettingsControllerNew {

    private I18N i18N;
    @FXML private Label regulatorsConfigLabel;
    @FXML private Label injectorsConfigLabel;
    @FXML private Label languagesLabel;
    @FXML private Label flowOutputDimensionLabel;
    @FXML private Label pressureSensorLabel;

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {
        pressureSensorLabel.textProperty().bind(i18N.createStringBinding("settings.pressureSensor.Label"));
        regulatorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.regulatorsConfig.ComboBox"));
        injectorsConfigLabel.textProperty().bind(i18N.createStringBinding("settings.injectorsConfig.ComboBox"));
        languagesLabel.textProperty().bind(i18N.createStringBinding("settings.languages.ComboBox"));
        flowOutputDimensionLabel.textProperty().bind(i18N.createStringBinding("settings.FlowOutputDimension.ComboBox"));
//        setupSettingsControls();
    }

//    private void setupSettingsControls(){
//        setupPressureSensor();

//        configRegulatorsInvolved(Integer.parseInt(regulatorsConfigComboBox.valueProperty().getValue()));
//        regulatorsConfigComboBox.valueProperty().addListener(new RegulatorsConfigListener());
//    }

//    private void setupPressureSensor() {
//
//        Integer maxPressure = pressureSensorComboBox.getSelectionModel().selectedItemProperty().getValue();
//        pressMultiplierProperty.setValue(maxPressure);
//        highPressureSectionController.getPressReg1Spinner().setValueFactory(
//                new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, maxPressure, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
//
//        pressureSensorComboBox.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
//            pressMultiplierProperty.setValue(newValue);
//            highPressureSectionController.getPressReg1Spinner().setValueFactory(
//                    new SpinnerValueFactory.IntegerSpinnerValueFactory(PRESS_REG_1_SPINNER_MIN, newValue, PRESS_REG_1_SPINNER_INIT, PRESS_REG_1_SPINNER_STEP));
//        }));
//    }

//    private class RegulatorsConfigListener implements ChangeListener<String> {
//        @Override
//        public void changed(ObservableValue<? extends String> observable, String  oldValue, String  newValue) {
//            switch (Integer.parseInt(newValue)){
//                case 1:
//                    configRegulatorsInvolved(1);
//                    break;
//                case 2:
//                    configRegulatorsInvolved(2);
//                    break;
//                case 3:
//                    configRegulatorsInvolved(3);
//                    break;
//                default:
//                    configRegulatorsInvolved(3);
//                    break;
//            }
//        }
//    }

//    private void configRegulatorsInvolved(int number) {
//        switch (number){
//            case 1:
//                highPressureSectionController.setVisibleRegulator2(false);
//                highPressureSectionController.setVisibleRegulator3(false);
//                break;
//            case 2:
//                highPressureSectionController.setVisibleRegulator2(true);
//                highPressureSectionController.setVisibleRegulator3(false);
//                break;
//            case 3:
//                highPressureSectionController.setVisibleRegulator2(true);
//                highPressureSectionController.setVisibleRegulator3(true);
//                break;
//            default:
//                highPressureSectionController.setVisibleRegulator2(true);
//                highPressureSectionController.setVisibleRegulator3(true);
//                break;
//        }
//    }
}
