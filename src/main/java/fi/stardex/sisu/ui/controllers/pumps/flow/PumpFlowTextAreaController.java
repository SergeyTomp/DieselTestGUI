package fi.stardex.sisu.ui.controllers.pumps.flow;

import fi.stardex.sisu.model.LanguageModel;
import fi.stardex.sisu.model.pump.PumpModel;
import fi.stardex.sisu.model.pump.PumpTestModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig.NO_CONFIG;

@PropertySource("/properties/app.properties")
public class PumpFlowTextAreaController {

    @FXML
    private TextArea fieldTextArea;

    private final StringProperty oem = new SimpleStringProperty();
    private final StringProperty pump = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty car = new SimpleStringProperty();
    private final StringProperty test = new SimpleStringProperty();
    private final StringProperty feed = new SimpleStringProperty();
    private final StringProperty direction = new SimpleStringProperty();
    private final StringProperty speed = new SimpleStringProperty();
    private final StringProperty set = new SimpleStringProperty();
    private final StringProperty bar = new SimpleStringProperty();
    private final StringProperty rpm = new SimpleStringProperty();
    private final StringProperty current = new SimpleStringProperty();
    private final StringProperty psv = new SimpleStringProperty();
    private final StringProperty rail = new SimpleStringProperty();
    private final StringProperty measure = new SimpleStringProperty();
    private static final String NEW_LINE = "\n";

    private PumpModel pumpModel;
    private PumpTestModel pumpTestModel;
    private I18N i18N;
    private LanguageModel languageModel;

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    public void setPumpTestModel(PumpTestModel pumpTestModel) {
        this.pumpTestModel = pumpTestModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    @PostConstruct
    public void init() {

        bindingI18N();

        pumpTestModel.pumpTestProperty().addListener((observableValue, oldValue, newValue) ->
                Optional.ofNullable(newValue).ifPresentOrElse(value -> makeText(), () -> fieldTextArea.clear()));

        languageModel.languageProperty().addListener((observableValue, oldValue, newValue) ->
                Optional.ofNullable(pumpModel.pumpProperty().get()).ifPresent((pump -> makeText())));

    }

    public void bindingI18N() {

        oem.bind(i18N.createStringBinding("pump.test.report.manufacturer"));
        pump.bind(i18N.createStringBinding("pump.test.report.pumpCode"));
        type.bind(i18N.createStringBinding("pump.test.report.pumpType"));
        car.bind(i18N.createStringBinding("pump.test.report.application"));
        test.bind(i18N.createStringBinding("pump.test.report.testName"));
        feed.bind(i18N.createStringBinding("pump.test.report.pumpFeed"));
        direction.bind(i18N.createStringBinding("pump.test.report.rotationDirection"));
        speed.bind(i18N.createStringBinding("pump.test.report.rotationSpeed"));
        set.bind(i18N.createStringBinding("pump.test.report.Set"));
        bar.bind(i18N.createStringBinding("pump.test.report.Bar"));
        rpm.bind(i18N.createStringBinding("pump.test.report.RPM"));
        current.bind(i18N.createStringBinding("pump.test.report.current"));
        psv.bind(i18N.createStringBinding("pump.test.report.setPCV"));
        rail.bind(i18N.createStringBinding("pump.test.report.setRailPressure"));
        measure.bind(i18N.createStringBinding("pump.test.report.measureStart"));

    }

    private void makeText() {

        fieldTextArea.clear();

        StringBuilder sb = new StringBuilder();

        Pump pump = pumpModel.pumpProperty().get();
        PumpTest pumpTest = pumpTestModel.pumpTestProperty().get();

        sb.append(oem.get()).append(pump.getManufacturerPump()).append(NEW_LINE)
                .append(this.pump.get()).append(pump.getPumpCode()).append(NEW_LINE)
                .append(type.get()).append(pump.getPumpInfo()).append(NEW_LINE)
                .append(car.get());

        pump.getPumpCarModelList().forEach(c -> sb.append(c.getCarModel()).append(", "));
        sb.append(NEW_LINE).append(NEW_LINE)
                .append(test.get()).append(pumpTest).append(NEW_LINE).append(NEW_LINE)
                .append(feed.get()).append(pump.getFeedPressure()).append(bar.get()).append(NEW_LINE)
                .append(direction.get()).append(pump.getPumpRotation()).append(NEW_LINE);

        if (!pump.getPumpRegulatorConfig().equals(NO_CONFIG)) {
            sb.append(set.get())
                    .append(pump.getPumpRegulatorConfig())
                    .append(current.get())
                    .append(pumpTestModel.pumpTestProperty().get().getRegulatorCurrent())
                    .append(NEW_LINE);
        }

        Optional.ofNullable(pumpTest.getPcvCurrent()).ifPresent(pcvCurrent -> sb.append(psv.get()).append(pcvCurrent).append(NEW_LINE));

        sb.append(speed.get()).append(pumpTest.getMotorSpeed()).append(rpm.get()).append(NEW_LINE)
                .append(rail.get()).append(pumpTest.getTargetPressure()).append(bar.get()).append(NEW_LINE).append(NEW_LINE)
                .append(measure.get());

        fieldTextArea.textProperty().set(sb.toString());

    }

}
