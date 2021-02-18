package fi.stardex.sisu.coding.delphi.c4i;

import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static fi.stardex.sisu.coding.delphi.c4i.DelphiC4ICodingDataStorage.*;

public class DelphiC4ICoding {

    private static int coefficient;
    private static Logger logger = LoggerFactory.getLogger(DelphiC4ICoding.class);
    private static String ALPHABET = "0123456789ABCDEFGHJKLMNPRSTUWXYZ";
    private static final int[] BITS = {6, 3, 4, 6, 4, 4, 4, 6, 5, 5, 3, 4, 4, 4, 4, 7, 6, 5, 5, 5, 6};
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

    private static String makeResultString(Map<String, Double> deltas) {

        final List<Double> values = deltas.entrySet().stream()
                .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        int[] data = generateArray(values);

        logger.error("1. data: {}", Arrays.toString(data));

        final StringBuilder result = new StringBuilder();
        final String binaryString = getBinaryString(data);

        for (int i = 0; i < binaryString.length(); i += 5) {
            int index = Integer.parseInt(binaryString.substring(i, i + 5), 2);
            result.append(ALPHABET.charAt(index));
        }
        logger.error("6. result: {}", result.toString());
        return result.toString();
    }

    private static int[] generateArray(List<Double> values){

        int[] data = new int[21];

        return data;
    }


    private static String getBinaryString(int[] data) {

        StringBuilder binary = new StringBuilder();
        binary.append(intToString(coefficient, 3));

        logger.error("2. appended: {}", binary.toString());

        binary.append(intToString(16, 5));

        logger.error("3. appended: {}", binary.toString());

        for (int i = 2; i <= 16; i++)
            binary.append(intToString(data[i - 2], BITS[i]));

        logger.error("4. appended: {}", binary.toString());
        return appendCheckSum(binary.toString());
    }

    private static String appendCheckSum(String binary) {

        StringBuilder appended = new StringBuilder();

        int m = 1, checksum = 0;

        for (int i = 93; i > 0; i--) {
            checksum += binary.charAt(i) == '1' ? m : 0;
            checksum %= 59;
            m = (m * 2) % 59;
        }

        checksum = 59 - checksum;
        appended.append(intToString(checksum, 6)).append(binary);

        logger.error("5. appended: {}", appended.toString());
        return appended.toString();
    }

    private static String intToString(int data, int size) {

        StringBuilder convert = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            int mask = 1 << i;
            convert.append((data & mask) != 0 ? "1" : "0");
        }
        return convert.toString();
    }
}
