package com.noriental.praxissvr.answer.mappers;

import com.noriental.praxissvr.answer.bean.FlowTurn;
import com.noriental.praxissvr.answer.bean.FlowTurnList;
import com.noriental.praxissvr.answer.bean.StudentExercise;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kate
 * @create 2018-7-13
 * @desc 求助批改
 **/
@Repository
public interface PigaiCirculationMapper {

    List<FlowTurn> findFlowTurn(@Param("is_corrected") Integer is_corrected,@Param("systemId") Long systemId,@Param("questionList") List<Long> questionList,@Param("sort_type") Integer sort_type,@Param("sort_value") Integer sort_value,@Param("page") int page,@Param("limits") int limits);

    int findFlowTurnTotalCount(@Param("questionList") List<Long> questionList,@Param("is_corrected") Integer is_corrected, @Param("systemId") Long systemId);

    int findFlowTurnListTotalNum(FlowTurn flowTurn);
    //该题未批人数
    int findFlowTurnListNum(FlowTurn flowTurn);

    int findFlowTurnTotalNum(FlowTurn flowTurn);

    int findFlowTurnNum(FlowTurn flowTurn);

    FlowTurn findFlowTurnDeadline();

    List<FlowTurnList> selStuQuesAnswOnBatch(FlowTurn in);

    int updateFlowTurnCorrectState(StudentExercise se);

    int updateFlowTurnCorrectStateList(List<StudentExercise> list);

    int findFlowTurnExpire();

    Long findQuestionUploadId(@Param("questionId") Long questionId);

    int selectFIsExist(FlowTurn in);

    List<String> selectWhiteSchool();

    /*List<FlowTurn> findQuestionIdList(FlowTurn flowTurn1);*/
}
