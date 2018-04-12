package fi.stardex.sisu.ui.controllers.main;

import fi.stardex.sisu.registers.RegisterProvider;
import fi.stardex.sisu.registers.modbusmaps.ModbusMapUltima;
import fi.stardex.sisu.registers.writers.ModbusWriter;
import fi.stardex.sisu.util.ApplicationConfigHandler;
import fi.stardex.sisu.util.view.ApplicationAppearanceChanger;
import fi.stardex.sisu.util.view.GUIType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainSectionController {

    //adasd

    @Autowired
    private ModbusWriter ultimaModbusWriter;
    @Autowired
    private RegisterProvider ultimaRegisterProvider;

    public TextField TF;
    public Button writeBtn;
    public Button ReadBtn;

    //asdasd


    private List<String> versions = new LinkedList<>();

    {
        versions.add("CR");
        versions.add("UIS");
    }

    @FXML
    private ComboBox<String> versionComboBox;
    @FXML
    private RadioButton pumpRB;
    @FXML
    private RadioButton injRB;
    @FXML
    private ToggleGroup injectorOrPump;

    @Autowired
    private ApplicationConfigHandler applicationConfigHandler;
    @Autowired
    private ApplicationAppearanceChanger applicationAppearanceChanger;

    @PostConstruct
    private void init() {
        versionComboBox.getItems().addAll(versions);

        versionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                return;

            switch (newValue) {
                case "UIS":
                    unselectAll();
                    changeToUIS();
                    break;
                case "CR":
                    unselectAll();
                    changeToCR();
                    break;
            }
        });

        injectorOrPump.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (versionComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("CR")) {
                if (injectorOrPump.getSelectedToggle() == injRB) {
                    GUIType.setCurrentType(GUIType.CR_Inj);
                    applicationConfigHandler.put("GUI_Type", "CR_Inj");
                    applicationAppearanceChanger.changeToCRInj();
                } else if (injectorOrPump.getSelectedToggle() == pumpRB) {
                    GUIType.setCurrentType(GUIType.CR_Pump);
                    applicationConfigHandler.put("GUI_Type", "CR_Pump");
                    applicationAppearanceChanger.changeToCRPump();
                }
            }
        });

        switch (GUIType.getByString(applicationConfigHandler.get("GUI_Type"))) {
            case UIS:
                versionComboBox.getSelectionModel().select("UIS");
                break;
            case CR_Inj:
                versionComboBox.getSelectionModel().select("CR");
                break;
            case CR_Pump:
                versionComboBox.getSelectionModel().select("CR");
        }


        //asdasd
        writeBtn.setOnMouseClicked(event -> {
            ultimaModbusWriter.add(ModbusMapUltima.BoostVoltage, Float.valueOf(TF.getText()));
            ultimaModbusWriter.execute(1, TimeUnit.MILLISECONDS);
        });

        ReadBtn.setOnMouseClicked(event -> {
            System.err.println(ultimaRegisterProvider.read(ModbusMapUltima.BoostVoltage));
        });

        //asdasd
    }

    private void changeToUIS() {
        GUIType.setCurrentType(GUIType.UIS);
        applicationConfigHandler.put("GUI_Type", "UIS");
        applicationAppearanceChanger.changeToUIS();
        injRB.setSelected(true);
        pumpRB.setDisable(true);
    }

    private void changeToCR() {
        injRB.setSelected(true);
        pumpRB.setDisable(false);
    }

    private void unselectAll() {
        injRB.setSelected(false);
        pumpRB.setSelected(false);
    }
}
