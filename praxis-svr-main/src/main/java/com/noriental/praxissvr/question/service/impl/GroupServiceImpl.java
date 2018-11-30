package com.noriental.praxissvr.question.service.impl;

import com.noriental.praxissvr.question.bean.Group;
import com.noriental.praxissvr.question.mapper.QuestionGroupMapper;
import com.noriental.praxissvr.question.request.FindGroupByIdRequest;
import com.noriental.praxissvr.question.request.FindGroupBySystemIdRequest;
import com.noriental.praxissvr.question.response.FindGroupByIdResponse;
import com.noriental.praxissvr.question.response.FindGroupBySystemIdResponse;
import com.noriental.praxissvr.question.service.GroupService;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.List;

import static com.noriental.praxissvr.exception.PraxisErrorCode.GROUP_NOT_FOUND;

/**
 * @author chenlihua
 * @date 2015/12/23
 * @time 11:14
 */
@Service("quiz2.groupService")
public class GroupServiceImpl implements GroupService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Resource
    private QuestionGroupMapper questionGroupMapper;

    @Override
    public FindGroupBySystemIdResponse findGroupBySystemId(FindGroupBySystemIdRequest request) {
        List<Group> groupList = questionGroupMapper.findBySystemId(request.getSystemId());
        FindGroupBySystemIdResponse resp = new FindGroupBySystemIdResponse();
        resp.setList(groupList);
        return resp;
    }

    @Override
    public FindGroupByIdResponse findGroupById(FindGroupByIdRequest request) {
        long id = request.getId();
        Group group = questionGroupMapper.findById(id);
        if (group == null) {
            logger.error("group not exit, groupId:{}", id);
            throw new BizLayerException("", GROUP_NOT_FOUND);
        }
        FindGroupByIdResponse resp = new FindGroupByIdResponse();
        resp.setGroup(group);
        return resp;
    }
}
