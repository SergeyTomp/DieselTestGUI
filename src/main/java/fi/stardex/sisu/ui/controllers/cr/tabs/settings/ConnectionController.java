package fi.stardex.sisu.ui.controllers.cr.tabs.settings;

import fi.stardex.sisu.ui.ViewHolder;
import fi.stardex.sisu.util.Pair;
import fi.stardex.sisu.util.enums.Theme;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.prefs.Preferences;

public class ConnectionController {

    @FXML private Label themeLabel;

    @FXML private ComboBox<Theme> themeComboBox;

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

    private List<ViewHolder> viewHoldersList = new ArrayList<>();

    private ViewHolder rootLayout;
    private ViewHolder printDialogPanel;
    private ViewHolder firmwareDialog;

    private Preferences rootPrefs;

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

    public void setRootLayout(ViewHolder rootLayout) {
        this.rootLayout = rootLayout;
        viewHoldersList.add(rootLayout);
    }

    public void setFirmwareDialog(ViewHolder firmwareDialog) {
        this.firmwareDialog = firmwareDialog;
        viewHoldersList.add(firmwareDialog);
    }

    public void setPrintDialogPanel(ViewHolder printDialogPanel) {
        this.printDialogPanel = printDialogPanel;
        viewHoldersList.add(printDialogPanel);
    }

    public void setNewEditTestDialog(ViewHolder newEditTestDialog) {
        viewHoldersList.add(newEditTestDialog);
    }

    public void setNewEditInjectorDialog(ViewHolder newEditInjectorDialog) {
        viewHoldersList.add(newEditInjectorDialog);
    }

    public void setManufacturerMenuDialog(ViewHolder manufacturerMenuDialog) {
        viewHoldersList.add(manufacturerMenuDialog);
    }

    public void setVoltAmpereProfileDialog(ViewHolder voltAmpereProfileDialog) {
        viewHoldersList.add(voltAmpereProfileDialog);
    }

    public void setCustomProducerPumpDialog(ViewHolder customProducerPumpDialog) {
        viewHoldersList.add(customProducerPumpDialog);
    }

    public void setCustomPumpDialog(ViewHolder customPumpDialog) {
        viewHoldersList.add(customPumpDialog);
    }

    public void setCustomPumpTestDialog(ViewHolder customPumpTestDialog) {
        viewHoldersList.add(customPumpTestDialog);
    }

    public void setCustomManufacturerUisDialog(ViewHolder customManufacturerUisDialog) {
        viewHoldersList.add(customManufacturerUisDialog);
    }

    public void setCustomInjectorUisDialog(ViewHolder customInjectorUisDialog) {
        viewHoldersList.add(customInjectorUisDialog);
    }

    public void setCustomTestUisDialog(ViewHolder customTestUisDialog) {
        viewHoldersList.add(customTestUisDialog);
    }

    public void setCustomVapUisDialog(ViewHolder customVapUisDialog) {
        viewHoldersList.add(customVapUisDialog);
    }

    public void setUisVap(ViewHolder uisVap) {
        viewHoldersList.add(uisVap);
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
        /**TODO: удалить 3 строки ниже после подключения функционала управления стилями и раскомментировать setThemeManager()*/
        rootLayout.getView().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
        printDialogPanel.getView().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
        firmwareDialog.getView().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
//        setThemeManager();
    }

    private void setPairValues() {

        ultimaConnect.setKey(rootPrefs.get("UltimaIP", "192.168.10.200"));
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

    private void setThemeManager() {

        themeComboBox.setButtonCell(new ListCell<>() {
            @Override
            public void updateItem(Theme item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.name());
                    setAlignment(Pos.CENTER);
                }
            }
        });

        themeComboBox.getItems().addAll(Theme.values());
        themeComboBox.getItems().sort(Comparator.comparingInt(Theme::getOrder));
        themeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setTheme(getClass().getResource(("/css/" + newValue.getFile()).intern()).toExternalForm());
            rootPrefs.put("theme", newValue.name());
        });

        Theme currentTheme = Theme.valueOf(rootPrefs.get("theme", "STANDARD"));
        if (themeComboBox.getItems().contains(currentTheme)) {
            themeComboBox.getSelectionModel().select(currentTheme);
        } else {
            themeComboBox.getSelectionModel().select(Theme.STANDARD);
        }

        themeComboBox.setVisible(true);
        themeLabel.setVisible(true);
    }

    private void setTheme(String theme) {

        viewHoldersList.forEach(vh -> {
            vh.getView().getStylesheets().clear();
            vh.getView().getStylesheets().add(theme);
        });
    }

    private void bindingI18N() {
        ultimaLabel.textProperty().bind(i18N.createStringBinding("link.ultima.label"));
        flowMeterLabel.textProperty().bind(i18N.createStringBinding("link.flowmeter.label"));
        standLabel.textProperty().bind(i18N.createStringBinding("link.stand.label"));
        ipAddressLabel.textProperty().bind(i18N.createStringBinding("link.ipAddress.label"));
        portLabel.textProperty().bind(i18N.createStringBinding("link.port.label"));
        acceptButton.textProperty().bind(i18N.createStringBinding("link.accept.button"));
        themeLabel.textProperty().bind(i18N.createStringBinding("link.theme.label"));
    }
}
