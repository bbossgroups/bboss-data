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
import io.minio.*;
import io.minio.messages.Item;
import org.frameworkset.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/8/7
 */
public class Minio {
    private static Logger logger = LoggerFactory.getLogger(Minio.class);
    private MinioClient minioClient;
    private long maxFilePartSize;
    private MinioConfig minioConfig;
    public Minio(MinioClient minioClient,MinioConfig minioConfig){
        this.minioClient = minioClient;
        this.maxFilePartSize = minioConfig.getMaxFilePartSize();
        this.minioConfig = minioConfig;
    }

    public MinioClient getMinioClient() {
        return minioClient;
    }
    public void shutdown(){
        if(minioClient != null){
            try {
                minioClient.close();
            } catch (Exception e) {
                logger.warn("");
            }
        }
    }
    
    private String buildErrorInfo(String error){
        StringBuilder builder = new StringBuilder();
        builder.append("Minio server:").append(minioConfig.getEndpoint()).append(",Name:").append(minioConfig.getName()).append(",").append(error);
        return builder.toString();
    }

    public void createBucket(String bucket) throws Exception {
        if(SimpleStringUtil.isEmpty(bucket)){
            throw new DataMinioException(buildErrorInfo("The bucket is null,bucket:"+bucket+",minio["+minioConfig.getName()+"]"));
        }
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        }
        catch (Exception e){
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket),e);
        }
        
    }


    public String saveOssFile(File file,String bucket, String id) {
        if(file == null){
            throw new DataMinioException("The insert file is null,bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        }
        String key = id;
        if (SimpleStringUtil.isEmpty(key)) {
            key = SimpleStringUtil.getUUID();
        }
        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(inputStream, -1, maxFilePartSize)
                            .build());
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("The insert file is "+file.getPath()+",bucket:"+bucket+",id:"+id),e);
        }
        return key;
    }

    public String saveOssFile(File file, String bucket) {
        return saveOssFile(file,bucket, null);
    }

    public String saveOssFile(byte[] bytes, String bucket,String id) {
        if (bytes == null || bytes.length == 0) {
            throw new DataMinioException(buildErrorInfo("bytes is blank,bucket:"+bucket));
        }
        String key = id;
        if (!SimpleStringUtil.hasLength(key)) {
            key = SimpleStringUtil.getUUID32();
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)                            
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .build());
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+id),e);
        }
        return key;
    }

    public String saveOssFile(byte[] bytes,String bucket) {
        return saveOssFile(bytes,  bucket, null);
    }

    public String saveOssFile(InputStream inputStream, String bucket,String id) {
        String key = id;
        if (!SimpleStringUtil.hasLength(key)) {
            key = SimpleStringUtil.getUUID32();
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(inputStream, -1, maxFilePartSize)
                            .build());
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+id),e);
        }
        return key;
    }

    public String saveOssFile(InputStream inputStream, String bucket) {
        return saveOssFile(inputStream, bucket,null);
    }

    private   byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }
    public OSSFileContent getOssFile(String bucket,String key) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        OSSFileContent ossObject = new OSSFileContent();
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .build()
        )) {
            ossObject.setKey(key);
            ossObject.setBucketName(bucket);
            ossObject.setBytes(readAllBytes(stream));
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key),e);
        }
        return ossObject;
    }

    public InputStream getOssFileStream(String bucket,String key) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        try {
           InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build()
            );
            return stream;
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key),e);
        }
    }

    public void getOssFile(String bucket,String key, OutputStream out) {
        byte[] bytes = (getOssFile(  bucket,key)).getBytes();
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key),e);
        }
    }

    public void getOssFile(String bucket,String key, File file) {
        // 先判断是否存在文件，再创建缓存文件。
        if (!exist(bucket,key)) {
            throw new DataMinioException("file not exist! file:" + key+",bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytes = (getOssFile(  bucket,key)).getBytes();
            if (bytes != null && bytes.length > 0)
                bos.write(bytes);
        } catch (IOException e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key+",file:"+file.getPath()),e);
        }
    }

    public void getOssFile(String bucket,String key, String fileName) {
        // 先判断是否存在文件，再创建缓存文件。
        if (!exist(bucket,key)) {
            throw new DataMinioException("File not exist! file:" + key + ",bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            byte[] bytes = (getOssFile(  bucket,key)).getBytes();
            if (bytes != null && bytes.length > 0)
                bos.write(bytes);
        } catch (IOException e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key+",file:"+fileName),e);
        }
    }

    public void deleteOssFile(String bucket,String key) {

        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");;
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket( bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key),e);
        }

    }

    public String updateOssFile(String bucket,String key, byte[] bytes) {
        if(bytes == null )
            throw new DataMinioException("content is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        deleteOssFile(  bucket,key);
        return saveOssFile(bytes,   bucket,"update");

    }

    public String updateOssFile(String bucket,String key, File file) {
        if(file == null )
            throw new DataMinioException("file is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");
        deleteOssFile(  bucket,key);
        return saveOssFile(file,  bucket, "update");
    }

    public String updateOssFile(String bucket,String key, InputStream inputStream) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        try {
            
            return updateOssFile(  bucket,key, readAllBytes(inputStream));
        }catch (DataMinioException e) {
            throw e;
        }catch (IOException e) {
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",key:"+key),e);
        } 
    }

    public boolean exist(String bucket,String key) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(key == null )
            throw new DataMinioException("key is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucket)
                    .object(key).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean pathExist(String bucket,String path) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(path == null )
            throw new DataMinioException("path is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        return exist(  bucket,path);
    }

    public void createPath(String bucket,String path) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(path == null )
            throw new DataMinioException("path is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(path)
                    .stream(new ByteArrayInputStream(new byte[0], 0, 0), 0, -1)
                    .build());
        } catch (Exception e) {
            logger.error("create path failed: {},bucket:{},minio[{}]", path,bucket,minioConfig.getName());
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",path:"+path),e);
        }
    }

    public List<OSSFile> listOssFile(String bucket,String path) {
        if(bucket == null )
            throw new DataMinioException("bucket is blank!"+",minio["+minioConfig.getName()+"]");
        if(path == null )
            throw new DataMinioException("path is blank,bucket:"+bucket+",minio["+minioConfig.getName()+"]");

        if (!SimpleStringUtil.hasLength(path))
            return null;
        try {
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucket).prefix(path).recursive(true).build());
            List<OSSFile> list = new ArrayList<>();
            OSSFile ossFile = null;
            for (Result<Item> result : results) {
                Item item = result.get();
                if (!item.objectName().equals(path)) {
                    ossFile = new OSSFile();
                    ossFile.setObjectName(item.objectName());
                    ossFile.setSize(item.size());
                    ossFile.setDir(item.isDir());
                    ossFile.setResponseDate(item.lastModified());
                    list.add(ossFile);
                }
            }
            return list;
        } catch (Exception e) {
            logger.error("list path: {},bucket:{},minio[{}]", path,bucket,minioConfig.getName());
            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",path:"+path),e);
        }
    }
}
