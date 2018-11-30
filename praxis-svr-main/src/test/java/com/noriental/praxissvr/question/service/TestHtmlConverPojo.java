package com.noriental.praxissvr.question.service;

import com.hyd.ssdb.util.Str;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.question.bean.Continued;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.bean.html.ComplexQuestion;
import com.noriental.praxissvr.question.bean.html.Question;
import com.noriental.praxissvr.question.bean.html.SingleQuestion;
import com.noriental.praxissvr.question.request.QuestionContinuedRequest;
import com.noriental.praxissvr.question.request.UploadQuestionRequest;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.utils.EntityUtil;
import com.noriental.solr.common.utils.UUIDUtil;
import com.noriental.utils.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hushuang on 2016/11/28.
 */
public class TestHtmlConverPojo {

    private static final Logger logger = LoggerFactory.getLogger(TestHtmlConverPojo.class);

    //测试SSDB数据转换
    @Test
    public void testSSDBUtilsconversion(){
        String str = "{\"questionId\":4002442,\"content\":{\"analysis\":\"<p>&#x3010;&#x89E3;&#x6790;&#x3011;consider &#x4F5C;&#x201C;&#x8003;&#x8651;&#x201D;&#x89E3;&#x65F6;&#xFF0C;&#x540E;&#x5E38;&#x63A5;-ing&#x5F62;&#x5F0F;&#xFF08;&#x77ED;&#x8BED;&#xFF09;&#x4F5C;&#x5BBE;&#x8BED;&#xFF1B;&#x4F5C;&#x201C;&#x8BA4;&#x4E3A;&#x201D; &#x89E3;&#x65F6;&#xFF0C;&#x540E;&#x5E38;&#x63A5;&#x542B;&#x6709;&#x540D;&#x8BCD;&#x3001;&#x5F62;&#x5BB9;&#x8BCD;&#x6216;to be&#x7684;&#x590D;&#x5408;&#x7ED3;&#x6784;&#x3002;</p>\",\"answer\":[\"D\",\"M\"],\"options\":[\"<p>to change; to be</p>\",\"<p>to change; being</p>\",\"<p>changing; being</p>\",\"<p>changing; to be</p>\"],\"body\":\"<p>&#x3010;&#x9898;&#x6587;&#x3011;&#x2014;&#x2014; Have you considered _____ your job as a teacher?<br>&#x2014;&#x2014;Yes. I like the job because a teacher is often considered _____ a gardener.</p>\"}}";
        String conentNull="{\"questionId\":4002442,\"content\":\"\"}";
        String content = JsonUtils.getIndexContent(conentNull,"content");
        logger.info("============="+content);
    }

    @Test
    public void testIs_Key(){

        String str="{\"body\": \"<p>吸烟<u> </u>有害健康，香烟的烟气中含有几百种对人体有害的物质，尼古丁是其中的一种，其化学式为<img src=\\\"http://172.18.4.81:8080/group0/M00/00/00/rBIEUVi-m6iAKwhQAAAC8ZalVBs632.png\\\" width=\\\"71px\\\" height=\\\"22px\\\">，下列关于尼古丁的说法正确的是（　　）</p>\", \"answer\": [\"C\"], \"options\": [\"<p>尼古丁的分子中氢、氮元素的质量比是7：1</p>\", \"<p>尼古丁中含有氮分子</p>\", \"<p>尼古丁中氢元素质量分数最小</p>\", \"<p>尼古丁是由26个原子构成</p>\"], \"analysis\": \"<p>【分析】A、根据化合物中各元素质量比=各原子的相对原子质量×原子个数之比，进行分析判断．<br>B、根据尼古丁的微观构成，进行分析判断．<br>C、根据化合物中各元素质量比=各原子的相对原子质量×原子个数之比，进行分析判断．<br>D、根据尼古丁的微观构成，进行分析判断．<br>【解答】解：A、尼古丁的分子中氢、氮元素的质量比是（12×10）：（1×14）=60：7，故选项说法错误．<br>B、尼古丁是由尼古丁分子构成的，故选项说法错误．<br>C、尼古丁中C、H、N元素的质量比是（12×10）：（1×14）：（14×2）=60：7：14，则尼古丁中氢元素质量分数最小，故选项说法正确．<br>D、尼古丁是由尼古丁分子构成的，1个尼古丁分子是由26个原子构成的，故选项说法错误．<br>故选：C．</p>\"}";
        String str1 = "{\"body\":\"<p>选择题20161226</p>\",\"options\":[\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"A\"],\"analysis\":\"<p>选择题20161226</p>\",\"questions\":[]}";

        boolean questions = JsonUtils.is_key(str1, "body");
        logger.info(questions+"");

    }



    @Test
    public void testQuestionContinuedRequest() throws IOException {

        String str = "{\"reqId\":null,\"object\":\"{\\\"typeId\\\": 1, \\\"src\\\": 0, \\\"group\\\": 0, \\\"uploadSrc\\\": 105, \\\"level\\\": 1, \\\"topicIds\\\": [2114], \\\"subjectId\\\": 4, \\\"typeName\\\": \\\"\\\\u9009\\\\u62e9\\\\u9898\\\", \\\"state\\\": \\\"ENABLED\\\", \\\"html\\\": \\\"{\\\\\\\"body\\\\\\\": \\\\\\\"<p>&#x5982;&#x56FE;&#xFF0C;&#x5706;O&#x4E0E;x&#x8F74;&#x7684;&#x6B63;&#x534A;&#x8F74;&#x7684;&#x4EA4;&#x70B9;&#x4E3A;A&#xFF0C;&#x70B9;B&#xFF0C;C&#x5728;&#x5706;O&#x4E0A;&#xFF0C;&#x70B9;B&#x7684;&#x5750;&#x6807;&#x4E3A;&#xFF08;-1&#xFF0C;2&#xFF09;&#xFF0C;&#x70B9;C&#x4F4D;&#x4E8E;&#x7B2C;&#x4E00;&#x8C61;&#x9650;&#xFF0C;&#x2220;AOC=&#x03B1;&#xFF0E;&#x82E5;|BC|=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_fbb12d64a888c8ccd9ec7d5ab24afc89.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"19px\\\\\\\\\\\\\\\">&#xFF0C;&#x5219;sin<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">cos<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_52fcbc629fdbed7bbd7dc3a2900a031e.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"19px\\\\\\\\\\\\\\\"><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c2e167ba311d6bf9cafa56ccd8511586.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"53px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\"><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">-<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_7d959b9f6725ec10ba814b2013e2a623.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">=&#xFF08;&#x3000;&#x3000;&#xFF09;<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_0600cbbcc5a1f0207f8f5a1d0db95ad7.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"133px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"135px\\\\\\\\\\\\\\\"></p>\\\\\\\", \\\\\\\"map\\\\\\\": {\\\\\\\"url\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"name\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"size\\\\\\\": \\\\\\\"\\\\\\\"}, \\\\\\\"analysis\\\\\\\": \\\\\\\"<p>&#x3010;&#x5206;&#x6790;&#x3011;&#x6839;&#x636E;&#x4E09;&#x89D2;&#x51FD;&#x6570;&#x7684;&#x500D;&#x89D2;&#x516C;&#x5F0F;&#x5C06;&#x51FD;&#x6570;&#x5F0F;&#x8FDB;&#x884C;&#x5316;&#x7B80;&#xFF0C;&#x7ED3;&#x5408;&#x4E09;&#x89D2;&#x51FD;&#x6570;&#x7684;&#x5B9A;&#x4E49;&#x5373;&#x53EF;&#x5F97;&#x5230;&#x7ED3;&#x8BBA;&#xFF0E;<br>&#x3010;&#x89E3;&#x7B54;&#x3011;&#x89E3;&#xFF1A;&#x2235;&#x70B9;B&#x7684;&#x5750;&#x6807;&#x4E3A;&#xFF08;-1&#xFF0C;2&#xFF09;&#xFF0C;<br>&#x2234;|OB|=|OC|=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_fbb12d64a888c8ccd9ec7d5ab24afc89.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"19px\\\\\\\\\\\\\\\">&#xFF0C;<br>&#x2235;|BC|=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_fbb12d64a888c8ccd9ec7d5ab24afc89.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"19px\\\\\\\\\\\\\\\">&#xFF0C;<br>&#x2234;&#x25B3;OBC&#x662F;&#x7B49;&#x8FB9;&#x4E09;&#x89D2;&#x5F62;&#xFF0C;<br>&#x5219;&#x2220;AOB=&#x03B1;+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_74c8e97616ca527b3a162da91035d940.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">&#xFF0E;<br>&#x5219;sin&#xFF08;&#x03B1;+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_74c8e97616ca527b3a162da91035d940.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">&#xFF09;=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_6a32652f9d131ece29553056f3f95a10.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_95b5ec3df21c5584178de70364026823.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"25px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"38px\\\\\\\\\\\\\\\">&#xFF0C;cos&#xFF08;&#x03B1;+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_74c8e97616ca527b3a162da91035d940.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">&#xFF09;=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_fee98867db3efdee30a43d8e580f2700.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"17px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"35px\\\\\\\\\\\\\\\">=-<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_6f1d46c8173e1573008178d4ef993061.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">&#xFF0C;<br>&#x5219;sin<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">cos<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_52fcbc629fdbed7bbd7dc3a2900a031e.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"19px\\\\\\\\\\\\\\\"><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c2e167ba311d6bf9cafa56ccd8511586.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"53px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\"><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_c439082535165911ad921c125778051f.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"15px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">-<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_7d959b9f6725ec10ba814b2013e2a623.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_924c83b63f1b78eb3edc78bf2174cd22.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"9px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">sin&#x03B1;+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_7d959b9f6725ec10ba814b2013e2a623.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">cos&#x03B1;=sin&#xFF08;&#x03B1;+<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_74c8e97616ca527b3a162da91035d940.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">&#xFF09;=<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_95b5ec3df21c5584178de70364026823.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"25px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"38px\\\\\\\\\\\\\\\">&#xFF0C;<br>&#x6545;&#x9009;&#xFF1A;D&#xFF0E;</p>\\\\\\\", \\\\\\\"questions\\\\\\\": [], \\\\\\\"answer\\\\\\\": [\\\\\\\"D\\\\\\\"], \\\\\\\"audio\\\\\\\": {\\\\\\\"url\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"name\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"size\\\\\\\": \\\\\\\"\\\\\\\"}, \\\\\\\"options\\\\\\\": [\\\\\\\"<p>-<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_95b5ec3df21c5584178de70364026823.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"25px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"38px\\\\\\\\\\\\\\\"></p>\\\\\\\", \\\\\\\"<p>-<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_6f1d46c8173e1573008178d4ef993061.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\"></p>\\\\\\\", \\\\\\\"<p><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_6f1d46c8173e1573008178d4ef993061.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"16px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\"></p>\\\\\\\", \\\\\\\"<p><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_95b5ec3df21c5584178de70364026823.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"25px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"38px\\\\\\\\\\\\\\\"></p>\\\\\\\"]}\\\", \\\"uploadId\\\": 62951096198, \\\"source\\\": \\\"\\\", \\\"chapterId\\\": 0, \\\"orgId\\\": 1, \\\"orgType\\\": 4, \\\"new_format\\\": 1}\"}";
        String str1 = "{\"reqId\":null,\"object\":\"{\\\"typeId\\\": 4, \\\"src\\\": 0, \\\"group\\\": 0, \\\"uploadSrc\\\": 105, \\\"level\\\": 1, \\\"topicIds\\\": [15577], \\\"subjectId\\\": 4, \\\"typeName\\\": \\\"\\\\u7b80\\\\u7b54\\\\u9898\\\", \\\"state\\\": \\\"ENABLED\\\", \\\"html\\\": \\\"{\\\\\\\"body\\\\\\\": \\\\\\\"<p>&#x6211;&#x6821;&#x4E3A;&#x4E86;&#x4E86;&#x89E3;&#x5B66;&#x751F;&#x7684;&#x65E9;&#x9910;&#x8D39;&#x7528;&#x60C5;&#x51B5;&#xFF0C;&#x62BD;&#x6837;&#x8C03;&#x67E5;&#x4E86;100&#x540D;&#x5B66;&#x751F;&#x7684;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#xFF08;&#x5355;&#x4F4D;&#xFF1A;&#x5143;&#xFF09;&#xFF0C;&#x5F97;&#x5982;&#x56FE;&#x6240;&#x793A;&#x7684;&#x9891;&#x7387;&#x5206;&#x5E03;&#x76F4;&#x65B9;&#x56FE;&#xFF0C;&#x56FE;&#x4E2D;&#x6807;&#x6CE8;&#x6570;&#x5B57;a&#x6A21;&#x7CCA;&#x4E0D;&#x6E05;&#xFF0E;<br><img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_d2852006a2d32edbbfc1406ad9d11c85.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"329px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"157px\\\\\\\\\\\\\\\"><br>&#xFF08;1&#xFF09;&#x8BD5;&#x6839;&#x636E;&#x9891;&#x7387;&#x5206;&#x5E03;&#x76F4;&#x65B9;&#x56FE;&#x6C42;a&#x7684;&#x503C;&#xFF0C;&#x5E76;&#x6C42;&#x6211;&#x6821;&#x5B66;&#x751F;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x7684;&#x4F17;&#x6570;&#xFF1B;<br>&#xFF08;2&#xFF09;&#x5DF2;&#x77E5;&#x6211;&#x6821;&#x6709;1000&#x540D;&#x5B66;&#x751F;&#xFF0C;&#x8BD5;&#x4F30;&#x8BA1;&#x6211;&#x6821;&#x6709;&#x591A;&#x5C11;&#x5B66;&#x751F;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x4E0D;&#x591A;&#x4E8E;6&#x5143;&#xFF1F;</p>\\\\\\\", \\\\\\\"map\\\\\\\": {\\\\\\\"url\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"name\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"size\\\\\\\": \\\\\\\"\\\\\\\"}, \\\\\\\"analysis\\\\\\\": \\\\\\\"<p>&#x3010;&#x5206;&#x6790;&#x3011;&#xFF08;1&#xFF09;&#x7531;&#x9891;&#x7387;&#x5206;&#x5E03;&#x76F4;&#x65B9;&#x56FE;&#x4E2D;&#x5404;&#x5C0F;&#x957F;&#x65B9;&#x5F62;&#x7684;&#x9762;&#x79EF;&#x4E4B;&#x548C;&#x7B49;&#x4E8E;1&#xFF0C;&#x6C42;&#x51FA;a&#x7684;&#x503C;&#xFF0C;&#x9891;&#x7387;&#x5206;&#x5E03;&#x76F4;&#x65B9;&#x56FE;&#x4E2D;&#x6700;&#x9AD8;&#x7684;&#x5C0F;&#x957F;&#x65B9;&#x4F53;&#x7684;&#x5E95;&#x9762;&#x8FB9;&#x957F;&#x7684;&#x4E2D;&#x70B9;&#x5373;&#x662F;&#x4F17;&#x6570;&#xFF1B;<br>&#xFF08;2&#xFF09;&#x6C42;&#x51FA;&#x6211;&#x6821;&#x5B66;&#x751F;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x4E0D;&#x591A;&#x4E8E;6&#x5143;&#x7684;&#x9891;&#x7387;&#xFF0C;&#x5373;&#x53EF;&#x6C42;&#x51FA;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x4E0D;&#x591A;&#x4E8E;6&#x5143;&#x7684;&#x4EBA;&#x6570;&#x662F;&#x591A;&#x5C11;&#xFF0E;</p>\\\\\\\", \\\\\\\"questions\\\\\\\": [], \\\\\\\"answer\\\\\\\": [\\\\\\\"&#x3010;&#x89E3;&#x7B54;&#x3011;&#x89E3;&#xFF1A;&#xFF08;1&#xFF09;&#x6839;&#x636E;&#x9891;&#x7387;&#x5206;&#x5E03;&#x76F4;&#x65B9;&#x56FE;&#x4E2D;&#x5404;&#x5C0F;&#x957F;&#x65B9;&#x5F62;&#x7684;&#x9762;&#x79EF;&#x4E4B;&#x548C;&#x7B49;&#x4E8E;1&#xFF0C;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#x2234;&#xFF08;0.05+0.10+a+0.10+0.05&#x00D7;2&#xFF09;&#x00D7;2=1&#xFF0C;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#x2234;a=0.15&#xFF0C;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#x2234;&#x6211;&#x6821;&#x5B66;&#x751F;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x7684;&#x4F17;&#x6570;&#x4E3A;x=\\\\\\\", \\\\\\\"<img src=\\\\\\\\\\\\\\\"http://qdimg.okjiaoyu.cn/qdimg_4ff90c4db2b912cffc464619ec790e44.png\\\\\\\\\\\\\\\" width=\\\\\\\\\\\\\\\"30px\\\\\\\\\\\\\\\" height=\\\\\\\\\\\\\\\"37px\\\\\\\\\\\\\\\">\\\\\\\", \\\\\\\"=5&#xFF1B;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#xFF08;2&#xFF09;&#x6211;&#x6821;&#x5B66;&#x751F;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x4E0D;&#x591A;&#x4E8E;6&#x5143;&#x7684;&#x9891;&#x7387;&#x662F;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#xFF08;0.05+0.10+0.15&#xFF09;&#x00D7;2=0.60&#xFF0C;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"&#x2234;&#x65E9;&#x9910;&#x5E73;&#x5747;&#x8D39;&#x7528;&#x4E0D;&#x591A;&#x4E8E;6&#x5143;&#x7684;&#x4EBA;&#x6570;&#x662F;\\\\\\\", \\\\\\\"<br>\\\\\\\", \\\\\\\"1000&#x00D7;0.60=600&#xFF0E;\\\\\\\"], \\\\\\\"audio\\\\\\\": {\\\\\\\"url\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"name\\\\\\\": \\\\\\\"\\\\\\\", \\\\\\\"size\\\\\\\": \\\\\\\"\\\\\\\"}, \\\\\\\"options\\\\\\\": []}\\\", \\\"uploadId\\\": 62951096198, \\\"source\\\": \\\"\\\", \\\"chapterId\\\": 0, \\\"orgId\\\": 1, \\\"orgType\\\": 4, \\\"new_format\\\": 1}\"}";
        Continued continued = JsonUtils.fromJson(str1, Continued.class);
        logger.info("《》《》《《》《》《《》《《》《》《》"+continued.getObject().toString());

        QuestionContinuedRequest request = JsonUtils.fromJson(continued.getObject().toString(), QuestionContinuedRequest.class);

        logger.info("==========="+request.getHtml());


    }


    @Test
    public void testdanti() throws IOException {

        String single="{\"body\":\"<p>如图（1）所示，<em>E</em>为矩形<em>ABCD</em>的边<em>AD</em>上一点，动点<em>P</em>、<em>Q</em>同时从点<em>B</em>出发，点<em>P</em>沿折线<em>BE</em>﹣<em>ED</em>﹣<em>DC</em>运动到点<em>C</em>时停止，点<em>Q</em>沿<em>BC</em>运动到点<em>C</em>时停止，它们运动的速度都是1<em>cm</em>/秒．设<em>P</em>、<em>Q</em>同发<em>t</em>秒时，△<em>BPQ</em>的面积为<em>ycm</em><img src=\\\"http://qdimg.okjiaoyu.cn/b9810177f165930a8497e15f34a3960d.png\\\" width=\\\"7px\\\" height=\\\"20px\\\"/>．已知<em>y</em>与<em>t</em>的函数关系图象如图（2）（曲线<em>OM</em>为抛物线的一部分），则下列结论：2秒时，△<em>ABE</em>∽△<em>QBP</em>；其中正确的结论是<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>（填序号）．<br/><img src=\\\"http://qdimg.okjiaoyu.cn/6cc8f776acb94b6b61b792af999c5bd0.png\\\" width=\\\"355px\\\" height=\\\"166px\\\"/><br/></p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"<p>①③④</p>\"],\"analysis\":\"<p>考点：动点问题的函数图象．<br/>专题：压轴题；动点型．<br/>分析：根据图（2）可以判断三角形的面积变化分为三段，可以判断出当点<em>P</em>到达点<em>E</em>时点<em>Q</em>到达点<em>C</em>，从而得到<em>BC</em>、<em>BE</em>的长度，再根据<em>M</em>、<em>N</em>是从5秒到7秒，可得<em>ED</em>的长度，然后表示出<em>AE</em>的长度，根据勾股定理求出<em>AB</em>的长度，然后针对各小题分析解答即可．<br/>解答：解：根据图（2）可得，当点<em>P</em>到达点<em>E</em>时点<em>Q</em>到达点<em>C</em>，<br/>∵点<em>P</em>、<em>Q</em>的运动的速度都是1<em>cm</em>/秒，<br/>∴<em>BC</em>=<em>BE</em>=5，<br/>∴<em>AD</em>=<em>BE</em>=5，故①小题正确；<br/>又∵从<em>M</em>到<em>N</em>的变化是2，<br/>∴<em>ED</em>=2，<br/>∴<em>AE</em>=<em>AD</em>﹣<em>ED</em>=5﹣2=3，<br/>在<em>Rt</em>△<em>ABE</em>中，<em>AB</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/16997b8134c1c94c750ad8908929a572.png\\\" width=\\\"157px\\\" height=\\\"27px\\\"/>=4，<br/>∴<em>cos</em>∠<em>ABE</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/78b60b28a004610999679fd468d9d12b.png\\\" width=\\\"29px\\\" height=\\\"41px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/01d7f0a0783b71ab80805e73fb159956.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>，故②小题错误；<br/>过点<em>P</em>作<em>PF</em>⊥<em>BC</em>于点<em>F</em>，<br/>∵<em>AD</em>∥<em>BC</em>，<br/>∴∠<em>AEB</em>=∠<em>PBF</em>，<br/>∴<em>sin</em>∠<em>PBF</em>=<em>sin</em>∠<em>AEB</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/fcf5d7604c030ec767a2e7bbfe2f883a.png\\\" width=\\\"29px\\\" height=\\\"41px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/e5f94ea2abd210a4628d8061f7ad1ca0.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>，<br/>∴<em>PF</em>=<em>PBsin</em>∠<em>PBF</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/9d65ca6193704e309254d8a3b5d1be6b.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/><em>t</em>，<br/>∴当0＜<em>t</em>≤5时，<em>y</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/5597b490ae65e9ea9f8a1ef47af100a4.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/><em>BQ</em>•<em>PF</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/0b0d45df4a1a17feb0937be12fef25a5.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/><em>t</em>•<img src=\\\"http://qdimg.okjiaoyu.cn/07a5a7a9006fa90f0876e9a0d66807f0.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/><em>t</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/1b30da644ef7febae83df81fca72c304.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/><em>t</em><img src=\\\"http://qdimg.okjiaoyu.cn/b9810177f165930a8497e15f34a3960d.png\\\" width=\\\"7px\\\" height=\\\"20px\\\"/>，故③小题正确；<br/>当<em>t</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/6b29b24f5c3a922c0aff18f8d91f636e.png\\\" width=\\\"24px\\\" height=\\\"41px\\\"/>秒时，点<em>P</em>在<em>CD</em>上，此时，<em>PD</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/89696a01ab9e070549b8d54916e2f245.png\\\" width=\\\"24px\\\" height=\\\"41px\\\"/>﹣<em>BE</em>﹣<em>ED</em>=<img src=\\\"http://qdimg.okjiaoyu.cn/f746ceb8ed469810b7a9d411a0e436dc.png\\\" width=\\\"24px\\\" height=\\\"41px\\\"/>﹣5﹣2=<img src=\\\"http://qdimg.okjiaoyu.cn/79b7c4b2be56b52faf5b941aa2c92730.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>，<br/><em>PQ</em>=<em>CD</em>﹣<em>PD</em>=4﹣<img src=\\\"http://qdimg.okjiaoyu.cn/9e48f6fd77a2d527634a6581ed589e6b.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/56d548119b55119eb1033c90d00e128e.png\\\" width=\\\"21px\\\" height=\\\"41px\\\"/>，<br/>∵<img src=\\\"http://qdimg.okjiaoyu.cn/0428b3e624cfb510f26d2cb259c73cba.png\\\" width=\\\"29px\\\" height=\\\"41px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/ebc418a6186e4dbe5180c6353d00a92b.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>，<img src=\\\"http://qdimg.okjiaoyu.cn/d7a3c4499369a48e61cacd8acbd321fe.png\\\" width=\\\"29px\\\" height=\\\"44px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/fad23d5fac6233c305fb5e06fb0cde78.png\\\" width=\\\"24px\\\" height=\\\"60px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/77c9f83460da2fc73580836d72418e2d.png\\\" width=\\\"16px\\\" height=\\\"41px\\\"/>，<br/>∴<img src=\\\"http://qdimg.okjiaoyu.cn/6d707716d49e5e7e88db6238aef2f9d9.png\\\" width=\\\"29px\\\" height=\\\"41px\\\"/>=<img src=\\\"http://qdimg.okjiaoyu.cn/ccb4171ab7c55ba92c3d790a077c4552.png\\\" width=\\\"29px\\\" height=\\\"44px\\\"/>，<br/>又∵∠<em>A</em>=∠<em>Q</em>=90°，<br/>∴△<em>ABE</em>∽△<em>QBP</em>，故④小题正确．<br/>综上所述，正确的有①③④．<br/>故答案为：①③④．<br/><img src=\\\"http://qdimg.okjiaoyu.cn/9d893d059ee42bbf707160c341424e75.png\\\" width=\\\"191px\\\" height=\\\"140px\\\"/><br/><img src=\\\"http://qdimg.okjiaoyu.cn/5c67100577d6697a2a3468adae71ee8b.png\\\" width=\\\"365px\\\" height=\\\"164px\\\"/><br/>点评：本题考查了动点问题的函数图象，根据图（2）判断出点<em>P</em>到达点<em>E</em>时点<em>Q</em>到达点<em>C</em>是解题的关键，也是本题的突破口．<br/></p>\",\"questions\":[]}";

        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);

        System.out.println(question.getBody());


    }




    //----------------New----单题--------------------------------


    @Test
    public void testXuanze() throws IOException {

       // String single = "{\"body\":\"<p>选择题</p>\",\"options\":[\"<p>撒的撒<br/></p>\",\"<p>滴滴顺风车</p>\",\"<p>发顺丰撒</p>\",\"<p>范德萨发的</p>\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"A\",\"C\"],\"analysis\":\"<p>发大水发大水</p>\",\"questions\":[]}";
        String single="{\"body\":\"<p>选择题20161226</p>\",\"options\":[\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\",\"<p>选择题20161226</p>\"],\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"A\"],\"analysis\":\"<p>选择题20161226</p>\",\"questions\":[]}";
        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);
        logger.info("选择题pojo={}",question);
    }


    @Test
    public void testTianKong() throws IOException {

        String single = "{\"body\":\"<p>我是填空题<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"<p>天空答案</p>\"],\"analysis\":\"<p>解析</p>\",\"questions\":[]}";
        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);
        logger.info("填空题pojo={}",question.toString());
    }

    @Test
    public void testPanDuan() throws Exception {

        String single = "{\"body\":\"<p>判断&nbsip;&nbsp;&nbsp;&nbsp;</p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"1\"],\"analysis\":\"<p>判断解析</p>\",\"questions\":[]}";
        //String single = "{\"body\":\"<p>fsfadsfsda</p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":\"1\",\"analysis\":\"<p>fdsfdsafasd</p>\",\"questions\":[]}";
        int questions = JsonUtils.getQuestionsLength(single, "questions");
        logger.info("长度为：==={}",questions);
        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);
        logger.info("判断题pojo={}",question.toString());
    }


    @Test
    public void testTingDanCiGenDuDanCi() throws IOException {

        String single = "{\"body\": \"<p>\\u542c\\u5355\\u8bcd, \\u5e76\\u5728\\u542c\\u5b8c\\u540e\\u70b9\\u51fb\\u5f55\\u97f3, \\u6a21\\u4eff\\u6717\\u8bfb\\u5355\\u8bcd</p>\", \"answer\": [{\"answer_audio\": {\"url\": \"http://qdimg.okjiaoyu.cn/qdimg_6f04f090c2735d8b9c33f5194716237f.mp3\", \"name\": \"\", \"size\": \"\"}, \"answer_content\": \"<p>nationality</p>\"}], \"audio\": {\"url\": \"\", \"name\": \"\", \"size\": \"\"}, \"prompt\": \"<p>\\u542c\\u5355\\u8bcd, \\u5e76\\u5728\\u542c\\u5b8c\\u540e\\u70b9\\u51fb\\u5f55\\u97f3, \\u6a21\\u4eff\\u6717\\u8bfb\\u5355\\u8bcd</p>\", \"analysis\": \"<p>\\u8003\\u6838\\u8bcd\\u6c47\\u53d1\\u97f3\\u7684\\u51c6\\u786e\\u6027</p>\"}";
        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);
        logger.info("\n===== {}",question);
    }

    @Test
    public void testJianDa() throws IOException {

        String single = "{\"body\":\"<p>简答题</p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"<p>简答题答案</p>\"],\"analysis\":\"<p>内容解析</p>\",\"questions\":[]}";
        SingleQuestion question = JsonUtils.fromJson(single, SingleQuestion.class);
        logger.info("简答题pojo={}",question.toString());
    }
    //-------------------复合题---------------------------------


    @Test
    public void testXianDaiWenYueDu() throws IOException {

        String complex = "{\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":3,\"name\":\"判断题\"},\"body\":\"<p>判断</p>\",\"options\":[],\"answer\":[\"1\"],\"analysis\":\"<p>艾斯德斯</p>\",\"difficulty\":\"2\"}],\"material\":\"<p>现代文阅读</p>\"}";
        ComplexQuestion complexQuestion = JsonUtils.fromJson(complex, ComplexQuestion.class);

        List<Question> questions = complexQuestion.getQuestions();
        for (Question question:questions){
            logger.info("-=-=-=-=-=-=-=-=-=-=-={}",question);
        }
    }

    @Test
    public void testWenYanWenYueDuQuestionLength() throws Exception {

        String complex = "{\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题</p>\",\"options\":[\"<p>撒范德萨a<br/></p>\",\"<p>&nbsp;阿斯顿发多少</p>\",\"<p>&nbsp;发的所发生的</p>\",\"<p>&nbsp;阿斯顿发多少</p>\"],\"answer\":[\"A\",\"B\"],\"analysis\":\"<p>发第三方</p>\",\"difficulty\":\"1\"},{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题2</p>\",\"options\":[\"<p>法第三方</p>\",\"<p>&nbsp;啥地方都是</p>\",\"<p>&nbsp;大发 的范德萨</p>\",\"<p>&nbsp;都是法第三方都是</p>\"],\"answer\":[\"A\",\"C\",\"D\"],\"analysis\":\"<p>&nbsp;阿萨德十大</p>\",\"difficulty\":\"2\"},{\"type\":{\"id\":2,\"name\":\"填空题\"},\"body\":\"<p>&nbsp;发多少发多少<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;十大范德萨方法发</p>\"],\"analysis\":\"<p>&nbsp;水电费都是发多少</p>\",\"difficulty\":\"3\"},{\"type\":{\"id\":3,\"name\":\"判断题\"},\"body\":\"<p>判断题</p>\",\"options\":[],\"answer\":[\"0\"],\"analysis\":\"<p>水电费大</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>发发大烦死哒</p>\"],\"analysis\":\"<p>&nbsp;撒地方第三方发</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;都是发多少发</p>\"],\"analysis\":\"<p>撒地方爱迪生</p>\",\"difficulty\":\"1\"}],\"material\":\"<p>文言文阅读</p>\"}";




        int questions = JsonUtils.getQuestionsLength(complex, "questions");
        logger.info("长度为：==={}",questions);

        ComplexQuestion complexQuestion = JsonUtils.fromJson(complex, ComplexQuestion.class);
        logger.info("文言文阅读pojo={}",complexQuestion);
    }



    @Test
    public void testWenYanWenYueDu() throws Exception {

        String complex = "{\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题</p>\",\"options\":[\"<p>撒范德萨a<br/></p>\",\"<p>&nbsp;阿斯顿发多少</p>\",\"<p>&nbsp;发的所发生的</p>\",\"<p>&nbsp;阿斯顿发多少</p>\"],\"answer\":[\"A\",\"B\"],\"analysis\":\"<p>发第三方</p>\",\"difficulty\":\"1\"},{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>选择题2</p>\",\"options\":[\"<p>法第三方</p>\",\"<p>&nbsp;啥地方都是</p>\",\"<p>&nbsp;大发 的范德萨</p>\",\"<p>&nbsp;都是法第三方都是</p>\"],\"answer\":[\"A\",\"C\",\"D\"],\"analysis\":\"<p>&nbsp;阿萨德十大</p>\",\"difficulty\":\"2\"},{\"type\":{\"id\":2,\"name\":\"填空题\"},\"body\":\"<p>&nbsp;发多少发多少<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/>&nbsp;</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;十大范德萨方法发</p>\"],\"analysis\":\"<p>&nbsp;水电费都是发多少</p>\",\"difficulty\":\"3\"},{\"type\":{\"id\":3,\"name\":\"判断题\"},\"body\":\"<p>判断题</p>\",\"options\":[],\"answer\":[\"0\"],\"analysis\":\"<p>水电费大</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>发发大烦死哒</p>\"],\"analysis\":\"<p>&nbsp;撒地方第三方发</p>\",\"difficulty\":\"4\"},{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>简答题</p>\",\"options\":[],\"answer\":[\"<p>&nbsp;都是发多少发</p>\"],\"analysis\":\"<p>撒地方爱迪生</p>\",\"difficulty\":\"1\"}],\"material\":\"<p>文言文阅读</p>\"}";

        ComplexQuestion complexQuestion = JsonUtils.fromJson(complex, ComplexQuestion.class);
        logger.info("文言文阅读pojo={}",complexQuestion);
    }



    @Test
    public void testYueDuLiJie(){
        String complex="{\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"map\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"questions\":[{\"type\":{\"id\":4,\"name\":\"简答题\"},\"body\":\"<p>而非</p>\",\"options\":[],\"answer\":\"<p>丹丹</p>\",\"analysis\":\"<p>的地方大幅度</p>\",\"difficulty\":\"2\"},{\"type\":{\"id\":3,\"name\":\"判断题\"},\"body\":\"<p>df&#39;d</p>\",\"options\":[],\"answer\":[\"0\"],\"analysis\":\"<p>地方地方大幅度发</p>\",\"difficulty\":\"2\"},{\"type\":{\"id\":1,\"name\":\"选择题\"},\"body\":\"<p>地方</p>\",\"options\":[\"<p>地方</p>\",\"<p>地方</p>\",\"<p>的d</p>\",\"<p>的</p>\"],\"answer\":[\"D\"],\"analysis\":\"<p>&nbsp;地方d</p>\",\"difficulty\":\"2\"}],\"material\":\"<p>的DVD发</p>\"}";

        ComplexQuestion question = null;
        try {
            question = JsonUtils.fromJson(complex, ComplexQuestion.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("question={}",question);



    }


    @Test
    public void testParseHtml(){

        String str = "<p>&nbsp;水电费都是发多少</p>";

        Document parse = Jsoup.parse(str);
        String p = parse.tagName("p").text();
        logger.info("p====={}",p);

    }



    @Test
    public void testLength(){

        int questions = JsonUtils.getQuestionsLength("{\"body\":\"<p>1<input type=\\\"text\\\" readonly=\\\"true\\\" class=\\\"questions-blank\\\" value=\\\"1\\\" contenteditable=\\\"true\\\"/> </p>\",\"audio\":{\"url\":\"\",\"name\":\"\",\"size\":\"\"},\"answer\":[\"<p>12321</p>\"],\"analysis\":\"<p>2</p>\",\"questions\":[]}", "questions");
        System.out.println(questions);

    }



    @Test
    public void testConverion() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        EntityQuestion entityQuestion = new EntityQuestion();
        entityQuestion.setId(1000L);
        QuestionDocument questionDocument = new QuestionDocument();
        questionDocument.setId(entityQuestion.getId());
        questionDocument.setVisible(0);

        Map<String,Object> map = new HashMap<>();

        EntityUtils.copyValueByGetter(questionDocument,map);
        System.out.println(map);
    }



    @Test
    public void testpojo(){

        EntityQuestion entityQuestion = new EntityQuestion();

        QuestionState enabled = entityQuestion.getState().ENABLED;
        entityQuestion.setState(enabled);

        System.out.println(QuestionState.ENABLED.equals(entityQuestion.getState()));

    }


    @Test
    public void testCollections(){

        List<Long> chapterIds = new ArrayList<>();
        chapterIds.add(100L);
        chapterIds.add(200L);

        List<List<Long>> lists = Collections.singletonList(chapterIds);
        System.out.println(lists);
    }

}
