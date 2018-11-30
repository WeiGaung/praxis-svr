package com.noriental.praxissvr.answer.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by bluesky on 2016/5/10.
 */
public class FindStuAnswsOnBatchRequest extends BaseRequest{
    @NotNull
    private Long studentId;
    @NotNull
    private Long resourceId;
    @NotBlank
    private String exerciseSource;
    //错题消灭来源
    private String redoSource;
    private boolean subQuesSort=false;
    private boolean isForPad=false;
    public FindStuAnswsOnBatchRequest() {
    }



    public FindStuAnswsOnBatchRequest(Long studentId, Long resourceId, String exerciseSource, String redoSource, List<Integer> structIdList) {
        this.studentId = studentId;
        this.resourceId = resourceId;
        this.exerciseSource = exerciseSource;
        this.redoSource = redoSource;
        this.structIdList = structIdList;
    }

    private List<Integer> structIdList;
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }


    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getExerciseSource() {
        return exerciseSource;
    }

    public void setExerciseSource(String exerciseSource) {
        this.exerciseSource = exerciseSource;
    }

    public List<Integer> getStructIdList() {
        return structIdList;
    }

    public void setStructIdList(List<Integer> structIdList) {
        this.structIdList = structIdList;
    }

    public String getRedoSource() {
        return redoSource;
    }

    public void setRedoSource(String redoSource) {
        this.redoSource = redoSource;
    }

    public boolean isSubQuesSort() {
        return subQuesSort;
    }

    public void setSubQuesSort(boolean subQuesSort) {
        this.subQuesSort = subQuesSort;
    }

    public boolean isForPad() {
        return isForPad;
    }

    public void setForPad(boolean forPad) {
        isForPad = forPad;
    }
}
