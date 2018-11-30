package com.noriental.praxissvr.question.bean;

import java.util.Comparator;

public class QuesitonRecomdPriorityCompare implements
        Comparator<QuesitonRecommend> {

    @Override
    public int compare(QuesitonRecommend q1, QuesitonRecommend q2) {
        return q1.getRecommedPrior() - q2.getRecommedPrior();
    }

}
