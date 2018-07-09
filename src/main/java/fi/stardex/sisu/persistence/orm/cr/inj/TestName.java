package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.util.enums.Measurement;
import org.springframework.lang.*;

import javax.persistence.*;

@Entity
@Table(name = "test_name")
public class TestName {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @Column(name = "test_name", unique = true, length = 45)
    private String testName;

    @Column(name = "measurement")
    @Enumerated(EnumType.STRING)
    private Measurement measurement;

    public Measurement getMeasurement() {
        return measurement;
    }

    @Override
    public String toString() {
        return testName;
    }

}
