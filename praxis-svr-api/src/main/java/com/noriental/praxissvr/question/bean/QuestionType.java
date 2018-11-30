package com.noriental.praxissvr.question.bean;

import com.noriental.publicsvr.bean.Subject;
import com.noriental.publicsvr.bean.Stage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author : Lance lance7in_gmail_com
 * Date   : 14/05/2014 22:54
 * Since  :
 */
public class QuestionType implements Serializable {

    private static final long serialVersionUID = 1193167348072015847L;

    private static Map<String, String[]> questionTypes;

    static {
        questionTypes = new HashMap<>();
        String[] pChinese = {"现代文阅读题", "文言文阅读题", "写作题", "选择题", "填空题", "选择填空题", "简答题", "材料题", "连线题", "判断题","作图题"};
        String[] pMath = {"选择题", "填空题", "解答题", "判断题","作图题"};
        String[] pEnglish = {"单项选择", "填空题", "词汇（运用）", "句型转换", "信息匹配题", "填空型完形填空", "填空型阅读理解", "改错题", "翻译题", "字母题", "排序题", "音标（语音）", "连词成句", "判断题", "短对话选择型听力", "长对话选择型听力", "填空型听力","口语文章朗读","听单词点击词义","听单词拼写单词","看单词点击词义","看词义点击单词","看词义拼写单词","作图题"};
        questionTypes.put("小学数学", pMath);
        questionTypes.put("小学语文", pChinese);
        questionTypes.put("小学英语", pEnglish);

        String[] jPhysics = {"选择题", "填空题", "实验题", "作图题", "计算题", "简答题", "信息综合题", "其他"};
        String[] jGeometry = {"组合选择题", "组合填空题", "综合题", "组合简答题", "选择题", "填空题", "简答题", "连线题","作图题"};
        String[] jEnglish = {"短对话选择型听力", "长对话选择型听力", "选择型完形填空", "选择型阅读理解", "词汇运用", "填空题", "句型转换", "填空型听力", "单项选择", "填空型完形填空", "信息匹配题", "填空型阅读理解", "改错题", "翻译题", "书面表达","口语文章朗读","听单词点击词义","听单词拼写单词","看单词点击词义","看词义点击单词","看词义拼写单词","作图题"};
        String[] jBiology = {"综合题", "探究题", "选择题", "判断题", "连线题", "填空题"};
        String[] jHistory = {"选择题", "选择搭配题", "填空题", "判断题", "辨析改错题", "连线题", "简答题", "材料题"};
        String[] jChinese = {"现代文阅读题", "文言文阅读题", "写作题", "选择题", "填空题", "选择填空题", "简答题", "材料题", "连线题", "判断题","作图题"};
        String[] jPolitics = {"组合选择题", "探究题", "分析说明题", "选择题", "判断题说明题", "论述题", "简答题", "填空题", "辨析题", "判断题"};
        String[] jMath = {"选择题", "填空题", "判断题", "解答题","作图题"};
        String[] jChemistry = {"选择题", "选择填空题", "填空题", "简答题", "计算题", "实验题", "判断题"};

        questionTypes.put("初中物理", jPhysics);
        questionTypes.put("初中地理", jGeometry);
        questionTypes.put("初中英语", jEnglish);
        questionTypes.put("初中生物", jBiology);
        questionTypes.put("初中历史", jHistory);
        questionTypes.put("初中语文", jChinese);
        questionTypes.put("初中政治", jPolitics);
        questionTypes.put("初中数学", jMath);
        questionTypes.put("初中化学", jChemistry);

        String[] sPhysics = {"选择题", "填空题", "实验题", "作图题", "计算题", "简答题"};
        String[] sGeometry = {"组合选择题", "组合填空题", "综合题", "组合简答题", "选择题", "填空题", "简答题", "判断题","作图题"};
        String[] sEnglish = {"短对话选择型听力", "长对话选择型听力", "选择型完形填空", "选择型阅读理解", "词汇运用", "填空题", "句型转换", "填空型听力", "单项选择", "填空型完形填空", "信息匹配题", "填空型阅读理解", "改错题", "翻译题", "书面表达", "选择题","口语文章朗读","听单词点击词义","听单词拼写单词","看单词点击词义","看词义点击单词","看词义拼写单词","作图题"};
        String[] sBiology = {"综合题", "探究题", "选择题"};
        String[] sHistory = {"选择题", "优选题", "填空题", "判断题", "简答题", "论证题", "材料题"};
        String[] sChinese = {"现代文阅读题", "文言文阅读题", "写作题", "选择题", "选择填空题", "填空题", "简答题", "材料题", "判断题","作图题"};
        String[] sPolitics = {"组合选择题", "探究题", "分析说明题", "组合简答题", "选择题", "论述题", "简答题", "辨析题"};
        String[] sMath = {"选择题", "填空题", "解答题","作图题"};
        String[] sChemistry = {"选择题", "填空题", "简答题", "计算题", "实验题", "判断题"};

        questionTypes.put("高中物理", sPhysics);
        questionTypes.put("高中地理", sGeometry);
        questionTypes.put("高中英语", sEnglish);
        questionTypes.put("高中生物", sBiology);
        questionTypes.put("高中历史", sHistory);
        questionTypes.put("高中语文", sChinese);
        questionTypes.put("高中政治", sPolitics);
        questionTypes.put("高中数学", sMath);
        questionTypes.put("高中化学", sChemistry);
    }

    private long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 学科
     */
    private Subject subject;
    /**
     * 显示名称
     */
    private String displayName;
    /**
     * 学段
     */
    private Stage Stage;

    //题型id 
    private Long typeId;
    private String typeName;
    //科目id
    private Long subjectId;
    //学段id
    private Long stageId;

    private Integer enable;

    private Long structId;
    private int isSingle;
    private int uploadExerciseEnable;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Stage getStage() {
        return Stage;
    }

    public void setStage(Stage stage) {
        Stage = stage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public static boolean isQuestionTypeCorrect(String stageName, String subjectName, String questionType) {
        if (questionTypes.get(stageName + subjectName) == null) {
            return false;
        }
        String[] obj = questionTypes.get(stageName + subjectName);
        for (String s : obj) {
            if (s.equals(questionType)) {
                return true;
            }
        }
        return false;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Long getStructId() {
        return structId;
    }

    public void setStructId(Long structId) {
        this.structId = structId;
    }

    public int getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(int isSingle) {
        this.isSingle = isSingle;
    }

    public int getUploadExerciseEnable() {
        return uploadExerciseEnable;
    }

    public void setUploadExerciseEnable(int uploadExerciseEnable) {
        this.uploadExerciseEnable = uploadExerciseEnable;
    }
}
