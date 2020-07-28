package fi.stardex.sisu.persistence.orm.uis;

import fi.stardex.sisu.persistence.orm.interfaces.Name;
import fi.stardex.sisu.util.enums.Measurement;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "test_names_uis")
public class InjectorUisTestName implements Name {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(name = "test_name", unique = true, length = 45)
    private String testName;

    @Column(name = "display_order", unique = true, nullable = false)
    private Integer displayOrder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "testName", cascade = CascadeType.ALL)
    private List<InjectorUisTest> injectorTests = new LinkedList<>();

    @Override public String getName() {
        return testName;
    }

    @Override public Integer getOrder() {
        return getDisplayOrder();
    }

    @Override
    public Measurement getMeasurement() {
        return Measurement.DELIVERY;
    }

    private Integer getDisplayOrder() {
        return displayOrder;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectorUisTestName)) return false;

        InjectorUisTestName that = (InjectorUisTestName) o;

        if (!id.equals(that.id)) return false;
        if (!testName.equals(that.testName)) return false;
        return getDisplayOrder().equals(that.getDisplayOrder());
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + testName.hashCode();
        result = 31 * result + getDisplayOrder().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return testName;
    }
}
