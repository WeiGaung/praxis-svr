package com.noriental.praxissvr.question.utils;

import com.alibaba.dubbo.common.json.JSON;
import com.noriental.adminsvr.bean.knowledge.Module;
import com.noriental.adminsvr.bean.knowledge.TopicWithParent;
import com.noriental.adminsvr.bean.knowledge.Unit;
import com.noriental.adminsvr.request.RequestEntity;
import com.noriental.adminsvr.response.ResponseEntity;
import com.noriental.adminsvr.service.knowledge.TopicService;
import com.noriental.extresources.request.ConvertMp3Request;
import com.noriental.extresources.service.QiniuVoiceService;
import com.noriental.log.TraceKeyHolder;
import com.noriental.praxissvr.answer.util.RedisKeyUtil;
import com.noriental.praxissvr.common.QuestionState;
import com.noriental.praxissvr.common.QuestionTypeEnum;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.question.bean.CustomQuestionResource;
import com.noriental.praxissvr.question.bean.EntityQuestion;
import com.noriental.praxissvr.question.bean.EntityTeachingChapter;
import com.noriental.praxissvr.question.bean.LinkExerciseQuestion;
import com.noriental.praxissvr.question.bean.html.Audio;
import com.noriental.praxissvr.question.bean.html.ComplexQuestion;
import com.noriental.praxissvr.question.bean.html.NewMap;
import com.noriental.praxissvr.question.bean.html.SingleQuestion;
import com.noriental.praxissvr.question.mapper.*;
import com.noriental.praxissvr.question.request.UploadQuestionRequest;
import com.noriental.publicsvr.bean.SeqNextIdListReq;
import com.noriental.publicsvr.service.SequenceService;
import com.noriental.resourcesvr.common.request.IdRequest;
import com.noriental.resourcesvr.customlist.service.CustomListService;
import com.noriental.resourcesvr.customlist.vo.CustomListVo;
import com.noriental.solr.bean.doc.LqResourceDocument;
import com.noriental.solr.bean.doc.QuestionDocument;
import com.noriental.solr.common.msg.SolrIndexReqMsg;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.redis.RedisUtil;
import com.noriental.validate.bean.CommonResponse;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.noriental.praxissvr.exception.PraxisErrorCode.*;

/**
 * Created by kate on 2017/8/24.
 */
public class QuestionServiceUtil {
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceUtil.class);

    public static List<String> whiteRuleAllCompare;
    public static List<String> whiteRuleKeyCompare;
    //获取style中的内容
    public static String htmlStyleRegex="style\\s*=[\\s\\S*]\"(.*?)[\\s\\S*]\"";
    //是否有unicode标示
    public static String htmlUnicodeRegex="&#(x[0-9A-Za-z]{4}|[0-9]{4})";
    //是否中文
    private static String isChineseRegex="[\u4e00-\u9fa5]";
    //是否数字
    private static String isNumberRegex="^-?\\d+$";
    //是否英文
    private static String isEnglishRegex="[a-zA-Z]+";
    //去除字符串中的html标记
    private static String noHtmlRegex = "<[^>]+>";

    static{
        whiteRuleAllCompare=new ArrayList<String>(){};
        whiteRuleKeyCompare=new ArrayList<String>(){};
        whiteRuleAllCompare.add("text-decoration:underline");
        whiteRuleAllCompare.add("text-decoration:line-through");
        whiteRuleAllCompare.add("white-space:normal");


        whiteRuleKeyCompare.add("border");
        whiteRuleKeyCompare.add("border-bottom");
        whiteRuleKeyCompare.add("text-indent");
        whiteRuleKeyCompare.add("width");
        whiteRuleKeyCompare.add("height");
        whiteRuleKeyCompare.add("border-collapse");
        whiteRuleKeyCompare.add("text-decoration");
        whiteRuleKeyCompare.add("text-decoration-style");
        whiteRuleKeyCompare.add("-moz-text-decoration-style");
        whiteRuleKeyCompare.add("text-decoration-line");
        whiteRuleKeyCompare.add("text-decoration-color");
        whiteRuleKeyCompare.add("list-style-type");

        whiteRuleKeyCompare.add("word-break");
        whiteRuleKeyCompare.add("break-all");
    }



    public static boolean isWhiteSchool(AuditsedSchoolMapper auditsedSchoolMapper,Long orgId,RedisUtil redisUtil){
        List<Long> schools=new ArrayList<>();
        if(redisUtil.exists(RedisKeyUtil.GET_WIHTE_SCHOOLS)){
            schools=(List<Long>)redisUtil.get(RedisKeyUtil.GET_WIHTE_SCHOOLS);
        }
        if(CollectionUtils.isEmpty(schools)){
            schools=auditsedSchoolMapper.queryAuditsedSchoolList();
            if(CollectionUtils.isEmpty(schools)){
                return false;
            }
            redisUtil.set(RedisKeyUtil.GET_WIHTE_SCHOOLS,schools,1500);
        }
        if(schools.contains(orgId)){
            return true;
        }
        return false;
    }
    /**
     *
     * @param styles
     * @param whiteRuleKey
     * @return  true表示styles样式中存在问题样式，拒绝入库。
     */
    public static boolean checkWhiteRuleKey(List<String> styles,List<String> whiteRuleKey,List<String> whiteRuleAll){
        List<String> second=new ArrayList<>();
        if(styles.size()>0){
            //把全部匹配没有匹配上的白名单规则加入到second，准备进行key值比较
            for(String style:styles){
                //style可能长这样[width:58px;height:46px;, line-height:1em;, line-height:1em;]
                String[] childStyle= style.split(";");
                if(childStyle.length>0){
                    for (String child:childStyle){
                        //去除style样式里面包含的
                        child= child.replaceAll("\\s*", "");
                        boolean result=whiteRuleAll.contains(child);
                        if(result==false){
                            if(!org.apache.commons.lang3.StringUtils.isEmpty(child.split(":")[0])){
                                second.add(child.split(":")[0].toLowerCase());
                            }
                        }
                    }
                }
            }
            if(second.size()>0){
                for (String key:second){
                    boolean result=whiteRuleKey.contains(key);
                    if(!result){
                        logger.error("白名单校验失败,失败的原因是key:{}不在白名单内",key);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 正则表达式匹配两个指定字符串中间的内容
     * @param soap
     * @return
     */
    public static List<String> getSubUtil(String soap, String regex){
        List<String> list = new ArrayList<String>();
        // 匹配的模式
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(soap);
        while (m.find()) {
            int i = 1;
            list.add(m.group(i).replaceAll("\\s{1,}", "").toLowerCase());
            i++;
        }
        return list;
    }
    /**
     * 返回单个字符串，若匹配到多个的话就返回第一个，方法与getSubUtil一样
     * @param soap
     * @param rgex
     * @return
     */
    public static String getSubUtilSimple(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }
    //修改题目时，如果该题目属于一个题集，而且该题集被缓存了，需要在此处过期该缓存
    public static void delExerciseQuestionCache(List<LinkExerciseQuestion> exerciseQuestions, RedisUtil redisUtil) {
        for(LinkExerciseQuestion exerciseQuestion:exerciseQuestions){
            String cacheKey= RedisKeyUtil.makeKey(RedisKeyUtil.GET_PAD_QUESTIONS, exerciseQuestion.getExerciseId()+"");
            if(redisUtil.exists(cacheKey)){
                //如果已缓存，则过期已缓存的题集
                redisUtil.del(cacheKey);
            }
        }
    }
    /**
     * @param entityQuestion 参数
     * @param cusDirId        :自定义目录ID
     * @param link_cus_dir_id ：自定义目录关联表Id
     * @param stat            ：是否需要收藏 1：收藏  0：不需要收藏
     * @param customListService
     * @param solrUploadQuestionRabbitTemplate
     */

    public static void sendLinkQuestionIndex(EntityQuestion entityQuestion, Long cusDirId, Long link_cus_dir_id, Integer
            stat,CustomListService customListService,RabbitTemplate solrUploadQuestionRabbitTemplate) {

        LqResourceDocument lqResourceDocument = new LqResourceDocument();
        //自定义关联目录ID
        lqResourceDocument.setId(link_cus_dir_id);
        //当前用户ID
        lqResourceDocument.setSystemId(entityQuestion.getUploadId());
        //创建时间
        lqResourceDocument.setCreateTime(entityQuestion.getUploadTime());
        //更新时间
        lqResourceDocument.setUpdateTime(new Date());
        //题目ID
        lqResourceDocument.setQuestionId(entityQuestion.getId());
        //题目类型ID
        lqResourceDocument.setQuestionTypeId(Long.valueOf(entityQuestion.getQuestionTypeId()));
        //QuestionTypeEnum typeEnum = QuestionTypeEnum.getQuestionTypeByTypeId(entityQuestion.getQuestionTypeId());
        //题型
        //lqResourceDocument.setQuestionType(typeEnum.getSolrType());
        if(entityQuestion.getQuestionTypeId().equals(1)){
            lqResourceDocument.setQuestionType("选择题");
        }else{
            lqResourceDocument.setQuestionType(entityQuestion.getQuestionType());
        }

        //难易程度
        lqResourceDocument.setDifficulty(entityQuestion.getDifficulty());
        //是否是单题
        lqResourceDocument.setIsSingle(entityQuestion.getIsSingle());
        //自定义目录题目状态
        lqResourceDocument.setResourceStatus(1);
        //是否收藏
        lqResourceDocument.setIsFav(stat);
        //题目状态
        lqResourceDocument.setState(QuestionState.PREVIEWED.name());
        //答案个数
        lqResourceDocument.setAnswerNum(entityQuestion.getAnswerNum());
        lqResourceDocument.setVisible(entityQuestion.getVisible());
        lqResourceDocument.setSubjectId(entityQuestion.getSubjectId());
        lqResourceDocument.setNewFormat(entityQuestion.getNewFormat());

        /*
            1.自定义目录索引
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                //自定义目录体系ID
                lqResourceDocument.setGroupId(data.get(0).getGroupId());
                Long cusDirIds1 = null;
                Long cusDirIds2 = null;
                Long cusDirIds3 = null;
                String customListName1 = "";
                String customListName2 = "";
                String customListName3 = "";
                for (CustomListVo customListVo : data) {
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1 = customListVo.getId();
                        customListName1 = customListVo.getName();
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2 = customListVo.getId();
                        customListName2 = customListVo.getName();
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3 = customListVo.getId();
                        customListName3 = customListVo.getName();
                    }
                }
                lqResourceDocument.setCustomListId1(cusDirIds1);
                lqResourceDocument.setCustomListName1(customListName1);

                lqResourceDocument.setCustomListId2(cusDirIds2);
                lqResourceDocument.setCustomListName2(customListName2);

                lqResourceDocument.setCustomListId3(cusDirIds3);
                lqResourceDocument.setCustomListName3(customListName3);

                lqResourceDocument.setCustomListId(cusDirId);
            }
        }

        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(lqResourceDocument);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("发送solr关联关系文档请求ID{}", solrIndexReqMsg.getRequestId());
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);
    }

    /**
     * 根据题目答案和题型ID，判断题目是否支持智批
     * @param answerObjlist 题目答案字符串
     * @param questionTypeId 题目类型ID
     * @param subjectId 题目所属学科
     * @return
     */
    public static int getIntelligent(List<Object> answerObjlist,int questionTypeId,Long subjectId){
        if(questionTypeId==2||questionTypeId==25||questionTypeId==27||questionTypeId==29||questionTypeId==34||questionTypeId==36||questionTypeId==45||questionTypeId==46||questionTypeId==47){
            //先去除html标签，进行是否纯中文，数字，英文的判断（忽视空格）,另外去除[]括号。
            List<String> answerStringList=new ArrayList<>();
            int listLength=0;
            if(answerObjlist.size()>0){
                for (Object answerObj:answerObjlist){
                    if(answerObj!=null){
                        answerStringList.add(answerObj.toString());
                    }
                }
                for(String answerString:answerStringList){
                    listLength++;
                    //unescapeHtml4 把<>等反编码；encode3替换里面的特殊字符（除却<> 因为这个到下一步要替换html标签的时候要用）；getNoHtmlIndexString 替换所有html标签内容；然后把替换后的字符串去空格
                    String noHtmlAnswerStr=getNoHtmlIndexString(encode3(StringEscapeUtils.unescapeHtml4(answerString)),noHtmlRegex).replace("<","").replace(">","").replace(" ","").replaceAll("\\s*", "");
                    if(isIllegalStr(noHtmlAnswerStr,isChineseRegex)||isIllegalStr(noHtmlAnswerStr,isNumberRegex)||isIllegalStr(noHtmlAnswerStr,isEnglishRegex)){
                        if(noHtmlAnswerStr.equals("")||noHtmlAnswerStr.equals("无")||noHtmlAnswerStr.equals("略")||noHtmlAnswerStr.equals("暂无")){
                            return 0;
                        }
                        if(listLength!=answerStringList.size()){
                            continue;
                        }
                        return 1;
                    }else{
                        return 0;
                    }
                }
            }
        }else if(questionTypeId==57||questionTypeId==60){
            //听口题型支持智批的题型
            return 1;
        }else if(null != subjectId && questionTypeId == 50 &&
                (subjectId.toString().equals("5") || subjectId.toString().equals("6") || subjectId.toString().equals("21"))){
            //英语作文题 支持智批 questionTypeId=50 作文题,subjectId=5,6,21 初中英语 高中英语 小学英语
            return 1;
        }
        return 0;
    }

    /***
     *根据正则条件判断字符串是否符合条件（会先去除空格），如果符合条件就返回true
     * @param str
     * @param regex
     * @return
     */
    public static boolean isIllegalStr(String str,String regex){

        Pattern pattern = Pattern.compile(regex);
        char c[] = str.toCharArray();
        for(int i=0;i<c.length;i++){
            if(StringUtils.isEmpty(String.valueOf(c[i]).trim())){
                continue;
            }
            Matcher matcher = pattern.matcher(String.valueOf(c[i]));
            if(!matcher.matches()){
                return false;
            }
        }
        return true;
    }

    /**
     * 根据正则条件去除字符串中的相应字符。
     * @param str   待处理的字符串
     * @param regex     条件正则
     * @return
     */
    public static String getNoHtmlIndexString(String str,String regex){
        Pattern p_space = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(str);
        return m_space.replaceAll("").replace("&nbsp;","");
    }

    /**
     * 替换字符串中的字符，循环一遍
     * @param str
     * @return
     */
    public static String encode3(String str) {
        char[] array = str.toCharArray();
        for (int i = 0, length = array.length; i < length; i++) {
            if(array[i] == '、') {
                array[i] = ' ';
            } else if(array[i] == '，') {
                array[i] = ' ';
            } else if(array[i] == '．') {
                array[i] = ' ';
            } else if(array[i] == '。') {
                array[i] = ' ';
            } else if(array[i] == '？') {
                array[i] = ' ';
            } else if(array[i] == '！') {
                array[i] = ' ';
            } else if(array[i] == '：') {
                array[i] = ' ';
            } else if(array[i] == '；') {
                array[i] = ' ';
            } else if(array[i] == '…') {
                array[i] = ' ';
            } else if(array[i] == '.') {
                array[i] = ' ';
            } else if(array[i] == '“') {
                array[i] = ' ';
            } else if(array[i] == '”') {
                array[i] = ' ';
            } else if(array[i] == '‘') {
                array[i] = ' ';
            } else if(array[i] == '’') {
                array[i] = ' ';
            } else if(array[i] == '＂') {
                array[i] = ' ';
            } else if(array[i] == '＇') {
                array[i] = ' ';
            } else if(array[i] == '（') {
                array[i] = ' ';
            } else if(array[i] == '）') {
                array[i] = ' ';
            } else if(array[i] == '＜') {
                array[i] = ' ';
            } else if(array[i] == '＞') {
                array[i] = ' ';
            } else if(array[i] == '～') {
                array[i] = ' ';
            } else if(array[i] == '－') {
                array[i] = ' ';
            } else if(array[i] == '＋') {
                array[i] = ' ';
            } else if(array[i] == '＝') {
                array[i] = ' ';
            } else if(array[i] == '《') {
                array[i] = ' ';
            } else if(array[i] == '》') {
                array[i] = ' ';
            } else if(array[i] == ',') {
                array[i] = ' ';
            } else if(array[i] == '?') {
                array[i] = ' ';
            } else if(array[i] == '!') {
                array[i] = ' ';
            } else if(array[i] == ':') {
                array[i] = ' ';
            } else if(array[i] == ';') {
                array[i] = ' ';
            } else if(array[i] == '"') {
                array[i] = ' ';
            } else if(array[i] == '\'') {
                array[i] = ' ';
            } else if(array[i] == '(') {
                array[i] = ' ';
            } else if(array[i] == ')') {
                array[i] = ' ';
            } else if(array[i] == '~') {
                array[i] = ' ';
            } else if(array[i] == '+') {
                array[i] = ' ';
            } else if(array[i] == '=') {
                array[i] = ' ';
            } else if(array[i] == '⋯') {
                array[i] = ' ';
            } else if(array[i] == '〜') {
                array[i] = ' ';
            }
        }
        return new String(array);
    }
    /***
     *  5.七牛回调听力题/
     * @param question
     * @param type
     * @param html
     * @param qiniuVoiceService
     * @param is_single
     */
    public static void qiniuCallBack(EntityQuestion question, QuestionTypeEnum type, String html, QiniuVoiceService
            qiniuVoiceService, int is_single) {
        if (49 == type.getTypeId()) {

            logger.info("===音频调用==={},是否是单题=={}", type.getTypeId(), is_single);

            if (is_single == 1) { //如果是单题
                logger.info("单题七牛回调音频触发成功:{}", is_single);
                SingleQuestion sinQuestion = null;
                try {
                    sinQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
                } catch (IOException e) {
                    logger.error("七牛回调单题JSON转换失败{}", e);
                    e.printStackTrace();
                }
                if (sinQuestion != null && sinQuestion.getAudio() != null) {
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(question.getId() + "");
                    convertMp3Request.setAudioUrl(sinQuestion.getAudio().getUrl());
                    CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                    logger.info("单题七牛回调音频触发成功:{}", convertMp3.getData());
                }
            } else {
                logger.info("复合题七牛回调音频触发成功:{}", is_single);
                ComplexQuestion complexQuestion = null;
                try {
                    complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
                } catch (IOException e) {
                    logger.info("七牛回调复合题JSON转换失败{}", html);
                    e.printStackTrace();
                }
                logger.info("触发转换{}", complexQuestion);
                if (complexQuestion != null && complexQuestion.getAudio() != null) {
                    ConvertMp3Request convertMp3Request = new ConvertMp3Request();
                    convertMp3Request.setEntityId(question.getId() + "");
                    convertMp3Request.setAudioUrl(complexQuestion.getAudio().getUrl());
                    CommonResponse<String> convertMp3 = qiniuVoiceService.convertMp3(convertMp3Request);
                    logger.info("复合题七牛回调音频触发成功:{}", convertMp3.getData());
                }
            }
        }
    }

    /***
     * 习题上传对html数据验证
     * @param request
     */
    public static void validateHtmlData(UploadQuestionRequest request) {
        // JSONObject json = null;
        try {
            new JSONObject(request.getHtml());
        } catch (JSONException e) {
            logger.error("读入html异常数据为:{}", request.getHtml());
            throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
        }
    }





    /***
     * 对听口题的html数据做特殊处理
     *  口语文章朗读 题型54
     *  需要对html进行转换
     *  添加answer字段
     *  添加third_party_use字段
     * @param request
     * @return
     */
    public static void getSpokenQuestionHtml(UploadQuestionRequest request, String html) {
        //String html = "";
        if (request.getType() == 54) {
            JSONObject json = null;
            try {
                json = new JSONObject(html);
            } catch (JSONException e) {
                logger.error("读入html异常数据为:{}", request.getHtml());
                throw new BizLayerException("", PraxisErrorCode.QUESTION_FORMAT_ERROR);
            }
            if (json != null) {
                String prompt = null;
                try {
                    prompt = json.getString("prompt");
                } catch (JSONException e) {
                    logger.error("获取prompt数据异常html数据为 :{}", request.getHtml());
                    e.printStackTrace();
                }

                if (StringUtils.isNotEmpty(prompt)) {
                    /*
                        在这根据prompt字段拼装answer数据和third_party_use 数据
                        answer数据的html格式为 [{answer_audio:{"url":"","name":"","size":""}},
                        "answer_content":"<p>aaabbbccc</p>"]
                        third_party_use的数据格式为  "aabbcddcdd"

                     */
                    try {
                        json.put("third_party_use", prompt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    List<Object> answer = new ArrayList<>();
                    Map<String, Object> answer_audio_content = new HashMap<>();
                    if (JsonUtils.is_key(request.getHtml(), "audio")) {
                        Audio audio = null;
                        try {
                            try {
                                audio = JsonUtils.fromJson(json.get("audio") + "", Audio.class);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        answer_audio_content.put("answer_audio", audio);
                    }
                    String content = "<p>" + prompt + "</p>";
                    answer_audio_content.put("answer_content", content);
                    answer.add(answer_audio_content);
                    try {
                        json.put("answer", answer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                html = json.toString();
            }
        }
    }

    /**
     * 对音频url处理
     *
     * @param request
     * @param html
     * @return
     */
    public static void getRadioQuestionHtml(UploadQuestionRequest request, String html) {

        if (request.getType() == 49) {
            JSONObject json = null;
            try {
                json = new JSONObject(html);
                JSONObject audio = json.getJSONObject("audio");
                audio.put("url", audio.getString("url").trim());
                json.put("audio", audio);
            } catch (JSONException e) {
                logger.error("读入html异常数据为:{}", request.getHtml());
                e.printStackTrace();
            }
            html = json == null ? "" : json.toString();
        }
    }


    /***
     * 创建自定义目录
     * @param linkCustomQuestionResourceMapper
     * @param request
     * @param entityQuestionMapper
     * @param question
     */
    public static void createCustomQuestionResource(LinkCustomQuestionResourceMapper
                                                            linkCustomQuestionResourceMapper, UploadQuestionRequest
            request, EntityQuestionMapper entityQuestionMapper, EntityQuestion question, RabbitTemplate
            solrUploadQuestionRabbitTemplate, CustomListService customListService) {
        Long customerDirectory = request.getCustomerDirectory();
        Long uploadId = request.getUploadId();

        Long groupId = request.getGroupId();
        CustomQuestionResource customQuestionResource = new CustomQuestionResource();
        customQuestionResource.setSystemId(uploadId);
        customQuestionResource.setCreateTime(new Date());
        customQuestionResource.setCustomListId(customerDirectory);
        customQuestionResource.setQuestionId(question.getId());
        customQuestionResource.setGroupId(groupId);
        customQuestionResource.setIsFav(0);
        customQuestionResource.setResourceStatus(1);
        int resource = linkCustomQuestionResourceMapper.insertLinkCustomQuestionResource(customQuestionResource);
        if (resource <= 0) {
            logger.info("插入自定义目录失败，入参：{}", customerDirectory);
            throw new BizLayerException("", INSERT_DIRECTORY_FAIL);
        }
        logger.info("\n <><>question{},\n cirectory{},\n QuestionResource{}", question, customerDirectory,
                customQuestionResource);
        sendLinkQuestionIndex(question, customerDirectory, customQuestionResource.getId(), 0,
                solrUploadQuestionRabbitTemplate, customListService);

        int i = entityQuestionMapper.createQuestion(question);
        if (i <= 0) {
            throw new BizLayerException("", QUESTION_CREATE_FAIL);
        }


    }

    /***
     * 向solr发送自定义目录信息
     * @param entityQuestion
     * @param cusDirId
     * @param link_cus_dir_id
     * @param stat
     * @param solrUploadQuestionRabbitTemplate
     * @param customListService
     */

    public static void sendLinkQuestionIndex(EntityQuestion entityQuestion, Long cusDirId, Long link_cus_dir_id,
                                             Integer stat, RabbitTemplate solrUploadQuestionRabbitTemplate,
                                             CustomListService customListService) {

        LqResourceDocument lqResourceDocument = new LqResourceDocument();
        //自定义关联目录ID
        lqResourceDocument.setId(link_cus_dir_id);
        //当前用户ID
        lqResourceDocument.setSystemId(entityQuestion.getUploadId());
        //创建时间
        lqResourceDocument.setCreateTime(entityQuestion.getUploadTime());
        //更新时间
        lqResourceDocument.setUpdateTime(new Date());
        //题目ID
        lqResourceDocument.setQuestionId(entityQuestion.getId());
        //题目类型ID
        lqResourceDocument.setQuestionTypeId(Long.valueOf(entityQuestion.getQuestionTypeId()));
        QuestionTypeEnum typeEnum = QuestionTypeEnum.getQuestionTypeByTypeId(entityQuestion.getQuestionTypeId());
        //题型
        lqResourceDocument.setQuestionType(typeEnum.getSolrType());
        //难易程度
        lqResourceDocument.setDifficulty(entityQuestion.getDifficulty());
        //是否是单题
        lqResourceDocument.setIsSingle(entityQuestion.getIsSingle());
        //自定义目录题目状态
        lqResourceDocument.setResourceStatus(1);
        //是否收藏
        lqResourceDocument.setIsFav(stat);
        //题目状态
        lqResourceDocument.setState(QuestionState.PREVIEWED.name());
        //答案个数
        lqResourceDocument.setAnswerNum(entityQuestion.getAnswerNum());

        /*
            1.自定义目录索引
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                //自定义目录体系ID
                lqResourceDocument.setGroupId(data.get(0).getGroupId());
                Long cusDirIds1 = null;
                Long cusDirIds2 = null;
                Long cusDirIds3 = null;
                String customListName1 = "";
                String customListName2 = "";
                String customListName3 = "";
                for (CustomListVo customListVo : data) {
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1 = customListVo.getId();
                        customListName1 = customListVo.getName();
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2 = customListVo.getId();
                        customListName2 = customListVo.getName();
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3 = customListVo.getId();
                        customListName3 = customListVo.getName();
                    }
                }
                lqResourceDocument.setCustomListId1(cusDirIds1);
                lqResourceDocument.setCustomListName1(customListName1);
                lqResourceDocument.setCustomListId2(cusDirIds2);
                lqResourceDocument.setCustomListName2(customListName2);
                lqResourceDocument.setCustomListId3(cusDirIds3);
                lqResourceDocument.setCustomListName3(customListName3);
                lqResourceDocument.setCustomListId(cusDirId);
            }
        }

        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(lqResourceDocument);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("发送solr关联关系文档请求ID{}", solrIndexReqMsg.getRequestId());
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);
    }



    public static List<Long> getBatchQuestionId(int count, SequenceService sequenceService) {
        SeqNextIdListReq seqNextIdListReq = new SeqNextIdListReq();
        seqNextIdListReq.setSequenceType("OKAY_QUESTION_ID");
        seqNextIdListReq.setIdSize(count);
        CommonResponse<List<Long>> idList = sequenceService.getNextIdList(seqNextIdListReq);
        return idList.getData();
    }

    /***
     * 创建复合题题目
     * @param is_single
     * @param question
     * @param html
     * @param questionIds
     * @param request
     * @param entityQuestionMapper
     */
    public static void createComplexQuestion(int is_single, EntityQuestion question, String html, List<Long>
            questionIds, UploadQuestionRequest request, EntityQuestionMapper entityQuestionMapper) {
        if (0 == is_single) { //如果是复合题
            Long subjectId = request.getSubjectId();
            Long uploadId = request.getUploadId();
            Long orgId = request.getOrgId();
            Integer orgType = request.getOrgType();
            ComplexQuestion complexQuestion;
            try {
                complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
            } catch (IOException e) {
                logger.debug("html转换复合题ComplexQuestion异常:{}", html);
                throw new BizLayerException("", JSON_CONVERT_FAIL);
            }
            if (complexQuestion == null) {
                throw new BizLayerException("复合题子题不能为空:{}", QUESTION_CREATE_FAIL);
            }
            List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
            //封装子题列表，用于批量插入
            List<EntityQuestion> subQuestionList = new ArrayList<>();
            for (int x = 1; x < questionIds.size(); x++) {
                EntityQuestion subQuestion = new EntityQuestion();
                subQuestion.setId(questionIds.get(x));
                //获取单个子题
                com.noriental.praxissvr.question.bean.html.Question sub_question_index = questions.get(x - 1);
                subQuestion.setQuestionTypeId(sub_question_index.getType().getId()); //题型ID
                if (sub_question_index.getType().getId() == 0 || sub_question_index.getType().getId() == 1) {
                    subQuestion.setRightOption(sub_question_index.getAnswer().toString()); //正确选项
                    subQuestion.setCountOptions(sub_question_index.getOptions().size());   //选项数量
                }
                if (sub_question_index.getAnswer() != null) {
                    subQuestion.setAnswerNum(sub_question_index.getAnswer().size()); //正确答案数量
                } else {
                    subQuestion.setAnswerNum(0); //正确答案数量
                }

                subQuestion.setQuestionType(sub_question_index.getType().getName()); //题目类型
                subQuestion.setQuestionTypeId(sub_question_index.getType().getId()); //题目类型ID
                subQuestion.setDifficulty(Integer.parseInt(sub_question_index.getDifficulty())); //子题的难易程度
                subQuestion.setIsSingle(1); //子题是单题
                subQuestion.setScore(0F); //题目分值
                subQuestion.setParentQuestionId(question.getId()); //子题的父类ID是大题的题目ID
                subQuestion.setState(QuestionState.PREVIEWED); //待审核
                subQuestion.setSubjectId(subjectId); //学科ID
                subQuestion.setUploadId(uploadId); //上传人ID
                subQuestion.setNewFormat(1); //是否是新题
                subQuestion.setOrgId(orgId); //平台ID
                subQuestion.setOrgType(orgType); //平台类型
                subQuestion.setVisible(1); //是否可见默认值是1
                subQuestion.setIntelligent(0); // 是否支持智能批改

                /**
                 * 如果为作图题
                 * 将底图信息取出来json化插入进数据库
                 */
                if (subQuestion.getQuestionTypeId() == 51) {
                    NewMap jsonMapImgae = sub_question_index.getMap();
                    try {
                        String json = JSON.json(jsonMapImgae);
                        subQuestion.setJsonMap(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                subQuestionList.add(subQuestion);
            }
            logger.info("\n 复合题批量插入数据为：{}", subQuestionList);
            int subI = entityQuestionMapper.batchInsertQuestion(subQuestionList);
            if (subI <= 0) {
                logger.error("创建题目复合题时，批量插入子题失败");
                throw new BizLayerException("", BATCH_INSERT_FAIL);
            }
        }
    }


    /***
     * 关联知识点插入，向solr推送全量信息
     * @param request
     * @param question
     * @param linkQuestionTopicMapper
     * @param linkQuestionChapterMapper
     * @param html
     * @param questionIds
     * @param customListService
     * @param topicService
     * @param teacherChapterMapper
     */
    public static void inserQuestionTopicLink(UploadQuestionRequest request, EntityQuestion question,
                                              LinkQuestionTopicMapper linkQuestionTopicMapper,
                                              LinkQuestionChapterMapper linkQuestionChapterMapper, String html,
                                              List<Long> questionIds, CustomListService customListService,
                                              TopicService topicService, TeacherChapterMapper teacherChapterMapper,
                                              RabbitTemplate solrUploadQuestionRabbitTemplate,Long customerDirectory) {
        List<Long> topicIds = request.getTopicIds();
        Long chapterId = request.getChapterId();
        /*Long customerDirectory = request.getCustomerDirectory();*/
          /*
            3.3 主题操作
               -->章节和知识点不能同时为空
               -->知识点不能超过十个
        */
        if ((topicIds == null && chapterId == null) || ((topicIds != null && topicIds.size() == 0) && chapterId ==
                null)) {
            throw new BizLayerException("知识点和章节不能同时为空", ANSWER_PARAMETER_ILL);
        }
        if (topicIds != null && topicIds.size() > 10) {
            throw new BizLayerException("", TOPIC_COUNT);
        }

        if (CollectionUtils.isNotEmpty(topicIds)) {
            List<Map<String, Object>> topicList = new ArrayList<>();
            for (Long topicId : topicIds) {
                Map<String, Object> mapTopic = new HashMap<>();
                mapTopic.put("questionId", question.getId());
                mapTopic.put("topicId", topicId);
                topicList.add(mapTopic);
            }
            //批量插入知识点
            int topicI = linkQuestionTopicMapper.batchQuestionTopicLink(topicList);
            if (topicI <= 0) {
                logger.error("知识点关联批量插入失败");
                throw new BizLayerException("知识点关联批量插入失败", BATCH_INSERT_FAIL);
            }
        }

        /*
            3.4 章节操作
         */
        if (chapterId != null && chapterId != 0) {
            int chapterI = linkQuestionChapterMapper.createLinkQuestionChapter(question.getId(), chapterId);
            if (chapterI <= 0) {
                throw new BizLayerException("章节插入失败", QUESTION_CREATE_FAIL);
            }
        }

        /*
            4. 发送MQ 创建Solr索引
         */
        MDC.put("id", TraceKeyHolder.getTraceKey());
        sendSolrDoc2Mq(question, html, chapterId, topicIds, questionIds, customerDirectory, customListService,
                topicService, teacherChapterMapper, solrUploadQuestionRabbitTemplate);
    }


    /**
     * 将转换后的solr实体发送给RabbitMQ
     *
     * @param entityQuestion entity
     */
    public static void sendSolrDoc2Mq(EntityQuestion entityQuestion, String html, Long chapterId, List<Long>
            topicIds, List<Long> ids, Long cusDirId, CustomListService customListService, TopicService topicService,
                                      TeacherChapterMapper teacherChapterMapper, RabbitTemplate
                                              solrUploadQuestionRabbitTemplate) {
        SolrIndexReqMsg solrIndexReqMsg ;
        if (1 == entityQuestion.getIsSingle()) {//单题
            solrIndexReqMsg = componentSolrDoc(entityQuestion, html, chapterId, topicIds, cusDirId,
                    customListService, topicService, teacherChapterMapper);
        } else {//复合题
            solrIndexReqMsg = SolrIndexReqMsgSubj(entityQuestion, html, chapterId, topicIds, ids, cusDirId,
                    customListService, topicService, teacherChapterMapper);
            logger.info("\n MQ发送复合题requestID:{} \n MQ发送复合题开始:{}", solrIndexReqMsg.getRequestId(), solrIndexReqMsg
                    .getBody());
        }
        solrUploadQuestionRabbitTemplate.convertAndSend(solrIndexReqMsg);
    }


    /***
     * 单题转换mq
     * @param entityQuestion question
     * @return solrMsg
     */
    public static SolrIndexReqMsg componentSolrDoc(EntityQuestion entityQuestion, String html, Long chapterId,
                                                   List<Long> topicIds, Long cusDirId, CustomListService
                                                           customListService, TopicService topicService,
                                                   TeacherChapterMapper teacherChapterMapper) {

        QuestionDocument questionTmpDocument = new QuestionDocument();
        questionTmpDocument.setId(entityQuestion.getId());
        questionTmpDocument.setCountOptions(entityQuestion.getCountOptions());
        questionTmpDocument.setDifficulty(entityQuestion.getDifficulty());
        questionTmpDocument.setHighQual(entityQuestion.getHighQual());
        questionTmpDocument.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmpDocument.setParentQuestionId(entityQuestion.getParentQuestionId());
        questionTmpDocument.setIsSingle(entityQuestion.getIsSingle());

        questionTmpDocument.setState(entityQuestion.getState().name());
        questionTmpDocument.setQrId(entityQuestion.getQrId());
        questionTmpDocument.setCountTopic(entityQuestion.getCountTopic());
        questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());
        //更新时间
        if (entityQuestion.getUpdateTime() != null) {
            questionTmpDocument.setUpdateTime(entityQuestion.getUpdateTime());
        }
        //机构ID
        questionTmpDocument.setOrgId(entityQuestion.getOrgId());
        questionTmpDocument.setOrgType(entityQuestion.getOrgType());
        //智能批改
        questionTmpDocument.setIntelligent(0);
        //上传时间
        if (null != entityQuestion.getUploadTime()) {
            questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
        }
        questionTmpDocument.setUploadId(entityQuestion.getUploadId());
        questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmpDocument.setHtmlData(entityQuestion.getHtmlData());
        questionTmpDocument.setJsonData(entityQuestion.getJsonData());
        SingleQuestion singleQuestion;
        try {
            singleQuestion = JsonUtils.fromJson(html, SingleQuestion.class);
            if (1 == entityQuestion.getQuestionTypeId()) {  //选择题
                questionTmpDocument.setQuestionType("选择题");
            } else {
                questionTmpDocument.setQuestionType(entityQuestion.getQuestionType());

            }
            if (singleQuestion.getAnswer() != null) {
                questionTmpDocument.setAnswerNum(singleQuestion.getAnswer().size());
            } else {
                questionTmpDocument.setAnswerNum(0);
            }

        } catch (IOException e) {
            logger.error("", e);
            throw new BizLayerException("", JSON_CONVERT_FAIL);
        }


        questionTmpDocument.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmpDocument.setVisible(1);
        questionTmpDocument.setSource(entityQuestion.getSource());
        questionTmpDocument.setNewFormat(1);

        /*
            作图题处理
         */
        if (entityQuestion.getQuestionTypeId() == 51) {
            if (StringUtils.isNotEmpty(entityQuestion.getJsonMap())) {
                questionTmpDocument.setJsonMap(entityQuestion.getJsonMap());
            }
        }

        /*
            自定义目录
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {

                List<Long> cusDirIds1 = new ArrayList<>();
                List<Long> cusDirIds2 = new ArrayList<>();
                List<Long> cusDirIds3 = new ArrayList<>();
                List<Long> cusDirIds = new ArrayList<>();
                for (CustomListVo customListVo : data) {
                    cusDirIds.add(customListVo.getId());
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3.add(customListVo.getId());
                    }

                }
                questionTmpDocument.setCustomListId1(cusDirIds1);
                questionTmpDocument.setCustomListStr1((cusDirIds1 != null && cusDirIds1.size() > 0) ?
                        cusDirIds1.toString().substring(1, cusDirIds1.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId2(cusDirIds2);
                questionTmpDocument.setCustomListStr2((cusDirIds2 != null && cusDirIds2.size() > 0) ?
                        cusDirIds2.toString().substring(1, cusDirIds2.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId3(cusDirIds3);
                questionTmpDocument.setCustomListStr3((cusDirIds3 != null && cusDirIds3.size() > 0) ?
                        cusDirIds3.toString().substring(1, cusDirIds3.toString().length() - 1) : "");
                questionTmpDocument.setCustomListId(cusDirIds);
                questionTmpDocument.setCustomListStr((cusDirIds != null && cusDirIds.size() > 0) ? cusDirIds.toString
                        ().substring(1, cusDirIds.toString().length() - 1) : "");
            }
        }


        //============主题================

        if (topicIds != null && topicIds.size() > 0) {

            questionTmpDocument.setTopicId(topicIds);

            Set<String> allMUTIds = new HashSet<>();
            List<String> topicNames = new ArrayList<>();

            ResponseEntity<List<TopicWithParent>> topicListResp = topicService.findTopicsWithParent(new
                    RequestEntity<>(topicIds));

            List<TopicWithParent> topicList = topicListResp.getEntity();
            for (TopicWithParent topic : topicList) {
                topicNames.add(topic.getName());
                allMUTIds.add("T" + topic.getId());
                Unit unit = topic.getUnit();
                if (unit != null) {
                    allMUTIds.add("U" + unit.getId());
                }
                Module module = topic.getModule();
                if (module != null) {
                    allMUTIds.add("M" + module.getId());
                }
            }
            questionTmpDocument.setTopicStr(topicIds.toString().substring(1, topicIds.toString().length() - 1));
            questionTmpDocument.setAllMUTIds(new ArrayList<>(allMUTIds));
            questionTmpDocument.setTopicName(topicNames);
        }


        //============章节================
        //通过章节ID获取章节列表
        if (chapterId != null && chapterId != 0) {

            //查询chapter是全部级联信息
            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);

            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }

            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuffer = new StringBuilder();
            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuffer.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuffer.toString();
            questionTmpDocument.setChapterId(chapterIds);
            questionTmpDocument.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmpDocument.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmpDocument.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmpDocument.setName1(name1);
                questionTmpDocument.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmpDocument.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmpDocument.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmpDocument.setName2(name2);
                questionTmpDocument.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmpDocument.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmpDocument.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmpDocument.setName3(name3);
                questionTmpDocument.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }

        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocument);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("\n requestID为:{} \n ,MQ 发送单题数据为:{}", solrIndexReqMsg.getRequestId(), solrIndexReqMsg.getBody());
        return solrIndexReqMsg;
    }


    /*
       复合题转换mq
    */
    public static SolrIndexReqMsg SolrIndexReqMsgSubj(EntityQuestion entityQuestion, String html, Long chapterId,
                                                      List<Long> topicIds, List<Long> ids, Long cusDirId,
                                                      CustomListService customListService, TopicService topicService,
                                                      TeacherChapterMapper teacherChapterMapper) {

        ComplexQuestion complexQuestion;
        try {
            complexQuestion = JsonUtils.fromJson(html, ComplexQuestion.class);
        } catch (IOException e) {
            logger.error("", e);
            throw new BizLayerException(e, INNER_ERROR);
        }
        List<com.noriental.praxissvr.question.bean.html.Question> questions = complexQuestion.getQuestions();
        List<QuestionDocument> questionTmpDocumentList = new ArrayList<>();

        List<Long> subIds = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {

            QuestionDocument questionTmpDocument = new QuestionDocument();

            questionTmpDocument.setId(ids.get(i + 1));
            if (questions.get(i).getOptions() != null) {
                questionTmpDocument.setCountOptions(questions.get(i).getOptions().size());
            } else {
                questionTmpDocument.setCountOptions(0);
            }

            String difficulty = questions.get(i).getDifficulty();
            if (null != difficulty && !"".equals(difficulty)) {
                questionTmpDocument.setDifficulty(Integer.parseInt(difficulty));
            }
            questionTmpDocument.setParentQuestionId(entityQuestion.getId());
            questionTmpDocument.setQuestionType(questions.get(i).getType().getName());//题型
            if (1 == questions.get(i).getType().getId()) { //选择题
                questionTmpDocument.setRightOption(questions.get(i).getAnswer() != null && questions.get(i).getAnswer
                        ().size() > 0 ? questions.get(i).getAnswer().toString() : "");

            }

            /*
             * answerNum个数
             */
            questionTmpDocument.setAnswerNum(questions.get(i).getAnswer().size());
            questionTmpDocument.setIsSingle(1);
            questionTmpDocument.setQuestionTypeId(questions.get(i).getType().getId());
            questionTmpDocument.setVisible(1);
            questionTmpDocument.setNewFormat(1);
            //上传人ID
            questionTmpDocument.setUploadId(entityQuestion.getUploadId());
            //学科ID
            questionTmpDocument.setSubjectId(entityQuestion.getSubjectId());
            //智能批改
            questionTmpDocument.setIntelligent(0);
            if (entityQuestion.getUploadTime() != null) {
                questionTmpDocument.setUploadTime(entityQuestion.getUploadTime());
            }
            /*
                子题作图题处理
             */
            if (questionTmpDocument.getQuestionTypeId() == 51) {
                NewMap map = questions.get(i).getMap();
                try {
                    String json = JSON.json(map);
                    questionTmpDocument.setJsonMap(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            questionTmpDocument.setUpdateTime(new Date());
            questionTmpDocument.setState(QuestionState.PREVIEWED.name());
            questionTmpDocument.setQrId(entityQuestion.getQrId());
            questionTmpDocument.setCountTopic(entityQuestion.getCountTopic());
            questionTmpDocument.setSrc(entityQuestion.getSrc());
            questionTmpDocument.setQuestionGroup(entityQuestion.getQuestionGroup());
            questionTmpDocument.setOrgId(entityQuestion.getOrgId());
            questionTmpDocument.setOrgType(entityQuestion.getOrgType());
            questionTmpDocument.setSource(entityQuestion.getSource());
            questionTmpDocument.setUploadSrc(entityQuestion.getUploadSrc());
            questionTmpDocument.setIsFinishedProduct(0);

            subIds.add(questionTmpDocument.getId());
            questionTmpDocumentList.add(questionTmpDocument);
        }


        //大题信息
        QuestionDocument questionTmp = new QuestionDocument();
        questionTmp.setId(entityQuestion.getId());
        questionTmp.setCountOptions(entityQuestion.getCountOptions());
        questionTmp.setDifficulty(entityQuestion.getDifficulty());
        questionTmp.setHighQual(entityQuestion.getHighQual());
        questionTmp.setMultiScoreAnswer(entityQuestion.getMultiScoreAnswer() != null ? entityQuestion
                .getMultiScoreAnswer() : "");
        questionTmp.setQuestionType(entityQuestion.getQuestionType());
        questionTmp.setRightOption(entityQuestion.getRightOption() != null && !"".equals(entityQuestion
                .getRightOption()) ? entityQuestion.getRightOption() : "");
        questionTmp.setIsSingle(0);
        questionTmp.setState(entityQuestion.getState().name());
        questionTmp.setQrId(entityQuestion.getQrId());
        questionTmp.setCountTopic(entityQuestion.getCountTopic());
        questionTmp.setSubjectId(entityQuestion.getSubjectId());
        questionTmp.setOrgId(entityQuestion.getOrgId());
        questionTmp.setOrgType(entityQuestion.getOrgType());
        //智能批改
        questionTmp.setIntelligent(0);
        questionTmp.setHtmlData(entityQuestion.getHtmlData());
        questionTmp.setJsonData(entityQuestion.getJsonData());

        questionTmp.setAllLeafQuesIds(subIds);

        if (entityQuestion.getUploadTime() != null) {
            questionTmp.setUploadTime(entityQuestion.getUploadTime());
        }
        questionTmp.setUpdateTime(new Date());

        questionTmp.setUploadId(entityQuestion.getUploadId());
        questionTmp.setUploadSrc(entityQuestion.getUploadSrc());
        questionTmp.setQuestionGroup(entityQuestion.getQuestionGroup());
        questionTmp.setAnswerNum(entityQuestion.getAnswerNum());
        questionTmp.setQuestionTypeId(entityQuestion.getQuestionTypeId());
        questionTmp.setSource(entityQuestion.getSource());
        questionTmp.setParentQuestionId(entityQuestion.getParentQuestionId());
        questionTmp.setVisible(1);
        questionTmp.setNewFormat(1);
        questionTmp.setParentQuestionId(entityQuestion.getParentQuestionId());

        /*
            自定义目录
         */
        if (cusDirId != null) {
            CommonResponse<List<CustomListVo>> response = customListService.findParentsByLeafId(new IdRequest
                    (cusDirId));
            List<CustomListVo> data = response.getData();
            if (CollectionUtils.isNotEmpty(data)) {

                List<Long> cusDirIds1 = new ArrayList<>();
                List<Long> cusDirIds2 = new ArrayList<>();
                List<Long> cusDirIds3 = new ArrayList<>();
                List<Long> cusDirIds = new ArrayList<>();
                for (CustomListVo customListVo : data) {
                    cusDirIds.add(customListVo.getId());
                    if (customListVo.getLevel() == 1) {
                        cusDirIds1.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 2) {
                        cusDirIds2.add(customListVo.getId());
                    } else if (customListVo.getLevel() == 3) {
                        cusDirIds3.add(customListVo.getId());
                    }

                }
                questionTmp.setCustomListId1(cusDirIds1);
                questionTmp.setCustomListStr1((cusDirIds1 != null && cusDirIds1.size() > 0) ? cusDirIds1.toString()
                        .substring(1, cusDirIds1.toString().length() - 1) : "");
                questionTmp.setCustomListId2(cusDirIds2);
                questionTmp.setCustomListStr2((cusDirIds2 != null && cusDirIds2.size() > 0) ? cusDirIds2.toString()
                        .substring(1, cusDirIds2.toString().length() - 1) : "");
                questionTmp.setCustomListId3(cusDirIds3);
                questionTmp.setCustomListStr3((cusDirIds3 != null && cusDirIds3.size() > 0) ? cusDirIds3.toString()
                        .substring(1, cusDirIds3.toString().length() - 1) : "");
                questionTmp.setCustomListId(cusDirIds);
                questionTmp.setCustomListStr((cusDirIds != null && cusDirIds.size() > 0) ? cusDirIds.toString()
                        .substring(1, cusDirIds.toString().length() - 1) : "");
            }
        }


        //================章节信息=================

        if (chapterId != null && chapterId != 0) {
            //通过章节ID获取章节列表
            //组装chapter参数

            List<EntityTeachingChapter> teachingChapters = teacherChapterMapper.findChaptersById(chapterId);

            if (CollectionUtils.isEmpty(teachingChapters)) {
                throw new BizLayerException("查询章节不存在={}", FIND_CHAPTER_FAIL);
            }


            List<Long> chapterIds = new ArrayList<>();
            StringBuilder chaptersBuf = new StringBuilder();
            for (EntityTeachingChapter entityTeachingChapter : teachingChapters) {
                //组装chapterIds
                chapterIds.add(entityTeachingChapter.getId());
                //组装chapterStrs
                chaptersBuf.append(entityTeachingChapter.getId() + ",");
            }
            String chapters = chaptersBuf.toString();
            questionTmp.setChapterId(chapterIds);
            questionTmp.setChapterStr(chapters.substring(0, chapters.length() - 1));

            if (teachingChapters.size() == 1) {
                //chapter1
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");
            }

            if (teachingChapters.size() == 2) {
                //chapter2
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");
            }
            if (teachingChapters.size() == 3) {
                //chapter3
                List<Long> chapter1Id = new ArrayList<>();
                chapter1Id.add(teachingChapters.get(0).getId());
                questionTmp.setChapterId1(chapter1Id);
                List<String> prefixName1 = new ArrayList<>();
                prefixName1.add(teachingChapters.get(0).getPrefixName());
                questionTmp.setPrefixName1(prefixName1);
                List<String> name1 = new ArrayList<>();
                name1.add(teachingChapters.get(0).getName());
                questionTmp.setName1(name1);
                questionTmp.setChapterStr1(teachingChapters.get(0).getId() + "");

                List<Long> chapter2Id = new ArrayList<>();
                chapter2Id.add(teachingChapters.get(1).getId());
                questionTmp.setChapterId2(chapter2Id);
                List<String> prefixName2 = new ArrayList<>();
                prefixName2.add(teachingChapters.get(1).getPrefixName());
                questionTmp.setPrefixName2(prefixName2);
                List<String> name2 = new ArrayList<>();
                name2.add(teachingChapters.get(1).getName());
                questionTmp.setName2(name2);
                questionTmp.setChapterStr2(teachingChapters.get(1).getId() + "");

                List<Long> chapter3Id = new ArrayList<>();
                chapter3Id.add(teachingChapters.get(2).getId());
                questionTmp.setChapterId3(chapter3Id);
                List<String> prefixName3 = new ArrayList<>();
                prefixName3.add(teachingChapters.get(2).getPrefixName());
                questionTmp.setPrefixName3(prefixName3);
                List<String> name3 = new ArrayList<>();
                name3.add(teachingChapters.get(2).getName());
                questionTmp.setName3(name3);
                questionTmp.setChapterStr3(teachingChapters.get(2).getId() + "");
            }
        }


        //==================主题信息==================

        if (topicIds != null && topicIds.size() > 0) {

            //通过topicIds获取unit 和 module

            ResponseEntity<List<TopicWithParent>> topicsWithParent = topicService.findTopicsWithParent(new
                    RequestEntity<>(topicIds));
            List<TopicWithParent> entitys = topicsWithParent.getEntity();
            List<Long> unitIds = new ArrayList<>();
            List<Long> moduleIds = new ArrayList<>();
            List<String> topicNames = new ArrayList<>();
            Set<String> allMUTIds = new HashSet<>();
            for (TopicWithParent entity : entitys) {
                if (entity != null) {

                    topicNames.add(entity.getName());        //知识点名称
                    allMUTIds.add("T" + entity.getId());
                    Unit unit = entity.getUnit();
                    if (unit != null) {
                        unitIds.add(entity.getUnit().getId());   //单元IDS
                        allMUTIds.add("U" + unit.getId());
                    }
                    Module module = entity.getModule();
                    if (module != null) {
                        moduleIds.add(entity.getModule().getId());//模块IDS
                        allMUTIds.add("M" + module.getId());
                    }

                }
            }

            questionTmp.setAllMUTIds(new ArrayList<String>(allMUTIds));
            questionTmp.setAllTopicIds(topicIds);
            questionTmp.setAllTopicIdStr(topicIds.toString().substring(1, topicIds.toString().length() - 1));
            questionTmp.setAllUnitIds(unitIds);
            questionTmp.setAllTopicIdStr(unitIds.toString().substring(1, unitIds.toString().length() - 1));
            questionTmp.setAllModuleIds(moduleIds);
            questionTmp.setAllModuleIdStr(moduleIds.toString().substring(1, moduleIds.toString().length() - 1));
            questionTmp.setTopicName(topicNames);
            questionTmp.setTopicId(topicIds);
        }

        questionTmpDocumentList.add(questionTmp);
        //************************************************************************************
        SolrIndexReqMsg solrIndexReqMsg = new SolrIndexReqMsg(questionTmpDocumentList);
        solrIndexReqMsg.setRequestId(TraceKeyHolder.getTraceKey());
        logger.info("复合题发送MQ的请求ID={}", solrIndexReqMsg.getRequestId());
        return solrIndexReqMsg;

    }

}
