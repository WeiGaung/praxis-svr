package com.noriental.praxissvr.wrong.service;

import com.noriental.praxissvr.wrong.request.UpdateRedoStatusRequest;
import com.noriental.validate.bean.CommonDes;

public interface StuRedoService {
    /**
     * 标记题目为已会了。
     * 消灭错题使用
     * @param in
     * @return
     * 该功能已经下线
     */
    @Deprecated
	CommonDes updateRedoStatus(UpdateRedoStatusRequest in);

}
