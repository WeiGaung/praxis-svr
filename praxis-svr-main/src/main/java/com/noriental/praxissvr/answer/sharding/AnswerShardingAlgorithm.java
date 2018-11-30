/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.noriental.praxissvr.answer.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.MultipleKeysTableShardingAlgorithm;
import com.noriental.validate.exception.BizLayerException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.noriental.praxissvr.exception.PraxisErrorCode.INNER_ERROR;

/**
 * 执行 具体的分片
 *
 * 整体分片 规则
 *
 * 年份 + HASH(class_id)  ：目前表名加后缀 _{yyyy}_{hash}，其中hash保留两位，值为classId最后两位除以2
 *
 * 如果 要查询 classId =0 的，需要提供 studentId 因为 classId = 0时，使用 studentId 进行的hash
 *
 */
public final class AnswerShardingAlgorithm implements MultipleKeysTableShardingAlgorithm {

    @Override
    public Collection<String> doSharding(Collection<String> tableNames, Collection<ShardingValue<?>> columnValues) {
        ShardingValue<?> shardClassId = ShardUtil.getShardingValueByColumnName("class_id", columnValues);
        ShardingValue<?> shardStudentId =  ShardUtil.getShardingValueByColumnName("student_id", columnValues);
        ShardingValue<?> shardYear =  ShardUtil.getShardingValueByColumnName("year", columnValues);

        Set<String> classIdValues = ShardUtil.getValuesByShard(shardClassId);
        Set<String> studentIdValues = ShardUtil.getValuesByShard(shardStudentId);
        Set<String> yearValues =   ShardUtil.getValuesByShard(shardYear);

        checkRequiredValue(classIdValues, studentIdValues, yearValues);

        Set<String> oneShardValues = getOneShardValues(classIdValues, studentIdValues);
        Set<String> oneShardValuesCoverted = convertNamePart(oneShardValues);
        Set<String> yearValuesCoverted =   convertNamePartYear(yearValues);

        Collection<String> atableNames = getTableNames(tableNames,yearValuesCoverted,oneShardValuesCoverted);

        if(CollectionUtils.isNotEmpty(atableNames))
        {
            return atableNames;
        }
        throw new UnsupportedOperationException();
    }

    private Set<String> convertNamePartYear(Set<String> yearValues) {
        Set<String> values = new HashSet<>();
        for(String s : yearValues){
            values.add("_"+s+"_");
        }
        return values;
    }

    private Collection<String> getTableNames(Collection<String> tableNames, Set<String> yearValuesCoverted, Set<String> oneShardValuesCoverted) {
        ArrayList<String> tables = new ArrayList<>();
        for (String tableName : tableNames) {
            for(String yearPart:yearValuesCoverted)
                if (tableName.indexOf(yearPart) >= 0) {
                    for (String onePart : oneShardValuesCoverted) {
                        if (tableName.endsWith(onePart)) {
                            tables.add(tableName);
                        }
                    }
                }
        }
        return tables;
    }

    private Set<String> convertNamePart(Set<String> oneShardValues) {
        Set<String> values = new HashSet<>();
        for(String s : oneShardValues){
            values.add("_"+String.valueOf(Long.valueOf(s) % 100 / 2 ));
        }
        return values;
    }


    private Set<String> getOneShardValues(Set<String> classIdValues,Set<String> studentIdValues) {
        if(CollectionUtils.isNotEmpty(classIdValues)){
            return classIdValues;
        }else{
            return studentIdValues;
        }
    }



    private void checkRequiredValue( Set<String> classIdValues, Set<String> studentIdValues, Set<String> yearValues){
        if(CollectionUtils.isEmpty(classIdValues) && CollectionUtils.isEmpty(studentIdValues))
        {
            throw new BizLayerException("class_id or shardStudentId should provided", INNER_ERROR);
        }
        if(CollectionUtils.isEmpty(yearValues))
        {
            throw new BizLayerException("year should provided",INNER_ERROR);
        }
    }

    public static void main(String[] args) {
        System.out.println(18%2);
    }

}
