package com.stuData.utils;

import java.text.DecimalFormat;

import org.apache.poi.ss.usermodel.Cell;


public class ExcleUtil {
	
	// decimalDigits 小数位数控制
	private static Boolean decimalDigitsLimit = false;
	// decimalDigits 小数位数
	private static Integer decimalDigits = 2;

    /**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @param decimalDigits 小数位数
     * @return String 单元格数据内容
     */
    public static String getStringCellValue(Cell cell) {
        String strCell = "";
        if (cell == null) {
        	return "";
        }
        switch (cell.getCellTypeEnum()) {
        case STRING:
            strCell = cell.getStringCellValue();
            break;
        case NUMERIC:
        	if(decimalDigitsLimit) {
        		String decimalFormatter = "0";
        		String decimalFormatterExt = ".";
        		if(decimalDigits > 0) {
        			for(int i = 0 ; i < decimalDigits; i++) {
        				decimalFormatterExt = decimalFormatterExt + "0";
        			}
        			decimalFormatter = decimalFormatter + decimalFormatterExt;
        		}
        		DecimalFormat dfs = new DecimalFormat(decimalFormatter);
        		strCell = String.valueOf(dfs.format(cell.getNumericCellValue()));
        		if(strCell.endsWith(decimalFormatterExt)) {
        			strCell = strCell.substring(0, strCell.length() - decimalFormatterExt.length());
        		}
        	} else {
        		strCell = String.valueOf(cell.getNumericCellValue());
        		if(strCell.endsWith(".0")) {
        			strCell = strCell.substring(0, strCell.length() - 2);
        		}
        	}
            break;
        case BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        return strCell;
    }
}
