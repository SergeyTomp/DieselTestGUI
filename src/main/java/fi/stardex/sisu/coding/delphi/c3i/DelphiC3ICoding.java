package fi.stardex.sisu.coding.delphi.c3i;

import fi.stardex.sisu.pdf.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static fi.stardex.sisu.coding.delphi.c3i.DelphiC3ICodingDataHandler.calculateUnsignedCoefficient;
import static fi.stardex.sisu.coding.delphi.c3i.DelphiC3ICodingDataStorage.*;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class DelphiC3ICoding {

    private static Logger logger = LoggerFactory.getLogger(DelphiC3ICoding.class);

    private static final char[] ALPHABET = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'};

    private static final int[] OFFSETS = {3, 5, 4, 5, 8, 5, 7, 5, 6, 6, 6, 5, 6, 6, 6, 5, 6};

    public static List<String> calculate(List<Integer> activeLEDs, List<Result> codes) {

        List<String> resultList = new LinkedList<>();

        if (codes.isEmpty()) {
            resultList.add("");
            resultList.add("");
            resultList.add("");
            resultList.add("");
        } else {

            resultList.add(codes.get(0) != null ? codes.get(0).getSubColumn1() : "" );
            resultList.add(codes.get(1) != null ? codes.get(1).getSubColumn1() : "" );
            resultList.add(codes.get(2) != null ? codes.get(2).getSubColumn1() : "" );
            resultList.add(codes.get(3) != null ? codes.get(3).getSubColumn1() : "" );
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

        return resultList;
    }

    private static String makeResultString(int[] data) {

        completeData(data);

        logger.error("1. data: {}", Arrays.toString(data));

        StringBuilder result = new StringBuilder();

        String binaryString = getBinaryString(data);

        for (int i = 0; i < binaryString.length(); i += 5) {
            int index = Integer.parseInt(binaryString.substring(i, i + 5), 2);
            result.append(ALPHABET[index]);
        }

        logger.error("6. result: {}", result.toString());

        return result.toString();

    }

    private static void completeData(int[] data) {

        data[7] = calculateUnsignedCoefficient(data[7] / 2, 6);
        data[8] = calculateUnsignedCoefficient(data[8] / 2, 6);
        data[11] = calculateUnsignedCoefficient(data[11] / 2, 6);
        data[12] = calculateUnsignedCoefficient(data[12] / 2, 6);

    }

    private static String getBinaryString(int[] data) {

        StringBuilder binary = new StringBuilder();

        binary.append(intToString(getInjector().getCoefficient(), 3));

        logger.error("2. appended: {}", binary.toString());

        binary.append(intToString(16, 5));

        logger.error("3. appended: {}", binary.toString());

        for (int i = 2; i <= 16; i++)
            binary.append(intToString(data[i - 2], OFFSETS[i]));

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

    private static String intToString(int coefficient, int size) {

        StringBuilder convert = new StringBuilder();

        for (int i = size - 1; i >= 0; i--) {
            int mask = 1 << i;
            convert.append((coefficient & mask) != 0 ? "1" : "0");
        }

        return convert.toString();

    }

}
