package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pump_info")
public class PumpInfo implements Serializable{

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pump_code")
    private Pump pumpCode;

    @Column(name = "pump_type")
    private String pumpType;

    @Override
    public String toString() {
        return pumpType;
    }

}
