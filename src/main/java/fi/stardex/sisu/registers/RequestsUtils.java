package fi.stardex.sisu.registers;

import java.nio.ByteBuffer;

/**
 * @author rom8
 * @since 11.07.16
 */
public class RequestsUtils {

    private RequestsUtils() {
    }

    public static Double createDouble(short first, short second) {
        byte[] firstWord = new byte[2];
        byte[] secondWord = new byte[2];
        firstWord[0] = (byte) ((first >> 8) & 0xff);
        firstWord[1] = (byte) (first & 0xff);
        secondWord[0] = (byte) ((second >> 8) & 0xff);
        secondWord[1] = (byte) (second & 0xff);
        byte[] resultWord = new byte[firstWord.length + secondWord.length];
        System.arraycopy(firstWord, 0, resultWord, 0, firstWord.length);
        System.arraycopy(secondWord, 0, resultWord, firstWord.length, secondWord.length);
        return (double) ByteBuffer.wrap(resultWord).getFloat();
    }
}
