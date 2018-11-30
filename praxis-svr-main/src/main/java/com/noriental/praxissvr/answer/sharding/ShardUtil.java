package com.noriental.praxissvr.answer.sharding;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.google.common.collect.Range;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bluesky on 2016/6/24.
 */
public class ShardUtil {
    static  ShardingValue<?> getShardingValueByColumnName(String columnName, Collection<ShardingValue<?>> columnValues) {
        for(ShardingValue<?> shard:columnValues)
        {
            if(shard.getColumnName().equalsIgnoreCase(columnName))
            {
                return shard;
            }
        }
        return null;
    }
    static Set<String> getValuesByShard(ShardingValue<?> shardYear) {
        Set<Object> values = new HashSet<>();
        Set<String> valuesStr = new HashSet<>();
        if(shardYear==null){
            return valuesStr;
        }
        final Comparable<?> valueTypeYear = shardYear.getType();
        if(valueTypeYear == ShardingValue.ShardingValueType.SINGLE){
            values.add(shardYear.getValue());
        }else if(valueTypeYear == ShardingValue.ShardingValueType.LIST){
            values.addAll(shardYear.getValues());
        }else if(valueTypeYear == ShardingValue.ShardingValueType.RANGE){
            Range range = (Range) shardYear.getValueRange();
            for (long i = (long)range.lowerEndpoint(); i <= (long)range.upperEndpoint(); i++) {
                values.add(i + "");
            }
        }
        for(Object o : values){
            valuesStr.add(o.toString());
        }
        valuesStr.remove("");
        valuesStr.remove(null);
        return valuesStr;
    }
}
