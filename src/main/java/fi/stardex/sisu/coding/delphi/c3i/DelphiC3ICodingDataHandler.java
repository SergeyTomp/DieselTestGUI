package fi.stardex.sisu.coding.delphi.c3i;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;

class DelphiC3ICodingDataHandler {

    static void storeCoefficient(InjectorTest injectorTest, double flow, int[] data) {

        String testName = injectorTest.getTestName().toString();

        int power;

        switch (testName) {

            case "Test Point 01":
                power = 5;
                data[13] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 02":
                power = 6;
                data[10] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 03":
                power = 6;
                data[14] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 04":
            case "Test Point 05":
                power = 6;
                data[12] += calculateCoefficient(injectorTest, flow, power);
                break;
            case "Test Point 06":
            case "Test Point 07":
                power = 6;
                data[11] += calculateCoefficient(injectorTest, flow, power);
                break;
            case "Test Point 08":
                power = 5;
                data[9] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 09":
                power = 6;
                data[6] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 10":
            case "Test Point 11":
                power = 6;
                data[8] += calculateCoefficient(injectorTest, flow, power);
                break;
            case "Test Point 12":
            case "Test Point 13":
                power = 6;
                data[7] += calculateCoefficient(injectorTest, flow, power);
                break;
            case "Test Point 14":
                power = 5;
                data[1] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;
            case "Test Point 15":
                power = 4;
                data[0] = calculateUnsignedCoefficient(calculateCoefficient(injectorTest, flow, power), power);
                break;

        }

    }

    static int calculateUnsignedCoefficient(int coefficient, int power) {

        return coefficient + (int) Math.pow(2, power - 1) - 1;

    }

    private static int calculateCoefficient(InjectorTest injectorTest, double flow, int power) {

        double nominalFlow = injectorTest.getNominalFlow();

        double flowRange = injectorTest.getFlowRange();

        double range = nominalFlow * flowRange / 100;

        int offset = (int) Math.pow(2, power - 1);

        int result = (int)((nominalFlow - flow) * offset / range);

        if (result > offset) {
            result = offset;
        } else if (result < -offset) {
            result = -offset;
        }
//        else if(result == 0)
//            result = -1;

        return result / 3;

    }

}
