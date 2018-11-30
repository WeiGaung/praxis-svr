package com.noriental.praxissvr.answer.service;

import com.noriental.praxissvr.answer.request.UpdateFinshCorrectRequest;
import com.noriental.validate.bean.CommonDes;

public interface AnswerPushService {
    CommonDes updateFinshCorrect(UpdateFinshCorrectRequest req);
    //小云自主学习推送
}
