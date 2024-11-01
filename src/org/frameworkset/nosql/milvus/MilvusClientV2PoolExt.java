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

import io.milvus.pool.ClientPool;
import io.milvus.pool.PoolConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/11/1
 */
public class MilvusClientV2PoolExt  extends ClientPool<ConnectConfig, MilvusClientV2> {
    public MilvusClientV2PoolExt(PoolConfig poolConfig, ConnectConfig connectConfig) throws ClassNotFoundException, NoSuchMethodException {
        super(poolConfig, new PoolClientFactoryExt<ConnectConfig, MilvusClientV2>(connectConfig, MilvusClientV2.class.getName()));
    }

    /**
     * Get a client object which is idle from the pool.
     * Once the client is hold by the caller, it will be marked as active state and cannot be fetched by other caller.
     * If the number of clients hits the MaxTotalPerKey value, this method will be blocked for MaxBlockWaitDuration.
     * If no idle client available after MaxBlockWaitDuration, this method will return a null object to caller.
     *
     * @param key the key of a group where the client belong
     * @return MilvusClient or MilvusClientV2
     */
    public MilvusClientV2 getClient(String key)  {
        try {
            return clientPool.borrowObject(key);
        }
        catch (InvocationTargetException e) {
//            logger.error("Failed to get client, exception: ", e);
//            return null;
            throw new DataMilvusException("Failed to get client, exception: ",e.getTargetException());
        }
        catch (Exception e) {
//            logger.error("Failed to get client, exception: ", e);
//            return null;
            throw new DataMilvusException("Failed to get client, exception: ",e);
        }
    }
}
