package com.noriental.praxissvr.answer.request;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by 18270 on 2018/6/29.
 */
public class FlowTurnCorrectRequest extends BaseRequest{

    //老师system_id
    @NotNull
    private Long systemId;

    /**
     * 目录体系id
     */
    @NotNull
    private Long catalogGroupId;
    /**
     * 目录id (第一级)
     */
    private Long catalogId;

    /**
     * 自定义目录id  (第二级)
     */
    private Long catalogIdFirst;

    /**
     * 自定义目录id  (第三级)
     */
    private Long catalogIdSecond;

    /**
     * 未级节点
     */
    private List<Long> customListid;

    /**
     * 是否是未级节点id集
     *      1:是     0:否
     */
    /*private boolean isCustomListid=false;*/
    private Integer isCustomListid;


    //目录级别 1:一级 2:二级 3:三级
    private Integer level;

    //题id集
    private List<Long> questionList;

    //是否已批改 3:已批改 4:未批改
    @NotNull
    private Integer is_corrected;

    //排序类型  1 按时间排序  2 按待批改人数排序
    @NotNull
    private Integer sort_type;

    //升序或降序 1 asc   2 desc
    @NotNull
    private Integer sort_value;

    @NotNull
    private Integer pageSize;

    //分页
    @NotNull
    private PageBounds pageBounds;

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getCatalogGroupId() {
        return catalogGroupId;
    }

    public void setCatalogGroupId(Long catalogGroupId) {
        this.catalogGroupId = catalogGroupId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getCatalogIdFirst() {
        return catalogIdFirst;
    }

    public void setCatalogIdFirst(Long catalogIdFirst) {
        this.catalogIdFirst = catalogIdFirst;
    }

    public Long getCatalogIdSecond() {
        return catalogIdSecond;
    }

    public void setCatalogIdSecond(Long catalogIdSecond) {
        this.catalogIdSecond = catalogIdSecond;
    }

    public List<Long> getCustomListid() {
        return customListid;
    }

    public void setCustomListid(List<Long> customListid) {
        this.customListid = customListid;
    }

    public Integer getIsCustomListid() {
        return isCustomListid;
    }

    public void setIsCustomListid(Integer isCustomListid) {
        this.isCustomListid = isCustomListid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Long> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Long> questionList) {
        this.questionList = questionList;
    }

    public Integer getIs_corrected() {
        return is_corrected;
    }

    public void setIs_corrected(Integer is_corrected) {
        this.is_corrected = is_corrected;
    }

    public Integer getSort_type() {
        return sort_type;
    }

    public void setSort_type(Integer sort_type) {
        this.sort_type = sort_type;
    }

    public Integer getSort_value() {
        return sort_value;
    }

    public void setSort_value(Integer sort_value) {
        this.sort_value = sort_value;
    }

    public PageBounds getPageBounds() {
        return pageBounds;
    }

    public void setPageBounds(PageBounds pageBounds) {
        this.pageBounds = pageBounds;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
