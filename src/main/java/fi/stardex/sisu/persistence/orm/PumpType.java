package fi.stardex.sisu.persistence.orm;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "pump_type")
public class PumpType {

    @Id
    @Column(name = "pump_type", nullable = false, length = 45)
    private String pumpType;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pumpType")
    private List<Pump> pumps;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pumpType")
    private List<PumpTest> pumpTests;

    public String getPumpType() {
        return pumpType;
    }

    public void setPumpType(String pumpType) {
        this.pumpType = pumpType;
    }

    public List<Pump> getPumps() {
        return pumps;
    }

    public void setPumps(List<Pump> pumps) {
        this.pumps = pumps;
    }

    public List<PumpTest> getPumpTests() {
        return pumpTests;
    }

    public void setPumpTests(List<PumpTest> pumpTests) {
        this.pumpTests = pumpTests;
    }


}
