package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "pump_info")
@NamedEntityGraph(name = "carModels", attributeNodes = {@NamedAttributeNode("pumpCarModels")})
public class PumpInfo {

    @Id
    @Column(name = "pump_code")
    private String pumpCode;

    @Column(name = "pump_type")
    private String pumpType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pumpCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PumpCarModel> pumpCarModels = new LinkedList<>();

    public List<PumpCarModel> getPumpCarModels() {
        return pumpCarModels;
    }

    @Override
    public String toString() {
        return pumpType;
    }

}
