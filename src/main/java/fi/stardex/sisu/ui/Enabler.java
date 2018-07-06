package fi.stardex.sisu.ui;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.enums.Tests;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;

public class Enabler {

    private CurrentInjectorObtainer currentInjectorObtainer;

    private RadioButton manualTestRadioButton;

    private RadioButton testPlanTestRadioButton;

    private RadioButton autoTestRadioButton;

    private RadioButton codingTestRadioButton;

    private ListView<InjectorTest> testListView;

    public Enabler(MainSectionController mainSectionController, CurrentInjectorObtainer currentInjectorObtainer) {
        this.currentInjectorObtainer = currentInjectorObtainer;
        manualTestRadioButton = mainSectionController.getManualTestRadioButton();
        testPlanTestRadioButton = mainSectionController.getTestPlanTestRadioButton();
        autoTestRadioButton = mainSectionController.getAutoTestRadioButton();
        codingTestRadioButton = mainSectionController.getCodingTestRadioButton();
        testListView = mainSectionController.getTestListView();
    }

    public Enabler selectInjector(boolean selected) {

        if (selected) {
            testPlanTestRadioButton.setDisable(false);
            autoTestRadioButton.setDisable(false);
            codingTestRadioButton.setDisable(false);
            if (!manualTestRadioButton.isSelected())
                fillTestListView();
        } else {
            testPlanTestRadioButton.setDisable(true);
            autoTestRadioButton.setDisable(true);
            codingTestRadioButton.setDisable(true);
            manualTestRadioButton.setSelected(true);
            testListView.getItems().clear();
        }

        return this;
    }

    public Enabler selectTest(Tests test) {
        switch (test) {
            case MANUAL:
                testListView.setDisable(false);
                testListView.getItems().clear();
                break;
            case TESTPLAN:
                testListView.setDisable(false);
                fillTestListView();
                break;
            case AUTO:
                testListView.setDisable(true);
                fillTestListView();
                break;
            case CODING:
                testListView.setDisable(true);
                fillTestListView();
                break;
            default:
                break;
        }
        return this;
    }

    private void fillTestListView() {
        testListView.getItems().setAll(currentInjectorObtainer.getInjector().getInjectorTests());
        testListView.getSelectionModel().select(0);
        testListView.scrollTo(0);
    }

}
