package com.noriental.praxissvr.exception;

import com.noriental.utils.json.JsonUtil;
import com.noriental.validate.error.ErrorCode;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author chenlihua
 * @date 2015/12/18
 * @time 14:14
 */
public enum PraxisErrorCode implements ErrorCode {
    PRAXIS_TEACHER_NOT_FOUND(20000, "老师不存在"),
    PRAXIS_STUDENT_CLASS_NULL(20001, "班级和学生ID不能同时为空"),
    PRAXIS_DEADLINE_DURATION_NULL(20002, "截止日期和作答时长不能同时为空"),
    PRAXIS_CHAPTER_NULL(20003, "章节ID不能不合法"),
    PRAXIS_MODULE_NULL(20004, "章节ID不能不合法"),
    PRAXIS_UNIT_NULL(20005, "章节ID不能不合法"),
    PRAXIS_TOPIC_NULL(20006, "章节ID不能不合法"),
    PRAXIS_CLASS_IDS_NULL(20007, "班级ID不能同时为空"),
    PRAXIS_STUDENT_IDS_NULL(20008, "学生ID不能同时为空"),
    PRAXIS_PUBLISH_TYPE_INVALID(20009, "无效的发布类型"),

    PRAXIS_EXERCISE_DISABLED(20101, "题集已停用"),
    PRAXIS_EXERCISE_CHECKED(20102, "题集已审核"),
    PRAXIS_EXERCISE_CHECK_REJECT(20103, "审核被拒绝"),
    PRAXIS_EXERCISE_STATUS_INVALID(20104, "题集状态不合法"),


    PRAXIS_INVOKE_SOLR(21000, "远程调用solr服务出错"),
    PRAXIS_QUESTION_NOT_FOUND(22000, "习题不存在"),
    PRAXIS_QUESTION_OUT_OF_RANGE(22003, "习题数量超出范围"),

    GROUP_NOT_FOUND(22100, "分组不存在"),

    PRAXIS_STUDENT_NOT_SUBMIT(22001, "学生未提交"),
    PRAXIS_STUDENT_SUBMITTED(22002, "已经提交，不要重复提交"),
    //操作
    PRAXIS_SAVE_FAIL(23000, "保存失败"),
    PRAXIS_UPDATE_FAIL(23001, "更新失败"),
    PRAXIS_SUBMIT_FAIL(23002, "提交失败"),
    PRAXIS_QUERY_TIME_OUT(23003, "查询超时"),
    PRAXIS_METHOD_DEPRECATED(23004, "方法过时"),

    PRAXIS_EXERCISE_NOT_FOUND(24000, "题集不存在"),
    PRAXIS_EXERCISE_HAS_UPDATED(24001, "题集已更新，历史题集不能查询"),
    PRAXIS_EXERCISE_DATA_ERROR(24002, "题集数据异常"),

    //请求参数错误
    PRAXIS_REQUEST_INVALID_DEADLINE(29000, "作业截止日期不能为空"),
    PRAXIS_REQUEST_INVALID_DURATION(29001, "评测作答时长不能为空"),
    PRAXIS_REQUEST_INVALID_CREATE_ID(29002, "登录账户不合法"),
    PRAXIS_REQUEST_INVALID_STUDENT(29003, "发布对象不合法"),

    PRAXIS_QUESTION_TYPE_NOT_FOUND(25001, "题型信息未找到"),

    PRAXIS_PLATFORM_NO_PERMISSION(25002, "平台无权限"),
    PRAXIS_ENTRUST_NO_FOUND(25003, "未找到指定的委托传题"),

    //答题数据错误码定义 22300 开始
    /**答题数据未找到    */
    ANSWER_RECORD_NOT_FOUND(22300, "答题数据未找到"),
    /**找到多条数据    */
    ANSWER_RECORD_REPEAT(22301, "找到多条数据"),
    /** 题目没有题型   */
    ANSWER_RECORD_NOT_QUES_TYPE(22302, "题目没有题型"),
    /**题目没有找到    */
    ANSWER_RECORD_QUES_NOT_FOUND(22303, "题目没有找到"),
    /**没有推荐题目    */
    ANSWER_RECORD_RECOMMEND_QUES_NOT_FOUND(22304, "没有可推荐题目"),
    /**没有找到刷题记录  */
    ANSWER_RECORD_BRUSH_QUES_NOT_FOUND(22305, "没有找到刷题记录"),
    /**记录不一致  */
    ANSWER_RECORD_BRUSH_COUNT_NOT_SAME(22306, "记录不一致"),
    /**大题小题不一致  */
    ANSWER_RECORD_QUES_NOT_SAME(22307, "大题小题不一致"),
    /**没有找到刷题题目记录  */
    ANSWER_RECORD_BRUSH_QUESTION_NOT_FOUND(22307, "没有找到刷题题目记录"),
    /**错题没有找到    */
    ANSWER_RECORD_WRONQUES_NOT_FOUND(22308, "错题没有找到"),
    /** 题型不正确   */
    ANSWER_RECORD_QUES_TYPE_WRONG(22309, "题型不正确"),
    /**题集未找到    */
    ANSWER_RECORD_SET_NOT_FOUND(22310, "题集未找到"),
    /**记录已存在    */
    ANSWER_RECORDE_EXIST(24311, "记录已存在"),
    /**强化题集不存在    */
    ANSWER_CHAL_SET_NOT_EXIST(24312, "错题消灭题集不存在"),
    ANSWER_CHAL_FIND_QUES_THAN_ONE(24313, "错题消灭题目查询条件大于1"),
    ANSWER_CHAL_FIND_STU_SHORT(24313, "错题消灭缺少学生查询条件"),
    ANSWER_CHAL_QUES_THAN_ONE(24314, "错题消灭只能操作一个题目或者整个题集（单题提交，按题集或单题查询）"),
    ANSWER_PUBLISH_RESOURCE_ID_NOT_EXIST(24315, "资源发布id不存在"),
    //答题操作错误码定义 24300 开始
    /**老师已经批改    */
    ANSWER_CORRECTED_BY_TEACHER(24300, "老师已经批改"),
    /**本人已经批改    */
    ANSWER_CORRECTED_BY_STUDENT(24301, "本人已经批改"),
    /**已提交过    */
    ANSWER_SUBMITED(24302, "已提交过"),
    //答题参数错误码定义 29300 开始
    /**参数为空    */
    ANSWER_PARAMETER_NULL(29300, "参数为空"),
    /**答题状态result不合法    */
    ANSWER_PARAMETER_INVALID_RESULT_VALUE(29301, "答题状态result不合法"),
    /**答题状态result格式不合法   */
    ANSWER_PARAMETER_INVALID_RESULT_FORMAT(29302, "答题状态result格式不合法"),
//    /**答题状态result数值不合法    */
//    ANSWER_PARAMETER_INVALID_TKT_RESULT_VALUE(29303, "答题状态result数值不合法"),
    /**知识点级别数值不合法    */
    ANSWER_PARAMETER_INVALID_LEVEL_VALUE(29304, "知识点级别数值不合法"),
    /**作答图片来源不合法    */
    ANSWER_PARAMETER_PICTRUE_SOURCE_VALUE(29305, "作答图片来源不合法"),
    /**大题id字段不应该为空  */
    ANSWER_PARAMETER_PARENT_ID_HAS_NOVALUE(29306, "大题id字段不应该为空"),
    /**参数不合法  */
    ANSWER_PARAMETER_ILL(29307, "参数不合法"),
    /**资源创建分组名称已存在**/
    RESOURCE_GROUP_NAME_EXIT(29308,"组名已存在"),
   /**sha1解密失败**/
    SHA1_CODE_EXCEPTION(29309,"sha1加密失败"),
    /**删除习题失败**/
    QUESTION_DELETE_FAIL(29310,"删除习题失败"),
    /**添加习题失败**/
    QUESTION_CREATE_FAIL(29311,"添加习题失败"),
    /**习题不存在**/
    QUESTION_DOWN_FAIL(29312,"习题不存在"),
    /**更新习题失败**/
    QUESTION_UPDATE_FAIL(29313,"习题更新失败"),
    /**json解析异常**/
    JSON_CONVERT_FAIL(29314,"JSON解析异常"),
    /**上传习题创建索引失败**/
    QUESTION_UPCREATE_INDEX_FAIL(29315,"上传习题创建索引失败"),
    /**SSDB获取更新失败**/
    SSDB_UPDATE_FAIL(29316,"SSDB操作失败"),
    /**批量插入失败**/
    BATCH_INSERT_FAIL(29317,"批量插入失败"),
    /**删除主题关联失败**/
    DELETE_TOPIC_FAIL(29318,"删除主题关联失败"),
    /**删除章节关联失败**/
    DELETE_CHAPTER_FAIL(29319,"删除章节关联失败"),
    /**查询习题不存在**/
    QUERY_CHAPTER_FAIL(29320,"查询的习题不存在"),
    /**习题密钥匹配失败**/
    SHA1_EQUAL_EXCEPTION(29321,"习题密钥匹配失败"),
    /**知识点数量做多为五个**/
    TOPIC_COUNT(29322,"知识点数量不能多于10个"),
    /**查询章节信息不存在**/
    FIND_CHAPTER_FAIL(29333,"查询章节信息不存在"),
    /**更新solr失败**/
    UPDATE_SOLR_FAIL(29334,"更新solr失败"),
    /**查询子题ID失败**/
    QUERY_QUESTIONID_FAIL(29335,"查询子题ID失败"),
    SEND_MQ_FAIL(29336,"消息发送队列失败"),
    SSDB_ADD_LOCK_FAIL(29337,"SSDB加锁失败"),
    GET_DATE_FAIL(29338,"时间转换失败"),
    QIQIU_FAIL(29339,"MP3转换失败"),
    BATCH_UPDATE_FAIL(29340,"批量更新失败"),
    INTELL_CORRECT_RESULT(29341,"智能批改结果不存在"),
    INTELL_CORRECT_NO_RESULT(29342,"学生未做答,不能使用智能批改"),
    QUESTION_SOURCE(29343,"题目来源不能为空"),
    INTELL_POSTIL_NO_RESULT(29344,"智能批注信息不存在"),
    POSTIL_NO_RESULT(29345,"批注信息不能为空"),
    WRONG_QUESTIONS_LEVEL(29346,"level层级不存在"),
    WRONG_QUESTIONS_OUT_RANGE(29347,"最大长度不能超过50"),
    UPDATE_AUDIO_FAIL(29348,"更新音频失败"),
    INSERT_DIRECTORY_FAIL(29349,"插入自定义目录失败"),
    QUESTION_COLLECTION_FAIL(29350,"题目收藏失败"),
    CUS_DIR_UPDATE_FAIL(29351,"更新自定义目录失败"),
    EXIST_COLLECTION(29352,"已经被收藏或是我的题目不允许被收藏"),
    CUS_DIR_DELETE_FAIL(29353,"删除自定义目录失败"),
    STRUCTID_PARENTID(29354,"根据题目ID获取题目结构和父题ID失败"),
    CUS_DIR_INVALID(29355,"自定义目录已被删除"),
    REAPT_SUBMIT(29356,"不允许重复提交"),
    QUESTION_FORMAT_ERROR(29357,"创建习题html数据格式错误"),
    CUS_DIR_MOVE_FAIL(29358,"不能放入到自身目录下"),
    DATA_IS_REPEAT(29359,"刷题数据有重复的习题ID"),
    MYSQL_DATA_PERSISTANT(29360,"批改或批注数据持久化库失败"),
    CORRECT_EXCEPTION(29361,"批改或批注失败"),
    INTELL_CORRECT_EXCEPTION(29362,"智能批改失败"),
    STUDENT_YEAR_CLASS_EXCEPTION(29363,"获取学生学年和班级ID失败"),
    QUESTION_SOURCE_ERROR(29364,"题目来源不合法"),
    NULL_QUESTION_TYPE_EXCEPTION(29365,"题型不能为空"),
    LEVEL_ERROR(29366,"知识点章节层级不正确"),
    QUERY_SCHOOLD_ID_ERROR(29367,"根据班级ID获取学校ID失败"),
    BATCH_CORRECT_EXCEPTION(29368,"一键批改失败"),
    BATCH_CORRECT_REPEAT_EXCEPTION(29369,"一键批改不允许重复提交"),
    QUESTION_HTML_FORMAT_ERROR(29370,"乱码验证失败"),
    QUESTION_FORMAT_HTML_ERROR(29371,"题目中包含不支持的格式，请检查后再试"),
    BATCH_INTELL_CORRECT_REPEAT_EXCEPTION(29372,"一键批改不允许重复提交"),
    BRUSH_QUESTION_EXCEPTION(29373,"刷题失败"),
    FIND_FLOW_TURN_EXCEPTION(29374,"查询求助批改列表失败"),


    QUESTION_CONTENT_NULL_EXCEPTION(29374,"题目内容html为空"),
    QUESTION_USER_ERROR(29400,"用户信息查询错误！"),
    QUESTION_SPECIAL_ERROR(29401,"题目专题信息插入失败！"),
    INNER_ERROR(29999,"praxis内部错误")
    ;
    private final int value;
    private final String comment;

    PraxisErrorCode(int value, String comment) {
        this.value = value;
        this.comment = comment;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getComment() {
        return this.comment;
    }


    private static Map<Integer, PraxisErrorCode> map =
            new TreeMap<>();

    static {
        for (PraxisErrorCode code : values()) {
            map.put(code.getValue(), code);
        }
    }

    public static PraxisErrorCode errorCodeFor(int value) {
        return map.get(value);
    }

    public static void addNewErrorCodes(PraxisErrorCode[] codes) {
        for (PraxisErrorCode code : codes) {
            if (!map.containsKey(code.getValue())) {
                map.put(code.getValue(), code);
            }
        }
    }
	
    @Override
	public String toString() {
		return JsonUtil.obj2Json(this);
	}
}
