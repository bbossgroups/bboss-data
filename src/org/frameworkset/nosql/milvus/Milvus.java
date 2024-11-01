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

import io.milvus.pool.MilvusClientV2Pool;
import io.milvus.v2.client.MilvusClientV2;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/10/3
 */
public class Milvus {
    private MilvusClientV2PoolExt milvusClientV2Pool;
    private String clientName;

    public MilvusClientV2PoolExt getMilvusClientV2Pool() {
        return milvusClientV2Pool;
    }
    
    public void shutdown(){
        if(this.milvusClientV2Pool != null){
            this.milvusClientV2Pool.close();
        }
    }

    public void setMilvusClientV2Pool(MilvusClientV2PoolExt milvusClientV2Pool) {
        this.milvusClientV2Pool = milvusClientV2Pool;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public MilvusClientV2 getMilvusClientV2(){
        return milvusClientV2Pool.getClient(clientName);
    }
    
    public void release(MilvusClientV2 milvusClientV2){
        milvusClientV2Pool.returnClient(clientName,milvusClientV2);
    }
}
