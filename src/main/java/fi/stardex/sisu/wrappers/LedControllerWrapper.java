package fi.stardex.sisu.wrappers;

import fi.stardex.sisu.ui.controllers.additional.LedController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LedControllerWrapper {
    private List<LedController> ledControllers;

    public LedControllerWrapper(List<LedController> ledControllers) {
        this.ledControllers = ledControllers;
    }

    public void startBlinking() {
        ledControllers.forEach(LedController::ledBlinkStart);
    }

    public void stopBlinking() {
        ledControllers.forEach(LedController::ledBlinkStop);
    }

    public List<LedController> activeControllers() {
        //FIXME: в режиме мультиченнел этот метод вызывается дважды при выборе от двух до 4 ледов
        List<LedController> result = new ArrayList<>();
        for (LedController s: ledControllers) {
            if(s.isSelected()) result.add(s);
        }
        result.sort(Comparator.comparingInt(LedController::getNumber));
        System.err.println("activeControllers: " + result);
        return result;
    }


    public boolean isAtLeastOneActive() {
        return ledControllers.size() != 0;
    }
}
