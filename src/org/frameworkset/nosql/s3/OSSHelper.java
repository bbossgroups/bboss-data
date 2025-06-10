package org.frameworkset.nosql.s3;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 管理OssClient数据源，可以通过名称获取OssClient操作和访问OssClient</p>
 *
 * @author biaoping.yin
 * @Date 2024/8/7
 */
public class OSSHelper {
    private static Logger logger = LoggerFactory.getLogger(OSSHelper.class);
    private static Map<String, OSSClient> ossClientConcurrentHashMap = new ConcurrentHashMap<>();

    /**
     * 初始化oss客户端
     * @param ossConfig
     * @return
     */
    public static boolean init(OSSConfig ossConfig){
        if(!ossClientConcurrentHashMap.containsKey(ossConfig.getName())){
            synchronized (ossClientConcurrentHashMap){
                if(!ossClientConcurrentHashMap.containsKey(ossConfig.getName())){
                  
                    // 构建客户端
                    ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                            .maxConnections(ossConfig.getPoolMaxIdleConnections())
                            .connectionTimeout(Duration.of(ossConfig.getConnectTimeout(), java.time.temporal.ChronoUnit.MILLIS))
                            .connectionTimeToLive(Duration.of(ossConfig.getPoolKeepAliveDuration(), java.time.temporal.ChronoUnit.MILLIS))
                            .connectionMaxIdleTime(Duration.of(ossConfig.getConnectionMaxIdleTime(), java.time.temporal.ChronoUnit.MILLIS))
                            .connectionAcquisitionTimeout(Duration.of(ossConfig.getConnectionAcquisitionTimeout(), java.time.temporal.ChronoUnit.MILLIS))
                            .socketTimeout(Duration.of(ossConfig.getSocketTimeout(), java.time.temporal.ChronoUnit.MILLIS))
                            .tcpKeepAlive(ossConfig.getTcpKeepAlive())
                            .socketFactory(null);
//                    
                    S3ClientBuilder s3ClientBuilder = S3Client.builder()
                    .endpointOverride(URI.create(ossConfig.getEndpoint().trim()))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(ossConfig.getAccessKeyId(), ossConfig.getSecretAccesskey())))
                   
                    .httpClientBuilder(httpClientBuilder) // 显式指定
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(ossConfig.getPathStyleAccess()) // 关键配置
                            .build())
                    ;
                            
                    if(SimpleStringUtil.isNotEmpty(ossConfig.getRegion()))
                        s3ClientBuilder .region(Region.of(ossConfig.getRegion()));
                    S3Client s3Client =s3ClientBuilder.build();
                     
                   
                    OSSClient ossClient = new OSSClientImpl(s3Client, ossConfig);
                    ossClientConcurrentHashMap.put(ossConfig.getName(),ossClient);
                    logger.info("Init oss datasource successed:{}",SimpleStringUtil.object2json(ossConfig));
                    return true;
                }
            }
        }
        return false;
    }
    
    public static S3Client getOSSClient(String name){
        OSSClient ossClient = ossClientConcurrentHashMap.get(name);
        if(ossClient != null)
            return ((OSSClientImpl)ossClient).getS3Client();
        return null;
    }

    /**
     * 获取OSSClient数据源操作对象
     * @param name
     * @return
     */
    public static OSSClient getOSSClientDS(String name){
        return ossClientConcurrentHashMap.get(name);
    }

 
    public static void shutdown(OSSStartResult OSSStartResult){
        Map<String,Object> stringObjectMap = OSSStartResult.getResourceStartResult();
        Iterator iterator = stringObjectMap.keySet().iterator();;
        while (iterator.hasNext()){
            String name = (String)iterator.next();
            OSSClient ossClient = ossClientConcurrentHashMap.get(name);
            if(ossClient != null){
                ((OSSClientImpl)ossClient).shutdown();
            }
        }
    }

    public static void shutdown(String name){
        OSSClient ossClient = ossClientConcurrentHashMap.get(name);
        if(ossClient != null){
            ((OSSClientImpl)ossClient).shutdown();
        }
    }
    
}
