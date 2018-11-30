package com.noriental.praxissvr.question.mapper;

import com.noriental.praxissvr.question.bean.solrBean.QueryDataForSolrBean;
import org.springframework.stereotype.Repository;

/**
 * Created by kate on 2016/12/1.
 */
@Repository
public interface QueryDataForSolrMapper {

    QueryDataForSolrBean findDataForSolr(Long id);



}
