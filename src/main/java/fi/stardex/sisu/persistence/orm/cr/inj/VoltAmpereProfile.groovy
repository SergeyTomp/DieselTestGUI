package fi.stardex.sisu.persistence.orm.cr.inj

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorCR
import groovy.transform.EqualsAndHashCode

import javax.persistence.*

/**
 * @author Rom8
 * @since 25.02.2016
 */
@Entity
@Table(name = "volt_ampere_profile")
@EqualsAndHashCode(includes = "profileName")
public class VoltAmpereProfile {

    @Id
    @Column(name = "profile_name", nullable = false, length = 45)
    String profileName

    @Column(name = "voltage", nullable = false)
    BigDecimal voltage

    @Column(name = "first_current", nullable = false)
    BigDecimal firstCurrent

    @Column(name = "second_current", nullable = false)
    BigDecimal secondCurrent

    @Column(name = "first_width", nullable = false)
    BigDecimal firstWidth

    @Column(name = "user_type")
    Boolean userType

    @Column(name = "boost_u", nullable = false)
    BigDecimal boostU

    @Column(name = "boost_i", nullable = false)
    BigDecimal boostI

    @Column(name = "battery_u", nullable = false)
    BigDecimal batteryU

    @Column(name = "first_i")
    BigDecimal firstI

    @Column(name = "first_w", nullable = false)
    BigDecimal firstW

    @Column(name = "second_i", nullable = false)
    BigDecimal secondI

    @Column(name = "negative_u1", nullable = false)
    BigDecimal negativeU1

    @Column(name = "negative_u2", nullable = false)
    BigDecimal negativeU2

    @Column(name = "boost_disable", nullable = false)
    Boolean boostDisable

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voltAmpereProfile")
    List<InjectorCR> injectors

    @Override
    public String toString() {
        return profileName
    }

    public void print() {
        println profileName
        println voltage
        println firstCurrent
        println secondCurrent
        println firstWidth
        println boostU
        println boostI
    }
}
