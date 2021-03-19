package fi.stardex.sisu.coding;

import fi.stardex.sisu.coding.bosch.*;
import fi.stardex.sisu.coding.delphi.DelphiC2ICoder;
import fi.stardex.sisu.coding.delphi.DelphiC3ICoder;
import fi.stardex.sisu.coding.delphi.DelphiC4ICoder;
import fi.stardex.sisu.coding.siemens.SiemensCoderOne;
import fi.stardex.sisu.coding.siemens.SiemensCoderTwo;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Producer;
import fi.stardex.sisu.states.InjectorControllersState;

import java.util.List;

public class CoderFactory {

    private FlowReportModel flowReportModel;
    private CodingReportModel codingReportModel;
    private InjectorControllersState injectorControllersState;

    public CoderFactory(FlowReportModel flowReportModel,
                        CodingReportModel codingReportModel,
                        InjectorControllersState injectorControllersState) {

        this.flowReportModel = flowReportModel;
        this.codingReportModel = codingReportModel;
        this.injectorControllersState = injectorControllersState;
    }

    public Coder getCoder(Injector injector) {

        Producer producer = injector.getManufacturer();
        CodeType codetype = CodeType.getCodeType(injector.getCodetype());
        List<Integer> activeLeds = injectorControllersState.getArrayNumbersOfActiveLedToggleButtons();
        List<Result> resultsList = codingReportModel.getResultsList();

        switch (producer.getManufacturerName()) {
            case "Bosch":
                switch (codetype) {
                    case ZERO:
                        return new BoschCoderZero(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case ONE:
                        return new BoschCoderOne(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case TWO:
                        return new BoschCoderTwo(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case THREE:
                        return new BoschCoderThree(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case FOUR:
                        return new BoschCoderFour(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case FIVE:
                        return new BoschCoderFive(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case SIX:
                        return new BoschCoderSix(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case IMA11_1:
                        return new BoschCoderIMA_111(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case IMA11_2:
                        return new BoschCoderIMA_112(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case IMA11_3:
                        return new BoschCoderIMA_113(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case IMA11_4:
                        return new BoschCoderIMA_114(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case SEVENTEEN:
                        return new BoschCoder_17(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    default:
                        throw new IllegalArgumentException("Bosch code type is undefined! Coding impossible.");
                }
            case "Siemens":
                switch (codetype) {
                    case TWO:
                        return new SiemensCoderTwo(activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case ONE:
                        return new SiemensCoderOne(activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    default:
                        throw new IllegalArgumentException("Siemens code type is undefined! Coding impossible.");
                }
            case "Denso":
                throw new IllegalArgumentException("Denso coder is undefined! Coding impossible.");
            case "Delphi":
                switch (injector.getCodetype()){
                    case 1:
                        return new DelphiC2ICoder(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case 2:
                        return new DelphiC3ICoder(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    case 3:
                        return new DelphiC4ICoder(injector, activeLeds, resultsList, flowReportModel.getResultObservableMap());
                    default:
                        throw new IllegalArgumentException("Delphi code type is undefined! Coding impossible.");
                }
            default:
                throw new IllegalArgumentException("Producer is undefined! Coding impossible.");
        }
    }
}
