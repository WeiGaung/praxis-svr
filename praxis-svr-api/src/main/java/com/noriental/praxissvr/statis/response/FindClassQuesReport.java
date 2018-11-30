package com.noriental.praxissvr.statis.response;

import java.io.Serializable;

/**
 * Created by bluesky on 2016/5/12.
 */
public class FindClassQuesReport implements Serializable {
   /**  正确数量（填空题空数）  **/
   private int rightCount;
   /**  错误数量（填空题空数）  **/
   private int wrongCount;
   /**  半对数量（填空题空数）  **/
   private int halfRightCount;
   /**  未批改数量（填空题空数）  **/
   private int correctNotCount;
   /**  提交人数  **/
   private int allSubmitedCount;

   public int getRightCount() {
      return rightCount;
   }

   public void setRightCount(int rightCount) {
      this.rightCount = rightCount;
   }

   public int getWrongCount() {
      return wrongCount;
   }

   public void setWrongCount(int wrongCount) {
      this.wrongCount = wrongCount;
   }

   public int getHalfRightCount() {
      return halfRightCount;
   }

   public void setHalfRightCount(int halfRightCount) {
      this.halfRightCount = halfRightCount;
   }

   public int getCorrectNotCount() {
      return correctNotCount;
   }

   public void setCorrectNotCount(int correctNotCount) {
      this.correctNotCount = correctNotCount;
   }

   public int getAllSubmitedCount() {
      return allSubmitedCount;
   }

   public void setAllSubmitedCount(int allSubmitedCount) {
      this.allSubmitedCount = allSubmitedCount;
   }
}
