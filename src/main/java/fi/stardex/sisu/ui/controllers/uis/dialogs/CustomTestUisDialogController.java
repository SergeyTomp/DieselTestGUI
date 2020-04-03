package fi.stardex.sisu.ui.controllers.uis.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.uis.CustomTestDialogModel;
import fi.stardex.sisu.model.uis.MainSectionUisModel;
import fi.stardex.sisu.persistence.orm.interfaces.Test;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTest;
import fi.stardex.sisu.persistence.orm.uis.InjectorUisTestName;
import fi.stardex.sisu.persistence.repos.uis.UisTestNameService;
import fi.stardex.sisu.persistence.repos.uis.UisTestService;
import fi.stardex.sisu.util.enums.InjectorSubType;
import fi.stardex.sisu.util.enums.Operation;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fi.stardex.sisu.util.converters.DataConverter.convertDataToDouble;
import static fi.stardex.sisu.util.converters.DataConverter.convertDataToInt;
import static fi.stardex.sisu.util.enums.GUI_type.UIS;
import static fi.stardex.sisu.util.enums.InjectorSubType.SINGLE_COIL;

public class CustomTestUisDialogController {

    @FXML private ComboBox<InjectorUisTestName> testComboBox;
    @FXML private TextField barTF;
    @FXML private TextField rpmTF;
    @FXML private TextField rackPositionTF;
    @FXML private TextField adjTimeTF;
    @FXML private TextField measureTimeTF;
    @FXML private TextField bipTF;
    @FXML private TextField bipRangeTF;
    @FXML private TextField width_1_TF;
    @FXML private TextField width_2_TF;
    @FXML private TextField offsetTF;
    @FXML private TextField angle_1_TF;
    @FXML private TextField angle_2_TF;
    @FXML private TextField nominalTF;
    @FXML private TextField flowRangeTF;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private Stage dialogStage;
    private Parent dialogView;
    private List<InjectorUisTestName> testNames = new LinkedList<>();
    private Alert alert;
    private StringProperty alertString = new SimpleStringProperty("Please specify all values!");
    private StringProperty yesButton = new SimpleStringProperty();
    private List<Control> controlsList = new ArrayList<>();

    private MainSectionUisModel mainSectionUisModel;
    private CustomTestDialogModel customTestDialogModel;
    private GUI_TypeModel guiTypeModel;
    private I18N i18N;
    private UisTestNameService uisTestNameService;
    private UisTestService uisTestService;

    public void setMainSectionUisModel(MainSectionUisModel mainSectionUisModel) {
        this.mainSectionUisModel = mainSectionUisModel;
    }
    public void setCustomTestDialogModel(CustomTestDialogModel customTestDialogModel) {
        this.customTestDialogModel = customTestDialogModel;
    }
    public void setGuiTypeModel(GUI_TypeModel guiTypeModel) {
        this.guiTypeModel = guiTypeModel;
    }
    public void setDialogView(Parent dialogView) {
        this.dialogView = dialogView;
    }
    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }
    public void setUisTestNameService(UisTestNameService uisTestNameService) {
        this.uisTestNameService = uisTestNameService;
    }
    public void setUisTestService(UisTestService uisTestService) {
        this.uisTestService = uisTestService;
    }

    @PostConstruct
    public void init() {

        mainSectionUisModel.customTestOperationProperty().addListener((observableValue, oldValue, newValue) -> {

            if(newValue == null) return;
            /** Additional check of GUI type is done to prevent listener invocation and dialog window irrelevant to GUI type activation.
             * This will be important after implementation of MainSectionUisController as a unique one for all GUI types.
             * Such a check could be done through {@code newValue instanceOf InjectorUisTest} but it is slower */
            if (guiTypeModel.guiTypeProperty().get() != UIS) {return;}

            if (dialogStage == null) {
                dialogStage = new Stage();
                dialogStage.setScene(new Scene(dialogView));
                dialogStage.setResizable(false);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setOnCloseRequest(event -> customTestDialogModel.cancelProperty().setValue(new Object()));
            }
            dialogStage.setTitle(mainSectionUisModel.customTestOperationProperty().get().getTitle() + " test");
            setLabels();
            dialogStage.show();
        });

        saveBtn.setOnMouseClicked(event -> {

            switch (mainSectionUisModel.customTestOperationProperty().get()) {

                case NEW:
                    create();
                    break;
                case EDIT:
                    update();
                    break;
                case DELETE:
                    delete();
                    break;
                case COPY:
                    break;
            }
        });

        cancelBtn.setOnMouseClicked(event -> {

            customTestDialogModel.cancelProperty().setValue(new Object());
            dialogStage.close();
        });

        testNames.addAll(uisTestNameService.findAll());

        testComboBox.valueProperty().addListener((observableValue, oldValue, newValue) -> {

            if (newValue == null){
                saveBtn.setDisable(true);
                return;
            }

            boolean isBipTest = newValue.getName().equals("BIP test");
            bipTF.setDisable(!isBipTest);
            bipRangeTF.setDisable(!isBipTest);
            saveBtn.setDisable(false);
        });

        controlsList.add(barTF);
        controlsList.add(rpmTF);
        controlsList.add(adjTimeTF);
        controlsList.add(measureTimeTF);
        controlsList.add(bipTF);
        controlsList.add(bipRangeTF);
        controlsList.add(width_1_TF);
        controlsList.add(width_2_TF);
        controlsList.add(offsetTF);
        controlsList.add(angle_1_TF);
        controlsList.add(angle_2_TF);
        controlsList.add(nominalTF);
        controlsList.add(flowRangeTF);

        bindingI18N();
    }

    private void showAlert() {

        if (alert == null) {
            alert = new Alert(Alert.AlertType.NONE, "", ButtonType.YES);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/Styling.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alertDialog");
            alert.setResizable(true);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        }
        ((Button) alert.getDialogPane().lookupButton(ButtonType.YES)).textProperty().setValue(yesButton.get());
        alert.setContentText(alertString.get());
        alert.show();
    }

    private void create() {

        if(!isDataComplete())return;

        InjectorUisTest test = new InjectorUisTest(
                (InjectorUIS) mainSectionUisModel.modelProperty().get(),
                testComboBox.getSelectionModel().getSelectedItem(),
                getInteger(rpmTF),
                getInteger(barTF),
                getInteger(adjTimeTF),
                getInteger(measureTimeTF),
                getInteger(width_1_TF),
                getDouble(nominalTF),
                getDouble(flowRangeTF),
                Boolean.TRUE,
                getInteger(width_2_TF),
                getInteger(offsetTF),
                getInteger(angle_1_TF),
                getInteger(angle_2_TF),
                null,
                getInteger(bipTF),
                getInteger(bipRangeTF),
                getInteger(rackPositionTF));

        uisTestService.save(test);
        customTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void update() {

        if(!isDataComplete())return;

        InjectorUisTest test = (InjectorUisTest)mainSectionUisModel.injectorTestProperty().get();

        test.setMotorSpeed(getInteger(rpmTF));
        test.setTargetPressure(getInteger(barTF));
        test.setAdjustingTime(getInteger(adjTimeTF));
        test.setMeasuringTime(getInteger(measureTimeTF));
        test.setTotalPulseTime1(getInteger(width_1_TF));
        test.setTotalPulseTime2(getInteger(width_2_TF));
        test.setNominalFlow(getDouble(nominalTF));
        test.setFlowRange(getDouble(flowRangeTF));
        test.setShift(getInteger(offsetTF));
        test.setAngle_1(getInteger(angle_1_TF));
        test.setAngle_2(getInteger(angle_2_TF));
        test.setBip(getInteger(bipTF));
        test.setBipRange(getInteger(bipRangeTF));
        test.setRackPosition(getInteger(rackPositionTF));

        controlsList.stream().filter(c -> !c.isDisabled()).filter(this::isEmpty).findAny().ifPresent(c -> showAlert());
        if(alert != null && alert.isShowing())return;

        uisTestService.update(test);
        customTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void delete() {

        Test test = mainSectionUisModel.injectorTestProperty().get();
        uisTestService.delete(test);
        customTestDialogModel.doneProperty().setValue(new Object());
        dialogStage.close();
    }

    private void setLabels() {

        saveBtn.setDisable(true);
        InjectorSubType injectorSubType = mainSectionUisModel.modelProperty().get().getVAP().getInjectorSubType();
        Operation operation = mainSectionUisModel.customTestOperationProperty().get();
        disableNodes(false);
        selectFields(injectorSubType);

        if (operation == Operation.NEW) {

            clearTextFields();
            testComboBox.getItems().setAll(testNames
                    .stream()
                    .filter(testName -> (!testName.getName().equals("BIP test")) || (injectorSubType == SINGLE_COIL))
                    .collect(Collectors.toList()));

            List<InjectorUisTest> injectorTests = uisTestService.findAllByModel(mainSectionUisModel.modelProperty().get());

            if (injectorTests != null) {
                injectorTests.forEach(injectorTest -> testComboBox.getItems().remove(injectorTest.getTestName())); }

        }
        else {

            testComboBox.getItems().setAll(testNames);
            testComboBox.getSelectionModel().select((InjectorUisTestName)mainSectionUisModel.injectorTestProperty().get().getTestName());
            testComboBox.setDisable(true);

            InjectorUisTest injectorTest = (InjectorUisTest)mainSectionUisModel.injectorTestProperty().get();

            rpmTF.setText(getStringFromInteger(InjectorUisTest::getMotorSpeed, injectorTest));
            barTF.setText(getStringFromInteger(InjectorUisTest::getTargetPressure, injectorTest));
            rackPositionTF.setText(getStringFromInteger(InjectorUisTest::getRackPosition, injectorTest));
            adjTimeTF.setText(getStringFromInteger(InjectorUisTest::getAdjustingTime, injectorTest));
            measureTimeTF.setText(getStringFromInteger(InjectorUisTest::getMeasuringTime, injectorTest));
            width_1_TF.setText(getStringFromInteger(InjectorUisTest::getTotalPulseTime1, injectorTest));
            width_2_TF.setText(getStringFromInteger(InjectorUisTest::getTotalPulseTime2, injectorTest));
            offsetTF.setText(getStringFromInteger(InjectorUisTest::getShift, injectorTest));
            angle_1_TF.setText(getStringFromInteger(InjectorUisTest::getAngle_1, injectorTest));
            angle_2_TF.setText(getStringFromInteger(InjectorUisTest::getAngle_2, injectorTest));
            nominalTF.setText(getStringFromDouble(InjectorUisTest::getNominalFlow, injectorTest));
            flowRangeTF.setText(getStringFromDouble(InjectorUisTest::getFlowRange, injectorTest));
            bipTF.setText(getStringFromInteger(InjectorUisTest::getBip, injectorTest));
            bipRangeTF.setText(getStringFromInteger(InjectorUisTest::getBipRange, injectorTest));
        }

        if (operation == Operation.DELETE) { disableNodes(true); }
    }

    private void selectFields(InjectorSubType injectorSubType) {

        switch (injectorSubType) {

            case SINGLE_COIL:

                barTF.setDisable(true);
                rackPositionTF.setDisable(true);
                angle_2_TF.setDisable(true);
                width_2_TF.setDisable(true);
                offsetTF.setDisable(true);
                break;
            case DOUBLE_COIL:

                barTF.setDisable(true);
                rackPositionTF.setDisable(true);
                angle_2_TF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
            case DOUBLE_SIGNAL:

                barTF.setDisable(true);
                rackPositionTF.setDisable(true);
                offsetTF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
            case SINGLE_PIEZO:

                barTF.setDisable(true);
                rackPositionTF.setDisable(true);
                angle_2_TF.setDisable(true);
                width_2_TF.setDisable(true);
                offsetTF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
            case F2E:

                rackPositionTF.setDisable(true);
                angle_2_TF.setDisable(true);
                width_2_TF.setDisable(true);
                offsetTF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
            case HPI:

                rackPositionTF.setDisable(true);
                barTF.setDisable(true);
                offsetTF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
            case MECHANIC:

                barTF.setDisable(true);
                angle_2_TF.setDisable(true);
                width_2_TF.setDisable(true);
                offsetTF.setDisable(true);
                bipTF.setDisable(true);
                bipRangeTF.setDisable(true);
                break;
        }
    }

    private boolean isEmpty(Control control) {

        if (control instanceof ComboBox) {
            return ((ComboBox) control).getSelectionModel().isEmpty();
        } else if (control instanceof TextField) {
            return ((TextField) control).getText().isEmpty();
        }else return true;
    }

    private Integer getInteger(Control control) {

        if (control instanceof TextField) {
            return ((TextField)control).getText().isEmpty() ? null : convertDataToInt(((TextField)control).getText());
        } return null;
    }

    private Double getDouble(Control control) {

        if (control instanceof TextField) {
            return ((TextField)control).getText().isEmpty() ? null : convertDataToDouble(((TextField)control).getText());
        } return null;
    }

    private String getStringFromInteger(Function<InjectorUisTest, Integer> integerFunction, InjectorUisTest test) {

        if(test == null) return null;
        if(integerFunction.apply(test) == null) return "";
        return integerFunction.apply(test).toString();
    }

    private String getStringFromDouble(Function<InjectorUisTest, Double> doubleFunction, InjectorUisTest test) {

        if(test == null) return null;
        if(doubleFunction.apply(test) == null) return "";
        return doubleFunction.apply(test).toString();
    }

    private void disableNodes(boolean disable) {

        barTF.setDisable(disable);
        rpmTF.setDisable(disable);
        rackPositionTF.setDisable(disable);
        adjTimeTF.setDisable(disable);
        measureTimeTF.setDisable(disable);
        bipTF.setDisable(disable);
        bipRangeTF.setDisable(disable);
        width_1_TF.setDisable(disable);
        width_2_TF.setDisable(disable);
        offsetTF.setDisable(disable);
        angle_1_TF.setDisable(disable);
        angle_2_TF.setDisable(disable);
        nominalTF.setDisable(disable);
        flowRangeTF.setDisable(disable);
        testComboBox.setDisable(disable);
    }

    private void clearTextFields() {

        testComboBox.getSelectionModel().clearSelection();
        barTF.setText("");
        rpmTF.setText("");
        rackPositionTF.setText("");
        adjTimeTF.setText("");
        measureTimeTF.setText("");
        bipTF.setText("");
        bipRangeTF.setText("");
        width_1_TF.setText("");
        width_2_TF.setText("");
        offsetTF.setText("");
        angle_1_TF.setText("");
        angle_2_TF.setText("");
        nominalTF.setText("");
        flowRangeTF.setText("");
    }

    private boolean isDataComplete() {

        controlsList.stream().filter(c -> !c.isDisabled()).filter(this::isEmpty).findAny().ifPresent(c -> showAlert());

        if(alert != null && alert.isShowing())return false;
        return true;
    }

    private void bindingI18N() {

        yesButton.bind((i18N.createStringBinding("alert.yesButton")));
        alertString.bind((i18N.createStringBinding("alert.customDialog")));
    }
}
