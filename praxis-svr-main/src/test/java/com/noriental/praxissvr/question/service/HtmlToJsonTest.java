package com.noriental.praxissvr.question.service;

import com.noriental.BaseTest;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.praxissvr.question.utils.HtmlToJsonUtil;
import com.noriental.praxissvr.question.utils.ParseHtmlUtil;
import com.noriental.praxissvr.question.utils.QuestionServiceUtil;
import com.noriental.validate.exception.BizLayerException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.noriental.praxissvr.exception.PraxisErrorCode.JSON_CONVERT_FAIL;


/**
 * Created by luozukai on 2016/11/21.
 */

public class HtmlToJsonTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(HtmlToJsonTest.class);
    @Resource
    private QuestionTypeDao questionTypeDao;


    /**
     * 选择题
     *
     * @throws Exception
     */
    @Test
    public void testQuestion1() throws Exception {
        //
        String html = "{\"reqId\":null,\"object\":{\"typeName\":\"选择题\",\"subjectId\":\"9\",\"typeId\":\"1\"," +
                "\"topicIds\":[\"7\"],\"chapterId\":0,\"src\":1,\"uploadId\":\"62951074424\",\"uploadSrc\":1000," +
                "\"new_format\":1,\"group\":0,\"orgId\":\"1\",\"orgType\":4,\"source\":\"CMS套卷上传\"," +
                "\"state\":\"PREVIEWED\",\"level\":1," +
                "\"html\":{\"body\":\"<p>在奥运比赛中，举重运动员举重前常用白色的“镁粉”搓手，是因为“镁粉”质轻，吸水性好，可做防滑剂。“镁粉”的有效成分是碱式碳酸镁，它不可以燃烧，300" +
                "℃即分解，其分解的化学方程式为</p><p>Mg<sub>5</sub>（OH）<sub>2</sub>（CO<sub>3</sub>）<sub>4</sub><sub><img " +
                "width=\\\"89\\\"height=\\\"66\\\"title=\\\"image2\\\" src=\\\"http: //rc.okjiaoyu" +
                ".cn/docx/docx_20170918163909793079.png\\\"alt=\\\"\\\"/></sub>5MgO+H<sub>2</sub>O+4X↑，则X的化学式是（）</p" +
                ">\",\"answer\":[\"B\"],\"options\":[\"<p>CO</p>\",\"<p>CO<sub>2</sub></p>\"," +
                "\"<p>O<sub>2</sub></p>\",\"<p>Mg（OH）<sub>2</sub></p>\"]," +
                "\"analysis\":\"<p>试题分析：根据质量守恒定律，已知：“Mg<sub>5</sub>（OH）<sub>2</sub>（CO<sub>3</sub>）<sub>4</sub><sub" +
                ">11111</sub>5MgO+X+4CO<sub>2</sub>↑”，在化学反应前后，原子的种类和数目保持不变，可推知每个X分子中含有1个碳原子、2个氧原子，化学式为CO<sub>2</sub" +
                ">。故选：B。</p><p>考点：质量守恒定律及其应用</p>\",\"questions\":[],\"audio\":{\"url\":\"\",\"name\":\"\"," +
                "\"size\":\"\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"}}}}";
        new HtmlToJsonUtil().htmlToJson("1", html, 8);
    }

    /**
     * 简答题
     *
     * @throws Exception
     */
    @Test
    public void testQuestion4() throws Exception {
        String html = "{\"body\":\"<p><span style=\"text-decoration:underline;" +
                "\"><strong><em>冯绍峰的双丰收？</em></strong></span></p>\",\"answer\":\"<p><span " +
                "style=\"text-decoration:line-through;\"><em><strong>答案答案答案<img class=\"kfformula\" src=\"http://rc" +
                ".okjiaoyu.cn/rc_Kys8R7eNsk.png\" data-latex=\"x=y*w\" width=\"131\" " +
                "height=\"43\"/></strong></em></span><br/></p>\",\"audio\":{\"url\":\"\",\"name\":\"\"," +
                "\"size\":\"\"},\"analysis\":\"<p>fsdfds</p>\",\"questions\":[]}";
        new HtmlToJsonUtil().htmlToJson("4", html, 8);
    }

    /**
     * 听力复合题
     *
     * @throws Exception
     */
    @Test
    public void testQuestion49() throws Exception {
        String html2 = "{\"translation\":\"<p><strong>fdsfsdif ruoiwerewonfs fsdnkfslfjds " +
                "fdsjfkdjsfjs</strong></p><p><strong>fdsfdsjfkjflsdfjs f,fjdsfkdsjfkds,.fds " +
                "sfdsfsdfs</strong><br/></p>\",\"material\":\"<p><span style=\"text-decoration: line-through;" +
                "\"><strong><em>这是听力题<img src=\"http://rc.okjiaoyu.cn/o_1b2t1h28s8l3115l1o33gds1l9p1f.png\" " +
                "width=\"190\" height=\"78\"/></em></strong></span><span style=\"text-decoration: none;" +
                "\">，这也是听力？</span><br/></p>\",\"questions\":[{\"body\":\"<p><span style=\"text-decoration: underline;" +
                "\"><em><strong>这是选择题<img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KyxUNX8obC.png\" " +
                "data-latex=\"x=y*34234\" width=\"173\" height=\"43\"/></strong></em></span><span " +
                "style=\"text-decoration: none;\">哈哈哈</span><br/></p>\"," +
                "\"analysis\":\"<p><em><strong>解析解析解析</strong></em><br/></p>\",\"difficulty\":\"2\"," +
                "\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"type\":{\"id\":1,\"name\":\"选择题\"}," +
                "\"options\":[\"<p>选项a<br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\",\"<p>选项d<br/></p>\"]}," +
                "{\"body\":\"<p><em><strong>这是简答题</strong></em><br/></p>\"," +
                "\"analysis\":\"<p><em><strong>解析解析解析</strong></em><br/></p>\",\"difficulty\":\"2\"," +
                "\"answer\":\"<p><strong>答案答案答案</strong><br/></p>\",\"type\":{\"id\":4,\"name\":\"简答题\"}," +
                "\"options\":[]}],\"audio\":{\"url\":\"http://ra.okjiaoyu.cn/ra_KyxKssxEpq.mp3\",\"name\":\"See You " +
                "Again - Wiz Khalifa,Charlie Puth.mp3\",\"size\":\"3.8 M\"}}";
        new HtmlToJsonUtil().htmlToJson("49", html2, 8);
    }


    /**
     * 选择完形填空
     *
     * @throws Exception
     */
    @Test
    public void testQuestion14() throws Exception {
        String html3 = "{\"translation\":\"<p><img src=\"http://rc.okjiaoyu.cn/o_1b2umkb11l9p1fit10pm1b61eia17.png\" " +
                "width=\"190\" height=\"78\"/><br/></p><p>jfkskkwfw fdsjklfhshewjf.fsdfisfjwj&nbsp; " +
                "fjsdkfksdfkds</p>fdsfjldksjfksdjfsdfje&nbsp; iewrjuiewriewjr\",\"material\":\"<p><span " +
                "style=\"text-decoration: underline;" +
                "\"><strong><em>这是选择完形填空题这是选择完形填空题<br/></em></strong></span></p><p><span style=\"text-decoration: " +
                "underline;\"><strong><em><img class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzBMfUBL9u.png\" " +
                "data-latex=\"\\frac {dy} {dx}\" width=\"70\" height=\"54\"/><img src=\"http://rc.okjiaoyu" +
                ".cn/o_1b2umefjf16601psfp2vtr6jp412.png\" width=\"190\" " +
                "height=\"78\"/><br/></em></strong></span></p><p><span style=\"text-decoration: none;" +
                "\">这里是正常字体，这里是正常字体</span><span style=\"text-decoration: underline;" +
                "\"><strong><em><br/></em></strong></span></p><p><span style=\"text-decoration: underline;" +
                "\"><strong><em><img src=\"http://rc.okjiaoyu.cn/o_1b2ump7md6uh1lku1smv19921nq72k.png\" width=\"190\"" +
                " height=\"78\"/></em></strong></span></p>\",\"questions\":[{\"answer\":[\"A\",\"B\",\"C\",\"D\"]," +
                "\"difficulty\":\"1\",\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p><em><strong>选项a<img " +
                "class=\"kfformula\" src=\"http://rc.okjiaoyu.cn/rc_KzC3TJRuLu.png\" data-latex=\"x=ii*3243\" " +
                "width=\"164\" height=\"43\"/></strong></em><br/></p>\",\"<p><img src=\"http://rc.okjiaoyu" +
                ".cn/o_1b2ummia31kel1oc8btqpfkubo25.png\" width=\"190\" height=\"78\"/></p>\",\"<p>选项c</p>\"," +
                "\"<p>选项d</p>\"],\"analysis\":\"<p>解析解析解析解析解析解析</p><p><img class=\"kfformula\" src=\"http://rc" +
                ".okjiaoyu.cn/rc_KzC7oNk1wc.png\" data-latex=\"x=y+1\" width=\"129\" height=\"43\"/><br/></p><p><img " +
                "src=\"http://rc.okjiaoyu.cn/o_1b2umnsdm1pnq4111jdi9ehb5h2a.png\" width=\"190\" " +
                "height=\"78\"/></p>\"},{\"answer\":[\"A\",\"B\",\"C\",\"D\"],\"difficulty\":\"4\"," +
                "\"type\":{\"id\":1,\"name\":\"选择题\"},\"options\":[\"<p>选项a<br/></p>\",\"<p>选项b</p>\",\"<p>选项c</p>\"," +
                "\"<p>选项d</p>\"],\"analysis\":\"<p>解析解析<br/></p>\"}],\"audio\":{\"url\":\"\",\"name\":\"\"," +
                "\"size\":\"\"}}";
        new HtmlToJsonUtil().htmlToJson("14", html3, 8);
    }


    /**
     * 填空题
     *
     * @throws Exception
     */
    @Test
    public void testQuestion2() throws Exception {
        String html3 = "{\"body\":\"<p>我的填空题<input readonly=\"true\" class=\"questions-blank\" value=\"1\" " +
                "type=\"text\" contenteditable=\"true\"/>放到方式发送到<input readonly=\"true\" class=\"questions-blank\" " +
                "value=\"2\" type=\"text\" contenteditable=\"true\"/>&nbsp;<input readonly=\"true\" " +
                "class=\"questions-blank\" value=\"3\" type=\"text\" contenteditable=\"true\"/>&nbsp;</p>\"," +
                "\"options\":null,\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"}," +
                "\"answer\":[\"<p><em><strong>答案1</strong></em><img class=\"kfformula\" src=\"http://rc.okjiaoyu" +
                ".cn/rc_LuAXs33mKI.png\" data-latex=\"x=y\" width=\"100\" height=\"43\"/></p>\",\"<p>答案2<br/></p>\"," +
                "\"<p>答案3<br/></p>\"],\"analysis\":\"<p>解析解析解析解析</p>\",\"questions\":[],\"map\":{\"url\":\"\"," +
                "\"name\":\"\",\"size\":\"\"}}";
        new HtmlToJsonUtil().htmlToJson("2", html3, 8);
    }


    @Test
    public void testParseHtmlUtil() throws IOException {

        String html = "{\"body\": \"<p>听单词, 并在听完后点击录音, 模仿朗读单词</p>\", \"audio\": {\"url\": \"\", \"name\": \"\", " +
                "\"size\": \"\"}, \"answer\": [{\"answer_audio\": {\"url\": \"http://qdimg.okjiaoyu" +
                ".cn/qdimg_3189fa6594d3fa86f9790343357fb0f7.mp3\", \"name\": \"\", \"size\": \"\"}, " +
                "\"answer_content\": \"<p>basket</p>\"}], \"prompt\": \"<p>basket</p>\", \"analysis\": " +
                "\"<p>考核词汇发音的准确性</p>\", \"third_party_use\": \"basket\"}";
        String htmls = "{\"content\":{\"body\":\"<p>文言字词</p><p>古今异义&nbsp;</p><p><span style=\"line-height:1em;" +
                "\">（1）与</span><span style=\"line-height:1em;text-decoration:underline;\">儿女</span><span " +
                "style=\"line-height:1em;\">讲论文义</span></p><p>古义:<input type=\"text\" readonly=\"true\" " +
                "class=\"questions-blank\" value=\"1\" contenteditable=\"true\"/>；今义：指儿子和女儿)</p><p><span " +
                "style=\"line-height:1em;\">（2）未若柳絮</span><span style=\"line-height:1em;text-decoration:underline;" +
                "\">因</span><span style=\"line-height:1em;\">风起</span></p><p>(古义:<input type=\"text\" " +
                "readonly=\"true\" class=\"questions-blank\" value=\"2\" contenteditable=\"true\"/>；今义:因为)</p>\"," +
                "\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"<p>文中泛指小辈，包括侄儿侄女</p>\"," +
                "\"<p>趁、乘</p>\"],\"analysis\":\"<p>暂无</p>\",\"questions\":[]},\"questionId\":4755515}";
        String htmlTrans="{\"map\": {\"url\": \"\",\"name\": \"\",\"size\": \"\"},\"audio\": {\"url\": \"http://qdimg.okjiaoyu.cn/qdimg_1a18dc5bdbf190d01a1b95fa07e94009.mp3\",\"name\": \"\",\"size\": \"\"},\"material\": \"<p>听单词，并在听完后在下列选项中选择正确的词义</p>\",\"translation\": \"<p>publish</p>\",\"questions\": [{\"body\": \"<p>请选择:</p>\",\"type\": {\"id\": 1,\"name\": \"选择题\"},\"answer\": [\"D\"],\"options\": [\"<p>指南;</p>\",\"<p>祝贺;</p>\",\"<p>类似;相像;</p>\",\"<p>业余的;</p>\"],\"analysis\": \"<p>考核词汇的词义</p>\",\"difficulty\": 1}]}";
        String htmlDoubleIndex="{\"body\": \"<p>某同学在解关于x的方程3x-1=mx+3时，把m看错了，结果解得x=4.这名同学把m看成了</p>\", \"answer\": [\"B\"], \"options\": [\"<p>-2</p>\", \"2\", \"<p><img width=\"21\" height=\"44\" src=\"http://rc.okjiaoyu.cn/rc_SAiTPvgVOM.png\"/></p>\", \"<p><img width=\"32\" height=\"40\" src=\"http://rc.okjiaoyu.cn/rc_SAiU9Xgebe.png\"/></p>\"], \"analysis\": \"<p>暂无</p>\", \"questions\": []}";
        String useStructId="{\"material\": \"<p><br/></p><p>(13分)材料一：某校团支部就 “异性同学交往”等问题对全校学生进行了调查，结果如下：</p><p><br/></p><table style=\\\"border:1px solid black; border-collapse:collapse;\\\" class=\\\"col_count_4\\\" rows=\\\"3\\\" columns=\\\"4\\\"><tbody><tr width=\\\"560\\\" class=\\\"firstRow\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"82\\\"><p>调查问题</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"208\\\"><p>渴望与异性</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"208\\\"><p><br/></p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_4\\\" width=\\\"88\\\"><p><br/></p></td></tr><tr width=\\\"560\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"82.11\\\"><p>同学交往</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"208\\\"><p>认为与异性同学交往是必要的</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"208.33\\\"><p>能够主动大方与异性同学交往</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_4\\\" width=\\\"127\\\"><p><br/></p></td></tr><tr width=\\\"560\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"82\\\"><p>调查结果</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"208\\\"><p>99.6%</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"208\\\"><p>98.7%</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_4\\\" width=\\\"85\\\"><p>18.9%</p></td></tr></tbody></table><p><br/></p><p>材料二：自从安上座机电话之后，七年级学生东宝的家里便开始热闹起来了。起初，找父母的电话比较多；但是近半年来，东宝的电话开始多起来了。细心的父母观察后发现：虽说同学打进电话大多是谈论学习问题，可一旦遇上女同学来电话，东宝的神情明显有些激动和兴奋。父母不放心，曾经偷听了几次，结果也没发现什么“特殊情况”。东宝和女同学也只是谈论学习和作<img width=\\\"3\\\" height=\\\"3\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20180514164420582162.png\\\" alt=\\\"\\\"/>业上的问题，令父母惊讶的是，东宝在和女同学交流之后，无论是性格上，还是学习上，都有了很大的改变和提高。</p>\", \"questions\": [{\"body\": \"<p>【理论探究】</p><p>材料一和材料二分别说明了什么？(4分)</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>材料一说明同学们渴望与异性同学交往，能够认识到与异性同学交往的必要性，但缺乏积极主动、健康交往的行动；材料二说明与异性同学的健康交往有利于自身的健康成长和发展。</p>\"], \"analysis\": \"\", \"difficulty\": \"1\"}, {\"body\": \"<p>【思考领悟】</p><p>同学们不能主动大方地与异性同学交往的原因可能有哪些？(<img width=\\\"3\\\" height=\\\"3\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20180514164420582162.png\\\" alt=\\\"\\\"/>3分)</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>①进入青春期后，我们开始懂得男女有别，对异性之间的交往非常敏感。②父母、老师、同学等外来因素的影响。③没有与异性同学健康、正常交往的良好氛围；等等<img width=\\\"3\\\" height=\\\"3\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20180514164420582162.png\\\" alt=\\\"\\\"/>。</p>\"], \"analysis\": \"\", \"difficulty\": \"1\"}, {\"body\": \"<p>【拓展延伸】</p><p><img width=\\\"162\\\" height=\\\"204\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20180514164420843869.png\\\" alt=\\\"\\\"/></p><p>你是否赞同异性同学的健康交往？为什么？(6分)</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>赞同。男生女生各自拥有自身的性别优势。欣赏对方的优势，有助于我们不断完善自己。因此，我们不仅要认识自己的优势，而且要发现对方的优势，相互取<img width=\\\"3\\\" height=\\\"3\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20180514164420582162.png\\\" alt=\\\"\\\"/>长补短，让自己变得更加优秀。与异性相处，有助于我们了解异性的思维方式、情感特征。与异性交往是我们成长的—个重要方面。</p>\"], \"analysis\": \"\", \"difficulty\": \"1\"}]}";

        String useStructIdBug="{\"material\": \"<p><br/></p><p>《水浒传》的经典人物形象一直备受喜爱，人们用多种艺术形式来再现他们的精神风貌。下面是中国邮政发行的《水浒传》邮票，请你认真读图，完成下面的问题。</p>\", \"questions\": [{\"body\": \"<p>请根据以下邮票画面表现的故事情节，将表格补充完整。</p><p></p><table style=\\\"border:1px solid black; border-collapse:collapse;\\\" class=\\\"col_count_2\\\" width=\\\"574.25px\\\" rows=\\\"2\\\" columns=\\\"2\\\"><tbody><tr width=\\\"574\\\" class=\\\"firstRow\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"287\\\"><p><img width=\\\"472\\\" height=\\\"305\\\" title=\\\"image14\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20171018182741100548.png\\\" alt=\\\"\\\"/>图1</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"287\\\"><p><img width=\\\"463\\\" height=\\\"329\\\" title=\\\"image15\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20171018182741598838.png\\\" alt=\\\"\\\"/></p><p>图2</p></td></tr><tr width=\\\"574\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"287\\\"><p><img width=\\\"472\\\" height=\\\"303\\\" title=\\\"image16\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20171018182741253028.png\\\" alt=\\\"\\\"/>图3</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"287\\\"><p><img width=\\\"474\\\" height=\\\"314\\\" title=\\\"image17\\\" src=\\\"http://rc.okjiaoyu.cn/docx/docx_20171018182741214893.png\\\" alt=\\\"\\\"/></p><p>图4</p></td></tr></tbody></table><p></p><p></p><table style=\\\"border:1px solid black; border-collapse:collapse;\\\" class=\\\"col_count_3\\\" width=\\\"573\\\" rows=\\\"5\\\" columns=\\\"3\\\"><tbody><tr width=\\\"573\\\" class=\\\"firstRow\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"76\\\"><p>邮票</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"254\\\"><p>故事情节</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"243\\\"><p>人物形象</p></td></tr><tr width=\\\"573\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"76\\\"><p>图1</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"254\\\"><p><input type=\\\"text\\\" readonly=\\\"true\\\" value=\\\"1\\\" class=\\\"questions-blank\\\" contenteditable=\\\"true\\\"/>（人名）风雪山神庙</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"243\\\"><p>奋起反抗</p></td></tr><tr width=\\\"573\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"76x.25px\\\"><p>图2</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"254\\\"><p>鲁智深<input type=\\\"text\\\" readonly=\\\"true\\\" value=\\\"2\\\" class=\\\"questions-blank\\\" contenteditable=\\\"true\\\"/></p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"243\\\"><p>神勇无敌</p></td></tr><tr width=\\\"573\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"76\\\"><p>图3</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"254\\\"><p>花荣梁山射雁</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"243\\\"><p><input type=\\\"text\\\" readonly=\\\"true\\\" value=\\\"3\\\" class=\\\"questions-blank\\\" contenteditable=\\\"true\\\"/></p></td></tr><tr width=\\\"573\\\"><td style=\\\"border:1px solid black;\\\" class=\\\"col_1\\\" width=\\\"76\\\"><p>图4</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_2\\\" width=\\\"254\\\"><p><input type=\\\"text\\\" readonly=\\\"true\\\" value=\\\"4\\\" class=\\\"questions-blank\\\" contenteditable=\\\"true\\\"/>（绰号）斗浪里白跳</p></td><td style=\\\"border:1px solid black;\\\" class=\\\"col_3\\\" width=\\\"243\\\"><p>急躁莽撞</p></td></tr></tbody></table>\", \"type\": {\"id\": 2, \"name\": \"填空题\"}, \"answer\": [\"<p>林冲</p>\", \"<p>倒拔垂杨柳</p>\", \"<p>箭法如神</p>\", \"<p>黑旋风</p>\"], \"analysis\": \"<p>识记有关名著《水浒传》中有关的重要人物，性格特点及相关的故事来解答。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>有人评价图1中这个人物既“忍”又“狠”。“风雪山神庙”的故事表现了他在一再的“忍”之后的“狠”。请结合相关故事情节，简要谈谈这个人物在雪夜上梁山之后“忍”与“狠”的又一次表现。</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>上梁山后，面对“投名状”的刁难，林冲隐忍不发，直至大战青面兽杨志，从而得到王伦的入伙许可，表现出他的“忍”；等到晁盖等人梁山入伙受阻时，林冲火并王伦，表现出他的“狠”。</p>\"], \"analysis\": \"<p>依据关于林冲所做两件事来分析人物的性格特点。刚上梁山，面对“投名状”的刁难，林冲隐忍不发，直至大战青面兽杨志，从而得到王伦的入伙许可，表现出他的“忍”；等到晁盖等人梁山入伙受阻时，林冲火并王伦，表现出他的“狠”。</p>\", \"difficulty\": \"1\"}]}";

        String underline="{\"body\": \"<p>给加下划线的字或词选择正确的理解（1分）。</p><p><span style=\\\"line-height: 1em;\\\">太阳一出来，榆树的叶子就发光了，它们</span><span style=\\\"line-height: 1em; text-decoration: underline;\\\">闪烁</span><span style=\\\"line-height: 1em;\\\">得和沙</span><span style=\\\"line-height: 1em;\\\">滩上的蚌壳一样。(&nbsp;&nbsp;&nbsp; )</span></p>\", \"answer\": [\"A\"], \"options\": [\"<p>（光亮）动摇不定，忽明忽暗。</p>\", \"<p>（说话）稍微露出一点想法，但不肯说明确，吞吞吐</p><p>吐。</p>\"], \"analysis\": \"<p>暂无</p>\"}";

        String table="{\"map\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"material\": \"<p>阅读下文，完成第16—21题。（18分）</p><p>①羊祜，字叔子，泰山南城人也。博学能<span style=\"border-bottom:dotted 3px\">属</span>文，美须眉，善谈论。郡将夏侯威异之，以兄霸之子妻之。举上计吏，州四辟从事，皆不就。夏侯霸之降蜀也，姻亲多告绝，祜独<span style=\"border-bottom:dotted 3px\">安</span>其室，恩礼有加焉。</p><p>②帝将有灭吴之志，以祜为都督荆州诸军事，镇南夏，甚得江汉之心，吴石城守去襄阳七百余里，每为边害，祜患之，竟以诡计<sup>①</sup>令吴罢守。于是戍逻减半，分以垦田八百余顷，大获其利。在军常轻裘缓带，身不被甲，铃阁以下，侍卫不过十数人，而颇以渔畋废政。尝欲夜出，军司徐胤执綮当营门曰：“将军都督万里，安可轻脱！将军之安危，亦国家之安危也。胤今日若死，此门乃开耳！”祜改容谢之，此后稀出矣。</p><p>③<span style=\"text-decoration: underline;\">每与吴人交兵，克日方战，不为掩袭之计。将帅有欲进谲诈之策者，辄饮以醇酒，使不得言。</span>吴将邓香掠夏口，祜募生缚香，既至，宥之。香感其恩甚，率部曲而降。祜出军行吴境，刈谷为粮，皆计所侵，送绢偿之，每会众江涡游猎，常止晋地，若禽兽先为吴人所伤而为晋兵所得者，皆封还之，于是吴人翁然悦服。称为“羊岱”，不之名也。祜与陆抗相对，使命交通，抗称祜之德量，虽乐毅，诸葛孔明不能过也。抗常病，祜馈之药。抗服之无疑心，人多谏抗，抗曰：“羊祜岂鸩人者？”</p><p>④祜女夫尝劝祜有所营置，令有归载者，祜黯然不应，遂告诸子曰：“人臣树私则背公，是大惑也，汝宜识吾此意。”</p><p>（节选自《晋书·羊祜传》）</p><p>【注】①诡计：奇计。</p>\", \"questions\": [{\"body\": \"<p>写出下列加点词在句中的意思。（2分）</p><p>①博学能<span style=\"border-bottom:dotted 3px\">属</span>文②枯独<span style=\"border-bottom:dotted 3px\">安</span>其室</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>①撰写 &nbsp;②安守</p>\"], \"options\": [], \"analysis\": \"<p>试题分析：文言实词的考核一直是文言文阅读重点考核的内容，课标卷近几年有加大难得的趋势，考的词语一般在课本中没有出现，要求学生根据文意进行推断，答题时注意分析词语前后搭配是否得当。（1）里根据前面“博学”和后面“文”可知，译为动词词性，和文章搭配，故译为“撰写、创作”；（2）根据“其室”可知译为动词，结合后面内容“恩礼有加焉”可知，译为“安守”。 文言词语大部分对应的是现代汉语的一个词语，但也有词义转移的现象，答题时应该重点注意。</p><p>考点：理解常见文言实词在文中的含义。能力层级为理解B。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>为下列句中加点词语选择释义正确的一项。（2分）</p><p>皆计所<span style=\"border-bottom:dotted 3px\">侵</span>，送绢尝之（）</p>\", \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"A\"], \"options\": [\"<p>侵占</p>\", \"<p>侵犯</p>\", \"<p>侵害</p>\", \"<p>侵袭</p>\"], \"analysis\": \"<p>试题分析：文言实词的考核一直是文言文阅读重点考核的内容，课标卷近几年有加大难度的趋势，考的词语一般在课本中没有出现，要求学生根据文意进行推断，答题时注意分析词语前后搭配是否得当。根据文意“割谷为军根，都按照所割的数量，译为侵占。</p><p>考点：理解常见文言实词在文中的含义。能力层级为理解B。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>为下列句中加点词语选择释义正确的一项。（2分）</p><p>枯与陆抗相对，使命<span style=\"border-bottom:dotted 3px\">交通</span>（）</p>\", \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"C\"], \"options\": [\"<p>结交</p>\", \"<p>连接</p>\", \"<p>往来</p>\", \"<p>沟通</p>\"], \"analysis\": \"<p>试题分析：文言实词的考核一直是文言文阅读重点考核的内容，课标卷近几年有加大难度的趋势，考的词语一般在课本中没有出现，要求学生根据文意进行推断，答题时注意分析词语前后搭配是否得当。结合语境“羊祜和（吴国）陆抗两军对峙，互通使者往来”可知，迭项C正确。</p><p>考点：理解常见文言实词在文中的含义。能力层级为理解B。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>下列句中加点词意义和用法都相同的一项是（）。（2分）</p>\", \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"C\"], \"options\": [\"<p>胤今日若死，此门<span style=\"border-bottom:dotted 3px\">乃</span>开耳今君<span style=\"border-bottom:dotted 3px\">乃</span>亡赵走燕</p>\", \"<p>称为“羊公”，不<span style=\"border-bottom:dotted 3px\">之</span>名也名读<span style=\"border-bottom:dotted 3px\">之</span>不知，惑之不解</p>\", \"<p>枯女夫尝劝枯有<span style=\"border-bottom:dotted 3px\">所</span>营置视成<span style=\"border-bottom:dotted 3px\">所</span>蓄，掩口胡卢而笑</p>\", \"<p>人臣树私<span style=\"border-bottom:dotted 3px\">则</span>背公及诸河，<span style=\"border-bottom:dotted 3px\">则</span>在舟中矣</p>\"], \"analysis\": \"<p>文言虚词的考核一直是文言文阅读重点考核的内容，课标卷近几年有加大难得的趋势，要求学生根据文意进行推断，答题时注意分析词语前后搭配是否得当，A项译为才；“乃”字是语气助词，可以起到承接的作用。B项结构助词，不译；宾语前置句的标志，不译。C项“所”，译为代词，放在动词前组成名词性短语。D项译为就；已经。此外还需要注意是否合语境，感情色彩等。文言词语大部分对应的是现代汉语的一个词语，但也有词义转移的现象。答题时应该重点注意。</p><p>考点：理解常见文言虚词在文中的含义。能力层级为理解B。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>把第③段画线句译成现代汉语。（6分）</p><p>每与吴人交兵，克日方战，不为掩袭之计。将帅有欲进谲诈之策者，辄饮以醇酒，使不得言。</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>每次与吴人交战约定好日期才交锋，不做那些偷袭的事，部下将领中要献那些诡诈的计策的．他就给人喝好酒，使献计者醉不能言。</p>\"], \"options\": [], \"analysis\": \"<p>试题分析：本题考查语句翻译，需要抓住的关键词有：“克”译为约定，“为”译为做，“将帅有欲进谲诈之策者”定语后置句，“辄”译为就。首先要找出专有名词，即人名、地名、官职等；然后再看有否特殊句式，最后再确定关键字进行翻译，一般为直译。文言文的翻译，最基本的方法就是替换、组词、保留、省略。对古今异义的词语要“替换”；对古今词义大体一致的词语则“组词”；对特殊的地名、人名等要“保留”，如“吴人”；对古汉语中的同义反复的词语可以“省略”其中一个，有些虚词不必要或难于恰当翻译出来的也可以“省略”。平时训练时注意自己确定句子的赋分点，翻译时保证赋分点的落实，还要注意翻译完之后一定要注意对句子进行必要的整理，使句意通顺。建议翻译时打草稿。</p><p>考点：理解并翻译文中的句子。能力层级为理解B。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>第④段中“此意”指的是：做大臣应当□ □ □ □。（2分）</p>\", \"type\": {\"id\": 4, \"name\": \"简答题\"}, \"answer\": [\"<p>自守卫疆界，不要贪求小利；不树私背公。</p>\"], \"options\": [], \"analysis\": \"<p>试题分析：本题考查第④段中“此意”指的是内容，结合题干提示，把“此意”放回原文，结合“人臣树私则背公，是大惑也”可知，“此意”应该指不树私背公，自守卫疆界，不要贪求小利。</p><p>考点：归纳内容要点，概括中心意思。能力层级为分析综合C。</p>\", \"difficulty\": \"1\"}, {\"body\": \"<p>依据②③两段相关事迹，概括羊祜的主要品质，完成表格。（4分）</p><table style=\"border: 1px solid black; border-collapse: collapse;\" class=\"col_count_2\" width=\"568\" rows=\"5\" columns=\"2\"><tbody><tr width=\"568\" class=\"firstRow\"><td style=\"border:1px solid black;\" class=\"col_1\" width=\"284\"><p>相关事迹</p></td><td style=\"border:1px solid black;\" class=\"col_2\" width=\"284\"><p>羊祜的品质</p></td></tr><tr width=\"568\"><td style=\"border:1px solid black;\" class=\"col_1\" width=\"284\"><p>安边垦田</p></td><td style=\"border:1px solid black;\" class=\"col_2\" width=\"284\"><p>（1）<input type=\"text\" readonly=\"true\" value=\"1\" class=\"questions-blank\" contenteditable=\"true\"/></p></td></tr><tr width=\"568\"><td style=\"border:1px solid black;\" class=\"col_1\" width=\"284\"><p>徐胤当门</p></td><td style=\"border:1px solid black;\" class=\"col_2\" width=\"284\"><p>（2）<input type=\"text\" readonly=\"true\" class=\"questions-blank\" value=\"2\" contenteditable=\"true\"/>&nbsp;</p></td></tr><tr width=\"568\"><td style=\"border:1px solid black;\" class=\"col_1\" width=\"284\"><p>邓香归降</p></td><td style=\"border:1px solid black;\" class=\"col_2\" width=\"284\"><p>（3）<input type=\"text\" readonly=\"true\" class=\"questions-blank\" value=\"3\" contenteditable=\"true\"/>&nbsp;</p></td></tr><tr width=\"568\"><td style=\"border:1px solid black;\" class=\"col_1\" width=\"284\"><p>陆抗服药</p></td><td style=\"border:1px solid black;\" class=\"col_2\" width=\"284\"><p>（4）<input type=\"text\" readonly=\"true\" class=\"questions-blank\" value=\"4\" contenteditable=\"true\"/>&nbsp;&nbsp;</p></td></tr></tbody></table>\", \"type\": {\"id\": 2, \"name\": \"填空题\"}, \"answer\": [\"<p>有眼见，识大局</p>\", \"<p>深得军心</p>\", \"<p>以德服人，做人光明磊落</p>\", \"<p>真诚待人，宽容大量</p>\"], \"options\": [], \"analysis\": \"<p>试题分析：考查二、三两节的内容，概括羊祜的主要品质。根据不同的事迹，抓住关键内容分析人物品质。“安边垦田”，“于是戍逻减半，分以垦田八百余顷，大获其利”可见他有眼光，有胆识，有谋略；“徐胤当门”根据徐胤说的那番话，可知他深得人心；“邓香归降”根据他从不偷袭，“香感其恩甚，率部曲而降”可知，他做人光明正大，以德服人；“陆抗服药”根据文意“祜馈之药。抗服之无疑心”可知，他能够真诚待人，宽宏大量。</p><p>考点：归纳内容要点，概括中心意思。能力层级为分析综合C。</p>\", \"difficulty\": \"1\"}]}";
        //String json = ParseHtmlUtil.html2json(html, "55", 8);

        //测试说明：用32题型的解法是对的，170默认用的1题型解法。
        //String json = ParseHtmlUtil.html2json(useStructId, "32", 8);
        //String json = ParseHtmlUtil.html2json(useStructId, "170", 8);

        String json = ParseHtmlUtil.html2json(table, "7", 5,questionTypeDao);

        //String json = ParseHtmlUtil.html2json(useStructId, "2", 8,2L);
        logger.info("\n json=" + json);


    }
    @Test
    public void testGetIntelligent(){
        String answerString="&nbsp; ............、1 ＜23 &nbsp;&nbsp; 1 2《3 &nbsp;&nbsp; 1 2)3 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;&nbsp; 1 23 &nbsp;";
//        String answerString1="a aa";
//        String answerString2=" aaa";
// <p>、</p><p>，</p><p>．</p><p>。</p><p>？</p><p>！</p><p>：</p><p>；</p><p>⋯⋯</p><p>......</p><p>“</p><p>”</p><p>‘</p><p>’</p><p>＂</p><p>＇</p><p>（</p><p>）</p><p>＜</p><p>＞</p><p>〜</p><p>－</p><p>＋</p><p>＝</p><p>⋯</p><p>...</p><p>《</p><p>》</p>",
// "<p>,</p><p>.</p><p>?</p><p>!</p><p>:</p><p>;</p><p>&quot;</p><p>&#39;</p><p>(</p><p>)</p><p>...</p><p>&lt;</p><p>&gt;</p><p>~</p><p>+</p><p>=</p><p>⋯</p>",
// "<p>测试</p>",
// "<p>124</p>",
// "<p>abc</p>
        Object objStr=null;
        String wuxxStr="<p>、</p><p>，</p><p>．</p><p>。</p><p>？</p><p>！</p><p>：</p><p>；</p><p>⋯⋯</p><p>......</p><p>“</p><p>”</p><p>‘</p><p>’</p><p>＂</p><p>＇</p><p>（</p><p>）</p><p>＜</p><p>＞</p><p>〜</p><p>－</p><p>＋</p><p>＝</p><p>⋯</p><p>...</p><p>《</p><p>》</p>";

        String singleIndex="<p>,.?!:;&quot;&#39;()...&lt;&gt;~+=⋯abc</p>";
        List<Object> answerStringList=new ArrayList<>();
        //answerStringList.add(answerString);
        //answerStringList.add(objStr);
        //answerStringList.add(wuxxStr);
//        answerStringList.add(answerString1);
//        answerStringList.add(answerString2);
        answerStringList.add(singleIndex);

        long l = System.currentTimeMillis();
        QuestionServiceUtil.getIntelligent(answerStringList,50,5L);
        long l1 = System.currentTimeMillis();
        logger.info("getIntelligent:cost:{}ms", l1 - l);
        System.out.println(QuestionServiceUtil.getIntelligent(answerStringList,2,6L));
    }

    @Test
    public void testEncode3(){
        String answer="1‘2’3＂＇（）＜＞～－＋＝…...《》,.?!\"'()...<>~:q;45......a、，．。？！：；……b......“”cde";
        long l = System.currentTimeMillis();
        QuestionServiceUtil.encode3(answer);
        long l1 = System.currentTimeMillis();
        logger.info("encode3:cost:{}ms", l1 - l);
        System.out.println(QuestionServiceUtil.encode3(answer));
    }

    @Test
    public void testHtml() {
        String s = "{\"content\": {\"material\": \"<p>dsafffadg&quot;&quot;</p>\", \"questions\": [{\"body\": " +
                "\"<p>dsf</p>\", \"type\": {\"id\": 1, \"name\": \"选择题\"}, \"answer\": [\"B\"], \"options\": " +
                "[\"<p>sdf</p>\", \"<p>sdf</p>\", \"<p>fsd</p>\", \"<p>sdf</p>\"], \"analysis\": \"\", " +
                "\"difficulty\": \"3\"}]}, \"question_id\": 7675622}";

        //s = StringEscapeUtils.unescapeHtml4(s);
        JSONObject object;
        Object content;
        try {
            object = new JSONObject(s);
            content = object.get("content");
        } catch (JSONException e) {
            logger.error("\n =====>>JSONObject 读取数据库htmlData数据异常:{}", s);
            throw new BizLayerException(e.getMessage(), JSON_CONVERT_FAIL);
        }

        logger.info(content.toString());
    }

}
