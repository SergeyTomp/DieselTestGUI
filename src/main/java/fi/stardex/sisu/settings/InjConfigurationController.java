package fi.stardex.sisu.settings;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.model.cr.InjConfigurationModel;
import fi.stardex.sisu.model.cr.InjectorTypeModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.flow.ModbusMapFlow;
import fi.stardex.sisu.states.VoltAmpereProfileDialogModel;
import fi.stardex.sisu.util.enums.InjectorChannel;
import fi.stardex.sisu.util.enums.InjectorType;
import fi.stardex.sisu.version.FirmwareVersion;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.prefs.Preferences;

import static fi.stardex.sisu.util.enums.InjectorChannel.MULTI_CHANNEL;
import static fi.stardex.sisu.util.enums.InjectorChannel.SINGLE_CHANNEL;

public class InjConfigurationController {

    @FXML
    private ComboBox<InjectorChannel> injectorsConfigComboBox;

    private InjConfigurationModel injConfigurationModel;

    private VoltAmpereProfileDialogModel voltAmpereProfileDialogModel;

    private MainSectionModel mainSectionModel;

    private Preferences rootPrefs;

    private static final String PREF_KEY = "injectorsConfigSelected";

    private InjectorTypeModel injectorTypeModel;

//    @Autowired
//    private ModbusConnect flowModbusConnect;
//
//    @Autowired
//    private RegisterProvider flowRegisterProvider;

    public void setInjConfigurationModel(InjConfigurationModel injConfigurationModel) {
        this.injConfigurationModel = injConfigurationModel;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
    }

    public void setInjectorTypeModel(InjectorTypeModel injectorTypeModel) {
        this.injectorTypeModel = injectorTypeModel;
    }

    public void setVoltAmpereProfileDialogModel(VoltAmpereProfileDialogModel voltAmpereProfileDialogModel) {
        this.voltAmpereProfileDialogModel = voltAmpereProfileDialogModel;
    }

    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }

    @PostConstruct
    public void init() {

        injectorsConfigComboBox.setItems(FXCollections.observableArrayList(SINGLE_CHANNEL, InjectorChannel.MULTI_CHANNEL));

        injConfigurationModel.injConfigurationProperty().bind(injectorsConfigComboBox.valueProperty());

        injectorsConfigComboBox.getSelectionModel().select(InjectorChannel.valueOf(rootPrefs.get(PREF_KEY, SINGLE_CHANNEL.name())));

        injectorsConfigComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> rootPrefs.put(PREF_KEY, newValue.name()));

        SINGLE_CHANNEL.setLastValue( injectorsConfigComboBox.getSelectionModel().getSelectedItem());

        MULTI_CHANNEL.setLastValue( injectorsConfigComboBox.getSelectionModel().getSelectedItem());

        injectorTypeModel.injectorTypeProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue.equals(InjectorType.PIEZO_DELPHI)) {
                selectChannelMode(SINGLE_CHANNEL);
            } else{
                restoreChannelMode();
            }
        });
        /**Old version of ChannelMode restore listener. Has a bug of incorrect return to previously selected SingleChannel mode.
         * If SINGLE_CHANNEL mode is selected as main mode and 2Coil injector selection is made after previously selected another 2Coil,
         * lastValue variable of MULTI_CHANNEL constant will be overridden by MULTI_CHANNEL value instead of SingleChannel value, written there after first 2Coil selection.
         *  Hence of this if further selection will be not 2Coil injector, channel mode will be restored to MULTI_CHANNEL but not to SINGLE_CHANNEL.
         *  Another bug was just a visual: if after first time 2Coil selected injector the next one was not 2Coil and first test in the testList of newly selected is of Measurement.NO type,
         *  channel mode restore was done only after not Measurement.NO test selection and hence user has possibility to switch additional channels in in fact SingleChannel mode.
         *  Leaved here as a reminder.*/
//        voltAmpereProfileDialogModel.isDoubleCoilProperty().addListener((observableValue, oldValue, newValue) -> {
//
//            if (newValue) {
//                selectChannelMode(MULTI_CHANNEL);
//            } else {
//                restoreChannelMode();
//            }
//        });

        /**New version of ChannelMode restore listener instead of old one above. */
        mainSectionModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue != null) {

                if (oldValue != null) {
                    if (oldValue.getVoltAmpereProfile().isDoubleCoil() && !newValue.getVoltAmpereProfile().isDoubleCoil()) {
                        restoreChannelMode();
                    } else if (!oldValue.getVoltAmpereProfile().isDoubleCoil() && newValue.getVoltAmpereProfile().isDoubleCoil()) {
                        selectChannelMode(MULTI_CHANNEL);
                    }
                }
                else {
                    if (newValue.getVoltAmpereProfile().isDoubleCoil()) {
                        selectChannelMode(MULTI_CHANNEL);
                    }
                }
            } else {
                if (oldValue.getVoltAmpereProfile().isDoubleCoil()) {
                    restoreChannelMode();
                }
            }
        });

        //Another variant of the listener above
//        mainSectionModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> {
//
//            if (newValue != null) {
//
//                if (oldValue != null && oldValue.getVoltAmpereProfile().isDoubleCoil() && newValue.getVoltAmpereProfile().isDoubleCoil()) {
//                    return;
//                }
//
//                if (newValue.getVoltAmpereProfile().isDoubleCoil()) {
//                    selectChannelMode(MULTI_CHANNEL);
//                } else if (oldValue != null && oldValue.getVoltAmpereProfile().isDoubleCoil() && !newValue.getVoltAmpereProfile().isDoubleCoil()) {
//                    restoreChannelMode();
//                }
//            } else {
//                if (oldValue.getVoltAmpereProfile().isDoubleCoil()) {
//                    restoreChannelMode();
//                }
//            }
//        });

        /** Listener below could be activated to prohibit switching of multi channel mode in case of single channel flow-meter hardware connected (MACTER, STAND_FM)
         * Do not forget to uncomment autowired ModbusConnect flowModbusConnect and RegisterProvider flowRegisterProvider in the header above*/
//        flowModbusConnect.connectedProperty().addListener((observableValue, oldValue, newValue) -> {
//
//            if (newValue) {
//                int firmwareVersionNumber = (int) flowRegisterProvider.read(ModbusMapFlow.FirmwareVersion);
//                switch (firmwareVersionNumber) {
//                    case 0xAACC:
//                    case 0xDDFF:
//                    case 0xBBCC:
//                        Platform.runLater(()->{
//                            injectorsConfigComboBox.getSelectionModel().select(SINGLE_CHANNEL);
//                            injectorsConfigComboBox.setVisible(false);
//                        });
//                        break;
//
//                    default:
//                        Platform.runLater(()-> injectorsConfigComboBox.setVisible(true));
//
//                    break;
//                }
//            }
//            else injectorsConfigComboBox.setVisible(true);
//        });
    }



    private void selectChannelMode(InjectorChannel mode) {

        InjectorChannel currentSelectedItem = injectorsConfigComboBox.getSelectionModel().getSelectedItem();
        injectorsConfigComboBox.getSelectionModel().select(mode);
        mode.setLastValue(currentSelectedItem);
        injectorsConfigComboBox.setDisable(true);
    }

    private void restoreChannelMode() {

        InjectorChannel previousSelectedItem = injectorsConfigComboBox.getSelectionModel().getSelectedItem().getLastValue();
        injectorsConfigComboBox.getSelectionModel().select(previousSelectedItem);
        injectorsConfigComboBox.setDisable(false);
    }
}
