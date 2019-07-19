package fi.stardex.sisu.persistence.orm.uis;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.annotations.NotFound;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "InjectorUisTest.testName", attributeNodes = @NamedAttributeNode("testName")),
        @NamedEntityGraph(name = "InjectorUisTest.allLazy", attributeNodes = {
                @NamedAttributeNode("injector"),
                @NamedAttributeNode("testName"),
                @NamedAttributeNode("voltAmpereProfile")})})
@Table(name = "injector_tests_uis")
public class InjectorUisTest implements Test, ChangeListener<Boolean> {

    private static final ObservableList<InjectorUisTest> listOfNonIncludedTests = FXCollections.observableArrayList();
    private static InjectorUisTest changedInjectorTest;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "injector_code")
    private InjectorUIS injector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_name")
    private InjectorUisTestName testName;

    @Column(name = "motor_speed")
    private Integer motorSpeed;

    @Column(name = "setted_pressure")
    private Integer settedPressure;

    @Column(name = "angle_1")
    private Integer angle_1;

    @Column(name = "angle_2")
    private Integer angle_2;

    @Column(name = "double_coil_offset")
    private Integer doubleCoilOffset;

    @Column(name = "total_pulse_time_1")
    private Integer totalPulseTime1;

    @Column(name = "total_pulse_time_2")
    private Integer totalPulseTime2;

    @Column(name = "nominal_flow")
    private Double nominalFlow;

    @Column(name = "flow_range")
    private Double flowRange;

    @Column(name = "adjusting_time")
    private Integer adjustingTime;

    @Column(name = "measurement_time")
    private Integer measurementTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volt_ampere_profile")
    @NotFound
    private InjectorUisVAP voltAmpereProfile;

    @Column(name = "bip")
    private Integer bip;

    @Column(name = "bip_range")
    private Integer bipRange;

    @Column(name = "rack_position")
    private Integer rackPosition;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @Transient
    private BooleanProperty included = new SimpleBooleanProperty(true);

    @PostPersist
    private void onPostPersist() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostUpdate
    private void onPostUpdate() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }

    @PostRemove
    private void onPostRemove() {
        EntityUpdates.getMapOfEntityUpdates().put(this.getClass().getSimpleName(), true);
    }


    public InjectorUisTest() { included.addListener(this); }

    public InjectorUisTest(InjectorUIS injector,
                           InjectorUisTestName testName,
                           Integer motorSpeed,
                           Integer settedPressure,
                           Integer adjustingTime,
                           Integer measurementTime,
                           Integer totalPulseTime1,
                           Double nominalFlow,
                           Double flowRange,
                           Boolean isCustom,
                           Integer totalPulseTime2,
                           Integer doubleCoilOffset,
                           InjectorUisVAP voltAmpereProfile,
                           Integer bip,
                           Integer bipRange,
                           Integer rackPosition,
                           BooleanProperty included) {
        this.injector = injector;
        this.testName = testName;
        this.motorSpeed = motorSpeed;
        this.settedPressure = settedPressure;
        this.adjustingTime = adjustingTime;
        this.measurementTime = measurementTime;
        this.totalPulseTime1 = totalPulseTime1;
        this.nominalFlow = nominalFlow;
        this.flowRange = flowRange;
        this.isCustom = isCustom;
        this.totalPulseTime2 = totalPulseTime2;
        this.doubleCoilOffset = doubleCoilOffset;
        this.voltAmpereProfile = voltAmpereProfile;
        this.bip = bip;
        this.bipRange = bipRange;
        this.rackPosition = rackPosition;
        this.included = included;
    }

    public static ObservableList<InjectorUisTest> getListOfNonIncludedTests() {
        return listOfNonIncludedTests;
    }
    public static InjectorUisTest getChangedInjectorTest() {
        return changedInjectorTest;
    }
    public BooleanProperty includedProperty() {
        return included;
    }

    @Override public Integer getId() {
        return id;
    }
    @Override public InjectorUIS getInjector() {
        return injector;
    }
    @Override public InjectorUisTestName getTestName() {
        return testName;
    }
    @Override public InjectorUisVAP getVoltAmpereProfile() {
        return voltAmpereProfile;
    }
    @Override public boolean isIncluded() {
        return included.get();
    }
    @Override public Integer getMotorSpeed() {
        return motorSpeed;
    }
    @Override public Integer getSettedPressure() {
        return settedPressure;
    }
    @Override public Integer getAdjustingTime() {
        return adjustingTime;
    }
    @Override public Integer getMeasurementTime() {
        return measurementTime;
    }
    @Override public Integer getTotalPulseTime1() {
        return totalPulseTime1;
    }
    @Override public Integer getTotalPulseTime2() {
        return totalPulseTime2;
    }
    @Override public Double getNominalFlow() {
        return nominalFlow;
    }
    @Override public Double getFlowRange() {
        return flowRange;
    }
    @Override public Boolean isCustom() {
        return isCustom;
    }
    @Override public Integer getShift() {
        return doubleCoilOffset;
    }

    public Integer getAngle_1() {
        return angle_1;
    }
    public Integer getAngle_2() {
        return angle_2;
    }
    public Integer getDoubleCoilOffset() {
        return doubleCoilOffset;
    }
    public Integer getBip() {
        return bip;
    }
    public Integer getBipRange() {
        return bipRange;
    }
    public Integer getRackPosition() {
        return rackPosition;
    }

    public void setVoltAmpereProfile(InjectorUisVAP voltAmpereProfile) {
        this.voltAmpereProfile = voltAmpereProfile;
    }
    public void setIncluded(boolean included) {
        this.included.set(included);
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {

        changedInjectorTest = this;

        if (newValue)
            listOfNonIncludedTests.remove(this);
        else
            listOfNonIncludedTests.add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectorUisTest that = (InjectorUisTest) o;
        return Objects.equals(injector, that.injector) &&
                Objects.equals(getTestName(), that.getTestName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(injector, getTestName());
    }
}
