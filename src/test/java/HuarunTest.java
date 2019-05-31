import com.google.gson.Gson;
import huarun.com.constant.SystemType;
import huarun.com.enity.*;
import huarun.com.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import util.ExcelUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HuarunTest {

    //文件所在目录
    public static String fileInputDir = "/Users/chenjianxing/Documents/demo/数据中心.xlsx";
    //文件所在目录
    public static String baseFileInputDir = "/Users/chenjianxing/Documents/demo/";

    public static String serverInfoDir = "/Users/chenjianxing/Documents/init_sql/server_info_list.xlsx";

    public static String serverDemoDir = "/Users/chenjianxing/Documents/init_sql/demo.xlsx";

    public AssestFrom assestFrom = new AssestFrom();
    public ServerInforFrom serverInforFrom = new ServerInforFrom();
    public ServerDemoFrom serverDemoFrom = new ServerDemoFrom();

    public static ExcelUtil excelUtil = new ExcelUtil();

    //需要初始化组织名称
    public String []  orgStrs = new String[]{
            //"数据中心","华润医商"
//            "保险","部室"
            "华创03","华润电力03"
    };
    public Integer rootNodeStartKey = 22;


    public Map<String,String> orgs = new TreeMap<String, String>();
    public Map<String,Integer> rootNodes = new TreeMap<String, Integer>();
    public static AssetsUsers assetsUsers= new AssetsUsers();



    static {
        List<AssetsUser> adminusers = new ArrayList<AssetsUser>();
        adminusers.add(new AssetsUser("appuser",SystemType.linuxAdminusers));
        adminusers.add(new AssetsUser("appadmin1",SystemType.WinAdminusers));
//        List<AssetsUser> systemuser = new ArrayList<AssetsUser>();
//        systemuser.add(new AssetsUser("appuser","appuser"));
//        systemuser.add(new AssetsUser("Windows","Windows"));
        assetsUsers.setAdminusers(adminusers);
//        assetsUsers.setSystemuser(systemuser);
    }


    @Test
    public void start(){
        System.out.println("文件载入中...");
        try {

            //System.out.println(new Gson().toJson(datas));
            //新建组织
            SqlUtils.createOrg(orgs, orgStrs);


            int orgNum = 1;
            for (Map.Entry<String, String> org:
                 orgs.entrySet()) {

                assestFrom.setWb(excelUtil.loadExcel2007(baseFileInputDir + org.getKey() + ".xlsx"));
                serverInforFrom.setWb(excelUtil.loadExcel2007(serverInfoDir));
                serverDemoFrom.setWb(excelUtil.loadExcel2007(serverDemoDir));

                init();

                //printInit();

                Set<String> systemUsersSet = new TreeSet<String>();

                Map<String, DataModle> orgDatas = buildDatas(systemUsersSet);

                Map<String, ServerInfoModle> serverInfos = buildServerInfoModle();

                System.out.println(new Gson().toJson(serverInfos));

                Map<String,String> userGroups = new TreeMap<String, String>();

                //将策略组中的资产，导入到对应组织的资产文件中
                SqlUtils.createOrgAssets(org, orgDatas, serverInfos, serverDemoFrom, assetsUsers, orgNum);
                orgNum ++ ;

                //创建管理用户和系统用户
                SqlUtils.createAdminuserAndSystemuser(org, assetsUsers, orgDatas);

                //新建用户组,org_id为空，则为默认组织
                SqlUtils.createUserGroup(orgDatas.keySet(), userGroups, org);
                //用户授权到组织
                SqlUtils.addUserToOrg(orgDatas.values(), org);
                //用户授权用户组
                SqlUtils.addUserToUserGroup(orgDatas, userGroups.keySet(), org);
                //创建资产节点，org_id为空为默认组织，key表示节点层级
                SqlUtils.createNodes(org, userGroups.keySet(), rootNodes, rootNodeStartKey);
                rootNodeStartKey ++;
                //资产通过资产界面导入
                //授权资产到节点
                SqlUtils.addAssetsToNodes(org, orgDatas);
                //授权资产到节点
                SqlUtils.addAssetsToNodes(org, orgDatas);
                //授权资产到节点
                SqlUtils.deleteRootNodeWithAssets(org, orgDatas);
                //创建授权规则
                SqlUtils.createAssetPermission(org, orgDatas, assetsUsers);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("文件创建完成！");
    }

    private Map<String,ServerInfoModle> buildServerInfoModle() {

        Map<String, ServerInfoModle> serverInfos = new TreeMap<String, ServerInfoModle>();

        for(int i = 2 ; i <= serverInforFrom.getRealRowCount(); i++){

            ServerInfoModle serverInfo = new ServerInfoModle();

            String hostName = null;
            String ip = null;
            String systemType = null;

            try {
                hostName = excelUtil.readExcelByRowAndCol(serverInforFrom.getWb(), 1,i, serverInforFrom.getHostNameNum());
                ip = excelUtil.readExcelByRowAndCol(serverInforFrom.getWb(),1,i, serverInforFrom.getIpNum());
                systemType = excelUtil.readExcelByRowAndCol(serverInforFrom.getWb(),1,i, serverInforFrom.getSystemTypeNum());

//
                serverInfo.setHostName(hostName);
                serverInfo.setIp(ip);
                serverInfo.setSystemType(systemType);
                serverInfos.put(hostName, serverInfo);
            } catch (Exception e) {
                System.out.println("build serverInfor datas faile...");
                e.printStackTrace();
            }
        }
        excelUtil.close(assestFrom.getWb());
        return serverInfos;
    }


    @Test
    public void genaralServerByorg(){
    }

    @Test
    public void testOrgCreateSql(){
        SqlUtils.createOrg(orgs, orgStrs);
        System.out.println(new Gson().toJson(orgs));
    }

    @Test
    public void test11(){
//        long time = System.currentTimeMillis();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        String currenTime = format.format(time);
//        System.out.println(currenTime);
        System.out.println(SqlUtils.getCurrentTime());
    }


    private Map<String,DataModle> buildDatas(Set<String> systemUsersSet) {

        Map<String,DataModle> orgDatas = new TreeMap<String, DataModle>();

        for(int i = 2 ; i <= assestFrom.getRealRowCount(); i++){

            DataModle dataModle = new DataModle();

            List<String> userNames = new ArrayList<String>();
            List<String> hostNames = new ArrayList<String>();
            List<String> systemUsers = new ArrayList<String>();
            List<String> ips = new ArrayList<String>();
            String ruleName = null;
            String userNameStrs = null;
            String hostNameStrs = null;
            String systemUserStr = null;
            String ipStrs = null;
            try {
                ruleName = excelUtil.readExcelByRowAndCol(assestFrom.getWb(), 1,i, assestFrom.getRuleNum());
                if(StringUtils.isBlank(ruleName)){
                    continue;
                }
                userNameStrs = excelUtil.readExcelByRowAndCol(assestFrom.getWb(),1,i, assestFrom.getUserNum());
                hostNameStrs = excelUtil.readExcelByRowAndCol(assestFrom.getWb(),1,i, assestFrom.getHostNameNum());
                systemUserStr = excelUtil.readExcelByRowAndCol(assestFrom.getWb(), 1,i, assestFrom.getSystemUserNum());

                ipStrs = excelUtil.readExcelByRowAndCol(assestFrom.getWb(),1,i, assestFrom.getIpNum());

//                System.out.println("ruleName:\n" + ruleName);
//                System.out.println("userNames:\n" + userNameStrs);
//                System.out.println("hostNames:\n" + hostNameStrs);
//                System.out.println("ipStrs:\n" + ipStrs);
                userNames = Arrays.asList(userNameStrs.split("\n"));
                hostNames = Arrays.asList(hostNameStrs.split("\n"));
                systemUsers = Arrays.asList(systemUserStr.split("\n"));
                ips = Arrays.asList(ipStrs.split("\n"));
                dataModle.setUserNames(userNames);
                dataModle.setHostNames(hostNames);
                dataModle.setSystemUsers(systemUsers);
                dataModle.setIps(ips);
                systemUsersSet.addAll(systemUsers);
                orgDatas.put(ruleName, dataModle);
            } catch (Exception e) {
                System.out.println("build datas faile...");
                e.printStackTrace();
            }
        }
        excelUtil.close(assestFrom.getWb());
        return orgDatas;
    }

    private  void printInit() {
        System.out.println("init:===================== ");
        printCommonInfo(assestFrom);
        System.out.println("ruleNum: " + assestFrom.getRuleNum());
        System.out.println("userNum: " + assestFrom.getUserNum());
        System.out.println("hostNameNum: " + assestFrom.getHostNameNum());
        System.out.println("ipNum: " + assestFrom.getIpNum());
        System.out.println("======================");
        printCommonInfo(serverInforFrom);
        System.out.println("HostNameNum: " + serverInforFrom.getHostNameNum());
        System.out.println("SystemTypeNum: " + serverInforFrom.getSystemTypeNum());
        System.out.println("IpNum: " + serverInforFrom.getIpNum());
        System.out.println("======================");
        printCommonInfo(serverDemoFrom);
        System.out.println("init:===================== ");
    }

    private  void printCommonInfo(BaseFrom from) {
        System.out.println("colCount: " + from.getColCount());
        System.out.println("realRowCount: " + from.getRealRowCount());
    }

    public void init() throws Exception {

        initCommonInfo(assestFrom);
        initCommonInfo(serverInforFrom);
        initCommonInfo(serverDemoFrom);

        for(int i = 1; i < 15; i++) {
            String value = excelUtil.readExcelByRowAndCol(assestFrom.getWb(), 1, 1, i);
            if(value.trim().equals("规则")){
                assestFrom.setRuleNum(i);
            }
            if(value.trim().equals("用户帐号")){
                assestFrom.setUserNum(i);
            }
            if(value.trim().equals("目标设备")){
                assestFrom.setHostNameNum(i);
            }
            if(value.trim().equals("IP地址")){
                assestFrom.setIpNum(i);
            }
            if(value.trim().equals("系统帐号")){
                assestFrom.setSystemUserNum(i);
            }

        }

        for(int i = 1; i < 15; i++) {
            String value = excelUtil.readExcelByRowAndCol(serverInforFrom.getWb(), 1, 1, i);
            if(value.trim().equals("设备名")){
                serverInforFrom.setHostNameNum(i);
            }
            if(value.trim().equals("IP地址")){
                serverInforFrom.setIpNum(i);
            }
            if(value.trim().equals("系统类型")){
                serverInforFrom.setSystemTypeNum(i);
            }

        }

        for(int i = 1; i < 30; i++) {
            String value = excelUtil.readExcelByRowAndCol(serverDemoFrom.getWb(), 1, 1, i);
            if(value.trim().equals("IP")){
                serverDemoFrom.setIpNum(i-1);
            }
            if(value.trim().equals("主机名")){
                serverDemoFrom.setHostNameNum(i-1);
            }
            if(value.trim().equals("协议")){
                serverDemoFrom.setProtocolNum(i-1);
            }
            if(value.trim().equals("端口")){
                serverDemoFrom.setPortNum(i-1);
            }
            if(value.trim().equals("系统平台")){
                serverDemoFrom.setPlatformNum(i-1);
            }
            if(value.trim().equals("激活")){
                serverDemoFrom.setActivateNum(i-1);
            }
            if(value.trim().equals("管理用户")){
                serverDemoFrom.setAdminuserNum(i-1);
            }
            if(value.trim().equals("操作系统")){
                serverDemoFrom.setOsTypeNum(i-1);
            }
            if(value.trim().equals("创建者")){
                serverDemoFrom.setCreatorNum(i-1);
            }

        }

    }

    public void initCommonInfo(BaseFrom from){
        from.setSheetNum(from.getWb().getNumberOfSheets());
        from.setFirstSheet(from.getWb().getSheetAt(0));
        from.setFistRow(from.getFirstSheet().getRow(0));
        from.setColCount((int)from.getFistRow().getLastCellNum());
        from.setRealRowCount(excelUtil.getRealRowCount(from.getWb(), 1));
    }

}
