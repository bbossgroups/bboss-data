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

import java.io.*;
import java.util.List;

/**
 * @author biaoping.yin
 * @Date 2025/6/3
 */
public interface OSSClient {
 

    boolean createBucket(String bucket) throws Exception ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    String uploadObject(String file,String bucket, String key,String contentType) ;
    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    String uploadObject(String file,String bucket, String key,String contentType,long maxFilePartSize);
    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    String uploadObject(String file,String bucket,long maxFilePartSize) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    String uploadObject(String file,String bucket) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    String uploadObject(String file,String bucket, String key) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    String uploadObject(File file, String bucket, String key) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    String uploadObject(String file,String bucket, String key,long maxFilePartSize);

    String saveOssFile(File file,String bucket, String key);

    String saveOssFile(File file, String bucket) ;

    String saveOssFile(byte[] bytes, String bucket,String id);

    String saveOssFile(byte[] bytes,String bucket) ;

    String saveOssFile(InputStream inputStream, long size, String bucket, String id);

    String saveOssFile(InputStream inputStream, long size, String bucket);
    OSSFileContent getOssFile(String bucket, String key) ;

    InputStream getOssFileStream(String bucket,String key);

    void getOssFile(String bucket,String key, OutputStream out) ;

    void getOssFile(String bucket,String key, File file) ;

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    void getOssFile(String bucket,String key, String fileName) ;

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    void downloadObject(String bucket,String key, String fileName) ;



    void deleteOssFile(String bucket,String key) ;

    String updateOssFile(String bucket,String key, byte[] bytes) ;

    String updateOssFile(String bucket,String key, File file) ;

    String updateOssFile(String bucket,String key, InputStream inputStream,long size) ;


    boolean exist(String bucket,String key) ;

    boolean pathExist(String bucket,String path) ;

    void createPath(String bucket,String path) ;

    List<OSSFile> listOssFile(String bucket, String path) ;

    List<OSSFile> listOssFile(String bucket,String path,boolean recursive) ;

    String getInternalDownloadUrl(String bucket,String objectName, Integer expirySec,String endpoint) ;

    String getExternalDownloadUrl(String bucket,String objectName, Integer expirySec,String endpoint);

    String getInternalDownloadUrl(String bucket,String objectName, Integer expirySec) ;

    String getExternalDownloadUrl(String bucket,String objectName, Integer expirySec);
}
