package com.noriental.praxissvr.question.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.utils.text.StringUtils;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.noriental.praxissvr.exception.PraxisErrorCode.PRAXIS_QUESTION_TYPE_NOT_FOUND;

/**
 * Created by hushuang on 2017/5/4.
 * 新增表格和上下角标的解析
 * 新增着重号的解析
 *
 */
public class ParseHtmlUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlToJsonUtil.class);


    /*
     * JSON转换器
     */
    public static ObjectMapper mapper = new ObjectMapper();
    /**
     * 用于添加选择题的题目选项
     */
    private static String[] abc = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
    /*
     * 第一个<P>不 newline
     */
    private volatile static boolean isFirst = true;
    /*
     *第一个<P>后面如果是table标签，则新增换行符
     */
    private volatile static boolean isFirstTable = true;
    /**
     * 根据题型 进行html转json
     */
    public synchronized static String html2json(String html,String questionType,long questionId,QuestionTypeDao questionTypeDao) throws IOException {
        //1.替换双引号为单引号
        String mark = replaceMark(html);

        Map<Integer, QuestionType> questionTypeMap = new HashMap<>();
        List<QuestionType> allEnable = questionTypeDao.findAllEnable();
        if (CollectionUtils.isNotEmpty(allEnable)) {
            for (QuestionType questionTypeEnabled : allEnable) {
                questionTypeMap.put(questionTypeEnabled.getTypeId().intValue(), questionTypeEnabled);
            }
        }
        QuestionType parentQuestion= questionTypeMap.get(Integer.parseInt(questionType));
        if(parentQuestion==null || parentQuestion.getStructId()==null){
            throw new BizLayerException("", PRAXIS_QUESTION_TYPE_NOT_FOUND);
        }

        //2.开始解析
        Map<String, Object> resultQuestion = readQuestion(questionType, mark, false, null,parentQuestion.getStructId(),questionTypeMap);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", questionId);
        wrapper.put("content", resultQuestion);

        return mapper.writeValueAsString(wrapper);
    }



    /**
     * 递归遍历题目以及小题
     * @param questionType
     * @param htmlJson
     * @param isChildQuestion
     * @param childQuestionMap
     * @return
     */
    public synchronized static Map<String,Object> readQuestion(String questionType, String htmlJson, boolean isChildQuestion, Map childQuestionMap,Long structId,Map<Integer, QuestionType> questionTypeMap) throws IOException {

        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> htmlMap;
        if (isChildQuestion) {
            htmlMap = childQuestionMap;
        } else {
            htmlMap = mapper.readValue(htmlJson, Map.class);
        }


        for (Map.Entry<String, Object> entry : htmlMap.entrySet()) {

            isFirst=true;

            if(entry.getValue()==null){
                continue;
            }
            /**
             *  获取map的value  例如：
             *  analysis=<p>分析分析分析分析分析分析</p>
             *  answer=[A, B, C, D]
             *  audio={url=, name=, size=}
             *
             */

            String keyAttr = entry.getKey();
            String valueHtml = entry.getValue().toString();
            if(valueHtml.contains("<") && valueHtml.contains(">")){
                Document document = Jsoup.parse(valueHtml);
                /**
                 * 只要<body></body>标签以及里面的内容
                 * 去掉<html></html>和<head></head>
                 *
                 * Document文档结构如下：
                 * <html>
                 *     <head>
                 *     </head>
                 *     <body>
                 *
                 *     </body>
                 * </html>
                 */
                Node node = document.childNode(0).childNode(1);

                //按照结构ID 4 去解析
                if(structId.equals(4L)&&"answer".equals(keyAttr)){
                    List<Map> answerList = new ArrayList<>();
                    // 选项数组，逐个解析
                    List array = (List) entry.getValue();
                    for (int i = 0; i < array.size(); i++) {
                        // isfirst重置
                        isFirst = true;

                        Map<String, Object> answer = new HashMap<>();
                        answer.put("index", i + 1);

                        // 一个answer作为一个单独的Document解析
                        Document optionDoc = Jsoup.parse((String) array.get(i));
                        node = optionDoc.childNodes().get(0).childNodes().get(1);

                        List<Map> groupList = new ArrayList<>();
                        parseNode(node, keyAttr, i, 0, groupList);
                        answer.put("group", groupList);
                        answerList.add(answer);
                    }
                    resultMap.put(keyAttr, answerList);


                }else if("options".equals(keyAttr)){

                    List<List<Map>> optionsResultList = new ArrayList<>();

                    // 选项数组，逐个解析
                    List array = (List) entry.getValue();
                    for (int i = 0; i < array.size(); i++) {
                        // isfirst重置
                        isFirst = true;
                        // 一个option作为一个单独的Document解析
                        Document optionDoc = Jsoup.parse((String) array.get(i));
                        node = optionDoc.childNodes().get(0).childNodes().get(1);

                        List<Map> optionMapList = new ArrayList<>();
                        parseNode(node, keyAttr, i, 0, optionMapList);
                        optionsResultList.add(optionMapList);
                    }
                    resultMap.put(keyAttr, optionsResultList);

                }else if("questions".equals(keyAttr)){
                    // 如果是复合题，再次递归当前方法，继续解析小题
                    List<Map> qtHtmlList = (List) entry.getValue();
                    List<Map> qtMapList = new ArrayList<>();
                    if (qtHtmlList.size() > 0) {
                        for (Map childQuestion : qtHtmlList) {
                            // 获取小题题型
                            Map childQuestionType = (Map) childQuestion.get("type");
                            //需要根据题目ID去获取一下结构ID
                            QuestionType questionTypeStruct= questionTypeMap.get(childQuestionType.get("id"));
                            if(questionTypeStruct==null || questionTypeStruct.getStructId()==null){
                                throw new BizLayerException("", PRAXIS_QUESTION_TYPE_NOT_FOUND);
                            }
                            qtMapList.add(readQuestion(childQuestionType.get("id").toString(), null, true, childQuestion,questionTypeStruct.getStructId(),questionTypeMap));
                        }
                    }
                    resultMap.put("questions", qtMapList);

                }else if ((structId.equals(8L)||structId.equals(3L)||structId.equals(5L)) &&"answer".equals(keyAttr)){  //去除简答题pad端答案带有中括号的问题
                    //struct_id  51(8)    32(3)     17(3)       4(3)    50(3)       19(3)
                    //按照结构ID 3,8 去解析
                    List<Map> mapList = new ArrayList<>();
                    Element ele = (Element) node;

                    String body = ele.select("body").get(0).html();

                    body = body.substring(1,body.length()-1);

                    Document parse = Jsoup.parse(body);
                    Node childNode = parse.childNode(0).childNode(1);

                    parseNode(childNode,"",0,0,mapList);

                    resultMap.put(keyAttr, mapList);
                }else if ((structId.equals(11L)||structId.equals(12L)) && "answer".equals(keyAttr)){  //听单词跟读单词
                    //按照结构ID 11,12 去解析
                    Map<String,Object> result = new HashMap<>();
                    List<Object> answList = new ArrayList<>();

                    List<Map> mapList = new ArrayList<>();
                    List array = (List)entry.getValue();
                    Map answerMap = (Map) array.get(0);
                    Map answer_audio = (Map) answerMap.get("answer_audio");
                    Object answer_content = answerMap.get("answer_content");
                    if(answer_audio!=null){
                        result.put("answer_audio",answer_audio);
                    }
                    Document parse = Jsoup.parse(answer_content+"");
                    Node childNode = parse.childNode(0).childNode(1);
                    parseNode(childNode,"",0,0,mapList);
                    result.put("answer_content",mapList);
                    answList.add(result);
                    resultMap.put(keyAttr,answList);

                } else {
                        //其他
                        // 其他标签
                        List<Map> mapList = new ArrayList<>();
                        parseNode(node, keyAttr, 0, 0, mapList);
                        resultMap.put(keyAttr, mapList);

                }

            }else {

                //按照结构ID 2 去解析
                //纯文本   判断题
                if (structId.equals(2L) && "answer".equals(keyAttr)) {
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        List array = (List) entry.getValue();
                        if (CollectionUtils.isNotEmpty(array)) {
                            resultMap.put(keyAttr, array.get(0));
                        } else {
                            logger.error("判断题结构答案不合法, 答案为空");
                            resultMap.put(keyAttr, "");
                        }
                    } else {
                        resultMap.put(keyAttr, value);
                    }
                }else if("prompt".equals(keyAttr)){
                    Object value = entry.getValue();
                    List<Map> maps = new ArrayList<>();
                    Map<String,Object> map = new HashMap<>();
                    map.put("type","text");
                    map.put("value",value+"");
                    maps.add(map);
                    resultMap.put("prompt",maps);
                } else {
                    resultMap.put(keyAttr, htmlMap.get(keyAttr));
                }

            }

        }

        return resultMap;
    }


    /**
     *
     * @param node   :需要解析的节点值
     * @param entryKey  ：需要解析的entryKey   例如 answer   options   audio  等
     * @param optionIndex : 当前选项下标索引
     * @param styleSum  ： 样式累加
     * @param mapList ：需要封装Map 值的返回结果
     */
    public synchronized static void parseNode(Node node,String entryKey,int optionIndex, int styleSum, List<Map> mapList){



        Map map = new HashMap();

        if(!"body".equals(node.nodeName())){
            //纯文本
            if(node instanceof TextNode){

                map.put("type", "text");
                map.put("value", ((TextNode) node).text());

            }else { //标签
                //标签可能带有的属性
                String src = "";
                int width = 0;
                int height = 0;
                String cls = "";
                String value = "";
                String latex = "";
                String style = "";
                int rows = 0;
                int columns=0;
                /**
                 * 如果为选择题时，要给选择题加上选项 类型为option
                 */
                if (isFirst && "options".equals(entryKey)) {
                    map.put("type", "option");
                    map.put("value", abc[optionIndex++]);
                }

                // 获取node上的Attribute
                List<Attribute> attrList = node.attributes().asList();
                for (Attribute attribute : attrList) {
                    if ("style".equals(attribute.getKey())) {
                        style = attribute.getValue();
                    }
                    if ("src".equals(attribute.getKey())) {
                        src = attribute.getValue();
                    }
                    if ("width".equals(attribute.getKey())) {
                        //width = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
                        width = Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^\\d^\\.]", "")))));
                    }
                    if ("height".equals(attribute.getKey())) {
                        //height = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
                        height=Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^\\d^\\.]", "")))));
                    }
                    if ("class".equals(attribute.getKey())) {
                        cls = attribute.getValue();
                    }
                    if ("value".equals(attribute.getKey())) {
                        value = attribute.getValue();
                    }
                    if ("data-latex".equals(attribute.getKey())) {
                        latex = attribute.getValue();
                    }
                    if ("rows".equals(attribute.getKey())) {
                        rows = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
                    }
                    if ("columns".equals(attribute.getKey())) {
                        columns = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
                    }

                }
                //当前节点是table并且前一个节点是p时，新增换行
                if(node!=null&&node.nodeName().equals("table")){
                    if(isFirstTable){
                        Node preNode= node.previousSibling();
                        if(preNode!=null&& preNode.nodeName().equals("p")){
                            Map mapTable = new HashMap();
                            mapTable.put("type", "newline");
                            mapTable.put("value", "1");
                            mapList.add(mapTable);
                            isFirstTable=false;
                        }
                    }
                }

                switch (node.nodeName()) {

                    case "p":
                        if (isFirst) { // 第一个p标签不需要new line
                            isFirst = false;
                            break;
                        }
                        map.put("type", "newline");
                        map.put("value", "1");
                        break;

                    case "sub":
                        if(node.childNodeSize()!=0){
                            if(node.childNode(0) instanceof TextNode){
                                map.put("type","icon");
                                map.put("value",((TextNode)node.childNode(0)).text());
                                map.put("direction",2);
                            }else{
                                String nodeName= node.childNode(0).nodeName();
                                if(nodeName.equals("img")){
                                    //map.put("height",Integer.parseInt(node.childNode(0).attr("height")));
                                    //map.put("width", Integer.parseInt(node.childNode(0).attr("width")));
                                    map.put("width",Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(node.childNode(0).attr("width"), "[^\\d^\\.]", ""))))));
                                    map.put("height",Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(node.childNode(0).attr("height"), "[^\\d^\\.]", ""))))));

                                    map.put("value",node.childNode(0).attr("src"));
                                    map.put("type","image");
                                }
                                else{
                                    try{
                                        if(node.childNode(0).childNode(0) instanceof TextNode) {
                                            map.put("type", "icon");
                                            map.put("value", ((TextNode) node.childNode(0).childNode(0)).text());
                                            map.put("direction", 2);
                                        }else{
                                            if(node.childNode(0).childNode(0).childNode(0) instanceof TextNode){
                                                map.put("type", "icon");
                                                map.put("value", ((TextNode) node.childNode(0).childNode(0).childNode(0)).text());
                                                map.put("direction", 2);
                                            }else{
                                                logger.warn("转jsonData时，sub数据有问题！");
                                                throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
                                            }
                                        }
                                    }catch (Exception e){
                                        throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
                                    }
                                }
                            }
                            //删除此文本节点，防止重复
                            node.childNode(0).remove();
                        }

                        break;
                    case "sup":
                        if(node.childNodeSize()!=0){
                            if(node.childNode(0) instanceof TextNode){
                                map.put("type","icon");
                                map.put("value",((TextNode)node.childNode(0)).text());
                                map.put("direction",1);
                            }else {
                                String nodeName= node.childNode(0).nodeName();
                                if(nodeName.equals("img")){
//                                    map.put("height",Integer.parseInt(node.childNode(0).attr("height")));
//                                    map.put("width", Integer.parseInt(node.childNode(0).attr("width")));

                                    map.put("width",Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(node.childNode(0).attr("width"), "[^\\d^\\.]", ""))))));
                                    map.put("height",Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(node.childNode(0).attr("height"), "[^\\d^\\.]", ""))))));

                                    map.put("value",node.childNode(0).attr("src"));
                                    map.put("type","image");
                                }else{
                                    try{
                                        if(node.childNode(0).childNode(0) instanceof TextNode){
                                            map.put("type","icon");
                                            map.put("value",((TextNode)node.childNode(0).childNode(0)).text());
                                            map.put("direction",1);
                                        }else{
                                            if(node.childNode(0).childNode(0).childNode(0) instanceof TextNode){
                                                map.put("type","icon");
                                                map.put("value",((TextNode)node.childNode(0).childNode(0).childNode(0)).text());
                                                map.put("direction",1);
                                            }else{
                                                logger.warn("转jsonData时，sup数据有问题！");
                                                throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
                                            }
                                        }
                                    }catch (Exception e){
                                        throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
                                    }
                                }
                            }
                            //删除此文本节点，防止重复
                            node.childNode(0).remove();
                        }

                        break;
                    case "strong":
                        styleSum += 1;
                        break;
                    case "em":
                        styleSum += 2;
                        break;
                    case "input":
                        if ("questions-blank".equals(cls)) {
                            map.put("type", "blank");
                            map.put("value", value);
                            map.put("union", 1);
                        }
                        break;
                    case "img":
                        if (StringUtils.hasText(latex)) {
                            map.put("type", "formula");
                            map.put("latex", latex);
                        } else {
                            map.put("type", "image");
                        }
                        map.put("value", src);
                        map.put("width", width);
                        map.put("height", height);

                        break;

                    case "table":

                        /**
                         * 获取带有表格的html
                         */
                        Document tableDoc = Jsoup.parse(node.toString());

                        map.put("type","table");
                        map.put("width",width);
                        map.put("rows",rows);
                        map.put("columns",columns);

                        List<Map> tableMap = new ArrayList<>();//表格的数据

                        //获取表格的tr数据
                        Elements trs = tableDoc.select("tr");

                        for(int x=0;x<trs.size();x++){
                            Element tr = trs.get(x);
                            Elements tds = tr.select("td");

                            for (int y=0;y<tds.size();y++){

                                Element td = tds.get(y);

                                //获取td的属性 width 值
                                String width1 = td.attributes().get("width");
                                //width = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(width1, "[^0-9]", ""));
                                width = Integer.parseInt(String.format("%.0f",Math.ceil(Double.parseDouble(org.apache.commons.lang3.StringUtils.replacePattern(width1, "[^\\d^\\.]", "")))));
                                //td map数据
                                Map<String,Object> tdMap = new HashMap<>();

                                tdMap.put("type","td");
                                //td 的坐标
                                tdMap.put("position",""+x+","+""+y);
                                tdMap.put("width",width);

                                //每一个 td 标签的值
                                List<Map> tdValueMap = new ArrayList<>();

                                isFirst=true;

                                //获取node节点
                                parseNode(td,"",0,0,tdValueMap);

                                tdMap.put("value",tdValueMap);

                                tableMap.add(tdMap);
                            }

                        }

                        map.put("value",tableMap);


                        break;

                    default:
                        break;
                }

                if (StringUtils.hasText(style)) {
                    String[] styleList = style.split(";");
                    for (String innerLineStyle : styleList) {
                        switch (innerLineStyle.trim()) {
                            case "u":
                                break;
                            case "s":
                                break;
                            case "text-decoration:underline":
                                styleSum += 4;
                                break;
                            case "text-decoration:line-through":
                                styleSum += 8;
                                break;
                            case "border-bottom:dotted 3px":
                                styleSum +=16;
                                break;
                            case "border:1px solid #000":
                                styleSum += 32;
                                break;
                            case "text-indent:8px":
                                styleSum += 64;
                                break;
                            // js版的判断是带空格的！！所以再写一遍好了
                            case "text-decoration: underline":
                                styleSum += 4;
                                break;
                            case "text-decoration: line-through":
                                styleSum += 8;
                                break;
                            case "border: 1px solid #000":
                                styleSum += 32;
                                break;
                            case "text-indent: 8px":
                                styleSum += 64;
                                break;
                            case "latex":
                                break;

                            default:
                                break;
                        }
                    }
                }
            }
        }


        if (!map.isEmpty()) {
            if (node.childNodes().size() == 0 && styleSum > 0) {
                map.put("style", styleSum);
            }
            mapList.add(map);
        }



        if (node.childNodes().size() > 0 && !node.nodeName().equals("tr")) {
            for (Node data : node.childNodes()) {
                parseNode(data, entryKey, optionIndex, styleSum, mapList);
            }

        }
    }



    /**
     * 单引号替换
     * 将 =" 替换成单引号
     */
    public synchronized static String replaceMark(String htmlJson){

        StringBuilder stringBuilder = new StringBuilder();
        if(null!=htmlJson && !"".equals(htmlJson)){
            stringBuilder.append(htmlJson);
            Pattern p = Pattern.compile("=\"");
            Matcher m = p.matcher(htmlJson);

            while (m.find()) {
                int start = m.start();
                int end = m.end();
                stringBuilder.replace(start,end,"='");

                int markStart = htmlJson.indexOf("\"",end);
                stringBuilder = stringBuilder.replace(markStart,markStart+1,"'");
            }
        }

        return stringBuilder.toString();
    }

    public synchronized static boolean exist(String id) {
        boolean suc = false;
        List<String> all = new ArrayList<>();
        all.add("36");
        all.add("2");
        all.add("34");
        all.add("25");
        all.add("27");
        all.add("47");
        all.add("46");
        all.add("45");
        all.add("29");
        all.add("60");
        for (String str : all) {
            if (str.equals(id)) {
                suc = true;
                break;
            }
        }
        return suc;
    }
}
