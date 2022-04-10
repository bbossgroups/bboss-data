package org.frameworkset.nosql.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.frameworkset.spi.BeanInfoAware;
import org.frameworkset.spi.InitializingBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static java.lang.Thread.sleep;

public class RedisDB extends BeanInfoAware implements InitializingBean,org.frameworkset.spi.DisposableBean{
	//	private ShardedJedisPool shardedJedispool;
	private String name;
	private JedisPool jedisPool;
	private List<NodeInfo> nodes;
	private String servers;
	private static Logger logger = LoggerFactory.getLogger(RedisDB.class);
	/**
	 * 等待超时重试次数
	 */
	private int poolTimeoutRetry  = 3;
	/**
	 * 等待超时重试时间间隔
	 */
	private long poolTimeoutRetryInterval  = 500l;

	public String getServers() {
		return servers;
	}

	public void setServers(String servers) {
		this.servers = servers;
	}

	private int poolMaxTotal;
	private long poolMaxWaitMillis;
	private int maxIdle = -1;
	private int minIdle = -1;

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	private Map<String,Object> properties;
	/**
	 * connectionTimeout
	 */
	private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	/**
	 * socketTimeout
	 */
	private int socketTimeout =  Protocol.DEFAULT_TIMEOUT;
	private int maxRedirections = 5;
	private boolean needAuthPerJedis = false;
	private boolean testOnBorrow = false;
	private boolean testOnReturn = false;
	private boolean testWhileIdle = false;
	private String auth;
	public static final String mode_single = "single";
	public static final String mode_cluster = "cluster";
	public static final String mode_shared = "shared";
	/**
	 * single|cluster|shared
	 */
	private String mode = mode_single;

	private ProviderJedisCluster jc;

	public RedisDB() {
		// TODO Auto-generated constructor stub
	}

	public RedisDB addProperity(String name,Object value){
		if(properties == null){
			properties = new LinkedHashMap<>();
		}
		properties.put(name, value);
		return this;
	}
	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getMaxRedirections() {
		return maxRedirections;
	}

	public void setMaxRedirections(int maxRedirections) {
		this.maxRedirections = maxRedirections;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	/**

	 public void startSharedPool() {
	 GenericObjectPoolConfig config = new GenericObjectPoolConfig();
	 config.setMaxTotal(poolMaxTotal);
	 config.setMaxWaitMillis(poolMaxWaitMillis);
	 List<JedisShardInfo> jedisClusterNode = new ArrayList<JedisShardInfo>();
	 for(int i = 0; i < nodes.size(); i ++)
	 {
	 NodeInfo node = nodes.get(i);
	 JedisShardInfo jedisShardInfo = new JedisShardInfo(node.getHost(), node.getPort());
	 if(this.auth != null)
	 jedisShardInfo.setPassword(auth);
	 jedisClusterNode.add(jedisShardInfo);
	 }

	 shardedJedispool = new ShardedJedisPool(config, jedisClusterNode);


	 }
	 */

	public void startSingleNode()
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(poolMaxTotal);
		config.setMaxWait(Duration.ofMillis(poolMaxWaitMillis));
//		config.setMaxWaitMillis(poolMaxWaitMillis);
		if(maxIdle > 0)
			config.setMaxIdle(maxIdle);
		if(minIdle > 0)
			config.setMinIdle(minIdle);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		config.setTestWhileIdle(testWhileIdle);
		NodeInfo node = nodes.get(0);
		jedisPool = new JedisPool(config,node.getHost(), node.getPort(), connectionTimeout,this.auth);

//		    Jedis jedis = pool.getResource();
//		    jedis.auth(this.auth);
//
//		    jedis.close();
//		    pool.destroy();
	}
	public void startPoolClusterPools() {
		GenericObjectPoolConfig<Connection> config = new GenericObjectPoolConfig<Connection>();
		config.setMaxTotal(poolMaxTotal);
		config.setMaxWait(Duration.ofMillis(poolMaxWaitMillis));
		if(maxIdle > 0)
			config.setMaxIdle(maxIdle);


		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		config.setTestWhileIdle(testWhileIdle);
		Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
		for(int i = 0; i < nodes.size(); i ++)
		{
			NodeInfo node = nodes.get(i);
			HostAndPort hostAndPort = new HostAndPort(node.getHost(), node.getPort());

			jedisClusterNode.add(hostAndPort);
		}

//		    jc = new JedisCluster(jedisClusterNode,this.timeout,this.maxRedirections, config);

		jc = new ProviderJedisCluster(jedisClusterNode, connectionTimeout, socketTimeout,
				this.maxRedirections, auth,config);

//		    jc.set("52", "poolTestValue2");
//		    jc.set("53", "poolTestValue2");
//		    System.out.println(jc.get("52"));
//		    System.out.println(jc.get("53"));
//		    jc.close();

	}


	public Jedis getRedis()
	{
		int count = 0;
		Jedis jedis = null;
		if(poolTimeoutRetry <= 0){//忽略重试
			jedis = jedisPool.getResource();
			if(needAuthPerJedis && this.auth != null)
				jedis.auth(auth);
			return jedis;
		}
		long interval = poolTimeoutRetryInterval;
		do {
			try {
				jedis = jedisPool.getResource();
				if(needAuthPerJedis && this.auth != null)
					jedis.auth(auth);
				break;
			} catch (JedisException jedisException) {
				Throwable noSuchElementException = jedisException.getCause();
				if (noSuchElementException instanceof NoSuchElementException) {
					String message = noSuchElementException.getMessage();
					if (message != null && message.startsWith("Timeout Waiting")) {//如果从连接池获取jedis对象失败，则进行重试
						if (count < this.poolTimeoutRetry) {
							try {
								if(interval > 0) {
									sleep(interval);
									if(interval < 10000l) {//最大等待10秒
										//每次等待延长100毫秒
										interval = interval + 100l;
									}
								}
								count ++;
								continue;
							} catch (InterruptedException e) {
								throw jedisException;
							}
						}
						else {
							throw jedisException;
						}
					}
					else{
						throw jedisException;
					}
				} else {
					throw jedisException;
				}
			}
		}while(true);
		return jedis;
	}
	/**
	 public ShardedJedis getSharedRedis()
	 {
	 ShardedJedis jedis = shardedJedispool.getResource();
	 return jedis;
	 }
	 */
	public ProviderJedisCluster geJedisCluster()
	{
		return jc;
	}
	/**
	 public void releaseSharedRedis(ShardedJedis redis) throws IOException
	 {
	 redis.close();
	 }*/

	public void releaseRedis(Jedis redis) throws IOException
	{
		redis.close();
	}

	public void close()
	{
		/**
		 if(shardedJedispool != null)
		 this.shardedJedispool.destroy();
		 */
		if(jc != null)
			try {
				jc.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if(jedisPool != null)
			jedisPool.destroy();
	}
	private void buildNodes(){
		List<NodeInfo> temp = new ArrayList<NodeInfo>();
		if(this.servers != null ){
			this.servers = servers.trim();
			if(!this.servers.equals("")){
				String _servers[] = null;
				if(servers.indexOf(",") > 0){
					_servers = servers.split(",");
				}
				else{
					_servers = servers.split("\n");
				}

				for(int i = 0; i < _servers.length; i ++){
					String server = _servers[i].trim();
					if(server.equals("")){
						continue;
					}
					String node[] = server.split(":");
					NodeInfo n = new NodeInfo();
					n.setHost(node[0].trim());
					if(node.length == 1){


						n.setPort(6379);
					}
					else
					{
						n.setPort(Integer.parseInt(node[1].trim()));
					}
					temp.add(n);
				}
			}
		}
		this.nodes = temp;
	}
	public static void main(String[] args){
		System.out.println("a\ra");
	}
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Redis datasourceName:")
				.append(this.getName() != null?getName():this.getBeaninfo().getName())
				.append(",servers:")
				.append(servers)
				.append(",auth:******,mode:")
				.append(mode)
				.append(",needAuthPerJedis:")
				.append(needAuthPerJedis)
				.append(",poolMaxTotal:")
				.append(poolMaxTotal)
				.append(",poolMaxWaitMillis:")
				.append(poolMaxWaitMillis)
				.append(",poolTimeoutRetry:")
				.append(poolTimeoutRetry)
				.append(",poolTimeoutRetryInterval:")
				.append(poolTimeoutRetryInterval)
				.append(",connectionTimeout:")
				.append(connectionTimeout)
				.append(",socketTimeout:")
				.append(socketTimeout)
				.append(",maxIdle:")
				.append(maxIdle)
				.append(",minIdle:")
				.append(minIdle)
				.append(",maxRedirections:")
				.append(maxRedirections)
				.append(",testOnBorrow:")
				.append(testOnBorrow)
				.append(",testOnReturn:")
				.append(testOnReturn)
				.append(",testWhileIdle:")
				.append(testWhileIdle)
				.append(",properties:");
		if(properties != null && properties.size() > 0){
			Iterator<Map.Entry<String,Object>> iterator = properties.entrySet().iterator();
			builder.append("{");
			int i = 0;
			while (iterator.hasNext()){
				if(i > 0){
					builder.append(",");
				}
				Map.Entry<String,Object> entry = iterator.next();
				builder.append(entry.getKey())
						.append(entry.getValue());

				i ++;
			}
			builder.append("}");

		}
		return builder.toString();
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("Init Jedis["+toString()+"]");
		}
		if(this.nodes == null){
			buildNodes();
		}
		/**
		 if(getMode().equals(mode_shared))
		 {
		 this.startSharedPool();
		 }*/
		if(getMode().equals(mode_cluster))
		{
			this.startPoolClusterPools();
		}
		else if(getMode().equals(mode_single))
		{
			this.startSingleNode();
		}

	}

	public int getPoolMaxTotal() {
		return poolMaxTotal;
	}

	public void setPoolMaxTotal(int poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
	}

	public long getPoolMaxWaitMillis() {
		return poolMaxWaitMillis;
	}

	public void setPoolMaxWaitMillis(long poolMaxWaitMillis) {
		this.poolMaxWaitMillis = poolMaxWaitMillis;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		if(auth != null && !auth.equals(""))
			this.auth = auth;
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		close();
	}


	public int getPoolTimeoutRetry() {
		return poolTimeoutRetry;
	}

	public void setPoolTimeoutRetry(int poolTimeoutRetry) {
		this.poolTimeoutRetry = poolTimeoutRetry;
	}

	public long getPoolTimeoutRetryInterval() {
		return poolTimeoutRetryInterval;
	}

	public void setPoolTimeoutRetryInterval(long poolTimeoutRetryInterval) {
		this.poolTimeoutRetryInterval = poolTimeoutRetryInterval;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
