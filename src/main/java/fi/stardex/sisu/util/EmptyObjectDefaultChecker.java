package fi.stardex.sisu.util;

/**
 * Created
 * by stardex
 * on 28.01.2016.
 */
public class EmptyObjectDefaultChecker {

    public static String getStringOrDefault(Object o, String def) {
        return (o == null || o.toString().equals("")) ? def : o.toString();
    }

}
