package fi.stardex.sisu.pdf;


import be.quodlibet.boxable.*;
import fi.stardex.sisu.company.CompanyDetails;
import fi.stardex.sisu.model.*;
import fi.stardex.sisu.model.cr.CodingReportModel;
import fi.stardex.sisu.model.cr.DelayReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel;
import fi.stardex.sisu.model.cr.FlowReportModel.FlowResult;
import fi.stardex.sisu.model.cr.RLC_ReportModel;
import fi.stardex.sisu.model.pump.PumpReportModel;
import fi.stardex.sisu.model.pump.PumpReportModel.PumpFlowResult;
import fi.stardex.sisu.persistence.orm.cr.inj.Injector;
import fi.stardex.sisu.persistence.orm.interfaces.Model;
import fi.stardex.sisu.persistence.orm.pump.Pump;
import fi.stardex.sisu.ui.controllers.cr.dialogs.PrintDialogPanelController;
import fi.stardex.sisu.util.DesktopFiles;
import fi.stardex.sisu.util.enums.Measurement;
import fi.stardex.sisu.util.i18n.I18N;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private StringProperty electricalCharacteristics;
    private StringProperty pumpFlowMeasurementResults;
    private StringProperty injectorNumber;
    private StringProperty code;
    private StringProperty nominalFlowRange;
    private StringProperty value;

    private Customer customer;
    private PDPageContentStream contentStream;
    private PDDocument document;
    private PDPage currentPage;
    private PDFont font;
    private LanguageModel languageModel;
    private I18N i18N;
    private List<Result> rlcResultsList;
    private List<Result> delayResultsList;
    private List<FlowResult> flowResultsList;
    private List<PumpFlowResult> pumpFlowResultsList;
    private List<Result> codingResultsList;
    private DesktopFiles desktopFiles;
    private DelayReportModel delayReportModel;
    private RLC_ReportModel rlc_reportModel;
    private CodingReportModel codingReportModel;
    private FlowReportModel flowReportModel;
    private PumpReportModel pumpReportModel;

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
    public void printAndPDFInjector(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.INJECTORS, true, true);
    }
    public void printAndPDFInjectorCoding(Customer customer, Injector injector) throws IOException {
        makePDF(customer, injector, ResultPrintMode.CODING, true, true);
    }
    public void printAndPDFPump(Customer customer, Pump pump) throws IOException {
        makePDF(customer, pump, ResultPrintMode.PUMPS, true, true);
    }

    private enum ResultPrintMode {

        INJECTORS, PUMPS, CODING
    }


    private void makePDF(Customer customer, Model model, ResultPrintMode resultMode, boolean printPDF, boolean savePDF) throws IOException {
        this.customer = customer;
        document = new PDDocument();
        currentPage = new PDPage();
        document.addPage(currentPage);

        rlcResultsList = rlc_reportModel.getResultsList();
        flowResultsList = flowReportModel.getResultsList();
        pumpFlowResultsList = pumpReportModel.getResultsList();

        delayResultsList = delayReportModel.getResultsList();
        codingResultsList = codingReportModel.getResultsList();

        drawHeaderPage(document, currentPage);

        drawCustomerAndInjectorData(customer, model);

        if (resultMode == ResultPrintMode.INJECTORS) {
            float startTable = drawTable(document, rlcResultsList, new MeasurementHeader(), START_TABLE);
            startTable = drawFlowTable(document, new FlowHeader(), startTable, flowResultsList);
            drawTable(document, delayResultsList, new DelayHeader(), startTable);
        } else if (resultMode == ResultPrintMode.CODING) {
            drawTable(document, codingResultsList, new CodingHeader(), START_TABLE);
        } else if (resultMode == ResultPrintMode.PUMPS) {
            drawFlowTable(document, new PumpHeader(), START_TABLE, pumpFlowResultsList);
        }
        finish(printPDF, savePDF);
    }

    private float drawTable(PDDocument document, List<? extends Result> resultsList,
                            Header header, float yStart) throws IOException {
        if (resultsList != null && !resultsList.isEmpty()) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);

            drawData(baseTable, resultsList);

            float newStartY = baseTable.draw() - CELL_HEIGHT;
            checkForNewPage(baseTable);
            return newStartY;
        }
        return yStart;
    }

    private float drawFlowTable(PDDocument document, Header header, float yStart, List result) throws IOException  {

        if (result != null && !result.isEmpty()) {
            BaseTable baseTable = createTable(document, yStart);
            drawHeader(header, baseTable);

            try {
                if (header instanceof FlowHeader) {
                    drawInjectorFlowData(baseTable);
                }else if(header instanceof PumpHeader){
                    drawPumpFlowData(baseTable);
                }
                else {
                    logger.error("Unknown header class, impossible to define FlowTable type");
                    throw new RuntimeException("Unknown header class, impossible to define FlowTable type");
                }
                float newStartY = baseTable.draw() - CELL_HEIGHT;
                checkForNewPage(baseTable);
                return newStartY;
            } catch (RuntimeException e) {
                e.printStackTrace();
                return yStart;
            }
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

    private void drawData(BaseTable baseTable, List<? extends Result> resultsStorage) {

        for (Result result : resultsStorage) {
            if(result == null)
                continue;

            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = result.getMainColumn() == null ? null : row.createCell(result.getMainColumn());
            cell = result.getSubColumn1() == null ? null : row.createCell(result.getSubColumn1());
            cell = result.getSubColumn2() == null ? null : row.createCell(result.getSubColumn2());
            if (null != result.getValueColumns()) {
                for (String value : result.getValueColumns()) {
                    row.createCell(value);
                }
            }
        }
        setCellSettings(baseTable);
    }

    private void drawInjectorFlowData(BaseTable baseTable) {

        for (FlowResult result: flowResultsList) {
            Row<PDPage> row = baseTable.createRow(CELL_HEIGHT);
            Cell<PDPage> cell = row.createCell(result.getMainColumn());
            row.createCell(result.getSubColumn1());
            row.createCell(result.getSubColumn2());
            cell = row.createCell(result.getFlow1());
            Color color = getColorCellOfResult(result.getFlow1_double(), result);
            cell.setFillColor(color);
            cell = row.createCell(result.getFlow2());
            color = getColorCellOfResult(result.getFlow2_double(), result);
            cell.setFillColor(color);
            cell = row.createCell(result.getFlow3());
            color = getColorCellOfResult(result.getFlow3_double(), result);
            cell.setFillColor(color);
            cell = row.createCell(result.getFlow4());
            color = getColorCellOfResult(result.getFlow4_double(), result);
            cell.setFillColor(color);
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

            nominal = result.nominalDeliveryFlowProperty().get();
            cell = row.createCell(nominal);

            flow = result.getDelivery_double();
            cell = row.createCell(String.valueOf(flow));
            Color color = getColorCellOfResult(flow, result, Measurement.DELIVERY);
            cell.setFillColor(color);

            nominal = result.nominalBackFlowProperty().get();
            cell = row.createCell(nominal);
            flow = result.getBackFlow_double();
            cell = row.createCell(String.valueOf(flow));
            color = getColorCellOfResult(flow, result, Measurement.BACK_FLOW);
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

        if(languageModel.languageProperty().get().equals("RUSSIAN")){
            inputStream = this.getClass().getResourceAsStream("/fonts/Arial_Cyr.ttf");
        }
        else {
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
            infoLine = injectorInfoTitle.getValue();
        }

        if (model instanceof Pump) {
            manufacturer = ((Pump) model).getManufacturerPump().toString();
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

    private Color getColorCellOfResult(double flow, FlowResult result){

        double nominalRight = result.getFlowRangeRight();
        double nominalLeft = result.getFlowRangeLeft();
        double nominalExceptRight = result.getAcceptableFlowRangeRight();
        double nominalExceptLeft = result.getAcceptableFlowRangeLeft();

        /** Change color if data out of borders : 5% - in borders - yellow, out of borders - red*/
        Color cellColor = Color.WHITE;

        if(Double.compare(flow, nominalRight) == 0 || flow > nominalRight || Double.compare(flow, nominalLeft) == 0 || flow < nominalLeft){
            cellColor = Color.ORANGE;
        }
        if(Double.compare(flow, nominalExceptRight) == 0 || flow > nominalExceptRight || Double.compare(flow, nominalExceptLeft) == 0 || flow < nominalExceptLeft) {
            cellColor = Color.RED;
        }
        if(flow < 0){
            cellColor = Color.WHITE;
        }
        return cellColor;
    }

    private Color getColorCellOfResult(double flow, PumpFlowResult result, Measurement flowType){

        double nominalRight = result.getFlowRangeRight(flowType);
        double nominalLeft = result.getFlowRangeLeft(flowType);
        double nominalExceptRight = result.getAcceptableFlowRangeRight(flowType);
        double nominalExceptLeft = result.getAcceptableFlowRangeLeft(flowType);

        /** Change color if data out of borders : 5% - in borders - yellow, out of borders - red*/
        Color cellColor = Color.WHITE;

        if (nominalRight == 0 && nominalLeft == 0) {
            return cellColor;
        }

        if(Double.compare(flow, nominalRight) == 0 || flow > nominalRight || Double.compare(flow, nominalLeft) == 0 || flow < nominalLeft){
            cellColor = Color.ORANGE;
        }
        if(Double.compare(flow, nominalExceptRight) == 0 || flow > nominalExceptRight || Double.compare(flow, nominalExceptLeft) == 0 || flow < nominalExceptLeft) {
            cellColor = Color.RED;
        }
        if(flow < 0){
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
                new HeaderCell(8, "RPM"),
                new HeaderCell(8, "Bar"),
                new HeaderCell(8, "SCV"),
                new HeaderCell(8, "PCV"),
                new HeaderCell(14, deliveryLH.get()),
                new HeaderCell(11, delivery.get()),
                new HeaderCell(14, deliveryLH.get()),
                new HeaderCell(11, backFlow.get())));
        }
    }

    private class HeaderCell {

        private float width;
        private String value;

        public HeaderCell(float width, String value) {
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
}
