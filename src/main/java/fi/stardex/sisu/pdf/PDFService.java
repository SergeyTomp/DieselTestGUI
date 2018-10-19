package fi.stardex.sisu.pdf;


import be.quodlibet.boxable.*;
import fi.stardex.sisu.company.CompanyDetails;
import fi.stardex.sisu.pdf.headers.*;
import fi.stardex.sisu.persistence.orm.Pump;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.pumps.CurrentPumpObtainer;
import fi.stardex.sisu.ui.controllers.additional.AdditionalSectionController;
import fi.stardex.sisu.ui.controllers.dialogs.PrintDialogPanelController;
import fi.stardex.sisu.ui.data.*;
import fi.stardex.sisu.util.DesktopFiles;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.UTF8Control;
import fi.stardex.sisu.util.obtainers.CurrentInjectorObtainer;
import fi.stardex.sisu.util.obtainers.CurrentManufacturerObtainer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

import static fi.stardex.sisu.company.CompanyDetails.*;


public class PDFService {

    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);

    private static final int LINES_INTERVAL = 16;
    private static final int FONT = 12;
    private static final int SEPARATOR_LINE_Y = 570;
    private static final int MAX_WIDTH = 603;
    private static final int BANNER_INJECTOR_FILLED_Y = 470;
    private static final int INJECTOR_X = 20;
    private static final int INJECTOR_TABLE_HEIGHT = 70;
    private static final int INJECTOR_INFO_CENTER_LINE_X = (INJECTOR_X + MAX_WIDTH - 40) / 3 + 30;
    private static final int SERIALS_X1 = INJECTOR_INFO_CENTER_LINE_X + 15;
    private static final int SERIALS_X2 = SERIALS_X1 + 170;
    private static final int CELL_HEIGHT = 25;
    private static final int UNDER_INJECTOR_Y = BANNER_INJECTOR_FILLED_Y - INJECTOR_TABLE_HEIGHT;
    private static final int START_TABLE = 365;

    private StringProperty titleValue;
    private StringProperty injectorInfoTitle;
    private StringProperty pumpInfoTitle;
    private StringProperty deliveryLH;
    private StringProperty targetPressureBar;
    private StringProperty motorSpeedRpm;
    private StringProperty deliveryBackflow;
    private StringProperty testName;
    private StringProperty customerLine;
    private StringProperty date;
    private StringProperty manufacturerTemplate;
    private StringProperty model;
    private StringProperty injector;
    private StringProperty injectorName;
    private StringProperty codingValue;
    private StringProperty page;
    private StringProperty parameterName;
    private StringProperty measurementUnits;
    private StringProperty delayTestName;
    private StringProperty logoLineFirst;
    private StringProperty logoLineSecond;
    private StringProperty stardexSite;

    private Customer customer;
    private ResourceBundle resourceBundle;
    private PDPageContentStream contentStream;
    private PDDocument document;
    private PDPage currentPage;
    private PDFont font;

    private AdditionalSectionController additionalSectionController;
    private I18N i18N;

    private SavedInjectorBeakerData savedDataForBeakers;

    private SavedPumpBeakerData savedDataForBeakersPump;

    private RLC_ResultsStorage rlcResultsStorage;

    private DelayResultsStorage delayResultsStorage;

    private CodingResultsStorage codingResultsStorage;

    private ReportUtils reportUtils;

    private DesktopFiles desktopFiles;

    public void setSavedDataForBeakers(SavedInjectorBeakerData savedInjectorBeakerData) {
        this.savedDataForBeakers = savedInjectorBeakerData;
    }

    public void setSavedDataForBeakersPump(SavedPumpBeakerData savedDataForBeakersPump) {
        this.savedDataForBeakersPump = savedDataForBeakersPump;
    }

    public void setRlcResultsStorage(RLC_ResultsStorage rlcResultsStorage) {
        this.rlcResultsStorage = rlcResultsStorage;
    }

    public void setDelayResultsStorage(DelayResultsStorage delayResultsStorage) {
        this.delayResultsStorage = delayResultsStorage;
    }

    public void setCodingResultsStorage(CodingResultsStorage codingResultsStorage) {
        this.codingResultsStorage = codingResultsStorage;
    }

    public void setReportUtils(ReportUtils reportUtils) {
        this.reportUtils = reportUtils;
    }

    public void setDesktopFiles(DesktopFiles desktopFiles) {
        this.desktopFiles = desktopFiles;
    }

    public void setAdditionalSectionController(AdditionalSectionController additionalSectionController) {
        this.additionalSectionController = additionalSectionController;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    private PrinterJob pjob = PrinterJob.getPrinterJob();

    @PostConstruct
    public void init() {

        resourceBundle = ResourceBundle.getBundle("properties.labels", i18N.getLocale(), new UTF8Control());
//        titleValue = resourceBundle.getString("h4.report.table.label.value");
//        injectorInfoTitle = resourceBundle.getString("h4.report.table.label.injInfo");
//        pumpInfoTitle = resourceBundle.getString("h4.report.table.label.pmpInfo");
//        deliveryLH = resourceBundle.getString("h4.report.table.label.nominal");
//        targetPressureBar = resourceBundle.getString("h4.report.table.label.pressure");
//        motorSpeedRpm = resourceBundle.getString("h4.report.table.label.motorSpeed");
//        deliveryBackflow = resourceBundle.getString("h4.report.table.label.delRec");
//        testName = resourceBundle.getString("h4.report.table.label.testName");
//        customerLine = resourceBundle.getString("h4.report.table.label.customer");
//        date = resourceBundle.getString("h4.report.table.label.date");
//        manufacturerTemplate = resourceBundle.getString("h4.report.table.label.defaultManufacturer");
//        model = resourceBundle.getString("h4.report.table.label.model");
//        injector = resourceBundle.getString("h4.report.table.label.injector");
//        injectorName = resourceBundle.getString("h4.report.table.label.injectorName");
//        codingValue = resourceBundle.getString("h4.report.table.label.codingValue");
//        page = resourceBundle.getString("h4.report.table.label.page");
//        parameterName = resourceBundle.getString("pdf.report.header.measurement.parameterName");
//        measurementUnits = resourceBundle.getString("pdf.report.header.measurement.measurementUnits");
//        delayTestName = resourceBundle.getString("pdf.report.header.delay.testName");
        bindingI18N();
    }

    private void printDocument() throws PrinterException {
        logger.debug("pdf print");

        Paper paper = new Paper();
        paper.setSize(21 * 72 / 2.54, 29.7 * 72 / 2.54); // 1/72 inch
        double margin = 10;
        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight() - margin * 2);

        PageFormat pageFormat = new PageFormat();
        pageFormat.setPaper(paper);

        Book book = new Book();
        // append all pages
        book.append(new PDFPrintable(document, Scaling.SHRINK_TO_FIT), pageFormat, document.getNumberOfPages());
        pjob.setPageable(book);
        pjob.print();
    }

    public void makePDFForInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.INJECTORS, false, true);
    }
    public void makePDFForInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.CODING, false, true);
    }
    public void makePDFForPumps(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, false, true);
    }
    public void printInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, CurrentInjectorObtainer.getInjector(), ResultPrintMode.INJECTORS, true, false);
    }
    public void printInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, CurrentInjectorObtainer.getInjector(), ResultPrintMode.CODING, true, false);
    }
    public void printPumps(Customer customer, Pump pump) throws IOException {
        makePDF(customer, CurrentPumpObtainer.getPump(), ResultPrintMode.PUMPS, true, false);
    }
    public void printAndPDFInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.INJECTORS, true, true);
    }
    public void printAndPDFInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.CODING, true, true);
    }
    public void printAndPDFPumps(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, true, true);
    }

    private void makePDF(Customer customer, Model model, ResultPrintMode resultMode, boolean printPDF, boolean savePDF) throws IOException {
        this.customer = customer;
        document = new PDDocument();
        currentPage = new PDPage();
        document.addPage(currentPage);

        drawHeaderPage(document, currentPage);

        if (resultMode == ResultPrintMode.INJECTORS) {
            Injector injector = CurrentInjectorObtainer.getInjector();
            drawCustomerAndInjectorData(customer, model);
            float startTable = drawTable(document, injector, rlcResultsStorage, new MeasurementHeader(), START_TABLE);
            startTable = drawInjectorFlowTable(document, injector, new FlowHeader(), startTable);
            drawTable(document, injector, delayResultsStorage, new DelayHeader(), startTable);
        } else if (resultMode == ResultPrintMode.PUMPS) {
            drawCustomerAndInjectorData(customer, model);
            drawPumpTable(document, model, new PumpHeader(), START_TABLE);
        } else if (resultMode == ResultPrintMode.CODING) {
            Injector injector = CurrentInjectorObtainer.getInjector();
            drawCustomerAndInjectorData(customer, model);
            drawTable(document, injector, codingResultsStorage, new CodingHeader(), START_TABLE);
        }
        finish(printPDF, savePDF);
    }

    private float drawTable(PDDocument document, Injector injector, ResultsStorage resultsStorage,
                            Header header, float yStart) throws IOException {
        if (resultsStorage.containsKey(injector)) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);
            drawData(baseTable, injector, resultsStorage);
            float newStartY = baseTable.draw() - CELL_HEIGHT;
            checkForNewPage(baseTable);
            return newStartY;
        }
        return yStart;
    }

    private float drawInjectorFlowTable(PDDocument document, Injector injector, Header header, float yStart) throws IOException {
        if (savedDataForBeakers.getSavedDataForBeakers().containsKey(injector)) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);
            drawFlowData(baseTable, injector);
            float newStartY = baseTable.draw() - CELL_HEIGHT;
            checkForNewPage(baseTable);
            return newStartY;
        }
        return yStart;
    }

    private float drawPumpTable(PDDocument document, Model model, Header header, float yStart) throws IOException {
        if (savedDataForBeakersPump.getSavedDataForBeakers().containsKey(model)) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);
            drawPumpData(baseTable, model);
            float newStartY = baseTable.draw() - CELL_HEIGHT;
            checkForNewPage(baseTable);
            return newStartY;
        }
        return yStart;
    }

    private BaseTable createTable(PDDocument document, float yStart) throws IOException {
        float yMargin = 50;
        float bottomMargin = 70;
        float width = 582;
        float margin = 20;
        float yStartNewPage = currentPage.getMediaBox().getHeight() - yMargin;
        return new BaseTable(yStart, yStartNewPage, bottomMargin, width, margin, document, currentPage,
                true, true);
    }

    private void checkForNewPage(BaseTable baseTable) throws IOException {
        if (baseTable.getCurrentPage() != currentPage) {
            currentPage = baseTable.getCurrentPage();
        }
    }

    private void drawHeader(Header header, BaseTable baseTable) {
        Row<PDPage> headerRow = baseTable.createRow(CELL_HEIGHT);
        Cell<PDPage> pdPageCell = headerRow.createCell(100, header.getMainHeader());
        pdPageCell.setFillColor(new Color(0x95, 0xD0, 0xFC));
        baseTable.addHeaderRow(headerRow);
        headerRow = baseTable.createRow(CELL_HEIGHT);
        for (HeaderCell headerCell : header.getHeaderCells()) {
            Cell<PDPage> cell = headerRow.createCell(headerCell.getWidth(), headerCell.getValue());
            cell.setFillColor(Color.LIGHT_GRAY);
        }
        baseTable.addHeaderRow(headerRow);
    }

    private void drawData(BaseTable baseTable, Injector injector, ResultsStorage resultsStorage) {
        for (Result result : resultsStorage.getResultsList(injector)) {
            if(result == null)
                continue;

            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = result.getMainColumn() == null ? null : row.createCell(result.getMainColumn());
            cell = result.getSubColumn1() == null ? null : row.createCell(result.getSubColumn1());
            cell = result.getSubColumn2() == null ? null : row.createCell(result.getSubColumn2());
            if (null != result.getValueColumns()) {
                for (String value : result.getValueColumns()) {
                    cell = row.createCell(value);
                }
            }
        }
        setCellSettings(baseTable);
    }

    private void drawFlowData(BaseTable baseTable, Injector injector) {
        for (TestResult testResult : savedDataForBeakers.getTestResult(injector)) {
            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = row.createCell(testResult.getTestName());
            cell = row.createCell(testResult.getDeliveryRecovery());
            cell = row.createCell(reportUtils.getFormulaNominalFlowRangeForInjectors(testResult));
            cell = row.createCell(reportUtils.getValueResultForInjectors(testResult.getBeakerValue1().toString(), testResult));
            Color color = ColorCellResult.getColorCellOfResult(testResult.getBeakerValue1(), testResult);
            cell.setFillColor(color);
            cell = row.createCell(reportUtils.getValueResultForInjectors(testResult.getBeakerValue2().toString(), testResult));
            color = ColorCellResult.getColorCellOfResult(testResult.getBeakerValue2(), testResult);
            cell.setFillColor(color);
            cell = row.createCell(reportUtils.getValueResultForInjectors(testResult.getBeakerValue3().toString(), testResult));
            color = ColorCellResult.getColorCellOfResult(testResult.getBeakerValue3(), testResult);
            cell.setFillColor(color);
            cell = row.createCell(reportUtils.getValueResultForInjectors(testResult.getBeakerValue4().toString(), testResult));
            color = ColorCellResult.getColorCellOfResult(testResult.getBeakerValue4(), testResult);
            cell.setFillColor(color);
            setCellSettings(baseTable);
        }
    }

    private void drawPumpData(BaseTable baseTable, Model model) {
        for (TestResult testResult : savedDataForBeakersPump.getTestResult(model)) {
            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = row.createCell(testResult.getTestName());
            cell = row.createCell(testResult.getMotorSpeed().toString());
            cell = row.createCell(testResult.getPressure().toString());
            cell = row.createCell(reportUtils.getFormulaNominalFlowRangeForPumps(testResult));
            cell = row.createCell(testResult.getDeliveryRecovery());
            cell = row.createCell(reportUtils.getValueResultForPumps(testResult.getValue().toString(), testResult));
            Color color = ColorCellResult.getColorCellOfResult(testResult.getValue(), testResult);
            cell.setFillColor(color);
            setCellSettings(baseTable);
        }
    }

    private void setCellSettings(BaseTable baseTable) {
        for (Row<PDPage> row: baseTable.getRows()) {
            for (Cell<PDPage> cell : row.getCells()) {
                cell.setFont(font);
                cell.setFontSize(FONT);
                cell.setAlign(HorizontalAlignment.CENTER);
                cell.setValign(VerticalAlignment.MIDDLE);
            }
        }
    }

    private void drawHeaderPage(PDDocument document, PDPage page) throws IOException {
        PDImageXObject companyLogoImage = getCompanyImage(document);
        BufferedImage stardexImage = ImageIO.read(getClass().getResource("/img/logo.png"));
        PDImageXObject stardexObjectImage = LosslessFactory.createFromImage(document, stardexImage);
        contentStream = new PDPageContentStream(document, page);

        if (companyLogoImage != null) {
            drawScaledXObject(companyLogoImage, 20, 650, 250, 120);
        }
        drawScaledXObject(stardexObjectImage, 390, 690, 170, 60);

        contentStream.beginText();

        InputStream inputStream;

        if (additionalSectionController.getSettingsController().getLanguagesConfigComboBox().getSelectionModel().getSelectedItem().toString().equals("Russian")) {
            inputStream = this.getClass().getResourceAsStream("/fonts/Arial_Cyr.ttf");
        } else {
            inputStream = this.getClass().getResourceAsStream("/fonts/LiberationSans-Bold.ttf");
        }
        font = PDType0Font.load(document, inputStream);

        contentStream.setFont(font, FONT);
        contentStream.newLineAtOffset(390, 630);
//        contentStream.showText(resourceBundle.getString("pdf.stardex.logo.line.first"));
        contentStream.showText(logoLineFirst.getValue());
        contentStream.newLineAtOffset(0, -LINES_INTERVAL);
//        contentStream.showText(resourceBundle.getString("pdf.stardex.logo.line.second"));
        contentStream.showText(logoLineSecond.getValue());
        contentStream.newLineAtOffset(0, -LINES_INTERVAL);
//        contentStream.showText(resourceBundle.getString("stardex.site"));
        contentStream.showText(stardexSite.getValue());
        contentStream.newLineAtOffset(0, -LINES_INTERVAL);
        contentStream.showText("Finland");
        contentStream.newLineAtOffset(-370, LINES_INTERVAL * 4f);
        drawAndMove(NAME.get());
        drawAndMove(ADDRESS.get());
        drawAndMove(PHONE1.get() + "   " + PHONE2.get());
        drawAndMove(EMAIL.get() + "   " + URL_FIELD.get());
        contentStream.endText();
        drawLine(0, SEPARATOR_LINE_Y, MAX_WIDTH, SEPARATOR_LINE_Y);
    }

    private void drawCustomerAndInjectorData(Customer customer, Model model) throws IOException {
        String manufacturer = "";
        String infoLine = "";

        if (model instanceof Injector) {
            manufacturer = CurrentManufacturerObtainer.getManufacturer().toString();
//            manufacturer = model.getManufacturer().toString();
            infoLine = injectorInfoTitle.getValue();
        }

        if (model instanceof Pump) {
            manufacturer = model.getManufacturer().toString();
            infoLine = pumpInfoTitle.getValue();
        }
        /*
         * Draw Customer data
         * **/

        contentStream.beginText();
        contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, 0, SEPARATOR_LINE_Y - LINES_INTERVAL));
        contentStream.newLineAtOffset(20, -LINES_INTERVAL);
        contentStream.showText(customerLine.getValue() + " " + customer.getCustomerName());
        contentStream.newLineAtOffset(0, -LINES_INTERVAL);
        contentStream.showText(date.getValue() + " " + customer.getLocalDateLine());
        contentStream.endText();

        /*
         * Draw injectInfoTitle
         * **/

        drawBoundedFilledRect(INJECTOR_X, BANNER_INJECTOR_FILLED_Y, MAX_WIDTH - 21, CELL_HEIGHT, Color.LIGHT_GRAY, infoLine, 10);

        /*
         * Draw data table
         * * **/
        drawBoundedRect(INJECTOR_X, UNDER_INJECTOR_Y, MAX_WIDTH - 21, INJECTOR_TABLE_HEIGHT);
        drawLine(INJECTOR_INFO_CENTER_LINE_X, UNDER_INJECTOR_Y,
                INJECTOR_INFO_CENTER_LINE_X, (float) UNDER_INJECTOR_Y + INJECTOR_TABLE_HEIGHT);
        contentStream.beginText();
        contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, INJECTOR_X + 20, UNDER_INJECTOR_Y + INJECTOR_TABLE_HEIGHT - LINES_INTERVAL));
        drawAndMove(String.format("%s %s", manufacturerTemplate.getValue(), manufacturer));
        drawAndMove(String.format("%s %s", this.model.getValue(), model));

        contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, SERIALS_X1, UNDER_INJECTOR_Y + INJECTOR_TABLE_HEIGHT - LINES_INTERVAL / 2));
        drawAndMove(String.format("1. %s", customer.getSerial1()));
        drawAndMove(String.format("2. %s", customer.getSerial2()));
        drawAndMove(String.format("3. %s", customer.getSerial3()));
        contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, SERIALS_X2, UNDER_INJECTOR_Y + INJECTOR_TABLE_HEIGHT - LINES_INTERVAL / 2));
        drawAndMove(String.format("4. %s", customer.getSerial4()));
        drawAndMove(String.format("5. %s", customer.getSerial5()));
        drawAndMove(String.format("6. %s", customer.getSerial6()));
        contentStream.endText();
    }

    private void drawScaledXObject(PDImageXObject img, float x, float y, float scaleX, float scaleY) throws IOException {
        float imgWidth = img.getWidth();
        float imgHeight = img.getHeight();

        float k1 = imgWidth / scaleX;
        float k2 = imgHeight / scaleY;

        if (k1 > k2) {
            contentStream.drawImage(img, x, y + (scaleY - imgHeight / k1) / 2, imgWidth / k1, imgHeight / k1);
        } else {
            contentStream.drawImage(img, x + (scaleX - imgWidth / k2) / 2, y, imgWidth / k2, imgHeight / k2);
        }
    }

    private void drawBoundedFilledRect(int x, int y, int width, int height, Color fillColor, String text, int closerToLeftBound) throws IOException {
        if (height < FONT) {
            return;
        }
        contentStream.setNonStrokingColor(fillColor);
        fillRect(x, y, width, height);
        drawBoundedRect(x, y, width, height);
        contentStream.beginText();
        contentStream.setTextMatrix(new Matrix(1, 0, 0, 1, x + closerToLeftBound, y + (height - FONT) / 2));
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.showText(text == null ? "" : text);
        contentStream.endText();
    }

    private void drawBoundedRect(int x, int y, int width, int height) throws IOException {
        drawLine(x, y, (float) x + width, y);
        drawLine(x, (float) y + height, (float) x + width, (float) y + height);
        drawLine(x, y, x, (float) y + height);
        drawLine((float) x + width, y, (float) x + width, (float) y + height);
    }

    private void drawPageNumber(int pageNumber) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(290, 10);
        contentStream.showText(page.getValue() + " " + pageNumber);
        contentStream.endText();
    }

    private void finish(boolean printing, boolean savePDF) throws IOException {
        contentStream.close();
        if (savePDF && !printing) {
            try {
                saveDocument();
            } catch (Exception e1) {
                logger.warn("Error while saving document. ", e1);
            } finally {
                document.close();
            }
        }

        if (!savePDF && printing) {
            try {
                printDocument();
            } catch (PrinterException e) {
                logger.warn("Error while printing document. ", e);
            } finally {
                document.close();
            }
        }

        if (savePDF && printing) {
            try {
                printDocument();
                saveDocument();
            } catch (PrinterException e1) {
                logger.warn("Error while printing and saving document. ", e1);
            } finally {
                document.close();
            }
        }
    }

    private void saveDocument() throws IOException {
        String dateLine = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss"));
        logger.debug("PDF save");
        File myPath = new File(desktopFiles.getReportsFolderPath());
        if (!myPath.exists()) {
            myPath.mkdirs();
        }
        if (customer.getCustomerName().equals(PrintDialogPanelController.UNDERSPACE)) {
            document.save(myPath.toString() + File.separator + "Customer " + dateLine + ".pdf");
        } else {
            document.save(myPath.toString() + File.separator + customer.getCustomerName() + " " + dateLine + ".pdf");
        }
    }

    private void drawAndMove(String printLine) throws IOException {
        if (!Objects.equals(printLine, "")) {
            contentStream.newLineAtOffset(0, -LINES_INTERVAL);
            contentStream.showText(printLine);
        }
    }

    private PDImageXObject getCompanyImage(PDDocument document) throws IOException {
        String urlLine = CompanyDetails.LOGO_URL.get();
        if (Objects.equals(urlLine, "")) {
            return null;
        }

        BufferedImage companyImage;

        try {
            companyImage = ImageIO.read(new URL(urlLine));
            if (urlLine.endsWith("png")) {
                return LosslessFactory.createFromImage(document, companyImage);
            }
            if (urlLine.endsWith("jpg")) {
                return LosslessFactory.createFromImage(document, companyImage);
            }
        } catch (IOException e) {
            logger.error("Error while getting company image. ", e);
        }
        return null;
    }

    private void drawLine(float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }

    private void fillRect(float x, float y, float width, float height) throws IOException {
        contentStream.addRect(x, y, width, height);
        contentStream.fill();
    }

    private void bindingI18N(){

        titleValue = new SimpleStringProperty();
        injectorInfoTitle = new SimpleStringProperty();
        pumpInfoTitle = new SimpleStringProperty();
        deliveryLH = new SimpleStringProperty();
        targetPressureBar = new SimpleStringProperty();
        motorSpeedRpm = new SimpleStringProperty();
        deliveryBackflow = new SimpleStringProperty();
        testName = new SimpleStringProperty();
        customerLine = new SimpleStringProperty();
        date = new SimpleStringProperty();
        manufacturerTemplate = new SimpleStringProperty();
        model = new SimpleStringProperty();
        injector = new SimpleStringProperty();
        injectorName = new SimpleStringProperty();
        codingValue = new SimpleStringProperty();
        page = new SimpleStringProperty();
        parameterName = new SimpleStringProperty();
        measurementUnits = new SimpleStringProperty();
        delayTestName = new SimpleStringProperty();
        logoLineFirst = new SimpleStringProperty();
        logoLineSecond = new SimpleStringProperty();
        stardexSite = new SimpleStringProperty();

        titleValue.bind((i18N.createStringBinding("h4.report.table.label.value")));
        injectorInfoTitle.bind(i18N.createStringBinding("h4.report.table.label.value"));
        pumpInfoTitle.bind(i18N.createStringBinding("h4.report.table.label.pmpInfo"));
        deliveryLH.bind(i18N.createStringBinding("h4.report.table.label.nominal"));
        targetPressureBar.bind(i18N.createStringBinding("h4.report.table.label.pressure"));
        motorSpeedRpm.bind(i18N.createStringBinding("h4.report.table.label.motorSpeed"));
        deliveryBackflow.bind(i18N.createStringBinding("h4.report.table.label.delRec"));
        testName.bind(i18N.createStringBinding("h4.report.table.label.testName"));
        customerLine.bind(i18N.createStringBinding("h4.report.table.label.customer"));
        date.bind(i18N.createStringBinding("h4.report.table.label.date"));
        manufacturerTemplate.bind(i18N.createStringBinding("h4.report.table.label.defaultManufacturer"));
        model.bind(i18N.createStringBinding("h4.report.table.label.model"));
        injector.bind(i18N.createStringBinding("h4.report.table.label.injector"));
        injectorName.bind(i18N.createStringBinding("h4.report.table.label.injectorName"));
        codingValue.bind(i18N.createStringBinding("h4.report.table.label.codingValue"));
        page.bind(i18N.createStringBinding("h4.report.table.label.page"));
        parameterName.bind(i18N.createStringBinding("pdf.report.header.measurement.parameterName"));
        measurementUnits.bind(i18N.createStringBinding("pdf.report.header.measurement.measurementUnits"));
        delayTestName.bind(i18N.createStringBinding("pdf.report.header.delay.testName"));
        logoLineFirst.bind(i18N.createStringBinding("pdf.stardex.logo.line.first"));
        logoLineSecond.bind(i18N.createStringBinding("pdf.stardex.logo.line.second"));
        stardexSite.bind(i18N.createStringBinding("stardex.site"));
    }
}
