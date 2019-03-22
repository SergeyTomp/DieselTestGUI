package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.EntityUpdates;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "volt_ampere_profile")
public class VoltAmpereProfile {

    @Id
    @Column(name = "profile_name")
    private String profileName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "injector_type")
    private InjectorType injectorType;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @Column(name = "boost_u")
    private Integer boostU;

    @Column(name = "battery_u")
    private Integer batteryU;

    @Column(name = "boost_i")
    private Double boostI;

    @Column(name = "first_i")
    private Double firstI;

    @Column(name = "first_w")
    private Integer firstW;

    @Column(name = "second_i")
    private Double secondI;

    @Column(name = "negative_u")
    private Integer negativeU;

    @Column(name = "boost_disable")
    private Boolean boostDisable;

    @Column(name = "boost_i2")
    private Double boostI2;

    @Column(name = "first_i2")
    private Double firstI2;

    @Column(name = "first_w2")
    private Integer firstW2;

    @Column(name = "second_i2")
    private Double secondI2;

    @Column(name = "is_double_coil")
    private Boolean isDoubleCoil;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voltAmpereProfile")
    private List<Injector> injectors = new LinkedList<>();

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

    public String getProfileName() {
        return profileName;
    }

    public InjectorType getInjectorType() {
        return injectorType;
    }

    public Boolean isCustom() {
        return isCustom;
    }

    public Integer getBoostU() {
        return boostU;
    }

    public Integer getBatteryU() {
        return batteryU;
    }

    public Double getBoostI() {
        return boostI;
    }

    public Double getFirstI() {
        return firstI;
    }

    public Integer getFirstW() {
        return firstW;
    }

    public Double getSecondI() {
        return secondI;
    }

    public Integer getNegativeU() {
        return negativeU;
    }

    public Boolean getBoostDisable() {
        return boostDisable;
    }

    public Double getBoostI2() {
        return boostI2;
    }

    public Double getFirstI2() {
        return firstI2;
    }

    public Integer getFirstW2() {
        return firstW2;
    }

    public Double getSecondI2() {
        return secondI2;
    }

    public Boolean isDoubleCoil() {
        return isDoubleCoil;
    }

    public VoltAmpereProfile() {
    }

    public VoltAmpereProfile(String profileName, InjectorType injectorType,
                             Boolean isCustom, Integer boostU, Integer batteryU,
                             Double boostI, Double firstI, Integer firstW,
                             Double secondI, Integer negativeU, Boolean boostDisable,
                             Double boostI2, Double firstI2, Integer firstW2, Double secondI2, Boolean isDoubleCoil) {

        this.profileName = profileName;
        this.injectorType = injectorType;
        this.isCustom = isCustom;
        this.boostU = boostU;
        this.batteryU = batteryU;
        this.boostI = boostI;
        this.firstI = firstI;
        this.firstW = firstW;
        this.secondI = secondI;
        this.negativeU = negativeU;
        this.boostDisable = boostDisable;
        this.boostI2 = boostI2;
        this.firstI2 = firstI2;
        this.firstW2 = firstW2;
        this.secondI2 = secondI2;
        this.isDoubleCoil = isDoubleCoil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoltAmpereProfile that = (VoltAmpereProfile) o;
        return Objects.equals(getProfileName(), that.getProfileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProfileName());
    }

    @Override
    public String toString() {
        return profileName;
    }
}
