package fi.stardex.sisu.coding;

import fi.stardex.sisu.coding.bosch.*;
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
                        return new BoschCoderZero(injector, activeLeds, resultsList);
                    case ONE:
                        return new BoschCoderOne(injector, activeLeds, resultsList);
                    case TWO:
                        return new BoschCoderTwo(injector, activeLeds, resultsList);
                    case THREE:
                        return new BoschCoderThree(injector, activeLeds, resultsList);
                    case FOUR:
                        return new BoschCoderFour(injector, activeLeds, resultsList);
                    case FIVE:
                        return new BoschCoderFive(injector, activeLeds, resultsList);
                    case SIX:
                        return new BoschCoderSix(injector, activeLeds, resultsList);
                    case IMA11_1:
                        return new BoschCoderIMA_111(injector, activeLeds, resultsList);
                    case IMA11_2:
                        return new BoschCoderIMA_112(injector, activeLeds, resultsList);
                    case IMA11_3:
                        return new BoschCoderIMA_113(injector, activeLeds, resultsList);
                    case IMA11_4:
                        return new BoschCoderIMA_114(injector, activeLeds, resultsList);
                    default:
                        return null;
                }
            case "Siemens":
                switch (codetype) {
                    case TWO:
                        return new SiemensCoderTwo(activeLeds, resultsList);
                    case ONE:
                    default:
                        return new SiemensCoderOne(activeLeds, resultsList);
                }
            case "Denso":
            case "Delphi":
            default:
                return null;
        }
    }
}
