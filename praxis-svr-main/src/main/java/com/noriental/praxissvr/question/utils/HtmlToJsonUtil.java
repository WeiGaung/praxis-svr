package com.noriental.praxissvr.question.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noriental.utils.text.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * Created by luozukai on 2016/11/29.
 * html转换成pad需要的json格式
 */
public class HtmlToJsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(HtmlToJsonUtil.class);
    // json转换器
    public static ObjectMapper mapper = new ObjectMapper();

    /**
     * 选项属性
     **/
    private static boolean isFirst = true;    // 标志第一个p标签不加new line
    //private static String[] abc = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};//选项
    private static String[] abc = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};

    /**
     * html转换成pad需要的json格式
     *
     * @param htmlJson htmljson
     * @return
     * @throws IOException
     */
    public static String htmlToJson(String questionType, String htmlJson, long questionId) throws IOException {
        // 替换双引号
        htmlJson = JsonUtils.replaceMark(htmlJson);
        logger.info("替换单引号后的：{}", htmlJson);

        // 开始解析
        Map<String, Object> resultMap = readQuestion(questionType, htmlJson, false, null);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("question_id", questionId);
        wrapper.put("content", resultMap);
        String json = mapper.writeValueAsString(wrapper);
        logger.info("返回的json:{}", json);
        return json;
    }

    /**
     * 递归遍历题目及小题
     *
     * @param htmlJson         html
     * @param isChildQuestion  是否当前是子题
     * @param childQuestionMap 子题htmlMap
     * @return
     * @throws IOException
     */
    public static Map<String, Object> readQuestion(String questionType, String htmlJson, boolean isChildQuestion, Map childQuestionMap) throws IOException {
        Map<String, Object> resultMap = new LinkedHashMap<>();

        // html标签map
        Map<String, Object> htmlMap;
        if (isChildQuestion) {
            htmlMap = childQuestionMap;
        } else {
            htmlMap = mapper.readValue(htmlJson, Map.class);
        }

        /**遍历外层html标签**/
        for (Map.Entry<String, Object> entry : htmlMap.entrySet()) {
            // isfirst重置
            isFirst = true;

            /**判断是html内容还是纯文本内容**/
            if (entry.getValue() == null)
                continue;
            String html = entry.getValue().toString();

            String attr = entry.getKey();
            if (html.contains("<") && html.contains(">")) {  // 带html内容
                Document document = Jsoup.parse(html);
                Node bodyNode = document.childNodes().get(0).childNodes().get(1);

                // 如果是填空题的answer,为数据 map{index、group}的格式，(老坑)
                if (exist(questionType) && "answer".equals(attr)) {
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
                        bodyNode = optionDoc.childNodes().get(0).childNodes().get(1);

                        List<Map> groupList = new ArrayList<>();
                        readNode(bodyNode, attr, i, 0, groupList);
                        answer.put("group", groupList);
                        answerList.add(answer);
                    }
                    resultMap.put(attr, answerList);
                } else if ("options".equals(attr)) {  // 如果是选择题的options,为 数组 数组 map的格式
                    List<List<Map>> optionsResultList = new ArrayList<>();

                    // 选项数组，逐个解析
                    List array = (List) entry.getValue();
                    for (int i = 0; i < array.size(); i++) {
                        // isfirst重置
                        isFirst = true;
                        // 一个option作为一个单独的Document解析
                        Document optionDoc = Jsoup.parse((String) array.get(i));
                        bodyNode = optionDoc.childNodes().get(0).childNodes().get(1);

                        List<Map> optionMapList = new ArrayList<>();
                        readNode(bodyNode, attr, i, 0, optionMapList);
                        optionsResultList.add(optionMapList);
                    }
                    resultMap.put(attr, optionsResultList);
                } else if ("questions".equals(attr)) {
                    // 如果是复合题，再次递归当前方法，继续解析小题
                    List<Map> qtHtmlList = (List) entry.getValue();
                    List<Map> qtMapList = new ArrayList<>();
                    if (qtHtmlList.size() > 0) {
                        for (Map childQuestion : qtHtmlList) {
                            // 获取小题题型
                            Map childQuestionType = (Map) childQuestion.get("type");
                            qtMapList.add(readQuestion(childQuestionType.get("id").toString(), null, true, childQuestion));
                        }
                    }
                    resultMap.put("questions", qtMapList);
                } else {
                    // 其他标签
                    List<Map> mapList = new ArrayList<>();
                    readNode(bodyNode, attr, 0, 0, mapList);
                    resultMap.put(attr, mapList);
                }
            } else { // 纯文本
                logger.info("curr_key:{}", attr);
                //判断题在pad的使用的字符串
                if ("3".equals(questionType) && "answer".equals(attr)) {
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        List array = (List) entry.getValue();
                        if (CollectionUtils.isNotEmpty(array)) {
                            resultMap.put(attr, array.get(0));
                        } else {
                            logger.error("判断题结构答案不合法, 答案为空");
                            resultMap.put(attr, "");
                        }
                    } else {
                        resultMap.put(attr, value);
                    }
                } else {
                    resultMap.put(attr, htmlMap.get(attr));
                }

            }
        }

        return resultMap;
    }

    public static boolean exist(String id) {
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
        for (String str : all) {
            if (str.equals(id)) {
                suc = true;
                break;
            }
        }
        return suc;
    }

    /**
     * 递归遍历html节点
     *
     * @param node        当前html节点
     * @param currentKey  当前解析的html标签
     * @param optionIndex 当前选项下标索引
     * @param styleSum    样式累加
     * @param mapList     返回结果
     */
    public static void readNode(Node node, String currentKey, int optionIndex, int styleSum, List<Map> mapList) {
        // 解析node
        Map object = new HashMap();
        if (!"body".equals(node.nodeName())) {
            if (node instanceof TextNode) { // 纯文本
                object.put("type", "text");
                object.put("value", ((TextNode) node).text());
            } else {  // html标签
                // html元素上的Attribute
                String src = "";
                int width = 0;
                int height = 0;
                String cls = "";
                String value = "";
                String latex = "";
                String style = "";

                // 当解析选择题选项时，加一个类型为option,只加一次所以加上isFirst的判断
                // （去掉了原来业务的题型判断，即全部选项的封装都一样格式）
                if (isFirst && "options".equals(currentKey)) {
                    object.put("type", "option");
                    object.put("value", abc[optionIndex++]);
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
                        width = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
                    }
                    if ("height".equals(attribute.getKey())) {
                        height = Integer.parseInt(org.apache.commons.lang3.StringUtils.replacePattern(attribute.getValue(), "[^0-9]", ""));
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
                }

                // html标签
                switch (node.nodeName()) {

                    case "p":
                        if (isFirst) { // 第一个p标签不需要new line
                            isFirst = false;
                            break;
                        }
                        object.put("type", "newline");
                        object.put("value", "1");
                        break;
                    //碰到br标签新加一行
//                    case "br":
//                        object.put("type", "newline");
//                        object.put("value", "1");
//                        break;
                    case "strong":
                        styleSum += 1;
                        break;
                    case "em":
                        styleSum += 2;
                        break;
                    case "input":
                        if ("questions-blank".equals(cls)) {
                            object.put("type", "blank");
                            object.put("value", value);
                            object.put("union", 1);
                        }
                        break;
                    case "img":
                        if (StringUtils.hasText(latex)) {
                            object.put("type", "formula");
                            object.put("latex", latex);
                        } else {
                            object.put("type", "image");
                        }
                        object.put("value", src);
                        object.put("width", width);
                        object.put("height", height);

                        break;
                    default:
                        break;
                }

                // style行内样式
                // (其实是为了把行内style统一转换成一层style)
                if (StringUtils.hasText(style)) {
                    String[] styleList = style.split(";");
                    for (String innerLineStyle : styleList) {
                        switch (innerLineStyle) {
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

        // 没有子元素，给当前元素赋上前面累加的style
        // (style为非引用传值，不需清0!)
        if (!object.isEmpty()) {
            if (node.childNodes().size() == 0 && styleSum > 0) {
                object.put("style", styleSum);
            }
            mapList.add(object);
        }

        // 如果有子元素，递归继续解析
        if (node.childNodes().size() > 0) {
            for (Node data : node.childNodes()) {
                readNode(data, currentKey, optionIndex, styleSum, mapList);
            }
        }
    }

//    private static String replace

    public static void main(String[] args) throws Exception {
        // 选择题
//        String html = "{\"body\":\"<p><em>地球半径有多少米</em></p><p><span style=\"text-decoration:underline;\"><strong>月亮周长多少米</strong></span></p><p><span style=\"text-decoration:underline;\"><strong><img src=\"http://rc.okjiaoyu.cn/o_1b2nim9k78qb1aplktt1bjpo2h10.png\" width=\"190\" height=\"78\"/></strong></span></p><p><strong><br/></strong></p>\",\"analysis\":\"<p>分析分析分析分析分析分析</p>\",\"questions\":[],\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"options\":[\"<p><span style=\"text-decoration:underline;\"><strong><em>选项a<img src=\"http://rc.okjiaoyu.cn/o_1b2qg7mptu4u1pee1a9ermk18pn10.png\" width=\"190\" height=\"78\"/><br/></em></strong></span></p><p><span style=\"text-decoration:underline;\"><strong><em><img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KwUaDxX5FC.png\" data-latex=\"fd=x*7\" width=\"140\" height=\"43\"/></em></strong></span><br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\",\"<p>选项d</p>\"]}";
//        new HtmlToJsonUtil().htmlToJson(html);

        //  简答题
        //String html = "{\"body\":\"<p><span style=\"text-decoration:underline;\"><strong><em>冯绍峰的双丰收？</em></strong></span></p>\",\"answer\":\"<p><span style=\"text-decoration:line-through;\"><em><strong>答案答案答案<img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_Kys8R7eNsk.png\" data-latex=\"x=y*w\" width=\"131\" height=\"43\"/></strong></em></span><br/></p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"analysis\":\"<p>fsdfds</p>\",\"questions\":[]}";
        //new HtmlToJsonUtil().htmlToJson(html);

        // 听力复合题
//        String html2 = "{\"translation\":\"<p><strong>fdsfsdif ruoiwerewonfs fsdnkfslfjds fdsjfkdjsfjs</strong></p><p><strong>fdsfdsjfkjflsdfjs f,fjdsfkdsjfkds,.fds sfdsfsdfs</strong><br/></p>\",\"material\":\"<p><span style=\"text-decoration: line-through;\"><strong><em>这是听力题<img src=\"http://rc.okjiaoyu.cn/o_1b2t1h28s8l3115l1o33gds1l9p1f.png\" width=\"190\" height=\"78\"/></em></strong></span><span style=\"text-decoration: none;\">，这也是听力？</span><br/></p>\",\"questions\":[{\"body\":\"<p><span style=\"text-decoration: underline;\"><em><strong>这是选择题<img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KyxUNX8obC.png\" data-latex=\"x=y*34234\" width=\"173\" height=\"43\"/></strong></em></span><span style=\"text-decoration: none;\">哈哈哈</span><br/></p>\",\"analysis\":\"<p><em><strong>解析解析解析</strong></em><br/></p>\",\"difficulty\":\"2\",\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>选项a<br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\",\"<p>选项d<br/></p>\"]},{\"body\":\"<p><em><strong>这是简答题</strong></em><br/></p>\",\"analysis\":\"<p><em><strong>解析解析解析</strong></em><br/></p>\",\"difficulty\":\"2\",\"answer\":\"<p><strong>答案答案答案</strong><br/></p>\",\"type\":{\"id\":4,\"name\":\"简答题\"},\"options\":[]}],\"audio\":{\"url\":\"http://ra.okjiaoyu.cn/ra_KyxKssxEpq.mp3\",\"name\":\"See You Again - Wiz Khalifa,Charlie Puth.mp3\",\"size\":\"3.8 M\"}}";
//        new HtmlToJsonUtil().htmlToJson(html2);

        // 选择完形填空
//        String html3 = "{\"translation\":\"<p><img src=\"http://rc.okjiaoyu.cn/o_1b2umkb11l9p1fit10pm1b61eia17.png\" width=\"190\" height=\"78\"/><br/></p><p>jfkskkwfw fdsjklfhshewjf.fsdfisfjwj&nbsp; fjsdkfksdfkds</p>fdsfjldksjfksdjfsdfje&nbsp; iewrjuiewriewjr\",\"material\":\"<p><span style=\"text-decoration: underline;\"><strong><em>这是选择完形填空题这是选择完形填空题<br/></em></strong></span></p><p><span style=\"text-decoration: underline;\"><strong><em><img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzBMfUBL9u.png\" data-latex=\"\\frac {dy} {dx}\" width=\"70\" height=\"54\"/><img src=\"http://rc.okjiaoyu.cn/o_1b2umefjf16601psfp2vtr6jp412.png\" width=\"190\" height=\"78\"/><br/></em></strong></span></p><p><span style=\"text-decoration: none;\">这里是正常字体，这里是正常字体</span><span style=\"text-decoration: underline;\"><strong><em><br/></em></strong></span></p><p><span style=\"text-decoration: underline;\"><strong><em><img src=\"http://rc.okjiaoyu.cn/o_1b2ump7md6uh1lku1smv19921nq72k.png\" width=\"190\" height=\"78\"/></em></strong></span></p>\",\"questions\":[{\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"difficulty\":\"1\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p><em><strong>选项a<img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzC3TJRuLu.png\" data-latex=\"x=ii*3243\" width=\"164\" height=\"43\"/></strong></em><br/></p>\",\"<p><img src=\"http://rc.okjiaoyu.cn/o_1b2ummia31kel1oc8btqpfkubo25.png\" width=\"190\" height=\"78\"/></p>\",\"<p>选项c</p>\",\"<p>选项d</p>\"],\"analysis\":\"<p>解析解析解析解析解析解析</p><p><img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzC7oNk1wc.png\" data-latex=\"x=y+1\" width=\"129\" height=\"43\"/><br/></p><p><img src=\"http://rc.okjiaoyu.cn/o_1b2umnsdm1pnq4111jdi9ehb5h2a.png\" width=\"190\" height=\"78\"/></p>\"},{\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"difficulty\":\"4\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>选项a<br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\",\"<p>选项d</p>\"],\"analysis\":\"<p>解析解析<br/></p>\"}],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"}}";
//        new HtmlToJsonUtil().htmlToJson(html3);

        // 文言文阅读复合题
        String html4 = "{\"material\":\"<p>文言文阅读题<br/></p>\",\"questions\":[{\"body\":\"<p><strong><em>这是填空题<input readonly=\"true\" class=\"questions-blank\" value=\"1\" type=\"text\" contenteditable=\"true\"/>&nbsp;</em></strong><br/></p>\",\"analysis\":\"<p><span style=\"text-decoration: line-through;\"><strong>解析解析解析解析解析解析</strong></span></p>\",\"difficulty\":\"1\",\"answer\":[\"<p><span style=\"text-decoration: underline;\"><strong><em>答案1<img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzPFxQF51m.png\" data-latex=\"t=3423\" width=\"127\" height=\"43\"/><br/></em></strong></span></p><p><span style=\"text-decoration: underline;\"><strong><em><img src=\"http://rc.okjiaoyu.cn/o_1b2v1k23rpa81nuc1a281grjdd2d.png\" width=\"190\" height=\"78\"/></em></strong></span><br/></p>\"],\"type\":{\"id\":2,\"name\":\"填空题\"},\"options\":[]},{\"body\":\"<p>这里是判断题</p><p><img src=\"http://rc.okjiaoyu.cn/o_1b2v1n5851mes1f4t8is1khi1cbd2s.png\" width=\"190\" height=\"78\"/></p>\",\"analysis\":\"<p><em><strong>解析解析解析</strong></em><br/></p>\",\"difficulty\":\"2\",\"answer\":\"0\",\"type\":{\"id\":3,\"name\":\"判断题\"},\"options\":[]},{\"body\":\"<p><strong>这是简答题简答题</strong></p><p><strong><img src=\"http://rc.okjiaoyu.cn/o_1b2v1ormsuf617be1a5c1uku1bm63g.png\" width=\"190\" height=\"78\"/></strong><br/></p>\",\"analysis\":\"<p><strong>分析分析</strong><br/></p>\",\"difficulty\":\"2\",\"answer\":\"<p><strong>答案答案</strong><br/></p>\",\"type\":{\"id\":4,\"name\":\"简答题\"},\"options\":[]},{\"body\":\"<p>选择题选择题</p>\",\"analysis\":\"<p>解析<br/></p>\",\"difficulty\":\"2\",\"answer\":[\"A\"],\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>选项a<br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\",\"<p>选项d<br/></p>\"]}],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"}}";
        new HtmlToJsonUtil().htmlToJson("7", html4, 8);

    }


}
