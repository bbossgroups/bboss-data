package org.frameworkset.nosql.mongodb;

import com.frameworkset.util.SimpleStringUtil;
import com.frameworkset.util.StringUtil;
import com.mongodb.*;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.connection.SslSettings;
import org.bson.conversions.Bson;
import org.frameworkset.spi.BeanNameAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MongoDB implements BeanNameAware {

	private MongoDBConfig config;
	private static Logger log = LoggerFactory.getLogger(MongoDB.class);

	private MongoClient mongoclient;


	private List<MongoCredential> mongoCredentials;

	public MongoDB getMongoClient() {

		 
		return this;
	}
	
	public MongoClient getMongo() {

		 
		return this.mongoclient;
	}

	private List<ServerAddress> parserAddress() throws NumberFormatException, UnknownHostException {
		String serverAddresses = this.getServerAddresses();
		if (StringUtil.isEmpty(serverAddresses))
			return null;

		serverAddresses = serverAddresses.trim();
		List<ServerAddress> trueaddresses = new ArrayList<ServerAddress>();
		String mode = this.getMode();
		if (mode != null && mode.equals("simple")) {
			String info[] = serverAddresses.split(":");
			ServerAddress ad = new ServerAddress(info[0].trim(), Integer.parseInt(info[1].trim()));
			trueaddresses.add(ad);
			return trueaddresses;
		}
		String[] addresses = null;
		if(serverAddresses.indexOf(",") > 0){
			addresses = serverAddresses.split(",");
		}
		else{
			addresses = serverAddresses.split("\n");
		}

		for (String address : addresses) {
			address = address.trim();
			String info[] = address.split(":");
			ServerAddress ad = new ServerAddress(info[0].trim(), Integer.parseInt(info[1].trim()));
			trueaddresses.add(ad);
		}
		return trueaddresses;
	}

 

	private WriteConcern _getWriteConcern() {
		String writeConcern = this.getWriteConcern();
		if (StringUtil.isEmpty(writeConcern))
			return null;
		writeConcern = writeConcern.trim();
		if (writeConcern.equals("NONE"))
			return WriteConcern.UNACKNOWLEDGED;
//		else if (writeConcern.equals("NORMAL"))
//			return WriteConcern.NORMAL;
//		else if (writeConcern.equals("SAFE"))
//			return WriteConcern.SAFE;
		else if (writeConcern.equals("MAJORITY"))
			return WriteConcern.MAJORITY;
		else if (writeConcern.equals("W1"))
			return WriteConcern.W1;
		else if (writeConcern.equals("W2"))
			return WriteConcern.W2;
		else if (writeConcern.equals("W3"))
			return WriteConcern.W3;
//		else if (writeConcern.equals("FSYNC_SAFE"))
//			return WriteConcern.FSYNC_SAFE;
		else if (writeConcern.equals("JOURNAL_SAFE"))
			return WriteConcern.JOURNALED;
		else if (writeConcern.equals("JOURNALED"))
			return WriteConcern.JOURNALED;
//		else if (writeConcern.equals("REPLICAS_SAFE"))
//			return WriteConcern.REPLICAS_SAFE;
//		else if (writeConcern.startsWith("REPLICA_ACKNOWLEDGED")) {
//			int idx = writeConcern.indexOf("(");
//			if (idx < 0) {
//				return WriteConcern.REPLICA_ACKNOWLEDGED;
//			} else {
//				String n = writeConcern.substring(idx + 1, writeConcern.length() - 1);
//				try {
//					if (n.indexOf(",") < 0) {
//						int N = Integer.parseInt(n);
//						return new WriteConcern(N);
//					} else {
//						String[] p = n.split(",");
//						n = p[0];
//						String _wtimeout = p[1];
//						int N = Integer.parseInt(n);
//						int wtimeout = Integer.parseInt(_wtimeout);
//						return new WriteConcern(N, wtimeout, false);
//					}
//				} catch (NumberFormatException e) {
//					return WriteConcern.REPLICA_ACKNOWLEDGED;
//				}
//			}
//		}
		else if (writeConcern.equals("ACKNOWLEDGED"))
			return WriteConcern.ACKNOWLEDGED;
		else if (writeConcern.equals("UNACKNOWLEDGED"))
			return WriteConcern.UNACKNOWLEDGED;
//		else if (writeConcern.equals("FSYNCED"))
//			return WriteConcern.FSYNCED;
		else if (writeConcern.equals("JOURNALED"))
			return WriteConcern.JOURNALED;
		else if (writeConcern.equals("ERRORS_IGNORED"))
			return WriteConcern.UNACKNOWLEDGED;

		throw new RuntimeException("未知的WriteConcern:" + writeConcern);
	}

	private ReadPreference _getReadPreference() {
		String readPreference = this.getReadPreference();
		if (StringUtil.isEmpty(readPreference))
			return null;
		if (readPreference.equals("PRIMARY"))
			return ReadPreference.primary();
		else if (readPreference.equals("SECONDARY"))
			return ReadPreference.secondary();
		else if (readPreference.equals("SECONDARY_PREFERRED"))
			return ReadPreference.secondaryPreferred();
		else if (readPreference.equals("PRIMARY_PREFERRED"))
			return ReadPreference.primaryPreferred();
		else if (readPreference.equals("NEAREST"))
			return ReadPreference.nearest();
		throw new RuntimeException("未知的ReadPreference:" + readPreference);
	}

	/**
	 * MONGODB-AWS
	 * GSSAPI
	 * PLAIN
	 * MONGODB_X509
	 * SCRAM-SHA-1
	 * SCRAM-SHA-256
	 */
	private void buildCredentials() {


//		/**
//		 * The GSSAPI mechanism.  See the <a href="http://tools.ietf.org/html/rfc4752">RFC</a>.
//		 *
//		 * @mongodb.driver.manual core/authentication/#kerberos-authentication GSSAPI
//		 */
//		public static final String GSSAPI_MECHANISM = GSSAPI.getMechanismName();
//
//		/**
//		 * The PLAIN mechanism.  See the <a href="http://www.ietf.org/rfc/rfc4616.txt">RFC</a>.
//		 *
//		 * @since 2.12
//		 * @mongodb.driver.manual core/authentication/#ldap-proxy-authority-authentication PLAIN
//		 */
//		public static final String PLAIN_MECHANISM = PLAIN.getMechanismName();
//
//		/**
//		 * The MongoDB X.509
//		 *
//		 * @since 2.12
//		 * @mongodb.driver.manual core/authentication/#x-509-certificate-authentication X-509
//		 */
//		public static final String MONGODB_X509_MECHANISM = MONGODB_X509.getMechanismName();
//
//		/**
//		 * The SCRAM-SHA-1 Mechanism.
//		 *
//		 * @since 2.13
//		 * @mongodb.server.release 3.0
//		 * @mongodb.driver.manual core/authentication/#authentication-scram-sha-1 SCRAM-SHA-1
//		 */
//		public static final String SCRAM_SHA_1_MECHANISM = SCRAM_SHA_1.getMechanismName();

//		/**
//		 * The SCRAM-SHA-256 Mechanism.
//		 *
//		 * @since 3.8
//		 * @mongodb.server.release 4.0
//		 * @mongodb.driver.manual core/authentication/#authentication-scram-sha-256 SCRAM-SHA-256
//		 */
//		public static final String SCRAM_SHA_256_MECHANISM = SCRAM_SHA_256.getMechanismName();
		if (config.getCredentials() != null && config.getCredentials().size() > 0) {
			this.mongoCredentials = new ArrayList<MongoCredential>();
			for (ClientMongoCredential clientMongoCredential : config.getCredentials()) {
				if (StringUtil.isEmpty(clientMongoCredential.getMechanism())) {
					mongoCredentials.add(MongoCredential.createCredential(clientMongoCredential.getUserName(),
							clientMongoCredential.getDatabase(),
							clientMongoCredential.getPassword().toCharArray()));
				}
				else  if (clientMongoCredential.getMechanism().equals(MongoCredential.SCRAM_SHA_1_MECHANISM)) {//    SCRAM_SHA_256("SCRAM-SHA-256");
					mongoCredentials.add(MongoCredential.createScramSha1Credential(clientMongoCredential.getUserName(),
							clientMongoCredential.getDatabase(),
							clientMongoCredential.getPassword().toCharArray()));
				}
				else  if (clientMongoCredential.getMechanism().equals(MongoCredential.SCRAM_SHA_256_MECHANISM)) {//    SCRAM_SHA_256("SCRAM-SHA-256");
					mongoCredentials.add(MongoCredential.createScramSha256Credential(clientMongoCredential.getUserName(),
							clientMongoCredential.getDatabase(),
							clientMongoCredential.getPassword().toCharArray()));
				}
//				else if (clientMongoCredential.getMechanism().equals(MongoCredential.MONGODB_CR_MECHANISM)) {
//					mongoCredentials.add(MongoCredential.createMongoCRCredential(clientMongoCredential.getUserName(),
//							clientMongoCredential.getDatabase(), clientMongoCredential.getPassword().toCharArray()));
//				}
				else if (clientMongoCredential.getMechanism().equals(MongoCredential.PLAIN_MECHANISM)) {
					mongoCredentials.add(MongoCredential.createPlainCredential(clientMongoCredential.getUserName(),
							clientMongoCredential.getDatabase(), clientMongoCredential.getPassword().toCharArray()));
				} else if (clientMongoCredential.getMechanism().equals(MongoCredential.MONGODB_X509_MECHANISM)) {
					mongoCredentials
							.add(MongoCredential.createMongoX509Credential(clientMongoCredential.getUserName()));
				} else if (clientMongoCredential.getMechanism().equals(MongoCredential.GSSAPI_MECHANISM)) {
					mongoCredentials.add(MongoCredential.createGSSAPICredential(clientMongoCredential.getUserName()));
				}
				else if (clientMongoCredential.getMechanism().equals("MONGODB-AWS")) {
					mongoCredentials.add(MongoCredential.createAwsCredential(clientMongoCredential.getUserName(),clientMongoCredential.getPassword().toCharArray()));
				}
				else {
					throw new UnsupportedOperationException("Unsupported Mechanism:"+clientMongoCredential.getMechanism());
				}
				
				
			}
		}

	}

	public void initWithConfig(MongoDBConfig config){
		this.config = config;
		this.init();
	}


	public String getName() {
		check();
		return config.getName();
	}

	public void setName(String name) {
		check();
		this.config.setName(name);
	}

	public String getServerAddresses() {
		return this.config.getServerAddresses();
	}

	private void check(){
		if(config == null){
			config = new MongoDBConfig();
		}
	}
	public void setServerAddresses(String serverAddresses) {
		check();
		this.config.setServerAddresses(serverAddresses);
	}
	public String getMode() {
		check();
		return this.config.getMode();
	}

	public void setMode(String mode) {
		check();
		this.config.setMode(mode);
	}
	public String getOption() {
		check();
		return this.config.getOption();
	}

	public void setOption(String option) {
		check();
		this.config.setOption(option);
	}

	public String getWriteConcern() {
		check();
		return config.getWriteConcern();
	}

	public void setWriteConcern(String writeConcern) {
		check();
		this.config.setWriteConcern(writeConcern);
	}

	public String getReadPreference() {
		check();
		return config.getReadPreference();
	}

	public void setReadPreference(String readPreference) {
		check();
		this.config.setReadPreference(readPreference);
	}


	public int getConnectionsPerHost() {
		check();
		return config.getConnectionsPerHost();
	}
	public int getMinSize() {
		check();
		return config.getMinSize();
	}
	public void setMinSize(int minSize) {
		config.setMinSize(minSize);
	}
	public void setConnectionsPerHost(int connectionsPerHost) {
		check();
		this.config.setConnectionsPerHost(connectionsPerHost);
	}

	public long getMaxConnectionLifeTime() {
		check();
		return this.config.getMaxConnectionLifeTime();
	}

	public void setMaxConnectionLifeTime(long maxConnectionLifeTime) {

		check();
		 this.config.setMaxConnectionLifeTime( maxConnectionLifeTime);
	}

	public long getMaxConnectionIdleTime() {
		check();
		return this.config.getMaxConnectionIdleTime();
	}

	public void setMaxConnectionIdleTime(long maxConnectionIdleTime) {
		check();
		this.config.setMaxConnectionIdleTime( maxConnectionIdleTime);
	}

	public long getMaintenanceInitialDelay() {
		check();
		return this.config.getMaintenanceInitialDelay();
	}

	public void setMaintenanceInitialDelay(long maintenanceInitialDelay) {
		check();
		this.config.setMaintenanceInitialDelay( maintenanceInitialDelay);
	}

	public long getMaintenanceFrequency() {
		check();
		return this.config.getMaintenanceFrequency( );
	}

	public void setMaintenanceFrequency(long maintenanceFrequency) {
		check();
		this.config.setMaintenanceFrequency(maintenanceFrequency );
	}

	public int getMaxWaitTime() {
		check();
		return config.getMaxWaitTime();
	}

	public void setMaxWaitTime(int maxWaitTime) {
		check();
		this.config.setMaxWaitTime(maxWaitTime);
	}

	public int getSocketTimeout() {
		check();
		return config.getSocketTimeout();
	}


	public int getReceiveBufferSize() {
		check();
		return config.getReceiveBufferSize();
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		config.setReceiveBufferSize(receiveBufferSize);
	}

	public int getSendBufferSize() {
		check();
		return config.getSendBufferSize();
	}
	public void setSendBufferSize(int sendBufferSize) {
		check();
		config.setSendBufferSize(  sendBufferSize);
	}
	public void setSocketTimeout(int socketTimeout) {
		check();
		this.config.setSocketTimeout(socketTimeout);
	}

	public int getConnectTimeout() {
		check();
		return config.getConnectTimeout();
	}

	public void setConnectTimeout(int connectTimeout) {
		check();
		this.config.setConnectTimeout(connectTimeout);
	}



	public Boolean getSocketKeepAlive() {
		check();
		return config.getSocketKeepAlive();
	}

	public void setSocketKeepAlive(Boolean socketKeepAlive) {
		check();
		this.config.setSocketKeepAlive(socketKeepAlive);
	}

	private Builder builder(){
		// Construct a ServerApi instance using the ServerApi.builder() method
		ServerApi serverApi = ServerApi.builder()
				.version(ServerApiVersion.V1)
				.build();
		Builder clientBuilder = MongoClientSettings.builder() ;
		WriteConcern writeConcern = _getWriteConcern();
		if(writeConcern != null)
			clientBuilder.writeConcern(writeConcern);
		String mode = this.getMode();
		if (mode != null && !mode.equals("simple")) {
			ReadPreference readPreference = _getReadPreference();
			if (readPreference != null) {
				clientBuilder.readPreference(readPreference);
			}
		}
		clientBuilder.serverApi(serverApi);
		this.buildCredentials();
		if(mongoCredentials != null){
			clientBuilder.credential(mongoCredentials.get(0));
		}
		clientBuilder.applyToConnectionPoolSettings(builder -> {
			if(this.getMaxWaitTime() >= 0) {
				builder.maxWaitTime(this.getMaxWaitTime(), TimeUnit.MILLISECONDS);
			}
			if(this.getConnectionsPerHost() >= 0 )
				builder.maxSize(this.getConnectionsPerHost());
			if(this.getMinSize() >= 0)
				builder.maxSize(this.getMinSize());
			if(this.getMaintenanceFrequency() > 0L)
				builder.maintenanceFrequency(getMaintenanceFrequency(),TimeUnit.MILLISECONDS);
			if(this.getMaintenanceInitialDelay() > 0L)
				builder.maintenanceInitialDelay(getMaintenanceInitialDelay(),TimeUnit.MILLISECONDS);
			if(this.getMaxConnectionIdleTime() > 0L)
				builder.maxConnectionIdleTime(getMaxConnectionIdleTime(),TimeUnit.MILLISECONDS);
			if(this.getMaxConnectionLifeTime() > 0L)
				builder.maxConnectionLifeTime(getMaxConnectionLifeTime(),TimeUnit.MILLISECONDS);

		});
		clientBuilder.applyToSocketSettings(builder -> {
			if(this.getConnectTimeout() > 0)
				builder.connectTimeout(getConnectTimeout(),TimeUnit.MILLISECONDS);
			if(this.getSocketTimeout() > 0){
				builder.readTimeout(this.getSocketTimeout(),TimeUnit.MILLISECONDS);
			}
			if(this.getSendBufferSize() > 0)
				builder.sendBufferSize(getSendBufferSize());
			if(this.getReceiveBufferSize() > 0 )
				builder.receiveBufferSize(this.getReceiveBufferSize());
		});
		return clientBuilder;
	}

    
    private void customSettingBuilder(MongoClientSettings.Builder clientBuilder){
        if(config.getCustomSettingBuilder() != null){
            config.getCustomSettingBuilder().customSettingBuilder(clientBuilder,config);

        }
    }
	public void init() {
		try {
			config.build();
			if(log.isInfoEnabled()){
				log.info("Init mongodb client config: {}",SimpleStringUtil.object2json(config));
			}

			if(SimpleStringUtil.isNotEmpty(config.getConnectString())){
				Builder clientBuilder = builder();
                clientBuilder.applyConnectionString(new ConnectionString(config.getConnectString()));
                customSettingBuilder(clientBuilder);
				MongoClientSettings settings = clientBuilder.build();
				// Create a new client and connect to the server
				this.mongoclient =  MongoClients.create(settings);
			}
			else {
				List<ServerAddress> servers = parserAddress();
				Builder clientBuilder = builder();
				clientBuilder.applyToClusterSettings(builder ->{
						builder.hosts(servers);

				});
                customSettingBuilder(clientBuilder);
				MongoClientSettings settings = clientBuilder.build();
				this.mongoclient = MongoClients.create(settings);

			}
		} catch (RuntimeException e) {
			log.error("初始化mongodb client failed:"+SimpleStringUtil.object2json(config), e);
			throw e;

		} catch (Exception e) {
			log.error("初始化mongodb client failed:"+SimpleStringUtil.object2json(config), e);
			throw new RuntimeException(e);

		}
	}


	public void close() {
		if (this.mongoclient != null)
			this.mongoclient.close();
	}

	public static <TDocument> UpdateResult replaceOne(MongoCollection<TDocument> collection, Bson filter, TDocument replacement) {
		try {
			UpdateResult wr = collection.replaceOne(filter, replacement);

			return wr;
		} catch (WriteConcernException e) {
			log.debug("update:", e);
			return null;
		}
	}
	public static UpdateResult updateMany(MongoCollection collection, Bson filter, Bson update) {

		UpdateResult wr = collection.updateMany(filter, update);

		return wr;

	}
	public static UpdateResult updateMany(MongoCollection collection, Bson filter, Bson update, UpdateOptions concern) {

		UpdateResult wr = collection.updateMany(filter, update,  concern);

		return wr;

	}

	public static UpdateResult updateOne(MongoCollection collection, Bson filter, Bson update) {

		UpdateResult wr = collection.updateOne(filter, update);

		return wr;

	}
	public static UpdateResult updateOne(MongoCollection collection, Bson filter, Bson update, UpdateOptions concern) {

		UpdateResult wr = collection.updateOne(filter, update,  concern);

		return wr;

	}
	public static <TDocument> TDocument findAndModify(MongoCollection<TDocument> collection, Bson query, Bson update, FindOneAndUpdateOptions options) {
		try {
			TDocument object = collection.findOneAndUpdate(query, update,options);
			return object;
		} catch (WriteConcernException e) {
			log.debug("findAndModify:", e);
			return null;
		}
	}
	public static <TDocument> TDocument findAndModify(MongoCollection<TDocument> collection, Bson query, Bson update) {
		try {
			TDocument object = collection.findOneAndUpdate(query, update);
			return object;
		} catch (WriteConcernException e) {
			log.debug("findAndModify:", e);
			return null;
		}
	}



	public static <TDocument> TDocument findAndRemove(MongoCollection<TDocument> collection, Bson query) {
		try {
			TDocument object = collection.findOneAndDelete(query);
			return object;
		} catch (WriteConcernException e) {
			log.debug("findAndRemove:", e);
			return null;
		}
	}



	public static <TDocument> InsertManyResult insert( MongoCollection<TDocument> collection, TDocument... documents) {

		if(documents == null)
			return null;
		List<TDocument> tDocuments = Arrays.asList(documents);
		return collection.insertMany(tDocuments);

	}

	public static <TDocument> InsertManyResult insert( MongoCollection<TDocument> collection, List<? extends TDocument> documents) {

		return collection.insertMany(documents);

	}


	public static <TDocument> InsertManyResult insert(InsertManyOptions concern, MongoCollection<TDocument> collection, TDocument... documents) {

		if(documents == null)
			return null;
		List<TDocument> tDocuments = Arrays.asList(documents);
		return collection.insertMany(tDocuments, concern);

	}
	public static <TDocument> InsertManyResult insert(InsertManyOptions concern, MongoCollection<TDocument> collection, List<? extends TDocument> documents) {

		return collection.insertMany(documents, concern);

	}

	public static DeleteResult remove(MongoCollection collection, Bson filter) {
		return collection.deleteOne(filter);
	}


	public static DeleteResult remove(MongoCollection collection, Bson filter, DeleteOptions concern) {

		return collection.deleteOne(filter,concern);
	}
	public static DeleteResult removeMany(MongoCollection collection, Bson filter) {
		return collection.deleteMany(filter);
	}


	public static DeleteResult removeMany(MongoCollection collection, Bson filter, DeleteOptions concern) {

		return collection.deleteMany(filter,concern);
	}
	public MongoDatabase getDB(String dbname) {
		return this.mongoclient.getDatabase(dbname);
	}

	public MongoDatabase getMongoDatabase(String dbname) {
		return this.mongoclient.getDatabase(dbname);
	}
	public  MongoCollection getDBCollection(String dbname,String table)
	{
		MongoDatabase db = this.getMongoDatabase(dbname);
		return db.getCollection(table);
		
	}
	public void setCredentials(List<ClientMongoCredential> credentials) {
		this.check();
		this.config.setCredentials( credentials);
	}
	@Override
	public void setBeanName(String name) {
		this.check();
		this.config.setName(name);
	}

	public String getConnectString() {
		if(config != null)
			return config.getConnectString();
		else{
			return null;
		}
	}

	public void setConnectString(String connectString) {
		this.check();
		this.config.setConnectString(connectString);
	}
}
