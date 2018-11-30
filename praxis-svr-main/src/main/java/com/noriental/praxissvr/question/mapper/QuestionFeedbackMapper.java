package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.CreateQuestionFeedback;
import com.noriental.praxissvr.question.bean.QuestionFeedback;
import com.noriental.praxissvr.question.request.FindQuestionFeedbacksRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface QuestionFeedbackMapper {

    List<QuestionFeedback> findQuestionFeedbacks(FindQuestionFeedbacksRequest request);


    boolean createQuestionFeedback(CreateQuestionFeedback create);

    /**
     * 获取问题上传人所属学校
     * @param systemId 上传人id
     * @return
     */
    String getQuesUploadSchool(@Param("systemId") Long systemId);
}
