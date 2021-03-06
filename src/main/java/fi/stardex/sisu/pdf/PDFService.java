package fi.stardex.sisu.pdf;


import be.quodlibet.boxable.*;
import fi.stardex.sisu.company.CompanyDetails;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.DelayReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.model.cr.RLC_ReportModel;
import fi.stardex.sisu.model.pump.PumpReportModel;
import fi.stardex.sisu.model.pump.PumpReportModel.PumpFlowResult;
import fi.stardex.sisu.model.uis.UisBipModel;
import fi.stardex.sisu.model.uis.UisDelayModel;
import fi.stardex.sisu.model.uis.UisFlowModel;
import fi.stardex.sisu.model.uis.UisRlcModel;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.persistence.orm.uis.InjectorUIS;
import fi.stardex.sisu.ui.controllers.cr.dialogs.PrintDialogPanelController;
import fi.stardex.sisu.util.DesktopFiles;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.i18n.I18N;
import fi.stardex.sisu.util.i18n.Locales;
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
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

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
    private StringProperty delivery;
    private StringProperty backFlow;
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
    private StringProperty codingResults;
    private StringProperty injectionDelayMeasurementResults;
    private StringProperty flowMeasurementResults;
    private StringProperty bipMeasurementRepults;
    private StringProperty electricalCharacteristics;
    private StringProperty pumpFlowMeasurementResults;
    private StringProperty injectorNumber;
    private StringProperty code;
    private StringProperty nominalFlowRange;
    private StringProperty value;
    @Value("${logo.image}")
    private String logoPath;
    @Value("${customer}")
    private String client;

    private Customer customer;
    private PDPageContentStream contentStream;
    private PDDocument document;
    private PDPage currentPage;
    private PDFont font;
    private LanguageModel languageModel;
    private I18N i18N;
    private List<PumpFlowResult> pumpFlowResultsList;
    private DesktopFiles desktopFiles;
    private DelayReportModel delayReportModel;
    private RLC_ReportModel rlc_reportModel;
    private CodingReportModel codingReportModel;
    private FlowReportModel flowReportModel;
    private PumpReportModel pumpReportModel;
    private UisFlowModel uisFlowModel;
    private UisDelayModel uisDelayModel;
    private UisRlcModel uisRlcModel;
    private UisBipModel uisBipModel;

    public void setDesktopFiles(DesktopFiles desktopFiles) {
        this.desktopFiles = desktopFiles;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }


    public void setDelayReportModel(DelayReportModel delayReportModel) {
        this.delayReportModel = delayReportModel;
    }

    public void setRlc_reportModel(RLC_ReportModel rlc_reportModel) {
        this.rlc_reportModel = rlc_reportModel;
    }

    public void setCodingReportModel(CodingReportModel codingReportModel) {
        this.codingReportModel = codingReportModel;
    }

    public void setFlowReportModel(FlowReportModel flowReportModel) {
        this.flowReportModel = flowReportModel;
    }

    public void setPumpReportModel(PumpReportModel pumpReportModel) {
        this.pumpReportModel = pumpReportModel;
    }

    public void setUisFlowModel(UisFlowModel uisFlowModel) {
        this.uisFlowModel = uisFlowModel;
    }

    public void setUisDelayModel(UisDelayModel uisDelayModel) {
        this.uisDelayModel = uisDelayModel;
    }

    public void setUisRlcModel(UisRlcModel uisRlcModel) {
        this.uisRlcModel = uisRlcModel;
    }

    public void setUisBipModel(UisBipModel uisBipModel) {
        this.uisBipModel = uisBipModel;
    }

    public void setI18N(I18N i18N) {
        this.i18N = i18N;
    }

    private PrinterJob pjob = PrinterJob.getPrinterJob();

    @PostConstruct
    public void init() {

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
    public void makePDFForPump(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, false, true);
    }
    public void makePDFForUis(Customer customer, InjectorUIS injectorUIS) throws IOException  {
        makePDF(customer, injectorUIS, ResultPrintMode.UIS, false, true);
    }
    public void makePDFForInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.CODING, false, true);
    }
    public void printInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, CurrentInjectorObtainer.getInjector(), ResultPrintMode.INJECTORS, true, false);
    }
    public void printInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, CurrentInjectorObtainer.getInjector(), ResultPrintMode.CODING, true, false);
    }
    public void printPump(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, true, false);
    }
    public void printUis(Customer customer, InjectorUIS injectorUIS) throws IOException  {
        makePDF(customer, injectorUIS, ResultPrintMode.UIS, true, false);
    }
    public void printAndPDFInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.INJECTORS, true, true);
    }
    public void printAndPDFInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.CODING, true, true);
    }
    public void printAndPDFPump(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, true, true);
    }
    public void printAndPDFUis(Customer customer, InjectorUIS injectorUIS) throws IOException  {
        makePDF(customer, injectorUIS, ResultPrintMode.UIS, true, true);
    }

    private enum ResultPrintMode {

        INJECTORS, PUMPS, CODING, UIS
    }

    private void makePDF(Customer customer, Model model, ResultPrintMode resultMode, boolean printPDF, boolean savePDF) throws IOException {
        this.customer = customer;
        document = new PDDocument();
        currentPage = new PDPage();
        document.addPage(currentPage);

        drawHeaderPage(document, currentPage);

        drawCustomerAndInjectorData(customer, model);

        if (resultMode == ResultPrintMode.INJECTORS) {
            float startTable = drawTable(document, rlc_reportModel.getResultsList(), new MeasurementHeader(), START_TABLE);
            startTable = drawTable(document, flowReportModel.getResultsList(), new FlowHeader(), startTable);
            drawTable(document, delayReportModel.getResultsList(), new DelayHeader(), startTable);
        } else if (resultMode == ResultPrintMode.CODING) {
            float startTable =  drawTable(document, codingReportModel.getResultsList(), new CodingHeader(), START_TABLE);
            drawTable(document, flowReportModel.getResultsList(), new FlowHeader(), startTable);
        } else if (resultMode == ResultPrintMode.PUMPS) {
            pumpFlowResultsList = pumpReportModel.getResultsList();
            drawTable(document, pumpFlowResultsList, new PumpHeader(), START_TABLE);
        } else if (resultMode == ResultPrintMode.UIS) {
            float startTable = drawTable(document, uisRlcModel.getResultsList(), new UisMeasurementHeader(), START_TABLE);
            startTable = drawTable(document, uisFlowModel.getResultsList(), new UisFlowHeader(), startTable);
            startTable = drawTable(document, uisBipModel.getResultsList(), new BipHeader(), startTable);
            drawTable(document, uisDelayModel.getResultsList(), new UisDelayHeader(), startTable);
        }
        finish(printPDF, savePDF);
    }

    private float drawTable(PDDocument document, List<? extends Result> resultsList, Header header, float yStart) throws IOException  {

        if (resultsList != null && !resultsList.isEmpty()) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);

            if (header instanceof PumpHeader) {
                drawPumpFlowData(baseTable);
            }else {
                drawData(baseTable, resultsList);
            }

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

    private void checkForNewPage(BaseTable baseTable) {
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

    private void drawData(BaseTable baseTable, List<? extends Result> resultsList) {

        for (Result result : resultsList) {
            if(result == null) continue;

            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = result.getMainColumn() == null ? null : row.createCell(result.getMainColumn());
            cell = result.getSubColumn1() == null ? null : row.createCell(result.getSubColumn1());
            cell = result.getSubColumn2() == null ? null : row.createCell(fixKorean(result.getSubColumn2()));
            List<String> textColumns = result.getValueColumns();
            List<Double> numericDataColumns = result.getNumericDataColumns();

            if (textColumns != null) {
                for (String textResult : textColumns) {
                    cell = row.createCell(textResult);
                    if (numericDataColumns != null) {
                        int i = textColumns.indexOf(textResult);
                        Color color = getColorCellOfResult(numericDataColumns.get(i), result);
                        cell.setFillColor(color);
                    }
                }
            }

            setCellSettings(baseTable);
        }
    }

    private void drawPumpFlowData(BaseTable baseTable) {

        String nominal;
        double flow;

        for (PumpFlowResult result : pumpFlowResultsList) {

            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = row.createCell(result.getMainColumn());
            row.createCell(result.getSubColumn1());
            row.createCell(result.getSubColumn2());

            //insert cell creation for SCV & PSV values
            cell = row.createCell(result.getSubColumn3());
            cell = row.createCell(result.getSubColumn4());

            nominal = fixKorean(result.nominalDeliveryFlowProperty().get());
            cell = row.createCell(nominal);

            flow = result.getDelivery_double();
            cell = row.createCell(String.valueOf(flow));
            Color color = getColorCellOfResult(flow, result, Measurement.DELIVERY);
            cell.setFillColor(color);

            nominal = fixKorean(result.nominalBackFlowProperty().get());
            cell = row.createCell(nominal);
            flow = result.getBackFlow_double();
            cell = row.createCell(String.valueOf(flow));
            color = getColorCellOfResult(flow, result, Measurement.BACK_FLOW);
            cell.setFillColor(color);

            setCellSettings(baseTable);
        }
    }

    private Color getColorCellOfResult(double flow, Result result){

        double nominalRight = result.getRangeRight();
        double nominalLeft = result.getRangeLeft();
        double acceptableRight = result.getAcceptableRangeRight();
        double acceptableLeft = result.getAcceptableRangeLeft();

        return getGolor(flow, nominalLeft, nominalRight, acceptableLeft, acceptableRight);
    }

    private Color getColorCellOfResult(double flow, PumpFlowResult result, Measurement flowType){

        double nominalRight = result.getRangeRight(flowType);
        double nominalLeft = result.getRangeLeft(flowType);
        double acceptableRight = result.getAcceptableRangeRight(flowType);
        double acceptableLeft = result.getAcceptableRangeLeft(flowType);

        return getGolor(flow, nominalLeft, nominalRight, acceptableLeft, acceptableRight);
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
        BufferedImage stardexImage = ImageIO.read(getClass().getResource("/" + logoPath));
        PDImageXObject stardexObjectImage = LosslessFactory.createFromImage(document, stardexImage);
        contentStream = new PDPageContentStream(document, page);

        if (companyLogoImage != null) {
            drawScaledXObject(companyLogoImage, 20, 650, 250, 120);
        }
        drawScaledXObject(stardexObjectImage, 390, 690, 170, 60);

        contentStream.beginText();

        InputStream inputStream;

        if(languageModel.languageProperty().get() == Locales.RUSSIAN){
            inputStream = this.getClass().getResourceAsStream("/fonts/Arial_Cyr.ttf");
        }
        else if (languageModel.languageProperty().get() == Locales.KOREAN) {
            inputStream = this.getClass().getResourceAsStream("/fonts/NanumGothic-Regular.ttf");
        }
        else {
            inputStream = this.getClass().getResourceAsStream("/fonts/LiberationSans-Bold.ttf");
        }
        font = PDType0Font.load(document, inputStream);

        contentStream.setFont(font, FONT);

        contentStream.newLineAtOffset(390, 630);
        if (!client.equals("merlin")) {
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
        }

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
            infoLine = injectorInfoTitle.getValue();
        }

        if (model instanceof Pump) {
            manufacturer = ((Pump) model).getManufacturer().toString();
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
        String dateLine = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss"));
        logger.debug("PDF save");
        File myPath = new File(desktopFiles.getReportsFolderPath()
                + File.separator +  LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                + File.separator + (customer.getCustomerName().equals(PrintDialogPanelController.UNDERSPACE)? "Customer" : customer.getCustomerName()));
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

    private PDImageXObject getCompanyImage(PDDocument document) {
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


    /** Change color if data out of borders : 5% - in borders - yellow, out of borders - red*/
    private Color getGolor(double value, double nominalLeft, double nominalRight, double acceptableLeft, double acceptableRight) {

        Color cellColor = Color.WHITE;

        if(Double.compare(value, nominalRight) == 0 || value > nominalRight || Double.compare(value, nominalLeft) == 0 || value < nominalLeft){
            cellColor = Color.ORANGE;
        }
        if(Double.compare(value, acceptableRight) == 0 || value > acceptableRight || Double.compare(value, acceptableLeft) == 0 || value < acceptableLeft) {
            cellColor = Color.RED;
        }
        if(value < 0 || (nominalLeft == 0 && nominalRight == 0 && acceptableLeft == 0 && acceptableRight == 0)){
            cellColor = Color.WHITE;
        }
        return cellColor;
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
        delivery = new SimpleStringProperty();
        backFlow = new SimpleStringProperty();

        codingResults = new SimpleStringProperty();
        injectionDelayMeasurementResults = new SimpleStringProperty();
        flowMeasurementResults = new SimpleStringProperty();
        bipMeasurementRepults = new SimpleStringProperty();
        electricalCharacteristics = new SimpleStringProperty();
        pumpFlowMeasurementResults = new SimpleStringProperty();
        injectorNumber = new SimpleStringProperty();
        code = new SimpleStringProperty();

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
        codingResults.bind((i18N.createStringBinding("h4.report.table.label.codingResults")));
        injectionDelayMeasurementResults.bind(i18N.createStringBinding("h4.report.table.label.injectionDelayMeasurementResults"));
        flowMeasurementResults.bind(i18N.createStringBinding("h4.report.table.label.flowMeasurementResults"));
        bipMeasurementRepults.bind(i18N.createStringBinding("h4.report.table.label.bipMeasurementResults"));
        electricalCharacteristics.bind(i18N.createStringBinding("h4.report.table.label.electricalCharacteristics"));
        pumpFlowMeasurementResults.bind(i18N.createStringBinding("h4.report.table.label.pumpFlowMeasurementResults"));
        injectorNumber.bind(i18N.createStringBinding("h4.report.table.label.injectorNumber"));
        code.bind(i18N.createStringBinding("h4.report.table.label.code"));
        delivery.bind(i18N.createStringBinding("flow.label.delivery"));
        backFlow.bind(i18N.createStringBinding("flow.label.backflow"));
    }

    private class CodingHeader  implements Header {

        public String getMainHeader() {
            return codingResults.getValue();
        }

        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(new HeaderCell(20, injectorNumber.getValue()), new HeaderCell(80, code.getValue())));
        }
    }

    private class DelayHeader implements Header {

        @Override
        public String getMainHeader() {
            return injectionDelayMeasurementResults.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(new HeaderCell(25, delayTestName.getValue()), new HeaderCell(25, measurementUnits.getValue()),
                    new HeaderCell(12.5f, "(1)"), new HeaderCell(12.5f, "(2)"), new HeaderCell(12.5f, "(3)"), new HeaderCell(12.5f, "(4)")));
        }
    }

    private class UisDelayHeader implements Header {

        @Override
        public String getMainHeader() {
            return injectionDelayMeasurementResults.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(
                    new HeaderCell(18, delayTestName.getValue()),
                    new HeaderCell(18, measurementUnits.getValue()),
                    new HeaderCell(8, "(1)"),
                    new HeaderCell(8, "(2)"),
                    new HeaderCell(8, "(3)"),
                    new HeaderCell(8, "(4)"),
                    new HeaderCell(8, "(5)"),
                    new HeaderCell(8, "(6)"),
                    new HeaderCell(8, "(7)"),
                    new HeaderCell(8, "(8)")));
        }
    }

    private class FlowHeader implements Header {

        @Override
        public String getMainHeader() {
            return flowMeasurementResults.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(new HeaderCell(19, testName.getValue()), new HeaderCell(21, deliveryBackflow.getValue()),
                    new HeaderCell(24, deliveryLH.get()), new HeaderCell(9, "(1)"), new HeaderCell(9, "(2)"),
                    new HeaderCell(9, "(3)"), new HeaderCell(9, "(4)")));
        }
    }

    private class UisFlowHeader implements Header {

        @Override
        public String getMainHeader() {
            return flowMeasurementResults.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(
                    new HeaderCell(18, testName.getValue()),
                    new HeaderCell(18, deliveryLH.get()),
                    new HeaderCell(8, "(1)"),
                    new HeaderCell(8, "(2)"),
                    new HeaderCell(8, "(3)"),
                    new HeaderCell(8, "(4)"),
                    new HeaderCell(8, "(5)"),
                    new HeaderCell(8, "(6)"),
                    new HeaderCell(8, "(7)"),
                    new HeaderCell(8, "(8)"))
            );
        }
    }

    private class BipHeader implements Header {
        @Override
        public String getMainHeader() {
            return bipMeasurementRepults.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(
                    new HeaderCell(18, testName.getValue()),
                    new HeaderCell(18, deliveryLH.get()),
                    new HeaderCell(8, "(1)"),
                    new HeaderCell(8, "(2)"),
                    new HeaderCell(8, "(3)"),
                    new HeaderCell(8, "(4)"),
                    new HeaderCell(8, "(5)"),
                    new HeaderCell(8, "(6)"),
                    new HeaderCell(8, "(7)"),
                    new HeaderCell(8, "(8)"))
            );
        }
    }


    private class UisMeasurementHeader implements Header{

        @Override
        public String getMainHeader() {
            return electricalCharacteristics.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(
                    new HeaderCell(18, parameterName.getValue()),
                    new HeaderCell(18, measurementUnits.getValue()),
                    new HeaderCell(8, "(1)"),
                    new HeaderCell(8, "(2)"),
                    new HeaderCell(8, "(3)"),
                    new HeaderCell(8, "(4)"),
                    new HeaderCell(8, "(5)"),
                    new HeaderCell(8, "(6)"),
                    new HeaderCell(8, "(7)"),
                    new HeaderCell(8, "(8)")));
        }
    }

    private class MeasurementHeader implements Header {

        @Override
        public String getMainHeader() {
            return electricalCharacteristics.getValue();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(new HeaderCell(25, parameterName.getValue()), new HeaderCell(25, measurementUnits.getValue()),
                    new HeaderCell(12.5f, "(1)"), new HeaderCell(12.5f, "(2)"), new HeaderCell(12.5f, "(3)"), new HeaderCell(12.5f, "(4)")));
        }
    }

    private class PumpHeader implements Header {
        @Override
        public String getMainHeader() {
            return pumpFlowMeasurementResults.get();
        }

        @Override
        public List<HeaderCell> getHeaderCells() {
            return new ArrayList<>(Arrays.asList(
                new HeaderCell(18, testName.get()),
                new HeaderCell(7, "RPM"),
                new HeaderCell(7, "Bar"),
                new HeaderCell(7, "SCV"),
                new HeaderCell(7, "PCV"),
                new HeaderCell(17, deliveryLH.get()),
                new HeaderCell(10, delivery.get()),
                new HeaderCell(17, deliveryLH.get()),
                new HeaderCell(10, backFlow.get())));
        }
    }

    private class HeaderCell {

        private float width;
        private String value;

        HeaderCell(float width, String value) {
            this.width = width;
            this.value = value;
        }

        public float getWidth() {
            return width;
        }

        public String getValue() {
            return value;
        }
    }

    private interface Header {

        String getMainHeader();
        List<PDFService.HeaderCell> getHeaderCells();
    }

    private String fixKorean(String str) {
        if (languageModel.languageProperty().get() == Locales.KOREAN) {
            str = str.replace(" \u00B1 ", "+/-");
        }
        return str;
    }
}
