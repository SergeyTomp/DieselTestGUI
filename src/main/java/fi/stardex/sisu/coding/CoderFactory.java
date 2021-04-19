package fi.stardex.sisu.coding;

import fi.stardex.sisu.coding.bosch.*;
import fi.stardex.sisu.coding.delphi.DelphiC2ICoder;
import fi.stardex.sisu.coding.delphi.DelphiC3ICoder;
import fi.stardex.sisu.coding.delphi.DelphiC4ICoder;
import fi.stardex.sisu.coding.denso.DensoCoder;
import fi.stardex.sisu.coding.siemens.SiemensCoderOne;
import fi.stardex.sisu.coding.siemens.SiemensCoderTwo;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.CoilOnePulseParametersModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.states.InjectorControllersState;

import java.util.List;

public class CoderFactory {

    private FlowReportModel flowReportModel;
    private CodingReportModel codingReportModel;
    private InjectorControllersState injectorControllersState;
    private MainSectionModel mainSectionModel;
    private CoilOnePulseParametersModel coilOnePulseParametersModel;
    private Coder coder;

    public CoderFactory(FlowReportModel flowReportModel,
                        CodingReportModel codingReportModel,
                        InjectorControllersState injectorControllersState,
                        MainSectionModel mainSectionModel,
                        CoilOnePulseParametersModel coilOnePulseParametersModel) {

        this.flowReportModel = flowReportModel;
        this.codingReportModel = codingReportModel;
        this.injectorControllersState = injectorControllersState;
        this.mainSectionModel = mainSectionModel;
        this.coilOnePulseParametersModel = coilOnePulseParametersModel;
        mainSectionModel.injectorProperty().addListener((observableValue, oldValue, newValue) -> coder = null);
    }

    public Coder getCoder(Injector injector) {

        if (coder != null) { return coder; }

        Producer producer = injector.getManufacturer();
        CodeType codetype = CodeType.getCodeType(injector.getCodetype());
        List<Integer> activeLeds = injectorControllersState.getArrayNumbersOfActiveLedToggleButtons();

        switch (producer.getManufacturerName()) {
            case "Bosch":
                switch (codetype) {
                    case ZERO:
                        coder = new BoschCoderZero(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case ONE:
                        coder = new BoschCoderOne(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case TWO:
                        coder = new BoschCoderTwo(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case THREE:
                        coder = new BoschCoderThree(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case FOUR:
                        coder = new BoschCoderFour(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case FIVE:
                        coder = new BoschCoderFive(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case SIX:
                        coder = new BoschCoderSix(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case IMA11_1:
                        coder = new BoschCoderIMA_111(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case IMA11_2:
                        coder = new BoschCoderIMA_112(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case IMA11_3:
                        coder = new BoschCoderIMA_113(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case IMA11_4:
                        coder = new BoschCoderIMA_114(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case SEVENTEEN:
                        coder = new BoschCoder_17(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    default:
                        throw new IllegalArgumentException("Bosch code type is undefined! Coding impossible.");
                }
                break;
            case "Siemens":
                switch (codetype) {
                    case TWO:
                        coder = new SiemensCoderTwo(activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case ONE:
                        coder = new SiemensCoderOne(activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    default:
                        throw new IllegalArgumentException("Siemens code type is undefined! Coding impossible.");
                }
                break;
            case "Denso":
                coder = new DensoCoder(mainSectionModel, activeLeds, flowReportModel, coilOnePulseParametersModel);
                break;
            case "Delphi":
                switch (codetype){
                    case ONE:
                        coder = new DelphiC2ICoder(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case TWO:
                        coder = new DelphiC3ICoder(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    case THREE:
                        coder = new DelphiC4ICoder(injector, activeLeds, codingReportModel, flowReportModel.getResultObservableMap());
                        break;
                    default:
                        throw new IllegalArgumentException("Delphi code type is undefined! Coding impossible.");
                }
                break;
                default:
                    throw new IllegalArgumentException("Producer is unknown, coding is impossible.");
        }
        return coder;
    }
}
