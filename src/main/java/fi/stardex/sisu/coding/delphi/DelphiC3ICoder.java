package fi.stardex.sisu.coding.delphi;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DelphiC3ICoder extends DelphiCoder {


    private static Logger logger = LoggerFactory.getLogger(DelphiC3ICoder.class);
    private final int[] BITS = {3, 5, 4, 5, 8, 5, 7, 5, 6, 6, 6, 5, 6, 6, 6, 5, 6};

    public DelphiC3ICoder(Injector injector,
                          List<Integer> activeLEDs,
                          List<Result> oldCodes,
                          ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        super(oldCodes);
        super.activeLEDs = activeLEDs;
        super.injectorCoefficient = injector.getCoefficient();
        super.mapOfFlowTestResults = mapOfFlowTestResults;
    }

    @Override
    public List<String> buildCode() {

        Map<InjectorTest, List<Double>> temp = getSourceMap(mapOfFlowTestResults);

        activeLEDs.forEach(i -> {
            int[] ledData = new int[15];
            temp.forEach((test,value) -> insertCodeValue(test, value.get(i - 1), ledData));
            previousResultList.set(i - 1, makeResultString(ledData));
        });
        return previousResultList;
    }

    private void completeData(int[] data) {

        data[2] = 63;
        data[3] = 17;
        data[4] = 15;
        data[5] = 19;
        data[7] = calculateUnsignedCodeValue(data[7] / 2, 6);
        data[8] = calculateUnsignedCodeValue(data[8] / 2, 6);
        data[11] = calculateUnsignedCodeValue(data[11] / 2, 6);
        data[12] = calculateUnsignedCodeValue(data[12] / 2, 6);
    }

    private String makeResultString(int[] data) {

        completeData(data);
        logger.error("1. data: {}", Arrays.toString(data));

        StringBuilder result = new StringBuilder();
        String binaryString = getBinaryString(data);

        for (int i = 0; i < binaryString.length(); i += 5) {
            int index = Integer.parseInt(binaryString.substring(i, i + 5), 2);
            result.append(ALPHABET.charAt(index));
        }
        logger.error("6. result: {}", result.toString());
        return result.toString();
    }

    private String getBinaryString(int[] data) {

        StringBuilder binary = new StringBuilder();
        binary.append(intToString(injectorCoefficient, 3));
        logger.error("2. appended: {}", binary.toString());

        binary.append(intToString(16, 5));
        logger.error("3. appended: {}", binary.toString());

        for (int i = 2; i <= 16; i++){
            binary.append(intToString(data[i - 2], BITS[i]));
        }

        logger.error("4. appended: {}", binary.toString());
        return appendCheckSum(binary.toString());
    }

    private String appendCheckSum(String binary) {

        StringBuilder appended = new StringBuilder();

        int m = 1, checksum = 0;
        for (int i = 93; i >= 0; i--) {
            checksum += binary.charAt(i) == '1' ? m : 0;
            checksum %= 59;
            m = (m * 2) % 59;
        }
        checksum = 59 - checksum;

        appended.append(intToString(checksum, 6)).append(binary);
        logger.error("5. appended: {}", appended.toString());
        return appended.toString();
    }

    private String intToString(int coefficient, int size) {

        StringBuilder convert = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            int mask = 1 << i;
            convert.append((coefficient & mask) != 0 ? "1" : "0");
        }
        return convert.toString();
    }

    private void insertCodeValue(InjectorTest injectorTest, double flow, int[] data) {

        String testName = injectorTest.getTestName().toString();
        int power;

        switch (testName) {

            case TP01:
                power = 5;
                data[13] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP02:
                power = 6;
                data[10] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP03:
                power = 6;
                data[14] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP04:
            case TP05:
                power = 6;
                data[12] += calculateCodeValue(injectorTest, flow, power);
                break;
            case TP06:
            case TP07:
                power = 6;
                data[11] += calculateCodeValue(injectorTest, flow, power);
                break;
            case TP08:
                power = 5;
                data[9] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP09:
                power = 6;
                data[6] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP10:
            case TP11:
                power = 6;
                data[8] += calculateCodeValue(injectorTest, flow, power);
                break;
            case TP12:
            case TP13:
                power = 6;
                data[7] += calculateCodeValue(injectorTest, flow, power);
                break;
            case TP14:
                power = 5;
                data[1] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
            case TP15:
                power = 4;
                data[0] = calculateUnsignedCodeValue(calculateCodeValue(injectorTest, flow, power), power);
                break;
        }
    }

    private int calculateUnsignedCodeValue(int coefficient, int power) {
        return coefficient + (int) Math.pow(2, power - 1) - 1;
    }

    private int calculateCodeValue(InjectorTest injectorTest, double flow, int power) {

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
        return result / 3;
    }
}
