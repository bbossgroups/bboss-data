package org.frameworkset.nosql;
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

import org.frameworkset.nosql.minio.Minio;
import org.frameworkset.nosql.minio.MinioConfig;
import org.frameworkset.nosql.minio.MinioHelper;
import org.frameworkset.nosql.s3.OSSClient;
import org.frameworkset.nosql.s3.OSSConfig;
import org.frameworkset.nosql.s3.OSSHelper;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2025/3/20
 */
public class MinioTest {
    public static void main(String[] args) throws Exception {
        

        
        //操作minio
//        uploadWordFiles();
        initOSS();
        initMinio();
    }
    private static void initMinio(){
        //1. 初始化Minio数据源chan_fqa，用来操作Minio数据库，一个Minio数据源只需要定义一次即可，后续通过名称miniotest反复引用，多线程安全
        // 可以通过以下方法定义多个Minio数据源，只要name不同即可，通过名称引用对应的数据源
        MinioConfig minioConfig = new MinioConfig();

        minioConfig.setEndpoint("http://172.24.176.18:9000");

        minioConfig.setName("miniotest");
        minioConfig.setAccessKeyId("N3XNZFqSZfpthypuoOzL");
        minioConfig.setSecretAccesskey("2hkDSEll1Z7oYVfhr0uLEam7r0M4UWT8akEBqO97");
        minioConfig.setConnectTimeout(5000l);
        minioConfig.setReadTimeout(5000l);
        minioConfig.setWriteTimeout(5000l);

        minioConfig.setMaxFilePartSize(10*1024*1024*1024);
        boolean result = MinioHelper.init(minioConfig);
        Minio ossClient = MinioHelper.getMinio("miniotest");
        String shareUrl = ossClient.getInternalDownloadUrl("etlfiles","HN_BOSS_TRADE000001_202503211019_000001.txt",100,"http://172.24.176.18:50003");
        System.out.println(shareUrl);
    }
    
    private static void uploadWordFiles() throws Exception {
        initMinio();

        //获取数据源
        Minio minio = MinioHelper.getMinio("miniotest");
        minio.createBucket("filedown");
//        minio.uploadObject("C:/data/wordfiles/以创新之火，点燃传统文化之光.docx","wordfiles","以创新之火，点燃传统文化之光.docx");
        minio.uploadObject("C:/data/wordfiles/经济预测与决策试题(修改2稿).docx","wordfiles","subdir/经济预测与决策试题(修改2稿).docx");
        
    }

    private static void minioTestFiles() throws Exception {
        initMinio();

        //获取数据源
        Minio minio = MinioHelper.getMinio("miniotest");
        minio.createBucket("filedown");
        minio.uploadObject("C:/data/wordfiles/以创新之火，点燃传统文化之光.docx","wordfiles","以创新之火，点燃传统文化之光.docx");
        minio.uploadObject("C:/data/filedown/HN_BOSS_TRADE_202501092032_000001.txt","filedown","filedown/HN_BOSS_TRADE_202501092032_000001.txt");
        minio.downloadObject("filedown","filedown/HN_BOSS_TRADE_202501092032_000001.txt","C:/data/filedown/xxxxxaaaa.txt");
        minio.deleteOssFile("filedown","filedown/HN_BOSS_TRADE_202501092032_000001.txt");
    }

    private static void initOSS(){
        OSSConfig ossConfig = new OSSConfig();
        ossConfig.setName("miniotest");
        ossConfig.setAccessKeyId("N3XNZFqSZfpthypuoOzL");
        ossConfig.setSecretAccesskey("2hkDSEll1Z7oYVfhr0uLEam7r0M4UWT8akEBqO97");
        ossConfig.setEndpoint("http://172.24.176.18:9000");

        ossConfig.setConnectTimeout(5000l);
//            ossConfig.setHttpClient(OSSFileConfig.getHttpClient());
        ossConfig.setMaxFilePartSize(10*1024*1024*1024);
        ossConfig.setReadTimeout(5000l);
        ossConfig.setWriteTimeout(5000l);
        ossConfig.setSocketTimeout(5000l);
        ossConfig.setConnectionAcquisitionTimeout(5000l);
        ossConfig.setConnectionMaxIdleTime(5000l);
        ossConfig.setPathStyleAccess(true);
        ossConfig.setPoolMaxIdleConnections(10);
        ossConfig.setTcpKeepAlive(true);
        ossConfig.setRegion("east-r-a1");
        boolean result = OSSHelper.init(ossConfig);
        OSSClient ossClient = OSSHelper.getOSSClientDS(ossConfig.getName());
        //http://172.24.176.18:50003/api/v1/download-shared-object/aHR0cDovLzEyNy4wLjAuMTo5MDAwL2V0bGZpbGVzL0hOX0JPU1NfVFJBREUwMDAwMDFfMjAyNTAzMjExMDE5XzAwMDAwMS50eHQ_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BM1BMR1dINEFFVU1TSVZaVU9PSCUyRjIwMjUxMjIzJTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDI1MTIyM1QxMDU0MzlaJlgtQW16LUV4cGlyZXM9NDMyMDAmWC1BbXotU2VjdXJpdHktVG9rZW49ZXlKaGJHY2lPaUpJVXpVeE1pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SmhZMk5sYzNOTFpYa2lPaUpCTTFCTVIxZElORUZGVlUxVFNWWmFWVTlQU0NJc0ltVjRjQ0k2TVRjMk5qVXpNREl5TVN3aWNHRnlaVzUwSWpvaWJXbHVhVzloWkcxcGJpSjkuUEtNSmdhYkFzN1dNNUd6RXRnd0lWWWR0aGU2ajdCeW1RVEtLd2tVbWxiUnFLMUdyQzd5RFlvRkM5akdKbDU4SVpSb2RNcXN6RFQzaU9RTFFDNnlJYncmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JnZlcnNpb25JZD1udWxsJlgtQW16LVNpZ25hdHVyZT1mMGJlNDVhMjczOTNmYzVhM2I2NGJkNmZkY2M5ZmRkZDhmY2QzOTkxNzVhYjFlODNkNjU3MDRmOWM3M2Q2OTM3
        String shareUrl = ossClient.getInternalDownloadUrl("etlfiles","HN_BOSS_TRADE000001_202503211019_000001.txt",100,"http://172.24.176.18:9000");
        System.out.println(shareUrl);
    
    }
    
}
