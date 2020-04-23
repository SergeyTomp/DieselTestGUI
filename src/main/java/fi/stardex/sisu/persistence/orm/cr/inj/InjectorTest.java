package fi.stardex.sisu.persistence.orm.cr.inj;

import fi.stardex.sisu.persistence.orm.EntityUpdates;
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
        @NamedEntityGraph(name = "InjectorTest.testName", attributeNodes = {
                @NamedAttributeNode("testName"),
                @NamedAttributeNode("injector")}),
        @NamedEntityGraph(name = "InjectorTest.allLazy", attributeNodes = {
                @NamedAttributeNode("injector"),
                @NamedAttributeNode("testName"),
                @NamedAttributeNode("voltAmpereProfile")})})
@Table(name = "injector_test")
public class InjectorTest implements ChangeListener<Boolean> {

    private static final ObservableList<InjectorTest> listOfNonIncludedTests = FXCollections.observableArrayList();

    private static InjectorTest changedInjectorTest;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "injector_code")
    private Injector injector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_name")
    private TestName testName;

    @Column(name = "motor_speed")
    private Integer motorSpeed;

    @Column(name = "setted_pressure")
    private Integer settedPressure;

    @Column(name = "adjusting_time")
    private Integer adjustingTime;

    @Column(name = "measurement_time")
    private Integer measurementTime;

    @Column(name = "injection_rate")
    private Integer injectionRate;

    @Column(name = "total_pulse_time")
    private Integer totalPulseTime;

    @Column(name = "nominal_flow")
    private Double nominalFlow;

    @Column(name = "flow_range")
    private Double flowRange;

    @Column(name = "is_custom")
    private Boolean isCustom;

    @Column(name = "total_pulse_time2")
    private Integer totalPulseTime2;

    @Column(name = "shift")
    private Integer shift;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "volt_ampere_profile")
    @NotFound
    private VoltAmpereProfile voltAmpereProfile;

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

    //FIXME: при добавлении кастомного теста не работает его перемещение по списку при вкл/откл галки так как нужно
    //снова с репозитория запрашивать обновленный список тестов
    public InjectorTest() {

        included.addListener(this);

    }

    public InjectorTest(Injector injector, TestName testName, Integer motorSpeed, Integer settedPressure,
                        Integer adjustingTime, Integer measurementTime, Integer injectionRate,
                        Integer totalPulseTime, Double nominalFlow, Double flowRange, Integer totalPulseTime2, Integer shift) {

        super();
        this.injector = injector;
        this.testName = testName;
        this.motorSpeed = motorSpeed;
        this.settedPressure = settedPressure;
        this.adjustingTime = adjustingTime;
        this.measurementTime = measurementTime;
        this.injectionRate = injectionRate;
        this.totalPulseTime = totalPulseTime;
        this.nominalFlow = nominalFlow;
        this.flowRange = flowRange;
        this.isCustom = true;
        this.totalPulseTime2 = totalPulseTime2;
        this.shift = shift;
    }

    /**Delete this constructor upon all real tests activation for codeType == 0  */
    public InjectorTest(TestName testName, Double nominalFlow, Double flowRange) {
        this.testName = testName;
        this.nominalFlow = nominalFlow;
        this.flowRange = flowRange;
    }

    public static ObservableList<InjectorTest> getListOfNonIncludedTests() {
        return listOfNonIncludedTests;
    }

    public static InjectorTest getChangedInjectorTest() {
        return changedInjectorTest;
    }

    public BooleanProperty includedProperty() {
        return included;
    }
    public void setIncluded(boolean included) {
        this.included.set(included);
    }
    public boolean isIncluded() {
        return included.get();
    }
    public Integer getId() {
        return id;
    }
    public Injector getInjector() {
        return injector;
    }
    public Integer getMotorSpeed() {
        return motorSpeed;
    }
    public Integer getSettedPressure() {
        return settedPressure;
    }
    public Integer getAdjustingTime() {
        return adjustingTime;
    }
    public Integer getMeasurementTime() {
        return measurementTime;
    }
    public Integer getInjectionRate() {
        return injectionRate;
    }
    public Integer getTotalPulseTime() {
        return totalPulseTime;
    }
    public Double getNominalFlow() {
        return nominalFlow;
    }
    public Double getFlowRange() {
        return flowRange;
    }
    public Boolean isCustom() {
        return isCustom;
    }
    public VoltAmpereProfile getVoltAmpereProfile() {
        return voltAmpereProfile;
    }
    public TestName getTestName() {
        return testName;
    }
    public Integer getTotalPulseTime2() {
        return totalPulseTime2;
    }
    public Integer getShift() {
        return shift;
    }

    public void setVoltAmpereProfile(VoltAmpereProfile voltAmpereProfile) {
        this.voltAmpereProfile = voltAmpereProfile;
    }
    public void setInjector(Injector injector) {
        this.injector = injector;
    }
    public void setTestName(TestName testName) {
        this.testName = testName;
    }
    public void setMotorSpeed(Integer motorSpeed) {
        this.motorSpeed = motorSpeed;
    }
    public void setSettedPressure(Integer settedPressure) {
        this.settedPressure = settedPressure;
    }
    public void setAdjustingTime(Integer adjustingTime) {
        this.adjustingTime = adjustingTime;
    }
    public void setMeasurementTime(Integer measurementTime) {
        this.measurementTime = measurementTime;
    }
    public void setInjectionRate(Integer injectionRate) {
        this.injectionRate = injectionRate;
    }
    public void setTotalPulseTime(Integer totalPulseTime) {
        this.totalPulseTime = totalPulseTime;
    }
    public void setNominalFlow(Double nominalFlow) {
        this.nominalFlow = nominalFlow;
    }
    public void setFlowRange(Double flowRange) {
        this.flowRange = flowRange;
    }
    public void setCustom(Boolean custom) {
        isCustom = custom;
    }
    public void setTotalPulseTime2(Integer totalPulseTime2) {
        this.totalPulseTime2 = totalPulseTime2;
    }
    public void setShift(Integer shift) {
        this.shift = shift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjectorTest that = (InjectorTest) o;
        return Objects.equals(injector, that.injector) &&
                Objects.equals(getTestName(), that.getTestName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(injector, getTestName());
    }

    @Override
    public String toString() {
        return testName.toString();
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

        changedInjectorTest = this;

        if (newValue)
            listOfNonIncludedTests.remove(this);
        else
            listOfNonIncludedTests.add(this);

    }
}
