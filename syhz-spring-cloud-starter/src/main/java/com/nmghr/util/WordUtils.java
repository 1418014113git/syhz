/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年8月14日 上午11:30:24 
 * @version 1.0   
 */
public class WordUtils {

  /**
   * 根据模板生成新word文档
   * 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
   * @param inputUrl 模板存放地址
   * @param outPutUrl 新文档存放地址
   * @param textMap 需要替换的信息集合
   * @param tableList 需要插入的表格信息集合
   * @return 成功返回true,失败返回false
   */
  public static boolean createWord(InputStream inputStream, HttpServletResponse response,
      Map<String, String> textMap, List<String[]> tableList) {

    // 模板转换默认成功
    boolean changeFlag = true;
    
    ServletOutputStream out = null;
    BufferedOutputStream bos = null;
    try {
      // 获取docx解析对象
      //XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(inputUrl));
      XWPFDocument document = new XWPFDocument(inputStream);
      // 解析替换文本段落对象
      WordUtils.initText(document, textMap);
      // 解析替换表格对象
      WordUtils.initTable(document, textMap, tableList);
      out = response.getOutputStream();
      bos = new BufferedOutputStream(out);
      document.write(bos);
      
    } catch (IOException e) {
      e.printStackTrace();
      changeFlag = false;
    } finally {
      if (bos != null) {
        try {
          bos.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    return changeFlag;

  }

  /**
   * 替换段落文本
   * @param document docx解析对象
   * @param textMap 需要替换的信息集合
   */
  public static void initText(XWPFDocument document, Map<String, String> textMap){
      //获取段落集合
      List<XWPFParagraph> paragraphs = document.getParagraphs();
      for (XWPFParagraph paragraph : paragraphs) {
          //判断此段落时候需要进行替换
          String text = paragraph.getText();
          if(checkText(text)){
              List<XWPFRun> runs = paragraph.getRuns();
              for (XWPFRun run : runs) {
                  //替换模板原来位置
                 run.setText(changeValue(run.toString(), textMap),0);
              }
          }
      }
  }

  /**
   * 替换表格对象方法
   * @param document docx解析对象
   * @param textMap 需要替换的信息集合
   * @param tableList 需要插入的表格信息集合
   */
  public static void initTable(XWPFDocument document, Map<String, String> textMap,
      List<String[]> tableList) {
    // 获取表格对象集合
    List<XWPFTable> tables = document.getTables();
    if (CollectionUtils.isNotEmpty(tables)) {
      for (int i = 0; i < tables.size(); i++) {
        // 只处理行数大于等于2的表格，且不循环表头
        XWPFTable table = tables.get(i);
        if (table.getRows().size() > 1) {
          // 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
          if (checkText(table.getText())) {
            List<XWPFTableRow> rows = table.getRows();
            // 遍历表格,并替换模板
            eachTable(rows, textMap);
          } else {
            if (CollectionUtils.isNotEmpty(tableList)) {
              insertTable(table, tableList);
            }
          }
        }
      }
    }

  }
  /**
   * 遍历表格
   * @param rows 表格行对象
   * @param textMap 需要替换的信息集合
   */
  public static void eachTable(List<XWPFTableRow> rows ,Map<String, String> textMap){
      for (XWPFTableRow row : rows) {
          List<XWPFTableCell> cells = row.getTableCells();
          for (XWPFTableCell cell : cells) {
              //判断单元格是否需要替换
              if(checkText(cell.getText())){
                  List<XWPFParagraph> paragraphs = cell.getParagraphs();
                  for (XWPFParagraph paragraph : paragraphs) {
                      List<XWPFRun> runs = paragraph.getRuns();
                      for (XWPFRun run : runs) {
                        run.setText(tableValue(run.toString(), textMap),0);
                      }
                  }
              }
          }
      }
  }

  /**
   * 为表格插入数据，行数不够添加新行
   * @param table 需要插入数据的表格
   * @param tableList 插入数据集合
   */
  public static void insertTable(XWPFTable table, List<String[]> tableList){
      //创建行,根据需要插入的数据添加新行，不处理表头
      for(int i = 1; i < tableList.size(); i++){
          XWPFTableRow row =table.createRow();
      }
      //遍历表格插入数据
      List<XWPFTableRow> rows = table.getRows();
      for(int i = 1; i < rows.size(); i++){
          XWPFTableRow newRow = table.getRow(i);
          List<XWPFTableCell> cells = newRow.getTableCells();
          for(int j = 0; j < cells.size(); j++){
              XWPFTableCell cell = cells.get(j);
              cell.setText(tableList.get(i-1)[j]);
          }
      }
  }



  /**
   * 判断文本中时候包含$
   * @param text 文本
   * @return 包含返回true,不包含返回false
   */
  public static boolean checkText(String text){
      boolean check  =  false;
      if(text.indexOf("$")!= -1 || text.indexOf("{")!=-1 || text.indexOf("}")!=-1 ){
          check = true;
      }
      return check;

  }
  
  public static boolean checkTableText(String text){
    boolean check  =  false;
    if(text.indexOf("$")!= -1 || text.indexOf("}")!= -1){
        check = true;
    }
    return check;

}

  /**
   * 匹配传入信息集合与模板
   * @param value 模板需要替换的区域
   * @param textMap 传入信息集合
   * @return 模板需要替换区域信息集合对应值
   */
  public static String changeValue(String value, Map<String, String> textMap){
    Set<Entry<String, String>> textSets = textMap.entrySet();
    for (Entry<String, String> textSet : textSets) {
        //匹配模板与替换值 格式${key}
        String key = textSet.getKey();
        if(value.indexOf(key)!= -1){
            value = textSet.getValue();
        }
    }
    //模板未匹配到区域替换为空
    if(checkText(value)){
        value = "";
    }
    return value;
}

  public static String tableValue(String value, Map<String, String> textMap){
    Set<Entry<String, String>> textSets = textMap.entrySet();
    for (Entry<String, String> textSet : textSets) {
        //匹配模板与替换值 格式${key}
        String key = textSet.getKey();
        if(value.indexOf(key)!= -1){
            value = textSet.getValue();
        }
    }
    //模板未匹配到区域替换为空
    if(checkTableText(value)){
        value = "";
    }
    return value;
}



/*  public static void main(String[] args) {
      //模板文件地址
      String inputUrl = "D:\\demo.docx";
      //新生产的模板文件
      String outputUrl = "D:\\demoTest.docx";

      Map<String, String> testMap = new HashMap<String, String>();
      testMap.put("name", "小明");
      testMap.put("sex", "男");
      testMap.put("address", "软件园");
      testMap.put("phone", "88888888");
      testMap.put("email", "brook@163.com");
      

      List<String[]> testList = new ArrayList<String[]>();
      testList.add(new String[]{"1","1AA","1BB","1CC"});
      testList.add(new String[]{"2","2AA","2BB","2CC"});
      testList.add(new String[]{"3","3AA","3BB","3CC"});
      testList.add(new String[]{"4","4AA","4BB","4CC"});

      WordUtils.createWord(inputUrl, outputUrl, testMap, null);
  }*/
}
