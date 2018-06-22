package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.Injector;
import fi.stardex.sisu.persistence.orm.Manufacturer;
import groovy.transform.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "Injector_CR")
@EqualsAndHashCode(includes = "injectorCode")
public class InjectorCR implements Injector {

    @Id
    @Column(name = "injector_code", nullable = false, length = 45)
    private String injectorCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "injector_type")
//    private InjectorType injectorType;
//
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volt_ampere_profile")
    private VoltAmpereProfile voltAmpereProfile;
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "injector")
//    private List<InjectorTest> injectorTests;
//
//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "injector")
//    private List<InjStep3> injStep3List;

    @Column(length = 4)
    private String codetype;

    @Column(name = "calibration_id", length = 4)
    private String calibrationId;

    @Column(name = "checksum_m", length = 4)
    private Integer checksumM;

    @Column(name = "k_coefficient", length = 4)
    private Integer kCoefficient;

    @Override
    public String toString() {
        return injectorCode;
    }

//    public List<InjectorTest> getCodingTests() {
//        List<InjectorTest> codingTests = new ArrayList();
//        for(InjectorTest test : injectorTests) {
//            if(test.getTestName().getMeasurement() == Measurement.VISUAL ||
//                    test.getTestName().getMeasurement() == Measurement.DIRECT) {
//                codingTests.add(test)
//            }
//        }
//        codingTests.sort()
//    }
}
