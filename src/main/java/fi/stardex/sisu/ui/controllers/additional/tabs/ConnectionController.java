package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.Pair;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.function.UnaryOperator;

public class ConnectionController {

    private Pair<String, String> ultimaConnect = new Pair<>();
    private Pair<String, String> flowMeterConnect = new Pair<>();
    private Pair<String, String> standConnect = new Pair<>();

    private final String ipRegex = makePartialIPRegex();
    private final String portRegex = "^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$";

    @FXML
    private TextField ultimaIPField;
    @FXML
    private TextField flowMeterIPField;
    @FXML
    private TextField standIPField;
    @FXML
    private TextField ultimaPortField;
    @FXML
    private TextField flowMeterPortField;
    @FXML
    private TextField standPortField;
    @FXML
    private Button acceptButton;

    private ApplicationConfigHandler applicationConfigHandler;

    public void setApplicationConfigHandler(ApplicationConfigHandler applicationConfigHandler) {
        this.applicationConfigHandler = applicationConfigHandler;
    }

    @PostConstruct
    private void init() {

        ultimaIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        flowMeterIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));
        standIPField.setTextFormatter(new TextFormatter<>(getFilter(ipRegex)));

        ultimaPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        flowMeterPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));
        standPortField.setTextFormatter(new TextFormatter<>(getFilter(portRegex)));

        ultimaConnect.setKey(applicationConfigHandler.get("UltimaIP"));
        flowMeterConnect.setKey(applicationConfigHandler.get("FlowIP"));
        standConnect.setKey(applicationConfigHandler.get("StandIP"));

        ultimaConnect.setValue(applicationConfigHandler.get("UltimaPort"));
        flowMeterConnect.setValue(applicationConfigHandler.get("FlowPort"));
        standConnect.setValue(applicationConfigHandler.get("StandPort"));

        ultimaIPField.setText(ultimaConnect.getKey());
        flowMeterIPField.setText(flowMeterConnect.getKey());
        standIPField.setText(standConnect.getKey());

        ultimaPortField.setText(ultimaConnect.getValue());
        flowMeterPortField.setText(flowMeterConnect.getValue());
        standPortField.setText(standConnect.getValue());

        acceptButton.setOnMouseClicked(event -> {
            applicationConfigHandler.put("UltimaIP", ultimaIPField.getText());
            applicationConfigHandler.put("FlowIP", flowMeterIPField.getText());
            applicationConfigHandler.put("StandIP", standIPField.getText());

            applicationConfigHandler.put("UltimaPort", ultimaPortField.getText());
            applicationConfigHandler.put("FlowPort", flowMeterPortField.getText());
            applicationConfigHandler.put("StandPort", standPortField.getText());

            ultimaConnect.setKey(applicationConfigHandler.get("UltimaIP"));
            flowMeterConnect.setKey(applicationConfigHandler.get("FlowIP"));
            standConnect.setKey(applicationConfigHandler.get("StandIP"));

            ultimaConnect.setValue(applicationConfigHandler.get("UltimaPort"));
            flowMeterConnect.setValue(applicationConfigHandler.get("FlowPort"));
            standConnect.setValue(applicationConfigHandler.get("StandPort"));
        });
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
