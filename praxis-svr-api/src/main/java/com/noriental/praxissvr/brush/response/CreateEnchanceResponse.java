package com.noriental.praxissvr.brush.response;

import com.noriental.validate.bean.CommonDes;

import java.io.Serializable;
import java.util.List;

public class CreateEnchanceResponse extends CommonDes implements Serializable {
   private List<Long> questionIds;
   private Long resourceId;

   public Long getResourceId() {
      return resourceId;
   }

   public void setResourceId(Long resourceId) {
      this.resourceId = resourceId;
   }

   public List<Long> getQuestionIds() {
      return questionIds;
   }

   public void setQuestionIds(List<Long> questionIds) {
      this.questionIds = questionIds;
   }
}
