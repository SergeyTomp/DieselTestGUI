package fi.stardex.sisu.coding.delphi.c2i;

import fi.stardex.sisu.pdf.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static fi.stardex.sisu.coding.delphi.c2i.DelphiC2ICoefficient.getDelphiC2ICoefficient;
import static fi.stardex.sisu.coding.delphi.c2i.DelphiC2ICodingDataStorage.*;
import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class DelphiC2ICoding {

    private static Logger logger = LoggerFactory.getLogger(DelphiC2ICoding.class);

    private static int coefficient;

    public static List<String> calculate(List<Integer> activeLEDs, List<Result> codes) {

        coefficient = getInjector().getCoefficient();

        List<String> resultList = new LinkedList<>();

        if (codes.isEmpty()) {
            resultList.add("");
            resultList.add("");
            resultList.add("");
            resultList.add("");
        }else{
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

    private static String makeResultString(Map<String, Integer> data) {

        StringBuilder resultString = new StringBuilder();

        resultString
                .append(completeBinaryWithZeroes(coefficient, 3))
                .append(completeBinaryWithZeroes(16, 5));

        logger.info("1. resultStr: {}", resultString);

        resultString
                .append(completeBinaryWithZeroes(data.get("Test Point 08"), getDelphiC2ICoefficient("Test Point 08")))
                .append(completeBinaryWithZeroes(data.get("Test Point 07"), getDelphiC2ICoefficient("Test Point 07")))
                .append(completeBinaryWithZeroes(data.get("Test Point 06"), getDelphiC2ICoefficient("Test Point 06")))
                .append(completeBinaryWithZeroes(data.get("Test Point 05"), getDelphiC2ICoefficient("Test Point 05")))
                .append(completeBinaryWithZeroes(data.get("Test Point 04"), getDelphiC2ICoefficient("Test Point 04")))
                .append(completeBinaryWithZeroes(data.get("Test Point 03"), getDelphiC2ICoefficient("Test Point 03")))
                .append(completeBinaryWithZeroes(data.get("Test Point 02"), getDelphiC2ICoefficient("Test Point 02")))
                .append(completeBinaryWithZeroes(data.get("Test Point 01"), getDelphiC2ICoefficient("Test Point 01")));

        logger.info("2. resultStr: {}", resultString);

        String temp = resultString.toString().substring(2);

        logger.info("3. temp: {}", temp);

        List<String> hexList = new ArrayList<>();

        for (int j = 0; j < 56; j += 8)
            hexList.add(intToHex(Integer.valueOf(temp.substring(j, j + 8), 2)));

        logger.info("4. hexList: {}", hexList);

        String hexCode = null;

        int checkSum = 0;

        switch (coefficient) {

            case 0:
                for (int j = hexList.size() - 1; j > -1; j--)
                    checkSum += Integer.valueOf(hexList.get(j), 16) * (j + 1);
                checkSum = (checkSum % 64) * 4;
                logger.info("5. checkSum: {}", checkSum);
                hexCode = addHexLines(hexList, checkSum);
                break;
            case 1:
                checkSum = (int) (61 - (Long.valueOf("001" + temp.substring(1, 56), 2)) % 61) * 4;
                logger.info("6. checkSum: {}", checkSum);
                hexCode = addHexLines(hexList, checkSum);
                break;

        }

        logger.info("7. Final code: {}", hexCode);

        return hexCode;

    }

    private static String completeBinaryWithZeroes(int value, int size) {

        StringBuilder result = new StringBuilder(Integer.toBinaryString(value));

        for (int i = result.length(); i < size; i++) {
            result.insert(0, "0");
        }

        return result.toString();

    }

    private static String intToHex(int number) {

        String hex = Integer.toHexString(number);

        if (hex.length() == 1)
            hex = "0" + hex;

        return hex.substring(hex.length() - 2).toUpperCase();

    }

    private static String addHexLines(List<String> hexList, int checkSum) {

        StringBuilder hexCheckSum = new StringBuilder(intToHex(checkSum));

        for (String hexLine : hexList)
            hexCheckSum.append(hexLine);

        return hexCheckSum.toString();

    }

}
