package fi.stardex.sisu.coding.delphi.c4i;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import static fi.stardex.sisu.coding.delphi.c4i.DelphiC4ICodingDataStorage.*;

public class DelphiC4ICoding {

    private static Logger logger = LoggerFactory.getLogger(DelphiC4ICoding.class);

    private static String ALPHABET = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";
    private static Map<Integer, Converter> converterMap = new HashMap<>();
    private static int coefficient;

    static {
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
    public static List<String> calculate(List<Integer> activeLEDs, List<Result> codes, Injector injector) {

        coefficient = injector.getCoefficient();
        List<String> resultList = new LinkedList<>();

        if (codes.isEmpty()) {
            resultList.add("");
            resultList.add("");
            resultList.add("");
            resultList.add("");
        } else {

            resultList.add(codes.get(0) != null ? codes.get(0).getSubColumn1() : "");
            resultList.add(codes.get(1) != null ? codes.get(1).getSubColumn1() : "");
            resultList.add(codes.get(2) != null ? codes.get(2).getSubColumn1() : "");
            resultList.add(codes.get(3) != null ? codes.get(3).getSubColumn1() : "");
        }

        if (activeLEDs.contains(1)) {
            Optional.ofNullable(getLed1DataStorage()).ifPresent(data -> resultList.set(0, makeResultString(data)));
        }
        if (activeLEDs.contains(2)) {
            Optional.ofNullable(getLed2DataStorage()).ifPresent(data -> resultList.set(1, makeResultString(data)));
        }
        if (activeLEDs.contains(3)) {
            Optional.ofNullable(getLed3DataStorage()).ifPresent(data -> resultList.set(2, makeResultString(data)));
        }
        if (activeLEDs.contains(4)) {
            Optional.ofNullable(getLed4DataStorage()).ifPresent(data -> resultList.set(3, makeResultString(data)));
        }
        coefficient = 0;
        return resultList;
    }

    private static String[] initializeArray() {

        String [] initData = new String[converterMap.size()];
        IntStream.rangeClosed(1, 19).forEach(i -> converterMap.get(i).convert(0, initData));
        converterMap.get(20).convert(4, initData);
        converterMap.get(21).convert(getCheckSum(concatParts(initData)), initData);
        return initData;
    }

    private static String makeResultString(Map<String, Double> deltas) {

        final String [] results = initializeArray();
        logger.error("0. initial data: {}", Arrays.toString(results));

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

    private static String getBinaryString(String[] data) {

        converterMap.get(20).convert(coefficient, data);
        logger.error("2. CSType appended: {}", Arrays.toString(data));

        converterMap.get(21).convert(getCheckSum(concatParts(data)), data);
        logger.error("2. ChkSum appended: {}", Arrays.toString(data));

        StringBuilder binary = new StringBuilder();
        Arrays.stream(data).forEach(binary::append);

        return binary.toString();
    }

    private static String concatParts(String[] data) {
        StringBuilder binary = new StringBuilder();
        IntStream.rangeClosed(1, converterMap.size() - 1).forEach(i -> binary.append(data[i]));
        return binary.toString();
    }

    private static int getCheckSum(String data) {

        int m = 1, checksum = 0;

        for (int i = 93; i >= 0; i--) {
            checksum += data.charAt(i) == '1' ? m : 0;
            checksum %= 59;
            m = (m * 2) % 59;
        }
        return 59 - checksum;
    }

    private static class Converter{

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
