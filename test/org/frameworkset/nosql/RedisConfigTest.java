package org.frameworkset.nosql;
/**
 * Copyright 2022 bboss
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.frameworkset.nosql.redis.RedisConfig;
import org.frameworkset.nosql.redis.RedisDB;
import org.frameworkset.nosql.redis.RedisFactory;
import org.frameworkset.nosql.redis.RedisTool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2022/4/10
 * @author biaoping.yin
 * @version 1.0
 */
public class RedisConfigTest {
	@Before
	public void init(){
		//构建名称为test的redis数据源，可以通过RedisFactory.builRedisDB构建其他的数据源
		//不同的数据源设置不同的name，如果对应的name已经被其他redis集群使用，则忽略创建
		RedisConfig redisConfig = new RedisConfig();
		redisConfig.setName("test")
				.setAuth("")
				//集群节点可以通过逗号分隔，也可以通过\n符分隔
//				.setServers("10.13.4.15:6359\n10.13.4.15:6369\n10.13.4.15:6379\n10.13.4.15:6389")
				.setServers("10.13.4.15:6359,10.13.4.15:6369,10.13.4.15:6379,10.13.4.15:6389")
				.setMaxRedirections(5)
				.setMode(RedisDB.mode_cluster)
				.setConnectionTimeout(10000)
				.setSocketTimeout(10000)
				.setPoolMaxWaitMillis(2000)
				.setPoolMaxTotal(50)
				.setPoolTimeoutRetry(3)
				.setPoolTimeoutRetryInterval(500l)
				.setMaxIdle(-1)
				.setMinIdle(-1)
				.setTestOnBorrow(true)
				.setTestOnReturn(false)
				.setTestWhileIdle(false)
				.setProperties(new LinkedHashMap<>());
		RedisFactory.builRedisDB(redisConfig);

	}
	@Test
	public void test(){
		//使用test数据源操作对应的redis集群
		RedisTool.getInstance("test").set("aaa","ddd");
		RedisTool.getInstance("test").hset("ddd","aaa","xxxx");
		Assert.assertEquals("ddd",RedisTool.getInstance("test").get("aaa"));
	}
}
