package com.noriental.praxissvr.statis.dao.impl;

import com.noriental.dao.BaseDaoImpl;
import com.noriental.praxissvr.statis.bean.StuWorkStatis;
import com.noriental.praxissvr.statis.dao.StuWorkStatisDao;
import com.noriental.praxissvr.statis.request.StatisLevelsRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StuWorkStatisDaoImpl extends BaseDaoImpl<StuWorkStatis, Long> implements StuWorkStatisDao {
    private static final String namespace = StuWorkStatis.class.getName();

    @Override
    public List<StuWorkStatis> getStuWorkStatis( long stuId,long subjectId) {
        StuWorkStatis stuWorkStatis = new StuWorkStatis();
        stuWorkStatis.setStudentId(stuId);
        stuWorkStatis.setSubjectId(subjectId);
        return super.findList(namespace + ".findstuworkstatis", stuWorkStatis);
    }

    @Override
    public List<StuWorkStatis> findstuworkstatisByIds(StatisLevelsRequest vo) {
        return super.findList(namespace + ".findstuworkstatisByIds", vo);
    }

    @Override
    public boolean isExist(StuWorkStatis stuWorkStatis) {
        return super.count(namespace + ".countByEntity", stuWorkStatis) > 0;
    }

    @Override
    public boolean updateNumberByEntity(StuWorkStatis stuWorkStatis) {
        return super.update(namespace + ".updateNumberByEntity", stuWorkStatis);
    }

    @Override
    public List<StuWorkStatis> findListByEntity(StuWorkStatis stuWorkStatis) {
        return super.findList(namespace + ".findListByEntity", stuWorkStatis);
    }

    @Override
    public List<StuWorkStatis> findListByEntityList(List<StuWorkStatis> stuWorkStatis) {
        return super.findList(namespace + ".findListByEntityList", stuWorkStatis);
    }
    @Override
    public boolean creates(List<StuWorkStatis> list) {
        return this.update(this.namespace + ".inserts", list);
    }
    @Override
    public boolean updateByEntityList(List<StuWorkStatis> list) {
        return this.update(this.namespace + ".updateByEntityList", list);
    }
}
