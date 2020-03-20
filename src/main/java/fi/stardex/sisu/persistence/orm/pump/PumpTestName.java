package fi.stardex.sisu.persistence.orm.pump;

import fi.stardex.sisu.persistence.orm.interfaces.Name;
import fi.stardex.sisu.util.enums.Measurement;

import javax.persistence.*;

@Entity
@Table(name = "pump_test_name")
public class PumpTestName implements Name {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_name")
    private String testName;

    @Override
    public String toString() {
        return testName;
    }

    @Override
    public String getName() {
        return testName;
    }

    @Override
    public Integer getOrder() {
        return null;
    }

    @Override
    public Measurement getMeasurement() {
        return null;
    }
}
