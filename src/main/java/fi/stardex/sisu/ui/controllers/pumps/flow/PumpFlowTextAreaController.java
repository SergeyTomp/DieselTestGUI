package fi.stardex.sisu.ui.controllers.pumps.flow;

import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.model.PumpTestModel;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.pump.PumpTest;
import fi.stardex.sisu.states.LanguageModel;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

import static fi.stardex.sisu.util.enums.pump.PumpRegulatorConfig.NO_CONFIG;

@PropertySource("/properties/app.properties")
public class PumpFlowTextAreaController {

    @FXML private TextArea fieldTextArea;

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
    private final String LF = "\n";

    private StringBuilder sb;
    private PumpModel pumpModel;
    private PumpTestModel pumpTestModel;
    private I18N i18N;
    private LanguageModel languageModel;
    private Logger log = LoggerFactory.getLogger(PumpFlowTextAreaController.class);



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
    public void init(){

        sb = new StringBuilder();
        bindingI18N();

        pumpTestModel.pumpTestObjectProperty().addListener((observableValue, oldValue, newValue) -> {
            fieldTextArea.clear();
            if(newValue != null){
                makeText();
            }
        });

        languageModel.languageProperty().addListener((observableValue, oldValue, newValue) -> {
            log.info(newValue.toString());
            fieldTextArea.clear();
            makeText();
        });
    }

    private void makeText(){

        Pump pmp = pumpModel.pumpProperty().get();
        PumpTest pumpTest = pumpTestModel.pumpTestObjectProperty().get();

        sb.setLength(0);
        sb.append(oem.get())
                .append(pmp.getManufacturerPump())
                .append(LF)
                .append(pump.get()).append(pmp.getPumpCode()).append(LF)
                .append(type.get()).append(pumpModel.getPumpType()).append(LF)
                .append(car.get());

        pmp.getPumpCarModelList().forEach(c -> sb.append(c.getCarModel()).append(", "));
        sb.append(LF).append(LF)
                .append(test.get()).append(pumpTest).append(LF).append(LF)
                .append(feed.get()).append(pmp.getFeedPressure()).append(bar.get()).append(LF)
                .append(direction.get()).append(pmp.getPumpRotation()).append(LF);

        if(!pmp.getPumpRegulatorConfig().equals(NO_CONFIG)){
            sb.append(set.get())
                    .append(pmp.getPumpRegulatorConfig())
                    .append(current.get())
                    .append(pumpTestModel.pumpTestObjectProperty().get().getRegulatorCurrent())
                    .append(LF);
        }

        Integer pcvCurrent = pumpTest.getPcvCurrent();
        if(pcvCurrent != null){
            sb.append(psv.get()).append(pumpTest.getPcvCurrent()).append(LF);
        }

        sb.append(speed.get()).append(pumpTest.getMotorSpeed()).append(rpm.get()).append(LF)
                .append(rail.get()).append(pumpTest.getTargetPressure()).append(bar.get()).append(LF).append(LF)
                .append(measure.get());

        fieldTextArea.textProperty().set(sb.toString());
    }

    public void bindingI18N(){

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
}
