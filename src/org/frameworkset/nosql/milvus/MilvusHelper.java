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

import com.frameworkset.util.SimpleStringUtil;
import io.milvus.pool.MilvusClientV2Pool;
import io.milvus.pool.PoolConfig;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/10/3
 */
public class MilvusHelper {
    private static Logger logger = LoggerFactory.getLogger(MilvusHelper.class);
    private static Map<String, Milvus> milvusContainer = new ConcurrentHashMap<>();

    public static Milvus getMilvus(String name){
        return milvusContainer.get(name);
    }
    public static void validate(MilvusConfig milvusConfig){
        if(SimpleStringUtil.isEmpty(milvusConfig.getName())){
            throw new DataMilvusException("milvusConfig.getName():"+milvusConfig.getName());
        }

        if(SimpleStringUtil.isEmpty(milvusConfig.getUri())){
            throw new DataMilvusException("milvusConfig.getUri():"+milvusConfig.getUri());
        }

        if(SimpleStringUtil.isEmpty(milvusConfig.getDbName())){
            throw new DataMilvusException("milvusConfig.getDbName():"+milvusConfig.getDbName());
        }
    }
    /**
     * 初始化minio客户端
     * @param milvusConfig
     * @return
     */
    public static MilvusStartResult init(MilvusConfig milvusConfig){
        validate(milvusConfig);
        MilvusStartResult milvusStartResult = new MilvusStartResult();
        if(!milvusContainer.containsKey(milvusConfig.getName())){
            synchronized (milvusContainer){
                if(!milvusContainer.containsKey(milvusConfig.getName())){
                    ConnectConfig.ConnectConfigBuilder<?, ?> connectConfigBuilder = ConnectConfig.builder();
                    connectConfigBuilder
                            .uri(milvusConfig.getUri())
                            .token(milvusConfig.getToken()) // replace this with your token
                            .dbName(milvusConfig.getDbName());
                    if(milvusConfig.getConnectTimeoutMs() != null && milvusConfig.getConnectTimeoutMs() > 0L)
                        connectConfigBuilder.connectTimeoutMs(milvusConfig.getConnectTimeoutMs());

                    if(milvusConfig.getIdleTimeoutMs() != null && milvusConfig.getIdleTimeoutMs() > 0L)
                        connectConfigBuilder.idleTimeoutMs(milvusConfig.getIdleTimeoutMs());
                    
                    if(milvusConfig.getCustomConnectConfigBuilder() != null){
                        milvusConfig.getCustomConnectConfigBuilder().customConnectConfigBuilder(connectConfigBuilder);
                    }
                    ConnectConfig connectConfig = connectConfigBuilder
                            .build();

                    PoolConfig.PoolConfigBuilder<?, ?> poolConfigBuilder = PoolConfig.builder();
                    if(milvusConfig.getMaxIdlePerKey() != null)
                        poolConfigBuilder.maxIdlePerKey(milvusConfig.getMaxIdlePerKey() ); // max idle clients per key
                    if(milvusConfig.getMaxTotalPerKey() != null)
                        poolConfigBuilder.maxTotalPerKey(milvusConfig.getMaxTotalPerKey()); // max total(idle + active) clients per key
                    if(milvusConfig.getMaxTotal() != null)
                        poolConfigBuilder.maxTotal(milvusConfig.getMaxTotal()); // max total clients for all keys
                    if(milvusConfig.getMaxBlockWaitDuration() != null)
                        poolConfigBuilder.maxBlockWaitDuration(Duration.ofSeconds(milvusConfig.getMaxBlockWaitDuration())); // getClient() will wait 5 seconds if no idle client available
                    if(milvusConfig.getMinEvictableIdleDuration() != null)
                        poolConfigBuilder.minEvictableIdleDuration(Duration.ofSeconds(milvusConfig.getMinEvictableIdleDuration())) ;// if number of idle clients is larger than maxIdlePerKey, redundant idle clients will be evicted after 10 seconds
                    if(milvusConfig.getBlockWhenExhausted() != null){
                        poolConfigBuilder.blockWhenExhausted(milvusConfig.getBlockWhenExhausted());
                    }
                    if(milvusConfig.getEvictionPollingInterval() != null){
                        poolConfigBuilder.evictionPollingInterval(Duration.ofSeconds(milvusConfig.getEvictionPollingInterval()));
                    }
                    if(milvusConfig.getMinIdlePerKey() != null){
                        poolConfigBuilder.minIdlePerKey(milvusConfig.getMinIdlePerKey());
                    }
                    if(milvusConfig.getTestOnBorrow() != null){
                        poolConfigBuilder.testOnBorrow(milvusConfig.getTestOnBorrow());
                    }
                    if(milvusConfig.getTestOnReturn() != null){
                        poolConfigBuilder.testOnReturn(milvusConfig.getTestOnReturn());
                    }
                    
                    PoolConfig poolConfig = poolConfigBuilder.build();
                   
                    try {
                        MilvusClientV2PoolExt pool = new MilvusClientV2PoolExt(poolConfig, connectConfig);
                    
                        Milvus milvus = new Milvus();
                        milvus.setMilvusClientV2Pool(pool);
                        milvus.setClientName(milvusConfig.getName());
                        validateMilvus(milvus);
                        milvusContainer.put(milvusConfig.getName(), milvus);
                        logger.info("Init milvus datasource successed:{}", SimpleStringUtil.object2json(milvusConfig));
                        milvusStartResult.addMilvusStartResult(milvusConfig.getName());
                    } catch (ClassNotFoundException e) {
                        throw new DataMilvusException("Init milvus datasource failed:",e);
                    } catch (NoSuchMethodException e) {
                        throw new DataMilvusException("Init milvus datasource failed:",e);
                    }catch (DataMilvusException e) {
                        throw e;
                    }catch (Exception e) {
                        throw new DataMilvusException("Init milvus datasource failed:",e);
                    }
                }
            }
        }
        return milvusStartResult;
    }
    
    public static void shutdown(MilvusStartResult milvusStartResult){
        if(milvusStartResult == null)
            return;
        Map<String,Object> stringObjectMap = milvusStartResult.getResourceStartResult();
        Iterator iterator = stringObjectMap.keySet().iterator();;
        while (iterator.hasNext()){
            String name = (String)iterator.next();
            Milvus milvus = milvusContainer.get(name);
            if(milvus != null){
                milvus.shutdown();
            }
        }
    }
    
    public static void validateMilvus(Milvus milvus) throws Exception{
        MilvusClientV2 milvusClientV2 = null;
        try {
            milvusClientV2 = milvus.getMilvusClientV2();
        }
        catch (Exception e){
            throw e;
        }
        finally {
            if(milvusClientV2 != null){
                milvus.release(milvusClientV2);
            }
        }
    }
    public static void validateMilvus(String name) throws Exception{
        Milvus milvus = getMilvus(name);
        validateMilvus(  milvus);
    }

    public static void shutdown(String name){
        Milvus milvus = milvusContainer.get(name);
        if(milvus != null){
            milvus.shutdown();
        }
    }
}
