package fi.stardex.sisu.util.wrappers;

import fi.stardex.sisu.devices.Device;
import fi.stardex.sisu.devices.Devices;
import fi.stardex.sisu.version.FlowFirmwareVersion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Iterator;

public class StatusBarWrapper {
    private static final String TEXT_FIELD_COLOR = "#ddff99";
    private static final String STYLE = "-fx-background-color: #15151e;-fx-border-width: 3px; -fx-border-color: #000000;";

    private final Devices devices;
    private final StackPane statusBar;
    private final Text textfield1;
    private final Text textfield2;
    private final Text textfield3;

    public StatusBarWrapper(Devices devices, String processStatus, String deviceConnected, String statusData) {
        this.devices = devices;
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
        this.textfield3.setText("  Version: " + statusData);
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
            stringBuilder.append(device.getLabel());
            if (device == Device.MODBUS_FLOW)
                doIfDeviceIsFlow(stringBuilder);
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        textfield2.setFill(Color.GREEN);
        textfield2.setText(stringBuilder.toString());
    }

    private void doIfDeviceIsFlow(StringBuilder sb) {
        FlowFirmwareVersion version = FlowFirmwareVersion.getFlowFirmwareVersion();
        if (version == FlowFirmwareVersion.FLOW_MASTER)
            sb.append(" CH04");
        else if (version == FlowFirmwareVersion.FLOW_STREAM)
            sb.append(" CH10");
    }
}
