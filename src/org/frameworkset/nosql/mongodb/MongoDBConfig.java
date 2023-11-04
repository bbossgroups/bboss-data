package org.frameworkset.nosql.mongodb;
/**
 * Copyright 2008 biaoping.yin
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

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description: </p>
 * <p></p>
 * <p>Copyright (c) 2018</p>
 * @Date 2019/1/11 16:42
 * @author biaoping.yin
 * @version 1.0
 */
public class MongoDBConfig {

	private String name;
	private String serverAddresses;
	private String option;
	private String writeConcern;
	private String readPreference;

	private int connectionsPerHost = 100;
	private int minSize = 100;
	private long maintenanceFrequency;
	private long maintenanceInitialDelay;
	private long maxConnectionIdleTime;
	private long maxConnectionLifeTime;
	private int maxWaitTime = 120000;
	private int socketTimeout = 0;
	private int connectTimeout = 15000;
	private int receiveBufferSize;
	private int sendBufferSize;


	private String connectString;
	private String userName;
	private String password;
	private String mechanism;

	public String getUserName() {
		return userName;
	}

	public MongoDBConfig setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public MongoDBConfig setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getMechanism() {
		return mechanism;
	}

	/**
	 * MONGODB-AWS
	 * GSSAPI
	 * PLAIN
	 * MONGODB_X509
	 * SCRAM-SHA-1
	 * SCRAM-SHA-256
	 */
	public MongoDBConfig setMechanism(String mechanism) {
		this.mechanism = mechanism;
		return this;
	}

	public List<ClientMongoCredential> getCredentials() {
		return credentials;
	}

	private boolean builded;
	public void build(){
		if(builded)
			return;
		builded = true;
		if(SimpleStringUtil.isNotEmpty(userName) && SimpleStringUtil.isNotEmpty(password))
		if (credentials == null || credentials.size() == 0) {
			if(credentials == null)
				credentials = new ArrayList<ClientMongoCredential>();

			ClientMongoCredential clientMongoCredential = new ClientMongoCredential();
			if(SimpleStringUtil.isNotEmpty(authDb)) {
				clientMongoCredential.setDatabase(authDb);
			}
			if(SimpleStringUtil.isNotEmpty(mechanism))
				clientMongoCredential.setMechanism(mechanism);
			clientMongoCredential.setUserName(userName);
			clientMongoCredential.setPassword(password);
			credentials.add(clientMongoCredential);
		}
	}
	public MongoDBConfig setCredentials(List<ClientMongoCredential> credentials) {
		this.credentials = credentials;
		return this;
	}

	private List<ClientMongoCredential> credentials;


	private String authDb;

	private Boolean socketKeepAlive = false;

	private String mode;


	public String getName() {
		return name;
	}

	public MongoDBConfig setName(String name) {
		this.name = name;
		return this;
	}

	public String getServerAddresses() {
		return serverAddresses;
	}

	public MongoDBConfig setServerAddresses(String serverAddresses) {
		this.serverAddresses = serverAddresses;
		return this;
	}

	public String getOption() {
		return option;
	}

	public MongoDBConfig setOption(String option) {
		this.option = option;
		return this;
	}

	public String getWriteConcern() {
		return writeConcern;
	}

	public MongoDBConfig setWriteConcern(String writeConcern) {
		this.writeConcern = writeConcern;
		return this;
	}

	public String getReadPreference() {
		return readPreference;
	}

	public MongoDBConfig setReadPreference(String readPreference) {
		this.readPreference = readPreference;
		return this;
	}


	public int getConnectionsPerHost() {
		return connectionsPerHost;
	}

	public MongoDBConfig setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
		return this;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public MongoDBConfig setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
		return this;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public MongoDBConfig setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public MongoDBConfig setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}


	public Boolean getSocketKeepAlive() {
		return socketKeepAlive;
	}

	public MongoDBConfig setSocketKeepAlive(Boolean socketKeepAlive) {
		this.socketKeepAlive = socketKeepAlive;
		return this;
	}

	public String getMode() {
		return mode;
	}

	public MongoDBConfig setMode(String mode) {
		this.mode = mode;
		return this;
	}


	public String getConnectString() {
		return connectString;
	}

	public MongoDBConfig setConnectString(String connectString) {
		this.connectString = connectString;
		return this;
	}

	public int getMinSize() {
		return minSize;
	}

	public MongoDBConfig setMinSize(int minSize) {
		this.minSize = minSize;
		return this;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public MongoDBConfig setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
		return this;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public MongoDBConfig setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
		return this;
	}

	public long getMaxConnectionLifeTime() {
		return maxConnectionLifeTime;
	}

	public MongoDBConfig setMaxConnectionLifeTime(long maxConnectionLifeTime) {
		this.maxConnectionLifeTime = maxConnectionLifeTime;
		return this;
	}

	public long getMaxConnectionIdleTime() {
		return maxConnectionIdleTime;
	}

	public MongoDBConfig setMaxConnectionIdleTime(long maxConnectionIdleTime) {
		this.maxConnectionIdleTime = maxConnectionIdleTime;
		return this;
	}

	public long getMaintenanceInitialDelay() {
		return maintenanceInitialDelay;
	}

	public MongoDBConfig setMaintenanceInitialDelay(long maintenanceInitialDelay) {
		this.maintenanceInitialDelay = maintenanceInitialDelay;
		return this;
	}

	public long getMaintenanceFrequency() {
		return maintenanceFrequency;
	}

	public MongoDBConfig setMaintenanceFrequency(long maintenanceFrequency) {
		this.maintenanceFrequency = maintenanceFrequency;
		return this;
	}


	public String getAuthDb() {
		return authDb;
	}

	public MongoDBConfig setAuthDb(String authDb) {
		this.authDb = authDb;
		return this;
	}
}
