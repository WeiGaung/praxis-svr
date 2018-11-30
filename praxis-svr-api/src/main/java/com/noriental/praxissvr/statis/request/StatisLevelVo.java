package com.noriental.praxissvr.statis.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by shengxian on 2016/12/22.
 */
public class StatisLevelVo implements Serializable{
    @NotNull
    @Min(1)
    private  Integer level;
    @NotNull
    @Min(1)
    private  Long levelId;

    public StatisLevelVo(Integer level, Long levelId) {
        this.level = level;
        this.levelId = levelId;
    }

    public StatisLevelVo() {
    }

    public Integer getLevel() {
        return level;
    }


    public Long getLevelId() {
        return levelId;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}
