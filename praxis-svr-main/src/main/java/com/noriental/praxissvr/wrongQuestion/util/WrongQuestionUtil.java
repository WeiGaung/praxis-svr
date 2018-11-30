package com.noriental.praxissvr.wrongQuestion.util;

import com.noriental.praxissvr.answer.bean.StudentExercise;
import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.praxissvr.wrongQuestion.bean.WrongQuesExerciseEntity;
import com.noriental.praxissvr.wrongQuestion.request.ErrorQuesChapQueryReq;
import com.noriental.praxissvr.wrongQuestion.request.WrongQuesChapQueryReq;
import com.noriental.praxissvr.wrongQuestion.request.WrongQuesListReq;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kate
 * @create 2017-10-30 17:23
 * @desc 错题业务工具类
 **/
public class WrongQuestionUtil {
    private static final Logger logger = LoggerFactory.getLogger(WrongQuestionUtil.class);

    /***
     *
     * @param wrongQuesListReq
     * @return
     */
    public static Map assembleWrongQuestions(WrongQuesListReq wrongQuesListReq) {
        Map paramMap = new HashMap(1);
        paramMap.put("studentId", wrongQuesListReq.getStudentId());
        paramMap.put("tableName", TableNameUtil.getStuQuesKnowledge(wrongQuesListReq.getStudentId()));
        int level = wrongQuesListReq.getLevel();
        if (level == 1) {
            paramMap.put("chapterName", "module_id");
            paramMap.put("chapterData", wrongQuesListReq.getKnowledgeId());
        } else if (level == 2) {
            paramMap.put("chapterName", "unit_id");
            paramMap.put("chapterData", wrongQuesListReq.getKnowledgeId());
        } else if (level == 3) {
            paramMap.put("chapterName", "topic_id");
            paramMap.put("chapterData", wrongQuesListReq.getKnowledgeId());
        } else {
            throw new BizLayerException("", PraxisErrorCode.WRONG_QUESTIONS_LEVEL);
        }
        paramMap.put("dataType", wrongQuesListReq.getDataType());
        return paramMap;
    }


    /***
     * 组装请求参数为
     * @param in
     * @return
     */
    public static Map assembleMapParam(WrongQuesChapQueryReq in) {
        Map map = new HashMap(1);
        String tableName = TableNameUtil.getStuQuesKnowledge(in.getStudentId());
        map.put("studentId", in.getStudentId());
        map.put("tableName", tableName);
        if (null != in.getSubjectId()) {
            map.put("subjectId", "subject_id");
            map.put("subjectIdValue", in.getSubjectId());
        } else {
            map.put("subjectId", "1");
            map.put("subjectIdValue", "1");
        }
        Long directoryId = in.getDirectoryId();
        if (null != directoryId) {
            map.put("directoryId", "directory_id");
            map.put("directoryIdData", in.getDirectoryId());
        } else {
            map.put("directoryId", "1");
            map.put("directoryIdData", 1);
        }
        int level = in.getChapterLevel();
        if (level == 1) {
            map.put("chapterName", "module_id");
            map.put("chapterData", in.getChapterId());
        } else if (level == 2) {
            map.put("chapterName", "unit_id");
            map.put("chapterData", in.getChapterId());
        } else if (level == 3) {
            map.put("chapterName", "topic_id");
            map.put("chapterData", in.getChapterId());
        } else {
            map.put("chapterName", "1");
            map.put("chapterData", 1);
        }
        Long fromIndex = in.getFromIndex();
        if (null != fromIndex && fromIndex > 0L) {
            map.put("indexData", "id<" + fromIndex);
        } else {
            map.put("indexData", "1=1");
        }
        return map;
    }


    /**
     * 组装单题查询结果
     *
     * @param dataList
     * @return
     */
    public static void assembleSingleQuestion(List<StudentExercise> dataList, List<WrongQuesExerciseEntity>
            responseList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // List<WrongQuesExerciseEntity> responseList = new ArrayList<>();
        if (null != dataList && dataList.size() > 0) {
            //initTextFieldFromSsdb(dataList);
            Map dataMap = new HashMap(5);
            for (StudentExercise data : dataList) {
                Date lastUpdateTime = data.getLastUpdateTime();
                String sdfDate = sdf.format(lastUpdateTime);
                if (dataMap.containsKey(sdfDate)) {
                    List<StudentExercise> resList = (List<StudentExercise>) dataMap.get(sdfDate);
                    resList.add(data);
                    dataMap.put(sdfDate, resList);
                } else {
                    List<StudentExercise> resList = new ArrayList<>();
                    resList.add(data);
                    dataMap.put(sdfDate, resList);
                }
            }
            Map<String, List<StudentExercise>> sortMap = new TreeMap<>(new WrongQuestionUtil.MapKeyComparator());
            sortMap.putAll(dataMap);
            //将同一天的数据以复合题数据模式放入
            for (Map.Entry<String, List<StudentExercise>> entry : sortMap.entrySet()) {
                String key = entry.getKey();
                List<StudentExercise> resultList = entry.getValue();
                List<StudentExercise> studentExerciseList = new ArrayList<>();
                for (StudentExercise entity : resultList) {
                    List<StudentExercise> singleList = new ArrayList<>();
                    singleList.add(entity);
                    StudentExercise studentExercise = new StudentExercise();
                    studentExercise.setStudentExerciseList(singleList);
                    studentExerciseList.add(studentExercise);
                    studentExercise.setCorrectStatus(entity.getCorrectStatus());
                }
                WrongQuesExerciseEntity wrongQuesExerciseEntity = new WrongQuesExerciseEntity();
                wrongQuesExerciseEntity.setStudentExerciseList(studentExerciseList);
                wrongQuesExerciseEntity.setSingle(true);
                try {
                    wrongQuesExerciseEntity.setUpdateTime(DateUtil.getLongDate(key + " 0:0:0"));
                } catch (BizLayerException e) {
                    logger.error("错题记录查询时间转换失败,失败的原因是:", e);
                    throw e;
                }
                responseList.add(wrongQuesExerciseEntity);
            }
        }
    }


    /***
     * 组装复合题查询结果
     * @param resultList
     * @return
     */
    public static void assembleComplexQuestion(List<StudentExercise> resultList, List<WrongQuesExerciseEntity>
            responseList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (null != resultList && resultList.size() > 0) {
            //先按照日期年月日将数据归类
            // initTextFieldFromSsdb(resultList);
            Map dataMap = new HashMap(5);
            for (StudentExercise data : resultList) {
                Date updateTime = data.getLastUpdateTime();
                String sdfDate = sdf.format(updateTime);
                if (dataMap.containsKey(sdfDate)) {
                    List<StudentExercise> resList = (List<StudentExercise>) dataMap.get(sdfDate);
                    resList.add(data);
                    dataMap.put(sdfDate, resList);
                } else {
                    List<StudentExercise> resList = new ArrayList<>();
                    resList.add(data);
                    dataMap.put(sdfDate, resList);
                }
            }

            //将按照日期归类的数据按照答题来源和资源ID再次归类
            Map<String, List<StudentExercise>> sortDateMap = new TreeMap<>(new WrongQuestionUtil.MapKeyComparator());
            sortDateMap.putAll(dataMap);
            for (Map.Entry<String, List<StudentExercise>> entry : sortDateMap.entrySet()) {
                Map<String, List<StudentExercise>> secSortMap = new HashMap(10);
                WrongQuesExerciseEntity wrongQuesExerciseEntity = new WrongQuesExerciseEntity();
                String currData = entry.getKey();
                List<StudentExercise> currentList = entry.getValue();
                //对相同时间不同来源和资源ID的数据进行归类  其目的找到复合题下的所有小题
                for (StudentExercise data : currentList) {
                    String key = data.getResourceId() + "_" + data.getExerciseSource();
                    if (secSortMap.containsKey(key)) {
                        List<StudentExercise> resList = secSortMap.get(key);
                        resList.add(data);
                        secSortMap.put(key, resList);
                    } else {
                        List<StudentExercise> resList = new ArrayList<>();
                        resList.add(data);
                        secSortMap.put(key, resList);
                    }
                }
                List<StudentExercise> secList = new ArrayList<>();
                //获取同一天不同答题来源 数据
                for (Map.Entry<String, List<StudentExercise>> entry1 : secSortMap.entrySet()) {
                    List<StudentExercise> valueList = entry1.getValue();
                    StudentExercise entity = new StudentExercise();
                    Collections.sort(valueList, new WrongQuestionUtil.StudentExerciseComparator());
                    entity.setStudentExerciseList(valueList);
                    entity.setLastUpdateTime(valueList.get(0).getLastUpdateTime());
                    secList.add(entity);
                }
                wrongQuesExerciseEntity.setSingle(false);

                try {
                    wrongQuesExerciseEntity.setUpdateTime(DateUtil.getLongDate(currData + " 0:0:0"));
                } catch (BizLayerException e) {
                    logger.error("错题记录查询时间转换失败,失败的原因是:", e);
                    throw e;
                }
                Collections.sort(secList, new WrongQuestionUtil.StudentExerciseTimeComparator());
                wrongQuesExerciseEntity.setStudentExerciseList(secList);
                responseList.add(wrongQuesExerciseEntity);
            }

        }
    }

    /***
     *
     * @param in
     */
    public static Map assembleErrorMap(ErrorQuesChapQueryReq in) {
        Map map=new HashMap(1);
        Integer days=in.getDays();
        Date startDate;
        if (null!=days){
            startDate=getdate(-days);
            map.put("createTime","create_time>'"+startDate+"'");
        }else{
            map.put("createTime","1=1");
        }
        String tableName = TableNameUtil.getStuQuesKnowledge(in.getStudentId());
        map.put("studentId", in.getStudentId());
        map.put("tableName", tableName);
        map.put("dataType",in.getDataType());
        int level = in.getLevel();
        if (level == 1) {
            map.put("chapterName", "module_id");
            map.put("chapterData", in.getPointId());
        } else if (level == 2) {
            map.put("chapterName", "unit_id");
            map.put("chapterData", in.getPointId());
        } else if (level == 3) {
            map.put("chapterName", "topic_id");
            map.put("chapterData", in.getPointId());
        } else {
          throw  new BizLayerException("",PraxisErrorCode.LEVEL_ERROR);
        }

        return map;
    }

    public static Date getdate(int i) // //获取前后日期 i为正数 向后推迟i天，负数时向前提前i天
    {
        Date dat = null;
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DATE, i);
        dat = cd.getTime();
        SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp date = Timestamp.valueOf(dformat.format(dat));
        return date;
    }


    static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str2.compareTo(str1);
        }
    }


    static class StudentExerciseComparator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            // 强制转换
            StudentExercise p1 = (StudentExercise) object1;
            StudentExercise p2 = (StudentExercise) object2;
            return p1.getQuestionId().compareTo(p2.getQuestionId());
        }
    }

    static class StudentExerciseTimeComparator implements Comparator {
        @Override
        public int compare(Object object1, Object object2) {
            StudentExercise p1 = (StudentExercise) object1;
            StudentExercise p2 = (StudentExercise) object2;
            return p2.getLastUpdateTime().compareTo(p1.getLastUpdateTime());
        }
    }

}
