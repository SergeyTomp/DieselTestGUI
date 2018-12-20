package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pump_info")
public class PumpInfo {

    @Id
    @Column(name = "pump_code")
    private String pumpCode;

    @Column(name = "pump_type")
    private String pumpType;

    @Override
    public String toString() {
        return pumpType;
    }

}
