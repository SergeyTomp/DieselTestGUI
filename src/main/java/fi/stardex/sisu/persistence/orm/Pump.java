package fi.stardex.sisu.persistence.orm;

import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.pumps.scv.SCVEnum;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "pump")
public class Pump implements Model {

    @Id
    @Column(name = "serial_number", nullable = false, length = 45)
    private String serialNumber;
    @ManyToOne
    @JoinColumn(name = "manufacturer")
    private Manufacturer manufacturer;
    @ManyToOne
    @JoinColumn(name = "pump_type")
    private PumpType pumpType;
    @Column
    private BigDecimal supply;
    @Enumerated(EnumType.STRING)
    @Column(name = "rotation_direction")
    private RotationDirection rotationDirection;
    @Enumerated(EnumType.STRING)
    @Column
    private SCVEnum scv;
    @Column(name = "shut_off")
    private Boolean shutOff;
    @Column(name = "closed_scv_def_cur")
    private BigDecimal closedScvDefCur;
    @Column(name = "closed_pcv_def_cur")
    private BigDecimal closedPcvDefCur;
    @Column(name = "shut_off_valve_def_cur")
    private BigDecimal shutOffValveDefCur;

    public static enum RotationDirection {
        LEFT, RIGHT;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public PumpType getPumpType() {
        return pumpType;
    }

    public void setPumpType(PumpType pumpType) {
        this.pumpType = pumpType;
    }

    public BigDecimal getSupply() {
        return supply;
    }

    public void setSupply(BigDecimal supply) {
        this.supply = supply;
    }

    public RotationDirection getRotationDirection() {
        return rotationDirection;
    }

    public void setRotationDirection(RotationDirection rotationDirection) {
        this.rotationDirection = rotationDirection;
    }

    public SCVEnum getScv() {
        return scv;
    }

    public void setScv(SCVEnum scv) {
        this.scv = scv;
    }

    public Boolean getShutOff() {
        return shutOff;
    }

    public void setShutOff(Boolean shutOff) {
        this.shutOff = shutOff;
    }

    public BigDecimal getClosedScvDefCur() {
        return closedScvDefCur;
    }

    public void setClosedScvDefCur(BigDecimal closedScvDefCur) {
        this.closedScvDefCur = closedScvDefCur;
    }

    public BigDecimal getClosedPcvDefCur() {
        return closedPcvDefCur;
    }

    public void setClosedPcvDefCur(BigDecimal closedPcvDefCur) {
        this.closedPcvDefCur = closedPcvDefCur;
    }

    public BigDecimal getShutOffValveDefCur() {
        return shutOffValveDefCur;
    }

    public void setShutOffValveDefCur(BigDecimal shutOffValveDefCur) {
        this.shutOffValveDefCur = shutOffValveDefCur;
    }

    @Override
    public String toString() {
        return serialNumber;
    }
}
