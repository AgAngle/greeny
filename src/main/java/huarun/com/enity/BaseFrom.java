package huarun.com.enity;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Serializable;

public class BaseFrom {

    //工作簿对象
    public XSSFWorkbook wb = null;
    //表格数目
    public Integer sheetNum = null;
    //表格对象
    private XSSFSheet firstSheet = null;
    //首行
    private XSSFRow fistRow = null;
    //列数
    private Integer colCount = null;
    //真实列数（含有操作或者内容的真实行数）
    private Integer realRowCount = null;

    public XSSFWorkbook getWb() {
        return wb;
    }

    public void setWb(XSSFWorkbook wb) {
        this.wb = wb;
    }

    public Integer getSheetNum() {
        return sheetNum;
    }

    public void setSheetNum(Integer sheetNum) {
        this.sheetNum = sheetNum;
    }

    public XSSFSheet getFirstSheet() {
        return firstSheet;
    }

    public void setFirstSheet(XSSFSheet firstSheet) {
        this.firstSheet = firstSheet;
    }

    public XSSFRow getFistRow() {
        return fistRow;
    }

    public void setFistRow(XSSFRow fistRow) {
        this.fistRow = fistRow;
    }

    public Integer getColCount() {
        return colCount;
    }

    public void setColCount(Integer colCount) {
        this.colCount = colCount;
    }

    public Integer getRealRowCount() {
        return realRowCount;
    }

    public void setRealRowCount(Integer realRowCount) {
        this.realRowCount = realRowCount;
    }
}
