package fi.stardex.sisu.ui.controllers.additional.tabs;

import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.Pair;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class ConnectionController {

    private Pair<String, String> ultimaConnect = new Pair<>();
    private Pair<String, String> flowMeterConnect = new Pair<>();
    private Pair<String, String> standConnect = new Pair<>();

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


    }



}
