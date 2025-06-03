package org.frameworkset.nosql.s3;
/**
 * Copyright 2025 bboss
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author biaoping.yin
 * @Date 2025/6/3
 */
public class OSSConfig {

    private String name;
    private String endpoint;
    private String accessKeyId;
    private String secretAccesskey;
    private String region;
    public static final int PROTOCOL_S3 = 1;
    public static final int PROTOCOL_MINIO = 2;
    private int protocol = PROTOCOL_S3;
    private Boolean pathStyleAccess = true;
 



    private long maxFilePartSize = 10 * 1024 * 1024;

    private Long connectTimeout = 60000L;

    private Long readTimeout = 60000L;

    private Long writeTimeout = 60000L;
    private int poolMaxIdleConnections = 5;
    private Long poolKeepAliveDuration = 300000L;
    
    private Long connectionAcquisitionTimeout = 10000L;
    private Long socketTimeout = 300000L;
    private Boolean tcpKeepAlive ;
    
    public Long getConnectionMaxIdleTime() {
        return connectionMaxIdleTime;
    }

    public void setConnectionMaxIdleTime(Long connectionMaxIdleTime) {
        this.connectionMaxIdleTime = connectionMaxIdleTime;
    }

    private Long connectionMaxIdleTime = 600000L;
 

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    public long getMaxFilePartSize() {
        return maxFilePartSize;
    }

    public void setMaxFilePartSize(long maxFilePartSize) {
        this.maxFilePartSize = maxFilePartSize;
    }
    public Long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccesskey() {
        return secretAccesskey;
    }

    public void setSecretAccesskey(String secretAccesskey) {
        this.secretAccesskey = secretAccesskey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
 

    public int getPoolMaxIdleConnections() {
        return poolMaxIdleConnections;
    }

    public void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
        this.poolMaxIdleConnections = poolMaxIdleConnections;
    }

    public Long getPoolKeepAliveDuration() {
        return poolKeepAliveDuration;
    }

    public void setPoolKeepAliveDuration(Long poolKeepAliveDuration) {
        this.poolKeepAliveDuration = poolKeepAliveDuration;
    }
 

    public Boolean getPathStyleAccess() {
        return pathStyleAccess;
    }

    public void setPathStyleAccess(Boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }

    public Long getConnectionAcquisitionTimeout() {
        return connectionAcquisitionTimeout;
    }

    public void setConnectionAcquisitionTimeout(Long connectionAcquisitionTimeout) {
        this.connectionAcquisitionTimeout = connectionAcquisitionTimeout;
    }

    public Long getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Long socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Boolean getTcpKeepAlive() {
        return tcpKeepAlive;
    }

    public void setTcpKeepAlive(Boolean tcpKeepAlive) {
        this.tcpKeepAlive = tcpKeepAlive;
    }
}
