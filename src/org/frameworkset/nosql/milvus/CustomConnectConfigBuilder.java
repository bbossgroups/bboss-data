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
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/1
 */
public interface CustomConnectConfigBuilder {
    /**
     * 增加自定义的Milvus连接参数配置，例如安全认证配置等等，默认支持配置有：
     *  connectConfigBuilder
     *                             .uri(milvusConfig.getUri())
     *                             .token(milvusConfig.getToken()) // replace this with your token
     *                             .dbName(milvusConfig.getDbName())
     *                             .connectTimeoutMs(milvusConfig.getConnectTimeoutMs())  
     *                             .idleTimeoutMs(milvusConfig.getIdleTimeoutMs());
     * 其他参数配置可以通过接口方法自行设置                            
     * @param connectConfigBuilder
     */
    public void customConnectConfigBuilder(ConnectConfig.ConnectConfigBuilder connectConfigBuilder);

}
