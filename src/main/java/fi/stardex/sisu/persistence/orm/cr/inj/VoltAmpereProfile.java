package fi.stardex.sisu.persistence.orm.cr.inj;

import javax.persistence.*;

@Entity
@Table(name = "volt_ampere_profile")
public class VoltAmpereProfile {

    @Id
    @Column(name = "profile_name")
    private String profileName;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public String getProfileName() {
        return profileName;
    }

    public InjectorType getInjectorType() {
        return injectorType;
    }

    public Boolean getCustom() {
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

    public VoltAmpereProfile() {
    }

    public VoltAmpereProfile(String profileName, InjectorType injectorType,
                             Boolean isCustom, Integer boostU, Integer batteryU,
                             Double boostI, Double firstI, Integer firstW,
                             Double secondI, Integer negativeU, Boolean boostDisable) {
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
    }

    @Override
    public String toString() {
        return profileName;
    }
}
