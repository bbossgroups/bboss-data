package org.frameworkset.nosql.minio;
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
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.frameworkset.nosql.mongodb.MongoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: 管理minio数据源，可以通过名称获取Minio操作和访问minio</p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/8/7
 */
public class MinioHelper {
    private static Logger logger = LoggerFactory.getLogger(MinioHelper.class);
    private static Map<String, Minio> minioContainer = new ConcurrentHashMap<>();

    /**
     * 初始化minio客户端
     * @param minioConfig
     * @return
     */
    public static boolean init(MinioConfig minioConfig){
        if(!minioContainer.containsKey(minioConfig.getName())){
            synchronized (minioContainer){
                if(!minioContainer.containsKey(minioConfig.getName())){
                    OkHttpClient httpClient = minioConfig.getHttpClient() == null?
                            new OkHttpClient.Builder()
                            .connectTimeout(minioConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(minioConfig.getReadTimeout(), TimeUnit.MILLISECONDS)
                            .writeTimeout(minioConfig.getWriteTimeout(), TimeUnit.MILLISECONDS)
                            .build():minioConfig.getHttpClient();
                    MinioClient minioClient =
                            MinioClient.builder()
                                    .endpoint(minioConfig.getEndpoint())
                                    .credentials(minioConfig.getAccessKeyId(), minioConfig.getSecretAccesskey())
                                    .httpClient(httpClient)
                                    .build();
                    Minio minio = new Minio(minioClient, minioConfig);
                    minioContainer.put(minioConfig.getName(),minio);
                    return true;
                }
            }
        }
        return false;
    }
    
    public static MinioClient getMinioClient(String name){
        Minio minio = minioContainer.get(name);
        if(minio != null)
            return minio.getMinioClient();
        return null;
    }

    /**
     * 获取minio数据源操作对象
     * @param name
     * @return
     */
    public static Minio getMinio(String name){
        return minioContainer.get(name);
    }

 
    public static void shutdown(MinioStartResult minioStartResult){
        Map<String,Object> stringObjectMap = minioStartResult.getResourceStartResult();
        Iterator iterator = stringObjectMap.keySet().iterator();;
        while (iterator.hasNext()){
            String name = (String)iterator.next();
            Minio minio = minioContainer.get(name);
            if(minio != null){
                minio.shutdown();
            }
        }
    }

    public static void shutdown(String name){    
        Minio minio = minioContainer.get(name);
        if(minio != null){
            minio.shutdown();
        }
    }
    
}
