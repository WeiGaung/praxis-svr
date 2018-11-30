package com.noriental.praxissvr.statis.dao;

import com.noriental.dao.BaseDao;
import com.noriental.praxissvr.statis.bean.StuWorkStatis;
import com.noriental.praxissvr.statis.request.StatisLevelsRequest;

import java.util.List;

public interface StuWorkStatisDao extends BaseDao<StuWorkStatis, Long> {

    List<StuWorkStatis> getStuWorkStatis(long stuId,long subjectId);


    List<StuWorkStatis> findstuworkstatisByIds(StatisLevelsRequest vo);

    /**
     * 是否存在实体数据记录
     *
     * @param stuWorkStatis 查询实体
     * @return
     */
    boolean isExist(StuWorkStatis stuWorkStatis);

    /**
     * 根据实体指定属性查找更新表<br/>
     * <br/>
     * 查找条件：studentId、stuType、subjectId、moduleId、topicId <br/>
     * 更新属性：rightNumber、answerNumber
     *
     * @return
     */
    boolean updateNumberByEntity(StuWorkStatis stuWorkStatis);

    /**
     * 根据实体条件查找数据记录
     *
     * @param stuWorkStatis 查询实体
     * @return
     */
    List<StuWorkStatis> findListByEntity(StuWorkStatis stuWorkStatis);

    List<StuWorkStatis> findListByEntityList(List<StuWorkStatis> stuWorkStatis);

    boolean creates(List<StuWorkStatis> list);

    boolean updateByEntityList(List<StuWorkStatis> list);
}
