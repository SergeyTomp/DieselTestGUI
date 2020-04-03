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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PumpTestName)) return false;

        PumpTestName that = (PumpTestName) o;

        if (id != that.id) return false;
        return testName.equals(that.testName);
    }

    @Override
    public int hashCode() {
        return 31 * id + testName.hashCode();
    }
}
