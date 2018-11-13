package fi.stardex.sisu.persistence.orm.pump;

import javax.persistence.*;

@Entity
@Table(name = "pump_car_model")
public class PumpCarModel {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "pump_code")
    private String pumpCode;

    @Column(name = "car_model")
    private String carModel;

    public String getPumpCode() {
        return pumpCode;
    }

    public String getCarModel() {
        return carModel;
    }

    @Override
    public String toString() {
        return carModel;
    }

}
