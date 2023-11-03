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

	public List<ClientMongoCredential> getCredentials() {
		return credentials;
	}

	public void setCredentials(List<ClientMongoCredential> credentials) {
		this.credentials = credentials;
	}

	private List<ClientMongoCredential> credentials;

	private Boolean socketKeepAlive = false;

	private String mode;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerAddresses() {
		return serverAddresses;
	}

	public void setServerAddresses(String serverAddresses) {
		this.serverAddresses = serverAddresses;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getWriteConcern() {
		return writeConcern;
	}

	public void setWriteConcern(String writeConcern) {
		this.writeConcern = writeConcern;
	}

	public String getReadPreference() {
		return readPreference;
	}

	public void setReadPreference(String readPreference) {
		this.readPreference = readPreference;
	}


	public int getConnectionsPerHost() {
		return connectionsPerHost;
	}

	public void setConnectionsPerHost(int connectionsPerHost) {
		this.connectionsPerHost = connectionsPerHost;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}


	public Boolean getSocketKeepAlive() {
		return socketKeepAlive;
	}

	public void setSocketKeepAlive(Boolean socketKeepAlive) {
		this.socketKeepAlive = socketKeepAlive;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public long getMaxConnectionLifeTime() {
		return maxConnectionLifeTime;
	}

	public void setMaxConnectionLifeTime(long maxConnectionLifeTime) {
		this.maxConnectionLifeTime = maxConnectionLifeTime;
	}

	public long getMaxConnectionIdleTime() {
		return maxConnectionIdleTime;
	}

	public void setMaxConnectionIdleTime(long maxConnectionIdleTime) {
		this.maxConnectionIdleTime = maxConnectionIdleTime;
	}

	public long getMaintenanceInitialDelay() {
		return maintenanceInitialDelay;
	}

	public void setMaintenanceInitialDelay(long maintenanceInitialDelay) {
		this.maintenanceInitialDelay = maintenanceInitialDelay;
	}

	public long getMaintenanceFrequency() {
		return maintenanceFrequency;
	}

	public void setMaintenanceFrequency(long maintenanceFrequency) {
		this.maintenanceFrequency = maintenanceFrequency;
	}
}
