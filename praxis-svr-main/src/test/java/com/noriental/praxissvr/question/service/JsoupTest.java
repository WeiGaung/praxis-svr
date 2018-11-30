package com.noriental.praxissvr.question.service;


import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.question.bean.SuperQuestionSsdb;
import com.noriental.praxissvr.question.bean.html.ComplexQuestion;
import com.noriental.praxissvr.question.bean.html.NewMap;
import com.noriental.praxissvr.question.request.FindMyQuestionRequest;
import com.noriental.praxissvr.question.request.QuestionContinuedRequest;
import com.noriental.praxissvr.question.response.FindQuestionsResponse;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.utils.json.JsonUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenlihua
 * @date 2015/12/24
 * @time 9:50
 */
public class JsoupTest {

    private static final Logger logger = LoggerFactory.getLogger(JsoupTest.class);



    @Test
    public void test001(){

        String html="{\"content\": {\"body\": \"<p>&#x94DC;&#x950C;&#x5408;&#x91D1;&#x5236;&#x6210;&#x7684;&#x5047;&#x91D1;&#x5143;&#x5B9D;&#x6B3A;&#x9A97;&#x884C;&#x4EBA;&#x7684;&#x4E8B;&#x4EF6;&#x5C61;&#x6709;&#x53D1;&#x751F;&#xFF0C;&#x4E0B;&#x5217;&#x4E0D;&#x6613;&#x533A;&#x522B;&#x5176;&#x771F;&#x4F2A;&#x7684;&#x65B9;&#x6CD5;&#x662F;&#xFF08;    &#xFF09;<br></p>\", \"answer\": [\"D\"], \"options\": [\"<p>&#x6D4B;&#x5B9A;&#x5BC6;&#x5EA6;<br></p>\", \"<p>&#x653E;&#x5165;&#x7A00;&#x786B;&#x9178;&#x4E2D;<br></p>\", \"<p>&#x653E;&#x5165;&#x76D0;&#x9178;&#x4E2D;<br></p>\", \"<p>&#x89C2;&#x5BDF;&#x5916;&#x89C2;<br></p>\"], \"analysis\": \"<p>&#x8003;&#x70B9;&#xFF1A;&#x91D1;&#x5C5E;&#x4E0E;&#x5408;&#x91D1;&#x5728;&#x6027;&#x80FD;&#x4E0A;&#x7684;&#x4E3B;&#x8981;&#x5DEE;&#x5F02;&#xFF0E;<br>&#x4E13;&#x9898;&#xFF1A;&#x91D1;&#x5C5E;&#x6982;&#x8BBA;&#x4E0E;&#x78B1;&#x5143;&#x7D20;&#xFF0E;<br>&#x5206;&#x6790;&#xFF1A;&#x5229;&#x7528;&#x7269;&#x8D28;&#x6027;&#x8D28;&#x4E0A;&#x7684;&#x5DEE;&#x5F02;&#x53EF;&#x4EE5;&#x5F88;&#x65B9;&#x4FBF;&#x5730;&#x533A;&#x522B;&#x5B83;&#x4EEC;&#xFF0E;<br>&#x89E3;&#x7B54;&#xFF1A;&#x89E3;&#xFF1A;A&#xFF0E;&#x6D4B;&#x5B9A;&#x5BC6;&#x5EA6;&#xFF0C;&#x94DC;&#x950C;&#x5408;&#x91D1;&#x5BC6;&#x5EA6;&#x5C0F;&#x4E8E;&#x9EC4;&#x91D1;&#xFF0C;&#x6D4B;&#x5B9A;&#x5BC6;&#x5EA6;&#x65F6;&#x4F1A;&#x53D1;&#x73B0;&#x4E8C;&#x8005;&#x5BC6;&#x5EA6;&#x4E0D;&#x540C;&#xFF0C;&#x5BC6;&#x5EA6;&#x5C0F;&#x8005;&#x4E3A;&#x5047;&#xFF0C;&#x6545;A&#x6B63;&#x786E;&#xFF1B;<br>B&#xFF0E;&#x653E;&#x5165;&#x7A00;&#x786B;&#x9178;&#x4E2D;&#xFF0C;&#x950C;&#x4E0E;&#x7A00;&#x786B;&#x9178;&#x53CD;&#x5E94;&#x751F;&#x6210;&#x6C14;&#x4F53;&#xFF0C;&#x800C;&#x9EC4;&#x91D1;&#x4E0E;&#x7A00;&#x786B;&#x9178;&#x4E0D;&#x53CD;&#x5E94;&#xFF0C;&#x6545;B&#x6B63;&#x786E;&#xFF1B;<br>C&#xFF0E;&#x653E;&#x5165;&#x76D0;&#x9178;&#x4E2D;&#xFF0C;&#x950C;&#x4E0E;&#x76D0;&#x9178;&#x53CD;&#x5E94;&#x751F;&#x6210;&#x6C22;&#x6C14;&#xFF0C;&#x800C;&#x9EC4;&#x91D1;&#x4E0E;&#x76D0;&#x9178;&#x4E0D;&#x53CD;&#x5E94;&#xFF0C;&#x6545;C&#x6B63;&#x786E;&#xFF1B;<br>D&#xFF0E;&#x89C2;&#x5BDF;&#x5916;&#x89C2;&#xFF0C;&#x7531;&#x4E8E;&#x4E8C;&#x8005;&#x7684;&#x989C;&#x8272;&#x57FA;&#x672C;&#x4E00;&#x81F4;&#xFF0C;&#x6240;&#x4EE5;&#x96BE;&#x4E8E;&#x6BD4;&#x8F83;&#x4E3A;&#x771F;&#x5047;&#xFF0C;&#x6545;D&#x9519;&#x8BEF;&#xFF1B;<br>&#x6545;&#x9009;D&#xFF0E;<br>&#x70B9;&#x8BC4;&#xFF1A;&#x672C;&#x9898;&#x8003;&#x67E5;&#x4E86;&#x771F;&#x5047;&#x9EC4;&#x91D1;&#x7684;&#x9274;&#x522B;&#xFF0C;&#x9898;&#x76EE;&#x96BE;&#x5EA6;&#x4E0D;&#x5927;&#xFF0C;&#x53EF;&#x4EE5;&#x4F9D;&#x636E;&#x91D1;&#x5C5E;&#x7684;&#x6027;&#x8D28;&#x8FDB;&#x884C;&#xFF0E;<br></p>\"}, \"questionId\": 572}";
        String html2="{\"content\": {\"map\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"body\": \"<p>美国独立战争爆发的原因是什么？（2分）1776年，哪一文件的发表宣告了美利坚合众国的诞生？（1分）美国独立战争的胜利有何意义？（3分）</p>\", \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"answer\": [\"<p>英国希望北美殖民地永远作为它的原料产地和商品市场，竭力压制北美经济的发展。殖民地人民强烈不满，反抗情绪日益高涨。（2分）《独立宣言》的发表。（1分）美国独立战争结束了英国的殖民统治，实现了国家的独立。（1分）确立了比较民主的资产阶级政治体制，有利于美国资本主义的发展。（1分）对以后欧洲和拉丁美洲的革命也起了推动作用。（1分）</p>\"], \"analysis\": \"<p>本题考查美国独立战争。美国独立战争爆发的原因结合教材从政治、经济等方面作答即可；结合教材知识可知，1776年《独立宣言》的发表标志着美利坚合众国的诞生；美国独立战争胜利的意义，从美国自身和其对世界其他殖民地国家的影响两方面作答。</p>\", \"questions\": []}, \"question_id\": 4169314}";
        html = StringEscapeUtils.unescapeHtml3(html2);
        logger.info("========={}",html);
    }


    @Test
    public void test01() throws Exception {
        String html = "111111<p><em>地球半径有多少米</em></p><p><span style=\"text-decoration: underline;\"><strong>月亮周长多少米</strong></span></p><p><span style=\"text-decoration: underline;\"><strong><img src=\"http: //rc.okjiaoyu.cn/o_1b2nim9k78qb1aplktt1bjpo2h10.png\" width=\"190\" height=\"78\"/></strong></span></p><p><strong><br/></strong></p>";
        Document document = Jsoup.parse(html);
        Node bodyNode = document.childNodes().get(0).childNodes().get(1);
        readNode(bodyNode);

//        ObjectMapper mapper2 = new ObjectMapper();
//        StringWriter sw = new StringWriter();
//        JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
//        mapper2.writeValue(gen,list);
//        gen.close();
//        String json = sw.toString();
//        System.out.println(json);
        
    }


    public static void readNode(Node node) throws Exception {
        // html标签
        System.out.println(node.nodeName());
        Attributes attr = node.attributes();


        List<Node> childNodes = node.childNodes();
        if (childNodes.size() > 0) {
            for (Node data : childNodes) {
                readNode(data);
            }
        }

    }


    @Test
    public void test03() throws JSONException {

        String html = "{\"body\": \"<p>口语文章朗读测试</p>\", \"prompt\": \"are you ok h h h h  h h h  h h h h  h h h\", \"analysis\": \"<p>口语文章朗读测试</p>\", \"questions\": []}";
        JSONObject json = new JSONObject(html);
        String prompt = json.getString("prompt");
        //logger.info("\n========={}",prompt);

        /*
            拼装third_party_use 字段
         */
        json.put("third_party_use",prompt);

        /*
            拼装 answer字段
            格式为List<Object>
         */
        List<Object> answer = new ArrayList<>();
        Map<String,Object> answer_audio = new HashMap<>();
        if(JsonUtils.is_key(html,"audio")){
            Map audio = (Map) json.get("audio");
            answer_audio.putAll(audio);
            answer.add(answer_audio);
        }
        String content = "<p>"+prompt+"</p>";
        Map<String,Object> answer_content = new HashMap<>();
        answer_content.put("answer_content",content);
        answer.add(answer_content);
        json.put("answer",answer);
        logger.info("\n========={}",json);
    }

    @Test
    public void testJson() throws IOException {

        NewMap map = new NewMap();
        map.setUrl("http://www.123.com/6699.jpg");
        map.setName("你好");
        map.setSize("110012");
        map.setType("PIC");
        String json = JSON.json(map);
        logger.info("\n========={}",json);
    }


    @Test
    public void testJsonDataConverSuperQuestion() throws ParseException, IOException {

        String json="{\"content\": {\"tag\": \"\", \"body\": [{\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"吸烟和被动吸烟都有害健康，因为烟雾中含有十几种致癌和有害物质．在空气不流通的房间里，只要有一人吸烟，一会儿房间里就会烟雾缭绕充满烟味，这表明（  ）\"}, {\"type\": \"newline\", \"value\": \"1\"}], \"answer\": [\"B\"], \"options\": [[{\"type\": \"option\", \"value\": \"A\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"分子的体积发生了变化\"}, {\"type\": \"newline\", \"value\": \"1\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"分子在不停地运动\"}, {\"type\": \"newline\", \"value\": \"1\"}], [{\"type\": \"option\", \"value\": \"C\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"分子之间有引力\"}, {\"type\": \"newline\", \"value\": \"1\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"分子之间有斥力\"}, {\"type\": \"newline\", \"value\": \"1\"}]], \"analysis\": [{\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"考点：扩散现象．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"专题：应用题．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"分析：一切物质的分子都在不停地做无规则运动．扩散现象的实质就是分子运动的结果．在空气不流通的房间里，烟味不会及时扩散到大气中．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"解答：解：在空气不流通的房间里，只要有一人吸烟，一会儿整个房就会充满烟味，这是因为烟分子不停的做无规则的运动，通过扩散到达整个房间的缘故，这是扩散现象．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"故选\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 2, \"value\": \"B\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \"点评：本题考查了分子运动论的知识，分子动理论的内容是：物质是由大量分子组成的；分子永不停息地做无规则的运动；分子间存在相互作用的引力与斥力．\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"font\": \"\", \"size\": \"\", \"type\": \"text\", \"align\": 0, \"style\": 0, \"value\": \" \"}, {\"type\": \"newline\", \"value\": \"1\"}]}, \"questionId\": 1256304}";

        String html="{\"content\": {\"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": [{\"type\": \"text\", \"value\": \"It’s the place where smart people make smart machines work even smarter. It’s also in the heart of sunny California, a great place to start a family and raise kids. What could be better?\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"But something is happening to their children. Up until the age of two they develop normally. But then everything seems to go backwards. The children become locked into their own small world, unable to communicate at all.\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"They call it the “curse(诅咒)of Silicon Valley,” but the medical name for the condi\u001Ftion is autism(自闭症). It used to be thought that autism was a kind of mental illness. Now doctors are sure that it is a neurological disease transmitted genetically. It seems that the people leading the communications revolution are having children who cannot commu\u001Fnicate at all.\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"But even the parents have trouble communicating. Asperger’s Syndrome is a mild version of autism. People who have it are highly intelligent and often brilliant with num\u001Fbers or system but have no social skill. This very combination of symptoms makes Asperger’s sufferers into ideal computer professionals.\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"The Asperger’s sufferer has always been a well—known figure in popular culture. He or she was the eccentric but dedicated scholar or the strange uncle or auntie who never married. But the high numbers of such people in Silicon Valley mean that they can meet others who understand them and share their interests. And while they might not be per\u001Fsonally attractive, they can earn truly attractive amounts of money. They can get married and have kids. Unfortunately, many of the children of two Asperger’s parents seem to be developing serious autism.\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"There is little anyone can do. It takes hours of work just to make autistic child realize that anyone else exists. And there is no cure in sight. Some argue that no cure should be found. “It may be that autistics are essentially different from normal people, but that these differences make them invaluable for the evolution of the human race,” says Dr. Kirk Whilhelmsen of the University of California. “To eliminate the genes for autism could be disastrous. ”\"}, {\"type\": \"newline\", \"value\": \"1\"}, {\"type\": \"text\", \"value\": \"It seems that the children of Silicon Valley are paying the price of genius.\"}], \"questions\": [{\"body\": [{\"type\": \"text\", \"value\": \"What does Dr Kirk Whilhelmsen think of autism?\"}], \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"B\"], \"options\": [[{\"type\": \"option\", \"value\": \"A\"}, {\"type\": \"text\", \"value\": \"It is disastrous to society.\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"type\": \"text\", \"value\": \"It is not completely a bad thing.\"}], [{\"type\": \"option\", \"value\": \"C\"}, {\"type\": \"text\", \"value\": \"It is a punishment to those working in Silicon Valley.\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": \"text\", \"value\": \"People with autism should never marry.\"}]], \"analysis\": [{\"type\": \"text\", \"value\": \"无\"}], \"difficulty\": \"2\"}, {\"body\": [{\"type\": \"text\", \"value\": \"What can we learn about autism according to the passage?\"}], \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"C\"], \"options\": [[{\"type\": \"option\", \"value\": \"A\"}, {\"type\": \"text\", \"value\": \"It is believed to be a king of mental illness that can be cured.\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"type\": \"text\", \"value\": \"People with autism can’t find people sharing their interests.\"}], [{\"type\": \"option\", \"value\": \"C\"}, {\"type\": \"text\", \"value\": \"They do not care about the presence of others.\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": \"text\", \"value\": \"They are a burden for the society.\"}]], \"analysis\": [{\"type\": \"text\", \"value\": \"无\"}], \"difficulty\": \"2\"}, {\"body\": [{\"type\": \"text\", \"value\": \"Why do people call autism “curse of Silicon Valley”？\"}], \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"B\"], \"options\": [[{\"type\": \"option\", \"value\": \"A\"}, {\"type\": \"text\", \"value\": \"Because autistic people live in Silicon Valley.\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"type\": \"text\", \"value\": \"Because many people working in Silicon Valley have autism children.\"}], [{\"type\": \"option\", \"value\": \"C\"}, {\"type\": \"text\", \"value\": \"Because people with autism will be driven out of Silicon Valley.\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": \"text\", \"value\": \"Because people with autism are not personally attractive and not liked by others.\"}]], \"analysis\": [{\"type\": \"text\", \"value\": \"无\"}], \"difficulty\": \"2\"}, {\"body\": [{\"type\": \"text\", \"value\": \"What can we know about Asperger’s Syndrome according to the passage?\"}], \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"A\"], \"options\": [[{\"type\": \"option\", \"value\": \"A\"}, {\"type\": \"text\", \"value\": \"Asperger’s sufferers are ideal computer professionals.\"}], [{\"type\": \"option\", \"value\": \"B\"}, {\"type\": \"text\", \"value\": \"Asperger’s sufferers never get married and have children.\"}], [{\"type\": \"option\", \"value\": \"C\"}, {\"type\": \"text\", \"value\": \"Asperger’s sufferers are ashamed of themselves and locked into their own world.\"}], [{\"type\": \"option\", \"value\": \"D\"}, {\"type\": \"text\", \"value\": \"Asperger’s sufferers can be beneficial to society if they are cured.\"}]], \"analysis\": [{\"type\": \"text\", \"value\": \"无\"}], \"difficulty\": \"2\"}]}, \"questionId\": 5876748}";

        SuperQuestionSsdb parse = JSON.parse(html, SuperQuestionSsdb.class);

        logger.info("<><>><<{}",parse);

    }

    @Test
    public void testQuestion() throws IOException {

        String html="{\"material\":\"<p>safsdfdsafsda</p>\",\"audio\":{\"name\":\"APE-xxxx.ape\",\"size\":24100896,\"url\":\"//ra.okjiaoyu.cn/ra_RukqVPijjq.ape\"},\"translation\":\"<p>dadsasDAS</p>\",\"questions\":[{\"body\":\"<p>dasdas</p>\",\"options\":[\"<p>DSAdaSD</p>\",\"<p>DASDAsd</p>\",\"<p>dasdas</p>\",\"<p>DASDSA</p>\"],\"analysis\":\"<p>dasdasDa</p>\",\"difficulty\":\"4\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"answer\":[\"A\"]}]}";
        //ComplexQuestion complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);

        String htmlLj="{\"content\": {\"map\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": \"<p>复合题题干<br/></p>\", \"questions\": [{\"body\": \"<p>复合题第一题<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\", \"type\": {\"id\": 2, \"name\": \"填空题\"}, \"answer\": [\"<p>测试<br/></p>\", \"<p>测试<br/></p>\"], \"options\": [], \"analysis\": \"<p>测试<br/></p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>复合题第二题<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\", \"type\": {\"id\": 2, \"name\": \"填空题\"}, \"answer\": [\"<p>测试<br/></p>\", \"<p>测试<br/></p>\"], \"options\": [], \"analysis\": \"\", \"difficulty\": \"1\"}]}, \"question_id\": 4003726}";
        String jsonLj="{\"map\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": \"<p>复合题题干<br/></p>\", \"questions\": [{\"body\": \"<p>复合题第一题<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\", \"type\": {\"id\": 2, \"name\": \"填空题\"},\"intelligent\":1, \"answer\": [\"<p>测试<br/></p>\", \"<p>测试<br/></p>\"], \"options\": [], \"analysis\": \"<p>测试<br/></p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>复合题第二题<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;<input readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"2\\\" type=\\\"text\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\", \"type\": {\"id\": 2, \"name\": \"填空题\"}, \"answer\": [\"<p>测试<br/></p>\", \"<p>测试<br/></p>\"], \"options\": [], \"analysis\": \"\", \"difficulty\": \"1\"}]}";
        String touchuan="{\"typeId\": 59, \"src\": 0, \"group\": 0, \"uploadSrc\": 115, \"level\": 1, \"topicIds\": [17638], \"subjectId\": 6, \"typeName\": \"看词义点击单词\", \"state\": \"ENABLED\", \"html\": \"{\"body\": \"<p>打字机;</p>\", \"map\":{\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"analysis\": \"<p>考核词汇的词义</p>\", \"answer\": [\"B\"], \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"options\": [\"<p>official</p>\", \"<p>typewriter</p>\", \"<p>schedule</p>\", \"<p>oxygen</p>\"]}\", \"uploadId\": 62951096197, \"source\": \"content_group\", \"chapterId\": 0, \"orgId\": 1, \"intelligent\": 0, \"orgType\": 4, \"new_format\": 1}";

        String zhipi180402="{\"audio\": {\"name\": \"\",\"url\": \"\",\"size\": \"\"},\"questions\": [{\"body\": \"<p>从地形图上分析可知，俄罗斯的地势特点是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;<\\/p>\",\"answer\": [\"<p>东高西低、南高北低<\\/p>\"],\"difficulty\": \"1\",\"analysis\": \"<p>无<\\/p>\",\"type\": {\"id\": 2,\"name\": \"填空题\"},\"options\": []},{\"body\":\"<p>俄罗斯河流众多，但除伏尔加河外，航运价值都不高，其原因是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;<\\/p>\",\"answer\":[\"<p>纬度较高，河流结冰期长，东部经济欠发达等<\\/p>\"],\"difficulty\": \"1\",\"analysis\": \"<p>无<\\/p>\",\"type\": {\"id\": 2,\"name\": \"填空题\"},\"options\": []},{\"body\":\"<p>由乙图分析可知，俄罗斯的工业多分布在<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;丰富的地区。<\\/p>\",\"answer\": [\"<p>矿产资源<\\/p>\"],\"difficulty\": \"1\",\"analysis\": \"<p>无<\\/p>\",\"type\": {\"id\": 2,\"name\": \"填空题\"},\"options\": []},{\"body\": \"<p>俄罗斯亚洲部分南部多山，可是西伯利亚大铁路却为什么沿南部山区修建？<\\/p>\",\"answer\":[\"<p>北部多冻土、沼泽，南部气温较高，农作物、人口、城市等主要分布在南部地区。<\\/p>\"],\"difficulty\": \"2\",\"analysis\": \"<p>无<\\/p>\",\"type\": {\"id\":4,\"name\": \"简答题\"},\"options\": []}],\"material\": \"<p><img width=\\\"506\\\" height=\\\"500\\\" src=\\\"http://rc.okjiaoyu.cn/rc_FpEGs1s0IE.png\\\"/><\\/p>\"}";
        ComplexQuestion complexQuestion1 = JsonUtils.fromJson(zhipi180402, ComplexQuestion.class);

        QuestionContinuedRequest request=JsonUtils.fromJson(touchuan, QuestionContinuedRequest.class);

        logger.info("\n=={}",request);


    }

}
