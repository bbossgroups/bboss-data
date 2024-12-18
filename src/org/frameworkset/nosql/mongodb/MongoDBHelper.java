package org.frameworkset.nosql.mongodb;

import com.frameworkset.util.StringUtil;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.frameworkset.spi.BaseApplicationContext;
import org.frameworkset.spi.DefaultApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MongoDBHelper {
	public static final String defaultMongoDB = "default";
	private static Logger logger = LoggerFactory.getLogger(MongoDBHelper.class);
	private static BaseApplicationContext context = null;
	private static Map<String,MongoDB> mongoDBContainer = new ConcurrentHashMap<String,MongoDB>();

	private static void check(){
		if(context == null){
			try {
				context = DefaultApplicationContext.getApplicationContext("mongodb.xml");
			}
			catch (Exception e){
				logger.warn("Init context with mongodb.xml failed",e);
			}
		}
	}

	public static boolean init(MongoDBConfig mongoDBConfig){
		if(!mongoDBContainer.containsKey(mongoDBConfig.getName())) {
			synchronized (mongoDBContainer) {
				if(!mongoDBContainer.containsKey(mongoDBConfig.getName())) {
					MongoDB mongoDB = new MongoDB();
					mongoDB.initWithConfig(mongoDBConfig);
					mongoDB = mongoDB.getMongoClient();
					mongoDBContainer.put(mongoDB.getName(), mongoDB);
					return true;
				}
			}
		}
		return false;
	}
	public static void closeDB(String name){
		synchronized (mongoDBContainer) {
			MongoDB mongoDB = mongoDBContainer.get(name);
			if (mongoDB != null) {
				mongoDB.close();
				mongoDBContainer.remove(name);
			}
		}
	}


    public static void closeDB(MongoDBStartResult mongoDBStartResult) {
        synchronized (mongoDBContainer) {
            Map<String, Object> dbs = mongoDBStartResult.getDbstartResult();
            if (dbs != null && dbs.size() > 0) {
                Iterator<Map.Entry<String, Object>> iterator = dbs.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    closeDB(entry.getKey());
                }
            }
        }
        
    }
	public static MongoDB getMongoClient(String name)
	{
		if(StringUtil.isEmpty(name))
		{
			name = defaultMongoDB;
		}
		MongoDB mongoDB = mongoDBContainer.get(defaultMongoDB);
		if(mongoDB != null)
			return mongoDB;
		synchronized (MongoDBHelper.class) {
			mongoDB = mongoDBContainer.get(name);
			if(mongoDB != null)
				return mongoDB;
			check();
			mongoDB = context.getTBeanObject(name, MongoDB.class);
			if (mongoDB != null) {
				mongoDBContainer.put(name, mongoDB);
			}
		}
		return mongoDB;
	}
	
	public static MongoDB getMongoDB(String name)
	{
		return getMongoClient(name);
	}
	
	public static MongoDB getMongoDB()
	{
		 return getMongoDB(defaultMongoDB);
	}
	
	public static MongoDB getMongoClient()
	{
		return getMongoDB();
	}
	

	private static final String dianhaochar = "____";
	private static final String moneychar = "_____";
	private static final int msize = moneychar.length();

	public static String recoverSpecialChar(String attribute) {
		if (attribute.startsWith(moneychar)) {
			attribute = "$" + attribute.substring(msize);
		}

		attribute = attribute.replace(dianhaochar, ".");
		return attribute;
	}
	public static String converterSpecialChar(String attribute)
	{
		attribute = attribute.replace(".", dianhaochar);
		if(attribute.startsWith("$"))
		{
			if(attribute.length() == 1)
			{
				attribute = moneychar;
			}
			else
			{
				attribute = moneychar + attribute.substring(1);
			}
		}
		return attribute;
	}
	

	
	public static MongoDatabase getDB(String poolname, String dbname)
	{
		return getMongoDB(poolname).getDB( dbname );
	}
	public static MongoDatabase getDB(String dbname)
	{
		return getDB(defaultMongoDB,dbname);
		
	}
	
	
	public static MongoCollection<Document> getDBCollection(String poolname, String dbname, String table)
	{
		return getMongoDB(poolname).getDB( dbname ).getCollection(table);
	}
	public static MongoCollection<Document> getDBCollection(String dbname,String table)
	{
		return getDBCollection(defaultMongoDB,dbname,table);
		
	}
	
	
	
 
	
	
}
