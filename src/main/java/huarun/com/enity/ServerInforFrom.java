package huarun.com.enity;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Serializable;

public class ServerInforFrom extends BaseFrom implements Serializable {

    //设备名称所在的列数
    private Integer hostNameNum = null;
    //系统类型
    private Integer systemTypeNum = null;
    //IP地址所在的列数
    private Integer ipNum = null;


    public Integer getHostNameNum() {
        return hostNameNum;
    }

    public void setHostNameNum(Integer hostNameNum) {
        this.hostNameNum = hostNameNum;
    }

    public Integer getSystemTypeNum() {
        return systemTypeNum;
    }

    public void setSystemTypeNum(Integer systemTypeNum) {
        this.systemTypeNum = systemTypeNum;
    }

    public Integer getIpNum() {
        return ipNum;
    }

    public void setIpNum(Integer ipNum) {
        this.ipNum = ipNum;
    }
}
