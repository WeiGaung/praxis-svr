package com.noriental.praxissvr.question.mapper;

import com.noriental.BaseTest;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.bean.EntityTeachingChapter;
import com.noriental.praxissvr.question.bean.QuestionType;
import com.noriental.praxissvr.question.dao.QuestionTypeDao;
import com.noriental.praxissvr.question.mapper.EntityQuestionMapper;
import com.noriental.praxissvr.question.mapper.TeacherChapterMapper;
import com.noriental.praxissvr.question.utils.Constants;
import com.noriental.praxissvr.question.utils.JsonUtils;
import com.noriental.praxissvr.question.utils.ParseHtmlUtil;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.validate.exception.BizLayerException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.noriental.praxissvr.exception.PraxisErrorCode.PRAXIS_QUESTION_TYPE_NOT_FOUND;
import static com.noriental.praxissvr.exception.PraxisErrorCode.UPDATE_SOLR_FAIL;

/**
 * Created by hushuang on 2016/11/30.
 */
public class EntityQuestionMapperTest extends BaseTest{


    @Resource
    private EntityQuestionMapper entityQuestionMapper;
    @Resource
    private TeacherChapterMapper teacherChapterMapper;
    @Resource
    private RabbitTemplate solrUploadQuestionRabbitTemplate;
    @Resource
    private AuditsedSchoolMapper auditsedSchoolMapper;
    @Resource
    private QuestionTypeDao questionTypeDao;



    @Test
    public void testAuditsedSchoolMapper(){
        List<Long> schoolIds= auditsedSchoolMapper.queryAuditsedSchoolList();
        System.out.println("白名单学校是："+schoolIds.toString());
    }

     /*
        更新数据库错误题目 54  55 两个题型
        更新solr 根据数据库查询出来的题目ID 更新solr数据
     */

    @Test
    public void testUpdateDBandsolr() throws JSONException, IOException {

        /*
            1.根据题型查询出题目列表
            2.根据题目列表更新数据库数据
            3.更新Solr数据
         */
        List<EntityQuestion> questions = entityQuestionMapper.getQuestionList(54, 55);

        for (EntityQuestion question : questions) {

            String htmlData = question.getHtmlData();



            JSONObject html = new JSONObject(htmlData);

            if(!JsonUtils.is_key(htmlData,"article")){


                System.out.println("题目ID 为："+question.toString());

                Object content = html.get("content");


                String html2json = ParseHtmlUtil.html2json(content + "", question.getQuestionTypeId()+"", question.getId(),questionTypeDao);

                EntityQuestion entityQuestion = new EntityQuestion();
                entityQuestion.setId(question.getId());
                entityQuestion.setJsonData(html2json);
                entityQuestionMapper.updateQuestion(entityQuestion);

                //更新solr
                Map<String, Object> mapBody = new HashMap<>();
                mapBody.put("requestId", TraceKeyHolder.getTraceKey());
                mapBody.put("id", entityQuestion.getId());
                mapBody.put("jsonData", entityQuestion.getJsonData());
                try {
                    mapBody.put(Constants._DOC_CLASS_NAME, QuestionDocument.class.getName());
                    SolrIndexReqMsg msg = new SolrIndexReqMsg(mapBody);
                    msg.setRequestId(TraceKeyHolder.getTraceKey());
                    solrUploadQuestionRabbitTemplate.convertAndSend(msg);
                } catch (Exception e) {
                }
            }

        }
    }


    @Test
    public void testgetQuestionList(){

        List<EntityQuestion> questionList = entityQuestionMapper.getQuestionList(54, 55);
        for (EntityQuestion entityQuestion : questionList) {
            System.out.println("数据为:"+entityQuestion);
        }

    }


    @Test
    public void testUpdateAudioData(){

        Map<String,Object> map = new HashMap<>();
        map.put("id",4052705);
        map.put("url","http://aa.mp3");
        map.put("name","听力名称");
        map.put("size","100K");
        int i = entityQuestionMapper.updateAudioData(map);
        System.out.println("影响行数："+i);
    }

    @Test
    public void testFindChaptersById(){
        List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(120L);
        System.out.println("========"+teachingChapters.toString());
    }

    @Test
    public void batchQueryQuestionsByIds(){
        List<Long> ids = new ArrayList<>();
        ids.add(5874969L);
        ids.add(356028L);
        ids.add(356027L);
        ids.add(356026L);
        ids.add(356025L);

        List<EntityQuestion> questions = entityQuestionMapper.batchQueryQuestionsByIds(ids);
        System.out.println(questions);
    }


    @Test
    public void batchUpdateQuestionStatusDISABLETest(){

        List<Long> questionIds = new ArrayList<>();
        questionIds.add(4003699L);
        questionIds.add(4003698L);
        questionIds.add(4003697L);
        int i = entityQuestionMapper.batchUpdateQuestionStatusDISABLE(questionIds);
        System.out.println("================================="+i);

    }

    @Test
    public void batchUpdateQuestionStatusABLETest(){

        List<Long> questionIds = new ArrayList<>();
        questionIds.add(4003699L);
        questionIds.add(4003698L);
        questionIds.add(4003697L);
        int i = entityQuestionMapper.batchUpdateQuestionStatusABLE(questionIds);
        System.out.println("================================="+i);

    }

    @Test
    public void findQuestionSubjIdByParentId(){
        List<Long> list = entityQuestionMapper.findQuestionSubjIdByParentId(4002217L);
        System.out.println(list);
    }


    @Test
    public void findQuestinByUploadIdAndGroupId(){
        List<EntityQuestion> entityQuestions = entityQuestionMapper.findQuestinByUploadIdAndGroupId(6106328L, 0L);
        System.out.println(entityQuestions);
    }



    @Test
    public void testFindEntityQuestion1() {
        EntityQuestion question = entityQuestionMapper.findQuestionById(4052722L);
        System.out.println("\n=中文"+question.getJsonData());
    }



    @Test
    public void testUpdateQuestion(){

        EntityQuestion entity = new EntityQuestion();

        entity.setId(4168729L);
        entity.setJsonMap("{\"name\":\"607FDEEC-CAFC-46CB-8366-3954B39BF5CD.png\",\"size\":95907,\"url\":\"//rc.okjiaoyu.cn/rc_QTOmsr7OIo.png\"}");
        int i = entityQuestionMapper.updateQuestion(entity);
        System.out.println("i<><><<><><><><><<><><><><>><>><<><>><><><====:"+i);

    }


    @Test
    public void testDeleteSubjectQuestionById(){

        int i = entityQuestionMapper.deleteSubjectQuestionById(4000018L);
        System.out.println("========i="+i);

    }


    /**
     * 批量插入测试
     */
    @Test
    public void testBatchInsertQuestion(){

        List<EntityQuestion> entityQuestionList = new ArrayList<EntityQuestion>();

        EntityQuestion entity = new EntityQuestion();

        entity.setId(4L);
        entity.setCountOptions(4);
        entity.setDifficulty(2);
        entity.setHighQual(0);
        entity.setQuestionType("选择题");
        entity.setState(QuestionState.PREVIEWED);
        entity.setNewFormat(0);
        entity.setAnswerNum(0);
        entity.setQuestionTypeId(1);
        entity.setVisible(1);
        entity.setScore(0f);
        entity.setIntelligent(1);
        entity.setJsonMap("{\"name\":\"607FDEEC-CAFC-46CB-8366-3954B39BF5CD.png\",\"size\":95907,\"url\":\"//rc.okjiaoyu.cn/rc_QTOmsr7OIo.png\"}");
        entityQuestionList.add(entity);



        EntityQuestion entity1 = new EntityQuestion();

        entity1.setId(5L);
        entity1.setCountOptions(5);
        entity1.setDifficulty(2);
        entity1.setHighQual(0);
        entity1.setQuestionType("填空题");
        entity1.setState(QuestionState.PREVIEWED);
        entity1.setNewFormat(0);
        entity1.setAnswerNum(0);
        entity1.setQuestionTypeId(1);
        entity1.setVisible(1);
        entity1.setScore(0f);
        entity1.setJsonMap("{\"name\":\"607FDEEC-CAFC-46CB-8366-3954B39BF5CD.png\",\"size\":95907,\"url\":\"//rc.okjiaoyu.cn/rc_QTOmsr7OIo.png\"}");
        entityQuestionList.add(entity1);


        EntityQuestion entity2 = new EntityQuestion();

        entity2.setId(6L);
        entity2.setCountOptions(6);
        entity2.setDifficulty(2);
        entity2.setHighQual(0);
        entity2.setQuestionType("选择题");
        entity2.setState(QuestionState.PREVIEWED);
        entity2.setNewFormat(0);
        entity2.setAnswerNum(0);
        entity2.setQuestionTypeId(1);
        entity2.setVisible(2);
        entity2.setScore(0f);
        entity2.setJsonMap("{\"name\":\"607FDEEC-CAFC-46CB-8366-3954B39BF5CD.png\",\"size\":95907,\"url\":\"//rc.okjiaoyu.cn/rc_QTOmsr7OIo.png\"}");
        entityQuestionList.add(entity2);

        int i = entityQuestionMapper.batchInsertQuestion(entityQuestionList);
        System.out.println("==-=-=-=-=-=<><><<><><)(_()*_*()_*(_*()_*(_*()_*()_*)("+i);
    }
}
