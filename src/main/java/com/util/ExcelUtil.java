package com.util;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.platform.commons.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author yuanmengfan
 * @date 2023/4/24 23:55
 * @description
 */
public class ExcelUtil {


   /**
     *
     * @param templatePath      模板的路径
     * @param dataList          数据
     * @param keys              map的key需要按顺序
     * @param fillDoneConsumer  数据填充完成后钩子
     * @return
    */
    public Workbook exportTemplateWorkBook(String templatePath, List<Map<String,Object>> dataList
            , String[] keys, Consumer<Workbook> fillDoneConsumer, Integer startRow,Integer startCol) {
        Workbook workbook =  fileToWorkBook(templatePath);
        Sheet sheet = workbook.getSheetAt(0);
        int rownum = sheet.getPhysicalNumberOfRows();
        if(startRow != null){
            rownum = startRow;
        }

        Row row = null;
        Cell cell = null;

        CellStyle defaultCellStyle = initColumnCenterStyle(workbook);
        // 把 maplist 中的数据都加载到 HssfWorkBook中
        for (int i = 0; i < dataList.size(); i++) {
            int currentRow = rownum++;
            row = sheet.getRow(currentRow);
            if(row == null){
                row = sheet.createRow(currentRow);
            }
            Map<String,Object> map = dataList.get(i);
            for (int j = 0; j < keys.length; j++) {
                int colIndex = j + startCol;
                cell = row.getCell(colIndex);
                if(cell == null){
                    cell= row.createCell(colIndex);
                }
                cell.setCellValue(map.getOrDefault(keys[j],"").toString());
                cell.setCellStyle(defaultCellStyle);
            }
        }
        if(fillDoneConsumer != null){
            fillDoneConsumer.accept(workbook);
        }
        return workbook;
    }
    /**
     *
     * @param templatePath      模板的路径
     * @param saveFilePath          导出的文件名称
     * @param dataList          数据
     * @param keys              map的key需要按顺序
     * @param fillDoneConsumer  数据填充完成后钩子
     * @return
     */
    public File exportTemplateFile(String templatePath, String saveFilePath, List<Map<String,Object>> dataList
            , String[] keys, Consumer<Workbook> fillDoneConsumer, Integer startRow, Integer startCol){
        Workbook workbook = exportTemplateWorkBook(templatePath, dataList, keys, fillDoneConsumer, startRow,startCol);
        return workBookToFile(workbook,saveFilePath);
    }

    /**
     * 基于数据导出
     * @param titleName
     * @param titles
     * @param mapList
     * @param keys
     * @return
     */
    public static Workbook exportWorkBook(String titleName, String[] titles, List<Map<String,Object>> mapList, String[] keys) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        // 初始化样式
        sheet.setMargin(HSSFSheet.TopMargin, 0.6); // 上边距
        sheet.setMargin(HSSFSheet.BottomMargin, 0.2); // 下边距

        int rownum = 0;

        // 创建标题
        Row row = sheet.createRow(rownum++);
        row.setHeightInPoints(30);
        Cell title = row.createCell(0);
        title.setCellValue(titleName);
        title.setCellStyle(initColumnHeadStyle(workbook, true, 22));

        // 合并标题
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, titles.length);
        sheet.addMergedRegion(region);

        // 创建表头
        row = sheet.createRow(rownum++);
        row.setHeightInPoints(22);

        CellStyle titleCellStyle = initColumnHeadStyle(workbook, true, 14);
        // 添加序号
        Cell cell = row.createCell(0);
        cell.setCellValue("序号");
        cell.setCellStyle(titleCellStyle);

        sheet.setColumnWidth(0, 2000);
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(titleCellStyle);
            sheet.setColumnWidth(i + 1, 8000);
        }
        CellStyle defaultCellStyle = initColumnCenterStyle(workbook);
        // 把 maplist 中的数据都加载到 WorkBook中
        for (int i = 0; i < mapList.size(); i++) {
            row = sheet.createRow(rownum++);
            // 序号值
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(defaultCellStyle);
            Map<String,Object> map = mapList.get(i);
            for (int j = 0; j < keys.length; j++) {
                cell = row.createCell(j + 1);
                cell.setCellValue(map.getOrDefault(keys[j],"").toString());
                cell.setCellStyle(defaultCellStyle);
            }
        }
        return workbook;
    }

    public static List<Map<String, String>> readExcel(String filePath, String[] keys) {
        return readExcel(filePath, null, keys, 1, 0);
    }

    public static List<Map<String, String>> readExcel(String filePath, String type, String[] keys) {
        return readExcel(filePath, type, keys, 1, 0);
    }

    public static  List<Map<String, String>> readExcel(String filePath, String[] keys, int dataStartIndex) {
        return readExcel(filePath, null, keys, dataStartIndex, 0);
    }

    public static List<Map<String, String>> readExcel(String filePath, String[] keys, int dataStartIndex, int startColumn) {
        return readExcel(filePath, null, keys, dataStartIndex, startColumn);
    }

    public static List<Map<String, String>> readExcel(String filePath, String type, String[] keys, int dataStartIndex, int startColumn) {
        Workbook workbook = fileToWorkBook(filePath, type);
        List<Map<String, String>> result = new ArrayList<>();
        Sheet sheetAt = workbook.getSheetAt(0);
        int physicalNumberOfRows = sheetAt.getPhysicalNumberOfRows();
        System.out.println(physicalNumberOfRows);
        for (int i = dataStartIndex; i < physicalNumberOfRows; i++) {
            Row row = sheetAt.getRow(i);
            if (row != null) {
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < keys.length; j++) {
                    Cell cell = row.getCell(j + startColumn);
                    map.put(keys[j], getCellValue(cell));
                }
                result.add(map);
            }
        }
        /**
         * 合并的单元格只有合并的第一行与第一列的单元格有值
         * 1.获取所有合并的单元格
         * 2.遍历合并单元格的每一个单元格，
         * row    >=  firstRow and row <= lastRow
         * column >=  firstColumn  and column <= lastColumn
         * 3. row >= dataStartIndex   行必须大于等于开始数据行的下标
         * 4. column >= startColumn and column < keys.length + startColumn    列 必须要小于数组的长度的
         * 5. 修改list中
         *          row    - dataStartIndex
         *          column - startColumn
         *          单元格的数据
         */
        List<CellRangeAddress> mergedRegions = sheetAt.getMergedRegions();
        for (CellRangeAddress mergedRegion : mergedRegions) {
            int firstRow = mergedRegion.getFirstRow();
            int lastRow = mergedRegion.getLastRow();
            int firstColumn = mergedRegion.getFirstColumn();
            int lastColumn = mergedRegion.getLastColumn();

            Cell cell = sheetAt.getRow(firstRow).getCell(firstColumn);
            for (int row = firstRow; row <= lastRow; row++) {
                if (row < dataStartIndex) {
                    continue;
                }
                Map<String, String> map = result.get(row - dataStartIndex);
                for (int column = firstColumn; column <= lastColumn; column++) {
                    if (column < startColumn || column >= keys.length + startColumn) {
                        continue;
                    }
                    map.put(keys[column - startColumn], getCellValue(cell));
                }
            }
        }
        return result;
    }

    public static Workbook fileToWorkBook(String filePath, String type) {
        Workbook workbook = null;
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            System.out.println("===filePath===:" + filePath);
            byte[] bytes = null;
            // 如果是本地文件 直接用 FileInputStream 读取即可
            if (FileUtils.isLocalFile(filePath)) {
                inputStream = new FileInputStream(filePath);
            } else {
                // 如果是网络文件 我们用ByteArrayOutputStream 接收然后写入到bytes中即可
                inputStream = new URL(filePath).openStream();
            }
            byteArrayOutputStream = new ByteArrayOutputStream();
            // 创建存放文件内容的数组
            byte[] buff = new byte[1024];
            // 所读取的内容使用n来接收
            int n;
            // 当没有读取完时,继续读取,循环
            while ((n = inputStream.read(buff)) != -1) {
                // 将字节数组的数据全部写入到输出流中
                byteArrayOutputStream.write(buff, 0, n);
            }
            bytes = byteArrayOutputStream.toByteArray();

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ExcelType excelType = null;
            if (StringUtils.isNotBlank(type)) {
                excelType = ExcelType.valueOf(type.toUpperCase());
            } else {
                excelType = getExcelType(filePath);
            }
            if (excelType == ExcelType.XLS) {
                workbook = new HSSFWorkbook(new POIFSFileSystem(byteArrayInputStream));
            } else if (excelType == ExcelType.XLSX) {
                workbook = new XSSFWorkbook(byteArrayInputStream);
            } else {
                workbook = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                byteArrayOutputStream.flush();
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return workbook;
    }

    // 将api中的文件转换成WorkBook
    // 支持 xls | x xlxs
    public static Workbook fileToWorkBook(String filePath) {
        return fileToWorkBook(filePath, null);
    }

    public static ExcelType getExcelType(String filePath) {
        if (StrUtil.isEmpty(filePath)) {
            return null;
        }
        if (!filePath.contains(".")) {
            return null;
        }
        String type = filePath.substring(filePath.lastIndexOf(".") + 1).toUpperCase();
        ExcelType excelType = null;
        try {
            excelType = ExcelType.valueOf(type);
        } catch (Exception e) {
            return null;
        }
        return excelType;
    }

    public enum ExcelType {
        XLSX,
        XLS
    }


    /**
     * 获取单元格的值
     *
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            return cell.getCellFormula();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return DateUtils.format(date);
            } else {
                return NumberToTextConverter.toText(cell.getNumericCellValue());
            }
        }
        return "";
    }

    /**
     * 默认表格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle initColumnCenterStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        CellStyle centerstyle = wb.createCellStyle();
        centerstyle.setFont(font);
        centerstyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        centerstyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        centerstyle.setWrapText(true);
        centerstyle.setLeftBorderColor((short) 8); // 黑
        centerstyle.setBorderLeft(BorderStyle.THIN);
        centerstyle.setBorderRight(BorderStyle.THIN);
        centerstyle.setRightBorderColor((short) 8);
        centerstyle.setBorderBottom(BorderStyle.THIN); // 设置单元格的边框为粗体
        centerstyle.setBottomBorderColor((short) 8); // 设置单元格的边框颜色．
        centerstyle.setFillForegroundColor((short) 9);// 设置单元格的背景颜色． 白
        return centerstyle;
    }

    public static CellStyle initColumnHeadStyle(Workbook wb, boolean border, int fontSize) {
        CellStyle columnHeadStyle = wb.createCellStyle();
        Font columnHeadFont = wb.createFont();
        columnHeadFont.setFontHeightInPoints((short) fontSize);
        columnHeadFont.setFontName("宋体");
        columnHeadFont.setBold(true); // 粗体
        columnHeadStyle.setFont(columnHeadFont);
        columnHeadStyle.setAlignment(HorizontalAlignment.CENTER);// 左右居中
        columnHeadStyle.setVerticalAlignment(VerticalAlignment.CENTER);// 上下居中
        columnHeadStyle.setLocked(true);
        columnHeadStyle.setWrapText(true);
        columnHeadStyle.setLeftBorderColor((short) 8);// 左边框的颜色
        if (border) {
            columnHeadStyle.setBorderLeft(BorderStyle.THIN);
            columnHeadStyle.setBorderRight(BorderStyle.THIN);
            columnHeadStyle.setBorderTop(BorderStyle.THIN);// 边框
            columnHeadStyle.setBorderBottom(BorderStyle.THIN);
            columnHeadStyle.setRightBorderColor((short) 8);// 右边框的颜色
            columnHeadStyle.setBottomBorderColor((short) 8); // 设置单元格的边框颜色

        } else {
            columnHeadStyle.setBorderLeft(BorderStyle.NONE);
            columnHeadStyle.setBorderRight(BorderStyle.NONE);
        }

        columnHeadStyle.setFillForegroundColor((short) 9);
        return columnHeadStyle;
    }

    public static File workBookToFile(Workbook workbook,String saveFilePath) {
        if (workbook instanceof HSSFWorkbook) {
            saveFilePath += ".xls";
        } else if (workbook instanceof XSSFWorkbook) {
            saveFilePath += ".xlsx";
        }
        File file = new File(saveFilePath);
        OutputStream os = null;
        try {
            // 用 file取创建一个输出流
            os = new FileOutputStream(file);
            // 把 Workbook 写入到 这个输出流中
            workbook.write(os);
            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(os !=null){
                try {
                    os.flush();
                    os.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }


}
