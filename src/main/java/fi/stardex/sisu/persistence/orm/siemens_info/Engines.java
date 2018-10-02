package fi.stardex.sisu.persistence.orm.siemens_info;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Engines {

    @Id
    @Column
    private String engine;

    @Column(name = "engine_type")
    private String engineType;

    public String getType() {
        return engine;
    }

    public String getEngineType() {
        return engineType;
    }

    @OneToMany(mappedBy = "engine",  cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Cars> carsList = new ArrayList<>();

    @OneToMany(mappedBy = "engine",  cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Spares> sparesList = new ArrayList<>();

    @OneToMany(mappedBy = "engine",  cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Reference> referenceList = new ArrayList<>();

    public String getEngine() {
        return engine;
    }

    public List<Cars> getCarsList() {
        return carsList;
    }

    public List<Spares> getSparesList() {
        return sparesList;
    }

    public List<Reference> getReferenceList() {
        return referenceList;
    }

    @Override
    public String toString() {
        return "Engines{" +
                "type='" + engine + '\'' +
                ", engineType='" + engineType + '\'' +
                '}';
    }
}
