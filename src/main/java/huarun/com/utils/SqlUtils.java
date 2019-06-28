package huarun.com.utils;

import com.google.gson.Gson;
import huarun.com.constant.SystemType;
import huarun.com.enity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlUtils {


    public static final String basePath = "/Users/chenjianxing/Documents/init_sql/";

    public static String createOrg(String org) throws Exception {

        File orgCreateSql = getFile(org, "1_userGroupSql.sql");
        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(orgCreateSql);

//            for (int i = 0; i < orgStrs.length; i++){
                UUID uuid = UUID.randomUUID();
//                orgs.put(org, uuid.toString());
                String sql = "INSERT INTO orgs_organization (id,name,created_by,date_created,comment) " +
                        "VALUES ('" + uuid.toString().replace("-", "") + "','" + org + "','Administrator','" + getCurrentTime() + "','test');";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());
                return uuid.toString();
//            }

        } catch (Exception e) {
            System.out.println("general orgCreateSql fail..");
            e.printStackTrace();
            throw e;
        }finally {
            closeOutPutStream(fileOut);
        }
    }

    public static void createUserGroup(Set<String> userGroups, Map<String, String> userGroupMap,  String orgName, String orgId) {

        File createUserGroupSql = getFile(orgName, "3_userGroupSql.sql");
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(createUserGroupSql);

            for (String userGroup :
                 userGroups) {
                UUID uuid = UUID.randomUUID();
                userGroupMap.put(userGroup, uuid.toString());
                String sql = "INSERT INTO users_usergroup (id,name,comment,date_created,created_by,org_id) " +
                        "VALUES ('" + uuid.toString().replace("-","") + "','" + userGroup + "','','" + getCurrentTime() + "','Administrator','" + orgId + "') ;";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());
            }

        } catch (Exception e) {
            System.out.println("createUserGroupSql fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }
    }


    public static String getCurrentTime(){
        long time = System.currentTimeMillis();
        time -= 86400000;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currenTime = format.format(time);
        return currenTime;
    }

    public static void addUserToOrg(Collection<DataModle> datas, String orgName, String orgId) {

        File addUserToOrgSql = getFile(orgName, "07_addUserToOrgSql.sql");
        FileOutputStream fileOut = null;

        List<String> userNames = new ArrayList<String>();
        for (DataModle data:
             datas) {
            userNames.addAll(data.getUserNames());
        }

        try {

            fileOut = new FileOutputStream(addUserToOrgSql);

            for (String userName :
                    userNames) {
                String sql = "INSERT INTO orgs_organization_users(user_id, organization_id) " +
                        "select u.id,g.id from users_user u, orgs_organization g where u.username='" + userName + "' and g.name = '" + orgName + "';";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());
            }

        } catch (Exception e) {
            System.out.println("addUserToOrg fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }
    }

    public static void closeOutPutStream(OutputStream out){
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUserToUserGroup(Map<String,DataModle> datas, Set<String> userGroups, String orgName, String orgId) {

        File orgCreateSql = getFile(orgName, "08_addUserToUserGroup.sql");
        FileOutputStream fileOut = null;

        List<String> userNames = new ArrayList<String>();

        try {

            fileOut = new FileOutputStream(orgCreateSql);

            for (String userGroup:
                    userGroups) {

                for (String userName:
                     datas.get(userGroup).getUserNames()) {

                    String sql = "INSERT INTO users_user_groups(user_id, usergroup_id) " +
                            "select u.id,g.id from users_user u, users_usergroup g where u.username='" + userName + "' and g.name = '" + userGroup + "' and g.org_id = '" + orgId + "';";
                    fileOut.write(sql.getBytes());
                    fileOut.write("\n".getBytes());
                }

            }

        } catch (Exception e) {
            System.out.println("addUserToUserGroup fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }

    }

    public static File getFile(String dirName, String fileName){
        File dir = new File(basePath + dirName);
        dir.mkdir();
        File file = new File(basePath + dirName + "/" + fileName);
        return file;
    }

    public static void createNodes(Set<String> nodes, Map<String, Integer> rootNodes, Integer rootNodeStartKey, Map<String, String> nodeMap, String orgName, String orgId) {

        File createNodesSql = getFile(orgName, "4_createNode.sql");
        FileOutputStream fileOut = null;

        try {

            UUID uuid = UUID.randomUUID();
            fileOut = new FileOutputStream(createNodesSql);

            String rootNodeSql = "INSERT INTO assets_node (id,`key`,value,child_mark,date_create,org_id) " +
                    "VALUES ('" + uuid.toString().replace("-","") + "','" + rootNodeStartKey + "','" + orgName + "'," + nodes.size() + ",'" + getCurrentTime() + "','" + orgId + "') ;";
            rootNodes.put(orgName, rootNodeStartKey);
            fileOut.write(rootNodeSql.getBytes());
            fileOut.write("\n".getBytes());

            int nodeStartKey = 0;
            for (String node :
                    nodes) {
                UUID uuid1 = UUID.randomUUID();
                String nodeSql = "INSERT INTO assets_node (id,`key`,value,child_mark,date_create,org_id) " +
                        "VALUES ('" + uuid1.toString().replace("-","") + "','" + rootNodeStartKey + ":" + nodeStartKey++ + "','" + node + "',0,'" + getCurrentTime() + "','" + orgId + "') ;";
                fileOut.write(nodeSql.getBytes());
                fileOut.write("\n".getBytes());

                nodeMap.put(node, "'" + uuid1.toString() + "'");
            }

        } catch (Exception e) {
            System.out.println("createNodes fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }

    }

    public static void createAdminuserAndSystemuser(String orgName, String orgId, Map<String,String> adminUserMap, Map<String, DataModle> orgDatas) {

        File createAdminuserAndSystemuserSql = getFile(orgName, "2_createAdminuserAndSystemuserSql.sql");
        FileOutputStream fileOut = null;

        try {

            fileOut = new FileOutputStream(createAdminuserAndSystemuserSql);

//            for (AssetsUser adminuser:
//                    assetsUsers.getAdminusers()) {
                UUID winAdminUseruuid = UUID.randomUUID();
                String adminuserSql = "INSERT INTO assets_adminuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,become,become_method,become_user,`_become_pass`,org_id) " +
                        "VALUES ('" + winAdminUseruuid.toString().replace("-","") + "','" + SystemType.WinAdminusers + "','" + SystemType.WinAdminusers + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',1,'sudo','root','','" + orgId + "') ;";
                fileOut.write(adminuserSql.getBytes());
                fileOut.write("\n".getBytes());

                adminUserMap.put(SystemType.WinAdminusers, winAdminUseruuid.toString());

                UUID linuxAdminUseruuid = UUID.randomUUID();
                adminuserSql = "INSERT INTO assets_adminuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,become,become_method,become_user,`_become_pass`,org_id) " +
                        "VALUES ('" + linuxAdminUseruuid.toString().replace("-","") + "','" + SystemType.LinuxAdminusers + "','" + SystemType.LinuxAdminusers + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',1,'sudo','root','','" + orgId + "') ;";
                fileOut.write(adminuserSql.getBytes());
                fileOut.write("\n".getBytes());

                adminUserMap.put(SystemType.LinuxAdminusers, linuxAdminUseruuid.toString());
//            }


//            for (String userGroup:
//                    orgDatas.keySet()) {
//                List<String> systemUsers = orgDatas.get(userGroup).getSystemUsers();
//                for (String systemuser:
//                        systemUsers) {
//                    UUID uuid = UUID.randomUUID();
//                    String systemuseSql = null;
//                    if(systemuser.contains("admin")){
//                        systemuseSql = "INSERT IGNORE INTO assets_systemuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,priority,protocol,auto_push,sudo,shell,login_mode,org_id) " +
//                                "VALUES ('" + uuid.toString().replace("-","") + "','" + systemuser + "','" + systemuser + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',20,'rdp',0,'/bin/whoami','/bin/bash','manual','" + orgId + "') ;";
//                    } else {
//                        systemuseSql = "INSERT IGNORE INTO assets_systemuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,priority,protocol,auto_push,sudo,shell,login_mode,org_id) " +
//                                "VALUES ('" + uuid.toString().replace("-","") + "','" + systemuser + "','" + systemuser + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',20,'ssh',0,'/bin/whoami','/bin/bash','manual','" + orgId + "') ;";
//                    }
//                    fileOut.write(systemuseSql.getBytes());
//                    fileOut.write("\n".getBytes());
//                }
//            }

        } catch (Exception e) {
            System.out.println("createAdminuserAndSystemuser fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }

    }

    public static void addAssetsToNodes(Map.Entry<String,String> org, Map<String,DataModle> orgDatas) {

        File addAssetsToNodesSql = getFile(org.getKey(), "6_addAssetsToNodesSql.sql");

        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(addAssetsToNodesSql);
            for (Map.Entry<String, DataModle> entry:
                    orgDatas.entrySet()) {

                String node = entry.getKey();
                for (String hostName:
                        entry.getValue().getHostNames()) {
                    String sql = "INSERT INTO assets_asset_nodes (asset_id,node_id) " +
                            "select u.id,g.id from assets_asset u, assets_node g where u.hostname='" + hostName + "' and g.value = '" + node + "' and u.org_id = '" + org.getValue() + "'and g.org_id = '" + org.getValue() + "';";
                    fileOut.write(sql.getBytes());
                    fileOut.write("\n".getBytes());
                }

            }
        } catch (Exception e) {
            System.out.println("addAssetsToNodes fail..");
            e.printStackTrace();
        } finally {
            closeOutPutStream(fileOut);
        }
    }

    public static void createAssetPermission(Map<String, DataModle> orgDatas, String orgName , String orgId) {

        File createAssetPermissionSql = getFile(orgName, "6_createAssetPermissionSql.sql");

        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(createAssetPermissionSql);
            for (Map.Entry<String, DataModle> entry:
                    orgDatas.entrySet()) {

                String rule = entry.getKey();
                UUID uuid = UUID.randomUUID();
                String sql = "INSERT INTO perms_assetpermission (id,name,is_active,date_start,date_expired,created_by,date_created,comment,org_id) " +
                        "VALUES ('" + uuid.toString().replace("-","") + "','" + rule + "',1,'" + getCurrentTime() + "','" + "2100-02-09 00:00:00" + "','Administrator','" + getCurrentTime() + "','','" + orgId + "');";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());

                //绑定动作
                String sql1 = "INSERT INTO perms_assetpermission_actions(assetpermission_id,action_id)  " +
                        "select u.id,g.id from perms_assetpermission u, perms_action g where u.name='" + rule + "' and u.org_id = '" + orgId + "' and g.name = 'all';";
                fileOut.write(sql1.getBytes());
                fileOut.write("\n".getBytes());


                //绑定节点
                String sql2 = "INSERT INTO perms_assetpermission_nodes (assetpermission_id,node_id) " +
                        "select u.id,g.id from perms_assetpermission u, assets_node g where u.name='" + rule + "' and u.org_id = '" + orgId + "'  and g.value = '" + rule + "' and g.org_id = '" + orgId + "' ;";
                fileOut.write(sql2.getBytes());
                fileOut.write("\n".getBytes());

                //绑定用户组
                String sql3 = "INSERT INTO perms_assetpermission_user_groups (assetpermission_id,usergroup_id) " +
                        "select u.id,g.id from perms_assetpermission u, users_usergroup g where u.name='" + rule + "' and u.org_id = '" + orgId + "' and g.name = '" + rule + "' and g.org_id = '" + orgId +"' ;";
                fileOut.write(sql3.getBytes());
                fileOut.write("\n".getBytes());

                for (String systemuser:
                        entry.getValue().getSystemUsers()) {
                    //授权规则绑定系统用户
                    String sql4 ="INSERT INTO perms_assetpermission_system_users (assetpermission_id,systemuser_id) " +
                            "select u.id,g.id from perms_assetpermission u, assets_systemuser g where u.name='" + rule + "' and u.org_id = '" + orgId + "'  and g.name = '" + systemuser + "' and g.org_id = '" + orgId + "' ;";
                    fileOut.write(sql4.getBytes());
                    fileOut.write("\n".getBytes());

                    //资产节点绑定系统用户
                    String sql5 = "INSERT INTO assets_systemuser_nodes (systemuser_id,node_id) " +
                            "select u.id,g.id from assets_systemuser u, assets_node g where u.name='" + systemuser + "' and g.value = '" + rule + "' and u.org_id = '" + orgId + "' and g.org_id = '" + orgId + "';";
                    fileOut.write(sql5.getBytes());
                    fileOut.write("\n".getBytes());

                    for (String hostName:
                            entry.getValue().getHostNames()) {
                        //资产绑定系统用户
                        String sql6 = "INSERT IGNORE INTO assets_systemuser_assets (systemuser_id,asset_id) " +
                                "select u.id,g.id from assets_systemuser u, assets_asset g where u.name='" + systemuser + "' and g.hostname = '" + hostName + "' and u.org_id = '" + orgId + "' and g.org_id = '" + orgId + "';";
                        fileOut.write(sql6.getBytes());
                        fileOut.write("\n".getBytes());
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("addAssetsToNodes fail..");
            e.printStackTrace();
        } finally {
            closeOutPutStream(fileOut);
        }
    }


    public static void createOrgAssets(Map<String, DataModle> orgDatas,
                                       Map<String, ServerInfoModle> serverInfos, ServerDemoFrom serverDemoFrom, Map<String, String> adminUserMap, int orgNum, Map<String, String> nodeMap, String orgName) {

        Set<String> orgHostNames = new TreeSet<String>();

        for (Map.Entry<String,DataModle> nodeData:
                orgDatas.entrySet()) {

            orgHostNames.addAll(nodeData.getValue().getHostNames());

            for (String hosName :
                    nodeData.getValue().getHostNames()) {
                ServerInfoModle serverInfoModle = serverInfos.get(hosName);
                if(null == serverInfoModle){
                    continue;
                }
                serverInfos.get(hosName).getNodeIds().add(nodeMap.get(nodeData.getKey()));
            }
        }

        int rowIdex = 1;
        for (String hostName:
             orgHostNames) {

            ServerInfoModle serverInfoModle = serverInfos.get(hostName);
            if(null == serverInfoModle || StringUtils.isBlank(serverInfoModle.getIp())){
                continue;
            }

            BuildWb(serverDemoFrom, serverInfoModle, rowIdex, adminUserMap);
            rowIdex ++ ;
        }
        writeToExcel(orgName, serverDemoFrom.getWb(), orgNum);
        //System.out.println("rowIdex: " + rowIdex);
    }

    public static void writeToExcel(String org, XSSFWorkbook wb, int orgNum){
        File fileOutput = getFile(org, "assetImport" + orgNum + ".xlsx");
        FileOutputStream fo = null; // 输出到文件
        try {
            fo = new FileOutputStream(fileOutput);
            wb.write(fo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fo != null){
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void BuildWb(ServerDemoFrom serverDemoFrom, ServerInfoModle serverInfoModle, int rowIdex, Map<String, String> adminUserMap){

        XSSFSheet firstSheet = serverDemoFrom.getFirstSheet();

        XSSFRow row = firstSheet.createRow(rowIdex);


        row.createCell(serverDemoFrom.getIpNum())
                .setCellValue(serverInfoModle.getIp());

        row.createCell(serverDemoFrom.getHostNameNum())
                .setCellValue(serverInfoModle.getHostName());

        if(serverInfoModle.getSystemType().toLowerCase().contains("windows")){

            row.createCell(serverDemoFrom.getProtocolNum())
                    .setCellValue("rdp");

            row.createCell(serverDemoFrom.getPortNum())
                    .setCellValue("3389");

            row.createCell(serverDemoFrom.getPlatformNum())
                    .setCellValue(SystemType.WINDOWS);

            row.createCell(serverDemoFrom.getAdminuserNum())
                    .setCellValue(adminUserMap.get(SystemType.WinAdminusers));


        } else {

            row.createCell(serverDemoFrom.getProtocolNum())
                    .setCellValue("ssh");

            row.createCell(serverDemoFrom.getPortNum())
                    .setCellValue("22");


            row.createCell(serverDemoFrom.getAdminuserNum())
                    .setCellValue(adminUserMap.get(SystemType.LinuxAdminusers));

            if(serverInfoModle.getSystemType().toLowerCase().contains("linux")){

                row.createCell(serverDemoFrom.getPlatformNum())
                        .setCellValue(SystemType.LINUX);

            } else if(serverInfoModle.getSystemType().toLowerCase().contains("unix")){
                row.createCell(serverDemoFrom.getPlatformNum())
                        .setCellValue(SystemType.UNIX);
            } else {
                row.createCell(serverDemoFrom.getPlatformNum())
                        .setCellValue(SystemType.OTHER);
            }
        }

        row.createCell(serverDemoFrom.getActivateNum())
                .setCellValue("TRUE");

        row.createCell(serverDemoFrom.getNodesNum())
                .setCellValue(serverInfoModle.getNodeIds().toString());

        row.createCell(serverDemoFrom.getTagNum())
                .setCellValue("[]");

    }

    public static void deleteRootNodeWithAssets(Map<String,DataModle> orgDatas, String orgName, String orgId) {

        File deleteRootNodeWithAssetsSql = getFile(orgName, "5_deleteRootNodeWithAssetsSql.sql");

        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(deleteRootNodeWithAssetsSql);
            for (Map.Entry<String, DataModle> entry:
                    orgDatas.entrySet()) {

                String node = entry.getKey();

                for (Map.Entry<String,DataModle> nodeData:
                     orgDatas.entrySet()) {

                    for (String hostName :
                            nodeData.getValue().getHostNames()) {
                        String sql = "DELETE FROM assets_asset_nodes " +
                                "WHERE asset_id = ( " +
                                "SELECT id FROM assets_asset WHERE hostname = '" + hostName + "' and org_id = '" + orgId + "'" +
                                ") and node_id = (" +
                                "SELECT id FROM assets_node WHERE  value = '" + orgName + "' and org_id = '" + orgId + "'" +
                                ");";
                        fileOut.write(sql.getBytes());
                        fileOut.write("\n".getBytes());
                    }

                }


            }
        } catch (Exception e) {
            System.out.println("deleteRootNodeWithAssets fail..");
            e.printStackTrace();
        } finally {
            closeOutPutStream(fileOut);
        }

    }

    public static void createUserImportFile(UserDemoFrom userDemoFrom, UserDemoFrom userFailFrom, Map<String, UserInfoModle> userInfos) {

        Integer rowIdex = 1;
        Integer rowfailIdex = 1;
        Set<String> emails = new HashSet<String>();
        Set<String> names = new HashSet<String>();

        for (UserInfoModle userInfo:
             userInfos.values()) {

            if(BuildUserWb(userDemoFrom, userFailFrom , userInfo, rowIdex, rowfailIdex , emails, names)){
                rowfailIdex++;
            }else {
                rowIdex ++ ;
            }
        }
        writeToUserExcel(userDemoFrom.getWb());
        writeToFailUserExcel(userFailFrom.getWb());

    }

    public static Boolean BuildUserWb(UserDemoFrom userDemoFrom, UserDemoFrom userFailFrom, UserInfoModle userInfoModle, int rowIdex, int rowfailIdex, Set<String> emails, Set<String> names){

        XSSFSheet firstSheet = null;
        XSSFRow row = null;
        UserDemoFrom currenFrom = null;

        Boolean isFail = false;
        Integer index = null;

        if(names.contains(userInfoModle.getLoginUser().trim().toLowerCase())
                || emails.contains(userInfoModle.getMail().trim().toLowerCase())){
            currenFrom = userFailFrom;
            index = rowfailIdex;
            isFail = true;
            System.out.println("repeat: " + userInfoModle.getLoginUser());
        } else {
            index = rowIdex;
            currenFrom = userDemoFrom;
        }

        firstSheet = currenFrom.getFirstSheet();
        row = firstSheet.createRow(index);

        row.createCell(currenFrom.getNameNum())
                .setCellValue(userInfoModle.getUserName());

//        if(names.contains(userInfoModle.getLoginUser().trim().toLowerCase())){
//            System.out.println("repeat name: " + userInfoModle.getLoginUser());
//        }

        row.createCell(currenFrom.getUserNameNum())
                .setCellValue(userInfoModle.getLoginUser());

        if(StringUtils.isNotBlank(userInfoModle.getLoginUser().trim())){
            names.add(userInfoModle.getLoginUser().trim().toLowerCase());
        }

//        if(emails.contains(userInfoModle.getMail().trim().toLowerCase())){
////            System.out.println("repeat email: " + userInfoModle.getMail());
////        }

        if(StringUtils.isNotBlank(userInfoModle.getMail()) && !emails.contains(userInfoModle.getMail().trim().toLowerCase())){
            row.createCell(currenFrom.getMailNum())
                    .setCellValue(userInfoModle.getMail());
        } else {
            row.createCell(currenFrom.getMailNum())
                    .setCellValue("asdfgzxcv@fit"+ index +"cloud.com");
        }

        if(StringUtils.isNotBlank(userInfoModle.getMail().trim())){
            emails.add(userInfoModle.getMail().trim().toLowerCase());
        }

        row.createCell(currenFrom.getUserGroupNum())
                .setCellValue("[]");

        row.createCell(currenFrom.getRoleNum())
                .setCellValue("User");

        row.createCell(currenFrom.getMFANum())
                .setCellValue("0");

        row.createCell(currenFrom.getActivateNum())
                .setCellValue("TRUE");

        if(StringUtils.isNotBlank(userInfoModle.getOverTime().trim())){
            row.createCell(currenFrom.getOverTimeNum())
                    .setCellValue(userInfoModle.getOverTime() + ":00 +0800");
        }

        if (isFail){
            return true;
        }

        return false;
    }


    public static void writeToUserExcel(XSSFWorkbook wb){

        File userImportFile = new File(basePath + "userImport.xlsx");
        FileOutputStream fo = null; // 输出到文件

        try {
            fo = new FileOutputStream(userImportFile);
            wb.write(fo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fo != null){
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeToFailUserExcel(XSSFWorkbook wb){

        File userImportFile = new File(basePath + "failUserImport.xlsx");
        FileOutputStream fo = null; // 输出到文件

        try {
            fo = new FileOutputStream(userImportFile);
            wb.write(fo);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fo != null){
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
