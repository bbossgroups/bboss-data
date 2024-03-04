package org.frameworkset.nosql.mongodb;
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

import com.mongodb.MongoClientSettings;

/**
 * <p>Description: 自定义clientBuilder</p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/3/4
 */
public interface CustomSettingBuilder {
    /**
     * 自定义MongoDB 客户端参数，例如:自定义ssl配置
     *             clientBuilder.applyToSslSettings(builder -> {
     *                 builder.invalidHostNameAllowed(true);
     *                 builder.enabled(true);
     *                 builder.context(sscontext);
     *             });
     * @param clientBuilder
     * @param mongoDBConfig
     */
    void customSettingBuilder(MongoClientSettings.Builder clientBuilder,MongoDBConfig mongoDBConfig);

}
