package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.util.enums.pump.PumpPressureControl;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig;
import fi.stardex.sisu.util.enums.pump.PumpRegulatorType;
import fi.stardex.sisu.util.enums.pump.PumpRotation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pump")
@NamedEntityGraph(name = "Pump.allLazy", attributeNodes = {@NamedAttributeNode("manufacturer"), @NamedAttributeNode("pumpCarModelList"), @NamedAttributeNode("pumpInfo")})
@NamedEntityGraph(name = "Pump.pumpCode", attributeNodes = {@NamedAttributeNode("manufacturer")})
public class Pump implements Model {

    @Id
    @Column(name = "pump_code")
    private String pumpCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer")
    private ManufacturerPump manufacturer;

    @Column
    private Boolean custom;

    @Column(name = "feed_pressure", nullable = false)
    private Double feedPressure;

    @Column(name = "rotation", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRotation pumpRotation;

    @Column(name = "regulator_config", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRegulatorConfig pumpRegulatorConfig;

    @Column(name = "pressure_control", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpPressureControl pumpPressureControl;

    @Column(name = "regulator_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PumpRegulatorType pumpRegulatorType;

    @Column(name = "scv_curr_inj")
    private Double scvCurrInj;

    @Column(name = "scv_min_curr")
    private Double scvMinCurr;

    @Column(name = "scv_max_curr")
    private Double scvMaxCurr;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pump", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PumpTest> pumpTests = new ArrayList<>();

    //  старый вариант маппинга, оставил как пример
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "pump_code", referencedColumnName = "pump_code")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pumpCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PumpCarModel> pumpCarModelList = new ArrayList<>();

    //  старый вариант маппинга, оставил как пример (в PumpInfo убрать implements Serializable и изменить тип pumpCode с Pump на String, как было)
//    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "pump_code", referencedColumnName = "pump_code")
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "pumpCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private PumpInfo pumpInfo;

    public Pump() { }

    public Pump(String pumpCode, Pump original) {
        this.pumpCode = pumpCode;
        this.manufacturer = original.manufacturer;
        this.feedPressure = original.feedPressure;
        this.pumpRotation = original.pumpRotation;
        this.pumpRegulatorConfig = original.pumpRegulatorConfig;
        this.pumpPressureControl = original.pumpPressureControl;
        this.pumpRegulatorType = original.pumpRegulatorType;
        this.custom = true;
    }

    public Pump(String pumpCode,
                ManufacturerPump manufacturer,
                Boolean custom,
                Double feedPressure,
                PumpRotation pumpRotation,
                PumpRegulatorConfig pumpRegulatorConfig,
                PumpPressureControl pumpPressureControl,
                PumpRegulatorType pumpRegulatorType) {
        this.pumpCode = pumpCode;
        this.manufacturer = manufacturer;
        this.custom = custom;
        this.feedPressure = feedPressure;
        this.pumpRotation = pumpRotation;
        this.pumpRegulatorConfig = pumpRegulatorConfig;
        this.pumpPressureControl = pumpPressureControl;
        this.pumpRegulatorType = pumpRegulatorType;
    }

    public void setFeedPressure(Double feedPressure) {
        this.feedPressure = feedPressure;
    }
    public void setPumpRotation(PumpRotation pumpRotation) {
        this.pumpRotation = pumpRotation;
    }
    public void setPumpRegulatorConfig(PumpRegulatorConfig pumpRegulatorConfig) {
        this.pumpRegulatorConfig = pumpRegulatorConfig;
    }
    public void setPumpPressureControl(PumpPressureControl pumpPressureControl) {
        this.pumpPressureControl = pumpPressureControl;
    }
    public void setPumpRegulatorType(PumpRegulatorType pumpRegulatorType) {
        this.pumpRegulatorType = pumpRegulatorType;
    }

    public List<PumpCarModel> getPumpCarModelList() {
        return pumpCarModelList;
    }

    public PumpInfo getPumpInfo() {
        return pumpInfo;
    }

    public String getPumpCode() {
        return pumpCode;
    }

    public double getFeedPressure() {
        return feedPressure;
    }

    public PumpRotation getPumpRotation() {
        return pumpRotation;
    }

    public PumpRegulatorConfig getPumpRegulatorConfig() {
        return pumpRegulatorConfig;
    }

    public PumpPressureControl getPumpPressureControl() {
        return pumpPressureControl;
    }

    public Double getScvCurrInj() {
        return scvCurrInj;
    }

    public Double getScvMinCurr() {
        return scvMinCurr;
    }

    public Double getScvMaxCurr() {
        return scvMaxCurr;
    }

    public List<PumpTest> getPumpTests() {
        return pumpTests;
    }

    @Override
    public Boolean isCustom() {
        return custom;
    }

    public PumpRegulatorType getPumpRegulatorType() {
        return pumpRegulatorType;
    }

    @Override
    public String toString() {
        return pumpCode;
    }

    @Override
    public String getModelCode() {
        return getPumpCode();
    }

    @Override
    public ManufacturerPump getManufacturer() {
        return manufacturer;
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
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pump that = (Pump) o;
        return Objects.equals(getModelCode(), that.getModelCode());
    }

    @Override
    public int hashCode() {

            return Objects.hash(getModelCode());
    }
}
