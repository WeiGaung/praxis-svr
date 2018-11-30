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
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.SingleKeyTableShardingAlgorithm;

import java.util.Collection;

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
public final class StuQuesShardingAlgorithm implements SingleKeyTableShardingAlgorithm<Long> {

    @Override
    public String doEqualSharding(Collection<String> availableTargetNames, ShardingValue<Long> shardingValue) {
        for (String each : availableTargetNames) {
            String end = "_"+(shardingValue.getValue() % 100);
            if (each.endsWith(end)) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> doInSharding(Collection collection, ShardingValue shardingValue) {
        return null;
    }

    @Override
    public Collection<String> doBetweenSharding(Collection collection, ShardingValue shardingValue) {
        return null;
    }
}
