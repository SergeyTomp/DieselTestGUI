package fi.stardex.sisu.model.cr;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.repos.ManufacturerRepository;
import fi.stardex.sisu.util.enums.Tests;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static fi.stardex.sisu.ui.controllers.cr.dialogs.ManufacturerMenuDialogController.State;

public class MainSectionModel {

    private final ManufacturerRepository manufacturerRepository;

    // manufacturer listView part
    private final ObservableList<Manufacturer> manufacturerObservableList = FXCollections.observableArrayList();
    private final ObjectProperty<Manufacturer> manufacturerObjectProperty = new SimpleObjectProperty<>();
    private ObjectProperty<State> makeOrDelProducer = new SimpleObjectProperty<>();

    // model listView part
    private ObjectProperty<Injector> injector = new SimpleObjectProperty<>();
    private boolean injectorIsChanging;

    // test listView part
    private final ObjectProperty<InjectorTest> injectorTestProperty = new SimpleObjectProperty<>();
    private boolean testIsChanging;

    // test mode checkButtons part
    private ObjectProperty<Tests.TestType> testType = new SimpleObjectProperty<>(Tests.TestType.AUTO);

    // start button part
    private BooleanProperty startButton = new SimpleBooleanProperty();

    // adjustment/measuring timers part

    public MainSectionModel(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public ObservableList<Manufacturer> getManufacturerObservableList() {
        return manufacturerObservableList;
    }
    public ObjectProperty<Manufacturer> manufacturerObjectProperty() {
        return manufacturerObjectProperty;
    }
    public ObjectProperty<State> makeOrDelProducerProperty() {
        return makeOrDelProducer;
    }
    public BooleanProperty startButtonProperty() {
        return startButton;
    }
    public ObjectProperty<Tests.TestType> testTypeProperty() {
        return testType;
    }
    public ObjectProperty<Injector> injectorProperty() {
        return injector;
    }
    public ObjectProperty<InjectorTest> injectorTestProperty() {
        return injectorTestProperty;
    }
    public boolean isInjectorIsChanging() {
        return injectorIsChanging;
    }
    public boolean isTestIsChanging() {
        return testIsChanging;
    }

    public void setInjectorIsChanging(boolean injectorIsChanging) {
        this.injectorIsChanging = injectorIsChanging;
    }
    public void setTestIsChanging(boolean testIsChanging) {
        this.testIsChanging = testIsChanging;
    }
}
