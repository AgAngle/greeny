package huarun.com.utils;

import huarun.com.enity.AssetsUser;
import huarun.com.enity.AssetsUsers;
import huarun.com.enity.DataModle;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlUtils {


    public static final String basePath = "/Users/chenjianxing/Documents/init_sql/";

    public static void createOrg(Map<String,String> orgs, String [] orgStrs){
        File orgCreateSql = new File(basePath + "01_orgCreateSql.sql");
        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(orgCreateSql);

            for (int i = 0; i < orgStrs.length; i++){
                UUID uuid = UUID.randomUUID();
                orgs.put(orgStrs[i], uuid.toString());
                String sql = "INSERT INTO orgs_organization (id,name,created_by,date_created,comment) " +
                        "VALUES ('" + uuid.toString().replace("-", "") + "','" + orgStrs[i] + "','Administrator','2019-05-23 07:56:34.458','test');";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());
            }

        } catch (Exception e) {
            System.out.println("general orgCreateSql fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }
    }

    public static void createUserGroup(Set<String> userGroups, Map<String, String> userGroupMap, Map.Entry<String, String> org) {

        File createUserGroupSql = getFile(org.getKey(), "2_userGroupSql.sql");
        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(createUserGroupSql);

            for (String userGroup :
                 userGroups) {
                UUID uuid = UUID.randomUUID();
                userGroupMap.put(userGroup, uuid.toString());
                String sql = "INSERT INTO users_usergroup (id,name,comment,date_created,created_by,org_id) " +
                        "VALUES ('" + uuid.toString().replace("-","") + "','" + userGroup + "','','" + getCurrentTime() + "','Administrator','" + org.getValue() + "') ;";
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currenTime = format.format(time);
        return currenTime;
    }

    public static void addUserToOrg(Collection<DataModle> datas, Map.Entry<String, String> org) {

        File addUserToOrgSql = getFile(org.getKey(), "3_addUserToOrgSql.sql");
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
                        "select u.id,g.id from users_user u, orgs_organization g where u.username='" + userName + "' and g.name = '" + org.getKey() + "';";
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

    public static void addUserToUserGroup(Map<String,DataModle> datas, Set<String> userGroups, Map.Entry<String, String> org) {

        File orgCreateSql = getFile(org.getKey(), "4_addUserToUserGroup.sql");
        FileOutputStream fileOut = null;

        List<String> userNames = new ArrayList<String>();

        try {

            fileOut = new FileOutputStream(orgCreateSql);

            for (String userGroup:
                    userGroups) {

                for (String userName:
                     datas.get(userGroup).getUserNames()) {

                    String sql = "INSERT INTO users_user_groups(user_id, usergroup_id) " +
                            "select u.id,g.id from users_user u, users_usergroup g where u.username='" + userName + "' and g.name = '" + userGroup + "' and g.org_id = '" + org.getValue() + "';";
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

    public static void createNodes(Map.Entry<String, String> org, Set<String> nodes, Map<String, Integer> rootNodes, Integer rootNodeStartKey) {

        File createNodesSql = getFile(org.getKey(), "5_createNode.sql");
        FileOutputStream fileOut = null;

        try {

            UUID uuid = UUID.randomUUID();
            fileOut = new FileOutputStream(createNodesSql);

            String rootNodeSql = "INSERT INTO assets_node (id,`key`,value,child_mark,date_create,org_id) " +
                    "VALUES ('" + uuid.toString().replace("-","") + "','" + rootNodeStartKey + "','" + org.getKey() + "'," + nodes.size() + ",'" + getCurrentTime() + "','" + org.getValue() + "') ;";
            rootNodes.put(org.getKey(), rootNodeStartKey);
            fileOut.write(rootNodeSql.getBytes());
            fileOut.write("\n".getBytes());

            int nodeStartKey = 0;
            for (String node :
                    nodes) {
                UUID uuid1 = UUID.randomUUID();
                String nodeSql = "INSERT INTO assets_node (id,`key`,value,child_mark,date_create,org_id) " +
                        "VALUES ('" + uuid1.toString().replace("-","") + "','" + rootNodeStartKey + ":" + nodeStartKey++ + "','" + node + "',0,'" + getCurrentTime() + "','" + org.getValue() + "') ;";
                fileOut.write(nodeSql.getBytes());
                fileOut.write("\n".getBytes());
            }

        } catch (Exception e) {
            System.out.println("createNodes fail..");
            e.printStackTrace();
        }finally {
            closeOutPutStream(fileOut);
        }

    }

    public static void createAdminuserAndSystemuser(Map.Entry<String, String> org, AssetsUsers assetsUsers, Map<String, DataModle> orgDatas) {

        File createAdminuserAndSystemuserSql = getFile(org.getKey(), "1_createAdminuserAndSystemuserSql.sql");
        FileOutputStream fileOut = null;

        try {

            fileOut = new FileOutputStream(createAdminuserAndSystemuserSql);

            for (AssetsUser adminuser:
                    assetsUsers.getAdminusers()) {
                UUID uuid = UUID.randomUUID();
                String adminuserSql = "INSERT INTO assets_adminuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,become,become_method,become_user,`_become_pass`,org_id) " +
                        "VALUES ('" + uuid.toString().replace("-","") + "','" + adminuser.getName() + "','" + adminuser.getUseranme() + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',1,'sudo','root','','" + org.getValue() + "') ;";
                fileOut.write(adminuserSql.getBytes());
                fileOut.write("\n".getBytes());
            }


            for (String userGroup:
                    orgDatas.keySet()) {
                List<String> systemUsers = orgDatas.get(userGroup).getSystemUsers();
                for (String systemuser:
                        systemUsers) {
                    UUID uuid = UUID.randomUUID();
                    String systemuseSql = "INSERT IGNORE INTO assets_systemuser (id,name,username,`_password`,`_public_key`,comment,date_created,date_updated,created_by,priority,protocol,auto_push,sudo,shell,login_mode,org_id) " +
                            "VALUES ('" + uuid.toString().replace("-","") + "','" + systemuser + "','" + systemuser + "',NULL,'','','" + getCurrentTime() + "','" + getCurrentTime() + "','Administrator',20,'ssh',0,'/bin/whoami','/bin/bash','manual','" + org.getValue() + "') ;";
                    fileOut.write(systemuseSql.getBytes());
                    fileOut.write("\n".getBytes());
                }
            }


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

    public static void createAssetPermission(Map.Entry<String, String> org, Map<String, DataModle> orgDatas, AssetsUsers assetsUsers) {

        File createAssetPermissionSql = getFile(org.getKey(), "7_createAssetPermissionSql.sql");

        FileOutputStream fileOut = null;

        try {
            fileOut = new FileOutputStream(createAssetPermissionSql);
            for (Map.Entry<String, DataModle> entry:
                    orgDatas.entrySet()) {

                String rule = entry.getKey();
                UUID uuid = UUID.randomUUID();
                String sql = "INSERT INTO perms_assetpermission (id,name,is_active,date_start,date_expired,created_by,date_created,comment,org_id) " +
                        "VALUES ('" + uuid.toString().replace("-","") + "','" + rule + "',1,'" + getCurrentTime() + "','" + "2100-02-09 00:00:00" + "','Administrator','" + getCurrentTime() + "','','" + org.getValue() + "');";
                fileOut.write(sql.getBytes());
                fileOut.write("\n".getBytes());

                //绑定动作
                String sql1 = "INSERT INTO perms_assetpermission_actions(assetpermission_id,action_id)  " +
                        "select u.id,g.id from perms_assetpermission u, perms_action g where u.name='" + rule + "' and u.org_id = '" + org.getValue() + "' and g.name = 'all';";
                fileOut.write(sql1.getBytes());
                fileOut.write("\n".getBytes());


                //绑定节点
                String sql2 = "INSERT INTO perms_assetpermission_nodes (assetpermission_id,node_id) " +
                        "select u.id,g.id from perms_assetpermission u, assets_node g where u.name='" + rule + "' and u.org_id = '" + org.getValue() + "'  and g.value = '" + rule + "' and g.org_id = '" + org.getValue() + "' ;";
                fileOut.write(sql2.getBytes());
                fileOut.write("\n".getBytes());

                //绑定用户组
                String sql3 = "INSERT INTO perms_assetpermission_user_groups (assetpermission_id,usergroup_id) " +
                        "select u.id,g.id from perms_assetpermission u, users_usergroup g where u.name='" + rule + "' and u.org_id = '" + org.getValue() + "' and g.name = '" + rule + "' and g.org_id = '" + org.getValue() +"' ;";
                fileOut.write(sql3.getBytes());
                fileOut.write("\n".getBytes());

                for (String systemuser:
                        entry.getValue().getSystemUsers()) {
                    //授权规则绑定系统用户
                    String sql4 ="INSERT INTO perms_assetpermission_system_users (assetpermission_id,systemuser_id) " +
                            "select u.id,g.id from perms_assetpermission u, assets_systemuser g where u.name='" + rule + "' and u.org_id = '" + org.getValue() + "'  and g.name = '" + systemuser + "' and g.org_id = '" + org.getValue() + "' ;";
                    fileOut.write(sql4.getBytes());
                    fileOut.write("\n".getBytes());

                    //资产节点绑定系统用户
                    String sql5 = "INSERT INTO assets_systemuser_nodes (systemuser_id,node_id) " +
                            "select u.id,g.id from assets_systemuser u, assets_node g where u.name='" + systemuser + "' and g.value = '" + rule + "' and u.org_id = '" + org.getValue() + "' and g.org_id = '" + org.getValue() + "';";
                    fileOut.write(sql5.getBytes());
                    fileOut.write("\n".getBytes());

                    for (String hostName:
                            entry.getValue().getHostNames()) {
                        //资产绑定系统用户
                        String sql6 = "INSERT IGNORE INTO assets_systemuser_assets (systemuser_id,asset_id) " +
                                "select u.id,g.id from assets_systemuser u, assets_asset g where u.name='" + systemuser + "' and g.hostname = '" + hostName + "' and u.org_id = '" + org.getValue() + "' and g.org_id = '" + org.getValue() + "';";
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

    public static void write(OutputStream fileOut, String sql) throws IOException {
        fileOut.write(sql.getBytes());
        fileOut.write("\n".getBytes());
    }
}
