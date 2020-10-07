package fi.stardex.sisu.util.wrappers;

import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.version.FirmwareVersion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Iterator;

import static fi.stardex.sisu.version.FlowFirmwareVersion.FlowVersions;
import static fi.stardex.sisu.version.StandFirmwareVersion.StandVersions;

public class StatusBarWrapper {

    private static final String TEXT_FIELD_COLOR = "#ddff99";
    private static final String STYLE = "-fx-background-color: #15151e;-fx-border-width: 3px; -fx-border-color: #000000;";

    private final Devices devices;
    private final StackPane statusBar;
    private final Text textfield1;
    private final Text textfield2;
    private final Text textfield3;

    private final FirmwareVersion<FlowVersions> flowFirmwareVersion;
    private final FirmwareVersion<StandVersions> standFirmwareVersion;

    public StatusBarWrapper(Devices devices, String processStatus, String deviceConnected, String statusData,
                            FirmwareVersion<FlowVersions> flowFirmwareVersion, FirmwareVersion<StandVersions> standFirmwareVersion) {
        this.devices = devices;
        this.flowFirmwareVersion = flowFirmwareVersion;
        this.standFirmwareVersion = standFirmwareVersion;
        this.statusBar = new StackPane();
        this.statusBar.setPrefSize(400, 20);
        this.statusBar.setStyle(STYLE);
        this.textfield1 = new Text();
        this.textfield2 = new Text();
        this.textfield3 = new Text();
        this.textfield1.setFill(Color.web(TEXT_FIELD_COLOR));
        this.textfield2.setFill(Color.web(TEXT_FIELD_COLOR));
        this.textfield3.setFill(Color.web(TEXT_FIELD_COLOR));
        this.textfield1.setOpacity(0.8);
        this.textfield2.setOpacity(0.8);
        this.textfield3.setOpacity(0.8);
        this.textfield1.setText("Process Status: " + processStatus);
        this.textfield2.setText("  Device status: " + deviceConnected);
        this.textfield2.setFill(Color.RED);
        this.textfield3.setText("  version: " + statusData);
        this.statusBar.getChildren().add(textfield1);
        this.statusBar.getChildren().add(textfield2);
        this.statusBar.getChildren().add(textfield3);
        StackPane.setAlignment(textfield1, Pos.TOP_LEFT);
        StackPane.setAlignment(textfield2, Pos.TOP_CENTER);
        StackPane.setAlignment(textfield3, Pos.TOP_RIGHT);
        StackPane.setMargin(textfield3, new Insets(0, 20, 0, 0));
    }

    public void setProcessStatus(String processStatus) {
        this.textfield1.setText("Process Status: " + processStatus);
    }

    public void setProcessStatus(String processStatus, Color color) {
        textfield1.setText(processStatus);
        textfield1.setFill(color);
    }

    public StackPane getStatusBar() {
        return statusBar;
    }

    public void refresh() {
        if (devices.isNoDeviceConnected()) {
            textfield2.setFill(Color.RED);
            textfield2.setText("Device status: no connected devices");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("Device connected: ");
        Iterator<Device> iterator = devices.connectedDevices();
        while (iterator.hasNext()) {
            Device device = iterator.next();
            switch (device) {
                case ULTIMA:
                    doIfDeviceIsUltima(stringBuilder, device);
                    break;
                case MODBUS_FLOW:
                    doIfDeviceIsFlow(stringBuilder, device);
                    break;
                case MODBUS_STAND:
                    doIfDeviceIsStand(stringBuilder, device);
                    break;
                default:
                    break;
            }
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        textfield2.setFill(Color.GREEN);
        textfield2.setText(stringBuilder.toString());
    }

    private void doIfDeviceIsUltima(StringBuilder sb, Device device) {

        sb.append(device.getLabel());

    }

    private void doIfDeviceIsFlow(StringBuilder sb, Device device) {

        FlowVersions version = flowFirmwareVersion.getVersions();

        switch (version) {
            case MASTER:
                sb.append(device.getLabel()).append(" CH04");
                break;
            case MASTER_DF:
                sb.append(device.getLabel()).append(" CH04 DF");
                break;
            case STREAM:
                sb.append(device.getLabel()).append(" CH10");
                break;
            case STAND_FM:
                sb.append("Stand FM");
                break;
            case STAND_FM_4_CH:
                sb.append("Stand FM 4 CH");
                break;
            case PUMP_METER:
                sb.append(device.getLabel()).append(" PUMP");
                break;
        }

    }

    private void doIfDeviceIsStand(StringBuilder sb, Device device) {

        StandVersions version = standFirmwareVersion.getVersions();
        switch (version) {
            case STAND:
                sb.append(device.getLabel());
                break;
            case STAND_FORTE:
                sb.append(version.name());
                break;
            case UNKNOWN:
                sb.append("Unknown Device");
                break;
        }

    }
}
