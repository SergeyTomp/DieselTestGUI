package fi.stardex.sisu.model.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.enums.Tests;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

public class MainSectionUisModel {

    // manufacturer listView part
    private final ObservableList<? extends Producer> producerObservableList = FXCollections.observableArrayList();
    private final ObjectProperty<? extends Producer> producerObjectProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customProducer = new SimpleObjectProperty<>();

    // model listView part
    private ObjectProperty<? extends Model> model = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customModel = new SimpleObjectProperty<>();
    private boolean modelIsChanging;

    // test listView part
    private final ObjectProperty<? extends Test> modelTestProperty = new SimpleObjectProperty<>();
    private ObjectProperty<Operation> customTest = new SimpleObjectProperty<>();
    private boolean testIsChanging;

    // test mode checkButtons part
    private ObjectProperty<Tests.TestType> testType = new SimpleObjectProperty<>(Tests.TestType.AUTO);

    // start button part
    private BooleanProperty startButton = new SimpleBooleanProperty();

    // adjustment/measuring timers part

    // reset/store/print button part
    private Button storeButton = new Button();

    public ObservableList<? extends Producer> getProducerObservableList() {
        return producerObservableList;
    }
    public ObjectProperty<? extends Producer> manufacturerObjectProperty() {
        return producerObjectProperty;
    }
    public ObjectProperty<Operation> customProducerProperty() {
        return customProducer;
    }
    public ObjectProperty<Operation> customModelProperty() {
        return customModel;
    }
    public ObjectProperty<Operation> customTestProperty() {
        return customTest;
    }
    public BooleanProperty startButtonProperty() {
        return startButton;
    }
    public ObjectProperty<Tests.TestType> testTypeProperty() {
        return testType;
    }
    public ObjectProperty<? extends Model> modelProperty() {
        return model;
    }
    public ObjectProperty<? extends Test> injectorTestProperty() {
        return modelTestProperty;
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

    public void setModelIsChanging(boolean modelIsChanging) {
        this.modelIsChanging = modelIsChanging;
    }
    public void setTestIsChanging(boolean testIsChanging) {
        this.testIsChanging = testIsChanging;
    }
}
