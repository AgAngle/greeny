import constant.Constant;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import util.ExcelUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码目前仅支持2007及以上文件后缀为".xlsx"格式的文件
 * 各字段的关键字有待整理，如有缺省，可自行添加关键字
 * 各字段匹配完后会删除，以免与其他字段关键词匹配产生误差，故匹配顺序会影响准确率，可根据需求调整
 */
public class ExcelTest {

//    //文件所在目录
////    public static String fileInputDir = "/Users/chenjianxing/Documents/2018.xlsx";
////    //文件输出目录
////    public static String fileOutputDir = "/Users/chenjianxing/Documents/2018_Finall.xlsx";
////    //拆分完的子标签
////    public static String[] newRows = new String[]{"包装规格","品牌","厂号","牛种","饲养周期","等级","饲养方式"};
////    //存储每行数据的子标签的标签值
////    public static List<Map<String, String>> newTypeMapList = new ArrayList<Map<String, String>>();
////    //包装规格的关键字
////    public static String []  packKey = new String[]{"KG","kg","/箱","规格","件","千克","/袋","/包"};
////    //饲养周期的关键字，由于“年”的关键词有较大几率出现在其他字段，故使用正则匹配出数字加年如“3年”或“3-5年”
////    public static String [] breedCycleCKey = new String[]{"月","天","岁","牛龄"};
////    //等级的关键字
////    public static String [] rankKey = new String[]{"级"};
////    //厂号的关键字，由于数据的不规则，使用正则匹配了“SIF+数字”、“PH+数字”、“ME+数字”或纯数的厂号
////    public static String [] factoryNum = new String[]{"厂"};
////    //饲养方式的关键字
////    public static String [] FeedingWayKey = new String[]{"草饲","谷饲"};
////    //牛种的关键字
////    public static String [] cattleKey = new String[]{
////            "潘帕斯牛","黄牛","阉公牛","内络尔牛","Hereford","ZEIBU","Zebu","Bostaurusindicus","夏洛莱","朋斯麻拉","母牛","Angus","荷斯坦公牛","英国牛","品种","NELORE","海福特","Britishbreeds","墨瑞灰牛","泽西","黄安格斯牛种","安格斯，西门塔尔，赫里福，夏洛莱等","黑白花牛混合","海福特牛婆罗门牛","邦斯马拉","牛种:安格斯","海弗尔及夏洛特杂交牛",
////            "BLACKANGUS","弗里西亚","安格斯，夏洛莱，利木赞","普通牛肉","海富特","安格斯夏洛莱牛","和牛","圣热特鲁迪斯","和牛安格斯","ZEBU牛","安格斯和其他牛","邦斯马拉牛","婆罗门","塞布水牛","布兰格斯莱","赫里福牛","安格斯","西门塔尔，夏洛莱，赫里福，安格斯等","黑安格斯","利木赞","印度杂交牛","普通","科诺乐牛","海福特牛","牛种:安格斯和海福特",
////            "海福特安格斯利木赞夏洛莱及其杂交牛","海福特牛、安格斯杂交牛","公牛","黑白花牛","安格斯牛与海福特牛杂交牛","混种","内络尔牛(Nellore)、居尔牛(Gyr)、克利罗牛(Griollo)","安格斯，海福特，以及他们的杂交","杂交牛","安格斯，海福特牛","海福特牛和安格斯牛的杂交牛","牛种：夏洛特，海富","牛种:angusandbranguscattle","海福特牛和安格斯牛杂交",
////            "荷斯坦牛和欧洲荷斯坦牛","海利丰","旱地之星","波罗门","优质幼牛","多数为婆罗门牛和瘤牛","Zeibu牛","英国种牛","婆罗门牛等","无品种","赫里福德、安格斯、夏洛啦","荷斯坦牛","海弗利、西门塔尔等","HEIFERS","好斯敦","ZEBU(瘤牛)","克莱蒙梭","赫里福牛，泽西牛，黑白花牛混合","安格斯牛老母牛","亚伯丁安格斯","海福特，安格斯以及杂交牛种","主要海福特",
////            "海福特牛或安格斯牛","牛种:安格斯/海福特","欧洲牛种等杂交","牛种:安格斯牛","赫里福德，夏洛莱","西门塔尔牛，利木赞牛阉牛或母牛","混合品种","弗里斯兰","印度牛","内洛尔","安格斯牛及杂交品种","海福特牛、安格斯牛杂交牛","短角牛","ZEBUNELORE","尼尔洛","欧洲海福特、安格斯、利木赞、夏洛莱及其杂交","白脸牛","尼尔洛牛","热带牛种","安格斯/海福特等英国牛种",
////            "杂交","安格斯牛","安格斯，海福特以及杂交牛","赫尔福特牛","安格斯牛和海福特","海佛牛","黑白花牛的杂交牛","瘤牛","Hereford","自然牛","安格斯等","欧洲牛和英国牛杂交","海弗利、西门塔尔","内洛尔牛居尔牛克利罗牛","黑安格斯牛","海福特，安格斯以及砸杂交牛","安格斯赫里福混种牛","海福特、安格斯牛","安格斯海福特以及杂交牛","巴西瘤牛","阉割公牛","海福特牛和安格斯牛的杂交种",
////            "安格斯等品种","不莱蒙特肉牛","黄牛牛种","海福特牛和安格斯牛","夏洛莱及其杂交","牛种：安格斯和海福特","安格斯/婆罗门牛","居尔牛克利罗","其他英国品种","海佛尔及夏洛特杂交牛种","尼洛丽","阉牛种","西门达尔","牛种","安格斯牛种","NeloreZebu","海富特牛","内洛儿肉牛","普通肉牛","公母牛都有","英国和大陆的品种","安格斯牛、海福特牛等","弗里斯兰牛","赫里福德","欧洲海福特",
////            "小乳牛","西门塔尔","BRANGUS牛","内络尔牛、居尔牛、克利罗牛","安格斯牛、赫里福德牛、夏洛莱牛","海福特牛，安格斯牛公母牛都有","牛种","泽西牛，黑白花牛","海福特牛，安格斯牛及其杂交牛","普通牛","混种阉割公牛","内罗门牛"
////    };
////    //品牌的关键字，由于品牌中一些名字较短品牌可能会出英文品名中，故先排除掉英文品名，再进行匹配
////    public static String[] brandKey = new String [] {
////            "PUL","KILCOYPURE","TACUAREMBO","BERNAL","GOODBEEF","SWIFT","GICO","LAANONIMA",
////            "BEEFMASTER","GEJOTA","FRIBOI","MATABOI", "GORINA","PANDO","TAYLOR","LAANONIMA","ELARREO",
////            "MinervaS/A","COMPANIA","MARFRIG","VELES","无品牌", "BXFOODS","GREENLEA","RANERSVALLE","LORSINAL",
////            "MONTECILLOS","FRIGORIFICO","MatTradingLLC", "FRIGOESTRELA","CIISA","COOPEMONTECILLOS","RIOPLATENSE",
////            "COCARSA","LORSINALS.A.","BPU","ALLIANCE","LASHERAS", "PLATE","COLES","WINGHAM","GreaterOmahaPackingCo.Inc.",
////            "FINEXCOR","SecurityFoods","FRIGOL","FRIAR","CASSINO", "GJ","FRIARSA","CONNOR","KARANBEEF","FRIGORIFICOGORINA",
////            "JBS","CANADIANDIAMOND","MAFRISUR","CREEKSTONE","KINGWARM", "SILVERFERNFARMS","MINERVA","FLANZ","RANGERSVALLEY","INALER",
////            "C.C.P.","哈维牛肉","DIAMANTINAWAGYU/LOCKYERVALLEY","FrigorificoLasPiedrasS.A.", "FRIGORIFICORIOSEGUNDO","WORLDBEEF","MINERVAFOODSASIA",
////            "LOGROS","FRISA","IMPRIALVALLEY", "MEZAFOODS","ESTABLECIMIENTOS","TEYS","RALPHS","AMH","FRIGOESRELAS.A","AACO","哈维","FRIGOLS/A",
////            "BPUMEATURUGUAY","SARUBBI","P.G.E.S.A","MEATEX","WORLDBEEF","RONDATEL","RONDATELS.A","JOCAUSTRALIA","SILVER","纽龙","BINDAREE","KILCOY",
////            "FB","TEYSAUSTRALIA","SWIFT&COMPANYTRADE","IBP","SOUTH","HARVEY","PURE640","SIRSIL","CARRASCO","FRFIBOI","SOLIS","AFFCO","WARMOLLFOODS",
////            "ESTRELA","ARRE","TAYLORPRESTON","TBS","RAFAELA","MONTANA","SLANEY","COTO","HAWKE","SBAYPREMIUMBEEF","FRIGOSORNO","GANADERAARENALES",
////            "RALPHS","SUDAMBEEF","WARMOLL","QUICKFOOD","LOCKYER?VALLEY","CMP","FRIGOCERROS.A.","PALATARE","和KURRABINYA","MORA","JOHNDEE","SIRSIL","CHUBB","THOMAS",
////            "MINERVAS/A","ANGUSPURE","GORINA","FRIARS","GORINA","SANGER","LASPIEDRAS","JBSAUSTRALIAPTY","COCARS","PHOENIX","CHOICE","MINERVAL","FRIGORIFICOLASPIEDRASS.A",
////            "FRICASA","SilverFern","FORRES-BELTRANS.A.","RESERVE","ST.HELEN","SILVERFERNFARMSLIMITED","FRIGOYI","FORRES-BELTRANS.A","Meramist","UNIVERSALBEEFPACKERS",
////            "FrigiestrelaS.A./EstrelaAlimentos","Barcco","ECT","LAGANADERA","PALTE","G&K","LORSINALSA","FRIGORIFICOGORINAS.A.I.C","JJBS","FRISA-FRIGORIFICORIODOCES/A",
////            "FRIGORIFICOSANJACINTONIREA","YOLARNO","RALPH","NORTHERCO-OPERATIVE","NORTHERN","RIVERLANDS","NIREA","CASTI","品牌","JOHN",
////            "KCNatural"
////    };
////    //英文品类的个别关键字
////    public static String [] englishNameKey = new String[] {
////            "FROZEN","BOLAR","OYSTER","CHUCK","BEEF","frozen","BONELESS",
////            "ROUND","FLANK","STRIPLOIN","BRISKET","SHIN","SHOULDER","NECK",
////            "KNUCKLE","CUBE"
////    };
////
////    //工作簿对象
////    public static XSSFWorkbook wb = null;
////    //表格数目
////    public static Integer sheetNum = null;
////
////    //代码只处理了第一张表格，以下字段默认是第一张表格中的属性
////
////    //表格对象
////    public static XSSFSheet firstSheet = null;
////    //首行
////    public static XSSFRow fistRow = null;
////    //列数
////    public static Integer colCount = null;
////    //真实列数（含有操作或者内容的真实行数）
////    public static Integer realRowCount = null;
////    //规格型号所在的列号
////    public static Integer typeRowNum = null;
////
////    public static ExcelUtil excelUtil = new ExcelUtil();
////
////    @Test
////    public void test(String [] args) throws Exception {
////
////        System.out.println("文件载入中...");
////        wb = excelUtil.loadExcel2007(fileInputDir);
////        System.out.println("加载文件成功！");
////
////        //初始化负值操作
////        init();
////
////        System.out.println("行数: " + realRowCount + " 行");
////        System.out.println("列数: " + colCount + "列");
////
////        System.out.println("============");
////
////        System.out.println("数据处理中...");
////
////        for(int i = 2 ; i <= realRowCount; i++){
////
////            Map<String, String> newTypeMap = new HashMap<String, String>();
////            String type = excelUtil.readExcelByRowAndCol(1,i, typeRowNum);
////            LinkedList<String> typeList = splitAndClearType(type);
////
////            //产品规格
////            newTypeMap = getNewTypeMap(typeList, Constant.PACK_SPECIFICATION, newTypeMap);
////
////            //饲养方式
////            newTypeMap = getNewTypeMap(typeList, Constant.FEEDING_WAY, newTypeMap);
////
////            //饲养周期
////            newTypeMap = getNewTypeMap(typeList, Constant.BREEDING_CYCLE, newTypeMap);
////
////            //牛种
////            newTypeMap = getNewTypeMap(typeList, Constant.CATTLE, newTypeMap);
////
////            //品牌
////            newTypeMap = getNewTypeMap(typeList, Constant.BRAND, newTypeMap);
////
////            //厂号
////            newTypeMap = getNewTypeMap(typeList, Constant.FACTORY_NUM, newTypeMap);
////
////            //等级
////            newTypeMap = getNewTypeMap(typeList, Constant.RANK, newTypeMap);
////
////            newTypeMapList.add(newTypeMap);
////
//////            if(newTypeMap.get(Constant.CATTLE) == null){
//////                System.out.println( "rowNum：" + i + " | " + Constant.CATTLE + ": " + newTypeMap.get(Constant.CATTLE));
//////                System.out.println("typeList：" + typeList);
//////            }
////
////        }
////
////        //打印结果：
//////        for(int i = 0; i< newTypeMapList.size(); i++){
//////            System.out.println("rowNum: " + i);
//////            for (String key : newTypeMapList.get(i).keySet()){
//////                System.out.print(key + ": " + newTypeMapList.get(i).get(key) + " | ");
//////            }
//////            System.out.println();
//////        }
////
////        System.out.println("数据处理完毕");
////        System.out.println("============");
////        System.out.println("文件写入中...");
////        writeToExcel(1, colCount, newTypeMapList, newRows);
////        System.out.println("文件写入完毕");
////        excelUtil.close();
////    }
////
////    public  void init() throws Exception {
////
////        sheetNum = wb.getNumberOfSheets();
////        firstSheet = wb.getSheetAt(0);
////        //int rowCount = excelUtil.getRowCount(1);
////        fistRow = firstSheet.getRow(0);
////        colCount = (int)fistRow.getLastCellNum();
////        realRowCount = excelUtil.getRealRowCount(1);
////        String[] strings = excelUtil.readExcelByRow(1, 1);
////        typeRowNum = null;
////        for(int i = 1; i < 15; i++) {
////            String value = excelUtil.readExcelByRowAndCol(1, 1, i);
////            if(value.trim().equals("规格型号")){
////                typeRowNum = i;
////                break;
////            }
////        }
////
////    }
////
////    /**
////     *
////     * @param typeList
////     * @param newType
////     * @param newTypeMap
////     * @return
////     */
////    public  Map<String,String> getNewTypeMap(LinkedList<String> typeList, String newType, Map<String, String> newTypeMap) {
////
////        for(int i = 0; i < typeList.size(); i++){
////
////            //产品规格
////            if(newType.trim().equals(Constant.PACK_SPECIFICATION)){
////                for (int j = 0; j < packKey.length; j++){
////                    if(typeList.get(i).contains(packKey[j])){
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////                }
////            }
////
////            //品牌
////            if(newType.trim().equals(Constant.BRAND)){
////                for (int j = 0; j < brandKey.length; j++){
////                    if(typeList.get(i).contains(brandKey[j])){
////                        Boolean isOk = true;
////                        for (int k = 0; k < englishNameKey.length; k++){
////                            if(typeList.get(i).contains(englishNameKey[k])){
////                                isOk = false;
////                                break;
////                            }
////                        }
////                        if(isOk){
////                            newTypeMap.put(newType, typeList.get(i));
////                            typeList.remove(i);
////                        }
////                        break;
////                    }
////                }
////            }
////
////            //饲养方式
////            if(newType.trim().equals(Constant.FEEDING_WAY)){
////                for (int j = 0; j < FeedingWayKey.length; j++){
////                    if(typeList.get(i).contains(FeedingWayKey[j])){
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////                }
////            }
////
////            //牛种
////            if(newType.trim().equals(Constant.CATTLE)){
////                for (int j = 0; j < cattleKey.length; j++){
////                    if(typeList.get(i).contains(cattleKey[j])){
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////                }
////            }
////
////            //饲养周期
////            if(newType.trim().equals(Constant.BREEDING_CYCLE)){
////                for (int j = 0; j < breedCycleCKey.length; j++){
////                    if(typeList.get(i).contains(breedCycleCKey[j])
////                            || Pattern.matches("^[0-9](-[0-9])?年$",typeList.get(i))){
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////                }
////            }
////
////
////            //厂号
////            if(newType.trim().equals(Constant.FACTORY_NUM)){
////                for (int j = 0; j < factoryNum.length; j++){
////                    if(
////                            typeList.get(i).contains(factoryNum[j])
////                                    || Pattern.matches("^SIF\\s*[0-9]+$",typeList.get(i).trim())
////                                    || Pattern.matches("^ME\\s*[0-9]+$",typeList.get(i).trim())
////                                    || Pattern.matches("^PH\\s*[0-9]+$",typeList.get(i).trim())
////                                    || Pattern.matches("^\\d+$",typeList.get(i))){
////                        //System.out.println(">>>>>>>" + typeList.get(i));
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////
////                }
////            }
////
////            //等级
////            if(newType.trim().equals(Constant.RANK)){
////                for (int j = 0; j < rankKey.length; j++){
////                    if(typeList.get(i).contains(rankKey[j])){
////                        newTypeMap.put(newType, typeList.get(i));
////                        typeList.remove(i);
////                        break;
////                    }
////                }
////            }
////
////            if(newTypeMap.get(newType) != null){
////                break;
////            }
//////                System.out.println("产品规格" + typeList.get(i));
////
////        }
////
////        return newTypeMap;
////
////    }
////
////    /**
////     * 将传入的规格型号按"|"分隔并去除前两行数字和空的值
////     * @param type 产品规格
////     * @return
////     */
////    public  LinkedList<String> splitAndClearType(String type){
////
////        String[] types = type.split("\\|");
////        //将数组转换成linkList，增删数据时提高效率
////        Collection<String> collection = Arrays.asList(types);
////        LinkedList<String> typeList = new LinkedList<String>(collection);
////
////        //如果前两个是数字则删除
////        if(Pattern.matches("^\\d+$", typeList.get(0))){
////            typeList.remove(0);
////        };
////        if(Pattern.matches("^\\d+$", typeList.get(0))){
////            typeList.remove(0);
////        };
////
////
////        for(int i = 0; i  < typeList.size(); i++){
////            String value = typeList.get(i);
////
////            //去除空项
////            if(value == null || value.trim().equals("")){
////                typeList.remove(i);
////                i--; //删除后下标迁移了一位，遍历下标不移动
////                break;
////            }
////
////            //去除产品编号
////            if(value != null && value.trim().contains("产品编号")){
////                typeList.remove(i);
////                i--; //删除后下标迁移了一位，遍历下标不移动
////                break;
////            }
////
////            //去除英文品名
////            for (int j = 0; j < englishNameKey.length; j++){
////                if(typeList.get(i).contains(englishNameKey[j])){
////                    typeList.remove(i);
////                    i--;
////                    break;
////                }
////            }
////
////        }
////
//////        for(int i = 0; i  < typeList.size(); i++){
//////            System.out.println(typeList.get(i) + " | ");
//////        }
////
////        return typeList;
////    }
////
////    public  void writeToExcel(Integer startRow, Integer startCol, List<Map<String, String>> newTypeMapList, String[] newRows) throws Exception {
////        XSSFRow row = null;
////        XSSFCell cell = null;
////
////        // 设置标题
////        for (int j = 0; j < newRows.length; j++){
////            row = firstSheet.getRow( 0);
////            if (row == null) {
////                row = firstSheet.createRow(0); // 该行无数据，创建行对象
////            }
////            cell = row.createCell(startCol + j);
////            cell.setCellValue(newRows[j]);
////        }
////
////
////        for(int i = 0; i< newTypeMapList.size(); i++){
////            //System.out.println("rowNum: " + i);
////            row = firstSheet.getRow(i + 1);
////            if (row == null) {
////                row = firstSheet.createRow(i); // 该行无数据，创建行对象
////            }
////            for (int j = 0; j < newRows.length; j++){
////                cell = row.createCell(startCol + j); // 创建指定单元格对象。如本身有数据会替换掉
////                cell.setCellValue(newTypeMapList.get(i).get(newRows[j])); // 设置内容
////            }
////
////        }
////
////        FileOutputStream fo = new FileOutputStream(fileOutputDir); // 输出到文件
////        wb.write(fo);
////
////        if(fo != null){
////            fo.close();
////        }
////
////    }
}
