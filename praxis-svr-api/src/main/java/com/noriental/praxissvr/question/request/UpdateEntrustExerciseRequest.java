package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.question.constaints.EntrustStatusEnum;
import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:13
 */
public class UpdateEntrustExerciseRequest extends BaseRequest {
    @Min(1)
    private long approve_system_id;
    @Min(1)
    private long id;
    @NotBlank
    private String approve_system_name;
    @NotEmpty
    private EntrustStatusEnum status;
    @NotNull
    private Long cusDirId;  //自定义目录
    @NotNull
    private Long groupId;   //自定义目录体系ID

//    @NotBlank
    private String approve_desc;

    public UpdateEntrustExerciseRequest(long id,long approve_system_id,  String approve_system_name, EntrustStatusEnum status, String approve_desc) {
        this.approve_system_id = approve_system_id;
        this.id = id;
        this.approve_system_name = approve_system_name;
        this.status = status;
        this.approve_desc = approve_desc;
    }

    public long getApprove_system_id() {
        return approve_system_id;
    }

    public void setApprove_system_id(long approve_system_id) {
        this.approve_system_id = approve_system_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApprove_system_name() {
        return approve_system_name;
    }

    public void setApprove_system_name(String approve_system_name) {
        this.approve_system_name = approve_system_name;
    }

    public EntrustStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EntrustStatusEnum status) {
        this.status = status;
    }

    public String getApprove_desc() {
        return approve_desc;
    }

    public void setApprove_desc(String approve_desc) {
        this.approve_desc = approve_desc;
    }

    public Long getCusDirId() {
        return cusDirId;
    }

    public void setCusDirId(Long cusDirId) {
        this.cusDirId = cusDirId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
