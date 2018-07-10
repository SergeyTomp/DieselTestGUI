package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.util.enums.Measurement;
import org.springframework.lang.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "test_name")
public class TestName {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @NonNull
    @Column(name = "test_name", unique = true, length = 45)
    String testName;

    @Column(name = "measurement")
    @Enumerated(EnumType.STRING)
    Measurement measurement;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestName testName1 = (TestName) o;
        return Objects.equals(testName, testName1.testName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(testName);
    }

    @Override
    public String toString() {
        return testName;
    }

}
