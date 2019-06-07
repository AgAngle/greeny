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
    public static String baseFileInputDir = "/Users/chenjianxing/Documents/demo-master/";

    public static String serverInfoDir = "/Users/chenjianxing/Documents/init_sql/server_info_list.xlsx";

    public static String serverDemoDir = "/Users/chenjianxing/Documents/init_sql/new_demo.xlsx";

    public static String userDemoDir = "/Users/chenjianxing/Documents/init_sql/test_user.xlsx";

    public static String userInfoDir = "/Users/chenjianxing/Documents/init_sql/user_info_list.xlsx";

    public AssestFrom assestFrom = new AssestFrom();
    public ServerInforFrom serverInforFrom = new ServerInforFrom();
    public ServerDemoFrom serverDemoFrom = new ServerDemoFrom();
    public UserDemoFrom userDemoFrom = new UserDemoFrom();
    public UserInfoFrom userInfoFrom = new UserInfoFrom();

    public static ExcelUtil excelUtil = new ExcelUtil();

    //-------------------------->>>>>>>>>>>>>>>>>>>>
    //需要初始化组织名称
    public String []  orgStrs = new String[]{
            "数据中心"
//            ,"系统开发中心"
//            "保险","部室","华润医商"
//            "华创07","华润电力07"
    };
    public Integer rootNodeStartKey = 4;
    //-------------------------->>>>>>>>>>>>>>>>>>>>

//    public Map<String,String> orgs = new TreeMap<String, String>();
    public Map<String,Integer> rootNodes = new TreeMap<String, Integer>();
//    public static AssetsUsers assetsUsers= new AssetsUsers();
    public Map<String, String> adminUserMap = new TreeMap<String, String>();
    public Map<String, String> nodeMap = new TreeMap<String, String>();


//    static {
//        List<AssetsUser> adminusers = new ArrayList<AssetsUser>();
//        adminusers.add(new AssetsUser(SystemType.linuxAdminusers,SystemType.linuxAdminusers));
//        adminusers.add(new AssetsUser(SystemType.WinAdminusers,SystemType.WinAdminusers));
////        List<AssetsUser> systemuser = new ArrayList<AssetsUser>();
////        systemuser.add(new AssetsUser("appuser","appuser"));
////        systemuser.add(new AssetsUser("Windows","Windows"));
//        assetsUsers.setAdminusers(adminusers);
////        assetsUsers.setSystemuser(systemuser);
//    }


    @Test
    public void start(){
        System.out.println("文件载入中...");
        try {

            //System.out.println(new Gson().toJson(datas));



            int orgNum = 1;
            for (String orgName:
                 orgStrs) {

//            for (Map.Entry<String, String> org:
//                 orgs.entrySet()) {

                assestFrom.setWb(excelUtil.loadExcel2007(baseFileInputDir + orgName + ".xlsx"));
                serverInforFrom.setWb(excelUtil.loadExcel2007(serverInfoDir));
                serverDemoFrom.setWb(excelUtil.loadExcel2007(serverDemoDir));
                userDemoFrom.setWb(excelUtil.loadExcel2007(userDemoDir));
                userInfoFrom.setWb(excelUtil.loadExcel2007(userInfoDir));


                init();

                //printInit();

                Set<String> systemUsersSet = new TreeSet<String>();


                Map<String, DataModle> orgDatas = buildDatas(systemUsersSet);

                Map<String, ServerInfoModle> serverInfos = buildServerInfoModle(orgDatas);

                System.out.println(new Gson().toJson(serverInfos));

                Map<String,String> userGroups = new TreeMap<String, String>();

                //新建组织
                String orgId = SqlUtils.createOrg(orgName);

                //创建管理用户和系统用户
                SqlUtils.createAdminuserAndSystemuser(orgName, orgId , adminUserMap, orgDatas);

                //新建用户组,org_id为空，则为默认组织
                SqlUtils.createUserGroup(orgDatas.keySet(), userGroups, orgName, orgId);

                //创建资产节点，org_id为空为默认组织，key表示节点层级
                SqlUtils.createNodes(userGroups.keySet(), rootNodes, rootNodeStartKey, nodeMap,  orgName, orgId);
                rootNodeStartKey ++;
                //资产通过资产界面导入

                //创建对应组织的资产导入文件
                SqlUtils.createOrgAssets(orgDatas, serverInfos, serverDemoFrom, adminUserMap, orgNum, nodeMap, orgName);
                orgNum ++ ;


                ////授权资产到节点
                //SqlUtils.addAssetsToNodes(org, orgDatas);

                //删除根节点的关联关系
                SqlUtils.deleteRootNodeWithAssets(orgDatas, orgName, orgId);

                ////创建授权规则
                //SqlUtils.createAssetPermission(orgDatas, orgName, orgId);

                //用户授权到组织
                SqlUtils.addUserToOrg(orgDatas.values(), orgName, orgId);
                //用户授权用户组
                SqlUtils.addUserToUserGroup(orgDatas, userGroups.keySet(), orgName, orgId);

            }

            Map<String, UserInfoModle> stringUserInfoModleMap = buildUserInfoModle(userInfoFrom);
            //创建用户信息导入文件
            SqlUtils.createUserImportFile(userDemoFrom, stringUserInfoModleMap);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("文件创建完成！");
    }

    private Map<String, UserInfoModle> buildUserInfoModle(UserInfoFrom userInfoFrom) {

        Map<String, UserInfoModle> userInfos = new TreeMap<String, UserInfoModle>();

        for(int i = 2 ; i <= userInfoFrom.getRealRowCount(); i++){

            UserInfoModle userInfo = new UserInfoModle();

            String loginUser;
            String userName;
            String mail;
            String overTime;

            try {
                loginUser = excelUtil.readExcelByRowAndCol(userInfoFrom.getWb(), 1,i, userInfoFrom.getLoginUserNum());
                userName = excelUtil.readExcelByRowAndCol(userInfoFrom.getWb(),1,i, userInfoFrom.getUserNameNum());
                mail = excelUtil.readExcelByRowAndCol(userInfoFrom.getWb(),1,i, userInfoFrom.getMailNum());
                overTime = excelUtil.readExcelByRowAndCol(userInfoFrom.getWb(),1,i, userInfoFrom.getOverTimeNum());

                userInfo.setLoginUser(loginUser);
                userInfo.setUserName(userName);
                userInfo.setMail(mail);
                userInfo.setOverTime(overTime);
                userInfos.put(loginUser, userInfo);
            } catch (Exception e) {
                System.out.println("build serverInfor datas faile...");
                e.printStackTrace();
            }
        }
        excelUtil.close(assestFrom.getWb());
        return userInfos;
    }

    private Map<String,ServerInfoModle> buildServerInfoModle(Map<String, DataModle> orgDatas) {

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
//        SqlUtils.createOrg(orgs, orgStrs);
//        System.out.println(new Gson().toJson(orgs));
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
        System.out.println("======================");
        System.out.println("NameNum: " + userDemoFrom.getNameNum());
        System.out.println("UserNameNum: " + userDemoFrom.getUserNameNum());
        System.out.println("MailNum: " + userDemoFrom.getMailNum());
        System.out.println("GroupNum: " + userDemoFrom.getUserGroupNum());
        System.out.println("RoleNum: " + userDemoFrom.getRoleNum());
        System.out.println("MFANum: " + userDemoFrom.getMFANum());
        System.out.println("ActivateNum: " + userDemoFrom.getActivateNum());
        System.out.println("OverTimeNum: " + userDemoFrom.getOverTimeNum());
        System.out.println("======================");
        printCommonInfo(userDemoFrom);
        System.out.println("======================");
        System.out.println("NameNum: " + userInfoFrom.getLoginUserNum());
        System.out.println("UserNameNum: " + userInfoFrom.getUserNameNum());
        System.out.println("MailNum: " + userInfoFrom.getOverTimeNum());
        System.out.println("GroupNum: " + userInfoFrom.getMailNum());
        System.out.println("======================");
        printCommonInfo(userInfoFrom);
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
        initCommonInfo(userDemoFrom);
        initCommonInfo(userInfoFrom);

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

        for(int i = 1; i < 15; i++) {
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
            if(value.trim().equals("节点管理")){
                serverDemoFrom.setNodesNum(i-1);
            }
            if(value.trim().equals("标签管理")){
                serverDemoFrom.setTagNum(i-1);
            }

        }

        for(int i = 1; i < 15; i++) {
            String value = excelUtil.readExcelByRowAndCol(userDemoFrom.getWb(), 1, 1, i);
            if(value.trim().equals("名称")){
                userDemoFrom.setNameNum(i - 1);
            }
            if(value.trim().equals("用户名")){
                userDemoFrom.setUserNameNum(i - 1);
            }
            if(value.trim().equals("邮件")){
                userDemoFrom.setMailNum(i - 1);
            }
            if(value.trim().equals("用户组")){
                userDemoFrom.setUserGroupNum(i - 1);
            }
            if(value.trim().equals("角色")){
                userDemoFrom.setRoleNum(i - 1);
            }
            if(value.trim().equals("MFA")){
                userDemoFrom.setMFANum(i - 1 );
            }
            if(value.trim().equals("有效")){
                userDemoFrom.setActivateNum(i - 1);
            }
            if(value.trim().equals("失效日期")){
                userDemoFrom.setOverTimeNum(i - 1);
            }
        }

        for(int i = 1; i < 20; i++) {
            String value = excelUtil.readExcelByRowAndCol(userInfoFrom.getWb(), 1, 1, i);
            if(value.trim().equals("登录名")){
                userInfoFrom.setLoginUserNum(i);
            }
            if(value.trim().equals("姓名")){
                userInfoFrom.setUserNameNum(i);
            }
            if(value.trim().equals("帐号期限")){
                userInfoFrom.setOverTimeNum(i);
            }
            if(value.trim().equals("邮件")){
                userInfoFrom.setMailNum(i);
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
