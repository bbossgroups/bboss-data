package org.frameworkset.nosql.redis;

import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RedisFactory {
	private static Logger logger = LoggerFactory.getLogger(RedisFactory.class);
	public static final String DEFAULT_REDIS_POOL = "default";
	private static Map<String,RedisDB> dbs = new ConcurrentHashMap();
	private static ThreadLocal<RedisHelperHolder> currentDB = new ThreadLocal<RedisHelperHolder>();

	private static void checkRedisConfig(RedisConfig redisConfig){
		if(redisConfig.getName() == null || redisConfig.getName().equals("")){
			throw new DataRedisException("Redis datasource name is null or empty:"+redisConfig.toString());
		}
		else if(redisConfig.getServers() == null || redisConfig.getServers().equals("")){
			throw new DataRedisException("Redis datasource servers is null or empty:"+redisConfig.toString());
		}
	}
	/**
	 * 根据配置加载redis配置，初始redis客户端组件
	 * @param redisConfig
	 * @throws Exception
	 */
	public static void builRedisDB(RedisConfig redisConfig) throws DataRedisException {
		if(redisConfig != null && redisConfig.getName() != null ){
			if(!dbs.containsKey(redisConfig.getName())) {
				synchronized (dbs) {
					if (!dbs.containsKey(redisConfig.getName())) {
						checkRedisConfig( redisConfig);
						RedisDB redisDB = new RedisDB();
						redisDB.setName(redisConfig.getName());
						redisDB.setAuth(redisConfig.getAuth());
						redisDB.setConnectionTimeout(redisConfig.getConnectionTimeout());
						redisDB.setMaxIdle(redisConfig.getMaxIdle());
						redisDB.setMinIdle(redisConfig.getMinIdle());
						redisDB.setMaxRedirections(redisConfig.getMaxRedirections());
						redisDB.setMode(redisConfig.getMode());
						redisDB.setPoolMaxTotal(redisConfig.getPoolMaxTotal());
						redisDB.setPoolTimeoutRetry(redisConfig.getPoolTimeoutRetry());
						redisDB.setPoolTimeoutRetryInterval(redisConfig.getPoolTimeoutRetryInterval());
						redisDB.setPoolMaxWaitMillis(redisConfig.getPoolMaxWaitMillis());

						redisDB.setServers(redisConfig.getServers());
						redisDB.setSocketTimeout(redisConfig.getSocketTimeout());
						redisDB.setTestOnBorrow(redisConfig.isTestOnBorrow());
						redisDB.setTestOnReturn(redisConfig.isTestOnReturn());
						redisDB.setTestWhileIdle(redisConfig.isTestWhileIdle());
						redisDB.setProperties(redisConfig.getProperties());
						try {
							redisDB.afterPropertiesSet();
						} catch (Exception e) {
							throw new DataRedisException("Init redis from redisconfig failed:" + redisConfig.toString(), e);
						}
						dbs.put(redisConfig.getName(), redisDB);
					} else {
						logger.warn("Ignore build existed RedisDatasource[{}]",redisConfig.toString());
					}

				}
			}
			else{
//				logger.warn("Ignore build existed RedisDatasource[{}]",redisConfig.toString());
			}

		}
	}
	static class RedisHelperHolder 
	{
		private int count;
		public int increament()
		{
			count = count + 1;
			return count;
		}
		
		public int decreament()
		{
			count = count - 1;
			
			return count;
		}
		private boolean allreleased()
		{
			return count <= 0;
		}
		
		public void release()
		{
			 
			 
				if(holderdbs != null)
				{
					Iterator<Entry<String, RedisHelper>>  its = holderdbs.entrySet().iterator();
					while(its.hasNext())
					{
						Entry<String, RedisHelper> entry = its.next();
						try {
							entry.getValue().release();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				this.holderdbs = null;
			 
		}
		public RedisHelper getRedisHelper(boolean init)
		{
			return getRedisHelper(DEFAULT_REDIS_POOL,init);
		}
		public RedisHelper getRedisHelper(String dbname){
			return getRedisHelper(dbname,true);
		}
		public RedisHelper getRedisHelper()
		{
			return getRedisHelper(DEFAULT_REDIS_POOL);
		}
		public RedisHelper getRedisHelper(String dbname,boolean init)
		{
			RedisHelper redisHelper = null;
			if(holderdbs == null)
			{
				holderdbs = new HashMap<String,RedisHelper>();
			}
			else
			{
				redisHelper = holderdbs.get(dbname);
			}
			if(redisHelper != null)
			{
				 
				return redisHelper;
			}
			else
			{
				RedisDB db = init(dbname,init);
				if(db != null) {
					redisHelper = new RedisHelper(db);
					holderdbs.put(dbname, redisHelper);
				}

				return redisHelper;

			}
		}
		private Map<String,RedisHelper> holderdbs = new HashMap<String,RedisHelper>();
	}
	public RedisFactory() {
		
	}
	private static RedisDB init(String dbname,boolean init)
	{
		RedisDB db = dbs.get(dbname);
		if(db == null )
		{
			if(init) {
				synchronized (RedisFactory.class) {
					db = dbs.get(dbname);
					if (db == null) {
						BaseApplicationContext context = DefaultApplicationContext.getApplicationContext("redis.xml");
						db = context.getTBeanObject(dbname, RedisDB.class);
						dbs.put(dbname, db);
					}
					else{
						logger.warn("Ignore build existed RedisDatasource[{}]",dbname);
					}

				}
			}
			else{
				logger.warn("RedisDatasource[{}] not exist.",dbname);
			}
		}
		else{
//			logger.warn("Ignore build existed RedisDatasource[{}]",dbname);
		}
		return db;
	}
	
	public static RedisHelper getTXRedisHelper()
	{
		return getTXRedisHelper(DEFAULT_REDIS_POOL);
	}
	
	public static RedisHelper getTXRedisHelper(String dbname)
	{
		if(dbname == null)
			dbname = DEFAULT_REDIS_POOL;
		RedisHelperHolder holder = currentDB.get();
		if(holder == null)
		{
			holder = new RedisHelperHolder();
			currentDB.set(holder);
			
		}
		holder.increament();
		return holder.getRedisHelper(dbname);
//		RedisDB db = init(dbname);
//		return new RedisHelper(db);
	}
	public static RedisHelper getRedisHelper(boolean init)
	{
		return getRedisHelper(DEFAULT_REDIS_POOL,init);
	}

	public static RedisHelper getRedisHelper()
	{
		return getRedisHelper(DEFAULT_REDIS_POOL,true);
	}

	public static RedisHelper getRedisHelper(String dbname){
		return getRedisHelper(dbname,true);
	}
	public static RedisHelper getRedisHelper(String dbname,boolean init)
	{

		RedisDB db = init(dbname,init);
		if(db != null) {
			return new RedisHelper(db);
		}
		else{
			return null;
		}
	}
	public static void releaseTX()
	{
		RedisHelperHolder holder = currentDB.get();
		if(holder != null)
		{
			holder.decreament();
			if(holder.allreleased())
			{
				currentDB.set(null);
				holder.release();
			}
		}
	}

	public static Jedis buildJedis(String host , int port , String password ){
		Jedis jedis = new Jedis(host, port);
		if(password != null && !password.equals("")) {
			jedis.auth(password);
		}
		return jedis;
	}

}
