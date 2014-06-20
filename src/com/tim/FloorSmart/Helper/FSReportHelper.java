package com.tim.FloorSmart.Helper;

import android.content.res.AssetManager;
import android.graphics.*;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import com.pdfjet.*;
import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;
import com.tim.FloorSmart.R;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: hanyong
 * Date: 6/19/14
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSReportHelper {
    static String kLabelKey = "label";
    static String kDescriptionKey = "description";
    static String kNewLineKey = "newLineSeparator";
    static String kFontSizeKey = "fontSize";

    static String kDateKey = "date";
    static String kReadingsKey = "readings";
    static String kLocProductKey = "locProduct";
    static String kLocationKey = "location";
    static String kDateReadingsKey = "datereadings";
    static String kLocLocProductKey = "loclocProduct";

    static float kHeaderHeight = 110.f;
    static float kSubtitleHeight = 120.f;
    static float kDateHeight = 50.f;
    static float kRowHeight = 40.0f;
    static float kStatisticHeight = 40.f;
    static float kStatisticTableHeight = 100.f;
    static float kLastReadingTableHeight = 100.f;
    static float kGap = 10.f;

    static float kBorderInset = 20.0f;
    static float kBorderWidth = 1.0f;
    static float kMarginInset = 10.0f;
    static float kLineWidth = 1.0f;

    static float A4PAPER_WIDTH_IN_PORTRATE = 1240.0f;
    static float A4PAPER_HEIGHT_IN_PORTRATE = 1753.0f;

    static String FS_REPORT_VALUE_SEPARATOR = ";+;";

    int kPadding = 25;
    static int countData;

    static String kEmptyPlaceholder = "EMPTY";

    PDF pdf;
    Page curPage;

    class DateReading
    {
        public Date date;
        public ArrayList<FSReading> readings;
    }

    class LocDateReading
    {
        public FSLocProduct locProduct;
        public ArrayList<DateReading> datereadings;
    }

    class LocLocProduct
    {
        public FSLocation location;
        public ArrayList<LocDateReading> loclocProduct;
    }

    class Size
    {
        public int cx;
        public int cy;

        public Size()
        {
            cx = 0;
            cy = 0;
        }

        public Size(int cx, int cy)
        {
            this.cx = cx;
            this.cy = cy;
        }
    }

    Size pageSize;
    ArrayList<LocLocProduct> fsReportArray;
    FSJob job;

    public FSReportHelper()
    {
    }

    private String getStringFromDate(Date date) {

        GlobalData globalData = GlobalData.sharedData();
        return CommonMethods.date2str(date, globalData.settingDateFormat);
    }

    private void fillReportData(FSJob job) {
        ArrayList<FSLocation> arrayLoc = DataManager.sharedInstance().getLocations(job.jobID, true);

        for (int i = 0; i < arrayLoc.size(); i++) {
            FSLocation loc = arrayLoc.get(i);
            ArrayList<FSLocProduct> arrayLocProduct = DataManager.sharedInstance().getLocProducts(loc, "", true);

            ArrayList<LocDateReading> arrayLocProductDates = new ArrayList<LocDateReading>();

            for (int j = 0; j < arrayLocProduct.size(); j++) {
                FSLocProduct locProduct = arrayLocProduct.get(j);
                ArrayList<Date> arrayDates = DataManager.sharedInstance().getAllReadingDates(locProduct.locProductID);

                ArrayList<DateReading> arrayDateReadings = new ArrayList<DateReading>();
                for (int k = 0; k < arrayDates.size(); k++)
                {
                    Date date = arrayDates.get(k);
                    ArrayList<FSReading> arrayReadings = DataManager.sharedInstance().getReadings(locProduct.locProductID, date);

                    if (arrayReadings.size() > 0)
                    {
                        DateReading newData = new DateReading();
                        newData.date = date;
                        newData.readings = arrayReadings;
                        arrayDateReadings.add(newData);
                    }
                }


                if (arrayDateReadings.size() > 0)
                {
                    LocDateReading newData = new LocDateReading();
                    newData.locProduct = locProduct;
                    newData.datereadings = arrayDateReadings;
                    arrayLocProductDates.add(newData);
                }
            }
            if (arrayLocProductDates.size() > 0)
            {
                if (fsReportArray == null)
                    fsReportArray = new ArrayList<LocLocProduct>();

                LocLocProduct newData = new LocLocProduct();
                newData.location = loc;
                newData.loclocProduct = arrayLocProductDates;
                fsReportArray.add(newData);
            }
        }
    }

    private Rect drawLabel(String label, String details, Point origin, float fontSize, boolean newLineSeparator)
    {
        Rect totalFrame = new Rect();

        try
        {
            Font labelFont = new Font(pdf, CoreFont.HELVETICA_BOLD);
            labelFont.setSize(fontSize);

            Font detailFont = new Font(pdf, CoreFont.HELVETICA);
            detailFont.setSize(fontSize);

            TextLine labelText = new TextLine(labelFont, label);
            TextLine detailText = new TextLine(detailFont, details);

            Rect labelFrame = getRectWithOriginSize(origin, labelText, 700, 300);

            Rect detailsFrame = getRectWithOriginSize(new Point(0, 0), detailText, 300, 100);
            totalFrame = new Rect(origin.x, origin.y, origin.x, origin.y);

            if (newLineSeparator) {
                detailsFrame.left = origin.x;
                detailsFrame.top = origin.y + labelFrame.height() + 10;

                totalFrame.right = totalFrame.left + Math.max(labelFrame.width(), detailsFrame.width());
                totalFrame.bottom = totalFrame.top + labelFrame.height() + detailsFrame.height() + 10;
            }
            else {
                detailsFrame.left = origin.x + labelFrame.width() + 10;
                detailsFrame.top = origin.y;

                totalFrame.right = totalFrame.left + labelFrame.width() + detailsFrame.width() + 10;
                totalFrame.bottom = totalFrame.top + Math.max(labelFrame.height(), detailsFrame.height());
            }

            drawText(label, labelFrame, labelFont);
            drawText(details, detailsFrame, detailFont);
        }
        catch (Exception e)
        {

        }

        return totalFrame;
    }

    class LabelInfo
    {
        public String label;
        public String description;
        public int fontsize;

        public LabelInfo(String label, String description, int fontsize)
        {
            this.label = label;
            this.description = description;
            this.fontsize = fontsize;
        }
    }

    private void renderFirstPage(FSJob aJob, String dateStr) {
        try
        {
            Page page = new Page(pdf, A4.PORTRAIT);
            curPage = page;
            drawImage();

            Rect previousRect = new Rect();
            previousRect.set((int)(kBorderInset + kMarginInset+50), (int)(kBorderInset + kMarginInset + 150.0), (int)(kBorderInset + kMarginInset+50), (int)(kBorderInset + kMarginInset + 150.0));

            ArrayList<LabelInfo> strings = new ArrayList<LabelInfo>();
            LabelInfo jobname = new LabelInfo("Job Name:", job.jobName, 24);
            LabelInfo date = new LabelInfo("Date:", dateStr, 24);

            strings.add(jobname);
            strings.add(date);

            for (int i = 0; i < strings.size(); i++)
            {
                LabelInfo _info = strings.get(i);

                previousRect = drawLabel(_info.label, _info.description, new Point(previousRect.left, previousRect.height() + previousRect.top + 10), _info.fontsize, false);
            }

            if (true) {
                drawLogoImage();
            }
        }
        catch (Exception e)
        {

        }
    }

    private void renderHeader(FSJob job, FSLocation loc) {
        try
        {
            Page headerPage = new Page(pdf, A4.PORTRAIT);
            curPage = headerPage;

            Font font = new Font(pdf, CoreFont.HELVETICA);
            font.setSize(12);

            String strHeader = String.format("Job: %s \t\t Location: %s", job.jobName, loc.locName);

            TextLine txtHeader = new TextLine(font, strHeader);
            float width = txtHeader.getWidth() + 20;

            drawText(strHeader, new Rect(50, 60, (int)width, 20), font);

            Point from = new Point(40, 90);
            Point to = new Point(pageSize.cy - 80, 90);

            drawLineFromPoint(from, to);
        }
        catch (Exception e)
        {

        }

    }

    private void renderFooter(int currentPage) {
        drawPageNumber(currentPage);
    }

    private void renderSubtitle(float ypos, FSLocation loc, FSLocProduct locProduct) {
        try
        {
            ypos += 30;

            Font font = new Font(pdf, CoreFont.HELVETICA);
            font.setSize(22);
            String strHeader = String.format("Location: %s", loc.locName);

            TextLine txtLine = new TextLine(font, strHeader);

            float width = txtLine.getWidth() + 20;

            drawText(strHeader, new Rect(60, (int)ypos, (int)width, 20), font);

            ypos += 30;

            strHeader = String.format("Product: %s", locProduct.locProductName);
            txtLine.setText(strHeader);

            width = txtLine.getWidth() + 20;

            drawText(strHeader, new Rect(60, (int)ypos, (int)width, 30), font);

            if (locProduct.locProductName.equals(DataManager.FMD_DEFAULT_PRODUCTNAME))
            {
                int xpos = 60 + (int)width;
                strHeader = String.format("(%s)", FSProduct.getDisplayProductType((int) locProduct.locProductType));
                font.setSize(22);

                txtLine.setFont(font);
                txtLine.setText(strHeader);
                width = txtLine.getWidth() + 20;

                drawText(strHeader, new Rect((int)xpos, (int)ypos, (int)width, 30), font);
            }

            ypos += 30;

            font.setSize(22);
            GlobalData globalData = GlobalData.sharedData();

            if (globalData.settingArea == GlobalData.AREA_FEET) //feet
                strHeader = String.format("Coverage: %.1f square feet", locProduct.locProductCoverage);
            else
            {
                strHeader = String.format("Coverage: %.1f square meter", globalData.sqft2sqm((float)locProduct.locProductCoverage));
            }

            font.setItalic(true);
            txtLine.setFont(font);
            txtLine.setText(strHeader);
            width = txtLine.getWidth() + 20;

            drawText(strHeader, new Rect(60, (int)ypos, (int)width, 30), font);
        }
        catch (Exception e)
        {

        }
    }

    private void renderDate(float ypos, Date date) {
        try
        {
            GlobalData globalData = GlobalData.sharedData();

            Font font = new Font(pdf, CoreFont.HELVETICA);
            font.setSize(18);
            String strDate = String.format("Date: %s", CommonMethods.date2str(date, globalData.settingDateFormat));

            TextLine txtline = new TextLine(font, strDate);

            float width = txtline.getWidth() + 20;

            drawText(strDate, new Rect(80, (int)ypos + 10, (int)width, 30), font);

            ypos += 25;
        }
        catch (Exception e)
        {

        }
    }

    private boolean isInPage(float ypos) {
        if (ypos >= A4PAPER_HEIGHT_IN_PORTRATE-120)
            return false;
        return true;
    }

    private float heightRemains(float ypos) {
        return A4PAPER_HEIGHT_IN_PORTRATE-120-ypos;
    }

    private void renderStatistics(float ypos, ArrayList<FSReading> arrayReadings) {

        float xOrigin = 100;
        float yOrigin = ypos;
        // draw statistics

        try
        {
            Font font = new Font(pdf, CoreFont.HELVETICA);
            font.setSize(18);

            float mcavg = FSReading.getMCAvg(arrayReadings);
            float rhavg = FSReading.getRHAvg(arrayReadings);
            float tempavg = FSReading.getTempAvg(arrayReadings);
            float emcavg = FSReading.getEmcAvg(arrayReadings);

            GlobalData globalData = GlobalData.sharedData();
            if (globalData.settingTemp == GlobalData.TEMP_FAHRENHEIT)
                tempavg = FSReading.getFTemperature(tempavg);

            String strStatistic = String.format("MC Avg: %.1f%%;\t\tRH Avg: %d%%;\t\tTemp Avg: %d%s;\t\tEMC Avg:%.1f%%;", mcavg, Math.round(rhavg), Math.round(tempavg), globalData.getTempUnit(), emcavg);

            //\t\ts.g.:%ld%%;\t\t;Material:%@;
            //[FSReading getDisplayDepth:lastReading.readDepth], [FSReading getDisplayMaterial:lastReading.readMaterial]

            TextLine txtline = new TextLine(font, strStatistic);
            float width = txtline.getWidth() + 20;

            drawText(strStatistic, new Rect((int)xOrigin, (int)yOrigin, (int)width, 30), font);

            ypos += 25;
        }
        catch (Exception e)
        {

        }
    }

    private void renderStatisticsTable(float ypos, ArrayList<FSReading> arrayReadings) {
        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 160;
        int numberOfColumns = 6;

        GlobalData globalData = GlobalData.sharedData();

        // table header

        try
        {
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("MC Avg (%)");
            labels.add("MC High (%)");
            labels.add("EMC Avg (%)");
            labels.add("RH Avg (%)");
            labels.add(String.format("Temp Avg (%s)", globalData.getTempUnit()));

            Font font = new Font(pdf, CoreFont.HELVETICA_BOLD);
            font.setSize(18);

            for (int i = 0; i < labels.size(); i++) {
                drawText(labels.get(i), new Rect((int)(xOrigin + 20 + columnWidth * i), (int)yOrigin + 10, (int)columnWidth - 40, 80), font);
            }

            yOrigin += kRowHeight;

            float mcavg = FSReading.getMCAvg(arrayReadings);
            float mchigh = FSReading.getMCMax(arrayReadings);
            float mclow = FSReading.getMCMin(arrayReadings);
            float rhavg = FSReading.getRHAvg(arrayReadings);
            float tempavg = FSReading.getTempAvg(arrayReadings);
            float emcavg = FSReading.getEmcAvg(arrayReadings);

            // temperature
            String tempUnit = globalData.getTempUnit();
            if (globalData.settingTemp == GlobalData.TEMP_FAHRENHEIT) //f
                tempavg = FSReading.getFTemperature(tempavg);

            Font textFont = new Font(pdf, CoreFont.HELVETICA);
            textFont.setSize(14);

            int startIndex = 0;
            int i = 0;

            // MC Avg.
            int column = 0;
            drawText(String.format("%.1f", mcavg), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // MC High(%)
            column++;
            drawText(String.format("%.1f", mchigh), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // MC Low(%)
            column++;
            drawText(String.format("%.1f", mclow), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // EMC Avg(%)
            column++;
            drawText(String.format("%.1f", emcavg), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // RH Avg(%)
            column++;
            drawText(String.format("%d", Math.round(rhavg)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // Temp Avg
            column++;
            drawText(String.format("%d", Math.round(tempavg)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);
        }
        catch (Exception e)
        {

        }
    }

    private void renderLastReadingTable(float ypos, FSReading lastReading) {
        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 240;
        int numberOfColumns = 4;

        GlobalData globalData = GlobalData.sharedData();

        // table header
        try
        {
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Material");
            labels.add("s.g.");
            labels.add("Depth");
            labels.add("Battery (%)");

            Font font = new Font(pdf, CoreFont.HELVETICA);
            font.setSize(18);

            for (int i = 0; i < labels.size(); i++) {
                drawText(labels.get(i), new Rect((int)(xOrigin + 20 + columnWidth * i),
                        (int)yOrigin + 10,
                        (int)columnWidth - 40,
                        80), font);
            }

            yOrigin += kRowHeight;

            Font textFont = new Font(pdf, CoreFont.HELVETICA);
            textFont.setSize(14);

            int startIndex = 0;
            int i = 0;

            // Material
            int column = 0;
            drawText(String.format("%s", FSReading.getDisplayMaterial(lastReading.readMaterial)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // s.g.
            column++;
            drawText(String.format("%d", lastReading.readGravity), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // Depth
            column++;
            drawText(String.format("%s", FSReading.getDisplayDepth(lastReading.readDepth)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);

            // Battery(%)
            column++;
            drawText(String.format("%d", lastReading.readBattery), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                    (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                    (int)columnWidth - 20,
                    30), textFont, kEmptyPlaceholder);
        }
        catch (Exception e)
        {

        }
    }

    private void renderRows(float ypos, ArrayList<FSReading> data, long startIndex, long count) {

        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 160;
        int numberOfColumns = 6;

        GlobalData globalData = GlobalData.sharedData();

        try
        {
            // table header
            drawTableAt(new Point((int)xOrigin, (int)yOrigin), (int)kRowHeight, (int)columnWidth, 1, numberOfColumns);

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("No");
            labels.add("MC (%)");
            labels.add("RH (%)");
            labels.add(String.format("Temp (%s)", globalData.getTempUnit()));
            labels.add("EMC (%)");
            labels.add("Time");

            Font font = new Font(pdf, CoreFont.HELVETICA_BOLD);
            font.setSize(18);
            for (int i = 0; i < labels.size(); i++) {
                drawText(labels.get(i), new Rect((int)(xOrigin + 20 + columnWidth * i),
                        (int)yOrigin + 10, (int)columnWidth - 40, 80), font);
            }

            yOrigin += kRowHeight;

            drawTableAt(new Point((int)xOrigin, (int)yOrigin), (int)kRowHeight, (int)columnWidth, (int)count, numberOfColumns);

            for (int i = (int)startIndex; i < (int)startIndex + count; i++) {
                Font textFont = new Font(pdf, CoreFont.HELVETICA);
                textFont.setSize(14);

                FSReading reading = data.get(i);

                // No.
                int column = 0;
                drawText(String.format("%d", (int)i + 1), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);

                // MC(%)
                column++;
                drawText(String.format("%s", reading.getDisplayRealMCValue()), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);

                // RH(%)
                column++;
                drawText(String.format("%d", Math.round(reading.readConvRH)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);

                // Temp
                column++;
                float temp = (float)reading.readConvTemp;
                if (globalData.settingTemp == GlobalData.TEMP_FAHRENHEIT) //f
                    temp = FSReading.getFTemperature(temp);
                drawText(String.format("%d", Math.round(temp)), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);

                // Emc
                column++;
                drawText(String.format("%.1f", reading.getEmcValue()), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);

                // Time
                column++;
                drawText(CommonMethods.date2str(reading.readTimestamp, "HH:mm"), new Rect((int)(xOrigin + 10 + (columnWidth * column)),
                        (int)(yOrigin + 10 + (kRowHeight * (i - startIndex))),
                        (int)columnWidth - 20,
                        30), textFont, kEmptyPlaceholder);
            }
        }
        catch (Exception e)
        {

        }
    }

    private String createReportForJob(FSJob aJob) {
        Date aDate = new Date();

        String fileName = String.format("%s_%s.pdf", aJob.jobName, CommonMethods.date2str(aDate, "MM_dd_HH_mm"));
        String  fullPDFPath = GlobalData.pdfCacheDir + "/" + fileName;

        try // Delete file if exists
        {
            File myFile = new File(fullPDFPath);
            if(myFile.exists())
                myFile.delete();
        }
        catch (Exception e)
        {

        }

        String dateStr = String.format("%s", getStringFromDate(new Date()));

        // fill report data
        fillReportData(aJob);

        if (fsReportArray.size() == 0) {
            return "";
        }

        // Opent the PDF context
        pageSize = new Size((int)A4PAPER_WIDTH_IN_PORTRATE, (int)A4PAPER_HEIGHT_IN_PORTRATE);

        try
        {
            FileOutputStream fos = new FileOutputStream(fullPDFPath);
            PDF pdf = new PDF(fos);
            this.pdf = pdf;

            int currentPage = 0;
            float yPos = 0.0f;

            // render first page
            renderFirstPage(aJob, dateStr);
            currentPage++;
            drawPageNumber(currentPage + 1);

            // render contents page
            boolean isStart = true;

            for (int i = 0; i < fsReportArray.size(); i++) {
                LocLocProduct dic = fsReportArray.get(i);
                FSLocation loc = dic.location;
                ArrayList<LocDateReading> arrayLocProducts = dic.loclocProduct;

                if (isStart == true) {
                    // draw header.
                    renderHeader(aJob, loc);
                    isStart = false;
                    yPos = kHeaderHeight;
                }

                for (int j = 0; j < arrayLocProducts.size(); j++) {
                    LocDateReading dic1 = arrayLocProducts.get(j);
                    FSLocProduct locProduct = dic1.locProduct;
                    ArrayList<DateReading> arrayLocProductsDates = dic1.datereadings;

                    // page break and render header
                    if (isInPage(yPos + kSubtitleHeight) == false) {
                        currentPage++;
                        drawPageNumber(currentPage + 1);

                        renderHeader(aJob, loc);
                        yPos = kHeaderHeight;
                    }

                    // draw subtitle
                    renderSubtitle(yPos ,loc ,locProduct);
                    yPos += kSubtitleHeight;

                    // draw parameters of product
                    //


                    for (int k = 0; k < arrayLocProductsDates.size(); k++) {
                        DateReading dic2 = arrayLocProductsDates.get(k);
                        Date date = dic2.date;
                        ArrayList<FSReading> arrayReadings = dic2.readings;

                        yPos += kGap;

                        // page break and render header
                        if (isInPage(yPos + kDateHeight + kStatisticTableHeight + kLastReadingTableHeight) == false) {
                            currentPage++;
                            drawPageNumber(currentPage + 1);

                            renderHeader(aJob, loc);
                            yPos = kHeaderHeight;
                        }

                        // draw date
                        renderDate(yPos, date);
                        yPos += kDateHeight;

                        // draw statistics
                        //[self renderStatistics:yPos arrayReadings:arrayReadings];
                        //yPos += kStatisticHeight;
                        renderStatisticsTable(yPos, arrayReadings);
                        yPos += kStatisticTableHeight;

                        if (arrayReadings.size() > 0)
                            renderLastReadingTable(yPos, arrayReadings.get(arrayReadings.size() - 1));
                        yPos += kLastReadingTableHeight;

                        int m = 0;
                        int count = (int)arrayReadings.size();
                        isStart = false;
                        while (m < count) {

                            if (isStart == false)
                            {
                                currentPage++;
                                drawPageNumber(currentPage + 1);

                                renderHeader(aJob, loc);
                                yPos = kHeaderHeight;

                                isStart = false;
                            }

                            float remains = heightRemains(yPos);
                            int remainRows = (int)(remains / kRowHeight - 1);


                            if (remainRows <= count - m) {
                                isStart = true;
                            }

                            if (remainRows <= 0)
                                continue;

                            if (remainRows > count - m)
                                remainRows = count - m;

                            renderRows(yPos, arrayReadings, m, remainRows);
                            yPos += (remainRows + 1) * kRowHeight;
                            m += remainRows;
                        }
                    }
                }
            }

            if (yPos > kHeaderHeight)
            {
                currentPage++;
                drawPageNumber(currentPage + 1);
            }

            // Close the PDF context and write the contents out.
            pdf.flush();
            fos.close();
        }
        catch (Exception e)
        {

        }


        return fullPDFPath;
    }

    public String generateReportForJob(FSJob aJob) {

        if (aJob == null)
            return "";

        if (fsReportArray != null) {
            fsReportArray.clear();
            countData = 0;
        }

        this.job = aJob;

        return createReportForJob(job);
    }

/*
    private int getFileSizeWithFilePath(String filePath) {

        NSNumber *fileSizeNumber = nil;
        NSError *attributesError = nil;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:&attributesError];
        fileSizeNumber = [fileAttributes objectForKey:NSFileSize];
        return fileSizeNumber;
    }
*/
/*
    private void drawBorder() {
        try
        {
            curPage.setPenColor(Color.BROWN);
            CGContextRef    currentContext = UIGraphicsGetCurrentContext();
            UIColor *borderColor = [UIColor brownColor];

            CGRect rectFrame = CGRectMake(kBorderInset, kBorderInset, pageSize.width-kBorderInset*2, pageSize.height-kBorderInset*2);

            CGContextSetStrokeColorWithColor(currentContext, borderColor.CGColor);
            CGContextSetLineWidth(currentContext, kBorderWidth);
            CGContextStrokeRect(currentContext, rectFrame);
        }
        catch (Exception e)
        {

        }
    }
*/

    private void drawPageNumber(int pageNumber) {
        try
        {
            String pageNumberString = String.format("Page %d", pageNumber);
            Font theFont = new Font(pdf, CoreFont.HELVETICA);
            theFont.setSize(16);

            TextLine txtLine = new TextLine(theFont, pageNumberString);
            txtLine.setPosition(kBorderInset - txtLine.getWidth() / 2, pageSize.cy - 80.0 - txtLine.getHeight() / 2);
            txtLine.drawOn(curPage);
        }
        catch (Exception e)
        {

        }
    }

    private void drawText(String textToDraw, Rect renderingRect, Font font)
    {
        drawText(textToDraw, renderingRect, font, "");
    }

    private void drawText(String textToDraw, Rect renderingRect, Font font, String placeholder)
    {
        try
        {
            String strDisplay = textToDraw;
            if (textToDraw == null || textToDraw.equals("") == true)
                strDisplay = placeholder;

            curPage.setPenColor(0, 0, 0);
            TextLine txtLine = new TextLine(font, strDisplay);

            txtLine.setPosition(renderingRect.left - txtLine.getWidth() / 2, renderingRect.top - txtLine.getHeight() / 2);
            txtLine.drawOn(curPage);
        }
        catch (Exception e)
        {

        }
    }

    private void drawTextWithLeftAllignment(String textToDraw, Rect renderingRect, Font font) {
        try
        {
            curPage.setPenColor(0, 0, 0);

            TextLine txtLine = new TextLine(font, textToDraw);
            txtLine.setPosition(renderingRect.left, renderingRect.top);
            txtLine.drawOn(curPage);
        }
        catch (Exception e)
        {

        }
    }

    private void drawLine() {
        try
        {
            curPage.setPenWidth(kLineWidth);
            curPage.setPenColor(Color.BLUE);

            Point startPoint = new Point((int)(kMarginInset + kBorderInset), (int)(kMarginInset + kBorderInset + 40.0));
            Point endPoint = new Point((int)(pageSize.cx - 2*kMarginInset -2*kBorderInset), (int)(kMarginInset + kBorderInset + 40.0));

            curPage.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
        catch (Exception e)
        {

        }
    }

    private void drawImage() {
        try
        {
            BufferedInputStream in = new BufferedInputStream(GlobalData._mainContext.getAssets().open("pdfimgs/ReportLogo.png"));

            Image demoImage = new Image(pdf, in, ImageType.PNG);
            demoImage.setPosition(pageSize.cx - demoImage.getWidth() - 50.f, 40.f);
            demoImage.drawOn(curPage);
        }
        catch (Exception e)
        {
            Log.e("error", e.toString());
        }
    }

    private void drawLogoImage() {
        try
        {
            AssetManager assetManager = GlobalData._mainContext.getAssets();
            InputStream in = assetManager.open("pdfimgs/ReportLogoBottomCenter.jpg");

            Image demoImage = new Image(pdf, in, ImageType.JPG);

            float scale = 5.0f;
            float imageWidth = demoImage.getWidth() / scale;
            float imageHeight = demoImage.getHeight() / scale;
            demoImage.setPosition(pageSize.cx / 2.0 - imageWidth / 2.0 , pageSize.cy - imageHeight - kBorderInset - kMarginInset - 200);
            demoImage.drawOn(curPage);
        }
        catch (Exception e)
        {
            Log.e("error", e.toString());
        }
    }

    private void drawLineFromPoint(Point from, Point to) {
        try
        {
            curPage.setPenWidth(2.0);
            curPage.setPenColor(0.2, 0.2, 0.3);

            curPage.drawLine(from.x, from.y, to.x, to.y);
        }
        catch (Exception e)
        {

        }
    }

    private void drawTableAt(Point origin, int rowHeight, int columnWidth, int numberOfRows, int numberOfColumns)
    {
        for (int i = 0; i <= numberOfRows; i++) {

            int newOrigin = origin.y + (rowHeight*i);

            Point from = new Point(origin.x, newOrigin);
            Point to = new Point(origin.x + (numberOfColumns*columnWidth), newOrigin);

            drawLineFromPoint(from, to);
        }

        for (int i = 0; i <= numberOfColumns; i++) {

            int newOrigin = origin.x + (columnWidth*i);

            Point from = new Point(newOrigin, origin.y);
            Point to = new Point(newOrigin, origin.y +(numberOfRows*rowHeight));

            drawLineFromPoint(from, to);
        }
    }

    private Rect getRectWithOriginSize(Point pt, TextLine txtLabel, int widthMax, int heightMax)
    {
        Size labelSize = new Size((int)txtLabel.getWidth(), (int)txtLabel.getHeight());
        if (widthMax != 0 && txtLabel.getWidth() > widthMax)
            labelSize.cx = widthMax;
        if (heightMax  != 0 && labelSize.cy > heightMax)
            labelSize.cy = heightMax;

        Rect rcRet = new Rect();
        rcRet.left = pt.x;
        rcRet.top = pt.y;
        rcRet.right = pt.x + labelSize.cx;
        rcRet.bottom = pt.y + labelSize.cy;

        return rcRet;
    }
}
