package fi.stardex.sisu.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created
 * by eduard
 * on 10.01.17.
 */
public class DelayCalculator {

    private final List<Double> delayValuesList = new ArrayList<>();

    public void addDelayValue(Double value) {
        delayValuesList.add(value);
    }

    public void clearDelayValuesList() {
        delayValuesList.clear();
    }

    public Double getMinimumDelay() {
        return Collections.min(delayValuesList);
    }

    public Double getMaximumDelay() {
        return Collections.max(delayValuesList);
    }

    public Double getAverageDelay() {
        Double sum = 0d;
        if (!delayValuesList.isEmpty()) {
            for (Double value : delayValuesList) {
                sum += value;
            }
            sum /= delayValuesList.size();
        }
        return sum;
    }
}