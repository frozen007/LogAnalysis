package com.myz.loganalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.jexl2.UnifiedJEXL;
import org.apache.commons.jexl2.UnifiedJEXL.Expression;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ReportUtil {

    private static Logger logger = Logger.getLogger(ReportUtil.class);

    public static File generateAnalysisCSVReport(String reportFile, Collection<LogStatistic> stats)
            throws FileNotFoundException, UnsupportedEncodingException, Exception {
        File resultFile = new File(reportFile);
        resultFile.getParentFile().mkdir();
        logger.info("Generating analysis result:" + resultFile.getAbsolutePath());

        PrintWriter writer = new PrintWriter(resultFile, "GBK");
        StringBuilder buf = new StringBuilder();
        // header
        writer.println("服务器,响应时间<1S,比例,响应时间1~3s,比例,响应时间3~10s,比例,响应时间>=10s,比例,500错误页面量,比例,exception数量,日志");
        for (LogStatistic statistic : stats) {
            buf.setLength(0);
            buf.append(statistic.servername).append(",");
            buf
               .append(statistic.cost0_1s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost0_1s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost1_3s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost1_3s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost3_10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost3_10s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost10s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.status500)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.status500 / statistic.totalrecord, 4))
               .append(",");
            buf.append(statistic.exceptioncnt).append(",");
            buf.append(statistic.logfile);
            writer.println(buf.toString());
        }
        writer.flush();
        return resultFile;
    }

    public static File generateAnalysisXLSReport(String xlsTemplateFile, String reportFile,
            Collection<LogStatistic> statList) throws Exception {
        File outputFile = new File(reportFile);
        logger.info("Generating analysis result:" + outputFile.getAbsolutePath());
        HSSFWorkbook book = new HSSFWorkbook(new FileInputStream(xlsTemplateFile));
        HSSFSheet sheet = book.getSheetAt(0);

        UnifiedJEXL ujexl = new UnifiedJEXL(new JexlEngine());
        int rowCur = 1;
        HSSFRow tempRow = sheet.getRow(rowCur);
        while (tempRow != null) {
            HSSFCell cell = tempRow.getCell(0);
            String cellComment = cell.getCellComment().getString().getString();
            if (cellComment.startsWith("@")) {
                cell.removeCellComment();
                int cellCur = 0;
                ArrayList<Expression> expLst = new ArrayList<Expression>();
                ArrayList<HSSFCellStyle> cellStyleLst = new ArrayList<HSSFCellStyle>();
                while (cell != null) {
                    String cellStr = cell.getStringCellValue();
                    cellStyleLst.add(cell.getCellStyle());
                    if (LogAnalysisUtil.isNull(cellStr)) {
                        expLst.add(null);
                    } else {
                        expLst.add(ujexl.parse(cellStr));
                    }

                    cellCur++;
                    cell = tempRow.getCell(cellCur);
                }

                Expression[] expArr = expLst.toArray(new Expression[expLst.size()]);

                int rowWriteCur = rowCur;
                sheet.removeRow(tempRow);
                sheet.shiftRows(rowWriteCur + 1, rowWriteCur + 1, -1);
                MapContext ctx = new MapContext();
                ctx.set("Double", Double.class);

                for (LogStatistic stat : statList) {
                    sheet.shiftRows(rowWriteCur, rowWriteCur, 1);
                    HSSFRow newRow = sheet.createRow(rowWriteCur);

                    ctx.set("stat", stat);
                    for (int i = 0; i < expArr.length; i++) {
                        Expression exp = expArr[i];
                        Object o = exp.evaluate(ctx);
                        logger.debug(o.toString() + ",");

                        HSSFCell newCell = newRow.createCell(i);
                        newCell.setCellStyle(cellStyleLst.get(i));
                        if (o instanceof Long) {
                            Long l = (Long) o;
                            newCell.setCellValue(Double.valueOf(l));
                        } else if (o instanceof Double) {
                            Double d = (Double) o;
                            newCell.setCellValue(d.doubleValue());
                        } else {
                            newCell.setCellValue(o.toString());
                        }

                    }
                    rowWriteCur++;
                }
                rowCur = rowWriteCur+1;
            }
            tempRow = sheet.getRow(rowCur);
        }

        FileOutputStream output = new FileOutputStream(outputFile);
        book.write(output);
        output.close();
        return outputFile;
    }
}
