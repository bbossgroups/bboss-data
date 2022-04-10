package org.frameworkset.nosql.redis;
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

import redis.clients.jedis.Protocol;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2022/4/9
 * @author biaoping.yin
 * @version 1.0
 */
public class RedisConfig {
	private String name;
	/**
	 * 认证口令
	 */
	private String auth;
	/**
	 * 最大空闲数
	 */
	private int maxIdle = -1;
	/**
	 * 最小空闲数
	 */
	private int minIdle = -1;
	/**
	 *
	 */
	private int maxRedirections = 5;

	private Map<String,Object> properties;
	/**
	 * single|cluster|shared
	 */
	private String mode = RedisDB.mode_cluster;
	private boolean needAuthPerJedis = false;
	private int poolMaxTotal;
	private long poolMaxWaitMillis;
	/**
	 * 等待超时重试次数
	 */
	private int poolTimeoutRetry  = 3;
	/**
	 * 等待超时重试时间间隔
	 */
	private long poolTimeoutRetryInterval  = 500l;
	private String servers;
	/**
	 * connectionTimeout
	 */
	private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	/**
	 * socketTimeout
	 */
	private int socketTimeout =  Protocol.DEFAULT_TIMEOUT;
	private boolean testOnBorrow = false;
	private boolean testOnReturn = false;
	private boolean testWhileIdle = false;

	public Map<String, Object> getProperties() {
		return properties;
	}

	public RedisConfig setProperties(Map<String, Object> properties) {
		this.properties = properties;
		return this;
	}

	public RedisConfig addProperity(String name,Object value){
		if(properties == null){
			properties = new LinkedHashMap<>();
		}
		properties.put(name, value);
		return this;
	}

	public String getAuth() {
		return auth;
	}

	public RedisConfig setAuth(String auth) {
		this.auth = auth;
		return this;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public RedisConfig setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
		return this;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public RedisConfig setMinIdle(int minIdle) {
		this.minIdle = minIdle;
		return this;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public RedisConfig setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
		return this;
	}

	public String getMode() {
		return mode;
	}

	public RedisConfig setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public boolean isNeedAuthPerJedis() {
		return needAuthPerJedis;
	}

	public RedisConfig setNeedAuthPerJedis(boolean needAuthPerJedis) {
		this.needAuthPerJedis = needAuthPerJedis;
		return this;
	}

	public int getPoolMaxTotal() {
		return poolMaxTotal;
	}

	public RedisConfig setPoolMaxTotal(int poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
		return this;
	}

	public long getPoolMaxWaitMillis() {
		return poolMaxWaitMillis;
	}

	public RedisConfig setPoolMaxWaitMillis(long poolMaxWaitMillis) {
		this.poolMaxWaitMillis = poolMaxWaitMillis;
		return this;
	}

	public int getPoolTimeoutRetry() {
		return poolTimeoutRetry;
	}

	public RedisConfig setPoolTimeoutRetry(int poolTimeoutRetry) {
		this.poolTimeoutRetry = poolTimeoutRetry;
		return this;
	}

	public long getPoolTimeoutRetryInterval() {
		return poolTimeoutRetryInterval;
	}

	public RedisConfig setPoolTimeoutRetryInterval(long poolTimeoutRetryInterval) {
		this.poolTimeoutRetryInterval = poolTimeoutRetryInterval;
		return this;
	}

	public String getServers() {
		return servers;
	}

	public RedisConfig setServers(String servers) {
		this.servers = servers;
		return this;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public RedisConfig setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public RedisConfig setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public RedisConfig setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
		return this;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public RedisConfig setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
		return this;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public RedisConfig setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
		return this;
	}

	public String getName() {
		return name;
	}

	public RedisConfig setName(String name) {
		this.name = name;
		return this;
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Redis datasourceName:")
				.append(getName())
				.append(",servers:")
				.append(servers)
				.append(",auth:******,mode:")
				.append(mode)
				.append(",needAuthPerJedis:")
				.append(needAuthPerJedis)
				.append(",poolMaxTotal:")
				.append(poolMaxTotal)
				.append(",poolMaxWaitMillis:")
				.append(poolMaxWaitMillis)
				.append(",poolTimeoutRetry:")
				.append(poolTimeoutRetry)
				.append(",poolTimeoutRetryInterval:")
				.append(poolTimeoutRetryInterval)
				.append(",connectionTimeout:")
				.append(connectionTimeout)
				.append(",socketTimeout:")
				.append(socketTimeout)
				.append(",maxIdle:")
				.append(maxIdle)
				.append(",minIdle:")
				.append(minIdle)
				.append(",maxRedirections:")
				.append(maxRedirections)
				.append(",testOnBorrow:")
				.append(testOnBorrow)
				.append(",testOnReturn:")
				.append(testOnReturn)
				.append(",testWhileIdle:")
				.append(testWhileIdle)
				.append(",properties:");
		if(properties != null && properties.size() > 0){
			Iterator<Map.Entry<String,Object>> iterator = properties.entrySet().iterator();
			builder.append("{");
			int i = 0;
			while (iterator.hasNext()){
				if(i > 0){
					builder.append(",");
				}
				Map.Entry<String,Object> entry = iterator.next();
				builder.append(entry.getKey())
						.append(entry.getValue());

				i ++;
			}
			builder.append("}");

		}
		return builder.toString();
	}
}
