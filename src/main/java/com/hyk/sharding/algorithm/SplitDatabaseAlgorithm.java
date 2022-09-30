package com.hyk.sharding.algorithm;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 分片策略
 *
 */
@Component
public final class SplitDatabaseAlgorithm implements StandardShardingAlgorithm<String> {

  @Override
  public String getType() {
    return "";
  }


  @Override
  public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
    Map<String, String> dataBaseRuleMap = new HashMap<>();
    dataBaseRuleMap.put("1","dataSource1");
    dataBaseRuleMap.put("2","dataSource2");
    dataBaseRuleMap.put("3","dataSource3");
    return dataBaseRuleMap.get(shardingValue.getValue());
  }

  @Override
  public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
    return null;
  }

  @Override
  public Properties getProps() {
    return null;
  }

  @Override
  public void init(Properties properties) {
  }
}
