package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.util.Pair;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javax.annotation.PostConstruct;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

public class ConnectionController {

    @FXML private TextField ultimaIPField;

    @FXML private TextField flowMeterIPField;

    @FXML private TextField standIPField;

    @FXML private TextField ultimaPortField;

    @FXML private TextField flowMeterPortField;

    @FXML private TextField standPortField;

    @FXML private Button acceptButton;

    private Preferences prefs = Preferences.userNodeForPackage(this.getClass());

    private Pair<String, String> ultimaConnect = new Pair<>();

    private Pair<String, String> flowMeterConnect = new Pair<>();

    private Pair<String, String> standConnect = new Pair<>();

    private final String ipRegex = makePartialIPRegex();

    public TextField getStandIPField() {
        return standIPField;
    }

    public TextField getStandPortField() {
        return standPortField;
    }

    @PostConstruct
    private void init() {

        ultimaIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        flowMeterIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        standIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));

        String portRegex = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$";

        ultimaPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        flowMeterPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        standPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));

        setPairValues();

        ultimaIPField.setText(ultimaConnect.getKey());
        flowMeterIPField.setText(flowMeterConnect.getKey());
        standIPField.setText(standConnect.getKey());

        ultimaPortField.setText(ultimaConnect.getValue());
        flowMeterPortField.setText(flowMeterConnect.getValue());
        standPortField.setText(standConnect.getValue());

        acceptButton.setOnMouseClicked(event -> {

            prefs.put("UltimaIP", ultimaIPField.getText());
            prefs.put("FlowIP", flowMeterIPField.getText());
            prefs.put("StandIP", standIPField.getText());

            prefs.put("UltimaPort", ultimaPortField.getText());
            prefs.put("FlowPort", flowMeterPortField.getText());
            prefs.put("StandPort", standPortField.getText());

            setPairValues();

        });
    }

    private void setPairValues() {

        ultimaConnect.setKey(prefs.get("UltimaIP", "192.168.10.206"));
        flowMeterConnect.setKey(prefs.get("FlowIP", "192.168.10.201"));
        standConnect.setKey(prefs.get("StandIP", "192.168.10.202"));

        ultimaConnect.setValue(prefs.get("UltimaPort", "502"));
        flowMeterConnect.setValue(prefs.get("FlowPort", "502"));
        standConnect.setValue(prefs.get("StandPort", "502"));

    }

    public Pair<String, String> getUltimaConnect() {
        return ultimaConnect;
    }

    public Pair<String, String> getFlowMeterConnect() {
        return flowMeterConnect;
    }

    public Pair<String, String> getStandConnect() {
        return standConnect;
    }

    private String makePartialIPRegex() {

        String partialBlock = "(([01]?[0-9]{0,2})|(2[0-4][0-9])|(25[0-5]))" ;
        String subsequentPartialBlock = "(\\."+partialBlock+")" ;
        String ipAddress = partialBlock+"?"+subsequentPartialBlock+"{0,3}";
        return "^"+ipAddress ;

    }

    private UnaryOperator<TextFormatter.Change> getFilter(String regex) {
        return c -> {
            String text = c.getControlNewText();
            if (text.matches(regex)) {
                return c;
            } else {
                return null;
            }
        };
    }

}
