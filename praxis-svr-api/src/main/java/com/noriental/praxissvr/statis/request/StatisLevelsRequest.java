package com.noriental.praxissvr.statis.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by shengxian on 2016/12/22.
 */
public class StatisLevelsRequest extends BaseRequest{
    @NotEmpty
    private List<StatisLevelVo> statisLevels;
    @NotNull
    private Long studentId;
//    private Long subjectId;

    public List<StatisLevelVo> getStatisLevels() {
        return statisLevels;
    }

    public void setStatisLevels(List<StatisLevelVo> statisLevels) {
        this.statisLevels = statisLevels;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

//    public Long getSubjectId() {
//        return subjectId;
//    }
//
//    public void setSubjectId(Long subjectId) {
//        this.subjectId = subjectId;
//    }
}
