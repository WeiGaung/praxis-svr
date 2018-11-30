package com.noriental.praxissvr.question.bean;

/**
 * Created by liujiang on 2018/8/17.
 */
public class EntityCounterResources {
    private Long objectId;
    private Long favCount;
    private Long refCount;

    public Long getRefCount() {
        return refCount;
    }

    public void setRefCount(Long refCount) {
        this.refCount = refCount;
    }

    @Override
    public String toString() {
        return "EntityCounterResources{" +
                "objectId=" + objectId +
                ", favCount=" + favCount +
                ", refCount=" + refCount +
                '}';
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getFavCount() {
        return favCount;
    }

    public void setFavCount(Long favCount) {
        this.favCount = favCount;
    }
}
