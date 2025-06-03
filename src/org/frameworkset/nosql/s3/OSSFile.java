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

import io.minio.messages.ResponseDate;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author biaoping.yin
 * @Date 2024/8/8
 */
public class OSSFile {
    private String objectName;
    private long size;
    private String userTags;
    private boolean isDir = false;
    private String encodingType = null;
    private Date responseDate;


    private String etag;
    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUserTags() {
        return userTags;
    }

    public void setUserTags(String userTags) {
        this.userTags = userTags;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }
}
