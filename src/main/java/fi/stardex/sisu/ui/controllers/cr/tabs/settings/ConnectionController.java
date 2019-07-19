package fi.stardex.sisu.ui.controllers.cr.tabs.settings;

import fi.stardex.sisu.util.Pair;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

public class ConnectionController {

    @FXML private GridPane connectionGridPane;

    @FXML private TextField ultimaIPField;

    @FXML private TextField flowMeterIPField;

    @FXML private TextField standIPField;

    @FXML private TextField ultimaPortField;

    @FXML private TextField flowMeterPortField;

    @FXML private TextField standPortField;

    @FXML private Button acceptButton;

    @FXML private Label ultimaLabel;

    @FXML private Label flowMeterLabel;

    @FXML private Label standLabel;

    @FXML private Label ipAddressLabel;

    @FXML private Label portLabel;

    private I18N i18N;

    private Preferences rootPrefs;

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

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setRootPrefs(Preferences rootPrefs) {
        this.rootPrefs = rootPrefs;
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

            rootPrefs.put("UltimaIP", ultimaIPField.getText());
            rootPrefs.put("FlowIP", flowMeterIPField.getText());
            rootPrefs.put("StandIP", standIPField.getText());

            rootPrefs.put("UltimaPort", ultimaPortField.getText());
            rootPrefs.put("FlowPort", flowMeterPortField.getText());
            rootPrefs.put("StandPort", standPortField.getText());

            setPairValues();

        });
        bindingI18N();
    }

    private void setPairValues() {

        ultimaConnect.setKey(rootPrefs.get("UltimaIP", "192.168.10.206"));
        flowMeterConnect.setKey(rootPrefs.get("FlowIP", "192.168.10.201"));
        standConnect.setKey(rootPrefs.get("StandIP", "192.168.10.202"));

        ultimaConnect.setValue(rootPrefs.get("UltimaPort", "502"));
        flowMeterConnect.setValue(rootPrefs.get("FlowPort", "502"));
        standConnect.setValue(rootPrefs.get("StandPort", "502"));

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

    private void bindingI18N() {
        ultimaLabel.textProperty().bind(i18N.createStringBinding("link.ultima.label"));
        flowMeterLabel.textProperty().bind(i18N.createStringBinding("link.flowmeter.label"));
        standLabel.textProperty().bind(i18N.createStringBinding("link.stand.label"));
        ipAddressLabel.textProperty().bind(i18N.createStringBinding("link.ipAddress.label"));
        portLabel.textProperty().bind(i18N.createStringBinding("link.port.label"));
        acceptButton.textProperty().bind(i18N.createStringBinding("link.accept.button"));
    }

}
