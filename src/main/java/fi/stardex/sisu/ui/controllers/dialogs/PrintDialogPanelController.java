package fi.stardex.sisu.ui.controllers.dialogs;

import fi.stardex.sisu.model.GUI_TypeModel;
import fi.stardex.sisu.model.PumpModel;
import fi.stardex.sisu.pdf.Customer;
import fi.stardex.sisu.pdf.ExceptionalConsumer;
import fi.stardex.sisu.pdf.PDFService;
import fi.stardex.sisu.ui.controllers.GUI_TypeController;
import fi.stardex.sisu.ui.controllers.main.MainSectionController;
import fi.stardex.sisu.util.EmptyObjectDefaultChecker;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.wrappers.DateTimePickerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static fi.stardex.sisu.company.CompanyDetails.*;


public class PrintDialogPanelController{

    private static final Logger logger = LoggerFactory.getLogger(PrintDialogPanelController.class);

    public static final String UNDERSPACE = "_________________";

    @FXML
    private StackPane paneDataPicker;
    @FXML
    private TextField customerName;
    @FXML
    private TextField serial1;
    @FXML
    private TextField serial2;
    @FXML
    private TextField serial3;
    @FXML
    private TextField serial4;
    @FXML
    private TextField serial5;
    @FXML
    private TextField serial6;
    @FXML
    private Tab customerTab;
    @FXML
    private Label customerLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label serial1Label;
    @FXML
    private Label serial2Label;
    @FXML
    private Label serial3Label;
    @FXML
    private Label serial4Label;
    @FXML
    private Label serial5Label;
    @FXML
    private Label serial6Label;
    @FXML
    private Tab companyTab;
    @FXML
    private Label companyDetailLabel;
    @FXML
    private Label logoLabel;
    @FXML
    private Label companyNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label phone1Label;
    @FXML
    private Label phone2Label;
    @FXML
    private Label emailLabel;
    @FXML
    private Label urlLabel;
    @FXML
    private Button selectLogoButton;
    @FXML
    private Button clearLogoButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button pdfAndPrintButton;
    @FXML
    private Button printButton;
    @FXML
    private Button pdfButton;
    @FXML
    private ImageView imageView;
    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField phone1;
    @FXML
    private TextField phone2;
    @FXML
    private TextField email;
    @FXML
    private TextField urlField;

    private PDFService pdfService;

    private MainSectionController mainSectionController;

    private Stage frameStage;

    private File selectedFile;

    private DateTimePickerImpl datePicker;

    private I18N i18N;

    private GUI_TypeModel gui_typeModel;

    private PumpModel pumpModel;

    public void setPdfService(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    public void setMainSectionController(MainSectionController mainSectionController) {
        this.mainSectionController = mainSectionController;
    }

    public void  setStage(Stage frameStage){

        this.frameStage = frameStage;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    public void setGui_typeModel(GUI_TypeModel gui_typeModel) {
        this.gui_typeModel = gui_typeModel;
    }

    public void setPumpModel(PumpModel pumpModel) {
        this.pumpModel = pumpModel;
    }

    @PostConstruct
    public void init() {

        datePicker = new DateTimePickerImpl();
        datePicker.setDateTimeValue(LocalDateTime.now());
        datePicker.getStyleClass().add("date-picker");
        datePicker.setPrefWidth(325);
        paneDataPicker.getChildren().add(datePicker);

        try {
            String logo_url = LOGO_URL.get();
            if (!logo_url.isEmpty() && !logo_url.equals(DEFAULT_VALUE)) {
                selectedFile = new File(new URL(LOGO_URL.get()).toURI());
                imageView.setImage(new Image(LOGO_URL.get()));
            }
        } catch (Exception e) {
            logger.error("Error while loading resource.", e);
        }
        name.setText(NAME.get());
        address.setText(ADDRESS.get());
        phone1.setText(PHONE1.get());
        phone2.setText(PHONE2.get());
        email.setText(EMAIL.get());
        urlField.setText(URL_FIELD.get());

        bindingI18N();
    }

    @FXML
    public void saveToPdf() {
        pdfOrPrint(customer -> pdfService.makePDFForInjector(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.makePDFForInjectorCoding(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.makePDFForPump(customer, pumpModel.pumpProperty().get()));
    }

    @FXML
    public void onPrint() {
        pdfOrPrint(customer -> pdfService.printInjector(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.printInjectorCoding(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.printPump(customer, pumpModel.pumpProperty().get()));
    }

    @FXML
    public void onPDFAndPrint() {
        pdfOrPrint(customer -> pdfService.printAndPDFInjector(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.printAndPDFInjectorCoding(customer, CurrentInjectorObtainer.getInjector()),
                customer -> pdfService.printAndPDFPump(customer, pumpModel.pumpProperty().get()));
    }

    private void pdfOrPrint(ExceptionalConsumer<Customer, IOException> processInjector,
                            ExceptionalConsumer<Customer, IOException> processInjectorCoding,
                            ExceptionalConsumer<Customer, IOException> processPump){
        Customer customer = prepareDocument();
        try {
            if (gui_typeModel.guiTypeProperty().get() == GUI_TypeController.GUIType.CR_Inj && CurrentInjectorObtainer.getInjector() != null) {
                    if (mainSectionController.getCodingTestRadioButton().isSelected()) {
                        processInjectorCoding.accept(customer);
                    } else {
                        processInjector.accept(customer);
                    }
            }else if (gui_typeModel.guiTypeProperty().get() == GUI_TypeController.GUIType.CR_Pump && pumpModel.pumpProperty().get() != null) {
                processPump.accept(customer);
            }

        } catch (IOException e) {
            logger.error("Error while printing.", e);
        } finally {
            frameStage.hide();
        }
    }
    private Customer prepareDocument(){
        try {
            LOGO_URL.put(imageView.getImage() == null ? "" : selectedFile.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            logger.error("Error while saving logo url. Wrong URI format.", e);
        }
        NAME.put(name.getText());
        ADDRESS.put(address.getText());
        PHONE1.put(phone1.getText());
        PHONE2.put(phone2.getText());
        EMAIL.put(email.getText());
        URL_FIELD.put(urlField.getText());
        Customer customer = new Customer();
        customer.setCustomerName(EmptyObjectDefaultChecker.getStringOrDefault(customerName.getText(), UNDERSPACE));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm");
        customer.setLocalDateLine(EmptyObjectDefaultChecker.getStringOrDefault(datePicker.getDateTimeValue().format(formatter),
                LocalDateTime.now().format(formatter)));
        customer.setSerial1(EmptyObjectDefaultChecker.getStringOrDefault(serial1.getText(), UNDERSPACE));
        customer.setSerial2(EmptyObjectDefaultChecker.getStringOrDefault(serial2.getText(), UNDERSPACE));
        customer.setSerial3(EmptyObjectDefaultChecker.getStringOrDefault(serial3.getText(), UNDERSPACE));
        customer.setSerial4(EmptyObjectDefaultChecker.getStringOrDefault(serial4.getText(), UNDERSPACE));
        customer.setSerial5(EmptyObjectDefaultChecker.getStringOrDefault(serial5.getText(), UNDERSPACE));
        customer.setSerial6(EmptyObjectDefaultChecker.getStringOrDefault(serial6.getText(), UNDERSPACE));
        return customer;
    }

    @FXML
    public void onClose() {
        frameStage.hide();
    }

    @FXML
    public void onClickSelectFile() {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = null;
            try {
                image = new Image(selectedFile.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                logger.error("Error while selecting image. Wrong URI format.", e);

            }
            imageView.setImage(image);
        }
    }

    @FXML
    public void onClickClear() {
        imageView.setImage(null);
    }

    private void bindingI18N(){

        customerTab.textProperty().bind(i18N.createStringBinding("dialog.customer.tab"));
        customerLabel.textProperty().bind(i18N.createStringBinding("dialog.customer.customer"));
        dateLabel.textProperty().bind(i18N.createStringBinding("dialog.customer.date"));
        serial1Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial1"));
        serial2Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial2"));
        serial3Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial3"));
        serial4Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial4"));
        serial5Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial5"));
        serial6Label.textProperty().bind(i18N.createStringBinding("dialog.customer.serial6"));
        companyTab.textProperty().bind(i18N.createStringBinding("dialog.company.tab"));
        companyDetailLabel.textProperty().bind(i18N.createStringBinding("dialog.company.details"));
        logoLabel.textProperty().bind(i18N.createStringBinding("dialog.company.logo"));
        companyNameLabel.textProperty().bind(i18N.createStringBinding("dialog.company.name"));
        addressLabel.textProperty().bind(i18N.createStringBinding("dialog.company.address"));
        phone1Label.textProperty().bind(i18N.createStringBinding("dialog.company.phone1"));
        phone2Label.textProperty().bind(i18N.createStringBinding("dialog.company.phone2"));
        emailLabel.textProperty().bind(i18N.createStringBinding("dialog.company.email"));
        urlLabel.textProperty().bind(i18N.createStringBinding("dialog.company.url"));
        selectLogoButton.textProperty().bind(i18N.createStringBinding("dialog.company.selectfile"));
        clearLogoButton.textProperty().bind(i18N.createStringBinding("dialog.company.clear"));
        closeButton.textProperty().bind(i18N.createStringBinding("dialog.customer.close"));
        pdfAndPrintButton.textProperty().bind(i18N.createStringBinding("dialog.customer.pdf.and.print"));
        printButton.textProperty().bind(i18N.createStringBinding("dialog.customer.print"));
        pdfButton.textProperty().bind(i18N.createStringBinding("dialog.customer.save.and.close"));
    }

}
