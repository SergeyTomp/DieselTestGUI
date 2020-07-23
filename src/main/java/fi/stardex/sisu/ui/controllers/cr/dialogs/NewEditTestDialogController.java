package fi.stardex.sisu.ui.controllers.cr.dialogs;

import fi.stardex.sisu.model.cr.MainSectionModel;
import fi.stardex.sisu.model.cr.NewEditTestDialogModel;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.TestNamesRepository;
import fi.stardex.sisu.util.converters.DataConverter;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class NewEditTestDialogController {

    @FXML private Label rpmLabel;
    @FXML private Label widthLabel;
    @FXML private Label nominalFlowLabel;
    @FXML private Label adjustingTimeLabel;
    @FXML private Label barLabel;
    @FXML private Label frquencyLabel;
    @FXML private Label flowRangeLabel;
    @FXML private Label measurementTimeLabel;
    @FXML private Label selectTestLabel;
    @FXML private ComboBox<TestName> testComboBox;
    @FXML private TextField barTF;
    @FXML private TextField rpmTF;
    @FXML private TextField widthTF;
    @FXML private TextField freqTF;
    @FXML private TextField nominalTF;
    @FXML private TextField flowRangeTF;
    @FXML private TextField adjTimeTF;
    @FXML private TextField measureTimeTF;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private List<TestName> testNames = new LinkedList<>();
    private InjectorTestRepository injectorTestRepository;
    private TestNamesRepository testNamesRepository;
    private Stage stage;
    private ListView<InjectorTest> testListView;
    private State currentState;
    private MainSectionModel mainSectionModel;
    private NewEditTestDialogModel newEditTestDialogModel;
    private I18N i18N;

    public void setInjectorTestRepository(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }
    public void setTestNamesRepository(TestNamesRepository testNamesRepository) {
        this.testNamesRepository = testNamesRepository;
    }
    public void setMainSectionModel(MainSectionModel mainSectionModel) {
        this.mainSectionModel = mainSectionModel;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setTestListView(ListView<InjectorTest> testListView) {
        this.testListView = testListView;
    }
    public void setNewEditTestDialogModel(NewEditTestDialogModel newEditTestDialogModel) {
        this.newEditTestDialogModel = newEditTestDialogModel;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    @PostConstruct
    private void init() {
        cancelBtn.setOnMouseClicked(event -> stage.close());
        saveBtn.setOnMouseClicked(event -> {
            switch (currentState) {
                case NEW:
                    createAndSave();
                    break;
                case EDIT:
                    editAndSave();
                    break;
                case DELETE:
                    deleteTest();
                    break;
            }
        });
        testNamesRepository.findAll().forEach(testName -> testNames.add(testName));
        bindingI18N();
    }

    private void createAndSave() {
        String frequency = String.valueOf(Math.round(1000 / DataConverter.convertDataToFloat(freqTF.getText())));
        InjectorTest injectorTest = new InjectorTest(getInjector(), testComboBox.getSelectionModel().getSelectedItem(),
                Integer.valueOf(rpmTF.getText()), Integer.valueOf(barTF.getText()), Integer.valueOf(adjTimeTF.getText()), Integer.valueOf(measureTimeTF.getText()),
                Integer.valueOf(frequency), Integer.valueOf(widthTF.getText()), Double.valueOf(nominalTF.getText()), Double.valueOf(flowRangeTF.getText()),
                0, 0);

        injectorTestRepository.save(injectorTest);
        newEditTestDialogModel.customTestProperty().setValue(injectorTest);
        newEditTestDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    private void editAndSave() {

        InjectorTest injectorTest = mainSectionModel.injectorTestProperty().get();
        Injector injector = mainSectionModel.injectorProperty().get();
        String frequency = String.valueOf(Math.round(1000 / DataConverter.convertDataToFloat(freqTF.getText())));

        injectorTest.setInjector(injector);
        injectorTest.setTestName(testComboBox.getSelectionModel().getSelectedItem());
        injectorTest.setMotorSpeed(Integer.valueOf(rpmTF.getText()));
        injectorTest.setSettedPressure( Integer.valueOf(barTF.getText()));
        injectorTest.setAdjustingTime(Integer.valueOf(adjTimeTF.getText()));
        injectorTest.setMeasurementTime(Integer.valueOf(measureTimeTF.getText()));
        injectorTest.setInjectionRate(Integer.valueOf(frequency));
        injectorTest.setTotalPulseTime(Integer.valueOf(widthTF.getText()));
        injectorTest.setNominalFlow(Double.valueOf(nominalTF.getText()));
        injectorTest.setFlowRange(Double.valueOf(flowRangeTF.getText()));
        injectorTest.setTotalPulseTime2(0);
        injectorTest.setShift(0);

        injectorTestRepository.save(injectorTest);
        newEditTestDialogModel.customTestProperty().setValue(injectorTest);
        newEditTestDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    private void deleteTest() {
        InjectorTest currentTest = testListView.getSelectionModel().getSelectedItem();
        injectorTestRepository.delete(currentTest);
        newEditTestDialogModel.customTestProperty().setValue(null);
        newEditTestDialogModel.doneProperty().setValue(new Object());
        stage.close();
    }

    public void setNew() {
        currentState = State.NEW;
        setLabels();
    }

    public void setEdit() {
        currentState = State.EDIT;
        setLabels();
    }

    public void setDelete() {
        currentState = State.DELETE;
        setLabels();
    }

    private void setLabels() {

        rpmTF.setDisable(false);
        barTF.setDisable(false);
        widthTF.setDisable(false);
        freqTF.setDisable(false);
        nominalTF.setDisable(false);
        flowRangeTF.setDisable(false);
        adjTimeTF.setDisable(false);
        measureTimeTF.setDisable(false);

        if (currentState == State.NEW) {
            testComboBox.setDisable(false);
            testComboBox.getItems().setAll(testNames);

            List<InjectorTest> injectorTests = injectorTestRepository.findAllByInjector(getInjector());

            if (injectorTests != null)
                injectorTests.forEach(injectorTest -> testComboBox.getItems().remove(injectorTest.getTestName()));

            testComboBox.getSelectionModel().clearSelection();
            rpmTF.setText("");
            barTF.setText("");
            widthTF.setText("");
            freqTF.setText("");
            nominalTF.setText("");
            flowRangeTF.setText("");
            adjTimeTF.setText("");
            measureTimeTF.setText("");
        } else {
            testComboBox.getItems().setAll(testNames);
            testComboBox.getSelectionModel().select(testListView.getSelectionModel().getSelectedItem().getTestName());
            testComboBox.setDisable(true);

            InjectorTest injectorTest = testListView.getSelectionModel().getSelectedItem();
            String frequency = String.valueOf((Math.round(100000f / injectorTest.getInjectionRate())) / 100);

            rpmTF.setText(injectorTest.getMotorSpeed().toString());
            barTF.setText(injectorTest.getSettedPressure().toString());
            widthTF.setText(injectorTest.getTotalPulseTime().toString());
            freqTF.setText(frequency);
            nominalTF.setText(injectorTest.getNominalFlow().toString());
            flowRangeTF.setText(injectorTest.getFlowRange().toString());
            adjTimeTF.setText(injectorTest.getAdjustingTime().toString());
            measureTimeTF.setText(injectorTest.getMeasurementTime().toString());
        }

        if (currentState == State.DELETE) {
            rpmTF.setDisable(true);
            barTF.setDisable(true);
            widthTF.setDisable(true);
            freqTF.setDisable(true);
            nominalTF.setDisable(true);
            flowRangeTF.setDisable(true);
            adjTimeTF.setDisable(true);
            measureTimeTF.setDisable(true);
        }
    }

    private enum State {
        NEW, EDIT, DELETE
    }

    private void bindingI18N(){

        rpmLabel.textProperty().bind(i18N.createStringBinding("h1.label.rpm"));
        widthLabel.textProperty().bind(i18N.createStringBinding("h3.label.width"));
        nominalFlowLabel.textProperty().bind(i18N.createStringBinding("editTestDialog.nominalFlow"));
        adjustingTimeLabel.textProperty().bind(i18N.createStringBinding("main.label.adjusting.time"));
        barLabel.textProperty().bind(i18N.createStringBinding("h4.report.table.label.pressure"));
        frquencyLabel.textProperty().bind(i18N.createStringBinding("editTestDialog.frequency"));
        flowRangeLabel.textProperty().bind(i18N.createStringBinding("editTestDialog.flowRange"));
        measurementTimeLabel.textProperty().bind(i18N.createStringBinding("main.label.measuring.time"));
        selectTestLabel.textProperty().bind(i18N.createStringBinding("editTestDialog.selectTest"));
        saveBtn.textProperty().bind(i18N.createStringBinding("h4.delay.button.save"));
        cancelBtn.textProperty().bind(i18N.createStringBinding("voapProfile.button.cancel"));
    }
}
