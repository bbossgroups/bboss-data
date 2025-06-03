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
 

    public boolean createBucket(String bucket) throws Exception ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    public String uploadObject(String file,String bucket, String key,String contentType) ;
    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    public String uploadObject(String file,String bucket, String key,String contentType,long maxFilePartSize);
    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    public String uploadObject(String file,String bucket,long maxFilePartSize) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    public String uploadObject(String file,String bucket) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(String file,String bucket, String key) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(File file, String bucket, String key) ;

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(String file,String bucket, String key,long maxFilePartSize);

    public String saveOssFile(File file,String bucket, String key);

    public String saveOssFile(File file, String bucket) ;

    public String saveOssFile(byte[] bytes, String bucket,String id);

    public String saveOssFile(byte[] bytes,String bucket) ;

    public String saveOssFile(InputStream inputStream, long size, String bucket, String id);

    public String saveOssFile(InputStream inputStream, long size, String bucket);
    public OSSFileContent getOssFile(String bucket, String key) ;

    public InputStream getOssFileStream(String bucket,String key);

    public void getOssFile(String bucket,String key, OutputStream out) ;

    public void getOssFile(String bucket,String key, File file) ;

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    public void getOssFile(String bucket,String key, String fileName) ;

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    public void downloadObject(String bucket,String key, String fileName) ;



    public void deleteOssFile(String bucket,String key) ;

    public String updateOssFile(String bucket,String key, byte[] bytes) ;

    public String updateOssFile(String bucket,String key, File file) ;

    public String updateOssFile(String bucket,String key, InputStream inputStream,long size) ;


    public boolean exist(String bucket,String key) ;

    public boolean pathExist(String bucket,String path) ;

    public void createPath(String bucket,String path) ;

    public List<OSSFile> listOssFile(String bucket, String path) ;

    public List<OSSFile> listOssFile(String bucket,String path,boolean recursive) ;
}
