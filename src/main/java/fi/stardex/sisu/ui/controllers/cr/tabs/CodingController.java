package fi.stardex.sisu.ui.controllers.cr.tabs;

import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.pdf.Result;
import fi.stardex.sisu.util.i18n.I18N;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.annotation.PostConstruct;
import java.util.List;

public class CodingController {

    @FXML private TextField injectorCode1TextField;

    @FXML private TextField injectorCode2TextField;

    @FXML private TextField injectorCode3TextField;

    @FXML private TextField injectorCode4TextField;

    @FXML private Label labelCode1TextField;

    @FXML private Label labelCode2TextField;

    @FXML private Label labelCode3TextField;

    @FXML private Label labelCode4TextField;

    @FXML private Label labelCodingNote;

    private List<TextField> textFieldsList;

    private CodingReportModel codingReportModel;

    private I18N i18N;

    public TextField getInjectorCode1TextField() {
        return injectorCode1TextField;
    }

    public TextField getInjectorCode2TextField() {
        return injectorCode2TextField;
    }

    public TextField getInjectorCode3TextField() {
        return injectorCode3TextField;
    }

    public TextField getInjectorCode4TextField() {
        return injectorCode4TextField;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setCodingReportModel(CodingReportModel codingReportModel) {
        this.codingReportModel = codingReportModel;
    }

    @PostConstruct
    private void init(){
        bindingI18N();
        
        textFieldsList = List.of(injectorCode1TextField, injectorCode2TextField, injectorCode3TextField, injectorCode4TextField);

        codingReportModel.getResultObservableMap().addListener((MapChangeListener<String, Result>) change -> {

            if(!change.getMap().isEmpty()){

                List<Result> values = codingReportModel.getResultsList();
                values.stream()
                        .filter(v -> !v.getMainColumn().equals(""))
                        .forEach(result -> textFieldsList.get(Integer.parseInt(result.getMainColumn()) - 1).setText(result.getSubColumn1()));
            }else{
                List.of(injectorCode1TextField, injectorCode2TextField, injectorCode3TextField, injectorCode4TextField).forEach(tF -> tF.setText(""));
            }
        });
    }

    private void bindingI18N() {
        labelCode1TextField.textProperty().bind(i18N.createStringBinding("h4.coding.label.injector1"));
        labelCode2TextField.textProperty().bind(i18N.createStringBinding("h4.coding.label.injector2"));
        labelCode3TextField.textProperty().bind(i18N.createStringBinding("h4.coding.label.injector3"));
        labelCode4TextField.textProperty().bind(i18N.createStringBinding("h4.coding.label.injector4"));
        labelCodingNote.textProperty().bind(i18N.createStringBinding("h4.coding.label.codingNote"));
    }
}
