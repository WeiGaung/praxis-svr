package com.noriental.praxissvr.common;

import java.util.Map;
import java.util.TreeMap;

/**
 * todo 将来要废弃此种枚举方式
 * @author chenlihua
 * @date 2015/12/1
 * @time 9:57
 */
public enum QuestionTypeEnum {
    ALL("*", "*", 0),
    XUAN_ZE("选择题", "选择题", 0),
    DAN_XUAN("单选题", "选择题", 1),
    DUO_XUAN("多选题", "选择题", 1),
    TIAN_KONG("填空题", "填空题", 2),
    PAN_DUAN("判断题", "判断题", 3),
    JIAN_DA("简答题", "简答题", 4),
    SHI_YAN("实验题", "实验题", 5),
    ZONG_HE("综合题", "综合题", 6),
    WEN_YAN_WEN_YUE_DU("文言文阅读题", "文言文阅读题", 7),
    FEN_XI_SHUO_MING("分析说明题", "分析说明题", 8),
    TAN_JIU("探究题", "探究题", 9),
    XIAN_DAI_WEN_YUE_DU("现代文阅读题", "现代文阅读题", 10),
    ZU_HE_TIAN_KONG("组合填空题", "组合填空题", 11),
    ZU_HE_JIAN_DA("组合简答题", "组合简答题", 12),
    ZU_HE_XUAN_ZE("组合选择题", "组合选择题", 13),
    XUAN_ZE_WAN_XING("选择型完形填空", "选择型完形填空", 14),
    XUAN_ZE_YUE_DU_LI_JIE("选择型阅读理解", "选择型阅读理解", 15),
    DA_XIANG_XUAN_ZE("单项选择", "单项选择", 16),
    JI_SUAN("计算题", "计算题", 17),
    XIN_XI_ZONG_HE("信息综合题", "信息综合题", 18),
    FAN_YI("翻译题", "翻译题", 19),
    LUN_ZHENG("论证题", "论证题", 20),
    CAI_LIAO("材料题", "材料题", 21),
    LUN_SHU("论述题", "论述题", 22),
    BIAN_XI("辨析题", "辨析题", 23),
    JIE_DA("解答题", "解答题", 24),
    BU_CHONG_JU_ZI("补充句子", "补充句子", 25),
    CAO_LIAO_FEN_XI("材料分析题", "材料分析题", 26),
    DAN_CI_PIN_XIE("单词拼写", "单词拼写", 27),
    DAN_CI_ZAO_JU("单词造句", "单词造句", 28),
    DU_TU_HUI_DA_WEN_TI("读图回答问题", "读图回答问题", 29),
    DU_TU("读图题", "读图题", 30),
    DUI_LIAN("对联题", "对联题", 31),
    GAI_CUO("改错题", "改错题", 32),
    GU_SHI_JIAN_SHANG("古诗鉴赏", "古诗鉴赏", 33),
    JU_XING_ZHUAN_HUAN("句型转换", "句型转换", 34),
    KAN_TU_XIE_HUA("看图写话", "看图写话", 35),
    MO_XIE("默写", "默写", 36),
    PAI_XU("排序题", "排序题", 37),
    TUI_DUAN("推断题", "推断题", 38),
    WEN_BEN_YUE_DU("文本阅读", "文本阅读", 39),
    XIN_XI_FEN_XI("信息分析题", "信息分析题", 40),
    XIN_XI_PI_PEI("信息匹配", "信息匹配", 41),
    XIU_GAI_BING_JU("修改病句", "修改病句", 42),
    YUE_DU_LI_JIE("阅读理解", "阅读理解", 43),
    ZHENG_MING("证明题", "证明题", 44),
    ZU_HE_LIE_JU("组合列举", "组合列举", 45),
    YUE_DU_TIAN_KONG("阅读填空", "阅读填空", 46),
    YU_FA_TIAN_KONG("语法填空", "语法填空", 47),
    JIE_XI("解析题", "解析题", 48),
    TING_LI("听力题", "听力题", 49),
    ZUO_WEN("作文题", "作文题", 50),
    QI_TA("其他", "其他", 99),

    ZUO_TU("作图题", "作图题", 51),
    QI_XUAN_WU("七选五", "七选五", 52),
    KAI_FANG("拍照题", "拍照题", 53),
    KOU_YU_WEN_ZHANG("口语文章朗读","口语文章朗读",54),
    TING_DAN_CI_GEN_DU_DAN_CI("听单词跟读单词","听单词跟读单词",55),
    TING_DAN_CI_DIAN_JI_CI_YI("听单词点击词义","听单词点击词义",56),
    TING_DAN_CI_PIN_XIE_DAN_CI("听单词拼写单词","听单词拼写单词",57),
    KAN_DAN_CI_DIAN_JI_CI_YI("看单词点击词义","看单词点击词义",58),
    KAN_CI_YI_DIAN_JI_DAN_CI("看词义点击单词","看词义点击单词",59),
    KAN_CI_YI_PIN_XIE_DAN_CI("看词义拼写单词","看词义拼写单词",60),




    ;
    private String type;
    private String solrType;
    private int typeId;

    QuestionTypeEnum(String type, String solrType, int typeId) {
        this.type = type;
        this.solrType = solrType;
        this.typeId = typeId;
    }

    private static Map<String, QuestionTypeEnum> map = new TreeMap<>();
    private static Map<Integer, QuestionTypeEnum> map1 = new TreeMap<>();

    static {
        for (QuestionTypeEnum questionType : values()) {
            map.put(questionType.getType(), questionType);
        }

        for (QuestionTypeEnum questionType : values()) {
            map1.put(questionType.getTypeId(), questionType);
        }
    }

    public static QuestionTypeEnum getQuestionTypeByType(String type) {
        return map.get(type);
    }

    public static QuestionTypeEnum getQuestionTypeByTypeId(int typeId) {
        return map1.get(typeId);
    }

    public String getType() {
        return type;
    }

    public String getSolrType() {
        return solrType;
    }

    public int getTypeId() {
        return typeId;
    }
}
