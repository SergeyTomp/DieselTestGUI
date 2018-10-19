package fi.stardex.sisu.pumps.tests;


import fi.stardex.sisu.persistence.orm.Test;
import fi.stardex.sisu.pumps.Command;
import fi.stardex.sisu.pumps.CurrentPumpObtainer;
import fi.stardex.sisu.pumps.scv.CurrentSCVObtainer;
import fi.stardex.sisu.ui.Enabler;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.cr.HighPressureSectionController;
import fi.stardex.sisu.ui.controllers.cr.TestBenchSectionController;
import fi.stardex.sisu.util.wrappers.TimeProgressBarWrapper;

import java.util.List;

public abstract class ExtendedPumpTest implements Test, Command, Comparable<ExtendedPumpTest> {


    protected HighPressureSectionController highPressureSectionController;


    protected AdditionalSectionController additionalSectionController;


    protected TestBenchSectionController testBenchSectionController;


    protected TimeProgressBarWrapper measuringTime;


    protected TimeProgressBarWrapper adjustingTime;


    protected CurrentSCVObtainer currentSCVObtainer;


    protected CurrentPumpObtainer currentPumpObtainer;


//    protected Beakers beakers;


    protected Enabler enabler;

    protected abstract void withTiming();

    public abstract boolean skipped();

    @Override
    public void execute() {
        withTiming();
    }

    @Override
    public int compareTo(ExtendedPumpTest o) {
        return getClass().getName().compareTo(o.getClass().getName());
    }

    public static String listToString(List<?> list) {
        StringBuilder stringBuilder = new StringBuilder();
        list.forEach(t -> stringBuilder.append(String.valueOf(t)).append("\n"));
        return stringBuilder.toString();
    }

}
