package fi.stardex.sisu.coding.delphi.c2i;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

class DelphiC2ICodingDataHandler {

    static int calculateCoefficient(InjectorTest injectorTest, double flow) {

        int range = (int) Math.pow(2, DelphiC2ICoefficient.getDelphiC2ICoefficient(injectorTest.getTestName().toString()) - 1);

        double nominalFlow = injectorTest.getNominalFlow();

        double flowRange = nominalFlow * (injectorTest.getFlowRange() / 100);

        double min = nominalFlow - flowRange;

        double max = nominalFlow + flowRange;

        int result;

        int roughDivisor = 2;

        if (flow < min)
            result = range * 2 - 1;
        else if (flow > max)
            result = 1;
        else {
            double deviation = Math.abs(nominalFlow - flow);
            result = nominalFlow >= flow ? (int) (-range * deviation / flowRange) + range
                    : (int) (range * deviation / flowRange) + range;
        }

        result = result / roughDivisor;

        return result;

    }

}
