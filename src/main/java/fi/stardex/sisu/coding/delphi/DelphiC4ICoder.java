package fi.stardex.sisu.coding.delphi;

import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

public class DelphiC4ICoder extends DelphiCoder {

    private Logger logger = LoggerFactory.getLogger(DelphiC4ICoder.class);
    private String ALPHABET = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";
    private Map<Integer, Converter> converterMap = new HashMap<>();

    public DelphiC4ICoder(Injector injector, List<Integer> activeLEDs, List<Result> oldCodes) {
        super(oldCodes);
        super.activeLEDs = activeLEDs;
        super.injectorCoefficient = injector.getCoefficient();
        converterMap.put(21, new Converter( 0, 6, (cv, bit) -> cv));                                  // ChkSum
        converterMap.put(20, new Converter( 1, 3, (cv, bit) -> cv));                                  // CSType
        converterMap.put(19, new Converter( 2, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1)));      // N19
        converterMap.put(18, new Converter( 3, 6, (cv, bit) -> 0));                                   // N18(=0)
        converterMap.put(17, new Converter( 4, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));  // N17
        converterMap.put(16, new Converter( 5, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));  // N16
        converterMap.put(15, new Converter( 6, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1)));      // N15
        converterMap.put(14, new Converter( 7, 6, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));  // N14
        converterMap.put(13, new Converter( 8, 5, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));  // N13
        converterMap.put(12, new Converter( 9, 5, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));  // N12
        converterMap.put(11, new Converter( 10, 3, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1)); // N11
        converterMap.put(10, new Converter( 11, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1)); // N10
        converterMap.put(9, new Converter(12, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N09
        converterMap.put(8, new Converter(13, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N08
        converterMap.put(7, new Converter(14, 4, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N07
        converterMap.put(6, new Converter(15, 7, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N06
        converterMap.put(5, new Converter(16, 6, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N05
        converterMap.put(4, new Converter(17, 5, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N04
        converterMap.put(3, new Converter(18, 5, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N03
        converterMap.put(2, new Converter(19, 5, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N02
        converterMap.put(1, new Converter(20, 6, (cv, bit) -> cv + (int)Math.pow(2, bit - 1) - 1));   // N01
    }

    @Override
    public List<String> buildCode(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {

        Map<String, List<Double>> temp = getSourceMap(mapOfFlowTestResults);

        activeLEDs.forEach(i -> {
            Map<String,Double> ledData = new HashMap<>();
            temp.forEach((test,value) -> ledData.put(test, value.get(i - 1)));
            previousResultList.set(i - 1, makeResultString(ledData));
        });
        return previousResultList;
    }

    private Map<String, List<Double>> getSourceMap(ObservableMap<InjectorTest, FlowReportModel.FlowResult> mapOfFlowTestResults) {
        Map<String, List<Double>> temp = new HashMap<>();

        for (Map.Entry<InjectorTest, FlowReportModel.FlowResult> entry : mapOfFlowTestResults.entrySet()) {

            if (entry.getKey().getTestName().isTestPoint()) {

                FlowReportModel.FlowResult flowTestResult = entry.getValue();
                Double nominalFlow = entry.getKey().getNominalFlow();

                temp.put(entry.getKey().toString(), Arrays.asList(
                        flowTestResult.getDoubleValue_1() - nominalFlow,
                        flowTestResult.getDoubleValue_2() - nominalFlow,
                        flowTestResult.getDoubleValue_3() - nominalFlow,
                        flowTestResult.getDoubleValue_4() - nominalFlow));
            }
        }
        return temp;
    }

    private String makeResultString(Map<String, Double> deltas) {

        final String [] results = new String[converterMap.size()];
        final StringBuilder sb = new StringBuilder();
        converterMap.values().forEach(v -> {
            IntStream.rangeClosed(1, v.bit).forEach(i -> sb.append("0"));
            results[v.index] = sb.toString();
            sb.setLength(0);
        });

        deltas.forEach((k,v) -> {

            int nxx = ((int)Math.abs(v * 10) + Integer.parseInt(k.substring(k.length() - 2)) - 1) % 19 + 1;
            int codeValue = ((int)(Math.abs(v * 10) / 19) % 2) * 2 - 1;
            codeValue = v < 0 ? - codeValue : v == 0 ? 0 : codeValue;
            converterMap.get(nxx).convert(codeValue, results);
        });
        logger.error("1. data: {}", Arrays.toString(results));

        final StringBuilder result = new StringBuilder();
        final String binaryString = getBinaryString(results);

        for (int i = 0; i < binaryString.length(); i += 5) {
            int index = Integer.parseInt(binaryString.substring(i, i + 5), 2);
            result.append(ALPHABET.charAt(index));
        }
        logger.error("6. result: {}", result.toString());
        return result.toString();
    }


    private String getBinaryString(String[] data) {

        converterMap.get(20).convert(injectorCoefficient, data);
        logger.error("2. CSType appended: {}", Arrays.toString(data));

        StringBuilder binary = new StringBuilder();
        Arrays.stream(data).forEach(binary::append);
        converterMap.get(21).convert(getCheckSum(binary.toString()), data);
        logger.error("2. ChkSum appended: {}", Arrays.toString(data));

        binary.setLength(0);
        Arrays.stream(data).forEach(binary::append);

        return binary.toString();
    }

    private int getCheckSum(String data) {

        int m = 1, checksum = 0;

        for (int i = 93; i > 0; i--) {
            checksum += data.charAt(i) == '1' ? m : 0;
            checksum %= 59;
            m = (m * 2) % 59;
        }
        return 59 - checksum;
    }

    private class Converter{

        private int index;
        private int bit;
        private BinaryOperator<Integer> operator;

        private Converter(int index, int bit, BinaryOperator<Integer> operator) {
            this.index = index;
            this.bit = bit;
            this.operator = operator;
        }

        private void convert(int cv, String[] data) {

            cv = operator.apply(cv, bit);

            StringBuilder sb = new StringBuilder();

            for (int i = bit - 1; i >= 0; i--) {
                int mask = 1 << i;
                sb.append((cv & mask) != 0 ? "1" : "0");
            }
            data[index] = sb.toString();
        }
    }
}
