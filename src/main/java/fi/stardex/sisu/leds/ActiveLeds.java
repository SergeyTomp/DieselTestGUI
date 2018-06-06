package fi.stardex.sisu.leds;

import fi.stardex.sisu.ui.controllers.additional.LedController;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActiveLeds {

    private static ObservableList<LedController> ledControllers;

    public ActiveLeds(ObservableList<LedController> ledControllers) {
        ActiveLeds.ledControllers = ledControllers;
    }

    public static List<LedController> activeControllers() {
        List<LedController> result = new ArrayList<>();
        for (LedController s : ledControllers) {
            if (s.isSelected()) result.add(s);
        }
        result.sort(Comparator.comparingInt(LedController::getNumber));
        return result;
    }

    public static List<Integer> arrayNumbersOfActiveControllers() {
        List<Integer> active = new ArrayList<>();
        activeControllers().forEach(e -> active.add(e.getNumber()));
        return active;
    }
}
