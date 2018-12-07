package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InjectorTestModel {

    private final InjectorTestRepository injectorTestRepository;
    private final ObjectProperty<ObservableList<InjectorTest>> injectorTestObservableListProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<InjectorTest> injectorTestProperty = new SimpleObjectProperty<>();

    public InjectorTestModel(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }

    public ObjectProperty<ObservableList<InjectorTest>> injectorTestObservableListProperty() {
        return injectorTestObservableListProperty;
    }

    public ObjectProperty<InjectorTest> injectorTestProperty() {
        return injectorTestProperty;
    }

    void initInjectorTestList(Injector injector){
        injectorTestObservableListProperty.setValue(FXCollections.observableList(injectorTestRepository.findAllByInjector(injector)));
    }
}
