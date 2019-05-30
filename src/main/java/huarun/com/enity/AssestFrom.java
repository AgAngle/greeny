package huarun.com.enity;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.Serializable;

public class AssestFrom extends BaseFrom implements Serializable {


    //用户组、设备组、策略组所在的列数
    private Integer ruleNum = null;
    //用户所在的列数
    private Integer userNum = null;
    //设备名称所在的列数
    private Integer hostNameNum = null;
    //系统用户所在的列数
    private Integer systemUserNum = null;
    //IP地址所在的列数
    private Integer ipNum = null;


    public Integer getRuleNum() {
        return ruleNum;
    }

    public void setRuleNum(Integer ruleNum) {
        this.ruleNum = ruleNum;
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
    }

    public Integer getHostNameNum() {
        return hostNameNum;
    }

    public void setHostNameNum(Integer hostNameNum) {
        this.hostNameNum = hostNameNum;
    }

    public Integer getSystemUserNum() {
        return systemUserNum;
    }

    public void setSystemUserNum(Integer systemUserNum) {
        this.systemUserNum = systemUserNum;
    }

    public Integer getIpNum() {
        return ipNum;
    }

    public void setIpNum(Integer ipNum) {
        this.ipNum = ipNum;
    }
}
