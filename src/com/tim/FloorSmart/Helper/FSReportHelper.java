package com.tim.FloorSmart.Helper;

import android.graphics.*;

import com.tim.FloorSmart.Database.*;
import com.tim.FloorSmart.Global.CommonMethods;
import com.tim.FloorSmart.Global.GlobalData;

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

    int kPadding = 25;
    static int countData;

    static String kEmptyPlaceholder = "EMPTY";

    Canvas currentCanvas;

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
        init();
    }

    private void init() {

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
        currentCanvas.save();
        Paint _paintLabel = new Paint();
        _paintLabel.setTypeface(Typeface.DEFAULT_BOLD);
        _paintLabel.setTextSize(fontSize);

        Rect labelFrame = getRectWithOriginSize(origin, _paintLabel, label, 700, 300);

        Paint _paintDetail = new Paint();
        _paintDetail.setTypeface(Typeface.DEFAULT);
        _paintDetail.setTextSize(fontSize);
        Rect detailsFrame = getRectWithOriginSize(new Point(0, 0), _paintDetail, details, 300, 100);
        Rect totalFrame = new Rect(origin.x, origin.y, origin.x, origin.y);

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

        //drawText(label, labelFrame, _paintLabel);
        //drawText(details, detailsFrame, _paintDetail);

        return totalFrame;
    }
               /*
    private void renderFirstPage(FSJob aJob, String dateStr) {

        UIGraphicsBeginPDFPageWithInfo(CGRectMake(0, 0, pageSize.width, pageSize.height), nil);
        [self drawImage];

        CGRect previousRect = {{kBorderInset + kMarginInset+50, kBorderInset + kMarginInset + 150.0}, {0, 0}};

        NSArray *strings = @[
        @{kLabelKey: "Job Name:",                   kDescriptionKey: STR(self.job.jobName),                               kFontSizeKey: @24.f},
        @{kLabelKey: "Date:",              kDescriptionKey: STR(dateStr),                                 kFontSizeKey: @24.f}
        ];

        for (NSDictionary *row in strings) {
            previousRect =[self drawLabel:row[kLabelKey]
            details:row[kDescriptionKey]
            origin:CGPointMake(previousRect.origin.x, previousRect.size.height + previousRect.origin.y + 10)
            fontSize:[row[kFontSizeKey] floatValue]
            newLineSeparator:[row[kNewLineKey] boolValue]];
        }

        if (YES) {
            [self drawLogoImage];
        }
    }

    - (void)renderHeader:(FSJob *)job loc:(FSLocation *)loc {

        UIGraphicsBeginPDFPageWithInfo(CGRectMake(0, 0, pageSize.width, pageSize.height), nil);

        UIFont *font = [UIFont systemFontOfSize:12.0f];
        String strHeader = [NSString stringWithFormat:"Job: %@ \t\t Location: %", job.jobName, loc.locName];
        float width = [CommonMethods widthOfString:strHeader withFont:font] + 20;

        [self drawText:strHeader
        withFrame:CGRectMake(50, 60, width, 20)
        withFont:font];



        CGPoint from = CGPointMake(40, 90);
        CGPoint to = CGPointMake(pageSize.width - 80, 90);

        [self drawLineFromPoint:from toPoint:to];
    }

    - (void) renderFooter:(NSInteger) currentPage {
        [self drawPageNumber:currentPage];
    }

    - (void) renderSubtitle:(float)ypos loc:(FSLocation *)loc locProduct:(FSLocProduct *)locProduct {


        ypos += 30;

        UIFont *font = [UIFont systemFontOfSize:22.0f];
        String strHeader = [NSString stringWithFormat:"Location: %", loc.locName];
        float width = [CommonMethods widthOfString:strHeader withFont:font] + 20;

        [self drawText:strHeader
        withFrame:CGRectMake(60, ypos, width, 20)
        withFont:font];

        ypos += 30;

        strHeader = [NSString stringWithFormat:"Product: %", locProduct.locProductName];
        width = [CommonMethods widthOfString:strHeader withFont:font] + 20;
        [self drawText:strHeader
        withFrame:CGRectMake(60, ypos, width, 30)
        withFont:font];
        if ([locProduct.locProductName isEqualToString:FMD_DEFAULT_PRODUCTNAME])
        {
            int xpos = 60 + width;
            strHeader = [NSString stringWithFormat:"(%@)", [FSProduct getDisplayProductType:locProduct.locProductType]];
            font = [UIFont systemFontOfSize:22.0f];
            width = [CommonMethods widthOfString:strHeader withFont:font] + 20;
            [self drawText:strHeader
            withFrame:CGRectMake(xpos, ypos, width, 30)
            withFont:font];
        }

        ypos += 30;

        font = [UIFont italicSystemFontOfSize:22.0f];
        GlobalData *globalData = [GlobalData sharedData];
        if (globalData.settingArea == YES) //feet
            strHeader = [NSString stringWithFormat:"Coverage: %.1f square feet", locProduct.locProductCoverage];
        else
        {
            strHeader = [NSString stringWithFormat:"Coverage: %.1f square meter", [GlobalData sqft2sqm:locProduct.locProductCoverage]];
        }
        width = [CommonMethods widthOfString:strHeader withFont:font] + 20;
        [self drawText:strHeader
        withFrame:CGRectMake(60, ypos, width, 30)
        withFont:font];

    }

    - (void) renderDate:(float)ypos date:(NSDate *)date {

        GlobalData *globalData = [GlobalData sharedData];

        UIFont *font = [UIFont systemFontOfSize:18.0f];
        String strDate = [NSString stringWithFormat:"Date: %", [CommonMethods date2str:date withFormat:globalData.settingDateFormat]];

        float width = [CommonMethods widthOfString:strDate withFont:font] + 20;

        [self drawText:strDate
        withFrame:CGRectMake(80, ypos + 10, width, 30)
        withFont:font];

        ypos += 25;
    }

    - (BOOL) isInPage:(float)ypos {
        if (ypos >= A4PAPER_HEIGHT_IN_PORTRATE-120)
            return NO;
        return YES;
    }

    - (float) heightRemains:(float)ypos {
        return A4PAPER_HEIGHT_IN_PORTRATE-120-ypos;
    }

    - (void) renderStatistics:(float)ypos arrayReadings:(NSMutableArray *)arrayReadings {

        float xOrigin = 100;
        float yOrigin = ypos;
        // draw statistics

        UIFont *font = [UIFont systemFontOfSize:18.0f];
        float mcavg = [FSReading getMCAvg:arrayReadings];
        float rhavg = [FSReading getRHAvg:arrayReadings];
        float tempavg = [FSReading getTempAvg:arrayReadings];
        float emcavg = [FSReading getEmcAvg:arrayReadings];

        GlobalData *globalData = [GlobalData sharedData];
        if (globalData.settingTemp == YES)
            tempavg = [FSReading getFTemperature:tempavg];

        String strStatistic = [NSString stringWithFormat:"MC Avg: %.1f%%;\t\tRH Avg: %d%%;\t\tTemp Avg: %d%@;\t\tEMC Avg:%.1f%%;", mcavg, ROUND(rhavg), ROUND(tempavg), [globalData getTempUnit], emcavg];

        //\t\ts.g.:%ld%%;\t\t;Material:%@;
        //[FSReading getDisplayDepth:lastReading.readDepth], [FSReading getDisplayMaterial:lastReading.readMaterial]

        float width = [CommonMethods widthOfString:strStatistic withFont:font] + 20;

        [self drawText:strStatistic
        withFrame:CGRectMake(xOrigin, yOrigin, width, 30)
        withFont:font];

        ypos += 25;
    }

    - (void) renderStatisticsTable:(float)ypos arrayReadings:(NSMutableArray *)arrayReadings {
        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 160;
        int numberOfColumns = 6;

        GlobalData *globalData = [GlobalData sharedData];

        // table header

        NSArray *labels = @["MC Avg (%)",
        "MC High (%)",
        "MC Low (%)",
        "EMC Avg (%)",
        "RH Avg (%)",
        [NSString stringWithFormat:"Temp Avg (%@)", [globalData getTempUnit]]
        ];

        for (int i = 0; i < [labels count]; i++) {
            [self drawText:labels[i]
            withFrame:CGRectMake(xOrigin + 20 + columnWidth * i,
                    yOrigin + 10,
                    columnWidth - 40,
                    80)
            withFont:[UIFont boldSystemFontOfSize:18.0f]];
        }

        yOrigin += kRowHeight;

        float mcavg = [FSReading getMCAvg:arrayReadings];
        float mchigh = [FSReading getMCMax:arrayReadings];
        float mclow = [FSReading getMCMin:arrayReadings];
        float rhavg = [FSReading getRHAvg:arrayReadings];
        float tempavg = [FSReading getTempAvg:arrayReadings];
        float emcavg = [FSReading getEmcAvg:arrayReadings];

        // temperature
        String tempUnit = [globalData getTempUnit];
        if (globalData.settingTemp == YES) //f
            tempavg = [FSReading getFTemperature:tempavg];



        UIFont *textFont = [UIFont systemFontOfSize:14.0f];

        int startIndex = 0;
        int i = 0;

        // MC Avg.
        int column = 0;
        [self drawText:[NSString stringWithFormat:"%.1f", mcavg]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // MC High(%)
        column++;
        [self drawText:[NSString stringWithFormat:"%.1f", mchigh]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // MC Low(%)
        column++;
        [self drawText:[NSString stringWithFormat:"%.1f", mclow]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // EMC Avg(%)
        column++;
        [self drawText:[NSString stringWithFormat:"%.1f", emcavg]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // RH Avg(%)
        column++;
        [self drawText:[NSString stringWithFormat:"%d", ROUND(rhavg)]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // Temp Avg
        column++;
        [self drawText:[NSString stringWithFormat:"%d", ROUND(tempavg)]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

    }

    - (void) renderLastReadingTable:(float)ypos lastReading:(FSReading *)lastReading {
        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 240;
        int numberOfColumns = 4;

        GlobalData *globalData = [GlobalData sharedData];

        // table header

        NSArray *labels = @["Material",
        "s.g.",
        "Depth",
        "Battery (%)"
        ];

        for (int i = 0; i < [labels count]; i++) {
            [self drawText:labels[i]
            withFrame:CGRectMake(xOrigin + 20 + columnWidth * i,
                    yOrigin + 10,
                    columnWidth - 40,
                    80)
            withFont:[UIFont boldSystemFontOfSize:18.0f]];
        }

        yOrigin += kRowHeight;


        UIFont *textFont = [UIFont systemFontOfSize:14.0f];

        int startIndex = 0;
        int i = 0;

        // Material
        int column = 0;
        [self drawText:[NSString stringWithFormat:"%", [FSReading getDisplayMaterial:lastReading.readMaterial]]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // s.g.
        column++;
        [self drawText:[NSString stringWithFormat:"%ld", lastReading.readGravity]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // Depth
        column++;
        [self drawText:[NSString stringWithFormat:"%", [FSReading getDisplayDepth:lastReading.readDepth]]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];

        // Battery(%)
        column++;
        [self drawText:[NSString stringWithFormat:"%ld", lastReading.readBattery]
        withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                yOrigin + 10 + (kRowHeight * (i - startIndex)),
                columnWidth - 20,
                30)
        withFont:textFont
        placeholder:kEmptyPlaceholder];
    }

    - (void) renderRows:(float)ypos data:(NSMutableArray *)data startIndex:(long)startIndex count:(long)count {

        float xOrigin = 100;
        float yOrigin = ypos;
        float columnWidth = 160;
        int numberOfColumns = 6;

        GlobalData *globalData = [GlobalData sharedData];

        // table header
        [self drawTableAt:CGPointMake(xOrigin, yOrigin)
        withRowHeight:kRowHeight
        andColumnWidth:columnWidth
        andRowCount:1
        andColumnCount:numberOfColumns];

        NSArray *labels = @["No",
        "MC (%)",
        "RH (%)",
        [NSString stringWithFormat:"Temp (%@)", [globalData getTempUnit]],
        "EMC (%)",
        "Time"
        ];

        for (int i = 0; i < [labels count]; i++) {
            [self drawText:labels[i]
            withFrame:CGRectMake(xOrigin + 20 + columnWidth * i,
                    yOrigin + 10,
                    columnWidth - 40,
                    80)
            withFont:[UIFont boldSystemFontOfSize:18.0f]];
        }

        yOrigin += kRowHeight;


        [self drawTableAt:CGPointMake(xOrigin, yOrigin)
        withRowHeight:kRowHeight
        andColumnWidth:columnWidth
        andRowCount:(int)count
        andColumnCount:numberOfColumns];

        for (int i = (int)startIndex; i < (int)startIndex + count; i++) {
            UIFont *textFont = [UIFont systemFontOfSize:14.0f];

            FSReading *reading = [data objectAtIndex:i];

            // No.
            int column = 0;
            [self drawText:[NSString stringWithFormat:"%d", (int)i + 1]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];

            // MC(%)
            column++;
            [self drawText:[NSString stringWithFormat:"%", [reading getDisplayRealMCValue]]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];

            // RH(%)
            column++;
            [self drawText:[NSString stringWithFormat:"%d", ROUND(reading.readConvRH)]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];

            // Temp
            column++;
            float temp = reading.readConvTemp;
            if (globalData.settingTemp == YES) //f
                temp = [FSReading getFTemperature:temp];
            [self drawText:[NSString stringWithFormat:"%d", ROUND(temp)]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];

            // Emc
            column++;
            [self drawText:[NSString stringWithFormat:"%.1f", [reading getEmcValue]]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];

            // Time
            column++;
            [self drawText:[CommonMethods date2str:reading.readTimestamp withFormat:"HH:mm"]
            withFrame:CGRectMake(xOrigin + 10 + (columnWidth * column),
                    yOrigin + 10 + (kRowHeight * (i - startIndex)),
                    columnWidth - 20,
                    30)
            withFont:textFont
            placeholder:kEmptyPlaceholder];
        }
    }

    - (String )createReportForJob:(FSJob *)aJob {

        NSDate * aDate = [NSDate date];
        String fileName = [NSString stringWithFormat:"%@_%@.pdf", aJob.jobName, [CommonMethods date2str:aDate withFormat:"MM_dd_HH_mm"]];
        String  fullPDFPath = [[CommonMethods getDocumentDirectory] stringByAppendingPathComponent:fileName];

        String dateStr = [NSString stringWithFormat:"%",[self getStringFromDate:[NSDate date]]];

        // fill report data
        [self fillReportData:aJob];

        if ([self.fsReportArray count] == 0) {
            return nil;
        }

        // Opent the PDF context
        pageSize = CGSizeMake(A4PAPER_WIDTH_IN_PORTRATE, A4PAPER_HEIGHT_IN_PORTRATE);
        UIGraphicsBeginPDFContextToFile(fullPDFPath, CGRectZero, nil);

        NSInteger currentPage = 0;

        float yPos = 0.0;

        // render first page
        [self renderFirstPage:aJob dateStr:dateStr];
        [self drawPageNumber:currentPage++ + 1];

        // render contents page
        BOOL isStart = YES;

        for (int i = 0; i < [self.fsReportArray count]; i++) {

            NSDictionary *dic = [self.fsReportArray objectAtIndex:i];
            FSLocation *loc = [dic objectForKey:kLocationKey];
            NSMutableArray *arrayLocProducts = [dic objectForKey:kLocLocProductKey];

            if (isStart == YES) {

                // draw header.
                [self renderHeader:aJob loc:loc];
                isStart = NO;
                yPos = kHeaderHeight;
            }

            for (int j = 0; j < [arrayLocProducts count]; j++) {
                NSDictionary *dic1 = [arrayLocProducts objectAtIndex:j];
                FSLocProduct *locProduct = (FSLocProduct *)[dic1 objectForKey:kLocProductKey];
                NSMutableArray *arrayLocProductsDates = [dic1 objectForKey:kDateReadingsKey];

                // page break and render header
                if ([self isInPage:yPos + kSubtitleHeight] == NO) {
                    [self drawPageNumber:currentPage++ + 1];

                    [self renderHeader:aJob loc:loc];
                    yPos = kHeaderHeight;
                }

                // draw subtitle
                [self renderSubtitle:yPos loc:loc locProduct:locProduct];
                yPos += kSubtitleHeight;

                // draw parameters of product
                //


                for (int k = 0; k < [arrayLocProductsDates count]; k++) {
                    NSDictionary *dic2 = [arrayLocProductsDates objectAtIndex:k];
                    NSDate *date = (NSDate *)[dic2 objectForKey:kDateKey];
                    NSMutableArray *arrayReadings = [dic2 objectForKey:kReadingsKey];

                    yPos += kGap;

                    // page break and render header
                    if ([self isInPage:yPos + kDateHeight + kStatisticTableHeight + kLastReadingTableHeight] == NO) {
                        [self drawPageNumber:currentPage++ + 1];

                        [self renderHeader:aJob loc:loc];
                        yPos = kHeaderHeight;
                    }

                    // draw date
                    [self renderDate:yPos date:date];
                    yPos += kDateHeight;

                    // draw statistics
                    //[self renderStatistics:yPos arrayReadings:arrayReadings];
                    //yPos += kStatisticHeight;
                    [self renderStatisticsTable:yPos arrayReadings:arrayReadings];
                    yPos += kStatisticTableHeight;

                    [self renderLastReadingTable:yPos lastReading:[arrayReadings lastObject]];
                    yPos += kLastReadingTableHeight;


                    int m = 0;
                    int count = (int)[arrayReadings count];
                    isStart = NO;
                    while (m < count) {

                        if (isStart == YES)
                        {
                            [self drawPageNumber:currentPage++ + 1];

                            [self renderHeader:aJob loc:loc];
                            yPos = kHeaderHeight;

                            isStart = NO;
                        }

                        float remains = [self heightRemains:yPos];
                        int remainRows = remains / kRowHeight - 1;


                        if (remainRows <= count - m) {
                            isStart = YES;
                        }

                        if (remainRows <= 0)
                            continue;

                        if (remainRows > count - m)
                            remainRows = count - m;

                        [self renderRows:yPos data:arrayReadings startIndex:m count:remainRows];
                        yPos += (remainRows + 1) * kRowHeight;
                        m += remainRows;
                    }
                }
            }
        }

        if (yPos > kHeaderHeight)
        [self drawPageNumber:currentPage++ + 1];

        // Close the PDF context and write the contents out.
        UIGraphicsEndPDFContext();

        NSNumber *fileSize = [self getFileSizeWithFilePath:fullPDFPath];


        [[self delegate] didFinishGeneratingReport];

        return fullPDFPath;
    }

    -(String ) generateReportForJob:(FSJob *) aJob {

        if (aJob == nil)
            return nil;

        if (self.fsReportArray) {
            [self.fsReportArray removeAllObjects];
            countData = 0;
        }
        self.job = aJob;

        return [self createReportForJob:self.job];
    }

    -(NSNumber*)getFileSizeWithFilePath:(NSString*)filePath {

        NSNumber *fileSizeNumber = nil;
        NSError *attributesError = nil;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:&attributesError];
        fileSizeNumber = [fileAttributes objectForKey:NSFileSize];
        return fileSizeNumber;
    }

    - (void) drawBorder {

        CGContextRef    currentContext = UIGraphicsGetCurrentContext();
        UIColor *borderColor = [UIColor brownColor];

        CGRect rectFrame = CGRectMake(kBorderInset, kBorderInset, pageSize.width-kBorderInset*2, pageSize.height-kBorderInset*2);

        CGContextSetStrokeColorWithColor(currentContext, borderColor.CGColor);
        CGContextSetLineWidth(currentContext, kBorderWidth);
        CGContextStrokeRect(currentContext, rectFrame);
    }

    - (void)drawPageNumber:(NSInteger)pageNumber {

        NSString* pageNumberString = [NSString stringWithFormat:"Page %d", pageNumber];
        UIFont* theFont = [UIFont systemFontOfSize:16];

        CGSize pageNumberStringSize = [pageNumberString sizeWithFont:theFont
        constrainedToSize:pageSize
        lineBreakMode:NSLineBreakByWordWrapping];

        CGRect stringRenderingRect = CGRectMake(kBorderInset,
                pageSize.height - 80.0,
                pageSize.width - 2*kBorderInset,
                pageNumberStringSize.height);

        [pageNumberString drawInRect:stringRenderingRect withFont:theFont lineBreakMode:NSLineBreakByWordWrapping alignment:NSTextAlignmentCenter];
    }

    - (void) drawText:(NSString*)textToDraw
    withFrame:(CGRect)renderingRect
    withFont:(UIFont*)font
    {

        [self drawText:textToDraw
        withFrame:renderingRect
        withFont:font
        placeholder:""];
    }

    - (void) drawText:(NSString*)textToDraw
    withFrame:(CGRect)renderingRect
    withFont:(UIFont*)font
    placeholder:(NSString*)placeholder
    {

        if (textToDraw == nil) {
            textToDraw = placeholder;
        }
        CGContextRef    currentContext = UIGraphicsGetCurrentContext();
        CGContextSetRGBFillColor(currentContext, 0.0, 0.0, 0.0, 1.0);

        [textToDraw drawInRect:renderingRect
        withFont:font
        lineBreakMode:NSLineBreakByWordWrapping
        alignment:NSTextAlignmentCenter];

    }

    - (void) drawTextWithLeftAllignment:(NSString*)textToDraw withFrame:(CGRect)renderingRect withFont:(UIFont*)font {

        if (textToDraw == nil) {
            textToDraw = "";
        }
        CGContextRef    currentContext = UIGraphicsGetCurrentContext();
        CGContextSetRGBFillColor(currentContext, 0.0, 0.0, 0.0, 1.0);

        [textToDraw drawInRect:renderingRect
        withFont:font
        lineBreakMode:NSLineBreakByWordWrapping
        alignment:NSTextAlignmentLeft];

    }

    - (void) drawLine {

        CGContextRef    currentContext = UIGraphicsGetCurrentContext();
        CGContextSetLineWidth(currentContext, kLineWidth);

        CGContextSetStrokeColorWithColor(currentContext, [UIColor blueColor].CGColor);

        CGPoint startPoint = CGPointMake(kMarginInset + kBorderInset, kMarginInset + kBorderInset + 40.0);
        CGPoint endPoint = CGPointMake(pageSize.width - 2*kMarginInset -2*kBorderInset, kMarginInset + kBorderInset + 40.0);

        CGContextBeginPath(currentContext);
        CGContextMoveToPoint(currentContext, startPoint.x, startPoint.y);
        CGContextAddLineToPoint(currentContext, endPoint.x, endPoint.y);

        CGContextClosePath(currentContext);
        CGContextDrawPath(currentContext, kCGPathFillStroke);
    }

    - (void) drawImage {

        //UIImage * demoImage = [UIImage imageNamed:"wagner_pdf_image.png"];
        UIImage * demoImage = [UIImage imageNamed:"ReportLogo"];
        [demoImage drawInRect:CGRectMake( (pageSize.width - demoImage.size.width - 50), 40, demoImage.size.width, demoImage.size.height)];
    }

    - (void) drawLogoImage {

        //UIImage * demoImage = [UIImage imageNamed:"wagner_pdf_logo.png"];
        UIImage * demoImage = [UIImage imageNamed:"ReportLogoBottomCenter"];
        float scale = 4.0;
        float imageWidth = demoImage.size.width / scale;
        float imageHeight = demoImage.size.height / scale;

        [demoImage drawInRect:CGRectMake(pageSize.width / 2.0 - imageWidth / 2.0 , pageSize.height - imageHeight - kBorderInset - kMarginInset - 200, demoImage.size.width / 5.0, demoImage.size.height / 5.0)];
    }

    -(void)drawLineFromPoint:(CGPoint)from toPoint:(CGPoint)to {

        CGContextRef context = UIGraphicsGetCurrentContext();
        CGContextSetLineWidth(context, 2.0);

        CGColorSpaceRef colorspace = CGColorSpaceCreateDeviceRGB();
        float components[] = {0.2, 0.2, 0.2, 0.3};
        CGColorRef color = CGColorCreate(colorspace, components);

        CGContextSetStrokeColorWithColor(context, color);

        CGContextMoveToPoint(context, from.x, from.y);
        CGContextAddLineToPoint(context, to.x, to.y);

        CGContextStrokePath(context);
        CGColorSpaceRelease(colorspace);
        CGColorRelease(color);

    }

    -(void)drawTableAt:(CGPoint)origin
    withRowHeight:(int)rowHeight
    andColumnWidth:(int)columnWidth
    andRowCount:(int)numberOfRows
    andColumnCount:(int)numberOfColumns

    {
        for (int i = 0; i <= numberOfRows; i++) {

            int newOrigin = origin.y + (rowHeight*i);

            CGPoint from = CGPointMake(origin.x, newOrigin);
            CGPoint to = CGPointMake(origin.x + (numberOfColumns*columnWidth), newOrigin);

            [self drawLineFromPoint:from toPoint:to];
        }

        for (int i = 0; i <= numberOfColumns; i++) {

            int newOrigin = origin.x + (columnWidth*i);

            CGPoint from = CGPointMake(newOrigin, origin.y);
            CGPoint to = CGPointMake(newOrigin, origin.y +(numberOfRows*rowHeight));

            [self drawLineFromPoint:from toPoint:to];
        }
    }
*/
    private Rect getRectWithOriginSize(Point pt, Paint paint, String label, int widthMax, int heightMax)
    {
        Rect rcLabel = new Rect();
        paint.getTextBounds(label, 0, label.length(), rcLabel);
        Size labelSize = new Size(rcLabel.width(), rcLabel.height());
        if (widthMax != 0 && labelSize.cx > widthMax)
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
