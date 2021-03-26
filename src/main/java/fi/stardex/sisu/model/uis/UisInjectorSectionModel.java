package fi.stardex.sisu.model.uis;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UisInjectorSectionModel {

    private ObjectProperty<List<ToggleButton>> activeLedToggleButtonsList = new SimpleObjectProperty<>(new ArrayList<>());
    private ObservableList<ToggleButton> ledToggleButtons;

    private ToggleButton ledBeaker1ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker2ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker3ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker4ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker5ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker6ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker7ToggleButton = new ToggleButton();
    private ToggleButton ledBeaker8ToggleButton = new ToggleButton();
    private IntegerProperty width_1 = new SimpleIntegerProperty();
    private IntegerProperty width_2 = new SimpleIntegerProperty();
    private IntegerProperty shift = new SimpleIntegerProperty();
    private IntegerProperty angle_1 = new SimpleIntegerProperty();
    private IntegerProperty angle_2 = new SimpleIntegerProperty();
    private BooleanProperty powerButton = new SimpleBooleanProperty();
    private IntegerProperty pressureSpinner = new SimpleIntegerProperty();
    private DoubleProperty bipValue = new SimpleDoubleProperty(0);
    private Button saveBipButton = new Button();
    private Button saveDelayButton = new Button();
    private StringProperty bipRangeLabel = new SimpleStringProperty("");
    private BooleanProperty pressureButtonProperty = new SimpleBooleanProperty();
    private IntegerProperty pressCorrectionProperty = new SimpleIntegerProperty();

    public ToggleButton getLedBeaker1ToggleButton() {
        return ledBeaker1ToggleButton;
    }
    public ToggleButton getLedBeaker2ToggleButton() {
        return ledBeaker2ToggleButton;
    }
    public ToggleButton getLedBeaker3ToggleButton() {
        return ledBeaker3ToggleButton;
    }
    public ToggleButton getLedBeaker4ToggleButton() {
        return ledBeaker4ToggleButton;
    }
    public ToggleButton getLedBeaker5ToggleButton() {
        return ledBeaker5ToggleButton;
    }
    public ToggleButton getLedBeaker6ToggleButton() {
        return ledBeaker6ToggleButton;
    }
    public ToggleButton getLedBeaker7ToggleButton() {
        return ledBeaker7ToggleButton;
    }
    public ToggleButton getLedBeaker8ToggleButton() {
        return ledBeaker8ToggleButton;
    }
    public IntegerProperty pressureSpinnerProperty() {
        return pressureSpinner;
    }
    public BooleanProperty injectorButtonProperty() {
        return powerButton;
    }
    public BooleanProperty pressureButtonProperty() {
        return pressureButtonProperty;
    }
    public IntegerProperty pressCorrectionProperty() {
        return pressCorrectionProperty;
    }

    public IntegerProperty width_1Property() {
        return width_1;
    }
    public IntegerProperty width_2Property() {
        return width_2;
    }
    public IntegerProperty shiftProperty() {
        return shift;
    }
    public IntegerProperty angle_1Property() {
        return angle_1;
    }
    public IntegerProperty angle_2Property() {
        return angle_2;
    }
    public ObjectProperty<List<ToggleButton>> activeLedToggleButtonsListProperty() {
        return activeLedToggleButtonsList;
    }
    public Button getSaveBipButton() {
        return saveBipButton;
    }
    public Button getSaveDelayButton() {
        return saveDelayButton;
    }

    public DoubleProperty bipValueProperty() {
        return bipValue;
    }
    public StringProperty bipRangeLabelProperty() {
        return bipRangeLabel;
    }

    @PostConstruct
    public void init() {

        ledToggleButtons = FXCollections.observableArrayList(new LinkedList<>());
        ledToggleButtons.add(ledBeaker1ToggleButton);
        ledToggleButtons.add(ledBeaker2ToggleButton);
        ledToggleButtons.add(ledBeaker3ToggleButton);
        ledToggleButtons.add(ledBeaker4ToggleButton);
        ledToggleButtons.add(ledBeaker5ToggleButton);
        ledToggleButtons.add(ledBeaker6ToggleButton);
        ledToggleButtons.add(ledBeaker7ToggleButton);
        ledToggleButtons.add(ledBeaker8ToggleButton);
        ledToggleButtons.forEach(tb -> tb.selectedProperty().addListener((observableValue, oldValue, newValue) -> fillArrayOfActiveLedToggleButtons(tb)));
    }

    /**method could be simply expanded to multiple ToggleButton selection variant if necessary:
     * just comment single selection variant and uncomment multiple selection variant*/
    private synchronized void fillArrayOfActiveLedToggleButtons(ToggleButton tb) {

        List<ToggleButton> toggleButtonsList = new ArrayList<>();

        /**single selection variant*/
//        toggleButtonsList.clear();
        if (tb.isSelected()) {
            toggleButtonsList.add(tb);
        }

        /**multiple selection variant*/
//        ledToggleButtons.stream().filter(ToggleButton::isSelected).forEach(toggleButtonsList::add);
//        toggleButtonsList.sort(Comparator.comparingInt(b -> Integer.parseInt(b.getText())));

        activeLedToggleButtonsList.setValue(toggleButtonsList);
    }
}
