package com.myz.loganalysis.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.UnifiedJEXL;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import com.myz.loganalysis.LogAnalysisUtil;
import com.myz.loganalysis.LogStatistic;
import com.myz.loganalysis.ReportUtil;

public class TestXlsReport extends TestCase {

    public void test001() throws Exception {
        HSSFWorkbook book = new HSSFWorkbook(new FileInputStream("report/r.xls"));
        HSSFSheet sheet = book.getSheet("Sheet1");
        int sheetI = book.getSheetIndex(sheet);
        HSSFRow tempRow = sheet.getRow(0);
        for (int i = 0; i < 10; i++) {
            HSSFRow newRow = sheet.createRow(i + 1);
            Iterator<Cell> itCell = tempRow.cellIterator();
            int colIndex = 0;
            while (itCell.hasNext()) {
                Cell temp = itCell.next();
                HSSFCell cell = newRow.createCell(colIndex);
                // cell.setCellValue(temp.get);
                // cell.setCellFormula(temp.getCellFormula());
                cell.setCellComment(temp.getCellComment());
                colIndex++;
            }

        }

        FileOutputStream output = new FileOutputStream("report/rr.xls");
        book.write(output);
        output.close();
    }

    public void test003() {
        LogStatistic stat = new LogStatistic("testserver", "test.log");
        stat.cost0_1s = 100;

        MapContext ctx = new MapContext();
        ctx.set("stat", stat);
        ctx.set("Double", Double.class);

        JexlEngine engine = new JexlEngine();
        Expression exp1 = engine.createExpression("stat.servername");
        Object o = exp1.evaluate(ctx);
        System.out.println(o);

        Expression exp2 = engine.createExpression("Double.valueOf(stat.cost0_1s)/9");
        o = exp2.evaluate(ctx);
        System.out.println(o);
    }

    public void test004() throws Exception {

        ArrayList<LogStatistic> statList = new ArrayList<LogStatistic>();
        LogStatistic stat1 = new LogStatistic("testserver1", "test1.log");
        stat1.cost0_1s = 100;
        statList.add(stat1);

        LogStatistic stat2 = new LogStatistic("testserver2", "test2.log");
        stat2.cost0_1s = 300;
        statList.add(stat2);

        HSSFWorkbook book = new HSSFWorkbook(new FileInputStream("report/a.xls"));
        HSSFSheet sheet = book.getSheet("Sheet1");
        int sheetI = book.getSheetIndex(sheet);
        int rowCur = 1;
        HSSFRow tempRow = sheet.getRow(rowCur);
        HSSFCell cell = tempRow.getCell(0);
        if (cell.getCellComment().getString().getString().startsWith("@")) {
            cell.removeCellComment();
            System.out.println("dectected list");
            // fetch all cell
            int cellCur = 0;
            ArrayList<String> cellLst = new ArrayList<String>();
            ArrayList<HSSFCellStyle> cellStyleLst = new ArrayList<HSSFCellStyle>();
            while (cell != null) {
                String cellStr = cell.getStringCellValue();
                cellStyleLst.add(cell.getCellStyle());
                if(LogAnalysisUtil.isNull(cellStr)) {
                    break;
                }
                cellLst.add(cellStr);
                cellCur++;
                cell = tempRow.getCell(cellCur);
            }

            String[] cellArr = cellLst.toArray(new String[cellLst.size()]);
            // write xls
            int rowWriteCur = rowCur;
            sheet.removeRow(tempRow);
            sheet.shiftRows(rowWriteCur+1, rowWriteCur+1, -1);
            MapContext ctx = new MapContext();
            JexlEngine engine = new JexlEngine();
            for (LogStatistic stat : statList) {
                ctx.set("stat", stat);
                sheet.shiftRows(rowWriteCur, rowWriteCur, 1);
                HSSFRow newRow = sheet.createRow(rowWriteCur);
                for (int i = 0; i < cellArr.length; i++) {
                    String str = cellArr[i];
                    Object o = engine.createExpression(str).evaluate(ctx);
                    System.out.print(o.toString() + ",");
                    HSSFCell newCell = newRow.createCell(i);
                    newCell.setCellStyle(cellStyleLst.get(i));
                    if(o instanceof Long) {
                        //newCell.setCellValue(o);
                        Long l = (Long) o;
                        newCell.setCellValue(Double.valueOf(l));
                    } else {
                        newCell.setCellValue(o.toString());
                    }
                    
                    
                }
                System.out.println();
                rowWriteCur++;
            }
        }

        FileOutputStream output = new FileOutputStream("report/aa.xls");
        book.write(output);
        output.close();
    }

    public void test005() {
        UnifiedJEXL ujexl = new UnifiedJEXL(new JexlEngine());
        LogStatistic stat1 = new LogStatistic("testserver1", "test1.log");
        stat1.cost0_1s = 100;
        MapContext ctx = new MapContext();
        ctx.set("stat", stat1);
        ctx.set("Double", Double.class);
        Object o = ujexl.parse("${Double.valueOf(stat.cost0_1s)/9}").evaluate(ctx);
        System.out.println(o);
    }

    public void test006() throws Exception {
        ArrayList<LogStatistic> statList = new ArrayList<LogStatistic>();
        LogStatistic stat1 = new LogStatistic("testserver1", "test1.log");
        stat1.cost0_1s = 100;
        statList.add(stat1);

        LogStatistic stat2 = new LogStatistic("testserver2", "test2.log");
        stat2.cost0_1s = 300;
        statList.add(stat2);

        ReportUtil.generateAnalysisXLSReport("report/analysis_template.xls", "report/analysis_test.xls", statList);
    }
}
