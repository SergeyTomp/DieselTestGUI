package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;

@Entity
@Table(name = "pump_test_name")
public class PumpTestName {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "test_name")
    private String testName;

}
