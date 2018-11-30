package com.noriental.praxissvr.question.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.noriental.praxissvr.question.bean.SuperQuestionMongoHtml;
import com.noriental.praxissvr.question.bean.SuperQuestionSsdbHtml;
import com.noriental.praxissvr.question.utils.QuestionSsdbUtil;
import com.noriental.utils.json.JsonUtil;
import com.noriental.utils.ssdb.SSDBUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.lang.invoke.MethodHandles;
import java.util.*;

@Repository
public class QuestionHtmlSsdbDao {

    @Resource
    private SSDBUtil ssdbUtil;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public SuperQuestionMongoHtml getQuestionMongoHtmlByQid(long id) {
        String htmlKey = QuestionSsdbUtil.getKey(id + "", "html");
        String html = ssdbUtil.get(htmlKey);
        if (StringUtils.isNotBlank(html)) {
            Map<String, Object> map = JsonUtil.readValue(html, new TypeReference<Map<String, Object>>() {
            });
            if (MapUtils.isEmpty(map)) {
                SuperQuestionMongoHtml superQuestionMongoHtml = new SuperQuestionMongoHtml();
                superQuestionMongoHtml.setContent(map);
                superQuestionMongoHtml.setQuestion_id(String.valueOf(id));
                return superQuestionMongoHtml;
            }
        }
        return null;
    }

    public Map<Long, SuperQuestionSsdbHtml> findByIds(List<Long> ids) {
        Map<Long, SuperQuestionSsdbHtml> htmlHashMap = new HashMap<>();

        if (CollectionUtils.isNotEmpty(ids)) {

            ids = new ArrayList<>(new HashSet<>(ids));

            List<String> keys = new ArrayList<>();
            Map<String, Long> keyQuesMap = new HashMap<>();
            for (Long id : ids) {
                String key = QuestionSsdbUtil.getKey(String.valueOf(id) + "", "html");
                keys.add(key);
                keyQuesMap.put(key, id);
            }
            long l5 = System.currentTimeMillis();
            Map<String, String> quesHtmlStrMap = ssdbUtil.mget(keys);
            long l7 = System.currentTimeMillis();
            logger.info("ssdbUtil mget cost:{}ms", l7 - l5);

            if (MapUtils.isNotEmpty(quesHtmlStrMap)) {

                Set<Map.Entry<String, String>> entries = quesHtmlStrMap.entrySet();

                for (Map.Entry<String, String> quesHtmlEntry : entries) {

                    String ssdbkey = quesHtmlEntry.getKey();
                    String html = quesHtmlEntry.getValue();

                    SuperQuestionSsdbHtml htmlMap = JsonUtil.readValue(html, SuperQuestionSsdbHtml.class);

                    Long questionId = keyQuesMap.get(ssdbkey);
                    htmlHashMap.put(questionId, htmlMap);
                }
            }

        }

        return htmlHashMap;
    }

}
