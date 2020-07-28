package fi.stardex.sisu.persistence.orm.uis;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "injectors_uis")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "InjectorsUIS.allLazy", attributeNodes = {@NamedAttributeNode("manufacturer"), @NamedAttributeNode("voltAmpereProfile")}),
        @NamedEntityGraph(name = "InjectorsUIS.modelCode", attributeNodes = {@NamedAttributeNode("voltAmpereProfile")})
})

public class InjectorUIS implements Model {

    @Id
    @Column(name = "injector_code")
    private String injectorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_name")
    private ManufacturerUIS manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volt_ampere_profile", nullable = false)
    private InjectorUisVAP voltAmpereProfile;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "injector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InjectorUisTest> injectorTests = new LinkedList<>();

    public InjectorUIS() { }

    public InjectorUIS(String injectorCode,
                       ManufacturerUIS manufacturer,
                       InjectorUisVAP voltAmpereProfile,
                       Boolean isCustom) {
        this.injectorCode = injectorCode;
        this.manufacturer = manufacturer;
        this.voltAmpereProfile = voltAmpereProfile;
        this.isCustom = isCustom;
    }

    @PostPersist
    private void onPostPersist() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostUpdate
    private void onPostUpdate() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostRemove
    private void onPostRemove() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @Override
    public String getModelCode() {
        return injectorCode;
    }

    @Override
    public ManufacturerUIS getManufacturer() {
        return manufacturer;
    }

    @Override
    public Boolean isCustom() {
        return isCustom;
    }

    @Override
    public VAP getVAP() {
        return voltAmpereProfile;
    }

    @Override
    public Integer getCodetype() {
        return -1;
    }

    public List<InjectorUisTest> getInjectorTests() {
        return injectorTests;
    }

    @Override
    public void setVAP(VAP voltAmpereProfile){
        this.voltAmpereProfile = (InjectorUisVAP) voltAmpereProfile;}

    @Override
    public String toString() {
        return injectorCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectorUIS that = (InjectorUIS) o;
        return Objects.equals(getModelCode(), that.getModelCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModelCode());
    }
}
