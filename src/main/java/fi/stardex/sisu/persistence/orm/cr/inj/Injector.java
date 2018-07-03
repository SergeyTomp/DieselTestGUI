package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.Manufacturer;
import fi.stardex.sisu.persistence.orm.interfaces.Model;

import javax.persistence.*;

@Entity
public class Injector implements Model {

    @Id
    @Column(name = "injector_code")
    private String injectorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;

    @ManyToOne
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

    public VoltAmpereProfile getVoltAmpereProfile() {
        return voltAmpereProfile;
    }

    @Override
    public String toString() {
        return injectorCode;
    }
}
