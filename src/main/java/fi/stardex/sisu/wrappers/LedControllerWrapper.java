package fi.stardex.sisu.wrappers;

import fi.stardex.sisu.ui.controllers.additional.LedController;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LedControllerWrapper {
//    private ObservableList<LedController> ledControllers;
//
//    public LedControllerWrapper(ObservableList<LedController> ledControllers) {
//        this.ledControllers = ledControllers;
//    }
//
//    public void startBlinking() {
//        ledControllers.forEach(LedController::ledBlinkStart);
//    }
//
//    public void stopBlinking() {
//        ledControllers.forEach(LedController::ledBlinkStop);
//    }
//
//    public List<LedController> activeControllers() {
//        List<LedController> result = new ArrayList<>();
//        for (LedController s: ledControllers) {
//            if(s.isSelected()) result.add(s);
//        }
//        result.sort(Comparator.comparingInt(LedController::getNumber));
//        System.err.println(result);
//        return result;
//    }
//
//    public boolean isAtLeastOneActive() {
//        return ledControllers.size() != 0;
//    }
//
//    public ObservableList<LedController> getLedControllers() {
//        return ledControllers;
//    }
}
