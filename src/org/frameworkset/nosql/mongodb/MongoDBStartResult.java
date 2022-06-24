package org.frameworkset.nosql.mongodb;
/**
 * Copyright 2022 bboss
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

import java.util.Map;

/**
 * <p>Description: 记录启动数据源结果信息</p>
 * <p></p>
 * <p>Copyright (c) 2020</p>
 * @Date 2022/5/2
 * @author biaoping.yin
 * @version 1.0
 */
public class MongoDBStartResult {
	private Map<String,Object> dbstartResult;

	public MongoDBStartResult addDBStartResult(String dbName){
		if(dbstartResult == null){
			dbstartResult = new java.util.LinkedHashMap<>();

		}
		dbstartResult.put(dbName,dbName);
		return this;
	}

	public Map<String,Object> getDbstartResult() {
		return dbstartResult;
	}
}
