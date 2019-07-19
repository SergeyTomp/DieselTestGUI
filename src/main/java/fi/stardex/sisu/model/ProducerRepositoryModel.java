package fi.stardex.sisu.model;

import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.springframework.data.repository.CrudRepository;

public class ProducerRepositoryModel {

    private ObjectProperty<CrudRepository<? extends Producer, String>> producerRepositoryProperty = new SimpleObjectProperty<>();

    public ObjectProperty<CrudRepository<? extends Producer, String>> getProducerRepositoryProperty() {
        return producerRepositoryProperty;
    }
}
