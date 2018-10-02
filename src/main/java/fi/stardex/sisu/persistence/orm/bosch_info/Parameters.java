package fi.stardex.sisu.persistence.orm.bosch_info;

import fi.stardex.sisu.util.enums.BoschImg;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parameters {

    @Id
    @Column(name = "injector_code")
    private String injector;

    @Column(name = "additional_serial_number")
    private String addidionalSerialNumber;

    @Column(name = "AH")
    private String AH;

    @Column(name = "RLS")
    private String RLS;

    @Column(name = "AHE")
    private String AHE;

    @Column(name = "DNH")
    private String DNH;

    @Column(name = "UEH")
    private String UEH;

    @Column
    @Enumerated(EnumType.STRING)
    private BoschImg image;

    @OneToMany(mappedBy = "injector", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Nozzles> nossles = new ArrayList<>();

    @OneToMany(mappedBy = "injector", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Valves> valves = new ArrayList<>();

    @OneToMany(mappedBy = "injector", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    List<Applications> applications = new ArrayList<>();

    public String getInjector() {
        return injector;
    }

    public String getAddidionalSerialNumber() {
        return addidionalSerialNumber;
    }

    public String getAH() {
        return AH;
    }

    public String getRLS() {
        return RLS;
    }

    public String getAHE() {
        return AHE;
    }

    public String getDNH() {
        return DNH;
    }

    public String getUEH() {
        return UEH;
    }

    public BoschImg getImage() {
        return image;
    }

    public List<Nozzles> getNossles() {
        return nossles;
    }

    public List<Valves> getValves() {
        return valves;
    }

    public List<Applications> getApplications() {
        return applications;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "injector='" + injector + '\'' +
                ", addidionalSerialNumber='" + addidionalSerialNumber + '\'' +
                ", AH='" + AH + '\'' +
                ", RLS='" + RLS + '\'' +
                ", AHE='" + AHE + '\'' +
                ", DNH='" + DNH + '\'' +
                ", UEH='" + UEH + '\'' +
                ", image='" + image + '\'' +
                ", nossles=" + nossles +
                ", valves=" + valves +
                ", applications=" + applications +
                '}';
    }
}
