package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.enums.TestSpeed;
import fi.stardex.sisu.util.enums.Tests;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

public class MainSectionUisModel {

    // manufacturer listView part
    private final ObservableList<Producer> producerObservableList = FXCollections.observableArrayList();
    private final ObjectProperty<Producer> producerProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customProducerOperation = new SimpleObjectProperty<>();

    // modelProperty listView part
    private ObjectProperty<Model> modelProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customModelOperation = new SimpleObjectProperty<>();
    private boolean modelIsChanging;

    // test listView part
    private final ObjectProperty<Test> testProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customTestOperation = new SimpleObjectProperty<>();
    private ObservableList<Test> testObservableList = FXCollections.observableArrayList();
    private boolean testIsChanging;

    // test mode checkButtons part
    private ObjectProperty<Tests.TestType> testType = new SimpleObjectProperty<>(Tests.TestType.AUTO);

    // start button part
    private BooleanProperty startButton = new SimpleBooleanProperty();

    // adjustment/measuring timers part
    private BooleanProperty measurementTimeEnabled = new SimpleBooleanProperty();
    private ObjectProperty<TestSpeed> multiplierProperty = new SimpleObjectProperty<>();

    // reset/store/print button part
    private Button storeButton = new Button();

    public ObservableList<Producer> getProducerObservableList() {
        return producerObservableList;
    }
    public ObjectProperty<Producer> manufacturerObjectProperty() {
        return producerProperty;
    }
    public ObjectProperty<Operation> customProducerOperationProperty() {
        return customProducerOperation;
    }
    public ObjectProperty<Operation> customModelOperationProperty() {
        return customModelOperation;
    }
    public ObjectProperty<Operation> customTestOperationProperty() {
        return customTestOperation;
    }
    public BooleanProperty startButtonProperty() {
        return startButton;
    }
    public ObjectProperty<Tests.TestType> testTypeProperty() {
        return testType;
    }
    public ObjectProperty<Model> modelProperty() {
        return modelProperty;
    }
    public ObjectProperty<Test> injectorTestProperty() {
        return testProperty;
    }
    public boolean isModelIsChanging() {
        return modelIsChanging;
    }
    public boolean isTestIsChanging() {
        return testIsChanging;
    }
    public Button getStoreButton() {
        return storeButton;
    }
    public ObservableList<Test> getTestObservableList() {
        return testObservableList;
    }
    public ObjectProperty<TestSpeed> multiplierProperty() {
        return multiplierProperty;
    }
    public BooleanProperty measurementTimeEnabledProperty() {
        return measurementTimeEnabled;
    }

    public void setModelIsChanging(boolean modelIsChanging) {
        this.modelIsChanging = modelIsChanging;
    }
    public void setTestIsChanging(boolean testIsChanging) {
        this.testIsChanging = testIsChanging;
    }
}
