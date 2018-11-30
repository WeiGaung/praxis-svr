package com.noriental.praxissvr.question.dao.impl;

import com.noriental.praxissvr.question.bean.SuperQuestionSsdb;
import com.noriental.praxissvr.question.utils.QuestionSsdbUtil;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.ssdb.SSDBUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;

@Service
public class QuestionJsonSsdbDao {
    private static final String QUESTION_KEY = "question_id";

    @Autowired
    private SSDBUtil ssdbUtil;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public Map<Long, SuperQuestionSsdb> findByIds(List<Long> ids) {
        Map<Long, SuperQuestionSsdb> htmlHashMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(ids)){

            ids = new ArrayList<>(new HashSet<>(ids));

            List<String> keys = new ArrayList<>();
            Map<String,Long> keyQuesMap = new HashMap<>();
            for (Long id: ids) {
                String key = QuestionSsdbUtil.getKey(String.valueOf(id) + "", "json");
                keys.add(key);
                keyQuesMap.put(key,id);
            }
            long l5 = System.currentTimeMillis();
            Map<String, String> quesHtmlStrMap = ssdbUtil.mget(keys);
            long l7 = System.currentTimeMillis();

            logger.info("ssdbUtil mget cost:{}ms", l7 - l5);

            if(MapUtils.isNotEmpty(quesHtmlStrMap)){

                Set<Map.Entry<String, String>> entries = quesHtmlStrMap.entrySet();

                for (Map.Entry<String, String> quesHtmlEntry: entries) {

                    String ssdbkey = quesHtmlEntry.getKey();
                    String html = quesHtmlEntry.getValue();

                    SuperQuestionSsdb content = JsonUtil.readValue(html, SuperQuestionSsdb.class);

                    Long questionId = keyQuesMap.get(ssdbkey);
                    htmlHashMap.put(questionId,content);
                }

            }

        }
        return  htmlHashMap;

    }



}
