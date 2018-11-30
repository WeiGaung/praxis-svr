package com.noriental.praxissvr.question.mapper;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.question.bean.EntrustUpload;
import com.noriental.praxissvr.question.request.UpdateEntrustExerciseRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrustUploadMapper {

//    PageList<EntrustUpload> findEntrustExercises(FindEntrustExercisesRequest request);

    PageList<EntrustUpload> findEntrustExercises(@Param("teacherId") long teacherId, @Param("status") Integer status, PageBounds pageBounds);


    boolean updateEntrustExercise(UpdateEntrustExerciseRequest request);

    int insert(EntrustUpload record);
}
