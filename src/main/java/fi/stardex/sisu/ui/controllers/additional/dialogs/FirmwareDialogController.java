package fi.stardex.sisu.ui.controllers.additional.dialogs;

import fi.stardex.sisu.connect.ModbusConnect;
import fi.stardex.sisu.registers.ModbusMap;
import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.*;

import static fi.stardex.sisu.registers.ultima.ModbusMapUltima.*;
import static fi.stardex.sisu.ui.controllers.additional.dialogs.FirmwareDialogController.CpuType.*;

public class FirmwareDialogController {

    public VBox firmwareVBox;
    @FXML private Label ultimaLabel;
    @FXML private Label flowMeterLabel;
    @FXML private Label standLabel;
    @FXML private Label mainCPUVersionLabel;
    @FXML private Label MeasuringCPUVersionLabel;
    @FXML private Label powerCPUVersionLabel;
    @FXML private Label injector1CPUVersionLabel;
    @FXML private Label injector2CPUVersionLabel;
    @FXML private Label injector3CPUVersionLabel;
    @FXML private Label injector4CPUVersionLabel;
    @FXML private Label injMeasuringCPUVersionLabel;
    @FXML private Label fmStreamVersionLabel;
    @FXML private Label standFmVersionLabel;
    @FXML private Label benchVersionLabel;
    @FXML private Label benchV2VersionLabel;
    @FXML private Label fmStreamLabel;
    @FXML private Label standFmLabel;
    @FXML private Button okButton;

    private Stage windowStage;

    private I18N i18N;

    private ViewHolder firmwareDialog;

    private final StringProperty windowTitle = new SimpleStringProperty();

    private Map<CpuType, Label> versionsMap = new EnumMap<>(CpuType.class);

    private List<Label> ultimaFirmwareLabels = new ArrayList<>();

    private List<Label> flowFirmwareLabels = new ArrayList<>();

    private List<Label> standFirmwareLabels = new ArrayList<>();

    private RegisterProvider ultimaRegisterProvider;

    private ModbusConnect ultimaModbusConnect;

    private ModbusConnect flowModbusConnect;

    private ModbusConnect standModbusConnect;

    public void setUltimaRegisterProvider(RegisterProvider ultimaRegisterProvider) {
        this.ultimaRegisterProvider = ultimaRegisterProvider;
    }

    public void setUltimaModbusConnect(ModbusConnect ultimaModbusConnect) {
        this.ultimaModbusConnect = ultimaModbusConnect;
    }

    public void setFlowModbusConnect(ModbusConnect flowModbusConnect) {
        this.flowModbusConnect = flowModbusConnect;
    }

    public void setStandModbusConnect(ModbusConnect standModbusConnect) {
        this.standModbusConnect = standModbusConnect;
    }

    public void setFirmwareDialog(ViewHolder firmwareDialog) {
        this.firmwareDialog = firmwareDialog;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    enum CpuType{

        Main(new ArrayList<>(Arrays.asList(Main_version_0, Main_version_1))) {String makeString(List<String> parts){return standardString(parts);}},

        Measure(new ArrayList<>(Arrays.asList(MeasureCPU_version_0, MeasureCPU_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        Power(new ArrayList<>(Arrays.asList(PowerCPU_version_0, PowerCPU_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        Injector_1(new ArrayList<>(Arrays.asList(InjectorCPU_1_version_0, InjectorCPU_1_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        Injector_2(new ArrayList<>(Arrays.asList(InjectorCPU_2_version_0, InjectorCPU_2_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        Injector_3(new ArrayList<>(Arrays.asList(InjectorCPU_3_version_0, InjectorCPU_3_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        Injector_4(new ArrayList<>(Arrays.asList(InjectorCPU_4_version_0, InjectorCPU_4_version_1))){String makeString(List<String> parts){return standardString(parts);}},

        RLC_Measure(new ArrayList<>(Arrays.asList(RLC_MeasureCPU_version_0, RLC_MeasureCPU_version_1))){String makeString(List<String> parts){return standardString(parts);}};

        private List<ModbusMap> registersList;
        private StringBuilder sb = new StringBuilder();

        CpuType(List<ModbusMap> registersList) {
            this.registersList = registersList;
        }

        public List<ModbusMap> getRegistersList() {
            return registersList;
        }

        abstract String makeString(List<String> parts);

        String standardString(List<String> parts) {
            sb.setLength(0);
            parts.forEach(part -> sb.append(part).append("."));
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
    }


    @PostConstruct
    public void init(){

        versionsMap.put(Main, mainCPUVersionLabel);
        versionsMap.put(Measure, MeasuringCPUVersionLabel);
        versionsMap.put(Power, powerCPUVersionLabel);
        versionsMap.put(Injector_1, injector1CPUVersionLabel);
        versionsMap.put(Injector_2, injector2CPUVersionLabel);
        versionsMap.put(Injector_3, injector3CPUVersionLabel);
        versionsMap.put(Injector_4, injector4CPUVersionLabel);
        versionsMap.put(RLC_Measure, injMeasuringCPUVersionLabel);

        ultimaFirmwareLabels.add(mainCPUVersionLabel);
        ultimaFirmwareLabels.add(MeasuringCPUVersionLabel);
        ultimaFirmwareLabels.add(powerCPUVersionLabel);
        ultimaFirmwareLabels.add(injector1CPUVersionLabel);
        ultimaFirmwareLabels.add(injector2CPUVersionLabel);
        ultimaFirmwareLabels.add(injector3CPUVersionLabel);
        ultimaFirmwareLabels.add(injector4CPUVersionLabel);
        ultimaFirmwareLabels.add(injMeasuringCPUVersionLabel);

        flowFirmwareLabels.add(fmStreamVersionLabel);
        flowFirmwareLabels.add(standFmVersionLabel);

        standFirmwareLabels.add(benchVersionLabel);
        standFirmwareLabels.add(benchV2VersionLabel);

        okButton.setOnAction(actionEvent -> windowStage.close());

        setupFirmwareVersionListener();

        bindingI18N();

        getVersions();
    }

    public void showInfo() {

        if (windowStage == null) {
            windowStage = new Stage();
            windowStage.setTitle(windowTitle.get());
            windowStage.setScene(new Scene(firmwareDialog.getView()));
            windowStage.setResizable(false);
            windowStage.initModality(Modality.APPLICATION_MODAL);
            windowStage.initStyle(StageStyle.UTILITY);
            windowStage.titleProperty().bind(i18N.createStringBinding("settings.firmware.Button"));
        }
        getVersions();
        windowStage.show();
    }

    public void getVersions() {

        if (!ultimaModbusConnect.connectedProperty().get()
                && !flowModbusConnect.connectedProperty().get()
                && !standModbusConnect.connectedProperty().get()) {

            clearLabels(ultimaFirmwareLabels);
            clearLabels(flowFirmwareLabels);
            clearLabels(standFirmwareLabels);
            return;
        }

        if (flowModbusConnect.connectedProperty().get()) {

            //place flowFirmwareVersion reading code here
            // Platform.runLater(() -> label.setText(version));
        }else{ clearLabels(flowFirmwareLabels); }

        if (standModbusConnect.connectedProperty().get()) {

            //place standFirmwareVersion reading code here
            //Platform.runLater(() -> label.setText(version));
        }else{ clearLabels(standFirmwareLabels); }

        if (ultimaModbusConnect.connectedProperty().get()) {

            ultimaRegisterProvider.read(Version_controllable);
            Object lastValue;
            if ((lastValue = Version_controllable.getLastValue()) != null && (int)lastValue == 0xAA) {

                List<String> parts = new ArrayList<>();
                versionsMap.forEach((cpuType, label) -> {

                    parts.clear();
                    cpuType.getRegistersList().forEach(reg -> {

                        String part;
                        ultimaRegisterProvider.read(reg);
                        parts.add((part = reg.getLastValue().toString()) != null ? part : "-");
                        String version = cpuType.makeString(parts);
                        Platform.runLater(() -> label.setText(version));
                    });
                });
            }
        }else { clearLabels(ultimaFirmwareLabels); }
    }

    private void clearLabels(List<Label> labels) {
        Platform.runLater(()-> labels.forEach(l -> l.setText("-")));
    }

    private void bindingI18N() {

        ultimaLabel.textProperty().bind(i18N.createStringBinding("link.ultima.label"));
        flowMeterLabel.textProperty().bind(i18N.createStringBinding("link.flowmeter.label"));
        standLabel.textProperty().bind(i18N.createStringBinding("link.stand.label"));
    }

    private void setupFirmwareVersionListener() {

        ultimaModbusConnect.connectedProperty().addListener((observable, oldValue, newValue) -> getVersions());
        flowModbusConnect.connectedProperty().addListener((observable, oldValue, newValue) -> getVersions());
        standModbusConnect.connectedProperty().addListener((observable, oldValue, newValue) -> getVersions());
    }
}
