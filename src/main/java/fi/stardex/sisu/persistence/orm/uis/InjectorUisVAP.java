package fi.stardex.sisu.persistence.orm.uis;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.VAP;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.InjectorType;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "volt_ampere_profiles_UIS")
public class InjectorUisVAP implements VAP {

    @Id
    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "cam_type")
    private Integer camType;

    @Column(name = "inlet_pressure")
    private Integer inletPressure;

    //    @ManyToOne(fetch = FetchType.EAGER)
    //    @JoinColumn(name = "injector_type")
    @Column(name = "injector_type")
    @Enumerated(EnumType.STRING)
    private InjectorType injectorType;

    //    @ManyToOne(fetch = FetchType.EAGER)
    //    @JoinColumn(name = "injector_sub_type")
    @Column(name = "injector_sub_type")
    @Enumerated(EnumType.STRING)
    private InjectorSubType injectorSubType;

    @Column(name = "boost_disable")
    private Boolean boostDisable;

    @Column(name = "boost_u")
    private Integer boostU;

    @Column(name = "boost_i")
    private Double boostI;

    @Column(name = "battery_u")
    private Integer batteryU;

    @Column(name = "first_i")
    private Double firstI;

    @Column(name = "first_w")
    private Integer firstW;

    @Column(name = "second_i")
    private Double secondI;

    @Column(name = "boost_i2")
    private Double boostI2;

    @Column(name = "first_i2")
    private Double firstI2;

    @Column(name = "first_w2")
    private Integer firstW2;

    @Column(name = "second_i2")
    private Double secondI2;

    @Column(name = "negative_u")
    private Integer negativeU;

    @Column(name = "bip_pwm")
    private Integer bipPWM;

    @Column(name = "bip_window")
    private Integer bipWindow;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voltAmpereProfile")
    private List<InjectorUIS> injectors = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voltAmpereProfile")
    private List<InjectorUisTest> injectorTests = new LinkedList<>();

    public InjectorUisVAP() { }

    public InjectorUisVAP(String profileName,
                          InjectorType injectorType,
                          InjectorSubType injectorSubType,
                          Integer camType, Boolean isCustom,
                          Integer inletPressure, Integer boostU,
                          Integer batteryU,
                          Double boostI,
                          Double firstI,
                          Integer firstW,
                          Double secondI,
                          Double boostI2,
                          Double firstI2,
                          Integer firstW2,
                          Double secondI2,
                          Integer negativeU,
                          Boolean boostDisable,
                          Integer bipPWM,
                          Integer bipWindow) {
        this.profileName = profileName;
        this.injectorType = injectorType;
        this.injectorSubType = injectorSubType;
        this.camType = camType;
        this.isCustom = isCustom;
        this.inletPressure = inletPressure;
        this.boostU = boostU;
        this.batteryU = batteryU;
        this.boostI = boostI;
        this.firstI = firstI;
        this.firstW = firstW;
        this.secondI = secondI;
        this.boostI2 = boostI2;
        this.firstI2 = firstI2;
        this.firstW2 = firstW2;
        this.secondI2 = secondI2;
        this.negativeU = negativeU;
        this.boostDisable = boostDisable;
        this.bipPWM = bipPWM;
        this.bipWindow = bipWindow;
    }

    @Override public String getProfileName() {
        return profileName;
    }
    @Override public InjectorType getInjectorType() {
        return injectorType;
    }
    @Override public InjectorSubType getInjectorSubType() {
        return injectorSubType;
    }
    @Override public Boolean isCustom() {
        return isCustom;
    }
    @Override public Integer getBoostU() {
        return boostU;
    }
    @Override public Integer getBatteryU() {
        return batteryU;
    }
    @Override public Double getBoostI() {
        return boostI;
    }
    @Override public Double getFirstI() {
        return firstI;
    }
    @Override public Integer getFirstW() {
        return firstW;
    }
    @Override public Double getSecondI() {
        return secondI;
    }
    @Override public Integer getNegativeU() {
        return negativeU;
    }
    @Override public Boolean getBoostDisable() {
        return boostDisable;
    }
    @Override public Double getBoostI2() {
        return boostI2;
    }
    @Override public Double getFirstI2() {
        return firstI2;
    }
    @Override public Integer getFirstW2() {
        return firstW2;
    }
    @Override public Double getSecondI2() {
        return secondI2;
    }

    public Integer getCamType() {
        return camType;
    }
    public Integer getInletPressure() {
        return inletPressure;
    }
    public Integer getBipPWM() {
        return bipPWM;
    }
    public Integer getBipWindow() {
        return bipWindow;
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
        InjectorUisVAP that = (InjectorUisVAP) o;
        return Objects.equals(getProfileName(), that.getProfileName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProfileName());
    }
}
