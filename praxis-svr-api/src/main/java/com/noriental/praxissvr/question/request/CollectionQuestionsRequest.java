package com.noriental.praxissvr.question.request;

import com.noriental.praxissvr.question.bean.CollectionQuestion;
import com.noriental.validate.bean.BaseRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by liujiang on 2018/8/22.
 */
public class CollectionQuestionsRequest extends BaseRequest {

    @NotNull
    private List<CollectionQuestion> collectionQuestions;

    public List<CollectionQuestion> getCollectionQuestions() {
        return collectionQuestions;
    }

    public void setCollectionQuestions(List<CollectionQuestion> collectionQuestions) {
        this.collectionQuestions = collectionQuestions;
    }

    @Override
    public String toString() {
        return "CollectionQuestionsRequest{" +
                "collectionQuestions=" + collectionQuestions +
                '}';
    }
}
