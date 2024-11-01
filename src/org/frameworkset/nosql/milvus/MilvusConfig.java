package org.frameworkset.nosql.milvus;
/**
 * Copyright 2024 bboss
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

import io.milvus.v2.client.ConnectConfig;

/**
 * <p>Description: https://milvus.io/api-reference/java/v2.4.x/v2/Client/MilvusClientV2Pool.md</p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/10/3
 */
public class MilvusConfig {
    private String name;
    private String uri;
    private String token;
    private Integer maxIdlePerKey;
    private Integer minIdlePerKey;
    private Integer maxTotalPerKey;
    private Integer maxTotal;
    private Boolean blockWhenExhausted;
    private Long maxBlockWaitDuration;
    private Long minEvictableIdleDuration;
    private Long evictionPollingInterval;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
    private String dbName;



    private Long connectTimeoutMs;
    private Long idleTimeoutMs;
    private CustomConnectConfigBuilder customConnectConfigBuilder;
    public Long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(Long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public String getName() {
        return name;
    }

    public MilvusConfig setName(String name) {
        this.name = name;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getMaxIdlePerKey() {
        return maxIdlePerKey;
    }

    public void setMaxIdlePerKey(Integer maxIdlePerKey) {
        this.maxIdlePerKey = maxIdlePerKey;
    }

    public Integer getMinIdlePerKey() {
        return minIdlePerKey;
    }

    public void setMinIdlePerKey(Integer minIdlePerKey) {
        this.minIdlePerKey = minIdlePerKey;
    }

    public Integer getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    public void setMaxTotalPerKey(Integer maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Boolean getBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(Boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public Long getMaxBlockWaitDuration() {
        return maxBlockWaitDuration;
    }

    public void setMaxBlockWaitDuration(Long maxBlockWaitDuration) {
        this.maxBlockWaitDuration = maxBlockWaitDuration;
    }

    public Long getMinEvictableIdleDuration() {
        return minEvictableIdleDuration;
    }

    public void setMinEvictableIdleDuration(Long minEvictableIdleDuration) {
        this.minEvictableIdleDuration = minEvictableIdleDuration;
    }

    public Long getEvictionPollingInterval() {
        return evictionPollingInterval;
    }

    public void setEvictionPollingInterval(Long evictionPollingInterval) {
        this.evictionPollingInterval = evictionPollingInterval;
    }

    public Boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(Boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public Boolean getTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(Boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Long getIdleTimeoutMs() {
        return idleTimeoutMs;
    }

    public void setIdleTimeoutMs(Long idleTimeoutMs) {
        this.idleTimeoutMs = idleTimeoutMs;
    }
    
    public void setCustomConnectConfigBuilder(CustomConnectConfigBuilder customConnectConfigBuilder){
        this.customConnectConfigBuilder = customConnectConfigBuilder;
    }

    public CustomConnectConfigBuilder getCustomConnectConfigBuilder() {
        return customConnectConfigBuilder;
    }
}
