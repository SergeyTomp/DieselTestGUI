package fi.stardex.sisu.ui.controllers.cr.dialogs;

import fi.stardex.sisu.persistence.orm.cr.inj.InjectorTest;
import fi.stardex.sisu.persistence.orm.cr.inj.TestName;
import fi.stardex.sisu.persistence.repos.cr.InjectorTestRepository;
import fi.stardex.sisu.persistence.repos.cr.TestNamesRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

import static fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer.getInjector;

public class NewEditTestDialogController {

    @FXML
    private ComboBox<TestName> testComboBox;
    @FXML
    private TextField barTF;
    @FXML
    private TextField rpmTF;
    @FXML
    private TextField widthTF;
    @FXML
    private TextField freqTF;
    @FXML
    private TextField nominalTF;
    @FXML
    private TextField flowRangeTF;
    @FXML
    private TextField adjTimeTF;
    @FXML
    private TextField measureTimeTF;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;

    private List<TestName> testNames = new LinkedList<>();

    private InjectorTestRepository injectorTestRepository;

    private TestNamesRepository testNamesRepository;

    private Stage stage;

    private ListView<InjectorTest> testListView;

    private State currentState;

    public void setInjectorTestRepository(InjectorTestRepository injectorTestRepository) {
        this.injectorTestRepository = injectorTestRepository;
    }

    public void setTestNamesRepository(TestNamesRepository testNamesRepository) {
        this.testNamesRepository = testNamesRepository;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTestListView(ListView<InjectorTest> testListView) {
        this.testListView = testListView;
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
                    break;
                case DELETE:
                    deleteTest();
                    break;
            }
        });
        testNamesRepository.findAll().forEach(testName -> testNames.add(testName));
    }

    //TODO freq and flowrange
    private void createAndSave() {
        InjectorTest injectorTest = new InjectorTest(getInjector(), testComboBox.getSelectionModel().getSelectedItem(),
                Integer.valueOf(rpmTF.getText()), Integer.valueOf(barTF.getText()), Integer.valueOf(adjTimeTF.getText()), Integer.valueOf(measureTimeTF.getText()),
                Integer.valueOf(freqTF.getText()), Integer.valueOf(widthTF.getText()), Double.valueOf(nominalTF.getText()), Double.valueOf(flowRangeTF.getText()),
                0, 0);

        injectorTestRepository.save(injectorTest);
        testListView.getItems().add(injectorTest);
        stage.close();
    }

    private void deleteTest() {
        InjectorTest currentTest = testListView.getSelectionModel().getSelectedItem();
        injectorTestRepository.delete(currentTest);
        testListView.getItems().remove(currentTest);
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
        if (currentState == State.NEW) {
            testComboBox.setDisable(false);
            testComboBox.getItems().setAll(testNames);

            List<InjectorTest> injectorTests = injectorTestRepository.findAllByInjector(getInjector());

            if (injectorTests != null)
                injectorTests.forEach(injectorTest -> testComboBox.getItems().remove(injectorTest.getTestName()));

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

            rpmTF.setText(injectorTest.getMotorSpeed().toString());
            barTF.setText(injectorTest.getSettedPressure().toString());
            widthTF.setText(injectorTest.getTotalPulseTime().toString());
            freqTF.setText(injectorTest.getInjectionRate().toString());
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
}
