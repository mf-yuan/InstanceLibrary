package com.demo;

import com.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.ArrayList;
import java.util.List;

public class XiaoChenExcel {
    public static void main(String[] args) {
        Workbook workbook = ExcelUtil.fileToWorkBook("C:\\Users\\EDY\\Desktop\\附件06：采购计划-采购部填写.xlsx");
        Sheet dataSheet = workbook.getSheetAt(0);

        int physicalNumberOfRows = dataSheet.getPhysicalNumberOfRows();
        Sheet sheet = workbook.createSheet();

        List<CellRangeAddress> rangeAddresses = new ArrayList<>();

        int currentRows = 0;
        for (int i = 8; i <= physicalNumberOfRows; i++) {
            Row row = sheet.createRow(currentRows++);

            int currentCols = 0;
            Cell cell = row.createCell(currentCols++);
            cell.setCellValue("物料编码");
            cell = row.createCell(currentCols++);
            cell.setCellValue("材料名称");
            cell = row.createCell(currentCols++);
            cell.setCellValue("规格型号");
            cell = row.createCell(currentCols++);
            cell.setCellValue("上年加权平均值采购价（含税）");
            cell = row.createCell(currentCols++);
            cell.setCellValue("当前值采购价（含税）");
            cell = row.createCell(currentCols++);
            cell.setCellValue("采购对象");
            cell = row.createCell(currentCols++);
            cell.setCellValue("明细");
            cell = row.createCell(currentCols++);
            cell.setCellValue("1月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("2月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("3月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("4月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("5月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("6月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("7月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("8月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("9月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("10月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("11月");
            cell = row.createCell(currentCols++);
            cell.setCellValue("12月");

            row = sheet.createRow(currentRows++);

            currentCols = 0;

            Row dataRow = dataSheet.getRow(i);
            cell = row.createCell(currentCols);
            cell.setCellValue(ExcelUtil.getCellValue(dataRow.getCell(1)));

            currentCols = 5;
            cell = row.createCell(currentCols);
            cell.setCellValue(ExcelUtil.getCellValue(dataRow.getCell(15)));

            currentCols = 6;
            cell = row.createCell(currentCols);
            cell.setCellValue("预计采购数量");

            row = sheet.createRow(currentRows++);
            cell = row.createCell(currentCols);
            cell.setCellValue("预计采购金额");

            row = sheet.createRow(currentRows++);
            cell = row.createCell(currentCols);
            cell.setCellValue("应付账款账期（天数）");

            row = sheet.createRow(currentRows);
            cell = row.createCell(currentCols);
            cell.setCellValue("付款总额");
            for (int j = 0; j < 6; j++) {
                rangeAddresses.add(new CellRangeAddress(currentRows - 3,currentRows , j, j));
            }
            currentRows++;
            row = sheet.createRow(currentRows++);
        }
        rangeAddresses.forEach(sheet::addMergedRegion);
        ExcelUtil.workBookToFile(workbook, "1");
    }
}
