package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import javax.persistence.*;

@Entity
public class Injector implements Model {

    @Id
    @Column(name = "injector_code")
    private String injectorCode;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volt_ampere_profile")
    private VoltAmpereProfile voltAmpereProfile;

    @Column(length = 15)
    private String codetype;

    @Column(name = "calibration_id", length = 4)
    private String calibrationId;

    @Column(name = "checksum_m", length = 4)
    private Integer checksumM;

    @Column(name = "k_coefficient")
    private Integer kCoefficient;

    @Column(name = "is_custom")
    private Boolean isCustom;

    public Injector() {
    }

    public Injector(String injectorCode, Manufacturer manufacturer, VoltAmpereProfile voltAmpereProfile, Boolean isCustom) {
        this.injectorCode = injectorCode;
        this.manufacturer = manufacturer;
        this.voltAmpereProfile = voltAmpereProfile;
        this.isCustom = isCustom;
    }

    public String getInjectorCode() {
        return injectorCode;
    }

    public void setInjectorCode(String injectorCode) {
        this.injectorCode = injectorCode;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(String calibrationId) {
        this.calibrationId = calibrationId;
    }

    public Integer getChecksumM() {
        return checksumM;
    }

    public void setChecksumM(Integer checksumM) {
        this.checksumM = checksumM;
    }

    public Integer getkCoefficient() {
        return kCoefficient;
    }

    public void setkCoefficient(Integer kCoefficient) {
        this.kCoefficient = kCoefficient;
    }

    public Boolean getCustom() {
        return isCustom;
    }

    public void setCustom(Boolean custom) {
        isCustom = custom;
    }

    public VoltAmpereProfile getVoltAmpereProfile() {
        return voltAmpereProfile;
    }

    public void setVoltAmpereProfile(VoltAmpereProfile voltAmpereProfile) {
        this.voltAmpereProfile = voltAmpereProfile;
    }

    @Override
    public String toString() {
        return injectorCode;
    }
}
