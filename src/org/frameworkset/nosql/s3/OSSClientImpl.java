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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/8/7
 */
public class OSSClientImpl implements OSSClient {
    private static Logger logger = LoggerFactory.getLogger(OSSClientImpl.class);
    private S3Client s3Client;
    private long maxFilePartSize;
    private OSSConfig ossConfig;
    public OSSClientImpl(S3Client s3Client,
                         OSSConfig ossConfig){
        this.s3Client = s3Client;
        this.maxFilePartSize = ossConfig.getMaxFilePartSize();
        this.ossConfig = ossConfig;
    }

    public S3Client getS3Client() {
        return s3Client;
    }
    public void shutdown(){
        if(s3Client != null){
            try {
                s3Client.close();
            } catch (Exception e) {
                logger.warn("");
            }
        }
    }
    
    private String buildErrorInfo(String error){
        StringBuilder builder = new StringBuilder();
        builder.append("OSS server:").append(ossConfig.getEndpoint()).append(",Name:").append(ossConfig.getName()).append(",").append(error);
        return builder.toString();
    }

    public boolean createBucket(String bucket) throws Exception {
        if(SimpleStringUtil.isEmpty(bucket)){
            throw new DataOSSException(buildErrorInfo("The bucket is null,bucket:"+bucket+",OSS["+ossConfig.getName()+"]"));
        }
        try {
            boolean found = false;
            try {
                s3Client.headBucket(b -> b.bucket(bucket));
                found = true;
            } catch (NoSuchBucketException ignored) {
                found = false;
            }

            if (!found) {
                s3Client.createBucket(b -> b.bucket(bucket));
                return true;
            }
            return false;
        }
        catch (Exception e){
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket),e);
        }
        
    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    public String uploadObject(String file,String bucket, String key,String contentType) {
       return uploadObject(  file,  bucket,  key,  contentType,this. maxFilePartSize);
    }
    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @param contentType
     * @return
     */
    public String uploadObject(String file,String bucket, String key,String contentType,long maxFilePartSize) {
        if(file == null){
            throw new DataOSSException("The insert file is null,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        }
        if (SimpleStringUtil.isEmpty(key)) {
            key = SimpleStringUtil.getUUID();
        }

        try {
            if (file == null) {
                throw new DataOSSException("The insert file is null,bucket:" + bucket);
            }
            if (SimpleStringUtil.isEmpty(key)) {
                key = SimpleStringUtil.getUUID();
            }

            PutObjectRequest.Builder putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key);

            if (contentType != null) {
                putObjectRequest.contentType(contentType);
            }

            File fileToUpload = new File(file);
            s3Client.putObject(putObjectRequest.build(), RequestBody.fromFile(fileToUpload));
            return key;
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("The insert file is "+file+",bucket:"+bucket+",key:"+key),e);
        }
    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    public String uploadObject(String file,String bucket,long maxFilePartSize) {
        return uploadObject(  file,  bucket,  null,null,maxFilePartSize);
    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @return
     */
    public String uploadObject(String file,String bucket) {
        return uploadObject(file, bucket, null, null, this.maxFilePartSize);
    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(String file,String bucket, String key) {
        return uploadObject(  file,  bucket,  key,null,this.maxFilePartSize);
    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(File file,String bucket, String key) {
        try {
            return uploadObject(  file.getCanonicalPath(),  bucket,  key,null,this.maxFilePartSize);
        } catch (IOException e) {
            throw new DataOSSException(buildErrorInfo("The insert file is "+file.getAbsolutePath()+",bucket:"+bucket+",key:"+key),e);
        }

    }

    /**
     * 上传文件
     * @param file
     * @param bucket
     * @param key
     * @return
     */
    public String uploadObject(String file,String bucket, String key,long maxFilePartSize) {
        return uploadObject(  file,  bucket,  key,null,maxFilePartSize);
    }

    public String saveOssFile(File file,String bucket, String key) {
        if(file == null){
            throw new DataOSSException("The insert file is null,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        }
        return this.uploadObject(file,bucket,key);
//        String key = id;
//        if (SimpleStringUtil.isEmpty(key)) {
//            key = SimpleStringUtil.getUUID();
//        }
//
//        try (InputStream inputStream = new FileInputStream(file)) {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(key)
//                            .stream(inputStream, -1, maxFilePartSize)
//                            .build());
//        } catch (Exception e) {
//            throw new DataMinioException(buildErrorInfo("The insert file is "+file.getPath()+",bucket:"+bucket+",id:"+id),e);
//        }
//        return key;
    }

    public String saveOssFile(File file, String bucket) {
        return saveOssFile(file,bucket, null);
    }

    public String saveOssFile(byte[] bytes, String bucket,String id) {
        if (bytes == null || bytes.length == 0) {
            throw new DataOSSException(buildErrorInfo("bytes is blank,bucket:"+bucket));
        }
        String key = id;
        if (!SimpleStringUtil.hasLength(key)) {
            key = SimpleStringUtil.getUUID32();
        }
        try {

            final long MULTIPART_THRESHOLD = maxFilePartSize;//5 * 1024 * 1024L; // 5MB
            // 将 InputStream 读入 ByteArrayOutputStream 以便重复使用
            long totalSize = bytes.length;

            

            byte[] fileBytes = bytes;

            // 小文件直接使用 putObject 普通上传
            if (totalSize < MULTIPART_THRESHOLD) {
                final String t = key;
                s3Client.putObject(builder -> builder
                                .bucket(bucket)
                                .key(t),
//                                .contentType(contentType),
                        RequestBody.fromBytes(fileBytes));
                logger.info("Single-part upload complete: {} ({} bytes)", t, totalSize);
                return key;
            }

            // 否则使用 Multipart Upload
            String uploadId = s3Client.createMultipartUpload(CreateMultipartUploadRequest.builder()
                            .bucket(bucket)
                            .key(key)
//                            .contentType(contentType)
                            .build())
                    .uploadId();

            List<CompletedPart> completedParts = new ArrayList<>();
            int partSize = (int) MULTIPART_THRESHOLD;
            int partCount = (int) Math.ceil((double) totalSize / partSize);

            for (int partNumber = 1; partNumber <= partCount; partNumber++) {
                int start = (partNumber - 1) * partSize;
                int end = Math.min(start + partSize, fileBytes.length);
                byte[] partBytes = Arrays.copyOfRange(fileBytes, start, end);

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(UploadPartRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .uploadId(uploadId)
                                .partNumber(partNumber)
                                .contentLength((long) partBytes.length)
                                .build(),
                        RequestBody.fromBytes(partBytes));

                completedParts.add(CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(uploadPartResponse.eTag())
                        .build());

                logger.debug("Uploaded part {} with size {}", partNumber, partBytes.length);
            }

            s3Client.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build());

            logger.info("Multipart upload complete: {} ({} bytes)", key, totalSize);
            return key;
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+",id:"+id),e);
        }
    }

    public String saveOssFile(byte[] bytes,String bucket) {
        return saveOssFile(bytes,  bucket, null);
    }

    public String saveOssFile(InputStream inputStream, long size ,String bucket,String id) {
        String key = id;
        if (!SimpleStringUtil.hasLength(key)) {
            key = SimpleStringUtil.getUUID32();
        }
        try {
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(key)
//                            .stream(inputStream, -1, maxFilePartSize)
//                            .build());
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    RequestBody.fromInputStream(inputStream,size)
            );
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+",id:"+id),e);
        }
        return key;
    }

    public String saveOssFile(InputStream inputStream, long size, String bucket) {
        return saveOssFile(inputStream,   size, bucket,null);
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
    
    
    public OSSFileContent getOssFile(String bucket, String key) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        OSSFileContent ossObject = new OSSFileContent();
        try (ResponseInputStream<GetObjectResponse> response = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())) {

            GetObjectResponse metadata = response.response();
            ossObject.setContentType(metadata.contentType());
            ossObject.setKey(key);
            ossObject.setBucketName(bucket);
            ossObject.setBytes(readAllBytes(response));
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + ",id:" + key), e);
        }
        return ossObject;
    }

    public InputStream getOssFileStream(String bucket,String key) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        try {
            return s3Client.getObject(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + ",id:" + key), e);
        }
    }

    public void getOssFile(String bucket,String key, OutputStream out) {
//        byte[] bytes = (getOssFile(  bucket,key)).getBytes();
//        try {
//            out.write(bytes);
//        } catch (IOException e) {
//            throw new DataMinioException(buildErrorInfo("bucket:"+bucket+",id:"+key),e);
//        }
        // 先判断是否存在文件，再创建缓存文件。
        if (!exist(bucket,key)) {
            throw new DataOSSException("file not exist! file:" + key+",bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        }
        try (             InputStream inputStream = getOssFileStream(  bucket,key)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new DataOSSException(buildErrorInfo("getOssFile bucket:"+bucket+",id:"+key+" to OutputStream failed:"),e);
        }
    }

    public void getOssFile(String bucket,String key, File file) {
        // 先判断是否存在文件，再创建缓存文件。
        if (!exist(bucket,key)) {
            throw new DataOSSException("file not exist! file:" + key+",bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)) ;
             InputStream inputStream = getOssFileStream(  bucket,key)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new DataOSSException(buildErrorInfo("getOssFile bucket:"+bucket+",id:"+key+",file:"+file.getName()),e);
        }
    }

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    public void getOssFile(String bucket,String key, String fileName) {
        downloadObject(  bucket,  key,fileName);
    }

    /**
     * 获取oss对象内容，并写入fileName对应的文件
     * @param bucket
     * @param key
     * @param fileName 保存的文件路径
     */
    public void downloadObject(String bucket,String key, String fileName) {
        // 先判断是否存在文件，再创建缓存文件。
        if (!exist(bucket,key)) {
            throw new DataOSSException("file not exist! file:" + key+",bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        }
        try {
            s3Client.getObject(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build(),
                    Paths.get(fileName));
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + ",id:" + key + ",fileName" + fileName), e);
        }
    }
    
     

    public void deleteOssFile(String bucket,String key) {

        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
        } catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + ",id:" + key), e);
        }

    }

    public String updateOssFile(String bucket,String key, byte[] bytes) {
        if(bytes == null )
            throw new DataOSSException("content is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        deleteOssFile(  bucket,key);
        return saveOssFile(bytes,   bucket,key);

    }

    public String updateOssFile(String bucket,String key, File file) {
        if(file == null )
            throw new DataOSSException("file is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
        deleteOssFile(  bucket,key);
        return saveOssFile(file,  bucket, key);
    }

    public String updateOssFile(String bucket,String key, InputStream inputStream,long size) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        try {

            deleteOssFile(  bucket,key);
            return saveOssFile(inputStream,size,   bucket,key);
        }catch (DataOSSException e) {
            throw e;
        }catch (Exception e) {
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+",key:"+key),e);
        } 
    }

    
    public boolean exist(String bucket,String key) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(key == null )
            throw new DataOSSException("key is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        try {
            s3Client.headObject(b -> b.bucket(bucket).key(key));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean pathExist(String bucket,String path) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(path == null )
            throw new DataOSSException("path is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        return exist(  bucket,path);
    }

    public void createPath(String bucket,String path) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
        if(path == null )
            throw new DataOSSException("path is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");

        try {
            // 创建一个大小为 0 的空对象，用于模拟目录结构
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(path)
                            .build(),
                    RequestBody.fromBytes(new byte[0])
            );
        } catch (Exception e) {
            logger.error("create path failed: {},bucket:{},OSS[{}]", path,bucket,ossConfig.getName());
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+",path:"+path),e);
        }
    }

    public List<OSSFile> listOssFile(String bucket, String path) {
        return listOssFile(bucket,path,false);
    }

    public List<OSSFile> listOssFile(String bucket) {
        return listOssFile(bucket,null,false);
    }

    public List<OSSFile> listOssFile(String bucket,String path,boolean recursive) {
        if(bucket == null )
            throw new DataOSSException("bucket is blank!"+",OSS["+ossConfig.getName()+"]");
//        if(path == null )
//            throw new DataOSSException("path is blank,bucket:"+bucket+",OSS["+ossConfig.getName()+"]");
//
//        if (!SimpleStringUtil.hasLength(path))
//            return null;
        try {
            List<OSSFile> list = new ArrayList<>();

            ListObjectsV2Request.Builder requestBuilder = null;
            if(SimpleStringUtil.isNotEmpty(path)){
                requestBuilder = ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(path);
            }
            else{
                requestBuilder = ListObjectsV2Request.builder()
                        .bucket(bucket);
            }

            if (!recursive) {
                requestBuilder.delimiter("/");
            }

            ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());
            String dir = null;
            for (S3Object object : response.contents()) {
                // 排除与目录本身同名的对象
                if (path != null && object.key().equals(path)) continue;

                OSSFile ossFile = new OSSFile();
                String key = object.key();
                ossFile.setObjectName(key);
                ossFile.setBulket(bucket);
                ossFile.setSize(object.size());
                boolean isDir = key.endsWith("/");
                int index = key.lastIndexOf("/");
                if(!isDir) {
                   

                    if (index != -1) {
                        dir = key.substring(0, index);
                    } else {
                        dir = null;
                    }
                }
                else{
                    key = key.substring(0,index);
                    index = key.lastIndexOf("/");
                    if (index != -1) {
                        dir = key.substring(0, index);
                    } else {
                        dir = null;
                    }
                }
                ossFile.setParentDir(dir);
                ossFile.setDir(isDir); // S3 没有真正的目录结构，通常以 / 结尾表示“伪目录”
                ossFile.setLastModified(Date.from(object.lastModified()));
                list.add(ossFile);
            }

            // 如果需要递归遍历更多页，可以使用分页器继续获取
            // 这里只处理了第一页内容，如需全量可添加分页逻辑

            return list;
        } catch (Exception e) {
            logger.error("list path: {},bucket:{},OSS[{}]", path,bucket,ossConfig.getName());
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+",path:"+path),e);
        }
    }
    @Override
    public String getInternalDownloadUrl(String bucket,String objectName, Integer expirySec) {
        return getSharedUrl(  bucket,objectName, expirySec, this.ossConfig.getEndpoint());
    }

    @Override
    public String getExternalDownloadUrl(String bucket,String objectName, Integer expirySec) {
        return getSharedUrl(  bucket,objectName, expirySec, this.ossConfig.getEndpoint());
    }


    @Override
    public String getInternalDownloadUrl(String bucket,String objectName, Integer expirySec,String endpoint) {
        return getSharedUrl(  bucket,objectName, expirySec, endpoint);
    }

    @Override
    public String getExternalDownloadUrl(String bucket,String objectName, Integer expirySec,String endpoint) {
        return getSharedUrl(  bucket,objectName, expirySec, endpoint);
    }
//    private String getSharedUrl(String bucket, String objectName, Integer expirySec, String customEndpoint) {
//        logger.info("getSharedUrl, objectName={}", objectName);
//        if (StringUtils.isBlank(objectName)) {
//            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + "customEndpoint:" + customEndpoint + ",objectName:" + objectName));
//        }
//    
//        try {
//            // 首先生成原始的预签名URL
//            S3Presigner presigner = S3Presigner.builder()
//                    .region(s3Client.serviceClientConfiguration().region())
//                    .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
//                    .endpointOverride(URI.create(customEndpoint))  // 使用传入的 endpoint
//                    .build();
//    
//            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                    .bucket(bucket)
//                    .key(objectName)
//                    .build();
//    
//            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                    .signatureDuration(Duration.ofSeconds(
//                            expirySec != null ? expirySec : 10))
//                    .getObjectRequest(getObjectRequest)
//                    .build();
//    
//            String originalUrl = presigner.presignGetObject(presignRequest).url().toString();
//            
//            // 将原始URL进行URL编码，然后进行Base64编码
//            String encodedUrl = java.net.URLEncoder.encode(originalUrl, "UTF-8");
//            String base64EncodedUrl = java.util.Base64.getEncoder().encodeToString(encodedUrl.getBytes("UTF-8"));
//            
//            // 构建最终的共享链接
//            String sharedUrl = customEndpoint + "/api/v1/download-shared-object/" + base64EncodedUrl;
//            
//            return sharedUrl;
//        } catch (DataOSSException e) {
//            logger.error("genDownloadUrl Exception !", e);
//            throw e;
//        } catch (Exception e) {
//            logger.error("genDownloadUrl Exception !", e);
//            throw new DataOSSException(buildErrorInfo("bucket:" + bucket + "customEndpoint:" + customEndpoint + ",objectName:" + objectName), e);
//        }
//    }

    private String getSharedUrl(String bucket,String objectName, Integer expirySec, String customEndpoint) {
        logger.info("getDownloadUrl, objectName={}", objectName);
        if (StringUtils.isBlank(objectName)) {
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+"customEndpoint:"+customEndpoint+",objectName:"+objectName));
        }

        try {
            // 创建 S3Presigner 实例，使用传入的 customEndpoint
            S3Presigner presigner = S3Presigner.builder()
                    .region(s3Client.serviceClientConfiguration().region())
                    .credentialsProvider(s3Client.serviceClientConfiguration().credentialsProvider())
                    .endpointOverride(URI.create(customEndpoint))  // 使用传入的 endpoint
                    .build();

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(
                            expirySec != null ? expirySec : 10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        } catch (DataOSSException e) {
            logger.error("genDownloadUrl Exception !", e);
            throw e;
        }catch (Exception e) {
            logger.error("genDownloadUrl Exception !", e);
            throw new DataOSSException(buildErrorInfo("bucket:"+bucket+"customEndpoint:"+customEndpoint+",objectName:"+objectName),e);
        }
    }

}
