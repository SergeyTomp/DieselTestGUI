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

    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;

    @PostConstruct
    private void init() {

        final UnaryOperator<TextFormatter.Change> ipAddressFilter = c -> {
            String text = c.getControlNewText();
            if  (text.matches(ipRegex)) {
                return c ;
            } else {
                return null ;
            }
        };

        ultimaIPField.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        flowMeterIPField.setTextFormatter(new TextFormatter<>(ipAddressFilter));
        standIPField.setTextFormatter(new TextFormatter<>(ipAddressFilter));

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

            ultimaConnect.setKey(applicationConfigHandler.get("UltimaIP"));

            applicationConfigHandler.put("UltimaPort", ultimaPortField.getText());
            applicationConfigHandler.put("FlowPort", flowMeterPortField.getText());
            applicationConfigHandler.put("StandPort", standPortField.getText());
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
}
