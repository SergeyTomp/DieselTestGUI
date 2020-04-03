package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@NamedEntityGraph(name = "Injector.allLazy", attributeNodes = {@NamedAttributeNode("manufacturer"), @NamedAttributeNode("voltAmpereProfile")})
public class Injector implements Model {

    @Id
    @Column(name = "injector_code")
    private String injectorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "volt_ampere_profile", nullable = false)
    private VoltAmpereProfile voltAmpereProfile;

    @Column(name = "code_type", nullable = false)
    private Integer codetype;

    @Column(name = "calibration_id", length = 4)
    private String calibrationId;

    @Column(name = "coefficient")
    private Integer coefficient;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @Column(name = "is_heui")
    private Boolean isHeui;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "injector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InjectorTest> injectorTests = new LinkedList<>();

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

    public Injector() {
    }

    public Injector(String injectorCode, Manufacturer manufacturer, VoltAmpereProfile voltAmpereProfile, Boolean isCustom, Integer codetype) {
        this.injectorCode = injectorCode;
        this.manufacturer = manufacturer;
        this.voltAmpereProfile = voltAmpereProfile;
        this.isCustom = isCustom;
        this.codetype = codetype;
    }

    public String getInjectorCode() {
        return injectorCode;
    }

    @Override
    public String getModelCode() {
        return getInjectorCode();
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
    @Override
    public Integer getCodetype() {
        return codetype;
    }

    public String getCalibrationId() {
        return calibrationId;
    }

    public Integer getCoefficient() {
        return coefficient;
    }

    @Override
    public Boolean isCustom() {
        return isCustom;
    }

    @Override
    public VAP getVAP() {
        return null;
    }

    public VoltAmpereProfile getVoltAmpereProfile() {
        return voltAmpereProfile;
    }

    public void setVoltAmpereProfile(VoltAmpereProfile voltAmpereProfile) {
        this.voltAmpereProfile = voltAmpereProfile;
    }

    public void setHeui(Boolean heui) {
        isHeui = heui;
    }

    public Boolean isHeui() {
        return isHeui;
    }

    @Override
    public String toString() {
        return injectorCode;
    }

}
